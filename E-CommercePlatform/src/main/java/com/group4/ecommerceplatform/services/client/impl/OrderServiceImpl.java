package com.group4.ecommerceplatform.services.client.impl;

import com.group4.ecommerceplatform.entities.CartProduct;
import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.entities.OrderDetail;
import com.group4.ecommerceplatform.entities.User;
import com.group4.ecommerceplatform.repositories.OrderDetailRepository;
import com.group4.ecommerceplatform.repositories.OrderRepository;
import com.group4.ecommerceplatform.repositories.UserRepository;
import com.group4.ecommerceplatform.services.client.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation của OrderService
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Order createOrderFromCart(User user, String orderCode, List<CartProduct> cartItems,
            Long cartTotal, String paymentMethod, String address) {
        log.info("Creating order for user: {}, orderCode: {}", user.getId(), orderCode);

        // Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderCode(orderCode);
        order.setAddress(address);
        order.setFinalPrice(BigDecimal.valueOf(cartTotal));
        order.setPaymentMethod(paymentMethod);

        // Xử lý paymentStatus dựa trên paymentMethod
        if ("CASH".equalsIgnoreCase(paymentMethod)) {
            // Thanh toán tiền mặt - chưa thanh toán
            order.setPaymentStatus("PENDING");
            order.setPaidAt(null);
        } else {
            // Thanh toán online (MoMo, etc.) - đã thanh toán
            order.setPaymentStatus("PAID");
            order.setPaidAt(LocalDateTime.now());
        }

        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Lưu đơn hàng
        order = orderRepository.save(order);

        // Tạo OrderDetails
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartProduct cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(order.getId());
            orderDetail.setProductId(cartItem.getProduct().getId());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getProduct().getPrice());
            orderDetails.add(orderDetail);
        }

        // Lưu OrderDetails
        orderDetailRepository.saveAll(orderDetails);

        log.info("Order created successfully with ID: {}", order.getId());
        return order;
    }

    @Override
    public User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        User user = findUserById(userId);
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public Order getOrderDetail(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra đơn hàng có thuộc về user này không
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xem đơn hàng này");
        }

        return order;
    }

    @Override
    public Order findByOrderCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode).orElse(null);
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Chỉ cho phép hủy đơn hàng đang chờ thanh toán
        if (!"PENDING".equals(order.getPaymentStatus())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang chờ thanh toán");
        }

        // Cập nhật trạng thái thành CANCELLED
        order.setPaymentStatus("CANCELLED");
        order.setUpdatedAt(java.time.LocalDateTime.now());
        orderRepository.save(order);

        log.info("Order {} has been cancelled", orderId);
    }
}
