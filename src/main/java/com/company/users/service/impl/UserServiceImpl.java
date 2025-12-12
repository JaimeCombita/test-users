package com.company.users.service.impl;

import com.company.users.crosscutting.Constants;
import com.company.users.crosscutting.ErrorMessage;
import com.company.users.crosscutting.Role;
import com.company.users.dto.UserUpdateDTO;
import com.company.users.exception.BadResourceRequestException;
import com.company.users.exception.NoSuchResourceFoundException;
import com.company.users.model.User;
import com.company.users.repository.UserRepository;
import com.company.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        Optional<User> existUser = userRepository.findByIdentificationNumber(user.getIdentificationNumber());

        if(existUser.isPresent()){
            throw new BadResourceRequestException(ErrorMessage.USER_WITH_SAME_IDENTIFICATION_NUMBER.getMessage());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRoles(Set.of(Role.ROLE_USER));
        user.setIsActive(Boolean.TRUE);
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User updateUser(UUID id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchResourceFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        if (userUpdateDTO.getName() != null) {
            user.setName(userUpdateDTO.getName());
        }
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }
        if (userUpdateDTO.getIsActive() != null) {
            user.setIsActive(userUpdateDTO.getIsActive());
        }
        user.setModified(LocalDateTime.now());

        return userRepository.save(user);
    }


    @Override
    public Optional<User> getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadResourceRequestException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Constants.ROLE_ADMIN));

        if (!isAdmin && !currentUserId.equals(id.toString())) {
            throw new AccessDeniedException(ErrorMessage.CAN_ONLY_ACCESS_YOY_OWN_INFO.getMessage());
        }

        return Optional.ofNullable(user);

    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public void deleteAllUsers() {
        userRepository.deleteAllInBatch();
    }

    @Override
    public void deleteUserById(UUID id) {
        userRepository.deleteUserById(id);
    }
}
