package com.company.users.service;

import com.company.users.dto.AverageRequest;
import com.company.users.dto.AverageResponse;

public interface AverageService {
    AverageResponse calculateAverage(AverageRequest request);
}

