package com.company.users.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "REFRESH_TOKENS")
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "expiration_date", nullable = false)
    private Instant expirationDate;

    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

}
