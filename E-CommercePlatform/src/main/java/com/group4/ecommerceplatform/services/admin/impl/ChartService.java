package com.group4.ecommerceplatform.services.admin.impl;

import com.group4.ecommerceplatform.dto.ChartDataDto;
import com.group4.ecommerceplatform.repositories.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
