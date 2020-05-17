package com.company.controller;

import com.company.entities.ChatMessage;
import com.company.entities.ChatMessageDTO;
import com.company.entities.ChatRoom;
import com.company.entities.CustomUser;
import com.company.listener.WebSocketEventListener;
import com.company.repo.RoomRepository;
import com.company.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;

    @MessageMapping({"/chat/{to}"})
    public void sendMessage(@DestinationVariable String to, @RequestBody ChatMessageDTO messageDTO) {
        logger.info("getting message: " + messageDTO.getContent() + " from: " + messageDTO.getSender());

        if (roomRepository.existsByName(to)) {
            CustomUser sender = userRepository.findByLogin(messageDTO.getSender());
            ChatRoom toRoom = roomRepository.findByName(to);
            ChatMessage newMessage = new ChatMessage(sender, toRoom, messageDTO.getContent());
            simpMessagingTemplate.convertAndSend("/topic/messages/" + to, newMessage);
        }
    }

}