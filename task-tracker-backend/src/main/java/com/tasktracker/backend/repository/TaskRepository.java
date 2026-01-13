package com.tasktracker.backend.repository;

import com.tasktracker.backend.model.Task;
import com.tasktracker.backend.model.Task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
    // Find all tasks by user ID
    List<Task> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    // Find tasks by user ID and status
    List<Task> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, TaskStatus status);
    
    // Find task by ID and user ID (for ownership verification)
    Optional<Task> findByIdAndUserId(UUID taskId, UUID userId);
    
    // Count tasks by user ID
    long countByUserId(UUID userId);
}