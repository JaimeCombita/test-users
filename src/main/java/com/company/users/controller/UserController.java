package com.company.users.controller;

import com.company.users.dto.UserRequestDTO;
import com.company.users.dto.UserResponseDTO;
import com.company.users.dto.UserUpdateDTO;
import com.company.users.exception.NoSuchResourceFoundException;
import com.company.users.mapper.UserMapper;
import com.company.users.model.User;
import com.company.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

import static com.company.users.crosscutting.ResourceEndpoint.CONSUMES_TYPE_JSON;
import static com.company.users.crosscutting.ResourceEndpoint.USER;
import static com.company.users.crosscutting.ResourceEndpoint.USERS;
import static com.company.users.crosscutting.ResourceEndpoint.USER_BY_ID;
import static com.company.users.crosscutting.ResourceEndpoint.API_VERSION;

@RestController
@RequestMapping(API_VERSION)
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = USER, consumes = CONSUMES_TYPE_JSON, produces = CONSUMES_TYPE_JSON)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        User user = userMapper.toEntity(userRequestDTO);
        User savedUser = userService.createUser(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping(value = USER_BY_ID)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        User updatedUser = userService.updateUser(id, userUpdateDTO);
        return userMapper.toUserResponseDto(updatedUser);

    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping(USER_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO getModelById(@PathVariable UUID id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new NoSuchResourceFoundException("No user with given id found."));
        return userMapper.toUserResponseDto(user);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(USERS)
    @ResponseStatus(HttpStatus.OK)
    public Page<UserResponseDTO> getUsers(
            @PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return userService.getAllUsers(pageable)
                .map(userMapper::toUserResponseDto);
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
