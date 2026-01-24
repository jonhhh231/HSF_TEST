package com.group4.ecommerceplatform.services.admin;

import com.group4.ecommerceplatform.entities.Category;

import java.util.List;

public interface CategoryService {
    void createCategory(Category category);
    void updateCategory(Category category);
    List<Category> getAllCategories();
}
