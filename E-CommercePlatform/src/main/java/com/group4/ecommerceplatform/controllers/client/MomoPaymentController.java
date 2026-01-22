package com.group4.ecommerceplatform.controllers.client;

import com.group4.ecommerceplatform.dto.MomoIPNRequest;
import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.services.payment.MomoPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý thanh toán MoMo
 */
@Controller
@RequestMapping("/payment/momo")
@Slf4j
public class MomoPaymentController {

    @Autowired
    private MomoPaymentService momoPaymentService;

    /**
     * Endpoint để nhận IPN callback từ MoMo (server-to-server)
     * MoMo sẽ gọi endpoint này để thông báo kết quả thanh toán
     */
    @PostMapping("/notify")
    @ResponseBody
    public ResponseEntity<?> handleIPNCallback(@RequestBody MomoIPNRequest ipnRequest) {
        log.info("Received MoMo IPN callback for order: {}", ipnRequest.getOrderId());

        boolean success = momoPaymentService.handleIPNCallback(ipnRequest);

        if (success) {
            return ResponseEntity.ok().body("{\"status\": \"success\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"status\": \"failed\"}");
        }
    }

    /**
     * Endpoint để nhận return URL từ MoMo (người dùng được redirect về)
     * Hiển thị kết quả thanh toán cho người dùng
     */
    @GetMapping("/return")
    public String handleReturnUrl(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) Integer resultCode,
            @RequestParam(required = false) String message,
            Model model) {

        log.info("User returned from MoMo payment - orderId: {}, resultCode: {}", orderId, resultCode);

        try {
            if (orderId == null || resultCode == null) {
                model.addAttribute("error", "Thông tin thanh toán không hợp lệ");
                return "client/payment-failed";
            }

            Order order = momoPaymentService.handleReturnUrl(orderId, resultCode);

            if (resultCode == 0) {
                // Thanh toán thành công
                model.addAttribute("order", order);
                model.addAttribute("message", "Thanh toán thành công!");
                return "client/payment-success";
            } else {
                // Thanh toán thất bại
                model.addAttribute("order", order);
                model.addAttribute("message", message != null ? message : "Giao dịch đã bị hủy hoặc không thành công");
                return "client/payment-failed";
            }

        } catch (Exception e) {
            log.error("Error handling MoMo return URL: ", e);
            model.addAttribute("error", "Đã có lỗi xảy ra: " + e.getMessage());
            return "client/payment-failed";
        }
    }

    /**
     * Test endpoint để kiểm tra tạo thanh toán MoMo
     * Chỉ dùng cho testing - xóa đi trong production
     */
    @GetMapping("/test")
    @ResponseBody
    public String testPayment() {
        try {
            // Tạo một đơn hàng test
            Order testOrder = new Order();
            testOrder.setOrderCode("TEST_" + System.currentTimeMillis());
            testOrder.setFinalPrice(new java.math.BigDecimal("50000")); // 50,000 VND

            String paymentUrl = momoPaymentService.createPaymentUrl(testOrder);

            return "<html><body>" +
                   "<h2>Test MoMo Payment</h2>" +
                   "<p>Order Code: " + testOrder.getOrderCode() + "</p>" +
                   "<p>Amount: " + testOrder.getFinalPrice() + " VND</p>" +
                   "<a href='" + paymentUrl + "'>Click here to pay with MoMo</a>" +
                   "</body></html>";

        } catch (Exception e) {
            log.error("Test payment error: ", e);
            return "<html><body><h2>Error</h2><p>" + e.getMessage() + "</p></body></html>";
        }
    }
}
