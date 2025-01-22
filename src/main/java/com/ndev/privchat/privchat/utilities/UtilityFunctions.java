package com.ndev.privchat.privchat.utilities;

import com.ndev.privchat.privchat.dtos.EncryptionChatRequest;
import com.ndev.privchat.privchat.dtos.MessageDTO;
import com.ndev.privchat.privchat.dtos.UserDataDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
public class UtilityFunctions {
    final int MAX_PUBLIC_KEY_LENGTH = 500;
    final int MAX_FILENAME_LENGTH = 255;
    final int MAX_PARAM_LENGTH = 50;
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

        List<String> params = List.of(
                dto.getType() != null ? dto.getType() : "",
                dto.getReceiver() != null ? dto.getReceiver() : "",
                dto.getCreatedAt() != null ? dto.getCreatedAt() : "",
                dto.getId() != null ? dto.getId() : "",
                dto.getExpiresAt() != null ? dto.getExpiresAt() : ""
        );
        if (!checkParams(params, MAX_PARAM_LENGTH)) {
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
        String requestedNickname = dto.getRequestedNickname();
        String requestedPublicKey = dto.getRequestedPublicKey();
        String requesterPublicKey = dto.getRequesterPublicKey();

        List<String> mandatoryFields = List.of(requesterNickname, requestedNickname);
        // Check mandatory fields
        boolean areMandatoryFieldsFine = checkParams(mandatoryFields, MAX_PARAM_LENGTH);

        // Check optional field if it's not null
        boolean isOptionalFieldFine = requestedPublicKey == null ||
                (requestedPublicKey.length() <= MAX_PARAM_LENGTH && !requestedPublicKey.isEmpty());

        if (Objects.equals(requestedNickname, requesterNickname)) {
            System.out.println("Validation failed: Receiver matches the sender.");
            return false;
        }

        if (!areMandatoryFieldsFine) {
            System.out.println("Error: One or more mandatory fields are invalid.");
        }

        if (!isOptionalFieldFine) {
            System.out.println("Error: The requestedPublicKey field is invalid.");
        }

        if (dto.getRequesterPublicKey() == null || dto.getRequesterPublicKey().isEmpty() || dto.getRequesterPublicKey().length() > MAX_PUBLIC_KEY_LENGTH) {
            System.out.println("Error: The requesterPublicKey field is invalid.");
            return false;
        }

        return areMandatoryFieldsFine && isOptionalFieldFine;
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
        List<String> params = List.of(receiver, fileType, id, expiresAt);
        if (!checkParams(params, MAX_PARAM_LENGTH)) {
            System.out.println("Invalid parameters: receiver, fileType, id, or expiresAt exceeds length limits or is empty.");
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

        // Validate userAgent
        if (dto.getUserAgent() == null || dto.getUserAgent().isEmpty() || dto.getUserAgent().length() > MAX_USER_AGENT_LENGTH) {
            System.out.println("Invalid userAgent: null, empty, or exceeds length limit.");
            return false;
        }

        // Validate platform
        if (dto.getPlatform() == null || dto.getPlatform().isEmpty() || dto.getPlatform().length() > MAX_PLATFORM_LENGTH) {
            System.out.println("Invalid platform: null, empty, or exceeds length limit.");
            return false;
        }

        // Validate screenWidth
        if (dto.getScreenWidth() == null || dto.getScreenWidth().length() > MAX_PARAM_LENGTH) {
            System.out.println("Invalid screen dimensions: screenWidth or screenHeight is zero or negative.");
            return false;
        }

        // Validate screenHeight
        if (dto.getScreenHeight() == null || dto.getScreenHeight().length() > MAX_PARAM_LENGTH) {
            System.out.println("Invalid screen dimensions: screenWidth or screenHeight is zero or negative.");
            return false;
        }

        // Validate language
        if (dto.getLanguage() == null || dto.getLanguage().isEmpty() || dto.getLanguage().length() > MAX_LANGUAGE_LENGTH) {
            System.out.println("Invalid language: null, empty, or exceeds length limit.");
            return false;
        }

        // Validate timezone
        if (dto.getTimezone() == null || dto.getTimezone().isEmpty() || dto.getTimezone().length() > MAX_TIMEZONE_LENGTH) {
            System.out.println("Invalid timezone: null, empty, or exceeds length limit.");
            return false;
        }
        return true;
    }

    private boolean checkParams(List<String> list, int maxLength) {
        for (String param : list) {
            if (param == null) {
                System.out.println("Validation failed: A parameter is null.");
                return false;
            }
            if (param.isEmpty()) {
                System.out.println("Validation failed: A parameter is empty.");
                return false;
            }
            if (param.length() > maxLength) {
                System.out.println("Validation failed: A parameter exceeds maximum allowed length of " + maxLength);
                return false;
            }
        }
        return true;
    }
}
