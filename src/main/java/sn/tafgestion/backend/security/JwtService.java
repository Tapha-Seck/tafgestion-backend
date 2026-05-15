package sn.tafgestion.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Générer un access token
    public String generateToken(String email, String userId,
                                String tenantId, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("user_id", userId)
                .claim("tenant_id", tenantId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    // Générer un refresh token
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getKey())
                .compact();
    }

    // Extraire tous les claims
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extraire l'email
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // Extraire le tenant_id
    public String extractTenantId(String token) {
        return extractClaims(token).get("tenant_id", String.class);
    }

    // Extraire le rôle
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // Vérifier si le token est expiré
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Valider le token
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}