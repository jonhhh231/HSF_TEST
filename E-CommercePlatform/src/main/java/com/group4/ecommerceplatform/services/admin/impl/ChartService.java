package com.group4.ecommerceplatform.services.admin.impl;

import com.group4.ecommerceplatform.dto.ChartDataDto;
import com.group4.ecommerceplatform.repositories.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChartService {

    @Autowired
    private StatsRepository statsRepo;

    private List<ChartDataDto> mapToDTO(List<Object[]> rawData) {
        List<ChartDataDto> list = new ArrayList<>();
        if (rawData == null || rawData.isEmpty()) {
            return list;
        }
        for (Object[] row : rawData) {
            String label = (row[0] != null) ? row[0].toString() : "Unknown";
            Double value = 0.0;
            if (row[1] != null) {
                value = ((Number) row[1]).doubleValue();
            }
            list.add(new ChartDataDto(label, value));
        }
        return list;
    }

    public List<ChartDataDto> getRevenueChartData(int month, int year) {
        return mapToDTO(statsRepo.getMonthlyRevenueRaw(month, year));
    }

    public List<ChartDataDto> getTopProductChartData(int month, int year) {
        return mapToDTO(statsRepo.getTopSellingRaw(month, year));
    }

    public Integer getTotalOrdersInMonth(int month, int year) {
        Integer count = statsRepo.countOrdersInMonth(month, year);
        return count != null ? count : 0;
    }

    public List<ChartDataDto> getCategorySalesData(int month, int year) {
        return mapToDTO(statsRepo.getCategorySalesRaw(month, year));
    }

    public ByteArrayInputStream exportDashboardToExcel(int month, int year) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet1 = workbook.createSheet("Doanh thu theo ngày");
            createSheetData(sheet1, "Ngày", "Doanh thu (VNĐ)", getRevenueChartData(month, year));

            Sheet sheet2 = workbook.createSheet("Top Sản phẩm");
            createSheetData(sheet2, "Tên sản phẩm", "Số lượng bán", getTopProductChartData(month, year));

            Sheet sheet3 = workbook.createSheet("Tỉ trọng loại hàng");
            createSheetData(sheet3, "Loại sản phẩm", "Số lượng bán", getCategorySalesData(month, year));

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private void createSheetData(Sheet sheet, String col1, String col2, List<ChartDataDto> data) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue(col1);
        header.createCell(1).setCellValue(col2);

        int rowIdx = 1;
        for (ChartDataDto dto : data) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(dto.getLabel());
            row.createCell(1).setCellValue(dto.getValue());
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
}
