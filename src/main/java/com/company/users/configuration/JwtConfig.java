package com.company.users.configuration;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.Key;


@Configuration
public class JwtConfig {
    private final Key key;
    private final long expiration;

    public JwtConfig(@Value("${jwt.secret}") String secret,
                     @Value("${jwt.expiration}") long expiration) {
        // Generar la clave a partir del secreto
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    public Key getKey() {
        return key;
    }

    public long getExpiration() {
        return expiration;
    }

}
