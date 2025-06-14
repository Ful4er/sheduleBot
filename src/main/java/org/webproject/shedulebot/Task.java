package org.webproject.shedulebot;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
public class Task {
    private long taskId;
    private LocalDateTime dateTime;
    private String description;
    private boolean withNotification;

    DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withLocale(Locale.forLanguageTag("ru"));

    @Override
    public String toString(){
        return "Task №"+taskId+" "+ dateTime.format(FMT)+" - "+description;
    }
    public String saveTaskEvent(){
        return String.format("Задача успешна сохранена!\n\n№%d\n%s\n%s", taskId, dateTime.format(FMT),  description);
    }

}
