package org.webproject.shedulebot.controller;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.webproject.shedulebot.service.MessageService;
import org.webproject.shedulebot.util.CallbackCommands;

public class CommandHandler {
    private final MessageService messageService;

    public CommandHandler(TelegramLongPollingBot bot) {
        this.messageService = new MessageService(bot);
    }

    public void handleUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText().trim();
            long chatId = update.getMessage().getChatId();
            messageService.handleCommand(chatId, text);
        }
    }

    public void handleUpdate(long chatId, String command) {
        messageService.handleCommand(chatId, command);
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        messageService.deleteMessage(chatId, messageId);

        switch (callbackData) {
            case CallbackCommands.MENU_MAIN:
                messageService.mainMenu(chatId);
                break;

            case CallbackCommands.MENU_VIEW_TASKS:
                messageService.sendAllTasks(chatId);
                break;

            case CallbackCommands.MENU_UPDATE_TASK:
                messageService.startUpdateTask(chatId);
                break;

            case CallbackCommands.MENU_DELETE_TASK:
                messageService.startDeleteTask(chatId);
                break;

            case CallbackCommands.MENU_HELP:
                messageService.sendHelp(chatId);
                break;
            case CallbackCommands.MENU_ADD_TASK:
                messageService.startCreateTask(chatId);
                break;
            case CallbackCommands.CANCEL_CREATION:
                messageService.cancelCreation(chatId);
                break;
            default:
                if (callbackData.startsWith("back_")) {
                    String command = callbackData.substring(5);
                    handleUpdate(chatId, command);
                }
        }
    }
}