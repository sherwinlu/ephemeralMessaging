package com.sherwin.fitnessPal.messaging.controller;

import com.sherwin.fitnessPal.messaging.domain.ChatInput;
import com.sherwin.fitnessPal.messaging.domain.ChatResponse;
import com.sherwin.fitnessPal.messaging.service.ChatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatController {
    @Autowired
    private ChatServiceImpl chatService;

    @RequestMapping(method = RequestMethod.POST, path = "/chat")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    private ChatResponse createMessage(@RequestBody ChatInput message) {
        return chatService.create(message);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/chat/{id}")
    private ChatResponse getMessageById(@PathVariable Long id) {
        return chatService.retrieveMessageById(id);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/chats/{username}")
    private ChatResponse[] getMessageByUserName(@PathVariable String username) {
        return chatService.retrieveMessagesByUserName(username);
    }
}
