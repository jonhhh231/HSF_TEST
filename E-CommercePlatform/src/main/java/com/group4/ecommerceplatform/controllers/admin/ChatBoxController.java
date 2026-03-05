package com.group4.ecommerceplatform.controllers.admin;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/chat")
public class ChatBoxController {
    @GetMapping("/list")
    public String list(){
        return "admin/pages/chat-list";
    }

    // Nhận tin từ: /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    // Gửi lại cho tất cả ai đang sub: /topic/public
    @SendTo("/topic/public")
    public String sendMessage(@Payload String chatMessage) {
        return chatMessage;
    }

    // Gửi tin nhắn riêng (Private Chat)
    @MessageMapping("/chat.private")
    @SendToUser("/queue/reply")
    public String handlePrivateMessage(String msg) {
        return "Chỉ mình bạn thấy tin này: " + msg;
    }
}
