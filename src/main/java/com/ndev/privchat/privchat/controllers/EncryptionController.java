package com.ndev.privchat.privchat.controllers;
import com.ndev.privchat.privchat.dtos.MessageDto;
import com.ndev.privchat.privchat.dtos.encryption.*;
import com.ndev.privchat.privchat.entities.Message;
import com.ndev.privchat.privchat.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    private final Map<UUID, EncryptionChatRequest> chatRequestsMap = new ConcurrentHashMap<>();

    public EncryptionController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/create")
    public ResponseEntity createChatRequest(HttpServletRequest rq, @RequestBody EncryptionChatRequest chatRequest) {
        String requesterNickname = messageService.extractNicknameFromRequest(rq);
        String requestedNickname = chatRequest.getRequestedNickname();
        String requesterPublicKey = chatRequest.getRequesterPublicKey();

        Boolean isNicknameDuplicated = Objects.equals(requestedNickname, requesterNickname);
        Boolean areEmptyFields = requestedNickname == null || requesterPublicKey == null;

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

        chatRequestsMap.put(UUID.randomUUID(), encryptionChatRequest);
        return ResponseEntity.ok("");
    }

    @PostMapping("/process")
    public ResponseEntity processChatRequests(HttpServletRequest rq, @RequestBody String requestedPublicKey) {
        System.out.println("Triggered");

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
                    System.out.println("Put public key!");
                    pendingRequest.setRequestedPublicKey(requestedPublicKey);
                    chatRequestsMap.put(requestId, pendingRequest);
                }
            }
        }

        List<EncryptionChatRequest> matchingRequests = chatRequestsMap.values().stream()
                .filter(req -> req.getRequestedNickname().equals(requestedNickname) || req.getRequesterNickname().equals(requestedNickname))
                .filter(req -> req.getRequestedPublicKey() != null && !req.getRequestedPublicKey().isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));



        System.out.println(chatRequestsMap);
        System.out.println(matchingRequests);
        
        return ResponseEntity.ok(matchingRequests);
    }


    // < ---------------------------- M E S S A G E S ---------------------------- >

    // NOTE: What if receiver is null?
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(HttpServletRequest rq) {
        String receiver = messageService.extractNicknameFromRequest(rq);
        List <Message> messages = messageService.getMessages(receiver);

        return ResponseEntity.ok(messages);
    }

    // NOTE: What if receiver is null?
    @GetMapping("/messages-from")
    public ResponseEntity<List<Message>> getMessagesFromUser(HttpServletRequest rq, String nickname) {
        String receiver = messageService.extractNicknameFromRequest(rq);

        if (nickname.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List <Message> messages = messageService.getMessagesFromUser(receiver, nickname);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/messages")
    public ResponseEntity sendMessage(HttpServletRequest rq, @RequestBody MessageDto messageDto) {
        String receiver = messageService.extractNicknameFromRequest(rq);

        boolean areNicknameDuplicated = receiver == messageDto.getReceiver();
        boolean areEmptyFields = messageDto.getContent().isEmpty() ||
                messageDto.getReceiver().isEmpty();

        if (areEmptyFields || areNicknameDuplicated) {
            return ResponseEntity.badRequest().build();
        }

        Message message = this.messageService.createMessage(receiver, messageDto);
        return ResponseEntity.ok(message);
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

