package com.group4.ecommerceplatform.converters.product;

import com.group4.ecommerceplatform.dto.product.ProductDTO;
import com.group4.ecommerceplatform.entities.Category;
import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductDTOConverter {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    public Product toProductEntity(ProductDTO dto)
    {
//        Optional<Category> categoryOptional = categoryRepository.findById(dto.getCategoryId());
        Product product = modelMapper.map(dto,Product.class);
        return product;
    }
}
