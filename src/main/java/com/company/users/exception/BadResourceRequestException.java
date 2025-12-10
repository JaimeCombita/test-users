package com.company.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class BadResourceRequestException extends RuntimeException {
    public BadResourceRequestException(String message) {
        super(message);
    }
    public BadResourceRequestException(String message, Throwable cause) {
        super(message,cause);
    }
}
