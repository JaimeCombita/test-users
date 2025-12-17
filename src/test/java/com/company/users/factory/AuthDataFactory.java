package com.company.users.factory;

import com.company.users.crosscutting.Roles;
import com.company.users.dto.AuthTokens;
import com.company.users.dto.LoginRequestDTO;
import com.company.users.dto.LoginResponseDTO;
import com.company.users.dto.PhoneDTO;
import com.company.users.model.RefreshToken;
import com.company.users.model.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AuthDataFactory {

    public static LoginRequestDTO createLoginRequestDTO() {
        return LoginRequestDTO.builder()
                .email("test@test.com")
                .password("plainPassword")
                .build();
    }

    public static LoginResponseDTO createLoginResponseDTO(User user, String token, Instant expiration) {
        return LoginResponseDTO.builder()
                .id(user.getId())
                .identificationNumber(user.getIdentificationNumber())
                .name(user.getName())
                .email(user.getEmail())
                .phones(List.of(new PhoneDTO("3001234567", "1", "57")))
                .isActive(user.getIsActive())
                .allowMultisession(user.getAllowMultisession())
                .rol(user.getRol().toString())
                .token(token)
                .expiration(expiration)
                .build();
    }

    public static AuthTokens createAuthTokens(User user) {
        String token = "jwtToken";
        Instant expiration = Instant.now().plusSeconds(3600);
        LoginResponseDTO responseDTO = createLoginResponseDTO(user, token, expiration);
        String refreshToken = UUID.randomUUID() + ".secret";
        return new AuthTokens(responseDTO, refreshToken);
    }

    public static User createUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setIdentificationNumber("12345");
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("hashedPassword");
        user.setRol(Roles.ROLE_USER);
        user.setIsActive(true);
        user.setAllowMultisession(true);
        return user;
    }

    public static RefreshToken createRefreshToken(User user, Instant expiration) {
        RefreshToken token = new RefreshToken();
        token.setId(UUID.randomUUID());
        token.setUser(user);
        token.setTokenHash("hashedSecret");
        token.setExpirationDate(expiration);
        token.setIsRevoked(false);
        return token;
    }
}
