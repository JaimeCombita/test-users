package com.company.users.crosscutting;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid Credentials"),
    USER_NOT_ACTIVE(HttpStatus.FORBIDDEN, "User is not active"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed"),
    MALFORMED_JSON(HttpStatus.BAD_REQUEST, "Malformed JSON request"),
    INVALID_DATA_ACCESS(HttpStatus.BAD_REQUEST, "Invalid data access"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

