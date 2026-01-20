package com.company.users.exception;

import com.company.users.crosscutting.ErrorCode;

public class RecoveryTokenException extends RuntimeException {
    private final ErrorCode errorCode;

    public RecoveryTokenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

