package com.example.easychat.controller;

import com.example.easychat.service.AsteriskRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/asterisk")
public class AsteriskRegistrationController {

    @Autowired
    private AsteriskRegistrationService registrationService;

    @RequestMapping("/register")
    public ResponseEntity<String> registerAccount(
            @RequestParam String username,
            @RequestParam String password) {
        try {
            registrationService.registerAccount(username, password);
            return ResponseEntity.ok("Account registered successfully: " + username);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error registering account: " + e.getMessage());
        }
    }
}
