package com.company.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private UUID id;
    private String identificationNumber;
    private String name;
    private String email;
    private List<PhoneDTO> phones;
    private Boolean isActive;
    private Boolean allowMultisession;
    private String rol;
    private String token;
    private Instant expiration;
}
