package com.ndev.privchat.privchat.repositories;

import com.ndev.privchat.privchat.entities.ChatRequest;
import com.ndev.privchat.privchat.entities.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRequestRepository extends CrudRepository<ChatRequest, UUID> {
    List<ChatRequest> findChatRequestByReceiver(String nickname);

    Optional<ChatRequest> findChatRequestByReceiverAndSender(String receiver, String sender);

}
