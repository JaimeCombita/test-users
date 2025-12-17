package com.company.users.factory;

import com.company.users.crosscutting.Roles;
import com.company.users.dto.UserRequestDTO;
import com.company.users.dto.UserResponseDTO;
import com.company.users.dto.UserUpdateDTO;
import com.company.users.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserDataFactory {

    public static User createUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setIdentificationNumber("12345");
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("plainPassword");
        user.setRol(Roles.ROLE_USER);
        user.setIsActive(true);
        user.setAllowMultisession(true);
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        return user;
    }

    public static UserRequestDTO createUserRequestDTO() {
        return new UserRequestDTO(
                "Test User",
                "test@test.com",
                "plainPassword",
                "12345",
                List.of()
        );
    }

    public static UserUpdateDTO createUserUpdateDTO() {
        return new UserUpdateDTO(
                "Updated User",
                "updated@test.com",
                true,
                true,
                Roles.ROLE_ADMIN
        );
    }

    public static UserResponseDTO createUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getIdentificationNumber(),
                user.getName(),
                user.getEmail(),
                List.of(),
                user.getCreated(),
                user.getModified(),
                user.getIsActive(),
                user.getAllowMultisession(),
                user.getRol()
        );
    }
}
