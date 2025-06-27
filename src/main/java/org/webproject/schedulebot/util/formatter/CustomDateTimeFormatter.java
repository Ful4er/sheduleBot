package org.webproject.schedulebot.util.formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class CustomDateTimeFormatter {
    private final DateTimeFormatter formatter;

    public CustomDateTimeFormatter() {
        this.formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                .withLocale(Locale.forLanguageTag("ru"));
    }

    public LocalDateTime parse(String dateTime) throws DateTimeParseException {
        return LocalDateTime.parse(dateTime, formatter);
    }

    public String format(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }
}