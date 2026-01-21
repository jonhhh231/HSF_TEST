package com.group4.ecommerceplatform.services.client;

import com.group4.ecommerceplatform.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<Product> getAllProducts(Pageable pageable);
    Page<Product> getProductsByCategory(Integer categoryId, Pageable pageable);
    Page<Product> searchProducts(String keyword, Pageable pageable);
    Product getProductById(Integer id);
}
