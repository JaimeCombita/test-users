package com.company.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageResponse {
    private BigDecimal average;
    private Integer count;
    private String error;

    public static AverageResponse ok(BigDecimal average, Integer count) {
        return new AverageResponse(average, count, null);
    }
    public static AverageResponse error(String message) {
        return new AverageResponse(null, null, message);
    }
}

