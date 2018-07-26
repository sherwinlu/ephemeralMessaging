package com.sherwin.fitnessPal.messaging;

import com.hazelcast.core.HazelcastInstance;
import com.sherwin.fitnessPal.messaging.dao.ChatDao;
import com.sherwin.fitnessPal.messaging.domain.ChatInput;
import com.sherwin.fitnessPal.messaging.domain.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;

@Component
public class HotToColdChatStorageMover {
    @Autowired
    @Qualifier("dbChatDao")
    private ChatDao dbChatDao;

    @Autowired
    @Qualifier("chatDaoImpl")
    private ChatDao cacheChatDao;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    // every 30s run and check to see if there's any expired messages that need to be moved from hot (cache) to cold (db) storage
    @Scheduled(fixedDelay = 30000)
    public void cacheCleanUp() {
        Lock lock = hazelcastInstance.getLock( "cacheCleanUp" );
        lock.lock();
        try {
            Collection<ChatInput> messages = cacheChatDao.findAllMessages();
            List<ChatResponse> expiredMessages = new ArrayList();
            Date now = new Date();
            for (ChatInput message : messages) {
                if (now.compareTo(message.getExpirationDate()) > 0) {
                    expiredMessages.add(convertToChatResponse(message));
                }
            }

            if (!CollectionUtils.isEmpty(expiredMessages)) {
                dbChatDao.insertChatMessages(expiredMessages);
                cacheChatDao.deleteChatMessages(expiredMessages);
            }
        }
        finally {
            lock.unlock();
        }
    }

    private ChatResponse convertToChatResponse(ChatInput message) {
        ChatResponse response = new ChatResponse();
        response.setId(message.getId());
        response.setUsername(message.getUsername());
        response.setText(message.getText());
        response.setExpirationDate(message.getExpirationDate());

        return response;
    }
}
