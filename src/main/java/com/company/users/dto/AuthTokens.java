package com.company.users.dto;

import java.time.Instant;

public record AuthTokens(LoginResponseDTO loginResponseDTO, String refreshToken) {
}
