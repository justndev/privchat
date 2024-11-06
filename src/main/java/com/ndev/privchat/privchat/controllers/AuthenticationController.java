package com.ndev.privchat.privchat.controllers;

import com.ndev.privchat.privchat.dtos.LoginUserDto;
import com.ndev.privchat.privchat.dtos.RegisterUserDto;
import com.ndev.privchat.privchat.entities.User;
import com.ndev.privchat.privchat.responses.LoginResponse;
import com.ndev.privchat.privchat.service.AuthenticationService;
import com.ndev.privchat.privchat.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000") // You can add this for additional safety
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }
    

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        Map<String, Object> extraClaims = new HashMap<>();
        String jwtToken = jwtService.generateToken(extraClaims, authenticatedUser);

        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
