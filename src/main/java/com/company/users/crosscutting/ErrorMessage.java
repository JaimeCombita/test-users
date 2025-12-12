package com.company.users.crosscutting;

public enum ErrorMessage {
    USER_NOT_FOUND("User not found"),
    INVALID_CREDENTIALS("Invalid Credentials"),
    USER_NOT_ACTIVE("User is not active"),
    USER_WITH_SAME_IDENTIFICATION_NUMBER("User with same Identification Number exists"),
    CAN_ONLY_ACCESS_YOY_OWN_INFO("You can only access your own information"),
    USER_SESSION_ACTIVE("The user already has an active session and multiple sessions are not allowed");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
