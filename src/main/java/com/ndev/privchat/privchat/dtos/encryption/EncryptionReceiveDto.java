package com.ndev.privchat.privchat.dtos.encryption;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EncryptionReceiveDto {
    private String requesterNickname;
    private String requesterPublicKey;
}
