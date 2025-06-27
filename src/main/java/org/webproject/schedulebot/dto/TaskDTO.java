package org.webproject.schedulebot.dto;

import lombok.Data;
import org.webproject.schedulebot.util.formatter.CustomDateTimeFormatter;

import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private long taskId;
    private LocalDateTime dateTime;
    private String description;
    private Boolean notified;

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