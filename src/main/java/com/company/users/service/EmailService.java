package com.company.users.service;

public interface EmailService {
    void sendPasswordRecoveryEmail(String to, String token);
}
