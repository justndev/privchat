package com.ndev.privchat.privchat.controllers;

import com.ndev.privchat.privchat.entities.Message;
import com.ndev.privchat.privchat.websocket.UserSessionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;

@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:8080",
        "http://16.171.154.134",
        "http://priv-chat.com"
})
@Controller
public class WsController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserSessionRegistry userSessionRegistry;

    @MessageMapping("/chat.sendMessage")
    public void sendMessageToAll(@Payload Message chatMessage) {
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendSpecific(@Payload Message msg, Principal user, @Header("simpSessionId") String sessionId) throws Exception {

        String session = this.userSessionRegistry.getUserSessions().get(msg.getReceiver());

        if (session !=null) {
            messagingTemplate.convertAndSend("/queue/specific-user-" + session, msg);
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload String username, Principal user, @Header("simpSessionId") String sessionId) {
        if (username == null) {
            System.out.println("Dropped connection");
            return;
        }
        Message msg = new Message();
        msg.setContent("Update");
        msg.setSender("System");

        this.userSessionRegistry.getUserSessions().put(username, sessionId);
        messagingTemplate.convertAndSend("/queue/specific-user-" + sessionId, msg);

    }
}