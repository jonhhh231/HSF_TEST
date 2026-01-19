package com.group4.ecommerceplatform.converters.product;

import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.repositories.CategoryRepository;
import com.group4.ecommerceplatform.repositories.ProductRepository;
import com.group4.ecommerceplatform.responses.admin.ProductSearchResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ProductResponseConverter {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    public ProductSearchResponse toProductSearchResponse(Product product){
        ProductSearchResponse  productSearchResponse = modelMapper.map(product, ProductSearchResponse.class);
        productSearchResponse.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : "Không thuộc danh mục nào");
        productSearchResponse.setCreatedAt(product.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")));
        return productSearchResponse;
    }
}
