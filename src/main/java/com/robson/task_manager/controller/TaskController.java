package com.robson.task_manager.controller;

import com.robson.task_manager.model.Task;
import com.robson.task_manager.repository.TaskRepository;
import com.robson.task_manager.exception.ResourceNotFoundException;
import com.robson.task_manager.dto.TaskRequest;
import com.robson.task_manager.dto.TaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    private TaskResponse convertToDto(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }


    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean completed) {


        List<Task> allTasks = taskRepository.findAll(pageable.getSort());

        List<Task> filteredTasks = allTasks.stream()
                .filter(task -> {
                    boolean matchesTitle = (title == null || task.getTitle().toLowerCase().contains(title.toLowerCase()));
                    boolean matchesCompleted = (completed == null || task.isCompleted() == completed);
                    return matchesTitle && matchesCompleted;
                })
                .collect(Collectors.toList());


        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredTasks.size());
        Page<Task> taskPage = new org.springframework.data.domain.PageImpl<>(
                filteredTasks.subList(start, end), pageable, filteredTasks.size()
        );

        Page<TaskResponse> responsePage = taskPage.map(this::convertToDto);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com ID: " + id));
        return ResponseEntity.ok(convertToDto(task));
    }


    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setCompleted(taskRequest.isCompleted());

        Task savedTask = taskRepository.save(task);
        return new ResponseEntity<>(convertToDto(savedTask), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com ID: " + id));

        existingTask.setTitle(taskRequest.getTitle());
        existingTask.setDescription(taskRequest.getDescription());
        existingTask.setCompleted(taskRequest.isCompleted());

        Task updatedTask = taskRepository.save(existingTask);
        return ResponseEntity.ok(convertToDto(updatedTask));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada com ID: " + id);
        }
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
