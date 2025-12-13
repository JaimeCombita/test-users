package com.company.users.crosscutting;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "User not found"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "E002", "Invalid Credentials"),
    USER_NOT_ACTIVE(HttpStatus.FORBIDDEN, "E003", "User is not active"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "E004", "Validation failed"),
    MALFORMED_JSON(HttpStatus.BAD_REQUEST, "E005", "Malformed JSON request"),
    INVALID_DATA_ACCESS(HttpStatus.BAD_REQUEST, "E006", "Invalid data access"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E007", "Internal server error"),
    REFRESH_TOKEN_NOT_FOUND_IN_COOKIE(HttpStatus.FORBIDDEN, "E008", "Refresh token not found in the cookie");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}


