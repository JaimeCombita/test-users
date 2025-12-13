package com.company.users.utils;

import com.company.users.configuration.JwtConfig;
import com.company.users.crosscutting.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class JwtUtils {

    private final JwtConfig jwtConfig;

    public JwtUtils(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generateRefreshToken(UUID userId, String email, Instant expirationInstant) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expirationInstant))
                .signWith(jwtConfig.getKey())
                .compact();
    }

    public String generateAccessToken(UUID userId, String email, Set<Role> roles, Instant expirationInstant) {
        List<String> roleNames = roles.stream()
                .map(Enum::name)
                .toList();

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .claim("roles", roleNames)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expirationInstant))
                .signWith(jwtConfig.getKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtConfig.getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtConfig.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
