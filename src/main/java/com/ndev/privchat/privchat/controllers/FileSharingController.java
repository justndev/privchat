package com.ndev.privchat.privchat.controllers;

import com.ndev.privchat.privchat.service.FileSharingService;
import com.ndev.privchat.privchat.service.MessageService;
import com.ndev.privchat.privchat.utilities.MediaEntry;
import com.ndev.privchat.privchat.websocket.WebSocketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/encrypt-files")
public class FileSharingController {

    @Autowired
    private FileSharingService fileSharingService;

    private final MessageService messageService;

    private final WebSocketService webSocketService;

    public FileSharingController(FileSharingService fileSharingService, MessageService messageService, WebSocketService webSocketService) {
        this.fileSharingService = fileSharingService;
        this.messageService = messageService;
        this.webSocketService = webSocketService;
    }

    @PostMapping("/files")
    public ResponseEntity uploadFile(HttpServletRequest rq,
                                                                   @RequestParam("file") MultipartFile file,
                                                                   @RequestParam("receiver") String receiver,
                                                                   @RequestParam("fileType") String fileType
    ) {
        String sender = messageService.extractNicknameFromRequest(rq);

        try {
            FileSharingService.FileEntry entry = fileSharingService.storeFile(file, receiver, sender, fileType);
            webSocketService.sendSpecific(receiver, entry, "file");
            webSocketService.sendSpecific(sender, entry, "file");
            return ResponseEntity.status(200).build();

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/files")
    public ResponseEntity<byte[]> getFile(HttpServletRequest rq, @RequestParam String filename) {
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
                                      @RequestParam("filename") String filename,
                                      @RequestParam("receiver") String receiver,
                                      @RequestParam("randomId") String randomId,
                                      @RequestParam("fileType") String fileType
                                      ) {
        String sender = messageService.extractNicknameFromRequest(rq);

        try {
            FileSharingService.FileEntry entry = fileSharingService.storeFile(file, receiver, sender, fileType);

            MediaEntry mediaEntry = new MediaEntry(entry.getFilename(), receiver, sender, randomId, fileType);
            webSocketService.sendSpecific(receiver, mediaEntry, "media");
            webSocketService.sendSpecific(sender, mediaEntry, "media");
            return ResponseEntity.status(200).build();

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/media")
    public ResponseEntity<byte[]> getMedia(HttpServletRequest rq, @RequestParam String filename) {
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
