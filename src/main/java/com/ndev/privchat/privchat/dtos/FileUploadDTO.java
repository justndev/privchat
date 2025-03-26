package com.ndev.privchat.privchat.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadDTO {
    private String receiver;
    private String filename;
    private String fileType;
    private String id;
    private String expiresAt;
    private String type;
    private String chatId;
}
