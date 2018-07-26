package com.sherwin.fitnessPal.messaging.service;

import com.sherwin.fitnessPal.messaging.domain.ChatInput;
import com.sherwin.fitnessPal.messaging.domain.ChatResponse;

public interface ChatService {
    ChatResponse create(ChatInput message);

    ChatResponse retrieveMessageById(Long id);

    ChatResponse[] retrieveMessagesByUserName(String userName);
}
