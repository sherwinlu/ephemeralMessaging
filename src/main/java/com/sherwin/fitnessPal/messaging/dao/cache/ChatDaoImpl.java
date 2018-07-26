package com.sherwin.fitnessPal.messaging.dao.cache;

import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.sherwin.fitnessPal.messaging.dao.ChatDao;
import com.sherwin.fitnessPal.messaging.domain.ChatInput;
import com.sherwin.fitnessPal.messaging.domain.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class ChatDaoImpl implements ChatDao {
    @Autowired
    @Qualifier("dbChatDao")
    private ChatDao dbChatDao;

    @Autowired
    @Qualifier("messageIdMap")
    private Map<Long, ChatInput> messageIdMap;

    @Autowired
    @Qualifier("messageUserNameMap")
    private Map<String, ChatInput> messageUserNameMap;

    @Autowired
    private FlakeIdGenerator keyGenerator;

    @Override
    public long insertChatMessage(ChatInput message) {
        message.setId(keyGenerator.newId());
        messageIdMap.put(message.getId(), message);
        ChatInput cachedMessage = messageUserNameMap.get(message.getUsername());
        if (cachedMessage != null) {
            dbChatDao.insertChatMessage(cachedMessage);
        }
        messageUserNameMap.put(message.getUsername(), message);
        return message.getId();
    }

    @Override
    public ChatResponse findMessageById(Long id) {
        ChatInput cachedMessage = messageIdMap.get(id);
        ChatResponse response = null;
        if (cachedMessage != null) {
            response = new ChatResponse();
            response.setUsername(cachedMessage.getUsername());
            response.setText(cachedMessage.getText());
            response.setExpirationDate(cachedMessage.getExpirationDate());

        }
        return response;
    }

    @Override
    public List<ChatResponse> findMessagesByUserName(String username) {
        ChatInput cachedMessage = messageUserNameMap.get(username);
        List<ChatResponse> chatResponses = null;

        if (cachedMessage != null) {
            ChatResponse response = new ChatResponse();
            response.setId(cachedMessage.getId());
            response.setText(cachedMessage.getText());
            response.setUsername(cachedMessage.getUsername());
            response.setExpirationDate(cachedMessage.getExpirationDate());

            chatResponses = new ArrayList<>(1);
            chatResponses.add(response);
        }

        return chatResponses;
    }

    @Override
    public void deleteChatMessagesByUserName(String username) {
        ChatInput cachedMessage = messageUserNameMap.get(username);
        messageUserNameMap.remove(username);
        messageIdMap.remove(cachedMessage.getId());
    }

    @Override
    public Collection<ChatInput> findAllMessages() {
        return messageUserNameMap.values();
    }

    @Override
    public void deleteChatMessages(List<ChatResponse> expiredMessages) {
        for (ChatResponse expiredMessage : expiredMessages) {
            messageUserNameMap.remove(expiredMessage.getUsername());
            messageIdMap.remove(expiredMessage.getId());
        }
    }

    @Override
    public void insertChatMessages(List<ChatResponse> cacheResponses) {
        throw new UnsupportedOperationException();
    }


}
