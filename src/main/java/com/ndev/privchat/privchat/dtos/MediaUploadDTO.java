package com.ndev.privchat.privchat.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadDTO {
    private String receiver;
    private String filename;
    private String id;
    private String expiresAt;
    private String fileType;
}
