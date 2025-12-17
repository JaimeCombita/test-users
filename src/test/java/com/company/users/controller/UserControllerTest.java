package com.company.users.controller;

import com.company.users.factory.UserDataFactory;
import com.company.users.model.User;
import com.company.users.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.users.dto.UserRequestDTO;
import com.company.users.dto.UserResponseDTO;
import com.company.users.dto.UserUpdateDTO;
import com.company.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    private ObjectMapper objectMapper;
    private User user;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        user = UserDataFactory.createUser();
        responseDTO = UserDataFactory.createUserResponseDTO(user);
    }

    @Test
    void createUser_success() throws Exception {
        UserRequestDTO requestDTO = UserDataFactory.createUserRequestDTO();

        Mockito.when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void updateUser_success() throws Exception {
        UserUpdateDTO updateDTO = UserDataFactory.createUserUpdateDTO();

        Mockito.when(userService.updateUser(eq(user.getId()), any(UserUpdateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void getUserById_success() throws Exception {
        Mockito.when(userService.getUserById(eq(user.getId()))).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/user/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void deleteUserById_success() throws Exception {
        Mockito.doNothing().when(userService).deleteUserById(eq(user.getId()));

        mockMvc.perform(delete("/api/v1/user/{id}", user.getId()))
                .andExpect(status().isNoContent());
    }
}

