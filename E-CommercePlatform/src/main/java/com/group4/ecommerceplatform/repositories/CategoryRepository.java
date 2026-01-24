package com.group4.ecommerceplatform.repositories;

import com.group4.ecommerceplatform.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
