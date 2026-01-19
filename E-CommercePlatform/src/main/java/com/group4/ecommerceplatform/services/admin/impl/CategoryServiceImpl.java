package com.group4.ecommerceplatform.services.admin.impl;

import com.group4.ecommerceplatform.converters.category.CategoryDTOConverter;
import com.group4.ecommerceplatform.dto.category.CategoryDTO;
import com.group4.ecommerceplatform.entities.Category;
import com.group4.ecommerceplatform.exceptions.NotFoundException;
import com.group4.ecommerceplatform.repositories.CategoryRepository;
import com.group4.ecommerceplatform.services.admin.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryDTOConverter categoryDTOConverter;

    @Override
    @Transactional
    public void createCategory(CategoryDTO dto) {
        Category category = categoryDTOConverter.toEntity(dto);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void updateCategory(CategoryDTO dto) {
        Category existingCategory = categoryRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Category Not Found"));

        // Map dữ liệu mới vào entity cũ
        categoryDTOConverter.updateEntity(dto, existingCategory);
        categoryRepository.save(existingCategory);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category Not Found"));
        return categoryDTOConverter.toDTO(category);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryDTOConverter::toDTO)
                .collect(Collectors.toList());
    }
}
