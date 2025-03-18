package com.leduy8.springbootjava.auth.controller;

import com.leduy8.springbootjava.auth.dto.LoginRequestDTO;
import com.leduy8.springbootjava.auth.dto.LoginResponseDTO;
import com.leduy8.springbootjava.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        String token = authService.authenticate(request.email(), request.password());
        return ResponseEntity.ok(LoginResponseDTO.of(token));
    }
}

