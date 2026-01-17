package com.group4.ecommerceplatform.converters.product;

import com.group4.ecommerceplatform.dto.product.ProductDTO;
import com.group4.ecommerceplatform.entities.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductDTOConverter {
    public Product toProductEntity(ProductDTO dto)
    {
        Product product = new Product();
        return product;
    }
}
