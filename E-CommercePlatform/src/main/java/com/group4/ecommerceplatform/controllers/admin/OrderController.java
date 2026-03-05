package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.services.admin.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

    @Autowired
    @Qualifier("adminOrderService")
    private OrderService orderService;

    // ------------------------------------------------------------------ //
    // Danh sách đơn hàng
    // ------------------------------------------------------------------ //

    @GetMapping
    public String getOrderList(Model model,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") String page,
            @RequestParam(value = "size", required = false, defaultValue = "10") String size) {
        keyword = keyword == null ? "" : keyword.trim();
        int pageNumber = Integer.parseInt(page);
        int pageSize = Integer.parseInt(size);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Order> orderPage = orderService.getOrderList(keyword, pageable);
        model.addAttribute("orderList", orderPage.getContent());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        return "admin/pages/order-list";
    }

    // ------------------------------------------------------------------ //
    // Chi tiết đơn hàng
    // ------------------------------------------------------------------ //

    @GetMapping("/detail/{id}")
    public String getOrderDetail(@PathVariable Integer id, Model model) {
        try {
            Order order = orderService.getOrderById(id);
            model.addAttribute("order", order);
            return "admin/pages/order-detail";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "admin/pages/errors/base-error";
        }
    }

    // ------------------------------------------------------------------ //
    // Tiến trạng thái vận chuyển (thuận chiều)
    // ------------------------------------------------------------------ //

    @PostMapping("/to-next-handle-status/{id}")
    public String updateOrderStatus(@PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        try {
            orderService.changeToNextHandleStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/detail/" + id;
    }

    // ------------------------------------------------------------------ //
    // Giao hàng thất bại
    // ------------------------------------------------------------------ //

    @PostMapping("/delivery-failed/{id}")
    public String markDeliveryFailed(@PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        try {
            orderService.markDeliveryFailed(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã đánh dấu giao hàng thất bại");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/detail/" + id;
    }

    // ------------------------------------------------------------------ //
    // Xác nhận hoàn tiền xong (đơn online)
    // ------------------------------------------------------------------ //

    @PostMapping("/confirm-refunded/{id}")
    public String confirmRefunded(@PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        try {
            orderService.confirmRefunded(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xác nhận hoàn tiền xong");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/detail/" + id;
    }

    // ------------------------------------------------------------------ //
    // Toggle thanh toán COD (thu tiền khi giao hàng)
    // ------------------------------------------------------------------ //

    @PostMapping("/change-cod-payment/{id}/{status}")
    public String changeCodPayment(@PathVariable Integer id,
            @PathVariable String status,
            RedirectAttributes redirectAttributes) {
        try {
            orderService.changeCodPaymentStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thanh toán thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/detail/" + id;
    }
}
