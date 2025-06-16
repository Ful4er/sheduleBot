package org.webproject.shedulebot.dto;

import lombok.Data;
import org.webproject.shedulebot.util.CustomDateTimeFormatter;

import java.time.LocalDateTime;

@Data
public class Task {
    private long taskId;
    private LocalDateTime dateTime;
    private String description;
    private boolean withNotification;

    private static final CustomDateTimeFormatter formatter = new CustomDateTimeFormatter();

    @Override
    public String toString() {
        return String.format("Задача №%d - %s - %s",
                taskId,
                formatter.format(dateTime),
                description);
    }

    public String saveTaskEvent() {
        return String.format("Задача успешно сохранена!\n\n№%d\n%s\n%s",
                taskId,
                formatter.format(dateTime),
                description);
    }
}