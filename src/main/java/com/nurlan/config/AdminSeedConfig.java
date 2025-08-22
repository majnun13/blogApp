package com.nurlan.config;

import com.nurlan.enums.Role;
import com.nurlan.models.User;
import com.nurlan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@Configuration
@Profile("dev") // sadece dev'te çalışır
@RequiredArgsConstructor
public class AdminSeedConfig {

    @Value("${app.admin.email:admin@blog.local}")
    private String adminEmail;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:Admin123!}")
    private String adminPassword;

    @Bean
    CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (users.existsByEmail(adminEmail)) return; // idempotent

            User admin = User.builder()
                    .firstName("adminName")
                    .lastName("adminLastName")
                    .createdDate(new Date())
                    .birthOfDate(new Date(105, 4, 14))
                    .email(adminEmail)
                    .username(adminUsername)
                    .password(encoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .emailVerified(true)
                    .build();

            users.save(admin);
            System.out.println("✅ Admin oluşturuldu: " + adminEmail);
        };
    }
}
