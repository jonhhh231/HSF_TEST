package com.group4.ecommerceplatform.controllers.client;

import com.group4.ecommerceplatform.entities.CartProduct;
import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.entities.User;
import com.group4.ecommerceplatform.services.client.CartService;
import com.group4.ecommerceplatform.services.client.OrderService;
import com.group4.ecommerceplatform.services.payment.MomoPaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
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
    private OrderService orderService;

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

    /**
     * Hiển thị trang thông tin đặt hàng
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return "redirect:/auth/login";
        }

        List<CartProduct> cartItems = cartService.getCartItems(userId);

        if (cartItems == null || cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng trống");
            return "redirect:/cart";
        }

        Long cartTotal = cartService.calculateCartTotal(userId);
        int itemCount = cartService.getCartItemCount(userId);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("itemCount", itemCount);

        return "client/checkout";
    }

    /**
     * API endpoint để lưu địa chỉ vào session (được gọi bởi AJAX)
     */
    @PostMapping("/checkout/save-address")
    @ResponseBody
    public java.util.Map<String, Object> saveAddress(@RequestParam("address") String address, HttpSession session) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            session.setAttribute("pendingAddress", address);
            response.put("success", true);
            response.put("message", "Đã lưu địa chỉ");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lưu địa chỉ");
        }

        return response;
    }

    /**
     * Xử lý đặt hàng với thông tin địa chỉ
     */
    @PostMapping("/checkout/proceed")
    public String proceedCheckout(
            @RequestParam("address") String address,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "paymentMethod", defaultValue = "MOMO") String paymentMethod,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return "redirect:/auth/login";
        }

        // Validate address
        if (address == null || address.trim().length() < 10) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập địa chỉ giao hàng đầy đủ");
            return "redirect:/cart/checkout";
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

            // Lưu thông tin vào session
            session.setAttribute("pendingCartItems", cartItems);
            session.setAttribute("pendingCartTotal", cartTotal);
            session.setAttribute("pendingAddress", address.trim());
            session.setAttribute("pendingPhone", phone);
            session.setAttribute("pendingNote", note);
            session.setAttribute("pendingPaymentMethod", paymentMethod);

            // Tạo mã đơn hàng tạm thời
            String tempOrderCode = "ORD_" + System.currentTimeMillis();

            // Lưu orderCode vào session
            session.setAttribute("pendingOrderCode", tempOrderCode);

            // Xử lý theo phương thức thanh toán
            if ("CASH".equalsIgnoreCase(paymentMethod)) {
                // Thanh toán tiền mặt - tạo đơn hàng ngay
                return processCODOrder(session, redirectAttributes, userId, cartItems, cartTotal,
                                      address.trim(), phone, note, tempOrderCode);
            } else {
                // Thanh toán MoMo
                Order tempOrder = new Order();
                tempOrder.setOrderCode(tempOrderCode);
                tempOrder.setFinalPrice(BigDecimal.valueOf(cartTotal));
                tempOrder.setAddress(address.trim());

                // Tạo URL thanh toán MoMo
                String paymentUrl = momoPaymentService.createPaymentUrl(tempOrder);

                // Redirect đến trang thanh toán MoMo
                return "redirect:" + paymentUrl;
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/cart/checkout";
        }
    }

    /**
     * Xử lý đơn hàng thanh toán tiền mặt (COD)
     */
    private String processCODOrder(HttpSession session, RedirectAttributes redirectAttributes,
                                   Integer userId, List<CartProduct> cartItems, Long cartTotal,
                                   String address, String phone, String note, String orderCode) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return "redirect:/auth/login";
            }

            // Kết hợp địa chỉ với phone và note
            String fullAddress = address;
            if (phone != null && !phone.isEmpty()) {
                fullAddress += " - SĐT: " + phone;
            }
            if (note != null && !note.isEmpty()) {
                fullAddress += " - Ghi chú: " + note;
            }

            // Tạo đơn hàng sử dụng service có sẵn (CASH = chưa thanh toán)
            orderService.createOrderFromCart(user, orderCode, cartItems, cartTotal, "CASH", fullAddress);

            // Xóa giỏ hàng
            cartService.clearCart(userId);

            // Xóa session pending
            session.removeAttribute("pendingCartItems");
            session.removeAttribute("pendingCartTotal");
            session.removeAttribute("pendingAddress");
            session.removeAttribute("pendingPhone");
            session.removeAttribute("pendingNote");
            session.removeAttribute("pendingOrderCode");
            session.removeAttribute("pendingPaymentMethod");

            // Redirect đến trang thành công
            redirectAttributes.addFlashAttribute("successMessage", "Đặt hàng thành công! Mã đơn hàng: " + orderCode);
            redirectAttributes.addFlashAttribute("orderCode", orderCode);
            redirectAttributes.addFlashAttribute("paymentMethod", "CASH");
            return "redirect:/orders?success=true&orderCode=" + orderCode;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi tạo đơn hàng: " + e.getMessage());
            return "redirect:/cart/checkout";
        }
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
    
    private void updateCartCountInSession(HttpSession session, Integer userId) {
        if (userId != null) {
            int count = cartService.getCartItemCount(userId);
            session.setAttribute("cartItemCount", count);
        } else {
            session.setAttribute("cartItemCount", 0);
        }
    }
}
