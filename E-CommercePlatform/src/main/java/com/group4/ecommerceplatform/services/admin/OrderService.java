package com.group4.ecommerceplatform.services.admin;

import com.group4.ecommerceplatform.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<Order> getOrderList(String keyword, Pageable pageable);

    Order getOrderById(Integer orderId);

    /**
     * Admin bấm "Tiếp theo" để chuyển trạng thái vận chuyển
     * PENDING→PROCESSING→PACKAGING→DELIVERING
     */
    void changeToNextHandleStatus(Integer orderId);

    /**
     * Admin đánh dấu giao hàng thất bại khi đơn đang ở DELIVERING.
     * - Nếu COD → shippingStatus = DELIVERY_FAILED, paymentStatus = CANCELLED
     * - Nếu Online → shippingStatus = DELIVERY_FAILED, paymentStatus =
     * REFUND_PENDING
     */
    void markDeliveryFailed(Integer orderId);

    /**
     * Admin xác nhận đã hoàn tiền xong (chỉ áp dụng khi paymentStatus =
     * REFUND_PENDING).
     * → paymentStatus = REFUNDED, shippingStatus = CANCELLED
     */
    void confirmRefunded(Integer orderId);

    /** Chỉ dùng cho COD: toggle thanh toán thủ công PENDING ↔ PAID */
    void changeCodPaymentStatus(Integer orderId, String status);
}
