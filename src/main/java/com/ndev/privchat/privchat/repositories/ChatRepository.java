package com.ndev.privchat.privchat.repositories;

import com.ndev.privchat.privchat.entities.Chat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends CrudRepository<Chat, UUID> {
    Optional<Chat> findChatBySenderAndReceiver(String sender, String receiver);
    List<Chat> findChatByReceiver(String receiver);
    List<Chat> findChatBySender(String sender);
}
