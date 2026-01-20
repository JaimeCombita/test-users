package com.company.users.dto;

import jakarta.validation.constraints.NotBlank;

public record RecoveryPasswordDTO(
        @NotBlank String token,
        @NotBlank String newPassword
) {
}
