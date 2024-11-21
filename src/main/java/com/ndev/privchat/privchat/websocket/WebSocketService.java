package com.ndev.privchat.privchat.websocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {


    @Autowired
    private UserSessionRegistry userSessionRegistry;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public <T> void sendSpecific(String userId, String update) throws Exception {
        String sessionId = this.userSessionRegistry.getUserSessions().get(userId);
        SocketResponse<T> response = new SocketResponse<>();
        response.setUpdate(update);

        if (sessionId != null) {
            messagingTemplate.convertAndSend("/queue/specific-user-" + sessionId, response);
        }
    }
}
