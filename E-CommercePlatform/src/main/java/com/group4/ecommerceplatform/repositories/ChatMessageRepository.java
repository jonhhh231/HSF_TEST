package com.group4.ecommerceplatform.repositories;

import com.group4.ecommerceplatform.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE m.sender.id = :userId OR m.receiverId = :userId ORDER BY m.createdAt ASC")
    List<ChatMessage> findChatHistoryByUserId(@Param("userId") Long userId);
}