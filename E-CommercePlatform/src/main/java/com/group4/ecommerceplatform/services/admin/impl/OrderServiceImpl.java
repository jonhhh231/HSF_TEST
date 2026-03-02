package com.group4.ecommerceplatform.services.admin.impl;

import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.exceptions.NotFoundException;
import com.group4.ecommerceplatform.repositories.OrderRepository;
import com.group4.ecommerceplatform.services.admin.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("adminOrderService")
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Override
    public void changeCodePaymentStatus(Integer orderId, String status) {
        Order order = orderRepository.findByIdAndPaymentMethod(orderId, "COD").orElseThrow(() -> new RuntimeException("Order not valid to update"));
        order.setPaymentStatus(status);
        orderRepository.save(order);
    }

    @Override
    public Page<Order> getOrderList(String keyword, Pageable pageable) {
        return orderRepository.findByOrderCodeContainingOrderByCreatedAtDesc(keyword, pageable);
    }

    @Override
    public void changeToNextHandleStatus(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not valid"));
        switch (order.getStatus()) {
            case "pending":
                order.setStatus("processing");
                break;
            case "processing":
                order.setStatus("packaging");
                break;
            case "packaging":
                order.setStatus("delivering");
                break;
            case "delivering":
                order.setStatus("delivered");
                break;
            default:
                break;
        }
        orderRepository.save(order);
    }


}
