package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.TaskRequestDTO;
import com.tasktracker.backend.dto.TaskResponseDTO;
import com.tasktracker.backend.exception.ResourceNotFoundException;
import com.tasktracker.backend.exception.UnauthorizedException;
import com.tasktracker.backend.model.Task;
import com.tasktracker.backend.model.Task.TaskStatus;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.repository.TaskRepository;
import com.tasktracker.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<TaskResponseDTO> getAllTasksByUser(UUID userId, String status) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Task> tasks;
        
        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            tasks = taskRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, taskStatus);
        } else {
            tasks = taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        // Convert to DTOs
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDTO getTaskById(UUID taskId, UUID userId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or you don't have access"));

        return convertToDTO(task);
    }

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO request, UUID userId) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create new task
        Task task = new Task();
        task.setUser(user);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.TODO);

        // Save and return
        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    @Override
    public TaskResponseDTO updateTask(UUID taskId, TaskRequestDTO request, UUID userId) {
        // Find task and verify ownership
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or you don't have access"));

        // Update fields
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        // Save and return
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    @Override
    public TaskResponseDTO toggleTaskStatus(UUID taskId, UUID userId) {
        // Find task and verify ownership
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or you don't have access"));

        // Toggle status
        task.toggleStatus();

        // Save and return
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    @Override
    public void deleteTask(UUID taskId, UUID userId) {
        // Find task and verify ownership
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or you don't have access"));

        // Delete
        taskRepository.delete(task);
    }

    // Helper method to convert Task entity to DTO
    private TaskResponseDTO convertToDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}