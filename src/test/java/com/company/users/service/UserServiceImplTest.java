package com.company.users.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.company.users.dto.ChangePasswordDTO;
import com.company.users.dto.RecoveryPasswordDTO;
import com.company.users.model.PasswordRecoveryToken;
import com.company.users.repository.PasswordRecoveryTokenRepository;
import com.company.users.exception.RecoveryTokenException;
import com.company.users.exception.BadCredentialsException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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

    // Mocks necesarios para las nuevas pruebas
    @Mock
    private PasswordRecoveryTokenRepository passwordRecoveryTokenRepository;

    @Mock
    private com.company.users.service.EmailService emailService;

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

    // Nuevas pruebas para changePassword
    @Test
    void changePassword_success() {
        UUID id = user.getId();
        ChangePasswordDTO dto = new ChangePasswordDTO("plainPassword", "newPassword");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.currentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(dto.newPassword())).thenReturn("newHashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.changePassword(id, dto);

        verify(userRepository).save(user);
        assertEquals("newHashedPassword", user.getPassword());
    }

    @Test
    void changePassword_invalidCurrent_throwsException() {
        UUID id = user.getId();
        ChangePasswordDTO dto = new ChangePasswordDTO("wrongPassword", "newPassword");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.currentPassword(), user.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.changePassword(id, dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_userNotFound_throwsException() {
        UUID id = UUID.randomUUID();
        ChangePasswordDTO dto = new ChangePasswordDTO("any", "anyNew");

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchResourceFoundException.class, () -> userService.changePassword(id, dto));
        verify(passwordEncoder, never()).matches(any(), any());
        verify(userRepository, never()).save(any());
    }

    // Nuevas pruebas para resetPassword
    @Test
    void resetPassword_success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordRecoveryTokenRepository.save(any(PasswordRecoveryToken.class))).thenAnswer(i -> i.getArgument(0));

        userService.resetPassword(user.getEmail());

        verify(passwordRecoveryTokenRepository).save(any(PasswordRecoveryToken.class));
        verify(emailService).sendPasswordRecoveryEmail(eq(user.getEmail()), any(String.class));
    }

    @Test
    void resetPassword_userNotFound_throwsException() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(NoSuchResourceFoundException.class, () -> userService.resetPassword("unknown@test.com"));
        verify(passwordRecoveryTokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordRecoveryEmail(any(), any());
    }

    // Nuevas pruebas para recoveryPassword
    @Test
    void recoveryPassword_success() {
        String token = "token123";
        RecoveryPasswordDTO dto = new RecoveryPasswordDTO(token, "recoveredPass");

        PasswordRecoveryToken tokenEntity = PasswordRecoveryToken.builder()
                .user(user)
                .token(token)
                .expirationDate(Instant.now().plus(Duration.ofHours(1)))
                .isUsed(false)
                .build();

        when(passwordRecoveryTokenRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));
        when(passwordEncoder.encode(dto.newPassword())).thenReturn("encodedRecovered");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(passwordRecoveryTokenRepository.save(any(PasswordRecoveryToken.class))).thenAnswer(i -> i.getArgument(0));

        userService.recoveryPassword(dto);

        verify(userRepository).save(user);
        verify(passwordRecoveryTokenRepository).save(tokenEntity);
        assertEquals("encodedRecovered", user.getPassword());
        assertTrue(tokenEntity.getIsUsed());
    }

    @Test
    void recoveryPassword_tokenNotFound_throwsException() {
        String token = "notexist";
        RecoveryPasswordDTO dto = new RecoveryPasswordDTO(token, "pass");

        when(passwordRecoveryTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(RecoveryTokenException.class, () -> userService.recoveryPassword(dto));
    }

    @Test
    void recoveryPassword_tokenExpiredOrUsed_throwsException() {
        String token = "usedOrExpired";
        RecoveryPasswordDTO dto = new RecoveryPasswordDTO(token, "pass");

        PasswordRecoveryToken tokenEntity = PasswordRecoveryToken.builder()
                .user(user)
                .token(token)
                .expirationDate(Instant.now().minus(Duration.ofHours(2)))
                .isUsed(false)
                .build();

        when(passwordRecoveryTokenRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));

        assertThrows(RecoveryTokenException.class, () -> userService.recoveryPassword(dto));

        // marcar usado para probar la rama 'isUsed'
        tokenEntity.setExpirationDate(Instant.now().plus(Duration.ofHours(1)));
        tokenEntity.setIsUsed(true);
        when(passwordRecoveryTokenRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));

        assertThrows(RecoveryTokenException.class, () -> userService.recoveryPassword(dto));
    }

}
