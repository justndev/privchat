package com.ndev.privchat.privchat.dtos;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EncryptionChatRequest {
    private String requesterNickname;
    private String requestedNickname;
    @Nullable
    private String requestedPublicKey;
    private String requesterPublicKey;
}


