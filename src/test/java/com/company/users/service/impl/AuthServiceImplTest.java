package com.company.users.service.impl;

import com.company.users.configuration.JwtConfig;
import com.company.users.dto.AuthTokens;
import com.company.users.dto.LoginRequestDTO;
import com.company.users.exception.NoSuchResourceFoundException;
import com.company.users.factory.AuthDataFactory;
import com.company.users.model.RefreshToken;
import com.company.users.model.User;
import com.company.users.repository.RefreshTokenRepository;
import com.company.users.repository.UserRepository;
import com.company.users.utils.JwtUtils;
import com.company.users.mapper.UserMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtil;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtConfig jwtConfig;

    private User user;

    @BeforeEach
    void setUp() {
        user = AuthDataFactory.createUser();
    }

    @Test
    void login_success() {
        LoginRequestDTO requestDTO = AuthDataFactory.createLoginRequestDTO();
        when(jwtConfig.getExpiration()).thenReturn(java.time.Duration.ofHours(1));
        when(jwtConfig.getRefresh()).thenReturn(java.time.Duration.ofDays(1));
        when(userRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateAccessToken(any(), any(), any(), any())).thenReturn("jwtToken");
        when(userMapper.toLoginResponseDto(any(User.class), anyString(), any()))
                .thenReturn(AuthDataFactory.createLoginResponseDTO(user, "jwtToken", Instant.now()));

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> {
                    RefreshToken token = invocation.getArgument(0);
                    token.setId(UUID.randomUUID());
                    return token;
                });

        AuthTokens result = authService.login(requestDTO);

        assertNotNull(result.loginResponseDTO());
        assertEquals("jwtToken", result.loginResponseDTO().getToken());
    }

    @Test
    void login_userNotFound_throwsException() {
        LoginRequestDTO requestDTO = AuthDataFactory.createLoginRequestDTO();
        when(userRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.empty());

        assertThrows(NoSuchResourceFoundException.class, () -> authService.login(requestDTO));
    }

    @Test
    void refreshToken_success() {
        RefreshToken token = AuthDataFactory.createRefreshToken(user, Instant.now().plusSeconds(3600));
        when(jwtConfig.getExpiration()).thenReturn(java.time.Duration.ofHours(1));
        when(jwtConfig.getRefresh()).thenReturn(java.time.Duration.ofDays(1));
        when(refreshTokenRepository.findById(token.getId())).thenReturn(Optional.of(token));
        when(passwordEncoder.matches(anyString(), eq(token.getTokenHash()))).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(any(), any(), any(), any())).thenReturn("jwtToken");
        when(userMapper.toLoginResponseDto(any(User.class), anyString(), any()))
                .thenReturn(AuthDataFactory.createLoginResponseDTO(user, "jwtToken", Instant.now()));

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> {
                    RefreshToken saved = invocation.getArgument(0);
                    saved.setId(UUID.randomUUID());
                    return saved;
                });

        String rawToken = token.getId().toString() + ".secret";
        AuthTokens result = authService.refreshToken(rawToken);

        assertNotNull(result.loginResponseDTO());
        assertEquals("jwtToken", result.loginResponseDTO().getToken());
    }

    @Test
    void logout_nullToken_throwsException() {
        assertThrows(AccessDeniedException.class, () -> authService.logout(null, new MockHttpServletResponse()));
    }
}
