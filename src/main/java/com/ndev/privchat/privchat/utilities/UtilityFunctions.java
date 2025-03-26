package com.ndev.privchat.privchat.utilities;

import com.ndev.privchat.privchat.dtos.EncryptionChatRequest;
import com.ndev.privchat.privchat.dtos.MessageDTO;
import com.ndev.privchat.privchat.dtos.UserDataDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Objects;


@Service
public class UtilityFunctions {
    final int MAX_PUBLIC_KEY_LENGTH = 500;
    final int MAX_FILENAME_LENGTH = 255;
    final int MAX_PARAM_LENGTH = 100;
    final int MAX_CONTENT_LENGTH = 5000;

    public boolean isMessageDtoValid(String sender, MessageDTO dto) {
        String content = dto.getContent();
        if (content == null || content.isEmpty()) {
            System.out.println("Validation failed: Content is null or empty.");
            return false;
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            System.out.println("Validation failed: Content exceeds maximum allowed length of " + MAX_CONTENT_LENGTH);
            return false;
        }

        if (dto.getExpiresAt() == null || dto.getExpiresAt().length() > MAX_PARAM_LENGTH) {
            System.out.println("Validation failed: Content exceeds maximum allowed length of " + MAX_CONTENT_LENGTH);
            return false;
        }

        if (!isParamValid(dto.getType()) ||
                !isParamValid(dto.getReceiver()) ||
                !isParamValid(dto.getCreatedAt()) ||
                !isParamValid(dto.getId())) {
            System.out.println("Validation failed: One or more parameters are invalid (null, empty, or too long).");
            return false;
        }

        if (Objects.equals(dto.getReceiver(), sender)) {
            System.out.println("Validation failed: Receiver matches the sender.");
            return false;
        }

        return true;
    }

    public boolean isChatRequestDtoValid(String requesterNickname, EncryptionChatRequest dto) {
        if (!isParamValid(requesterNickname) || !isParamValid(dto.getRequestedNickname())) {
            System.out.println("Validation failed: Mandatory fields are invalid.");
            return false;
        }

        String requestedPublicKey = dto.getRequestedPublicKey();
        if (requestedPublicKey != null && (requestedPublicKey.length() > MAX_PARAM_LENGTH || requestedPublicKey.isEmpty())) {
            System.out.println("Error: The requestedPublicKey field is invalid.");
            return false;
        }

        if (Objects.equals(dto.getRequestedNickname(), requesterNickname)) {
            System.out.println("Validation failed: Receiver matches the sender.");
            return false;
        }

        if (dto.getRequesterPublicKey() == null || dto.getRequesterPublicKey().isEmpty() || dto.getRequesterPublicKey().length() > MAX_PUBLIC_KEY_LENGTH) {
            System.out.println("Error: The requesterPublicKey field is invalid.");
            return false;
        }

        return true;
    }

    public boolean isParamValid(String str) {
        return str != null && !str.trim().isEmpty() && str.length() < MAX_PARAM_LENGTH;
    }

    public boolean isFileUploadInputValid(MultipartFile file, String sender, String receiver, String fileType, String id, String expiresAt) {
        if (Objects.equals(sender, receiver)) {
            System.out.println("Validation failed: Receiver matches the sender.");
            return false;
        }

        // Validate file
        if (file == null || file.isEmpty() || file.getOriginalFilename().length() > MAX_FILENAME_LENGTH) {
            System.out.println("Invalid file: either null, empty, or filename exceeds the limit.");
            return false;
        }

        // Validate other parameters
        if (!isParamValid(receiver) || !isParamValid(fileType) || !isParamValid(id)) {
            System.out.println("Invalid parameters: receiver, fileType, id, or expiresAt exceeds length limits or is empty.");
            return false;
        }

        if (expiresAt == null || expiresAt.length() > MAX_PARAM_LENGTH) {
            System.out.println("Invalid expiresAt: null, or exceeds length limit.");
            return false;
        }

        return true;
    }

    private boolean isFilenameValid(String filename) {
        if (filename == null || filename.isEmpty() || filename.length() > MAX_FILENAME_LENGTH) {
            System.out.println("Invalid filename: null, empty, or exceeds length limit.");
            return false;
        }

        return true;
    }

    public boolean isUserDataDtoValid(UserDataDto dto) {
        int MAX_USER_AGENT_LENGTH = 512;
        int MAX_PLATFORM_LENGTH = 256;
        int MAX_LANGUAGE_LENGTH = 10;
        int MAX_TIMEZONE_LENGTH = 100;

        if (!isParamValid(dto.getUserAgent()) || dto.getUserAgent().length() > MAX_USER_AGENT_LENGTH) {
            System.out.println("Invalid userAgent: null, empty, or exceeds length limit.");
            return false;
        }

        if (!isParamValid(dto.getPlatform()) || dto.getPlatform().length() > MAX_PLATFORM_LENGTH) {
            System.out.println("Invalid platform: null, empty, or exceeds length limit.");
            return false;
        }

        if (!isParamValid(dto.getScreenWidth())) {
            System.out.println("Invalid screenWidth: null, empty, or exceeds length limit.");
            return false;
        }

        if (!isParamValid(dto.getScreenHeight())) {
            System.out.println("Invalid screenHeight: null, empty, or exceeds length limit.");
            return false;
        }

        if (!isParamValid(dto.getLanguage()) || dto.getLanguage().length() > MAX_LANGUAGE_LENGTH) {
            System.out.println("Invalid language: null, empty, or exceeds length limit.");
            return false;
        }

        if (!isParamValid(dto.getTimezone()) || dto.getTimezone().length() > MAX_TIMEZONE_LENGTH) {
            System.out.println("Invalid timezone: null, empty, or exceeds length limit.");
            return false;
        }

        return true;
    }
}
