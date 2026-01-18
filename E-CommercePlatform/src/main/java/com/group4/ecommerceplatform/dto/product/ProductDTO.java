package com.group4.ecommerceplatform.dto.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {
    private String id;
    @NotEmpty(message = "Product is required")
    private String name;
    @NotNull(message = "Description can not be null")
    private String description;

    private Long categoryId;
    @NotNull(message = "Price can not be null")
    private Double price;
    @NotNull(message = "Stock can not be null")
    private Integer stock;
    @NotNull(message = "isActive is required")
    private Boolean isActive;
}
