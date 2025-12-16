package com.company.users.dto;

import com.company.users.crosscutting.Roles;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {

    @Size(max = 100)
    private String name;

    @Email(message = "Email format is invalid")
    @Size(max = 150)
    private String email;

    private Boolean isActive;

    private Boolean allowMultisession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles rol;


}
