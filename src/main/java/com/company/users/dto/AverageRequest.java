package com.company.users.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AverageRequest {
    private List<BigDecimal> numbers;
}

