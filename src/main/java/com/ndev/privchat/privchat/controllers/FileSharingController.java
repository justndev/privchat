package com.ndev.privchat.privchat.controllers;

import com.ndev.privchat.privchat.data.RuntimeDataStore;
import com.ndev.privchat.privchat.service.FileSharingService;
import com.ndev.privchat.privchat.service.LoggingService;
import com.ndev.privchat.privchat.service.MessageService;
import com.ndev.privchat.privchat.service.SQLiteService;
import com.ndev.privchat.privchat.utilities.FileEntry;
import com.ndev.privchat.privchat.utilities.UtilityFunctions;
import com.ndev.privchat.privchat.websocket.WebSocketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/encrypt-files")
public class FileSharingController {

    @Autowired
    private FileSharingService fileSharingService;

    private final MessageService messageService;

    private final WebSocketService webSocketService;

    private final RuntimeDataStore runtimeDataStore;

    private  final LoggingService loggingService;

    private final SQLiteService sqliteService;

    private final UtilityFunctions utilityFunctions;

    public FileSharingController(FileSharingService fileSharingService, MessageService messageService, WebSocketService webSocketService, RuntimeDataStore runtimeDataStore, LoggingService loggingService, SQLiteService sqliteService, UtilityFunctions utilityFunctions) {
        this.fileSharingService = fileSharingService;
        this.messageService = messageService;
        this.webSocketService = webSocketService;
        this.runtimeDataStore = runtimeDataStore;
        this.loggingService = loggingService;
        this.sqliteService = sqliteService;
        this.utilityFunctions = utilityFunctions;
    }

    @PostMapping("/files")
    public ResponseEntity uploadFile(HttpServletRequest rq,
                                     @RequestParam("file") MultipartFile file,
                                     @RequestParam("receiver") String receiver,
                                     @RequestParam("fileType") String fileType,
                                     @RequestParam("id") String id,
                                     @RequestParam("chatId") String chatId,
                                     @RequestParam("expiresAt") String expiresAt
                                    ) {
        String sender = messageService.extractNicknameFromRequest(rq);

        String ipAddress = rq.getRemoteAddr();
        if (!runtimeDataStore.processFileLimit(ipAddress)) {
            return ResponseEntity.badRequest().body("Limit");
        };

        boolean isDtoCorrect = utilityFunctions.isFileUploadInputValid(file, sender, receiver, fileType, id, expiresAt);
        if (!isDtoCorrect) {
            return ResponseEntity.badRequest().build();
        }

        loggingService.log("Put File | S: " + sender + "R: " + receiver + "F:" + file.getOriginalFilename() + "T: " + fileType );
        sqliteService.addMessageTime(String.valueOf(System.currentTimeMillis()));
        try {
            FileEntry entry = fileSharingService.storeFile(file, receiver, sender, fileType, Optional.ofNullable(id), Optional.ofNullable(expiresAt));
            entry.setType("file");
            entry.setChatId(chatId);
            webSocketService.sendSpecific(receiver, entry, "file");
            webSocketService.sendSpecific(sender, entry, "file");
            return ResponseEntity.status(200).build();

        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(500).build();
        }

    }

    @GetMapping("/files")
    public ResponseEntity<byte[]> getFile(HttpServletRequest rq, @RequestParam String filename) {
        String sender = messageService.extractNicknameFromRequest(rq);

        boolean isFilenameCorrect = utilityFunctions.isParamValid(filename);
        if (!isFilenameCorrect) {
            System.out.println("File has wrong name");
            return ResponseEntity.badRequest().build();
        }
        loggingService.log("Got File | S: " + sender + "F: " + filename);

        try {
            byte[] fileData = fileSharingService.tryGetFile(filename);
            if (fileData != null) {
                return ResponseEntity.ok().body(fileData);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/media")
    public ResponseEntity uploadMedia(HttpServletRequest rq,
                                      @RequestParam("file") MultipartFile file,
                                      @RequestParam("receiver") String receiver,
                                      @RequestParam("id") String id,
                                      @RequestParam("chatId") String chatId,
                                      @RequestParam("expiresAt") String expiresAt,
                                      @RequestParam("fileType") String fileType
                                      ) {
        String ipAddress = rq.getRemoteAddr();
        if (!runtimeDataStore.processMediaLimit(ipAddress)) {
            return ResponseEntity.badRequest().body("Limit");
        };

        String sender = messageService.extractNicknameFromRequest(rq);
        loggingService.log("Put Media | S: " + sender + "R: " + receiver + "F:" + file.getOriginalFilename() + "T: " + fileType );
        sqliteService.addMessageTime(String.valueOf(System.currentTimeMillis()));

        boolean isDtoCorrect = utilityFunctions.isFileUploadInputValid(file, sender, receiver, fileType, id, expiresAt);
        if (!isDtoCorrect) {
            return ResponseEntity.badRequest().build();
        }

        try {

            FileEntry entry = fileSharingService.storeFile(file, receiver, sender, fileType, Optional.ofNullable(id), Optional.ofNullable(expiresAt));
            entry.setType("media");
            entry.setChatId(chatId);
            webSocketService.sendSpecific(receiver, entry, "media");
            webSocketService.sendSpecific(sender, entry, "media");
            return ResponseEntity.status(200).build();

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/media")
    public ResponseEntity<byte[]> getMedia(HttpServletRequest rq, @RequestParam String filename) {
        String sender = messageService.extractNicknameFromRequest(rq);

        boolean isFilenameCorrect = utilityFunctions.isParamValid(filename);
        if (!isFilenameCorrect) {
            return ResponseEntity.badRequest().build();
        }
        loggingService.log("Got Media | S: " + sender + "F: " + filename);

        try {
            byte[] fileData = fileSharingService.tryGetFile(filename);
            if (fileData != null) {
                return ResponseEntity.ok().body(fileData);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
