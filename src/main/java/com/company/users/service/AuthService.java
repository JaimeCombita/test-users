package com.company.users.service;

import com.company.users.dto.AuthTokens;
import com.company.users.dto.LoginRequestDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthTokens login(LoginRequestDTO loginRequestDTO);
    AuthTokens refreshToken(String refreshToken);
    void logout(String refreshTokenRaw, HttpServletResponse response);
}
