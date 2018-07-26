package com.sherwin.fitnessPal.messaging.dao.db;

import com.sherwin.fitnessPal.messaging.dao.ChatDao;
import com.sherwin.fitnessPal.messaging.domain.ChatInput;
import com.sherwin.fitnessPal.messaging.domain.ChatResponse;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;

@Mapper
public interface DbChatDao extends ChatDao {
    @Override
    @Insert("INSERT INTO message (id, username, text, expirationDate) VALUES (#{id}, #{username}, #{text}, #{expirationDate})")
    long insertChatMessage(ChatInput message);

    @Override
    @Select("SELECT username, text, expirationDate FROM message WHERE id = #{id}")
    ChatResponse findMessageById(Long id);

    @Override
    @Select("SELECT id, text FROM message WHERE username = #{username}")
    List<ChatResponse> findMessagesByUserName(String username);

    @Override
    @Insert({
            "<script>",
            "INSERT INTO message",
            "(id, username, text, expirationDate)",
            "VALUES" +
                    "<foreach collection='cacheResponses' item='cacheResponse' open='' separator=',' close=''>" +
                    "(" +
                    "#{cacheResponse.id},",
            "#{cacheResponse.username},",
            "#{cacheResponse.text},",
            "#{cacheResponse.expirationDate}" +
                    ")" +
                    "</foreach>",
            "</script>"})
    void insertChatMessages(@Param("cacheResponses") List<ChatResponse> cacheResponses);

    @Override
    @Delete("DELETE FROM message where username = #{username}")
    void deleteChatMessagesByUserName(String username);

    @Override
    @Select("SELECT id, username, text, expirationDate FROM message")
    Collection<ChatInput> findAllMessages();
}