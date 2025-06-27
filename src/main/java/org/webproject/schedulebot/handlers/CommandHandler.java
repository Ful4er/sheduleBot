package org.webproject.schedulebot.handlers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.webproject.schedulebot.service.MessageSenderService;
import org.webproject.schedulebot.service.MessageService;
import org.webproject.schedulebot.service.TaskService;
import org.webproject.schedulebot.service.UserStatesService;
import org.webproject.schedulebot.util.constant.CallbackCommands;
import org.webproject.schedulebot.util.formatter.CustomDateTimeFormatter;

public class CommandHandler {
    private final MessageService messageService;
    private final MessageSenderService messageSenderService;

    public CommandHandler(TelegramLongPollingBot bot,
                          TaskService taskService,
                          CustomDateTimeFormatter customDateTimeFormatter,
                          UserStatesService userStatesService) {
        this.messageSenderService = new MessageSenderService(bot);
        this.messageService = new MessageService(messageSenderService, taskService,
                customDateTimeFormatter, userStatesService);
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
        messageSenderService.deleteMessage(chatId, messageId);

        switch (callbackData) {
            case CallbackCommands.MENU_MAIN -> messageService.mainMenu(chatId);
            case CallbackCommands.MENU_VIEW_TASKS -> messageService.sendAllTasks(chatId);
            case CallbackCommands.MENU_UPDATE_TASK -> messageService.startUpdateTask(chatId);
            case CallbackCommands.MENU_DELETE_TASK -> messageService.startDeleteTask(chatId);
            case CallbackCommands.MENU_HELP -> messageService.sendHelp(chatId);
            case CallbackCommands.MENU_ADD_TASK -> messageService.startCreateTask(chatId);
            case CallbackCommands.CANCEL_CREATION -> messageService.cancelCreation(chatId);
            default -> {
                if (callbackData.startsWith(CallbackCommands.POSTPONE_PREFIX)) {
                    long taskId = Long.parseLong(callbackData.substring(CallbackCommands.POSTPONE_PREFIX.length()));
                    messageService.postponeTask(chatId, taskId);
                } else if (callbackData.startsWith(CallbackCommands.COMPLETE_PREFIX)) {
                    long taskId = Long.parseLong(callbackData.substring(CallbackCommands.COMPLETE_PREFIX.length()));
                    messageService.completeTask(chatId, taskId);
                } else if (callbackData.startsWith("back_")) {
                    String command = callbackData.substring(5);
                    handleUpdate(chatId, command);
                }
            }
        }
    }
}