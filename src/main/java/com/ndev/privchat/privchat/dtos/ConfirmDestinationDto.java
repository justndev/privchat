package com.ndev.privchat.privchat.dtos;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConfirmDestinationDto {
    private String messageId;
    private String receiver;
    @Nullable
    private String chatId;
    @Nullable
    private String type;
}
