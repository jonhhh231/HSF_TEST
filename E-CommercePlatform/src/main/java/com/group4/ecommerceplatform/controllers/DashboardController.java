package com.group4.ecommerceplatform.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model){
        model.addAttribute("title","Dashboard Page");
        return "admin/pages/dashboard";
    }
}
