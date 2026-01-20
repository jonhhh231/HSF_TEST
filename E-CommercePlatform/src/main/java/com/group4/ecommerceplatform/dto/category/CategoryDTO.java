package com.group4.ecommerceplatform.dto.category;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDTO {
    private Long id;

    @NotEmpty(message = "Category name is required")
    private String name;

    private String description;
}
