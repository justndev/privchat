package com.ndev.privchat.privchat.service;

import com.ndev.privchat.privchat.dtos.ChatRequestDto;
import com.ndev.privchat.privchat.dtos.MessageDto;
import com.ndev.privchat.privchat.entities.ChatRequest;
import com.ndev.privchat.privchat.entities.Message;
import com.ndev.privchat.privchat.repositories.ChatRequestRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatRequestService {
    private final ChatRequestRepository chatRequestRepository;

    @Autowired
    private final MessageService messageService;
    @Autowired
    private final ChatService chatService;

    public ChatRequestService(ChatRequestRepository chatRequestRepository, MessageService messageService, ChatService chatService) {
        this.chatRequestRepository = chatRequestRepository;
        this.messageService = messageService;
        this.chatService = chatService;
    }

    public List<ChatRequest> getChatRequestsForUser(String receiver) {
        return this.chatRequestRepository.findChatRequestByReceiver(receiver);
    }

    public ChatRequest createRequest(String sender, ChatRequestDto chatRequestDto) {
        Optional<ChatRequest> request = this.chatRequestRepository.findChatRequestByReceiverAndSender(chatRequestDto.getReceiver(), sender);
        if (request.isPresent()) {
            return null;
        }
        ChatRequest chatRequest = new ChatRequest();

        chatRequest.setUuid(UUID.randomUUID());
        chatRequest.setSender(sender);
        chatRequest.setReceiver(chatRequestDto.getReceiver());
        return this.chatRequestRepository.save(chatRequest);
    }

    public Boolean tryAcceptRequest(String receiver, ChatRequestDto chatRequestDto) {
        // Here chatRequestDto.getReceiver() means sender
        Optional<ChatRequest> request = this.chatRequestRepository.findChatRequestByReceiverAndSender(receiver, chatRequestDto.getReceiver());
        if (request.isEmpty()) {
            return false;
        }
        this.chatRequestRepository.delete(request.get());
        this.chatService.createChat(receiver, chatRequestDto.getReceiver());
        return true;
    }

    public Boolean tryDenyRequest(String receiver,  ChatRequestDto chatRequestDto) {
        Optional<ChatRequest> request = this.chatRequestRepository.findChatRequestByReceiverAndSender(receiver, chatRequestDto.getReceiver());
        if (request.isEmpty()) {
            return false;
        }
        this.chatRequestRepository.delete(request.get());
        return true;
    }
}
