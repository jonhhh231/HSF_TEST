package com.group4.ecommerceplatform.controllers.client;

import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.services.client.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String viewOrders(HttpSession session, Model model) {
        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return "redirect:/auth/login";
        }

        List<Order> orders = orderService.getOrdersByUserId(userId);

        model.addAttribute("orders", orders);
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
            return "client/order-detail";
        } catch (RuntimeException e) {
            return "redirect:/orders";
        }
    }

    private Integer getCurrentUserId(HttpSession session) {
        return (Integer) session.getAttribute("userId");
    }
}
