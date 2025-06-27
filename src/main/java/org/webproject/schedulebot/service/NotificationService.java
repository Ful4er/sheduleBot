package org.webproject.schedulebot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.webproject.schedulebot.entity.Task;
import org.webproject.schedulebot.repository.TaskRepository;
import org.webproject.schedulebot.util.constant.MessageTexts;
import org.webproject.schedulebot.util.formatter.CustomDateTimeFormatter;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final TaskRepository taskRepository;
    private final NotificationMessageService notificationMessageService;
    private final CustomDateTimeFormatter formatter;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void processTasks() {
        LocalDateTime now = LocalDateTime.now();
        processUpcomingTasks(now);
        processExpiredTasks(now);
    }

    private void processUpcomingTasks(LocalDateTime now) {
        LocalDateTime oneHourLater = now.plusHours(1);
        List<Task> upcomingTasks = taskRepository.findByDateTimeBetweenAndNotifiedFalse(now, oneHourLater);

        if (upcomingTasks.isEmpty()) {
            log.debug("No upcoming tasks found for notification");
            return;
        }

        log.info("Found tasks for notification: {}", upcomingTasks.size());
        upcomingTasks.forEach(this::sendReminder);
    }

    private void processExpiredTasks(LocalDateTime now) {
        List<Task> expiredTasks = taskRepository.findExpiredTasks(now);

        if (expiredTasks.isEmpty()) {
            log.debug("No expired tasks found");
            return;
        }

        log.info("Found expired tasks: {}", expiredTasks.size());
        expiredTasks.forEach(this::notifyAndDeleteTask);
    }

    private void sendReminder(Task task) {
        try {
            String message = String.format(
                    MessageTexts.REMINDER_TASK_FORMAT,
                    task.getDescription(),
                    formatter.format(task.getDateTime())
            );
            notificationMessageService.sendUpcomingNotification(
                    task.getChatId(),
                    message,
                    task.getTaskId()
            );
            task.setNotified(true);
            taskRepository.save(task);
        } catch (TelegramApiException e) {
            log.error("Error sending reminder: {}", e.getMessage());
        }
    }

    private void notifyAndDeleteTask(Task task) {
        try {
            String message = String.format(
                    MessageTexts.TASK_AUTO_DELETED_FORMAT,
                    task.getDescription(),
                    formatter.format(task.getDateTime())
            );
            notificationMessageService.sendExpiredNotification(
                    task.getChatId(),
                    message,
                    task.getTaskId()
            );
        } catch (TelegramApiException e) {
            log.error("Error sending deletion notification: {}", e.getMessage());
        }
    }
}