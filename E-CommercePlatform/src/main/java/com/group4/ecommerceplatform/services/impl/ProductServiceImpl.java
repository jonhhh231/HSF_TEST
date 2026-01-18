package com.group4.ecommerceplatform.services.impl;

import com.group4.ecommerceplatform.converters.product.ProductDTOConverter;
import com.group4.ecommerceplatform.dto.product.ProductDTO;
import com.group4.ecommerceplatform.entities.Category;
import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.repositories.CategoryRepository;
import com.group4.ecommerceplatform.repositories.ProductRepository;
import com.group4.ecommerceplatform.services.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductDTOConverter productDTOConverter;
    @Transactional
    @Override
    public void createProduct(ProductDTO productDTO) {
        // check category exist

        // convert to product entity
        Product product = productDTOConverter.toProductEntity(productDTO);
        // save to db
        productRepository.save(product);
    }
}
