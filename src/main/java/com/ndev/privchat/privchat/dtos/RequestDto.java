package com.ndev.privchat.privchat.dtos;

import jakarta.annotation.Nullable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDto {
    private String requestedNickname;
    @Nullable
    private String type;
    @Nullable String chatId;

}
