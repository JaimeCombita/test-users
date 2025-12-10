package com.company.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneDTO {

    @NotBlank(message = "Phone number is required")
    @Size(max = 20)
    private String number;

    @NotBlank(message = "City code is required")
    @Size(max = 10)
    private String cityCode;

    @NotBlank(message = "Country code is required")
    @Size(max = 10)
    private String countryCode;

}
