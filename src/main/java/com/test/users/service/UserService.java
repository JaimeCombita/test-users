package com.test.users.service;

import com.test.users.dto.UserRequestDTO;
import com.test.users.dto.UserUpdateDTO;
import com.test.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
