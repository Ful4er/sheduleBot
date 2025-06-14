package org.webproject.shedulebot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(SheduleBotApplication.class);
    @Value("${telegram.bot.username}")
    private String username;

    @Value("${telegram.bot.token}")
    private String token;

    @Override
    public String getBotUsername() {return username;}

    @Override
    public String getBotToken() {return token;}

    private final List<Task> tasks = new ArrayList<>();
    DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withLocale(Locale.forLanguageTag("ru"));

    @Override
    public void onUpdateReceived(Update upd) {
        if (upd.hasMessage() && upd.getMessage().hasText()) {
            String text = upd.getMessage().getText().trim();
            long chatId = upd.getMessage().getChatId();
            logger.info(String.format("User(%d): %s",chatId,text));
            try {
                if ("/start".equals(text)) {
                    sendMessage(chatId, "Привет, я бот планировщик!\n/help - узнай какие есть команды");
                } else if ("/alltask".equals(text)) {
                    allTask(chatId, tasks);
                } else if (text.startsWith("/settask")) {
                    setTask(chatId, text);
                } else if (text.startsWith("/updatetask")) {
                } else if (text.equals("/help")) {
                    sendMessage(chatId, "Доступные комманды:\n/settask - создать новую задачу\n/updatetask - обновить задачу\n/alltask - все задачи\n/deletetask - удалить задачу\n/help - все доступные команды");
                } else {
                    sendMessage(chatId, "Неизвестная команда. Используй /help, чтобы узнать какие команды доступны");
                }
            } catch (Exception e) {
                sendMessage(chatId, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void setTask(long chatId, String text) {
        if (text.length() == "/settask".length())
            throw new IllegalArgumentException("\n\nТребуется строчка форматом:\n/start <time> <desсription>\n\nПример:\n/start 31.12.2030 Новый Год");

        String[] parts = text.split("\\s+", 4);
        String datePart = parts[1] + " " + parts[2];
        LocalDateTime date = LocalDateTime.parse(datePart, FMT);
        String description = parts.length > 3 ? parts[3] : "Без описания";

        Task newTask = new Task();
        newTask.setTaskId(tasks.size());
        newTask.setDateTime(date);
        newTask.setDescription(description);
        tasks.add(newTask);

        sendMessage(chatId, newTask.saveTaskEvent());
    }

    public void allTask(long chatId, List<Task> tasks) {
        String allTask = tasks.stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
        sendMessage(chatId, allTask);
        logger.info(String.format("The user has all his tasks provided by ID: %d",chatId));
    }

    private void sendMessage(long chatId, String text) {
        try {
            execute(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(text)
                    .build());
            logger.info(String.format("Message to the user by ID:%d | %s",chatId,text.replaceAll("(\\n)", " ")));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}