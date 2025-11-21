package com.hotel.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptHashGeneratorTest {
    @Test
    public void generateHashes() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        
        System.out.println("=== BCrypt Hashes for Seed Data ===");
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("frontdesk123: " + encoder.encode("frontdesk123"));
        System.out.println("housekeeping123: " + encoder.encode("housekeeping123"));
    }
}

