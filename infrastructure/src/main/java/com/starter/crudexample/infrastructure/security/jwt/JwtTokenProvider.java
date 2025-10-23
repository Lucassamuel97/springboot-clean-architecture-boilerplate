package com.starter.crudexample.infrastructure.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.starter.crudexample.domain.user.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtTokenProvider {

    private final Key jwtSecret;
    private final long jwtExpirationMs;

    public JwtTokenProvider(
        @Value("${app.jwt.secret:mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongToMeetTheRequirements}") String secret,
        @Value("${app.jwt.expiration-ms:86400000}") long expirationMs
    ) {
        this.jwtSecret = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationMs = expirationMs;
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        List<String> roles = userPrincipal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        return Jwts.builder()
            .setSubject(userPrincipal.getId())
            .claim("username", userPrincipal.getUsername())
            .claim("email", userPrincipal.getEmail())
            .claim("roles", roles)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(jwtSecret)
            .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith((SecretKey) jwtSecret)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<Role> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith((SecretKey) jwtSecret)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        List<String> roleStrings = (List<String>) claims.get("roles");
        return roleStrings.stream()
            .map(role -> Role.valueOf(role.replace("ROLE_", "")))
            .collect(Collectors.toList());
    }

    public boolean validateToken(String authToken) {
        final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtTokenProvider.class);
        try {
            Jwts.parser()
            .verifyWith((SecretKey) jwtSecret)
            .build()
            .parseSignedClaims(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.debug("JWT validation failed: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.debug("JWT validation failed: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.debug("JWT validation failed: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.debug("JWT validation failed: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.debug("JWT validation failed: {}", ex.getMessage());
        }
        return false;
    }
}
