package com.ndev.privchat.privchat.controllers;

import com.ndev.privchat.privchat.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @GetMapping("/create")
    public ResponseEntity createPayment() {
        Object obj = paymentService.createPayment();
        return ResponseEntity.ok().body(obj);
    }
    @GetMapping("/check")
    public ResponseEntity checkPayment(HttpServletRequest rq, @RequestParam String id) {
        boolean result = paymentService.checkPaymentById(id);
        return ResponseEntity.ok().body(result);
    }
}
