package com.ndev.privchat.privchat.dtos.encryption;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EncryptionStartDto {
    private String requestedNickname;
    private String requesterPublicKey;
}
