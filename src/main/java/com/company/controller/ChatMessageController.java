package com.company.controller;

import com.company.entities.ChatMessage;
import com.company.utils.ChatMessageDTO;
import com.company.entities.ChatRoom;
import com.company.entities.CustomUser;
import com.company.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class ChatMessageController {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageController.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private MessageService messageService;

    @MessageMapping({"/chat/{to}"})
    public void sendMessage(@DestinationVariable String to, @RequestBody ChatMessageDTO messageDTO) {
        logger.info("getting message: " + messageDTO.getContent() + " from: " + messageDTO.getSender());

        if (roomService.existsByName(to)) {
            CustomUser sender = userService.findByLogin(messageDTO.getSender());
            ChatRoom toRoom = roomService.findByName(to);
            ChatMessage newMessage = messageService.save(sender, toRoom, messageDTO.getContent());
            toRoom.addMessage(newMessage);
            simpMessagingTemplate.convertAndSend("/topic/messages/" + to, newMessage);
        }
    }

    @RequestMapping(value = "/getRoomMessages")
    public synchronized List<ChatMessage> getSingleRoomMessages(@RequestParam String userFrom, @RequestParam String userTo){
        ChatRoom room = roomService.findByName(userTo);
        List<ChatMessage> theirMessages;

        if(room.isSingleUserRoom()){
            CustomUser user1 = userService.findByLogin(userFrom);
            CustomUser user2 = userService.findByLogin(userTo);
            ChatRoom userRoom1 = user1.getUserPrivateRoom();
            ChatRoom userRoom2 = user2.getUserPrivateRoom();
            List<ChatMessage> user1Messages = userRoom1.getRoomMessages();      //all messages to user1 private room
            user1Messages = user1Messages.stream().filter(message -> message.getSender().equals(user2)).collect(Collectors.toList());
            List<ChatMessage> user2Messages = userRoom2.getRoomMessages();
            user2Messages = user2Messages.stream().filter(message -> message.getSender().equals(user1)).collect(Collectors.toList());
            theirMessages = user1Messages;
            theirMessages.addAll(user2Messages);
            theirMessages.sort(Comparator.comparing(ChatMessage::getDate));
        }else {
            theirMessages = room.getRoomMessages();
        }
        logger.info(String.valueOf(theirMessages));
        return theirMessages;
    }

}