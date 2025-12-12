package com.company.users.repository;

import com.company.users.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String id);
    void deleteByUserId(UUID userId);
    boolean existsByUserIdAndIsRevokedFalse(UUID user_id);

}
