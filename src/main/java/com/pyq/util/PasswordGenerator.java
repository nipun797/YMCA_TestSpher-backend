package com.pyq.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public static void main(String[] args) {
        String plainPassword = "admin123"; // Change this to your desired admin password
        String hashedPassword = passwordEncoder.encode(plainPassword);
        
        System.out.println("Plain password: " + plainPassword);
        System.out.println("BCrypt hash: " + hashedPassword);
        
        // Verify the password
        boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
        System.out.println("Password verification: " + matches);
    }
    
    public static String generateHash(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
}
