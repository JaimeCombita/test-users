package com.company.users.crosscutting;

public enum ErrorMessage {
    USER_NOT_FOUND("User not found"),
    INVALID_CREDENTIALS("Invalid Credentials"),
    USER_NOT_ACTIVE("User is not active"),
    USER_WITH_SAME_IDENTIFICATION_NUMBER("User with same Identification Number exists"),
    CAN_ONLY_ACCESS_YOu_OWN_INFO("You can only access your own information"),
    USER_SESSION_ACTIVE("The user already has an active session and multiple sessions are not allowed"),
    REFRESH_TOKEN_ID_NOT_FOUND("Refresh token not found"),
    REFRESH_TOKEN_INVALID("Refresh token invalid"),
    REFRESH_TOKEN_EXPIRED_OR_REVOKED("Refresh token expired or revoked"),
    USER_WITH_SAME_EMAIL("User with same email already exists: %s"),
    USER_NOT_FOUND_ID("User not found with id: %s");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }


}
