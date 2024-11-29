package com.ndev.privchat.privchat.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserCleanupService {
    private final UserService userService;

    // Define the expiration time (e.g., 5 minutes)
    private static final long EXPIRATION_TIME_MS = TimeUnit.MINUTES.toMillis(60);

    public UserCleanupService(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(fixedRate = 6000) // Run every 60 seconds
    public void cleanUpExpiredUsers() {
        long currentTime = System.currentTimeMillis();

        userService.getUserMap().forEach((nickname, user) -> {
            if (currentTime - user.getCreatedAt().getTime() > EXPIRATION_TIME_MS) {
                System.out.println("Removing " + nickname + " from UserMap");
                userService.removeUser(nickname);
            }
        });
    }
}
