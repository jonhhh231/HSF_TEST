package com.group4.ecommerceplatform.controllers.client;

import com.group4.ecommerceplatform.entities.CartProduct;
import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.entities.OrderDetail;
import com.group4.ecommerceplatform.entities.User;
import com.group4.ecommerceplatform.repositories.OrderDetailRepository;
import com.group4.ecommerceplatform.repositories.OrderRepository;
import com.group4.ecommerceplatform.repositories.UserRepository;
import com.group4.ecommerceplatform.services.client.CartService;
import com.group4.ecommerceplatform.services.payment.MomoPaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class ClientCartController {

    @Autowired
    @Qualifier("clientCartService")
    private CartService cartService;

    @Autowired
    private MomoPaymentService momoPaymentService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return "redirect:/auth/login";
        }

        List<CartProduct> cartItems = cartService.getCartItems(userId);
        Long cartTotal = cartService.calculateCartTotal(userId);
        int itemCount = cartService.getCartItemCount(userId);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("itemCount", itemCount);

        return "client/cart";
    }

    @PostMapping("/add")
    public String addToCart(
            @RequestParam("productId") Integer productId,
            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "Referer", required = false) String referer) {

        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng");
            return "redirect:/auth/login";
        }

        try {
            cartService.addProductToCart(userId, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng");
        }

        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/products";
    }

    @PostMapping("/update")
    public String updateCartItem(
            @RequestParam("productId") Integer productId,
            @RequestParam("quantity") Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return "redirect:/auth/login";
        }

        try {
            if (quantity <= 0) {
                cartService.removeProductFromCart(userId, productId);
                redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng");
            } else {
                cartService.updateProductQuantity(userId, productId, quantity);
                redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật số lượng");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật giỏ hàng");
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(
            @PathVariable("productId") Integer productId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return "redirect:/auth/login";
        }

        try {
            cartService.removeProductFromCart(userId, productId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa sản phẩm");
        }

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return "redirect:/auth/login";
        }

        try {
            cartService.clearCart(userId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tất cả sản phẩm trong giỏ hàng");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa giỏ hàng");
        }

        return "redirect:/cart";
    }

    @GetMapping("/count")
    @ResponseBody
    public int getCartItemCount(HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) {
            return 0;
        }
        return cartService.getCartItemCount(userId);
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return "redirect:/auth/login";
        }

        try {
            // Lấy thông tin giỏ hàng
            List<CartProduct> cartItems = cartService.getCartItems(userId);

            if (cartItems == null || cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng trống");
                return "redirect:/cart";
            }

            // Tính tổng tiền
            Long cartTotal = cartService.calculateCartTotal(userId);

            // Lưu thông tin giỏ hàng vào session để tạo order sau khi thanh toán thành công
            session.setAttribute("pendingCartItems", cartItems);
            session.setAttribute("pendingCartTotal", cartTotal);

            // Tạo mã đơn hàng tạm thời (chưa lưu vào DB)
            String tempOrderCode = "ORD_" + System.currentTimeMillis();

            // Tạo temporary order object chỉ để gửi cho MoMo
            Order tempOrder = new Order();
            tempOrder.setOrderCode(tempOrderCode);
            tempOrder.setFinalPrice(BigDecimal.valueOf(cartTotal));

            // Lưu orderCode vào session
            session.setAttribute("pendingOrderCode", tempOrderCode);

            // Tạo URL thanh toán MoMo (không lưu order vào DB)
            String paymentUrl = momoPaymentService.createPaymentUrl(tempOrder);

            // KHÔNG xóa giỏ hàng ở đây - sẽ xóa khi thanh toán thành công

            // Redirect đến trang thanh toán MoMo
            return "redirect:" + paymentUrl;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    // ==================== Private Helper Methods ====================

    private Integer getCurrentUserId(HttpSession session) {
        return (Integer) session.getAttribute("userId");
    }
}
