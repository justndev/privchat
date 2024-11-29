package com.ndev.privchat.privchat.service;


import com.ndev.privchat.privchat.configs.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    public JwtService jwtService;


    public MessageService() {

    }



    public String extractNicknameFromRequest(HttpServletRequest rq) {
        String jwt = jwtService.parseJwt(rq);
        return jwtService.extractUsername(jwt);
    }
}