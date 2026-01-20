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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

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

    @Test
    void getUsers_success() throws Exception {
        Page<UserResponseDTO> page = new PageImpl<>(List.of(responseDTO));
        Mockito.when(userService.getAllUsers(any(Pageable.class), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("test@test.com"));
    }

    @Test
    void deleteAllUsers_success() throws Exception {
        Mockito.doNothing().when(userService).deleteAllUsers();

        mockMvc.perform(delete("/api/v1/users"))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePassword_success() throws Exception {
        UUID id = user.getId();
        var dto = new com.company.users.dto.ChangePasswordDTO("currentPass", "newPass");

        Mockito.doNothing().when(userService).changePassword(eq(id), any());

        mockMvc.perform(put("/api/v1/user/{id}/change-password", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void resetPassword_success() throws Exception {
        var req = new com.company.users.dto.PasswordRecoveryRequest(user.getEmail());

        Mockito.doNothing().when(userService).resetPassword(eq(user.getEmail()));

        mockMvc.perform(put("/api/v1/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void recoveryPassword_success() throws Exception {
        var dto = new com.company.users.dto.RecoveryPasswordDTO("token123", "newPass");

        Mockito.doNothing().when(userService).recoveryPassword(any());

        mockMvc.perform(put("/api/v1/user/recovery-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
