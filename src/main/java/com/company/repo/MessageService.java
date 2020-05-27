package com.company.repo;

import com.company.entities.ChatMessage;
import com.company.entities.ChatRoom;
import com.company.entities.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Transactional
    public ChatMessage save(CustomUser sender, ChatRoom toRoom, String content) {
        ChatMessage newMessage = new ChatMessage(sender, toRoom, content);
        toRoom.addMessage(newMessage);
        messageRepository.save(newMessage);
        return newMessage;
    }
}
