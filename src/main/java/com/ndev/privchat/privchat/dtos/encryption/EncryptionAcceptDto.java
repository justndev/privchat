package com.ndev.privchat.privchat.dtos.encryption;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EncryptionAcceptDto {
    private String requesterNickname;
    private String requestedPublicKey;
}
