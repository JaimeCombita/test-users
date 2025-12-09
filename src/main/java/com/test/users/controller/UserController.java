package com.test.users.controller;

import com.test.users.dto.UserRequestDTO;
import com.test.users.dto.UserResponseDTO;
import com.test.users.dto.UserUpdateDTO;
import com.test.users.exception.NoSuchResourceFoundException;
import com.test.users.mapper.UserMapper;
import com.test.users.model.User;
import com.test.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

import static com.test.users.crosscutting.ResourceEndpoint.CONSUMES_TYPE_JSON;
import static com.test.users.crosscutting.ResourceEndpoint.USER;
import static com.test.users.crosscutting.ResourceEndpoint.USERS;
import static com.test.users.crosscutting.ResourceEndpoint.USER_BY_ID;
import static com.test.users.crosscutting.ResourceEndpoint.API_VERSION;

@RestController
@RequestMapping(API_VERSION)
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @PostMapping(value = USER, consumes = CONSUMES_TYPE_JSON, produces = CONSUMES_TYPE_JSON)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        User user = userMapper.toEntity(userRequestDTO);
        User savedUser = userService.createUser(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @PutMapping(value = USER_BY_ID)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        User updatedUser = userService.updateUser(id, userUpdateDTO);
        return userMapper.toUserResponseDto(updatedUser);

    }

    @GetMapping(USER_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO getModelById(@PathVariable UUID id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new NoSuchResourceFoundException("No user with given id found."));
        return userMapper.toUserResponseDto(user);

    }

    @GetMapping(USERS)
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDTO> getUsers() {
        return userMapper.toUserResponseDtoList(userService.getAllUsers());
    }

    @DeleteMapping(USER_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(USERS)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.noContent().build();
    }

}
