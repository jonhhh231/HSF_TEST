package com.group4.ecommerceplatform.repositories;

import com.group4.ecommerceplatform.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Tìm đơn hàng theo order code
     */
    Optional<Order> findByOrderCode(String orderCode);
}
