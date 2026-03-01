package com.group4.ecommerceplatform.dto;
public record AiIntentResponse(
        String intent,          // "PURCHASE" hoặc "INFO"
        String categoryName,    // Ví dụ: "Laptop Gaming", "Laptop Văn Phòng"
        Double minPrice,        // Giá thấp nhất
        Double maxPrice,        // Giá cao nhất
        String processor,       // Ví dụ: "i5", "Ryzen 7"
        String graphics,        // Ví dụ: "RTX 3050", "Intel Iris Xe"
        String storage,         // Ví dụ: "512GB", "1TB"
        String display,         // Ví dụ: "15.6 inch", "OLED"
        String battery,         // Ví dụ: "56Wh"
        String reply            // Câu trả lời nếu là INFO
) {}
