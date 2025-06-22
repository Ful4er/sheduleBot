package org.webproject.shedulebot.controller;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.webproject.shedulebot.service.TaskService;
import org.webproject.shedulebot.util.CustomDateTimeFormatter;

public class BotController extends TelegramLongPollingBot {
    private final String username;
    private final String token;
    private final CommandHandler commandHandler;

    public BotController(String username, String token, TaskService taskService, CustomDateTimeFormatter customDateTimeFormatter) {
        super(token);
        this.username = username;
        this.token = token;
        this.commandHandler = new CommandHandler(this, taskService, customDateTimeFormatter);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        commandHandler.handleUpdate(update);
    }
}