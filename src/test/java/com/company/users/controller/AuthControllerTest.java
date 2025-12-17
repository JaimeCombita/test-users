package com.company.users.controller;

import com.company.users.configuration.JwtConfig;
import com.company.users.dto.AuthTokens;
import com.company.users.dto.LoginRequestDTO;
import com.company.users.factory.AuthDataFactory;
import com.company.users.service.AuthService;
import com.company.users.model.User;

import com.company.users.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtConfig jwtConfig;

    @MockBean
    private JwtUtils jwtUtils;

    private ObjectMapper objectMapper;
    private User user;
    private AuthTokens tokens;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        user = AuthDataFactory.createUser();
        tokens = AuthDataFactory.createAuthTokens(user);

        when(jwtConfig.getRefresh()).thenReturn(java.time.Duration.ofDays(1));
    }

    @Test
    void login_success() throws Exception {
        LoginRequestDTO requestDTO = AuthDataFactory.createLoginRequestDTO();

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(tokens);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.token").value("jwtToken"));
    }

    @Test
    void refreshToken_success() throws Exception {
        when(authService.refreshToken(anyString())).thenReturn(tokens);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(new Cookie("refreshToken", tokens.refreshToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtToken"));
    }

    @Test
    void refreshToken_missingCookie_throwsException() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isForbidden());
    }

    @Test
    void logout_success() throws Exception {
        doNothing().when(authService).logout(anyString(), any(HttpServletResponse.class));

        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(new Cookie("refreshToken", tokens.refreshToken())))
                .andExpect(status().isNoContent());
    }
}

