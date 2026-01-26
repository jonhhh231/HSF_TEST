package com.group4.ecommerceplatform.repositories;

import com.group4.ecommerceplatform.entities.Review;
import com.group4.ecommerceplatform.entities.User;
import com.group4.ecommerceplatform.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    /**
     * Đếm số lượng review của một sản phẩm
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    Long countByProductId(@Param("productId") Integer productId);

    /**
     * Tính rating trung bình của một sản phẩm
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") Integer productId);

    /**
     * Lấy tất cả reviews của nhiều sản phẩm cùng lúc (để tính rating batch)
     * Tránh N+1 query problem
     */
    @Query("SELECT r FROM Review r WHERE r.product.id IN :productIds")
    List<Review> findByProductIdIn(@Param("productIds") Iterable<Integer> productIds);

    /**
     * Lấy tất cả reviews của một sản phẩm, sắp xếp theo ID giảm dần
     */
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.id DESC")
    List<Review> findByProductIdOrderByIdDesc(@Param("productId") Integer productId);

    /**
     * Kiểm tra xem user đã review sản phẩm này chưa
     */
    boolean existsByUserAndProduct(User user, Product product);

    /**
     * Tìm review của user cho một sản phẩm cụ thể
     */
    Optional<Review> findByUserAndProduct(User user, Product product);
}
