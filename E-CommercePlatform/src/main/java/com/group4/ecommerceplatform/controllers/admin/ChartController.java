package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.dto.ChartDataDto;
import com.group4.ecommerceplatform.services.admin.impl.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 1. Import Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ChartController {

    @Autowired
    private ChartService chartService;

    @GetMapping("/admin/chart")
    public String showDashboard(Model model) {
        LocalDate now = LocalDate.now();
        model.addAttribute("currentMonth", now.getMonthValue());
        model.addAttribute("currentYear", now.getYear());
        return "admin/pages/chart";
    }

    @GetMapping("/api/stats/summary")
    @ResponseBody
    public Integer getSummaryApi(@RequestParam int month, @RequestParam int year) {
        return chartService.getTotalOrdersInMonth(month, year);
    }

    @GetMapping("/api/stats/revenue")
    @ResponseBody
    public List<ChartDataDto> getRevenueApi(@RequestParam int month, @RequestParam int year) {
        return chartService.getRevenueChartData(month, year);
    }

    @GetMapping("/api/stats/top-products")
    @ResponseBody
    public List<ChartDataDto> getTopProductsApi(@RequestParam int month, @RequestParam int year) {
        return chartService.getTopProductChartData(month, year);
    }
}