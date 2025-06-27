package org.webproject.schedulebot.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webproject.schedulebot.dto.TaskDTO;
import org.webproject.schedulebot.entity.Task;
import org.webproject.schedulebot.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskDTO> getUserTasks(long chatId) {
        return taskRepository.findByChatId(chatId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TaskDTO createTask(long chatId, LocalDateTime dateTime, String description) {
        Long maxTaskId = taskRepository.findMaxTaskIdByChatId(chatId)
                .orElse(0L);

        Task task = new Task();
        task.setChatId(chatId);
        task.setTaskId(maxTaskId + 1);
        task.setDateTime(dateTime);
        task.setDescription(description);

        Task savedTask = taskRepository.save(task);
        return convertToDto(savedTask);
    }

    public boolean updateTask(long chatId, long taskId, LocalDateTime newDateTime, String newDescription) {
        Optional<Task> taskOpt = taskRepository.findByTaskIdAndChatId(taskId, chatId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setDateTime(newDateTime);
            task.setDescription(newDescription);
            task.setNotified(false);
            taskRepository.save(task);
            return true;
        }
        return false;
    }

    public boolean deleteTask(long chatId, long taskId) {
        if (taskRepository.existsByTaskIdAndChatId(taskId, chatId)) {
            taskRepository.deleteByTaskIdAndChatId(taskId, chatId);
            return true;
        }
        return false;
    }

    public Optional<TaskDTO> findTaskById(long chatId, long taskId) {
        return taskRepository.findByTaskIdAndChatId(taskId, chatId)
                .map(this::convertToDto);
    }

    private TaskDTO convertToDto(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskId(task.getTaskId());
        dto.setDateTime(task.getDateTime());
        dto.setDescription(task.getDescription());
        return dto;
    }
}