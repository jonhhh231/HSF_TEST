package com.group4.ecommerceplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String content;
    private MessageType type;
    private String timestamp;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}