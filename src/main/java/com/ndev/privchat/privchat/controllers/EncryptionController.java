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
    public ResponseEntity createChatRequest(HttpServletRequest rq, @RequestBody EncryptionStartDto encryptionStartDto) {
        String requesterNickname = messageService.extractNicknameFromRequest(rq);
        String requestedNickname = encryptionStartDto.getRequestedNickname();
        String requesterPublicKey = encryptionStartDto.getRequesterPublicKey();

        if (Objects.equals(requestedNickname, requesterNickname)) {
            return ResponseEntity.badRequest().build();
        }

        UUID matchingRequestId = chatRequestsMap.entrySet().stream()
                .filter(entry -> entry.getValue().getRequesterNickname().equals(requesterNickname))
                .filter(entry -> entry.getValue().getRequestedNickname().equals(encryptionStartDto.getRequestedNickname()))
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
        
        System.out.println(encryptionChatRequest);


        chatRequestsMap.put(UUID.randomUUID(), encryptionChatRequest);
        return ResponseEntity.ok("");
    }

    @GetMapping("/receive")
    public ResponseEntity receiveChatRequests(HttpServletRequest rq) {
        String requestedNickname = messageService.extractNicknameFromRequest(rq);

        List<EncryptionChatRequest> matchingRequests = chatRequestsMap.values().stream()
                .filter(req -> req.getRequestedNickname().equals(requestedNickname))
                .filter(req -> req.getRequestedPublicKey() == null || req.getRequestedPublicKey().isEmpty())
                .toList();
        System.out.println("Sent back requests: " + matchingRequests.size() );

        return ResponseEntity.ok(matchingRequests);
    }

    @PostMapping("/accept")
    public ResponseEntity acceptChatRequest(HttpServletRequest rq, @RequestBody EncryptionAcceptDto encryptionAcceptDto) {
        String requestedNickname = messageService.extractNicknameFromRequest(rq);
        String requesterNickname = encryptionAcceptDto.getRequesterNickname();
        String requestedPublicKey = encryptionAcceptDto.getRequestedPublicKey();

        UUID matchingRequestId = chatRequestsMap.entrySet().stream()
                .filter(entry -> entry.getValue().getRequestedNickname().equals(requestedNickname))
                .filter(entry -> entry.getValue().getRequesterNickname().equals(requesterNickname))
                .filter(entry -> entry.getValue().getRequestedPublicKey() == null || entry.getValue().getRequestedPublicKey().isEmpty())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (matchingRequestId != null) {
            EncryptionChatRequest chatRequest = chatRequestsMap.get(matchingRequestId);

            chatRequest.setRequestedPublicKey(requestedPublicKey);
            chatRequestsMap.put(matchingRequestId, chatRequest);

            return ResponseEntity.ok("Public key updated for chat request with UUID: " + matchingRequestId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/receive-completed")
    public ResponseEntity completeChatRequests(HttpServletRequest rq) {
        String requestedNickname = messageService.extractNicknameFromRequest(rq);

        List<EncryptionChatRequest> matchingRequestsForRequested = chatRequestsMap.values().stream()
                .filter(req -> req.getRequestedNickname().equals(requestedNickname))
                .filter(req -> req.getRequestedPublicKey() != null && !req.getRequestedPublicKey().isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));

        List<EncryptionChatRequest> matchingRequestsForRequester = chatRequestsMap.values().stream()
                .filter(req -> req.getRequesterNickname().equals(requestedNickname))
                .filter(req -> req.getRequestedPublicKey() != null && !req.getRequestedPublicKey().isEmpty())
                .toList();

        matchingRequestsForRequested.addAll(matchingRequestsForRequester);

        return ResponseEntity.ok(matchingRequestsForRequested);
    }

    // < ---------------------------- M E S S A G E S ---------------------------- >

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(HttpServletRequest rq) {
        String receiver = messageService.extractNicknameFromRequest(rq);
        List <Message> messages = messageService.getMessages(receiver);

        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages-from")
    public ResponseEntity<List<Message>> getMessagesFromUser(HttpServletRequest rq, String nickname) {
        String receiver = messageService.extractNicknameFromRequest(rq);
        List <Message> messages = messageService.getMessagesFromUser(receiver, nickname);

        return ResponseEntity.ok(messages);
    }

    @PostMapping("/messages")
    public ResponseEntity sendMessage(HttpServletRequest rq, @RequestBody MessageDto messageDto) {
        String receiver = messageService.extractNicknameFromRequest(rq);
        Message message = this.messageService.createMessage(receiver, messageDto);

        return ResponseEntity.ok(message);
    }
}
