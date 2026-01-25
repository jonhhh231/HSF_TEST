package com.group4.ecommerceplatform.controllers.client;

import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.services.client.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class ClientOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Hiển thị danh sách đơn hàng của user
     */
    @GetMapping
    public String viewOrders(
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String orderCode,
            HttpSession session,
            Model model) {
        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return "redirect:/auth/login";
        }

        List<Order> orders = orderService.getOrdersByUserId(userId);

        model.addAttribute("orders", orders);

        // Handle success message from CASH order (URL parameters)
        if ("true".equals(success) && orderCode != null && !model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", "Đặt hàng thành công!");
            model.addAttribute("orderCode", orderCode);
        }

        // Flash attributes from redirectAttributes (like cancel success) will be automatically added to model

        return "client/orders";
    }

    /**
     * Xem chi tiết một đơn hàng
     */
    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Integer id, HttpSession session, Model model) {
        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return "redirect:/auth/login";
        }

        try {
            Order order = orderService.getOrderDetail(id, userId);
            model.addAttribute("order", order);

            // Kiểm tra xem có message từ payment success không
            String paymentSuccessMessage = (String) session.getAttribute("paymentSuccessMessage");
            if (paymentSuccessMessage != null) {
                model.addAttribute("successMessage", paymentSuccessMessage);
                session.removeAttribute("paymentSuccessMessage");
            }

            return "client/order-detail";
        } catch (RuntimeException e) {
            return "redirect:/orders";
        }
    }

    /**
     * Hủy đơn hàng
     */
    @PostMapping("/cancel/{id}")
    public String cancelOrder(
            @PathVariable Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return "redirect:/auth/login";
        }

        try {
            // Kiểm tra đơn hàng có thuộc về user này không
            Order order = orderService.getOrderDetail(id, userId);

            // Chỉ cho phép hủy đơn hàng đang chờ thanh toán
            if ("PENDING".equals(order.getPaymentStatus())) {
                orderService.cancelOrder(id);
                redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn hàng thành công");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể hủy đơn hàng này");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/orders";
    }

    private Integer getCurrentUserId(HttpSession session) {
        return (Integer) session.getAttribute("userId");
    }
}
