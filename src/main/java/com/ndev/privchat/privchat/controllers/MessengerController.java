package com.ndev.privchat.privchat.controllers;

import com.ndev.privchat.privchat.dtos.ChatRequestDto;
import com.ndev.privchat.privchat.dtos.MessageDto;
import com.ndev.privchat.privchat.entities.Chat;
import com.ndev.privchat.privchat.entities.ChatRequest;
import com.ndev.privchat.privchat.entities.Message;
import com.ndev.privchat.privchat.entities.User;
import com.ndev.privchat.privchat.service.ChatRequestService;
import com.ndev.privchat.privchat.service.ChatService;
import com.ndev.privchat.privchat.service.MessageService;
import com.ndev.privchat.privchat.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messenger")
public class MessengerController {
    private final UserService userService;
    private final MessageService messageService;
    private final ChatRequestService chatRequestService;
    private final ChatService chatService;

    public MessengerController(UserService userService, MessageService messageService, ChatRequestService chatRequestService, ChatService chatService) {
        this.userService = userService;
        this.messageService = messageService;
        this.chatRequestService = chatRequestService;
        this.chatService = chatService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    // < ---------------------------- M E S S A G E S ---------------------------- >

    @GetMapping("/")
    public ResponseEntity<List<Message>> getUserReceivedMessages(HttpServletRequest rq) {
        String receiver = messageService.extractNicknameFromRequest(rq);
        List <Message> messages = messageService.getUserReceivedMessages(receiver);

        return ResponseEntity.ok(messages);
    }

    @PostMapping("/")
    public ResponseEntity sendMessage(HttpServletRequest rq, @RequestBody MessageDto messageDto) {
        String receiver = messageService.extractNicknameFromRequest(rq);
        Message message = this.messageService.createMessage(receiver, messageDto);

        return ResponseEntity.ok(message);
    }

    // < ---------------------------- C H A T  R E Q U E S T S ---------------------------- >

    @GetMapping("/requests")
    public ResponseEntity getRequests(HttpServletRequest rq) {
        String receiver = messageService.extractNicknameFromRequest(rq);
        List<ChatRequest> requests = this.chatRequestService.getChatRequestsForUser(receiver);

        return ResponseEntity.ok(requests);
    }

    @PostMapping("/requests")
    public ResponseEntity sendRequest(HttpServletRequest rq, @RequestBody ChatRequestDto chatRequestDto) {
        String receiver = messageService.extractNicknameFromRequest(rq);
        ChatRequest request = this.chatRequestService.createRequest(receiver, chatRequestDto);

        if (request == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(request);
    }

    @PostMapping("/requests-accept")
    public ResponseEntity acceptRequest(HttpServletRequest rq, @RequestBody ChatRequestDto chatRequestDto) {
        String receiver = messageService.extractNicknameFromRequest(rq);
        Boolean isAccepted = this.chatRequestService.tryAcceptRequest(receiver, chatRequestDto);
        if (isAccepted) {
            return ResponseEntity.ok(chatRequestDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/requests-deny")
    public ResponseEntity denyRequest(HttpServletRequest rq, @RequestBody ChatRequestDto chatRequestDto) {
        String receiver = messageService.extractNicknameFromRequest(rq);
        Boolean isDenied = this.chatRequestService.tryDenyRequest(receiver, chatRequestDto);
        if (isDenied) {
            return ResponseEntity.ok(chatRequestDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // < ---------------------------- C H A T S  ---------------------------- >

    @GetMapping("/chats")
    public ResponseEntity getChats(HttpServletRequest rq) {
        String nickname = messageService.extractNicknameFromRequest(rq);
        List<Chat> chats = this.chatService.getChats(nickname);

        return ResponseEntity.ok(chats);
    }
}