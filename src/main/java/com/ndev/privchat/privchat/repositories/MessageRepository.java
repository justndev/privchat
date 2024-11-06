package com.ndev.privchat.privchat.repositories;

import com.ndev.privchat.privchat.entities.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends CrudRepository<Message, UUID> {
    Optional<Message> findMessageByUuid(UUID uuid);

    List<Message> findMessageByReceiver(String nickname);
    List<Message> findMessageBySender(String nickname);
    List<Message> findMessageBySenderAndReceiver(String receiver, String sender);
}
