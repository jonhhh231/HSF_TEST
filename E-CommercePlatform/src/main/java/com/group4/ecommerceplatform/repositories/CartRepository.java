package com.group4.ecommerceplatform.repositories;

import com.group4.ecommerceplatform.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUserId(Integer userId);
    boolean existsByUserId(Integer userId);
}
