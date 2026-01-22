package com.group4.ecommerceplatform.controllers.client;

import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.entities.User;
import com.group4.ecommerceplatform.repositories.OrderRepository;
import com.group4.ecommerceplatform.repositories.UserRepository;
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
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Hiển thị danh sách đơn hàng của user
     */
    @GetMapping
    public String viewOrders(HttpSession session, Model model) {
        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return "redirect:/auth/login";
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);

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

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra đơn hàng có thuộc về user này không
        if (!order.getUser().getId().equals(userId)) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        return "client/order-detail";
    }

    private Integer getCurrentUserId(HttpSession session) {
        return (Integer) session.getAttribute("userId");
    }
}
