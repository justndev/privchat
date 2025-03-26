package com.ndev.privchat.privchat.dtos;

import jakarta.annotation.Nullable;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private String content;
    private String receiver;
    private String sender;
    private String createdAt;
    private String id;
    private String type;
    private String expiresAt;
    private String chatId;
}
