package com.group4.ecommerceplatform.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/messages")
public class MessageController {

    @GetMapping
    public String listMessages(Model model) {
        model.addAttribute("title", "Quản lý tin nhắn");

        return "admin/pages/messages";
    }
}