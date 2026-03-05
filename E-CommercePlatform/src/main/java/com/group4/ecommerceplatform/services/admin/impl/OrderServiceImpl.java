package com.group4.ecommerceplatform.services.admin.impl;

import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.repositories.OrderRepository;
import com.group4.ecommerceplatform.services.admin.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("adminOrderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // ------------------------------------------------------------------ //
    // Hiển thị
    // ------------------------------------------------------------------ //

    @Override
    public Page<Order> getOrderList(String keyword, Pageable pageable) {
        return orderRepository.findByOrderCodeContainingOrderByCreatedAtDesc(keyword, pageable);
    }

    @Override
    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
    }

    // ------------------------------------------------------------------ //
    // Tiến trạng thái vận chuyển (luồng thuận chiều)
    // ------------------------------------------------------------------ //

    @Override
    @Transactional
    public void changeToNextHandleStatus(Integer orderId) {
        Order order = getOrderById(orderId);
        switch (order.getShippingStatus()) {
            case "PENDING":
                order.setShippingStatus("PROCESSING");
                break;
            case "PROCESSING":
                order.setShippingStatus("PACKAGING");
                break;
            case "PACKAGING":
                order.setShippingStatus("DELIVERING");
                break;
            default:
                throw new RuntimeException("Không thể tiến trạng thái từ: " + order.getShippingStatus());
        }
        orderRepository.save(order);
    }

    // ------------------------------------------------------------------ //
    // Giao hàng thất bại
    // ------------------------------------------------------------------ //

    /**
     * Admin đánh dấu giao hàng thất bại (chỉ áp dụng khi đang DELIVERING).
     * <ul>
     * <li>COD → shippingStatus = DELIVERY_FAILED, paymentStatus = CANCELLED</li>
     * <li>Online → shippingStatus = DELIVERY_FAILED, paymentStatus =
     * REFUND_PENDING</li>
     * </ul>
     */
    @Override
    @Transactional
    public void markDeliveryFailed(Integer orderId) {
        Order order = getOrderById(orderId);

        if (!"DELIVERING".equals(order.getShippingStatus())) {
            throw new RuntimeException("Chỉ có thể đánh dấu thất bại khi đơn đang ở trạng thái 'Đang giao hàng'");
        }

        order.setShippingStatus("DELIVERY_FAILED");

        if ("CASH".equalsIgnoreCase(order.getPaymentMethod())) {
            // COD: chưa thu tiền → hủy luôn
            order.setPaymentStatus("CANCELLED");
        } else {
            // Online (MoMo, …): đã thu tiền → cần hoàn tiền
            order.setPaymentStatus("REFUND_PENDING");
        }

        orderRepository.save(order);
    }

    // ------------------------------------------------------------------ //
    // Hoàn tiền
    // ------------------------------------------------------------------ //

    /**
     * Admin xác nhận đã hoàn tiền xong.
     * Điều kiện: paymentStatus = REFUND_PENDING
     * Kết quả : paymentStatus = REFUNDED, shippingStatus = CANCELLED
     */
    @Override
    @Transactional
    public void confirmRefunded(Integer orderId) {
        Order order = getOrderById(orderId);

        if (!"REFUND_PENDING".equals(order.getPaymentStatus())) {
            throw new RuntimeException("Đơn hàng không ở trạng thái chờ hoàn tiền");
        }

        order.setPaymentStatus("REFUNDED");
        order.setShippingStatus("CANCELLED");
        orderRepository.save(order);
    }

    // ------------------------------------------------------------------ //
    // Toggle thanh toán COD (thu tiền mặt khi giao)
    // ------------------------------------------------------------------ //

    /**
     * Chỉ dùng cho đơn COD: đánh dấu đã thu tiền (PENDING → PAID) hoặc ngược lại.
     */
    @Override
    @Transactional
    public void changeCodPaymentStatus(Integer orderId, String status) {
        Order order = orderRepository.findByIdAndPaymentMethod(orderId, "CASH")
                .orElseThrow(() -> new RuntimeException("Đơn hàng không hợp lệ hoặc không phải thanh toán COD"));

        if (!"PAID".equals(status) && !"PENDING".equals(status)) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }

        order.setPaymentStatus(status);
        orderRepository.save(order);
    }
}
