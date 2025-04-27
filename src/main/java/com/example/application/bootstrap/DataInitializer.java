package com.example.application.bootstrap;

import com.example.application.data.User;
import com.example.application.data.UserRepository;
import com.example.application.data.UserType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadAdmin(UserRepository userRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@example.com";
            // Jos adminia ei vielä ole, luodaan se
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setFirstName("Admin");
                admin.setLastName("Admin");
                // Vaihda salasana mieleiseksi, nyt esimerkkinä ChangeMe123!
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setUserType(UserType.ADMIN);
                userRepository.save(admin);
                System.out.println("🔐 Admin user created: " + adminEmail);
            }
        };
    }
}
