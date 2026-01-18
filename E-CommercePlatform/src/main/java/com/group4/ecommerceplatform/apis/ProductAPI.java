package com.group4.ecommerceplatform.apis;

import com.group4.ecommerceplatform.dto.product.ProductDTO;
import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
public class ProductAPI {
    @Autowired
    private ProductService productService;
    @PostMapping
    public ResponseEntity<?> createProduct(@ModelAttribute ProductDTO dto)
    {
        System.out.println(dto.getPrice());
        return null;
    }
}
