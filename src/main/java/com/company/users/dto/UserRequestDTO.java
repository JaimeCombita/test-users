package com.company.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserRequestDTO(
        @NotBlank @Size(max = 100) String name,
        @Email(message = "Email format is invalid") @Size(max = 150) String email,
        @NotBlank String password,
        @NotBlank String identificationNumber,
        List<PhoneDTO> phones
) {}
