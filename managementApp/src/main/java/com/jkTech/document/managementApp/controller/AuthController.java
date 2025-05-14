package com.jkTech.document.managementApp.controller;

import com.jkTech.document.managementApp.dto.request.LoginRequest;
import com.jkTech.document.managementApp.dto.request.SignupRequest;
import com.jkTech.document.managementApp.dto.response.JwtResponse;
import com.jkTech.document.managementApp.dto.response.MessageResponse;
import com.jkTech.document.managementApp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(authService.registerUser(signupRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logoutUser() {
        return ResponseEntity.ok(authService.logoutUser());
    }
}
