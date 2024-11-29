package com.ndev.privchat.privchat.dtos;

import jakarta.annotation.Nullable;
import lombok.*;

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
}
