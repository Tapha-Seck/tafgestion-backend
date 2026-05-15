package sn.tafgestion.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sn.tafgestion.backend.dto.AuthResponse;
import sn.tafgestion.backend.dto.LoginRequest;
import sn.tafgestion.backend.dto.RefreshTokenRequest;
import sn.tafgestion.backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /api/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @AuthenticationPrincipal String email) {
        authService.logout(email);
        return ResponseEntity.ok("Déconnexion réussie");
    }
}