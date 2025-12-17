package com.company.users.controller;

import com.company.users.configuration.JwtConfig;
import com.company.users.crosscutting.ErrorCode;
import com.company.users.crosscutting.ResourceEndpoint;
import com.company.users.dto.AuthTokens;
import com.company.users.dto.LoginRequestDTO;
import com.company.users.dto.LoginResponseDTO;
import com.company.users.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import static com.company.users.crosscutting.ResourceEndpoint.*;
import static com.company.users.crosscutting.ResourceEndpoint.CONSUMES_TYPE_JSON;

@RestController
@RequestMapping(API_VERSION)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtConfig jwtConfig;

    @PostMapping(value = LOGIN, consumes = CONSUMES_TYPE_JSON, produces = CONSUMES_TYPE_JSON)
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO,
                                                  HttpServletResponse response) {
        AuthTokens tokens = authService.login(loginRequestDTO);

        addRefreshTokenCookie(response, tokens.refreshToken());

        return ResponseEntity.ok(tokens.loginResponseDTO());

    }

    @PostMapping(value = REFRESH_TOKEN, produces = CONSUMES_TYPE_JSON)
    public ResponseEntity<LoginResponseDTO> tokenRefresh(@CookieValue(value = "refreshToken", required = false) String refreshTokenRaw,
                                                  HttpServletResponse response) {
        if (refreshTokenRaw == null) {
            throw new AccessDeniedException(ErrorCode.REFRESH_TOKEN_NOT_FOUND_IN_COOKIE.getMessage());
        }

        AuthTokens tokens = authService.refreshToken(refreshTokenRaw);

        addRefreshTokenCookie(response, tokens.refreshToken());

        return ResponseEntity.ok(tokens.loginResponseDTO());
    }

    @PostMapping(value = ResourceEndpoint.LOGOUT)
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshTokenRaw,
            HttpServletResponse response) {
        authService.logout(refreshTokenRaw, response);
        return ResponseEntity.noContent().build();
    }


    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) jwtConfig.getRefresh().toSeconds());
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

}
