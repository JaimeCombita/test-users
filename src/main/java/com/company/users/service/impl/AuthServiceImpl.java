package com.company.users.service.impl;

import com.company.users.configuration.JwtConfig;
import com.company.users.crosscutting.ErrorMessage;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO){
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

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRoles());

        Instant refreshExpiration = Instant.now().plus(jwtConfig.getRefresh());

        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail(), refreshExpiration);

        saveRefreshToken(user, refreshToken, refreshExpiration);

        return userMapper.toLoginResponseDto(user, accessToken);
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

    private void saveRefreshToken(User user, String tokenHash, Instant expiration) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(passwordEncoder.encode(tokenHash))
                .expirationDate(expiration)
                .isRevoked(Boolean.FALSE)
                .build();
        refreshTokenRepository.save(refreshToken);
    }


}
