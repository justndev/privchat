package com.ndev.privchat.privchat.controllers;

import com.ndev.privchat.privchat.responses.LoginResponse;
import com.ndev.privchat.privchat.service.AuthenticationService;
import com.ndev.privchat.privchat.service.JwtService;
import com.ndev.privchat.privchat.swarmPool.SwampUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/pool")
@Controller
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/enter")
    public ResponseEntity<LoginResponse> authenticateToPool() throws IOException {

        try {
            SwampUser authenticatedUser = authenticationService.analogAuthenticate();
            if (authenticatedUser == null) {
                return ResponseEntity.internalServerError().build();
            }

            Map<String, Object> extraClaims = new HashMap<>();
            String jwtToken = jwtService.generateToken(extraClaims, authenticatedUser);

            LoginResponse loginResponse = new LoginResponse()
                    .setNickname(authenticatedUser.getNickname())
                    .setToken(jwtToken)
                    .setExpiresIn(jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
