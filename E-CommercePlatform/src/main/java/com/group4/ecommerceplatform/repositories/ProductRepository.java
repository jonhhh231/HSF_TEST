package com.group4.ecommerceplatform.repositories;

import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.repositories.customs.ProductRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer>, ProductRepositoryCustom {
    Page<Product> findByIsActiveTrue(Pageable pageable);
    Page<Product> findByCategoryIdAndIsActiveTrue(Integer categoryId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);
}
