package com.company.users.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableEnvironment env = (ConfigurableEnvironment) event.getApplicationContext().getEnvironment();

        String mailUser = env.getProperty("spring.mail.username");
        if (mailUser == null) {
            log.warn("Property 'spring.mail.username' is NOT set (null) - application.yml may not be loaded or property missing");
        } else if (mailUser.isBlank()) {
            log.warn("Property 'spring.mail.username' is set but empty");
        } else {
            String masked = mailUser.length() <= 1 ? "*" : mailUser.charAt(0) + "***" + mailUser.charAt(mailUser.length() - 1);
            log.info("Property 'spring.mail.username' is present (masked)={} length={}", masked, mailUser.length());
        }

        log.info("Listing property sources (order matters):");
        for (PropertySource<?> ps : env.getPropertySources()) {
            try {
                log.info(" - {}", ps.getName());
            } catch (Exception e) {
                log.info(" - {} (error reading name)", ps);
            }
        }
    }
}

