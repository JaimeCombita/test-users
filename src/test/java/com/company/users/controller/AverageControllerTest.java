package com.company.users.controller;

import com.company.users.dto.AverageRequest;
import com.company.users.dto.AverageResponse;
import com.company.users.service.AverageService;
import com.company.users.filter.JwtAuthFilter;
import com.company.users.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AverageController.class)
@AutoConfigureMockMvc(addFilters = false)
class AverageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AverageService averageService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void average_success() throws Exception {
        AverageRequest req = new AverageRequest();
        req.setNumbers(List.of(new BigDecimal("1.5"), new BigDecimal("2.5"), new BigDecimal("3.0")));
        // Mock service response independientemente de la instancia
        when(averageService.calculateAverage(any(AverageRequest.class)))
                .thenReturn(AverageResponse.ok(new BigDecimal("2.333333"), 3));

        mockMvc.perform(post("/api/v1/math/average")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.average").value(2.333333))
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    void average_empty_error() throws Exception {
        AverageRequest req = new AverageRequest();
        req.setNumbers(List.of());
        // Mock service error response independientemente de la instancia
        when(averageService.calculateAverage(any(AverageRequest.class)))
                .thenReturn(AverageResponse.error("numbers array must not be empty"));

        mockMvc.perform(post("/api/v1/math/average")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("numbers array must not be empty"));
    }
}
