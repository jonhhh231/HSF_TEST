package com.group4.ecommerceplatform.repositories;

import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.repositories.customs.ProductRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>, ProductRepositoryCustom {
    Page<Product> findByNameContaining(String name, Pageable pageable);
    Page<Product> findByIsActiveTrue(Pageable pageable);
    Page<Product> findByCategoryIdAndIsActiveTrue(Integer categoryId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.isActive = true " +
            "AND (:category IS NULL OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :category, '%'))) " +
            "AND (:minP IS NULL OR p.price >= :minP) " +
            "AND (:maxP IS NULL OR p.price <= :maxP) " +
            "AND (:proc IS NULL OR LOWER(p.processor) LIKE LOWER(CONCAT('%', :proc, '%'))) " +
            "AND (:graph IS NULL OR LOWER(p.graphics) LIKE LOWER(CONCAT('%', :graph, '%'))) " +
            "AND (:stor IS NULL OR LOWER(p.storage) LIKE LOWER(CONCAT('%', :stor, '%'))) " +
            "AND (:disp IS NULL OR LOWER(p.display) LIKE LOWER(CONCAT('%', :disp, '%'))) " +
            "AND (:batt IS NULL OR LOWER(p.battery) LIKE LOWER(CONCAT('%', :batt, '%')))")
    List<Product> findSmartSearch(
            @Param("category") String category,
            @Param("minP") BigDecimal minP,
            @Param("maxP") BigDecimal maxP,
            @Param("proc") String proc,
            @Param("graph") String graph,
            @Param("stor") String stor,
            @Param("disp") String disp,
            @Param("batt") String batt
    );
}
