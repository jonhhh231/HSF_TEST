package com.group4.ecommerceplatform.services.admin;

import com.group4.ecommerceplatform.dto.category.CategoryDTO;

import java.util.List;

public interface CategoryService {
    void createCategory(CategoryDTO categoryDTO);
    void updateCategory(CategoryDTO categoryDTO);
    CategoryDTO getCategoryById(Long id);
    List<CategoryDTO> getAllCategories();
    void deleteCategoryById(Long id);
}
