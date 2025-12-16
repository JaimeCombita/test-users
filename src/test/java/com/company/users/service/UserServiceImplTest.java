package com.company.users.service;

import com.company.users.crosscutting.Roles;
import com.company.users.dto.UserUpdateDTO;
import com.company.users.exception.BadResourceRequestException;
import com.company.users.exception.NoSuchResourceFoundException;
import com.company.users.model.User;
import com.company.users.repository.UserRepository;
import com.company.users.service.impl.UserServiceImpl;
import com.company.users.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtil;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(UUID.randomUUID());
        user.setIdentificationNumber("12345");
        user.setEmail("test@test.com");
        user.setPassword("plainPassword");
        user.setRoles(Set.of(Roles.ROLE_ADMIN));
    }

    @Test
    void createUser_success() {
        when(userRepository.findByIdentificationNumber("12345")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRoles(), Instant.now())).thenReturn("jwtToken");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser(user);

        assertEquals("hashedPassword", created.getPassword());
        assertTrue(created.getIsActive());
        assertNotNull(created.getCreated());
        verify(userRepository).save(created);
    }

    @Test
    void createUser_alreadyExists_throwsException() {
        when(userRepository.findByIdentificationNumber("12345")).thenReturn(Optional.of(user));

        assertThrows(BadResourceRequestException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_success() {
        UUID id = user.getId();
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("Updated Name");
        dto.setEmail("updated@test.com");
        dto.setPassword("newPassword");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(id, dto);

        assertEquals("Updated Name", updated.getName());
        assertEquals("updated@test.com", updated.getEmail());
        assertEquals("hashedNewPassword", updated.getPassword());
        verify(userRepository).save(updated);
    }

    @Test
    void updateUser_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        UserUpdateDTO dto = new UserUpdateDTO();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchResourceFoundException.class, () -> userService.updateUser(id, dto));
    }

    @Test
    void getUserById_success() {
        UUID id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(id);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void getUserById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BadResourceRequestException.class, () -> userService.getUserById(id));
    }

    @Test
    void getAllUsers_success() {

        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        Page<User> users = userService.getAllUsers(PageRequest.of(0, 10));
        assertEquals(1, users.getTotalElements());
        assertEquals(user, users.getContent().get(0));

    }

    @Test
    void deleteAllUsers_success() {
        userService.deleteAllUsers();
        verify(userRepository).deleteAllInBatch();
    }

    @Test
    void deleteUserById_success() {
        UUID id = user.getId();
        userService.deleteUserById(id);
        verify(userRepository).deleteUserById(id);
    }

}
