package com.company.users.service;

import com.company.users.crosscutting.Roles;
import com.company.users.dto.UserRequestDTO;
import com.company.users.dto.UserResponseDTO;
import com.company.users.dto.UserUpdateDTO;
import com.company.users.exception.BadResourceRequestException;
import com.company.users.exception.NoSuchResourceFoundException;
import com.company.users.factory.UserDataFactory;
import com.company.users.mapper.UserMapper;
import com.company.users.model.User;
import com.company.users.repository.UserRepository;
import com.company.users.service.impl.UserServiceImpl;
import com.company.users.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    private User user;
    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        user = UserDataFactory.createUser();
        requestDTO = UserDataFactory.createUserRequestDTO();
        responseDTO = UserDataFactory.createUserResponseDTO(user);
    }

    @Test
    void createUser_success() {
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.existsByIdentificationNumber("12345")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(responseDTO);

        UserResponseDTO created = userService.createUser(requestDTO);

        assertEquals("test@test.com", created.email());
        verify(userRepository).save(user);
    }

    @Test
    void createUser_alreadyExists_throwsException() {
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.existsByIdentificationNumber("12345")).thenReturn(true);

        assertThrows(BadResourceRequestException.class, () -> userService.createUser(requestDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        UserUpdateDTO dto = UserDataFactory.createUserUpdateDTO();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchResourceFoundException.class, () -> userService.updateUser(id, dto));
    }

    @Test
    void deleteUserById_success() {
        UUID id = user.getId();
        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUserById(id);

        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteUserById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(NoSuchResourceFoundException.class, () -> userService.deleteUserById(id));
    }
}