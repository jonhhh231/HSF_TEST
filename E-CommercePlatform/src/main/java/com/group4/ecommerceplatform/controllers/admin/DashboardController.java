package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.dto.ChartDataDto;
import com.group4.ecommerceplatform.services.admin.impl.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayInputStream; // Sửa lỗi Cannot resolve symbol 'ByteArrayInputStream'
import java.io.IOException;

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
        List<ChartDataDto> categorySales = chartService.getCategorySalesData(targetMonth, targetYear);

        // Đẩy toàn bộ vào Model
        model.addAttribute("categorySales", categorySales);
        model.addAttribute("selectedMonth", targetMonth);
        model.addAttribute("selectedYear", targetYear);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("revenueData", revenueData);
        model.addAttribute("topProducts", topProducts);

        return "admin/pages/dashboard";
    }

    @GetMapping("/admin/dashboard/export")
    public ResponseEntity<InputStreamResource> exportExcel(
            @RequestParam int month,
            @RequestParam int year) throws IOException {

        ByteArrayInputStream in = chartService.exportDashboardToExcel(month, year);


        String fileName = "ThongKe_" + month + "_" + year + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in)); // Bây giờ sẽ nhận diện được InputStreamResource
    }
}
