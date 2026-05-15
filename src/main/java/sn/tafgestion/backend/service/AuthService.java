package sn.tafgestion.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.tafgestion.backend.dto.AuthResponse;
import sn.tafgestion.backend.dto.LoginRequest;
import sn.tafgestion.backend.dto.RefreshTokenRequest;
import sn.tafgestion.backend.model.AppUser;
import sn.tafgestion.backend.model.RefreshToken;
import sn.tafgestion.backend.model.Tenant;
import sn.tafgestion.backend.repository.AppUserRepository;
import sn.tafgestion.backend.repository.RefreshTokenRepository;
import sn.tafgestion.backend.repository.TenantRepository;
import sn.tafgestion.backend.security.JwtService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // ── LOGIN ─────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {

        // 1. Trouver l'utilisateur
        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        // 2. Vérifier le mot de passe
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        // 3. Vérifier que l'utilisateur est actif
        if (!user.isActive()) {
            throw new RuntimeException("Compte désactivé");
        }

        // 4. Récupérer le nom du tenant si applicable
        String tenantName = "";
        if (user.getTenantId() != null) {
            tenantName = tenantRepository.findById(user.getTenantId())
                    .map(Tenant::getName)
                    .orElse("");
        }

        // 5. Générer les tokens
        String tenantId = user.getTenantId() != null
                ? user.getTenantId().toString() : "";

        String accessToken = jwtService.generateToken(
                user.getEmail(),
                user.getId().toString(),
                tenantId,
                user.getRole()
        );
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // 6. Sauvegarder le refresh token en base
        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .userId(user.getId())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole())
                .tenantId(tenantId)
                .tenantName(tenantName)
                .build();
    }

    // ── REFRESH TOKEN ─────────────────────────────────────
    public AuthResponse refresh(RefreshTokenRequest request) {

        // 1. Trouver le refresh token en base
        RefreshToken savedToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token invalide"));

        // 2. Vérifier l'expiration
        if (savedToken.isExpired()) {
            refreshTokenRepository.delete(savedToken);
            throw new RuntimeException("Refresh token expiré — reconnectez-vous");
        }

        // 3. Trouver l'utilisateur
        AppUser user = userRepository.findById(savedToken.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 4. Générer un nouveau access token
        String tenantId = user.getTenantId() != null
                ? user.getTenantId().toString() : "";

        String newAccessToken = jwtService.generateToken(
                user.getEmail(),
                user.getId().toString(),
                tenantId,
                user.getRole()
        );
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        // 5. Rotation du refresh token
        refreshTokenRepository.delete(savedToken);
        refreshTokenRepository.save(RefreshToken.builder()
                .token(newRefreshToken)
                .userId(user.getId())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .email(user.getEmail())
                .role(user.getRole())
                .tenantId(tenantId)
                .build();
    }

    // ── LOGOUT ────────────────────────────────────────────
    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(user ->
                refreshTokenRepository.deleteByUserId(user.getId()));
    }
}