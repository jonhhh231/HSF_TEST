package com.group4.ecommerceplatform.controllers.socket;

import com.group4.ecommerceplatform.dto.ChatMessageDto;
import com.group4.ecommerceplatform.entities.User;
import com.group4.ecommerceplatform.services.socket.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    // Khách hàng gửi tới Admin qua đường dẫn: /app/chat.sendToAdmin
    @MessageMapping("/chat.sendToAdmin")
    public void sendToAdmin(@Payload ChatMessageDto chatMessage) {
        // 1. Tạo timestamp chuẩn để hiển thị trên giao diện
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));

        // 2. Gọi Service để lưu tin nhắn vào Database
        chatService.saveMessage(chatMessage);

        // 3. Gửi tới tất cả admin đang subscribe topic này
        messagingTemplate.convertAndSend("/topic/admin/messages", chatMessage);
    }

    // Admin gửi tin nhắn riêng cho 1 khách hàng cụ thể
    @MessageMapping("/chat.sendToUser")
    public void sendToUser(@Payload ChatMessageDto chatMessage) {
        // 1. Tạo timestamp
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));

        // 2. Lưu tin nhắn vào Database
        chatService.saveMessage(chatMessage);

        // 3. Ép kiểu ID người nhận sang String (Bắt buộc đối với convertAndSendToUser)
        String receiverIdStr = String.valueOf(chatMessage.getReceiverId());

        // 4. Gửi tới topic riêng của User: /user/{userId}/topic/messages
        messagingTemplate.convertAndSendToUser(
                receiverIdStr,
                "/topic/messages",
                chatMessage
        );
    }
}