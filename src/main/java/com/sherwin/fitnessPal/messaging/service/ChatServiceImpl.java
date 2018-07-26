package com.sherwin.fitnessPal.messaging.service;

import com.sherwin.fitnessPal.messaging.dao.ChatDao;
import com.sherwin.fitnessPal.messaging.domain.ChatInput;
import com.sherwin.fitnessPal.messaging.domain.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    final static int DEFAULT_TIMEOUT = 60;

    @Autowired
    @Qualifier("dbChatDao")
    private ChatDao dbChatDao;

    @Autowired
    @Qualifier("chatDaoImpl")
    private ChatDao cacheChatDao;

    /**
     * create a new chat message and put it into 'hot' storage
     * @param message
     * @return
     */
    @Override
    public ChatResponse create(ChatInput message) {
        if (message.getTimeout() == null) {
            message.setTimeout(DEFAULT_TIMEOUT);
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, message.getTimeout());
        message.setExpirationDate(cal.getTime());
        ChatResponse response = new ChatResponse();
        response.setId(cacheChatDao.insertChatMessage(message));

        return response;
    }

    /**
     * retrieve the message by checking the 'hot' storage first and if it's not there
     * then check the 'cold' storage
     * @param id
     * @return
     */
    @Override
    public ChatResponse retrieveMessageById(Long id) {
        ChatResponse response = cacheChatDao.findMessageById(id);
        return response != null ? response : dbChatDao.findMessageById(id);
    }

    /**
     * retrieves all the messages for the 'username' by checking both 'cold' and 'hot' storage
     * @param userName
     * @return
     */
    @Override
    public ChatResponse[] retrieveMessagesByUserName(String userName) {
        List<ChatResponse> cacheResponses = cacheChatDao.findMessagesByUserName(userName);
        List<ChatResponse> dbResponses = dbChatDao.findMessagesByUserName(userName);

        if (cacheResponses != null) {
            dbChatDao.insertChatMessages(cacheResponses);
            cacheChatDao.deleteChatMessagesByUserName(userName);

            // strip out the username and expiration date field from the response
            for (ChatResponse response : cacheResponses) {
                response.setExpirationDate(null);
                response.setUsername(null);
            }
            dbResponses.addAll(cacheResponses);
        }
        return dbResponses.toArray(new ChatResponse[dbResponses.size()]);
    }
}
