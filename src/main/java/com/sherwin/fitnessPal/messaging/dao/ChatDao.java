package com.sherwin.fitnessPal.messaging.dao;

import com.sherwin.fitnessPal.messaging.domain.ChatInput;
import com.sherwin.fitnessPal.messaging.domain.ChatResponse;

import java.util.Collection;
import java.util.List;

public interface ChatDao {
    long insertChatMessage(ChatInput message);

    void insertChatMessages(List<ChatResponse> cacheResponses);

    ChatResponse findMessageById(Long id);

    List<ChatResponse> findMessagesByUserName(String username);

    void deleteChatMessagesByUserName(String username);

    Collection<ChatInput> findAllMessages();

     void deleteChatMessages(List<ChatResponse> expiredMessages);
}
