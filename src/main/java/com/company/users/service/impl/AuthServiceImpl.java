package com.company.users.service.impl;

import com.company.users.configuration.JwtConfig;
import com.company.users.crosscutting.ErrorMessage;
import com.company.users.dto.AuthTokens;
import com.company.users.dto.LoginRequestDTO;
import com.company.users.dto.LoginResponseDTO;
import com.company.users.exception.BadCredentialsException;
import com.company.users.exception.NoSuchResourceFoundException;
import com.company.users.mapper.UserMapper;
import com.company.users.model.RefreshToken;
import com.company.users.model.User;
import com.company.users.repository.RefreshTokenRepository;
import com.company.users.repository.UserRepository;
import com.company.users.service.AuthService;
import com.company.users.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service("authService")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtil;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;

    @Override
    public AuthTokens login(LoginRequestDTO loginRequestDTO){
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new NoSuchResourceFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        validateUserActive(user);
        validatePassword(loginRequestDTO, user);

        if (Boolean.FALSE.equals(user.getAllowMultisession())) {
            boolean hasActiveSession = refreshTokenRepository.existsByUserIdAndIsRevokedFalse(user.getId());
            if (hasActiveSession) {
                throw new AccessDeniedException(ErrorMessage.USER_SESSION_ACTIVE.getMessage());
            }
        }

        return getAuthTokens(user);
    }

    @Override
    public AuthTokens refreshToken(String refreshTokenRaw){
        RefreshToken tokenEntity = validateRefreshToken(refreshTokenRaw);;

        if(Boolean.TRUE.equals(tokenEntity.getIsRevoked()) || Instant.now().isAfter(tokenEntity.getExpirationDate())){
            throw new RuntimeException(ErrorMessage.REFRESH_TOKEN_EXPIRED_OR_REVOKED.getMessage());
        }

        User user = userRepository.findById(tokenEntity.getUser().getId())
                .orElseThrow(() -> new NoSuchResourceFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        tokenEntity.setIsRevoked(true);
        refreshTokenRepository.save(tokenEntity);

        return getAuthTokens(user);
    }

    @Override
    public void logout(String refreshTokenRaw, HttpServletResponse response) {
        if (refreshTokenRaw == null) {
            throw new AccessDeniedException(ErrorMessage.REFRESH_TOKEN_ID_NOT_FOUND.getMessage());
        }
        RefreshToken tokenEntity = validateRefreshToken(refreshTokenRaw);

        tokenEntity.setIsRevoked(true);
        refreshTokenRepository.save(tokenEntity);

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);

    }

    private RefreshToken validateRefreshToken(String refreshTokenRaw) {
        String[] parts = refreshTokenRaw.split("\\.");
        UUID tokenId = UUID.fromString(parts[0]);
        String tokenSecret = parts[1];

        RefreshToken tokenEntity = refreshTokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException(ErrorMessage.REFRESH_TOKEN_ID_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(tokenSecret, tokenEntity.getTokenHash())) {
            throw new RuntimeException(ErrorMessage.REFRESH_TOKEN_INVALID.getMessage());
        }

        return tokenEntity;
    }

    private AuthTokens getAuthTokens(User user) {
        Instant instant = Instant.now();

        Instant accessTokenExpiration = instant.plus(jwtConfig.getExpiration());
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRol().toString(), accessTokenExpiration);

        Instant refreshExpiration = instant.plus(jwtConfig.getRefresh());
        String newRefreshToken = saveRefreshToken(user, refreshExpiration);

        LoginResponseDTO loginResponseDTO = userMapper.toLoginResponseDto(user, newAccessToken, accessTokenExpiration);
        return new AuthTokens(loginResponseDTO, newRefreshToken);
    }

    private void validateUserActive(User user) {
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new AccessDeniedException(ErrorMessage.USER_NOT_ACTIVE.getMessage());
        }
    }

    private void validatePassword(LoginRequestDTO dto, User user) {
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException(ErrorMessage.INVALID_CREDENTIALS.getMessage());
        }
    }

    private String saveRefreshToken(User user, Instant expiration) {
        String tokenSecret = RandomStringUtils.randomAlphanumeric(64);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(passwordEncoder.encode(tokenSecret))
                .expirationDate(expiration)
                .isRevoked(Boolean.FALSE)
                .build();
        refreshToken = refreshTokenRepository.save(refreshToken);
        return String.join(".", refreshToken.getId().toString(), tokenSecret);

    }

}
