package com.group4.ecommerceplatform.dto.product;

import jakarta.validation.constraints.NotEmpty;

public class ProductDTO {
    private String id;
    @NotEmpty(message = "Product is required")
    private String name;
}
