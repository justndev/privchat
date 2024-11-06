package com.ndev.privchat.privchat.dtos.encryption;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EncryptionChatRequest {
    private String requesterNickname;
    private String requestedNickname;
    private String requestedPublicKey;
    private String requesterPublicKey;
}


