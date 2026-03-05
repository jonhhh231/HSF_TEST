package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.dto.ChatMessageDto;
import com.group4.ecommerceplatform.dto.CustomerChatSummaryDto;
import com.group4.ecommerceplatform.services.socket.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin/messages")
public class MessageController {

    @Autowired
    private ChatService chatService;

    @GetMapping
    public String listMessages(Model model) {
        model.addAttribute("title", "Quản lý tin nhắn");
        return "admin/pages/messages";
    }

    // API lấy danh sách customer đã chat
    @GetMapping("/api/customers")
    @ResponseBody
    public ResponseEntity<List<CustomerChatSummaryDto>> getCustomersWithMessages() {
        List<CustomerChatSummaryDto> customers = chatService.getCustomersWithMessages();
        return ResponseEntity.ok(customers);
    }

    // API lấy lịch sử chat với một customer
    @GetMapping("/api/history/{customerId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessageDto>> getChatHistoryWithCustomer(@PathVariable Long customerId) {
        List<ChatMessageDto> history = chatService.getChatHistory(customerId);
        return ResponseEntity.ok(history);
    }
}