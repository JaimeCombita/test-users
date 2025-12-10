package com.company.users.service;

import com.company.users.dto.LoginRequestDTO;
import com.company.users.dto.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
