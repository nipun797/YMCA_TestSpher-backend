package com.pyq.util;

import com.pyq.entity.User;
import com.pyq.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user exists
        if (!userRepository.existsByEmail("admin@pyq.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@pyq.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user created successfully!");
        } else {
            // Update existing admin password
            User admin = userRepository.findByEmail("admin@pyq.com").orElse(null);
            if (admin != null) {
                admin.setPassword(passwordEncoder.encode("admin123"));
                userRepository.save(admin);
                System.out.println("Admin password updated successfully!");
            }
        }
    }
}
