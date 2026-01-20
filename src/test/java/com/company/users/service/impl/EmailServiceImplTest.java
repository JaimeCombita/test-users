package com.company.users.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailServiceImpl emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(mailSender);
        // establecer el 'from' como si viniera de application.properties
        ReflectionTestUtils.setField(emailService, "from", "no-reply@test.com");
    }

    @Test
    void sendPasswordRecoveryEmail_sendsCorrectMessage() {
        String to = "user@example.com";
        String token = "abc123";

        // Act
        emailService.sendPasswordRecoveryEmail(to, token);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sent = messageCaptor.getValue();

        assertNotNull(sent);
        assertEquals("no-reply@test.com", sent.getFrom());
        assertArrayEquals(new String[]{to}, sent.getTo());
        assertNotNull(sent.getSubject());
        assertTrue(sent.getSubject().toLowerCase().contains("recuperación") || sent.getSubject().toLowerCase().contains("recuperacion"));
        assertNotNull(sent.getText());
        assertTrue(sent.getText().contains(token), "El cuerpo del email debe contener el token");
        assertTrue(sent.getText().contains("http://localhost:8080"), "El cuerpo del email debe contener el enlace de recuperación");
    }

    @Test
    void sendPasswordRecoveryEmail_nullFromStillSends() {
        // dejar from en null para comprobar que no lanza excepción y se invoca mailSender
        ReflectionTestUtils.setField(emailService, "from", null);

        String to = "user2@example.com";
        String token = "xyz789";

        emailService.sendPasswordRecoveryEmail(to, token);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

}

