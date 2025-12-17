package com.company.users.repository;

import com.company.users.model.PasswordRecoveryToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordRecoveryTokenRepository extends JpaRepository<PasswordRecoveryToken, UUID> {

    Optional<PasswordRecoveryToken> findByToken(String token);

}
