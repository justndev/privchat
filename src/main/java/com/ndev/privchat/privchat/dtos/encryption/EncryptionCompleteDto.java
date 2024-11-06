package com.ndev.privchat.privchat.dtos.encryption;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EncryptionCompleteDto {
    private String requestedNickname;
    private String requestedPublicKey;
}
