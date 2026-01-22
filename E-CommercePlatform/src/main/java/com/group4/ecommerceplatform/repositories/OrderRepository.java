package com.group4.ecommerceplatform.repositories;

import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Tìm đơn hàng theo order code
     */
    Optional<Order> findByOrderCode(String orderCode);

    /**
     * Tìm tất cả đơn hàng của user, sắp xếp theo thời gian tạo mới nhất
     */
    List<Order> findByUserOrderByCreatedAtDesc(User user);
}
