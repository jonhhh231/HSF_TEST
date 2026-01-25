package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.dto.ChartDataDto;
import com.group4.ecommerceplatform.services.admin.impl.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired
    private ChartService chartService;

    @GetMapping("/admin/dashboard")
    public String showDashboard(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model) {

        LocalDate now = LocalDate.now();
        // Nếu không có tham số (lần đầu vào trang), dùng tháng/năm hiện tại
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();

        // Lấy dữ liệu từ Service
        int totalOrders = chartService.getTotalOrdersInMonth(targetMonth, targetYear);
        List<ChartDataDto> revenueData = chartService.getRevenueChartData(targetMonth, targetYear);
        List<ChartDataDto> topProducts = chartService.getTopProductChartData(targetMonth, targetYear);

        // Đẩy toàn bộ vào Model
        model.addAttribute("selectedMonth", targetMonth);
        model.addAttribute("selectedYear", targetYear);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("revenueData", revenueData);
        model.addAttribute("topProducts", topProducts);

        return "admin/pages/dashboard";
    }
}
