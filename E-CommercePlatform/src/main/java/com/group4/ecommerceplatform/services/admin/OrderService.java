package com.group4.ecommerceplatform.services.admin;

import com.group4.ecommerceplatform.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    void changeCodePaymentStatus(Integer orderId, String status);
    Page<Order> getOrderList(String keyword, Pageable pageable);
}
