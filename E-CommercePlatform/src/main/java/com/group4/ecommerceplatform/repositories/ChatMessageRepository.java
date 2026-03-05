package com.group4.ecommerceplatform.repositories;

import com.group4.ecommerceplatform.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE m.sender.id = :userId OR m.receiver.id = :userId ORDER BY m.createdAt ASC")
    List<ChatMessage> findChatHistoryByUserId(@Param("userId") Long userId);

    // Lấy danh sách customer đã chat - Lấy tất cả customer đã có tin nhắn
    // Lấy customer từ sender (type = 0) và từ receiver (type = 1)
    @Query("SELECT DISTINCT u FROM User u WHERE " +
           "u.id IN (SELECT m.sender.id FROM ChatMessage m WHERE m.type = 0) OR " +
           "u.id IN (SELECT m.receiver.id FROM ChatMessage m WHERE m.type = 1)")
    List<com.group4.ecommerceplatform.entities.User> findAllCustomersWithMessages();

    // Lấy tin nhắn cuối cùng của một customer (bất kể ai gửi)
    @Query("SELECT m FROM ChatMessage m WHERE m.sender.id = :customerId OR m.receiver.id = :customerId ORDER BY m.createdAt DESC")
    List<ChatMessage> findLastMessageByCustomerId(@Param("customerId") Long customerId);
}