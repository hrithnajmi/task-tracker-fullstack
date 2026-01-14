package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.TaskRequestDTO;
import com.tasktracker.backend.dto.TaskResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<TaskResponseDTO> getAllTasksByUser(UUID userId, String status);
    TaskResponseDTO getTaskById(UUID taskId, UUID userId);
    TaskResponseDTO createTask(TaskRequestDTO request, UUID userId);
    TaskResponseDTO updateTask(UUID taskId, TaskRequestDTO request, UUID userId);
    TaskResponseDTO toggleTaskStatus(UUID taskId, UUID userId);
    void deleteTask(UUID taskId, UUID userId);
}