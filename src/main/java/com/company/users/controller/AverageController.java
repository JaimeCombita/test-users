package com.company.users.controller;

import com.company.users.dto.AverageRequest;
import com.company.users.dto.AverageResponse;
import com.company.users.service.AverageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/math")
@RequiredArgsConstructor
public class AverageController {

    private final AverageService averageService;

    @PostMapping("/average")
    public ResponseEntity<AverageResponse> calculateAverage(@RequestBody AverageRequest request) {
        AverageResponse resp = averageService.calculateAverage(request);
        if (resp.getError() != null) {
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}
