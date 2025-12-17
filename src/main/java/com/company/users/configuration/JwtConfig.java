package com.company.users.configuration;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.time.Duration;


@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
public class JwtConfig {

    private String secret;
    @Setter
    private Duration expiration;
    @Setter
    private Duration refresh;
    private Key key;

    public void setSecret(String secret) {
        this.secret = secret;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

}

