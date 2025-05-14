package com.ndev.privchat.privchat.controllers;

import com.ndev.privchat.privchat.dtos.UserDataDto;
import com.ndev.privchat.privchat.responses.LoginResponse;
import com.ndev.privchat.privchat.service.*;
import com.ndev.privchat.privchat.swarmPool.SwampUser;
import com.ndev.privchat.privchat.utilities.UtilityFunctions;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/pool")
@Controller
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final LoggingService loggingService;
    private final SQLiteService sqliteService;
    private final UtilityFunctions utilityFunctions;
    private final PaymentService paymentService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, LoggingService loggingService, SQLiteService sqliteService, UtilityFunctions utilityFunctions, PaymentService paymentService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.loggingService = loggingService;
        this.sqliteService = sqliteService;
        this.utilityFunctions = utilityFunctions;
        this.paymentService = paymentService;
    }

    @PostMapping("/enter")
    public ResponseEntity<LoginResponse> authenticateToPool(HttpServletRequest rq, @RequestParam(required = false) String paymentId, @RequestBody UserDataDto dto) throws IOException {
        String ipAddress = rq.getRemoteAddr();
        String userData = dto.toString();

        boolean isDtoValid = utilityFunctions.isUserDataDtoValid(dto);
        if (!isDtoValid) {
            System.out.println("Messed user data :D");
        }

        loggingService.log("Entered | IP: " + ipAddress + "User: " + userData);
        sqliteService.addRegistrationTime(String.valueOf(System.currentTimeMillis()));
        boolean hasSubscription = false;
        if (paymentId != null && !paymentId.isEmpty()) {
            hasSubscription = paymentService.checkPaymentById(paymentId);
        }
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
                    .setExpiresIn(System.currentTimeMillis()+ jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/check-token")
    public ResponseEntity<Boolean> useToken(HttpServletRequest rq, @RequestParam String token) throws IOException {
        try {
            return ResponseEntity.ok(sqliteService.tokenExists(token));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/check")
    public ResponseEntity checkServerResponse() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
