package com.company.users.init;

import com.company.users.crosscutting.Role;
import com.company.users.model.User;
import com.company.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args){
        boolean existsAdmin = userRepository.existsByRolesContaining(Role.ROLE_ADMIN);

        if(!existsAdmin){

            User admin = User.builder()
                    .id(UUID.randomUUID())
                    .name("User Admin")
                    .identificationNumber("987654321")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles(Set.of(Role.ROLE_ADMIN))
                    .isActive(Boolean.TRUE)
                    .created(LocalDateTime.now())
                    .modified(LocalDateTime.now())
                    .allowMultisession(Boolean.FALSE)
                    .build();
            userRepository.save(admin);
            System.out.println("Initial User ADMIN Created: "+adminEmail);
        }
    }
}
