package com.test.users.repository;

import com.test.users.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Transactional
    void deleteUserById(UUID id);

    Optional<User> findByIdentificationNumber(String identificationNumber);
}
