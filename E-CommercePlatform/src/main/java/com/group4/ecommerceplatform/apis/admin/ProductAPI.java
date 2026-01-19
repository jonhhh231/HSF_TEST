package com.group4.ecommerceplatform.apis.admin;

import com.group4.ecommerceplatform.dto.product.ProductDTO;
import com.group4.ecommerceplatform.responses.SuccessResponse;
import com.group4.ecommerceplatform.services.admin.ProductService;
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
    public ResponseEntity<SuccessResponse> createProduct(@RequestBody @Valid ProductDTO dto)
    {
        productService.createProduct(dto);
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage("Create success");
        successResponse.setData(null);
        return ResponseEntity.ok(successResponse);
    }
}
