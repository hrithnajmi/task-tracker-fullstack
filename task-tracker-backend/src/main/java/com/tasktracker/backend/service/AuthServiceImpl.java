package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.AuthResponseDTO;
import com.tasktracker.backend.dto.LoginRequestDTO;
import com.tasktracker.backend.dto.RegisterRequestDTO;
import com.tasktracker.backend.exception.DuplicateResourceException;
import com.tasktracker.backend.exception.UnauthorizedException;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Create new user with hashed password
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Save user to database
        User savedUser = userRepository.save(user);

        // Return response (without password)
        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                "User registered successfully"
        );
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        // Return response (without password)
        return new AuthResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                "Login successful"
        );
    }
}