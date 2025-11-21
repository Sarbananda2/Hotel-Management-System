package com.hotel.service;

import com.hotel.dto.LoginRequest;
import com.hotel.dto.LoginResponse;
import com.hotel.dto.RegisterRequest;
import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.security.JwtTokenProvider;
import com.hotel.security.RoleChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RoleChecker roleChecker;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      JwtTokenProvider tokenProvider, RoleChecker roleChecker) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.roleChecker = roleChecker;
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = tokenProvider.generateToken(user.getEmail(), user.getRole(), user.getId());
        
        LoginResponse.UserDto userDto = new LoginResponse.UserDto(
            user.getId(), user.getName(), user.getEmail(), user.getRole()
        );

        return new LoginResponse(token, userDto);
    }

    @Transactional
    public User register(RegisterRequest request) {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Only ADMIN can register new users
        if (!roleChecker.hasRole("ADMIN")) {
            throw new RuntimeException("Only ADMIN can register new users");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return userRepository.create(user);
    }
}

