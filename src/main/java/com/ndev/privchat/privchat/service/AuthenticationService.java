package com.ndev.privchat.privchat.service;

import com.ndev.privchat.privchat.service.nickname.NicknameService;
import com.ndev.privchat.privchat.swarmPool.SwampUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthenticationService {
    // SWAMP PROJECT
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    private final NicknameService nicknameService;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            NicknameService nicknameService,
            UserService userService
    )
    {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.nicknameService = nicknameService;
        this.userService = userService;
    }

    // SWAMP PROJECT
    public SwampUser analogAuthenticate() throws IOException {
        String randomNickname = nicknameService.generateNickname();

        boolean isNicknameTaken = userService.userExists(randomNickname);
        if (isNicknameTaken) {
            return null;
        }
        SwampUser swampUser = SwampUser.builder()
                .nickname(randomNickname)
                .createdAt(new Date(System.currentTimeMillis()))
                .build();
        userService.addUser(swampUser);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        randomNickname,
                        randomNickname
                )
        );
        return swampUser;
    }
}