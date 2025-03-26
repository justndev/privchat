package com.ndev.privchat.privchat.controllers;

import com.ndev.privchat.privchat.data.RuntimeDataStore;
import com.ndev.privchat.privchat.dtos.ConfirmDestinationDto;
import com.ndev.privchat.privchat.dtos.MessageDTO;
import com.ndev.privchat.privchat.service.LoggingService;
import com.ndev.privchat.privchat.service.MessageService;
import com.ndev.privchat.privchat.service.SQLiteService;
import com.ndev.privchat.privchat.utilities.UtilityFunctions;
import com.ndev.privchat.privchat.websocket.WebSocketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
@RequestMapping("/chat")
public class MessageController {

    private final WebSocketService webSocketService;
    private final LoggingService loggingService;
    private final SQLiteService sqliteService;
    private final MessageService messageService;
    private final UtilityFunctions utilityFunctions;
    private final RuntimeDataStore runtimeDataStore;



    public MessageController(WebSocketService webSocketService, SQLiteService sqliteService, MessageService messageService, LoggingService loggingService, UtilityFunctions utilityFunctions, RuntimeDataStore runtimeDataStore) {
        this.webSocketService = webSocketService;
        this.sqliteService = sqliteService;
        this.messageService = messageService;
        this.loggingService = loggingService;
        this.utilityFunctions = utilityFunctions;
        this.runtimeDataStore = runtimeDataStore;
    }

    @PostMapping("/messages")
    public ResponseEntity sendMessage(HttpServletRequest rq, @RequestBody MessageDTO messageDto) throws Exception {
        String sender = messageService.extractNicknameFromRequest(rq);
        String ipAddress = rq.getRemoteAddr();
        if (!runtimeDataStore.processMessageLimit(ipAddress)) {
            return ResponseEntity.badRequest().body("Limit");
        };
//        boolean isRequestFine = utilityFunctions.isMessageDtoValid(sender, messageDto);
//        if (!isRequestFine) {
//            return ResponseEntity.badRequest().build();
//        }
        messageDto.setSender(sender);
        if (Objects.equals(messageDto.getType(), "delete-chat")) {

        }
//      ### Must be in service
        loggingService.log("Message | S: " + sender + "R: " + messageDto.getReceiver() + "T: " + messageDto.getType());
        sqliteService.addMessageTime(String.valueOf(System.currentTimeMillis()));
        webSocketService.sendSpecific(messageDto.getReceiver(), messageDto, "message");
        webSocketService.sendSpecific(sender, messageDto, "message");
//      ###
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm-reached")
    public ResponseEntity confirmReached(HttpSession  rq, @RequestBody ConfirmDestinationDto dto) throws Exception {
        dto.setType("reached");
        webSocketService.sendSpecific(dto.getReceiver(), dto, "reached");
        return ResponseEntity.ok().build();
    }
    @PostMapping("/confirm-watched")
    public ResponseEntity confirmWatched(HttpSession  rq,  @RequestBody ConfirmDestinationDto dto) throws Exception {
        dto.setType("watched");
        webSocketService.sendSpecific(dto.getReceiver(), dto, "watched");
        return ResponseEntity.ok().build();
    }
}
