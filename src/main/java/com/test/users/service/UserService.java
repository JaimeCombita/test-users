package com.test.users.service;

import com.test.users.dto.UserRequestDTO;
import com.test.users.dto.UserUpdateDTO;
import com.test.users.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User createUser(User user);

    User updateUser(UUID id, UserUpdateDTO userUpdateDTO);

    Optional<User> getUserById(UUID id);

    List<User> getAllUsers();

    void deleteUserById(UUID id);

    void deleteAllUsers();

}
