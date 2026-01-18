package com.group4.ecommerceplatform.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

@Entity
@Table(name="Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Id")
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;


    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews; // chi can truy nguoc ve review chu khong can phai truy ve lai order voi cart product

    @Column(nullable = false, name="IsActive")
    private Boolean isActive;
    @Column(nullable = false, name="CreatedAt")
    private LocalDateTime createdAt;
    @Column(nullable = false, name="UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Product() {
    }

    public Product(
            String name,
            String description,
            double price,
            Category category
    ) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.isActive = true;
    }

}
