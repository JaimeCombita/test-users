package com.company.users.service;

import com.company.users.dto.UserUpdateDTO;
import com.company.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User createUser(User user);

    User updateUser(UUID id, UserUpdateDTO userUpdateDTO);

    Optional<User> getUserById(UUID id);

    Page<User> getAllUsers(Pageable pageable);

    void deleteUserById(UUID id);

    void deleteAllUsers();

}
