package com.group4.ecommerceplatform.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

@Entity
@Table(name="Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Id")
    private Long id;

    @Column(nullable = false, name = "Name", columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(nullable = false, name="Description", columnDefinition = "NVARCHAR(500)")
    private String description;

    @Column(nullable = false, name="Price")
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CategoryId",
            nullable = false
    )
    private Category category;

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
