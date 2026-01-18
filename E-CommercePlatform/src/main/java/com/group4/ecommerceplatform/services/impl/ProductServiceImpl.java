package com.group4.ecommerceplatform.services.impl;

import com.group4.ecommerceplatform.dto.product.ProductDTO;
import com.group4.ecommerceplatform.services.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public void createProduct(ProductDTO productDTO) {
        // check category exist

        // convert to product entity

        // save to db
    }

}
