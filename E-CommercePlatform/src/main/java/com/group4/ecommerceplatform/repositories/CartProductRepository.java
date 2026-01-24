package com.group4.ecommerceplatform.repositories;

import com.group4.ecommerceplatform.entities.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, CartProduct.CartProductId> {
    // Tìm item trong cart
    Optional<CartProduct> findByCartIdAndProductId(Integer cartId, Integer productId);

    // Lấy tất cả items
    List<CartProduct> findByCartId(Integer cartId);

    // Xóa một item
    @Modifying
    void deleteByCartIdAndProductId(Integer cartId, Integer productId);

    // Xóa tất cả
    @Modifying
    void deleteByCartId(Integer cartId);

    // Đếm số items
    long countByCartId(Integer cartId);

    // Kiểm tra sản phẩm có trong cart chưa
    boolean existsByCartIdAndProductId(Integer cartId, Integer productId);
}
