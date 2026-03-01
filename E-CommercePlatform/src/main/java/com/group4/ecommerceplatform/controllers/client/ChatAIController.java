package com.group4.ecommerceplatform.controllers.client;

import com.group4.ecommerceplatform.services.client.ChatAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatAIController {
    @Autowired
    private ChatAIService chatAIService;
    @PostMapping
    public ResponseEntity<String> chatWithAi(@RequestBody Map<String, Object> requestBody){
        String message =  (String) requestBody.get("message");
        String reply = chatAIService.processMessage(message);
        return ResponseEntity.ok(reply);
    }
}
