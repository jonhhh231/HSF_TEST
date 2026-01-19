package com.group4.ecommerceplatform.responses.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSearchResponse {
    private Long id;
    private String name;
    private String categoryName;
    private Double price;
    private Integer stock;
    private Boolean isActive;
    private String createdAt;
}
