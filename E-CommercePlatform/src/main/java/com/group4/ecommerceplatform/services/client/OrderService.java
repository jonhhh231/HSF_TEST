package com.group4.ecommerceplatform.services.client;

import com.group4.ecommerceplatform.entities.CartProduct;
import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.entities.User;

import java.util.List;

public interface OrderService {
    Order createOrderFromCart(User user, String orderCode, List<CartProduct> cartItems,
                              Long cartTotal, String paymentMethod, String address);
    User findUserById(Integer userId);
    List<Order> getOrdersByUserId(Integer userId);
    Order getOrderDetail(Integer orderId, Integer userId);
    Order findByOrderCode(String orderCode);
    void cancelOrder(Integer orderId);
    void deleteOrder(Integer orderId);
}