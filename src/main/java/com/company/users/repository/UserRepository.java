package com.company.users.repository;

import com.company.users.crosscutting.Roles;
import com.company.users.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Transactional
    void deleteUserById(UUID id);

    Optional<User> findByIdentificationNumber(String identificationNumber);

    Optional<User> findByEmail(String email);

    boolean existsByRolesContaining(Roles role);
}
