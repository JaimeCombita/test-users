package com.company.users.service;

import com.company.users.crosscutting.Roles;
import com.company.users.dto.ChangePasswordDTO;
import com.company.users.dto.UserRequestDTO;
import com.company.users.dto.UserResponseDTO;
import com.company.users.dto.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO userDto);

    UserResponseDTO updateUser(UUID id, UserUpdateDTO userUpdateDTO);

    UserResponseDTO getUserById(UUID id);

    Page<UserResponseDTO> getAllUsers(Pageable pageable,
                                      String name,
                                      String email,
                                      Boolean isActive,
                                      Roles role,
                                      LocalDateTime createdFrom,
                                      LocalDateTime createdTo,
                                      LocalDateTime modifiedFrom,
                                      LocalDateTime modifiedTo);

    void deleteUserById(UUID id);

    void deleteAllUsers();

    void changePassword(UUID id, ChangePasswordDTO changePasswordDTO);

    void initiatePasswordRecovery(String email);

    void completePasswordRecovery(String token, String newPassword);

}
