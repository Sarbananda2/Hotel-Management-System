package com.hotel.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("frontdesk123: " + encoder.encode("frontdesk123"));
        System.out.println("housekeeping123: " + encoder.encode("housekeeping123"));
    }
}

