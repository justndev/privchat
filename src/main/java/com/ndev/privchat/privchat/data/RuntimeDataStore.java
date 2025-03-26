package com.ndev.privchat.privchat.data;

import com.ndev.privchat.privchat.dtos.EncryptionChatRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuntimeDataStore {
    private final Map<UUID, EncryptionChatRequest> chatRequestsMap = new ConcurrentHashMap<>();
    private final Map<String, Limitations> limitsMap = new ConcurrentHashMap<>();

    public Map<UUID, EncryptionChatRequest> getDataMap() {
        return chatRequestsMap;
    }

    public Map<String, Limitations> getLimitsMap() {
        return limitsMap;
    }

    private void checkIfLimitsExist(String key) {
        boolean exists = getLimitsMap().containsKey(key);
        if (!exists) {
            getLimitsMap().put(key, new Limitations());
        }
    }

    public boolean processMessageLimit(String key) {
        this.checkIfLimitsExist(key);
        Limitations prevLimitations = getLimitsMap().get(key);
        System.out.println("@processMessageLimit");
        System.out.println(prevLimitations);
        boolean isOverReached = prevLimitations.isMessageLimitExceeded();
        limitsMap.put(key, prevLimitations);
        return isOverReached;
    }

    public boolean processChatLimit(String key) {
        this.checkIfLimitsExist(key);
        Limitations prevLimitations = getLimitsMap().get(key);
        System.out.println("@processChatLimit");
        System.out.println(prevLimitations);
        boolean isOverReached = prevLimitations.isChatLimitExceeded();
        limitsMap.put(key, prevLimitations);
        return isOverReached;
    }

    public boolean processFileLimit(String key) {
        this.checkIfLimitsExist(key);
        Limitations prevLimitations = getLimitsMap().get(key);
        System.out.println("@processFileLimit");
        System.out.println(prevLimitations);
        boolean isOverReached = prevLimitations.isFileLimitExceeded();
        limitsMap.put(key, prevLimitations);
        return isOverReached;
    }

    public boolean processMediaLimit(String key) {
        this.checkIfLimitsExist(key);
        Limitations prevLimitations = getLimitsMap().get(key);
        System.out.println("@processMediaLimit");
        System.out.println(prevLimitations);
        boolean isOverReached = prevLimitations.isMediaLimitExceeded();
        limitsMap.put(key, prevLimitations);
        return isOverReached;
    }

    public void putRequest(UUID key, EncryptionChatRequest value) {
        chatRequestsMap.put(key, value);
    }

    public EncryptionChatRequest getRequest(UUID key) {
        return chatRequestsMap.get(key);
    }

    public void removeRequest(UUID key) {
        chatRequestsMap.remove(key);
    }
}

@Getter
@Setter
@ToString
class Limitations {
    private int messageCount = 0;
    private int chatCount = 0;
    private int fileCount = 0;
    private int mediaCount = 0;

    private final int MESSAGE_LIMIT = 50;
    private final int CHAT_LIMIT = 10;
    private final int FILE_LIMIT = 1;
    private final int MEDIA_LIMIT = 5;

    public boolean isMessageLimitExceeded() {
        if (messageCount >= MESSAGE_LIMIT) {
            return false;
        }
        messageCount++;
        return true;
    }
    public boolean isChatLimitExceeded() {
        if (chatCount >= CHAT_LIMIT) {
            return false;
        }
        chatCount++;
        return true;

    }
    public boolean isFileLimitExceeded() {
        if (fileCount >= FILE_LIMIT) {
            return false;
        }
        fileCount++;
        return true;
    }
    public boolean isMediaLimitExceeded() {
        if (mediaCount >= MEDIA_LIMIT) {
            return false;
        }
        mediaCount++;
        return true;
    }
}
