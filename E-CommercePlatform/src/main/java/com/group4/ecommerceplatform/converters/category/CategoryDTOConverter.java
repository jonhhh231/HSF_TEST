package com.group4.ecommerceplatform.converters.category;

import com.group4.ecommerceplatform.dto.category.CategoryDTO;
import com.group4.ecommerceplatform.entities.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryDTOConverter {
    @Autowired
    private ModelMapper modelMapper;

    public Category toEntity(CategoryDTO dto) {
        return modelMapper.map(dto, Category.class);
    }

    public CategoryDTO toDTO(Category entity) {
        return modelMapper.map(entity, CategoryDTO.class);
    }

    public void updateEntity(CategoryDTO source, Category destination) {
        modelMapper.map(source, destination);
    }
}
