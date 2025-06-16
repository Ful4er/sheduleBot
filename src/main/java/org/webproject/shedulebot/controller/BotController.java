package org.webproject.shedulebot.controller;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotController extends TelegramLongPollingBot {
    private final String username;
    private final String token;
    private final CommandHandler commandHandler;

    public BotController(String username, String token) {
        this.username = username;
        this.token = token;
        this.commandHandler = new CommandHandler(this);
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