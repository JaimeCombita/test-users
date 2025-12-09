package com.test.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.users.dto.UserRequestDTO;
import com.test.users.dto.UserResponseDTO;
import com.test.users.dto.UserUpdateDTO;
import com.test.users.model.User;
import com.test.users.service.UserService;
import com.test.users.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private ObjectMapper objectMapper;
    private User user;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        user = new User();
        user.setId(UUID.randomUUID());
        user.setIdentificationNumber("12345");
        user.setEmail("test@test.com");
        user.setPassword("hashedPassword");
        user.setToken("jwtToken");

        responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setToken(user.getToken());
    }

    @Test
    void createUser_success() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setIdentificationNumber("12345");
        requestDTO.setName("Test User");
        requestDTO.setEmail("test@test.com");
        requestDTO.setPassword("plainPassword");

        Mockito.when(userMapper.toEntity(any(UserRequestDTO.class))).thenReturn(user);
        Mockito.when(userService.createUser(any(User.class))).thenReturn(user);
        Mockito.when(userMapper.toUserResponseDto(any(User.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/user") // depende de tu constante API_VERSION + USER
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.token").value("jwtToken"));
    }

    @Test
    void updateUser_success() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated Name", "updated@test.com", "newPassword", true);
        user.setName("Updated Name");
        user.setEmail("updated@test.com");

        Mockito.when(userService.updateUser(eq(user.getId()), any(UserUpdateDTO.class))).thenReturn(user);
        Mockito.when(userMapper.toUserResponseDto(any(User.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void getUserById_success() throws Exception {
        Mockito.when(userService.getUserById(eq(user.getId()))).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toUserResponseDto(any(User.class))).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/user/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void getUsers_success() throws Exception {
        Page<User> userPage = new PageImpl<>(List.of(user));
        Mockito.when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);
        Mockito.when(userMapper.toUserResponseDto(user)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("test@test.com"));
    }


    @Test
    void deleteUserById_success() throws Exception {
        Mockito.doNothing().when(userService).deleteUserById(eq(user.getId()));

        mockMvc.perform(delete("/api/v1/user/{id}", user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAllUsers_success() throws Exception {
        Mockito.doNothing().when(userService).deleteAllUsers();

        mockMvc.perform(delete("/api/v1/users"))
                .andExpect(status().isNoContent());
    }

}
