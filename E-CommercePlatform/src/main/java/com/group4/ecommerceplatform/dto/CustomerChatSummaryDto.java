package com.group4.ecommerceplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerChatSummaryDto {
    private Long customerId;
    private String customerName;
    private String lastMessage;
    private String lastMessageTime;
    private Integer unreadCount;
}
