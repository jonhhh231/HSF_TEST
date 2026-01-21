package com.group4.ecommerceplatform.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "cart_products")
@Getter
@Setter
@NoArgsConstructor
@IdClass(CartProduct.CartProductId.class)
public class CartProduct {
    @Id
    @Column(name = "cart_id")
    private Integer cartId;

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", insertable = false, updatable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartProductId implements Serializable {
        private Integer cartId;
        private Integer productId;
    }
}
