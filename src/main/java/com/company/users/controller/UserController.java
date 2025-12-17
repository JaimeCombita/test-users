package com.company.users.controller;

import com.company.users.crosscutting.Roles;
import com.company.users.dto.ChangePasswordDTO;
import com.company.users.dto.UserRequestDTO;
import com.company.users.dto.UserResponseDTO;
import com.company.users.dto.UserUpdateDTO;
import com.company.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.company.users.crosscutting.ResourceEndpoint.*;

@RestController
@RequestMapping(API_VERSION)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = USER, consumes = CONSUMES_TYPE_JSON, produces = CONSUMES_TYPE_JSON)
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping(value = USER_BY_ID)
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable("id") UUID id,
            @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.name")
    @GetMapping(USER_BY_ID)
    public ResponseEntity<UserResponseDTO> getModelById(@PathVariable("id") UUID id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(USERS)
    public ResponseEntity<Page<UserResponseDTO>> getUsers(
            @PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Roles role,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modifiedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modifiedTo) {
        return ResponseEntity.ok(
                userService.getAllUsers(pageable, name, email, isActive, role, createdFrom, createdTo, modifiedFrom, modifiedTo)
        );

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(USER_BY_ID)
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") UUID id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(USERS)
    public ResponseEntity<Void> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(CHANGE_PASSWORD)
    public ResponseEntity<Void> changePassword(
            @PathVariable("id") UUID id,
            @RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(id, changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(INIT_RESET_PASSWORD)
    public ResponseEntity<Void> initResetPassword(
            @PathVariable("id") UUID id,
            @RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(id, changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(COMPLETE_RESET_PASSWORD)
    public ResponseEntity<Void> completeResetPassword(
            @PathVariable("id") UUID id,
            @RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(id, changePasswordDTO);
        return ResponseEntity.ok().build();
    }

}
