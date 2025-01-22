package com.ndev.privchat.privchat.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConfirmDestinationDto {
    private String messageId;
    private String receiver;
}
