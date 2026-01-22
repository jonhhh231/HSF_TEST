package com.group4.ecommerceplatform.services.payment.impl;

import com.group4.ecommerceplatform.config.MomoConfig;
import com.group4.ecommerceplatform.dto.MomoIPNRequest;
import com.group4.ecommerceplatform.dto.MomoPaymentRequest;
import com.group4.ecommerceplatform.dto.MomoPaymentResponse;
import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.repositories.OrderRepository;
import com.group4.ecommerceplatform.services.payment.MomoPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation của MomoPaymentService
 */
@Service
@Slf4j
public class MomoPaymentServiceImpl implements MomoPaymentService {

    @Autowired
    private MomoConfig momoConfig;

    @Autowired
    private OrderRepository orderRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String createPaymentUrl(Order order) throws Exception {
        // Validate order
        if (order == null || order.getOrderCode() == null) {
            throw new IllegalArgumentException("Đơn hàng không hợp lệ");
        }

        // Tạo requestId và orderId unique
        String requestId = UUID.randomUUID().toString();
        String orderId = order.getOrderCode();
        String amount = order.getFinalPrice().longValue() + "";
        String orderInfo = "Thanh toán đơn hàng " + orderId;

        // Tạo raw signature
        String rawSignature = String.format(
            "accessKey=%s&amount=%s&extraData=&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
            momoConfig.getAccessKey(),
            amount,
            momoConfig.getNotifyUrl(),
            orderId,
            orderInfo,
            momoConfig.getPartnerCode(),
            momoConfig.getReturnUrl(),
            requestId,
            momoConfig.getRequestType()
        );

        // Tạo signature
        String signature = signHmacSHA256(rawSignature, momoConfig.getSecretKey());

        // Tạo request
        MomoPaymentRequest request = new MomoPaymentRequest();
        request.setPartnerCode(momoConfig.getPartnerCode());
        request.setAccessKey(momoConfig.getAccessKey());
        request.setRequestId(requestId);
        request.setAmount(amount);
        request.setOrderId(orderId);
        request.setOrderInfo(orderInfo);
        request.setRedirectUrl(momoConfig.getReturnUrl());
        request.setIpnUrl(momoConfig.getNotifyUrl());
        request.setRequestType(momoConfig.getRequestType());
        request.setExtraData("");
        request.setLang("vi");
        request.setSignature(signature);

        // Gọi API MoMo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MomoPaymentRequest> entity = new HttpEntity<>(request, headers);

        log.info("Sending payment request to MoMo for order: {}", orderId);

        try {
            ResponseEntity<MomoPaymentResponse> response = restTemplate.exchange(
                momoConfig.getEndpoint(),
                HttpMethod.POST,
                entity,
                MomoPaymentResponse.class
            );

            MomoPaymentResponse momoResponse = response.getBody();

            if (momoResponse == null) {
                throw new RuntimeException("Không nhận được phản hồi từ MoMo");
            }

            log.info("MoMo response - resultCode: {}, message: {}",
                momoResponse.getResultCode(), momoResponse.getMessage());

            if (momoResponse.getResultCode() == 0) {
                // KHÔNG lưu order vào DB - sẽ lưu sau khi thanh toán thành công
                log.info("MoMo payment URL created successfully for order: {}", orderId);
                return momoResponse.getPayUrl();
            } else {
                throw new RuntimeException("Lỗi từ MoMo: " + momoResponse.getMessage());
            }

        } catch (Exception e) {
            log.error("Error creating MoMo payment: ", e);
            throw new RuntimeException("Không thể tạo thanh toán MoMo: " + e.getMessage());
        }
    }

    @Override
    public boolean handleIPNCallback(MomoIPNRequest ipnRequest) {
        try {
            log.info("Received IPN callback for order: {}", ipnRequest.getOrderId());

            // Verify signature
            String rawSignature = String.format(
                "accessKey=%s&amount=%d&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%d&resultCode=%d&transId=%d",
                momoConfig.getAccessKey(),
                ipnRequest.getAmount(),
                ipnRequest.getExtraData(),
                ipnRequest.getMessage(),
                ipnRequest.getOrderId(),
                ipnRequest.getOrderInfo(),
                ipnRequest.getOrderType(),
                ipnRequest.getPartnerCode(),
                ipnRequest.getPayType(),
                ipnRequest.getRequestId(),
                ipnRequest.getResponseTime(),
                ipnRequest.getResultCode(),
                ipnRequest.getTransId()
            );

            if (!verifySignature(rawSignature, ipnRequest.getSignature())) {
                log.error("Invalid signature from MoMo IPN");
                return false;
            }

            // Tìm order (có thể có hoặc không - nếu user vừa thanh toán xong)
            var orderOpt = orderRepository.findByOrderCode(ipnRequest.getOrderId());

            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();

                if (ipnRequest.getResultCode() == 0) {
                    // Thanh toán thành công
                    order.setPaymentStatus("PAID");
                    order.setPaidAt(LocalDateTime.now());
                    log.info("Payment successful for order: {}", ipnRequest.getOrderId());
                } else {
                    // Thanh toán thất bại
                    order.setPaymentStatus("FAILED");
                    log.warn("Payment failed for order: {}, code: {}",
                        ipnRequest.getOrderId(), ipnRequest.getResultCode());
                }

                orderRepository.save(order);
            } else {
                log.info("Order not found in DB (may be created later): {}", ipnRequest.getOrderId());
            }

            return true;

        } catch (Exception e) {
            log.error("Error handling IPN callback: ", e);
            return false;
        }
    }

    @Override
    public Order handleReturnUrl(String orderId, Integer resultCode) {
        try {
            // Tìm order nếu đã tạo (có thể có hoặc không)
            var orderOpt = orderRepository.findByOrderCode(orderId);

            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();

                if (resultCode == 0) {
                    log.info("User returned from MoMo payment - Success for order: {}", orderId);
                } else {
                    // Thanh toán thất bại
                    order.setPaymentStatus("FAILED");
                    orderRepository.save(order);
                    log.warn("User returned from MoMo payment - Failed for order: {}, code: {}",
                        orderId, resultCode);
                }

                return order;
            } else {
                log.info("Order not found - will be created in controller: {}", orderId);
                // Return null - controller sẽ tạo order mới
                return null;
            }

        } catch (Exception e) {
            log.error("Error handling return URL: ", e);
            throw new RuntimeException("Lỗi xử lý kết quả thanh toán");
        }
    }

    @Override
    public boolean verifySignature(String rawSignature, String signature) {
        try {
            String generatedSignature = signHmacSHA256(rawSignature, momoConfig.getSecretKey());
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying signature: ", e);
            return false;
        }
    }

    /**
     * Tạo HMAC SHA256 signature
     */
    private String signHmacSHA256(String data, String secretKey) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
            secretKey.getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );
        hmacSHA256.init(secretKeySpec);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // Convert to hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
