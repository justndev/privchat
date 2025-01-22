package com.ndev.privchat.privchat.controllers;

import com.ndev.privchat.privchat.dtos.AppDataDto;
import com.ndev.privchat.privchat.entities.MessageRecord;
import com.ndev.privchat.privchat.entities.RegistrationRecord;
import com.ndev.privchat.privchat.service.SQLiteService;
import com.ndev.privchat.privchat.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "http://privchat.s3-website.eu-north-1.amazonaws.com")
@RequestMapping("/admin")
@Controller
public class AdminController {

    private final UserService userService;
    private final SQLiteService sqliteSerivce;

    public AdminController(UserService userService, SQLiteService sqliteSerivce) {
        this.userService = userService;
        this.sqliteSerivce = sqliteSerivce;
    }

    @GetMapping("/stats")
    public ResponseEntity<AppDataDto> getAppData(@RequestParam String password) {
        if (Objects.equals(password, "admin")) {
            int currentUsers = userService.countUsers();
            List<RegistrationRecord> allRegistrations= sqliteSerivce.getRegistrations();
            List<MessageRecord> allMessages= sqliteSerivce.getMessages();
            AppDataDto appDataDto = AppDataDto.builder().messages(allMessages).registrations(allRegistrations).currentUsersAmount(currentUsers).build();

            return ResponseEntity.ok(appDataDto);
        }
        return ResponseEntity.badRequest().build();
    }
}
