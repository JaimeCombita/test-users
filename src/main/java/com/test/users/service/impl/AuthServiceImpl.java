package com.test.users.service.impl;

import com.test.users.dto.LoginRequestDTO;
import com.test.users.dto.LoginResponseDTO;
import com.test.users.exception.BadCredentialsException;
import com.test.users.exception.NoSuchResourceFoundException;
import com.test.users.mapper.UserMapper;
import com.test.users.model.User;
import com.test.users.repository.UserRepository;
import com.test.users.service.AuthService;
import com.test.users.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("authService")
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO){
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new NoSuchResourceFoundException("User not found"));

        if(!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Invalid Credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRoles());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());

        user.setToken(refreshToken);
        userRepository.save(user);

        LoginResponseDTO loginResponseDTO = userMapper.toLoginResponseDto(user);
        loginResponseDTO.setToken(accessToken);

        return loginResponseDTO;
    }
}
