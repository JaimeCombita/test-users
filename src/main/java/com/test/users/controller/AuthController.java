package com.test.users.controller;

import com.test.users.dto.LoginRequestDTO;
import com.test.users.dto.LoginResponseDTO;
import com.test.users.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.test.users.crosscutting.ResourceEndpoint.*;
import static com.test.users.crosscutting.ResourceEndpoint.CONSUMES_TYPE_JSON;

@RestController
@RequestMapping(API_VERSION)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = LOGIN, consumes = CONSUMES_TYPE_JSON, produces = CONSUMES_TYPE_JSON)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = authService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }
}
