package com.group4.ecommerceplatform.services.client;

import com.group4.ecommerceplatform.entities.CartProduct;
import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.entities.User;

import java.util.List;

/**
 * Service interface xử lý các nghiệp vụ liên quan đến Order
 */
public interface OrderService {

    /**
     * Tạo đơn hàng từ giỏ hàng
     * @param user Người dùng
     * @param orderCode Mã đơn hàng
     * @param cartItems Danh sách sản phẩm trong giỏ
     * @param cartTotal Tổng tiền
     * @param paymentMethod Phương thức thanh toán
     * @return Order đã được lưu
     */
    Order createOrderFromCart(User user, String orderCode, List<CartProduct> cartItems,
                              Long cartTotal, String paymentMethod);

    /**
     * Tìm user theo ID
     * @param userId ID người dùng
     * @return User
     */
    User findUserById(Integer userId);

    /**
     * Lấy danh sách đơn hàng của user
     * @param userId ID người dùng
     * @return Danh sách đơn hàng
     */
    List<Order> getOrdersByUserId(Integer userId);

    /**
     * Lấy chi tiết đơn hàng và kiểm tra quyền truy cập
     * @param orderId ID đơn hàng
     * @param userId ID người dùng
     * @return Order nếu hợp lệ
     */
    Order getOrderDetail(Integer orderId, Integer userId);
}
