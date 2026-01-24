package com.group4.ecommerceplatform.services.client;

import com.group4.ecommerceplatform.entities.Cart;
import com.group4.ecommerceplatform.entities.CartProduct;

import java.util.List;

public interface CartService {
    // Lấy cart của user hoặc tạo mới
    Cart getOrCreateCartByUserId(Integer userId);

    // Lấy list sp trong cart
    List<CartProduct> getCartItems(Integer userId);

    // Thêm sp vào cart, tăng sl nếu đã có
    CartProduct addProductToCart(Integer userId, Integer productId, Integer quantity);

    // update sl sp
    CartProduct updateProductQuantity(Integer userId, Integer productId, Integer quantity);

    // Xóa sp khỏi cart
    void removeProductFromCart(Integer userId, Integer productId);

    // Xóa tất cả sp
    void clearCart(Integer userId);

    // Tính tổng tiền
    Long calculateCartTotal(Integer userId);

    // Đếm số sản phẩm
    int getCartItemCount(Integer userId);
}
