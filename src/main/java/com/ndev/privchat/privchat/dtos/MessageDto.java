package com.ndev.privchat.privchat.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private String content;
    private String receiver;
}
