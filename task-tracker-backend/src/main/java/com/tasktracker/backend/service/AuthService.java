package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.AuthResponseDTO;
import com.tasktracker.backend.dto.LoginRequestDTO;
import com.tasktracker.backend.dto.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
}