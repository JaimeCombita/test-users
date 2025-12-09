package com.test.users.service.impl;

import com.test.users.dto.UserRequestDTO;
import com.test.users.dto.UserUpdateDTO;
import com.test.users.exception.BadResourceRequestException;
import com.test.users.exception.NoSuchResourceFoundException;
import com.test.users.model.User;
import com.test.users.repository.UserRepository;
import com.test.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        Optional<User> existUser = userRepository.findByIdentificationNumber(user.getIdentificationNumber());

        if(existUser.isPresent()){
            throw new BadResourceRequestException("User with same Identification Number exists.");
        }

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
            user.setPassword(userUpdateDTO.getPassword());
        }
        if (userUpdateDTO.getIsActive() != null) {
            user.setIsActive(userUpdateDTO.getIsActive());
        }
        user.setModified(LocalDateTime.now());

        return userRepository.save(user);
    }


    @Override
    public Optional<User> getUserById(UUID id) {
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()){
            throw new BadResourceRequestException("No user with given id found.");
        }
        
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
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
