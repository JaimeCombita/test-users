package com.company.users.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Session;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender noopMailSender() {
        return new JavaMailSender() {
            @Override
            public MimeMessage createMimeMessage() {
                return new MimeMessage(Session.getDefaultInstance(new Properties()));
            }

            @Override
            public MimeMessage createMimeMessage(InputStream contentStream) {
                try {
                    return new MimeMessage(Session.getDefaultInstance(new Properties()), contentStream);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void send(MimeMessage mimeMessage) {
                // no-op
                System.out.println("[noop-mail] send(MimeMessage) called");
            }

            @Override
            public void send(MimeMessage... mimeMessages) {
                System.out.println("[noop-mail] send(MimeMessage...) called: count=" + (mimeMessages != null ? mimeMessages.length : 0));
            }

            @Override
            public void send(MimeMessagePreparator mimeMessagePreparator) {
                System.out.println("[noop-mail] send(MimeMessagePreparator) called");
            }

            @Override
            public void send(MimeMessagePreparator... mimeMessagePreparators) {
                System.out.println("[noop-mail] send(MimeMessagePreparator...) called: count=" + (mimeMessagePreparators != null ? mimeMessagePreparators.length : 0));
            }

            @Override
            public void send(SimpleMailMessage simpleMessage) {
                System.out.println("[noop-mail] send(SimpleMailMessage) to=" + Arrays.toString(simpleMessage.getTo()) + " subject=" + simpleMessage.getSubject());
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) {
                System.out.println("[noop-mail] send(SimpleMailMessage...) called: count=" + (simpleMessages != null ? simpleMessages.length : 0));
                if (simpleMessages != null) {
                    for (SimpleMailMessage m : simpleMessages) {
                        send(m);
                    }
                }
            }
        };
    }
}

