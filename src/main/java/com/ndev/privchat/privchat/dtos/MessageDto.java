package com.ndev.privchat.privchat.dtos;

import jakarta.annotation.Nullable;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private String content;
    private String receiver;
    @Nullable
    private String sender;
    @Nullable
    private Date createdAt;
    @Nullable
    private String id;
}
