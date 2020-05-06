package com.company.controller;

import com.company.entities.ChatMessage;
import com.company.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private UserRepository repository;

    @MessageMapping("/chat/{to}")
    public void sendMessage(@DestinationVariable String to, ChatMessage message) {
        System.out.println("handling send message: " + message + " to: " + to);

        if (repository.existsByLogin(to)) {
            simpMessagingTemplate.convertAndSend("/topic/messages/" + to, message);
        }
    }

}