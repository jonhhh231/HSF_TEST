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

    // gọi hồn REPO lên để sai nó làm việc
    @Autowired
    private CategoryRepository categoryRepository;


    // gọi hồn DTO lên để kêu nó lấy thông tin
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
        // Gọi Repo lấy hết Entity -> Chuyển sang DTO -> Trả về List
        List<Category> entities = categoryRepository.findAll();
        return entities.stream()
                .map(entity -> categoryDTOConverter.toDTO(entity))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
    }
}
