package sn.tafgestion.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            if (jwtService.validateToken(token)) {
                String email    = jwtService.extractEmail(token);
                String tenantId = jwtService.extractTenantId(token);
                String role     = jwtService.extractRole(token);

                // Router vers le bon schéma PostgreSQL
                if (tenantId != null && !tenantId.isBlank()) {
                    // Utilisateur tenant → son schéma
                    TenantContext.setTenantId(tenantId);
                } else {
                    // Super Admin → schéma public
                    TenantContext.setTenantId("public");
                }

                // Éviter ROLE_ROLE_SUPER_ADMIN
                String authority = role.startsWith("ROLE_")
                        ? role : "ROLE_" + role;

                var auth = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority(authority))
                );
                auth.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            TenantContext.clear();
        }

        // Exécuter la requête avec le bon schéma
        filterChain.doFilter(request, response);

        // Nettoyer APRÈS que la requête est complètement traitée
        TenantContext.clear();
    }
}