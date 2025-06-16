package org.webproject.shedulebot.service;

import org.springframework.stereotype.Service;
import org.webproject.shedulebot.dto.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final Map<Long, List<Task>> userTasks = new ConcurrentHashMap<>();
    private final AtomicLong taskIdGenerator = new AtomicLong(0);

    public List<Task> getUserTasks(long chatId) {
        return userTasks.getOrDefault(chatId, Collections.emptyList());
    }

    public Task createTask(long chatId, LocalDateTime dateTime, String description) {
        Task task = new Task();
        task.setTaskId(taskIdGenerator.incrementAndGet());
        task.setDateTime(dateTime);
        task.setDescription(description);

        userTasks.computeIfAbsent(chatId, k -> new ArrayList<>()).add(task);
        return task;
    }

    public boolean updateTask(long chatId, long taskId, LocalDateTime newDateTime, String newDescription) {
        List<Task> tasks = userTasks.get(chatId);
        if (tasks == null) return false;

        return findTaskById(chatId,taskId)
                .map(task -> {
                    task.setDateTime(newDateTime);
                    task.setDescription(newDescription);
                    return true;
                })
                .orElse(false);
    }

    public boolean deleteTask(long chatId, long taskId) {
        List<Task> tasks = userTasks.get(chatId);
        if (tasks == null) return false;

        return tasks.removeIf(t -> t.getTaskId() == taskId);
    }

    public Optional<Task> findTaskById(long chatId, long taskId) {
        List<Task> tasks = userTasks.get(chatId);
        if (tasks == null) return Optional.empty();

        return tasks.stream()
                .filter(t -> t.getTaskId() == taskId)
                .findFirst();
    }
}