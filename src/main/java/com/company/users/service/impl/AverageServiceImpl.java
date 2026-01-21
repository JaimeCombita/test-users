package com.company.users.service.impl;

import com.company.users.dto.AverageRequest;
import com.company.users.dto.AverageResponse;
import com.company.users.service.AverageService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AverageServiceImpl implements AverageService {
    @Override
    public AverageResponse calculateAverage(AverageRequest request) {
        List<BigDecimal> numbers = request.getNumbers();
        if (numbers == null || numbers.isEmpty()) {
            return AverageResponse.error("numbers array must not be empty");
        }
        BigDecimal sum = numbers.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avg = sum.divide(BigDecimal.valueOf(numbers.size()), 6, RoundingMode.HALF_UP);
        return AverageResponse.ok(avg, numbers.size());
    }
}

