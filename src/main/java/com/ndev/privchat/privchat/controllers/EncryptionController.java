package com.ndev.privchat.privchat.controllers;
import com.ndev.privchat.privchat.dtos.EncryptionChatRequest;
import com.ndev.privchat.privchat.dtos.MessageDto;
import com.ndev.privchat.privchat.service.MessageService;
import com.ndev.privchat.privchat.websocket.WebSocketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/encrypt-chat")
public class EncryptionController {
    private final MessageService messageService;

    private final WebSocketService webSocketService;

    private final Map<UUID, EncryptionChatRequest> chatRequestsMap = new ConcurrentHashMap<>();

    public EncryptionController(MessageService messageService, WebSocketService webSocketService) {
        this.messageService = messageService;
        this.webSocketService = webSocketService;
    }

    @PostMapping("/messages")
    public ResponseEntity sendMessage(HttpServletRequest rq, @RequestBody MessageDto messageDto) throws Exception {
        String sender = messageService.extractNicknameFromRequest(rq);

        boolean areNicknameDuplicated = Objects.equals(sender, messageDto.getReceiver());
        boolean areEmptyFields = messageDto.getContent().isEmpty() ||
                messageDto.getReceiver().isEmpty();

        if (areEmptyFields || areNicknameDuplicated) {
            return ResponseEntity.badRequest().build();
        }
        messageDto.setSender(sender);

        webSocketService.sendSpecific(messageDto.getReceiver(), messageDto, "message");
        webSocketService.sendSpecific(sender, messageDto, "message");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity createChatRequest(HttpServletRequest rq, @RequestBody EncryptionChatRequest chatRequest) throws Exception {
        String requesterNickname = messageService.extractNicknameFromRequest(rq);
        String requestedNickname = chatRequest.getRequestedNickname();
        String requesterPublicKey = chatRequest.getRequesterPublicKey();

        if (!isValidEncryptionChatRequestStart(chatRequest) || Objects.equals(chatRequest.getRequestedNickname(), requesterNickname)) {
            return ResponseEntity.badRequest().build();
        }

        UUID matchingRequestId = chatRequestsMap.entrySet().stream()
                .filter(entry -> entry.getValue().getRequesterNickname().equals(requesterNickname))
                .filter(entry -> entry.getValue().getRequestedNickname().equals(chatRequest.getRequestedNickname()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        if (matchingRequestId != null) {
            return ResponseEntity.status(409).build();
        }

        EncryptionChatRequest encryptionChatRequest = new EncryptionChatRequest();
        encryptionChatRequest.setRequestedNickname(requestedNickname);
        encryptionChatRequest.setRequesterNickname(requesterNickname);
        encryptionChatRequest.setRequesterPublicKey(requesterPublicKey);
        webSocketService.sendSpecific(requestedNickname, "request", "request");
        chatRequestsMap.put(UUID.randomUUID(), encryptionChatRequest);
        return ResponseEntity.ok("");
    }

    @PostMapping("/process")
    public ResponseEntity processChatRequests(HttpServletRequest rq, @RequestBody String requestedPublicKey) throws Exception {
        String requestedNickname = messageService.extractNicknameFromRequest(rq);

        if (!isNonEmptyString(requestedPublicKey)) {
            return ResponseEntity.badRequest().body("Public key is required to accept requests.");
        }

        List<UUID> pendingRequestIds = chatRequestsMap.entrySet().stream()
                .filter(entry -> entry.getValue().getRequestedNickname().equals(requestedNickname))
                .filter(entry -> entry.getValue().getRequestedPublicKey() == null || entry.getValue().getRequestedPublicKey().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!pendingRequestIds.isEmpty()) {
            for (UUID requestId : pendingRequestIds) {
                EncryptionChatRequest pendingRequest = chatRequestsMap.get(requestId);
                if (pendingRequest != null) {
                    pendingRequest.setRequestedPublicKey(requestedPublicKey);
                    chatRequestsMap.put(requestId, pendingRequest);
                    webSocketService.sendSpecific(pendingRequest.getRequesterNickname(), "request", "request");
                }
            }
        }

        List<EncryptionChatRequest> matchingRequests = chatRequestsMap.values().stream()
                .filter(req -> req.getRequestedNickname().equals(requestedNickname) || req.getRequesterNickname().equals(requestedNickname))
                .filter(req -> req.getRequestedPublicKey() != null && !req.getRequestedPublicKey().isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));

        return ResponseEntity.ok(matchingRequests);
    }

    private boolean isValidEncryptionChatRequestStart(EncryptionChatRequest request) {
        return isNonEmptyString(request.getRequestedNickname())
                && isNonEmptyString(request.getRequesterPublicKey());
    }

    private boolean isValidEncryptionChatRequestAccept(EncryptionChatRequest request) {
        return isNonEmptyString(request.getRequesterNickname())
                && isNonEmptyString(request.getRequestedPublicKey());
    }

    private boolean isNonEmptyString(String str) {
        return str != null && !str.trim().isEmpty();
    }
}

