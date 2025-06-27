package org.webproject.schedulebot.controller;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.webproject.schedulebot.handlers.CommandHandler;
import org.webproject.schedulebot.service.TaskService;
import org.webproject.schedulebot.service.UserStatesService;
import org.webproject.schedulebot.util.formatter.CustomDateTimeFormatter;

public class BotController extends TelegramLongPollingBot {
    private final String username;
    private final CommandHandler commandHandler;

    public BotController(String username,
                         String token,
                         TaskService taskService,
                         CustomDateTimeFormatter customDateTimeFormatter,
                         UserStatesService userStatesService) {
        super(token);
        this.username = username;
        this.commandHandler = new CommandHandler(this, taskService, customDateTimeFormatter, userStatesService);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        commandHandler.handleUpdate(update);
    }
}