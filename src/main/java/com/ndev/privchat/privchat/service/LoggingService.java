package com.ndev.privchat.privchat.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class LoggingService {

    private static final String LOG_FILE_PATH = "logs/users.log";

    // Updated format pattern that properly handles zone
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss z")
            .withZone(ZoneId.systemDefault());

    public LoggingService() {
        createLogFileIfNotExists();
    }

    private void createLogFileIfNotExists() {
        try {
            File logFile = new File(LOG_FILE_PATH);
            File logDir = logFile.getParentFile();

            if (!logDir.exists()) {
                Files.createDirectories(Paths.get(logDir.getAbsolutePath()));
            }

            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Failed to create log file: " + e.getMessage());
            // Consider throwing a runtime exception if log file creation is critical
            throw new RuntimeException("Failed to initialize logging system", e);
        }
    }

    public void log(String message) {
        // Use ZonedDateTime instead of LocalDateTime to include zone information
        String timestamp = ZonedDateTime.now().format(DATE_FORMAT);
        String logMessage = String.format("%n%s - %s", timestamp, message);

        try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) {
            writer.write(logMessage);
            writer.flush(); // Ensure the log is written immediately
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
            // Consider throwing a runtime exception if logging is critical
            throw new RuntimeException("Failed to write log message", e);
        }
    }
}