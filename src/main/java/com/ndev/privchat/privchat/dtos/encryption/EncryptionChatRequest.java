package com.ndev.privchat.privchat.dtos.encryption;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EncryptionChatRequest {
    @Nullable
    private String requesterNickname;
    @Nullable
    private String requestedNickname;
    @Nullable
    private String requestedPublicKey;
    @Nullable
    private String requesterPublicKey;
}


