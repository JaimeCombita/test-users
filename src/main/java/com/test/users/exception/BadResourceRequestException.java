package com.test.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadResourceRequestException extends RuntimeException {
    public BadResourceRequestException(String message) {
        super(message);
    }
}
