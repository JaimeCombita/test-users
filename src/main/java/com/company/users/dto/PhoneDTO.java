package com.company.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PhoneDTO(
        @NotBlank(message = "Phone number is required")
        @Size(max = 20)
        String number,

        @NotBlank(message = "City code is required")
        @Size(max = 10)
        String cityCode,

        @NotBlank(message = "Country code is required")
        @Size(max = 10)
        String countryCode
) {}

