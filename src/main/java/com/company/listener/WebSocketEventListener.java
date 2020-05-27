package com.company.listener;

import com.company.utils.ChatMessageDTO;
import com.company.utils.UserState;
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

@Component
public class WebSocketEventListener {

    private static final String JOINMESSAGE = "JOIN";
    private static final String LEAVEMESSAGE = "LEAVE";
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        ChatMessageDTO joinMessage = new ChatMessageDTO("server", JOINMESSAGE);
        messagingTemplate.convertAndSend("/topic/roomOnlineListener", joinMessage);

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            logger.info("User Disconnected : " + username);
            userService.findByLogin(username).setState(UserState.OFFLINE);
            ChatMessageDTO leaveMessage = new ChatMessageDTO("server", LEAVEMESSAGE);
            messagingTemplate.convertAndSend("/topic/roomOnlineListener", leaveMessage);
        }
    }

}
