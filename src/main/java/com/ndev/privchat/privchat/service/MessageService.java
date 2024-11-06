package com.ndev.privchat.privchat.service;


import com.ndev.privchat.privchat.configs.JwtAuthenticationFilter;
import com.ndev.privchat.privchat.dtos.MessageDto;
import com.ndev.privchat.privchat.entities.Message;
import com.ndev.privchat.privchat.repositories.MessageRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ChatService chatService;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message createMessage(String sender, MessageDto messageDto) {

        Message message = new Message();

        message.setUuid(UUID.randomUUID());
        message.setSender(sender);
        message.setReceiver(messageDto.getReceiver());
        message.setContent(messageDto.getContent());
        return this.messageRepository.save(message);
    }

    public List<Message> getMessages(String receiver) {
        List<Message> msgsReceived = this.messageRepository.findMessageByReceiver(receiver);
        List<Message> msgsSent = this.messageRepository.findMessageBySender(receiver);
        msgsSent.addAll(msgsReceived);
        return msgsSent;
    }

    public List<Message> getMessagesFromUser(String receiver, String sender) {
        List<Message> list1 = this.messageRepository.findMessageBySenderAndReceiver(receiver, sender);
        List<Message> list2 = this.messageRepository.findMessageBySenderAndReceiver(sender, receiver);
        list1.addAll(list2);

        return list1;
    }


    public String extractNicknameFromRequest(HttpServletRequest rq) {
        String jwt = jwtService.parseJwt(rq);
        return jwtService.extractUsername(jwt);
    }
}