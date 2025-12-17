package com.company.users.dto;

import com.company.users.crosscutting.Roles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String identificationNumber,
        String name,
        String email,
        List<PhoneDTO> phones,
        LocalDateTime created,
        LocalDateTime modified,
        Boolean isActive,
        Boolean allowMultisession,
        Roles rol
) {}
