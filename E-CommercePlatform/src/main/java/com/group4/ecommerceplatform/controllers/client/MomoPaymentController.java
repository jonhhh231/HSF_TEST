package com.group4.ecommerceplatform.controllers.client;

import com.group4.ecommerceplatform.dto.MomoIPNRequest;
import com.group4.ecommerceplatform.entities.CartProduct;
import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.entities.User;
import com.group4.ecommerceplatform.services.client.CartService;
import com.group4.ecommerceplatform.services.client.OrderService;
import com.group4.ecommerceplatform.services.payment.MomoPaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller xử lý thanh toán MoMo
 */
@Controller
@RequestMapping("/payment/momo")
@Slf4j
public class MomoPaymentController {

    @Autowired
    private MomoPaymentService momoPaymentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    @Qualifier("clientCartService")
    private CartService cartService;

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
            HttpSession session,
            Model model) {

        log.info("User returned from MoMo payment - orderId: {}, resultCode: {}", orderId, resultCode);

        try {
            if (orderId == null || resultCode == null) {
                model.addAttribute("error", "Thông tin thanh toán không hợp lệ");
                return "client/payment-failed";
            }

            if (resultCode == 0) {
                // Thanh toán thành công - TẠO ĐơN HÀNG THỰC
                Integer userId = (Integer) session.getAttribute("userId");
                String pendingOrderCode = (String) session.getAttribute("pendingOrderCode");
                @SuppressWarnings("unchecked")
                List<CartProduct> cartItems = (List<CartProduct>) session.getAttribute("pendingCartItems");
                Long cartTotal = (Long) session.getAttribute("pendingCartTotal");

                if (userId == null || cartItems == null || cartTotal == null) {
                    model.addAttribute("error", "Không tìm thấy thông tin giỏ hàng");
                    return "client/payment-failed";
                }

                // Lấy thông tin user và tạo Order thông qua service
                User user = orderService.findUserById(userId);
                Order order = orderService.createOrderFromCart(user, pendingOrderCode, cartItems, cartTotal, "MOMO");

                // Xóa giỏ hàng
                cartService.clearCart(userId);

                // Xóa session data
                session.removeAttribute("pendingOrderCode");
                session.removeAttribute("pendingCartItems");
                session.removeAttribute("pendingCartTotal");

                log.info("Order created successfully: {}", order.getOrderCode());

                model.addAttribute("order", order);
                model.addAttribute("message", "Thanh toán thành công!");
                return "client/payment-success";
            } else {
                // Thanh toán thất bại - KHÔNG tạo Order
                String pendingOrderCode = (String) session.getAttribute("pendingOrderCode");

                Order tempOrder = new Order();
                tempOrder.setOrderCode(pendingOrderCode);

                model.addAttribute("order", tempOrder);
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
