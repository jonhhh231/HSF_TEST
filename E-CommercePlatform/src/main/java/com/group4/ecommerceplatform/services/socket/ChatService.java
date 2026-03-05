package com.group4.ecommerceplatform.services.socket;

import com.group4.ecommerceplatform.dto.ChatMessageDto;
import com.group4.ecommerceplatform.entities.ChatMessage;
import com.group4.ecommerceplatform.entities.User;
import com.group4.ecommerceplatform.repositories.ChatMessageRepository;
import com.group4.ecommerceplatform.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
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
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User gửi tin nhắn!"));

        // 2. Chuyển đổi DTO -> Entity bằng Builder
        ChatMessage entity = ChatMessage.builder()
                .sender(sender)
                .receiverId(dto.getReceiverId())
                .content(dto.getContent())
                .build();
        // createdAt sẽ được tự động tạo nhờ @PrePersist trong Entity

        // 3. Lưu vào DB
        chatMessageRepository.save(entity);
    }

    // Hàm lấy lịch sử chat để load lên giao diện khi người dùng vừa mở khung chat
    public List<ChatMessageDto> getChatHistory(Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findChatHistoryByUserId(userId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        // Chuyển Entity -> DTO trả về cho giao diện
        return messages.stream().map(msg -> ChatMessageDto.builder()
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getFullName()) // Giả sử User có getFullName()
                .receiverId(msg.getReceiverId())
                .content(msg.getContent())
                .timestamp(msg.getCreatedAt().format(formatter))
                .type(ChatMessageDto.MessageType.CHAT)
                .build()
        ).collect(Collectors.toList());
    }
}