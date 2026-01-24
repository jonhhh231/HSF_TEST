package com.group4.ecommerceplatform.services.admin;

import com.group4.ecommerceplatform.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    void saveProduct(Product product);
    Product getProductById(Integer id);
    Page<Product> getProductList(String keyword, Pageable pageable);
}
