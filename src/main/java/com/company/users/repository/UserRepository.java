package com.company.users.repository;

import com.company.users.crosscutting.Roles;
import com.company.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    boolean existsByIdentificationNumber(String identificationNumber);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRol(Roles rol);
}
