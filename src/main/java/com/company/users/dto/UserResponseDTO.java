package com.company.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private UUID id;
    private Integer identificationNumber;
    private String name;
    private String email;
    private List<PhoneDTO> phones;
    private LocalDateTime created;
    private LocalDateTime modified;
    private String token;
    private Boolean isActive;
    private String password;

}
