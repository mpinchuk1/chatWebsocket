package com.company.listener;

import com.company.entities.ChatMessage;
import com.company.entities.UserState;
import com.company.repo.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
//        if (username != null) {
//            logger.info("User Connecting:" + username);
//            userService.findByLogin(username).setState(UserState.ONLINE);
//        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            logger.info("User Disconnected : " + username);
            userService.findByLogin(username).setState(UserState.OFFLINE);
//           ChatMessage chatMessage = new ChatMessage();
//           chatMessage.setType(ChatMessage.MessageType.LEAVE);
//          chatMessage.setSender(username);

            //messagingTemplate.convertAndSend("/topic/publicChatRoom", chatMessage);
        }
    }

}
