package com.company.users.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRecoveryToken {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private User user;

    private String token;
    private Instant expirationDate;
    private Boolean isUsed;
}

