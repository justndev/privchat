package com.ndev.privchat.privchat.controllers;
import com.ndev.privchat.privchat.data.RuntimeDataStore;
import com.ndev.privchat.privchat.dtos.EncryptionChatRequest;
import com.ndev.privchat.privchat.dtos.RequestDto;
import com.ndev.privchat.privchat.service.LoggingService;
import com.ndev.privchat.privchat.service.MessageService;
import com.ndev.privchat.privchat.utilities.UtilityFunctions;
import com.ndev.privchat.privchat.websocket.WebSocketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
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

    private final LoggingService loggingService;



    private final RuntimeDataStore runtimeDataStore;

    private final UtilityFunctions utilityFunctions;

    public EncryptionController(MessageService messageService, WebSocketService webSocketService, LoggingService loggingService, RuntimeDataStore runtimeDataStore, UtilityFunctions utilityFunctions) {
        this.messageService = messageService;
        this.webSocketService = webSocketService;
        this.loggingService = loggingService;
        this.runtimeDataStore = runtimeDataStore;
        this.utilityFunctions = utilityFunctions;
    }

    @PostMapping("/create")
    public ResponseEntity createChatRequest(HttpServletRequest rq, @RequestBody EncryptionChatRequest chatRequest) throws Exception {
        String ipAddress = rq.getRemoteAddr();
//        if (!runtimeDataStore.processChatLimit(ipAddress)) {
//            return ResponseEntity.badRequest().body("Limit");
//        };

        String requesterNickname = messageService.extractNicknameFromRequest(rq);
        String requestedNickname = chatRequest.getRequestedNickname();
        String requesterPublicKey = chatRequest.getRequesterPublicKey();
        String chatId = UUID.randomUUID().toString();

        // TODO: Add validation for the chatId
        boolean isChatRequestFine = utilityFunctions.isChatRequestDtoValid(requesterNickname, chatRequest);
        if (!isChatRequestFine) {
            return ResponseEntity.badRequest().build();
        }

        loggingService.log("Request | S: " + requesterNickname + "R: " + requestedNickname);

        UUID matchingRequestId = runtimeDataStore.getDataMap().entrySet().stream()
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
        encryptionChatRequest.setChatId(chatId);
        RequestDto dto = new RequestDto();
        dto.setRequestedNickname(requestedNickname);
        dto.setChatId(chatRequest.getChatId());
        dto.setType("request");
        webSocketService.sendSpecific(requestedNickname, dto, "request");
        runtimeDataStore.putRequest(UUID.randomUUID(), encryptionChatRequest);
        return ResponseEntity.ok("");
    }

    @PostMapping("/process")
    public ResponseEntity processChatRequests(HttpServletRequest rq, @RequestBody String requestedPublicKey) throws Exception {
        String requestedNickname = messageService.extractNicknameFromRequest(rq);

        if (requestedNickname == null || requestedNickname.isEmpty() || requestedNickname.length() > 50) {
            return ResponseEntity.badRequest().body("Wrong public key provided");
        }

//      ### Must be in a service
        List<UUID> pendingRequestIds = runtimeDataStore.getDataMap().entrySet().stream()
                .filter(entry -> entry.getValue().getRequestedNickname().equals(requestedNickname))
                .filter(entry -> entry.getValue().getRequestedPublicKey() == null || entry.getValue().getRequestedPublicKey().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!pendingRequestIds.isEmpty()) {
            for (UUID requestId : pendingRequestIds) {
                EncryptionChatRequest pendingRequest = runtimeDataStore.getRequest(requestId);
                pendingRequest.setType("request");
                if (pendingRequest != null) {
                    pendingRequest.setRequestedPublicKey(requestedPublicKey);
                    runtimeDataStore.putRequest(requestId, pendingRequest);
                    webSocketService.sendSpecific(pendingRequest.getRequesterNickname(), pendingRequest, "request");
                }
            }
        }

        List<EncryptionChatRequest> matchingRequests = runtimeDataStore.getDataMap().values().stream()
                .filter(req -> req.getRequestedNickname().equals(requestedNickname) || req.getRequesterNickname().equals(requestedNickname))
                .filter(req -> req.getRequestedPublicKey() != null && !req.getRequestedPublicKey().isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
//      ###
        return ResponseEntity.ok(matchingRequests);
    }
}