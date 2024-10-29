package com.ndev.privchat.privchat.service;

import com.ndev.privchat.privchat.dtos.LoginUserDto;
import com.ndev.privchat.privchat.dtos.RegisterUserDto;
import com.ndev.privchat.privchat.entities.User;
import com.ndev.privchat.privchat.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    )
    {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {
        User user = new User(
                UUID.randomUUID(),
                input.getNickname(),
                passwordEncoder.encode(input.getPassword()),
                null
        );

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getNickname(),
                        input.getPassword()
                )
        );
        return userRepository.findByNickname(input.getNickname())
                .orElseThrow();
    }
}