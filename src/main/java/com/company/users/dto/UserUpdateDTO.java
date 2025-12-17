package com.company.users.dto;

import com.company.users.crosscutting.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @Size(max = 100) String name,
        @Email(message = "Email format is invalid") @Size(max = 150) String email,
        Boolean isActive,
        Boolean allowMultisession,
        Roles rol
) {}
