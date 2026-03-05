package com.group4.ecommerceplatform.controllers.client;

import com.group4.ecommerceplatform.dto.ChatMessageDto;
import com.group4.ecommerceplatform.services.socket.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatHistoryController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ChatMessageDto>> getChatHistory(@PathVariable Long userId) {
        List<ChatMessageDto> history = chatService.getChatHistory(userId);
        return ResponseEntity.ok(history);
    }
}
