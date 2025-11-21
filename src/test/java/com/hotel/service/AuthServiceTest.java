package com.hotel.service;

import com.hotel.dto.LoginRequest;
import com.hotel.dto.LoginResponse;
import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.security.JwtTokenProvider;
import com.hotel.security.RoleChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenProvider tokenProvider;
    
    @Mock
    private RoleChecker roleChecker;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, tokenProvider, roleChecker);
    }

    @Test
    void testLoginSuccess() {
        User user = new User(1, "Test User", "test@example.com", 
                           "$2a$10$hashed", "ADMIN", OffsetDateTime.now());
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPasswordHash())).thenReturn(true);
        when(tokenProvider.generateToken(anyString(), anyString(), any())).thenReturn("test-token");

        LoginRequest request = new LoginRequest("test@example.com", "password123");
        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        assertNotNull(response.getUser());
    }

    @Test
    void testLoginInvalidEmail() {
        when(userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("invalid@example.com", "password123");
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void testLoginInvalidPassword() {
        User user = new User(1, "Test User", "test@example.com", 
                           "$2a$10$hashed", "ADMIN", OffsetDateTime.now());
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", user.getPasswordHash())).thenReturn(false);

        LoginRequest request = new LoginRequest("test@example.com", "wrongpassword");
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}

