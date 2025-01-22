package com.ndev.privchat.privchat.service;

import com.ndev.privchat.privchat.utilities.FileEntry;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileSharingService {

    private static final long EXPIRATION_TIME_MS = 5 * 60 * 1000; // 5 minutes
    private static final String TEMP_DIR = "src/main/resources/temp-files/";

    private final Map<String, FileEntry> fileMetadata = new HashMap<>();

    public FileSharingService() {
        // Create the temporary directory if it doesn't exist
        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    public FileEntry storeFile(MultipartFile file, String receiver, String sender, String fileType, Optional<String> id, Optional<String> expiresAt) throws IOException {
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path filePath = Paths.get(TEMP_DIR, filename);
        Files.write(filePath, file.getBytes());

        FileEntry entry = new FileEntry(
                filename,
                file.getOriginalFilename(),
                System.currentTimeMillis(),
                getReadableFileSize(file.getSize()),
                receiver,
                sender,
                fileType,
                expiresAt.orElse(null),
                id.orElse(null)
                );
        fileMetadata.put(filename, entry);

        System.out.println("File stored in temp folder: " + filePath);
        return entry;
    }

    public byte[] tryGetFile(String filename) throws IOException {
        FileEntry entry = fileMetadata.get(filename);

        if (entry != null) {
            Path filePath = Paths.get(TEMP_DIR, entry.getFilename());
            return Files.readAllBytes(filePath);
        }
        return null;
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void cleanupExpiredFiles() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, FileEntry>> iterator = fileMetadata.entrySet().iterator();

        // Cleanup expired files from metadata map
        while (iterator.hasNext()) {
            Map.Entry<String, FileEntry> entry = iterator.next();
            if (currentTime - entry.getValue().getAcceptedAt() > EXPIRATION_TIME_MS) {
                deleteFile(entry.getValue().getFilename());
                iterator.remove();
                System.out.println("Expired file removed: " + entry.getValue().getFilename());
            }
        }

        // Check for orphaned files in the temp directory
        try {
            File tempDir = new File(TEMP_DIR);
            File[] files = tempDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    // If the file is not in metadata map, delete it
                    if (!isFileInMetadata(file.getName())) {
                        boolean deleted = file.delete();
                        if (deleted) {
                            System.out.println("Orphaned file deleted: " + file.getName());
                        } else {
                            System.err.println("Failed to delete orphaned file: " + file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during orphaned file cleanup: " + e.getMessage());
        }
    }

    // Helper method to check if a file is in the metadata map
    private boolean isFileInMetadata(String filename) {
        return fileMetadata.values().stream().anyMatch(entry -> entry.getFilename().equals(filename));
    }

    private String generateKey(String receiver, String sender) {
        return sender + ":" + receiver; // Unique key for sender-receiver pair
    }

    private String getReadableFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " Bytes";
        } else if (sizeInBytes < 1024 * 1024) {
            return (sizeInBytes / 1024) + " KB";
        } else {
            return String.format("%.2f MB", sizeInBytes / (1024.0 * 1024));
        }
    }

    private void deleteFile(String filename) {
        Path filePath = Paths.get(TEMP_DIR, filename);
        try {
            Files.deleteIfExists(filePath);
            System.out.println("File deleted: " + filePath);
        } catch (IOException e) {
            System.err.println("Error deleting file: " + filePath + " - " + e.getMessage());
        }
    }

}
