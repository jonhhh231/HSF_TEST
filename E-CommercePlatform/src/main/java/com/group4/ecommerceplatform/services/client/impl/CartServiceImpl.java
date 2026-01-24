package com.group4.ecommerceplatform.services.client.impl;

import com.group4.ecommerceplatform.entities.Cart;
import com.group4.ecommerceplatform.entities.CartProduct;
import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.entities.User;
import com.group4.ecommerceplatform.exceptions.NotFoundException;
import com.group4.ecommerceplatform.repositories.CartProductRepository;
import com.group4.ecommerceplatform.repositories.CartRepository;
import com.group4.ecommerceplatform.repositories.ProductRepository;
import com.group4.ecommerceplatform.repositories.UserRepository;
import com.group4.ecommerceplatform.services.client.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service("clientCartService")
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Cart getOrCreateCartByUserId(Integer userId) {
        validateUserId(userId);

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCartForUser(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartProduct> getCartItems(Integer userId) {
        validateUserId(userId);

        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) {
            return List.of();
        }

        return cartProductRepository.findByCartId(cart.getId());
    }

    @Override
    public CartProduct addProductToCart(Integer userId, Integer productId, Integer quantity) {
        validateUserId(userId);
        validateProductId(productId);
        validateQuantity(quantity);

        // Lấy product và validate
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm với ID: " + productId));

        if (!product.getIsActive()) {
            throw new IllegalArgumentException("Sản phẩm này hiện không còn bán");
        }

        // Lấy hoặc tạo cart
        Cart cart = getOrCreateCartByUserId(userId);

        // Kiểm tra sản phẩm đã có trong giỏ chưa
        CartProduct cartProduct = cartProductRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        if (cartProduct != null) {
            // Sản phẩm đã có → tăng quantity
            int newQuantity = cartProduct.getQuantity() + quantity;
            validateStockQuantity(product, newQuantity);
            cartProduct.setQuantity(newQuantity);
        } else {
            // Sản phẩm chưa có → tạo mới
            validateStockQuantity(product, quantity);
            cartProduct = new CartProduct();
            cartProduct.setCartId(cart.getId());
            cartProduct.setProductId(productId);
            cartProduct.setQuantity(quantity);
        }

        return cartProductRepository.save(cartProduct);
    }

    @Override
    public CartProduct updateProductQuantity(Integer userId, Integer productId, Integer quantity) {
        validateUserId(userId);
        validateProductId(productId);
        validateQuantity(quantity);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giỏ hàng"));

        CartProduct cartProduct = cartProductRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không có trong giỏ hàng"));

        // Validate stock
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));
        validateStockQuantity(product, quantity);

        cartProduct.setQuantity(quantity);
        return cartProductRepository.save(cartProduct);
    }

    @Override
    public void removeProductFromCart(Integer userId, Integer productId) {
        validateUserId(userId);
        validateProductId(productId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giỏ hàng"));

        cartProductRepository.deleteByCartIdAndProductId(cart.getId(), productId);
    }

    @Override
    public void clearCart(Integer userId) {
        validateUserId(userId);

        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart != null) {
            cartProductRepository.deleteByCartId(cart.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long calculateCartTotal(Integer userId) {
        List<CartProduct> cartItems = getCartItems(userId);

        return cartItems.stream()
                .mapToLong(item -> {
                    BigDecimal price = item.getProduct().getPrice();
                    return price.longValue() * item.getQuantity();
                })
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(Integer userId) {
        validateUserId(userId);

        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) {
            return 0;
        }

        return (int) cartProductRepository.countByCartId(cart.getId());
    }

    // ==================== Private Helper Methods ====================

    private Cart createNewCartForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với ID: " + userId));

        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    private void validateUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID người dùng không hợp lệ");
        }
    }

    private void validateProductId(Integer productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
    }

    private void validateStockQuantity(Product product, Integer requestedQuantity) {
        if (product.getStockQuantity() < requestedQuantity) {
            throw new IllegalArgumentException(
                    String.format("Số lượng tồn kho không đủ. Còn lại: %d, yêu cầu: %d",
                            product.getStockQuantity(), requestedQuantity));
        }
    }
}
