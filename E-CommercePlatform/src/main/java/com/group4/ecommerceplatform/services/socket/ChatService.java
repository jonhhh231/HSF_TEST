package com.group4.ecommerceplatform.services.socket;

import com.group4.ecommerceplatform.dto.ChatMessageDto;
import com.group4.ecommerceplatform.dto.CustomerChatSummaryDto;
import com.group4.ecommerceplatform.entities.ChatMessage;
import com.group4.ecommerceplatform.entities.User;
import com.group4.ecommerceplatform.repositories.ChatMessageRepository;
import com.group4.ecommerceplatform.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveMessage(ChatMessageDto dto) {
        // 1. Tìm User gửi tin nhắn
        User sender = userRepository.findById(dto.getSenderId().intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User gửi tin nhắn!"));

        // 2. Tìm User nhận tin nhắn (nếu có)
        User receiver = null;
        if (dto.getReceiverId() != null) {
            receiver = userRepository.findById(dto.getReceiverId().intValue())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy User nhận tin nhắn!"));
        }

        // 3. Xác định type: 0 = CUSTOMER, 1 = ADMIN
        Integer messageType = determineMessageType(sender);

        // 4. Chuyển đổi DTO -> Entity bằng Builder
        ChatMessage entity = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .type(messageType)
                .content(dto.getContent())
                .build();
        // createdAt sẽ được tự động tạo nhờ @PrePersist trong Entity

        // 5. Lưu vào DB
        chatMessageRepository.save(entity);
    }

    private Integer determineMessageType(User user) {
        // Kiểm tra role của user để xác định type
        // 0 = CUSTOMER, 1 = ADMIN
        if (user.getRole() != null && user.getRole().toUpperCase().contains("ADMIN")) {
            return 1; // ADMIN
        }
        return 0; // CUSTOMER
    }

    // Hàm lấy lịch sử chat để load lên giao diện khi người dùng vừa mở khung chat
    public List<ChatMessageDto> getChatHistory(Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findChatHistoryByUserId(userId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        // Chuyển Entity -> DTO trả về cho giao diện
        return messages.stream().map(msg -> ChatMessageDto.builder()
                .senderId(msg.getSender().getId().longValue())
                .senderName(msg.getSender().getFullName()) // Giả sử User có getFullName()
                .receiverId(msg.getReceiver() != null ? msg.getReceiver().getId().longValue() : null)
                .content(msg.getContent())
                .timestamp(msg.getCreatedAt().format(formatter))
                .type(ChatMessageDto.MessageType.CHAT)
                .build()
        ).collect(Collectors.toList());
    }

    // Lấy danh sách customer đã chat
    public List<CustomerChatSummaryDto> getCustomersWithMessages() {
        List<User> customers = chatMessageRepository.findAllCustomersWithMessages();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        List<CustomerChatSummaryDto> result = new ArrayList<>();

        for (User customer : customers) {
            List<ChatMessage> lastMessages = chatMessageRepository.findLastMessageByCustomerId(customer.getId().longValue());

            String lastMessage = "";
            String lastMessageTime = "";

            if (!lastMessages.isEmpty()) {
                ChatMessage lastMsg = lastMessages.get(0);
                lastMessage = lastMsg.getContent();
                lastMessageTime = lastMsg.getCreatedAt().format(formatter);
            }

            CustomerChatSummaryDto dto = CustomerChatSummaryDto.builder()
                    .customerId(customer.getId().longValue())
                    .customerName(customer.getFullName())
                    .lastMessage(lastMessage)
                    .lastMessageTime(lastMessageTime)
                    .unreadCount(0) // Có thể tính sau nếu cần
                    .build();

            result.add(dto);
        }

        // Sắp xếp theo thời gian tin nhắn mới nhất
        result.sort((a, b) -> {
            List<ChatMessage> messagesA = chatMessageRepository.findLastMessageByCustomerId(a.getCustomerId());
            List<ChatMessage> messagesB = chatMessageRepository.findLastMessageByCustomerId(b.getCustomerId());

            if (messagesA.isEmpty() && messagesB.isEmpty()) return 0;
            if (messagesA.isEmpty()) return 1;
            if (messagesB.isEmpty()) return -1;

            return messagesB.get(0).getCreatedAt().compareTo(messagesA.get(0).getCreatedAt());
        });

        return result;
    }
}