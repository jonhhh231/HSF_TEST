package com.group4.ecommerceplatform.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 255, message = "Tên danh mục phải có từ 2 đến 255 ký tự")
    @Column(name = "name", columnDefinition = "NVARCHAR(255)")
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }
}
