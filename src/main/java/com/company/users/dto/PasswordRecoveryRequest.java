package com.company.users.dto;

import jakarta.validation.constraints.Email;

public record PasswordRecoveryRequest(
        @Email(message = "Invalid email format") String email
) {
}
