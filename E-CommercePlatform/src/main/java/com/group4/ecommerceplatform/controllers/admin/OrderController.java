package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.entities.Order;
import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.services.admin.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {
    @Autowired
    @Qualifier("adminOrderService")
    private OrderService orderService;
    @GetMapping
    public String getOrderList(Model model,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "page", required = false, defaultValue = "1") String page,
                               @RequestParam(value = "size", required = false, defaultValue = "10") String size){
        keyword = keyword==null?"":keyword.trim();
        int pageNumber = Integer.parseInt(page);
        int pageSize = Integer.parseInt(size);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Order> orderPage = orderService.getOrderList(keyword, pageable);
        model.addAttribute("orderList", orderPage.getContent());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());
        model.addAttribute("keyword",  keyword);
        return "admin/pages/order-list";
    }
    // chỉ trả cho đơn COD
    @PostMapping("/change-status/{id}/{stauts}")
    public String updateStatus(@PathVariable("id") Integer id, @PathVariable("stauts") String stauts, Model model) {
        try {
            orderService.changeCodePaymentStatus(id, stauts);
            return "redirect:/admin/orders";
        }catch (Exception e){
            model.addAttribute("message", e.getMessage());
            return "admin/pages/errors/base-error";
        }
    }
}
