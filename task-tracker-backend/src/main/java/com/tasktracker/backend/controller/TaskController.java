package com.tasktracker.backend.controller;

import com.tasktracker.backend.dto.TaskRequestDTO;
import com.tasktracker.backend.dto.TaskResponseDTO;
import com.tasktracker.backend.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(required = false) String status) {
        List<TaskResponseDTO> tasks = taskService.getAllTasksByUser(userId, status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        TaskResponseDTO task = taskService.getTaskById(id, userId);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO request,
            @RequestHeader("X-User-Id") UUID userId) {
        TaskResponseDTO task = taskService.createTask(request, userId);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody TaskRequestDTO request,
            @RequestHeader("X-User-Id") UUID userId) {
        TaskResponseDTO task = taskService.updateTask(id, request, userId);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> toggleTaskStatus(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        TaskResponseDTO task = taskService.toggleTaskStatus(id, userId);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }
}