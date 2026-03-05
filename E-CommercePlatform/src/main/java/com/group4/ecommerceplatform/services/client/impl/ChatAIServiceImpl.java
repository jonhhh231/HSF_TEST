package com.group4.ecommerceplatform.services.client.impl;

import com.group4.ecommerceplatform.dto.AiIntentResponse;
import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.repositories.ProductRepository;
import com.group4.ecommerceplatform.services.client.ChatAIService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ChatAIServiceImpl implements ChatAIService {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ProductRepository productRepository;
    private String generatePrompt(String userMessage){
        return "Bạn là chuyên gia tư vấn laptop của 'TechStore'. \n" +
                "TUYỆT ĐỐI KHÔNG SHOW THÔNG TIN CỬA HÀNG" +
                "Thông tin của hàng(dựa vào đây để trả lời nếu khách có hỏi đến): " +
                "+ Địa chỉ: cửa hàng nằm ở 165 Linh Trung Thủ Đức, mở cửa từ thứ 2 đến thứ 7 từ 8h sáng đến 17h" +
                "                Nhiệm vụ: Phân tích câu chat và trả về định dạng JSON chính xác.\n" +
                "                \n" +
                "                Các trường dữ liệu cần trích xuất:\n" +
                "                - categoryName: Loại máy (Gaming, Văn phòng, Mỏng nhẹ).\n" +
                "                - minPrice/maxPrice: Khoảng giá (đơn vị VNĐ). Nếu khách nói 'tầm 20tr' thì maxPrice=20000000.\n" +
                "                - processor: Chip (i5, i7, Ryzen...).\n" +
                "                - graphics: Card đồ họa (RTX 3050, Card onboard...).\n" +
                "                - storage: Ổ cứng (256GB, 512GB, 1TB...).\n" +
                "                - display: Màn hình (15.6 inch, OLED, 144Hz...).\n" +
                "                - battery: Pin (56Wh, 70Wh...).\n" +
                "                - intent: 'PURCHASE' nếu tìm máy, 'INFO' nếu hỏi địa chỉ/bảo hành.\n" +
                "                - reply: Câu trả lời thân thiện nếu intent là 'INFO'." +
                "QUY TẮC QUAN TRỌNG:\n" +
                "            1. Nếu không có thông tin về minPrice hoặc maxPrice, GIÁ TRỊ PHẢI LÀ NULL (không để trong ngoặc kép).\n" +
                "            2. KHÔNG ĐƯỢC trả về chuỗi \"unknown\" cho các trường kiểu số.\n" +
                "            3. Các trường String khác nếu không có hãy để null." +
                "- Tin nhắn của user: " + userMessage;
    }
    public String processMessage(String userMessage) {
        // Gọi AI trích xuất thông tin
        AiIntentResponse ai = chatClient.prompt()
                .user(this.generatePrompt(userMessage))
                .call()
                .entity(AiIntentResponse.class);
        System.out.println(ai.toString());
        if ("PURCHASE".equals(ai.intent())) {
            BigDecimal min = ai.minPrice() != null ? BigDecimal.valueOf(ai.minPrice()) : null;
            BigDecimal max = ai.maxPrice() != null ? BigDecimal.valueOf(ai.maxPrice()) : null;

            // Thực hiện truy vấn DB với các field đã lọc
            List<Product> products = productRepository.findSmartSearch(
                    ai.categoryName(), min, max, ai.processor(),
                    ai.graphics(), ai.storage(), ai.display(), ai.battery()
            );

            if (products.isEmpty()) {
                return "Rất tiếc, shop chưa có máy nào khớp chính xác với yêu cầu của bạn, bạn thử tìm dòng sản phẩm khác nhé!";
            }

            return formatResponse(products);
        }

        return ai.reply();
    }

    private String formatResponse(List<Product> products) {
        StringBuilder sb = new StringBuilder("Dưới đây là các mẫu máy phù hợp nhất cho bạn:\n\n");
        products.stream().limit(3).forEach(p -> {
            sb.append("💻 **").append(p.getName()).append("**\n");
            sb.append("💰 Giá: ").append(String.format("%,.0f", p.getPrice())).append("đ\n");
            sb.append("⚙️ ").append(p.getProcessor()).append(" | ").append(p.getGraphics()).append("\n");
            sb.append("📦 SSD: ").append(p.getStorage()).append(" | Màn: ").append(p.getDisplay()).append("\n");

            sb.append("🔗 [Xem chi tiết sản phẩm tại đây](/products/").append(p.getId()).append(")\n\n");
        });
        return sb.toString();
    }
}
