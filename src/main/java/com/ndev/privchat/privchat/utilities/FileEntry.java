package com.ndev.privchat.privchat.utilities;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FileEntry {

    private final String filename;
    private final String originalFilename;
    private final long acceptedAt;
    private final String size;
    private final String receiver;
    private final String sender;
    private final String fileType;
    @Nullable
    private final String expiresAt;
    @Nullable
    private final String id;
}