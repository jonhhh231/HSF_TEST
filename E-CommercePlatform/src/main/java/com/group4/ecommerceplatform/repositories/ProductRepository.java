package com.group4.ecommerceplatform.repositories;

import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.repositories.customs.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long>, ProductRepositoryCustom {
}
