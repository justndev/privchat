package com.ndev.privchat.privchat.service;

import com.ndev.privchat.privchat.entities.Chat;
import com.ndev.privchat.privchat.entities.Message;
import com.ndev.privchat.privchat.repositories.ChatRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat createChat(String sender, String receiver) {
        Optional<Chat> existingChat = this.chatRepository.findChatBySenderAndReceiver(sender, receiver);
        if (existingChat.isPresent()) {
            return existingChat.get();
        }
        Chat chat = new Chat();

        chat.setUuid(UUID.randomUUID());
        chat.setSender(sender);
        chat.setReceiver(receiver);
        return this.chatRepository.save(chat);
    }

    public List<Chat> getChats(String nickname) {
        List<Chat> chatsCreatedByThisUser = this.chatRepository.findChatByReceiver(nickname);
        List<Chat> chatsCreatedByAnotherUser = this.chatRepository.findChatBySender(nickname);
        List<Chat> allChats = new ArrayList<>();
        allChats.addAll(chatsCreatedByAnotherUser);
        allChats.addAll(chatsCreatedByThisUser);

        return allChats;
    }

    // TODO: Find better solution
    public Boolean checkIfChatExists(String userOne, String userTwo) {
        Optional<Chat> existingChat1 = this.chatRepository.findChatBySenderAndReceiver(userOne, userTwo);
        Optional<Chat> existingChat2 = this.chatRepository.findChatBySenderAndReceiver(userOne, userTwo);

        return existingChat2.isPresent() || existingChat1.isPresent();
    }
}
