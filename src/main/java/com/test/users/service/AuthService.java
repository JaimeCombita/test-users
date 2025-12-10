package com.test.users.service;

import com.test.users.dto.LoginRequestDTO;
import com.test.users.dto.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
