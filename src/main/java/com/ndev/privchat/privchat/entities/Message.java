package com.ndev.privchat.privchat.entities;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private UUID uuid;
    private String content;
    private Date createdAt;
    private String sender;
    private String receiver;
}
