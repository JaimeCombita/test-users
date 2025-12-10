package com.test.users.service.impl;

import com.test.users.crosscutting.Role;
import com.test.users.dto.UserUpdateDTO;
import com.test.users.exception.BadResourceRequestException;
import com.test.users.exception.NoSuchResourceFoundException;
import com.test.users.model.User;
import com.test.users.repository.UserRepository;
import com.test.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
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
            throw new BadResourceRequestException("User with same Identification Number exists.");
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
                .orElseThrow(() -> new NoSuchResourceFoundException("User not found"));

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
                .orElseThrow(() -> new BadResourceRequestException("No user with given id found."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !currentUserId.equals(id.toString())) {
            throw new AccessDeniedException("You can only access your own information");
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
