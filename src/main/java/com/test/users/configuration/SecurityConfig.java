package com.test.users.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Parámetros: saltLength, hashLength, parallelism, memory, iterations
        return new Argon2PasswordEncoder(
                16,   // salt length (bytes)
                32,   // hash length (bytes)
                1,    // parallelism (hilos)
                1 << 13, // memory (8 MB)
                3     // iterations
        );
    }
}
