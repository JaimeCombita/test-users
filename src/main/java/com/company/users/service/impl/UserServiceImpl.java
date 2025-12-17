package com.company.users.service.impl;

import com.company.users.crosscutting.ErrorCode;
import com.company.users.crosscutting.ErrorMessage;
import com.company.users.crosscutting.Roles;
import com.company.users.dto.ChangePasswordDTO;
import com.company.users.dto.UserRequestDTO;
import com.company.users.dto.UserResponseDTO;
import com.company.users.dto.UserUpdateDTO;
import com.company.users.especifications.UserSpecifications;
import com.company.users.exception.BadCredentialsException;
import com.company.users.exception.BadResourceRequestException;
import com.company.users.exception.NoSuchResourceFoundException;
import com.company.users.exception.RecoveryTokenException;
import com.company.users.mapper.UserMapper;
import com.company.users.model.PasswordRecoveryToken;
import com.company.users.model.User;
import com.company.users.repository.PasswordRecoveryTokenRepository;
import com.company.users.repository.UserRepository;
import com.company.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final PasswordRecoveryTokenRepository passwordRecoveryTokenRepository;

    @Transactional
    @Override
    public UserResponseDTO createUser(UserRequestDTO userDto) {

        User user = userMapper.toEntity(userDto);

        if (userRepository.existsByIdentificationNumber(user.getIdentificationNumber())) {
            throw new BadResourceRequestException(ErrorMessage.USER_WITH_SAME_IDENTIFICATION_NUMBER.getMessage());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRol(Roles.ROLE_USER);
        user.setIsActive(Boolean.TRUE);
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserResponseDTO updateUser(UUID id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchResourceFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        Optional.ofNullable(userUpdateDTO.email()).ifPresent(email -> {
            if (userRepository.existsByEmail(email) && !user.getEmail().equals(email)) {
                throw new BadResourceRequestException(ErrorMessage.USER_WITH_SAME_EMAIL.getMessage());
            }
            user.setEmail(email);
        });

        userMapper.updateUserFromDto(userUpdateDTO, user);
        return userMapper.toUserResponseDto(userRepository.save(user));
    }


    @Transactional(readOnly = true)
    @Override
    public UserResponseDTO getUserById(UUID id) {
        return userMapper.toUserResponseDto(userRepository.findById(id)
                .orElseThrow(() -> new NoSuchResourceFoundException(ErrorMessage.USER_NOT_FOUND.getMessage())));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserResponseDTO> getAllUsers(Pageable pageable,
                                             String name,
                                             String email,
                                             Boolean isActive,
                                             Roles role,
                                             LocalDateTime createdFrom,
                                             LocalDateTime createdTo,
                                             LocalDateTime modifiedFrom,
                                             LocalDateTime modifiedTo) {
        Specification<User> spec = Specification.where(UserSpecifications.hasNameLike(name))
                .and(UserSpecifications.hasEmailLike(email))
                .and(UserSpecifications.isActive(isActive))
                .and(UserSpecifications.hasRole(role))
                .and(UserSpecifications.createdBetween(createdFrom, createdTo))
                .and(UserSpecifications.modifiedBetween(modifiedFrom, modifiedTo));

        return userRepository.findAll(spec, pageable)
                .map(userMapper::toUserResponseDto);

    }

    @Transactional
    @Override
    public void deleteAllUsers() {
        userRepository.deleteAllInBatch();
    }

    @Transactional
    @Override
    public void deleteUserById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchResourceFoundException(ErrorMessage.USER_NOT_FOUND_ID.format(id));
        }
        userRepository.deleteById(id);

   }

    @Transactional
    @Override
    public void changePassword(UUID userId, ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchResourceFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(changePasswordDTO.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException(ErrorMessage.INVALID_CREDENTIALS.getMessage());
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.newPassword()));
        userRepository.save(user);
    }

    @Override
    public void initiatePasswordRecovery(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchResourceFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        String recoveryToken = UUID.randomUUID().toString();

        PasswordRecoveryToken tokenEntity = PasswordRecoveryToken.builder()
                .user(user)
                .token(recoveryToken)
                .expirationDate(Instant.now().plus(Duration.ofHours(1)))
                .isUsed(false)
                .build();
        passwordRecoveryTokenRepository.save(tokenEntity);

        //emailService.sendPasswordRecoveryEmail(user.getEmail(), recoveryToken);
    }

    @Override
    public void completePasswordRecovery(String token, String newPassword) {
        PasswordRecoveryToken passwordRecoveryTokenEntity = passwordRecoveryTokenRepository.findByToken(token)
                .orElseThrow(() -> new RecoveryTokenException(ErrorCode.RECOVERY_TOKEN_NOT_FOUND.getMessage()));

        if (passwordRecoveryTokenEntity.getIsUsed() || Instant.now().isAfter(passwordRecoveryTokenEntity.getExpirationDate())) {
            throw new RecoveryTokenException(ErrorCode.RECOVERY_TOKEN_EXPIRED.getMessage());
        }

        User user = passwordRecoveryTokenEntity.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordRecoveryTokenEntity.setIsUsed(true);
        passwordRecoveryTokenRepository.save(passwordRecoveryTokenEntity);
    }

}
