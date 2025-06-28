package org.webproject.schedulebot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.webproject.schedulebot.util.builder.KeyboardMarkupBuilder;
import org.webproject.schedulebot.util.factory.ButtonFactory;
import org.webproject.schedulebot.util.constant.CallbackCommands;

public class NotificationMessageService {
    private final MessageSenderService messageSender;

    public NotificationMessageService(MessageSenderService messageSender) {
        this.messageSender = messageSender;
    }

    public void sendUpcomingNotification(Long chatId, String messageText, Long taskId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText);

        InlineKeyboardMarkup markup = KeyboardMarkupBuilder.create()
                .addRow(
                        ButtonFactory.createCompleteButton(taskId),
                        ButtonFactory.createBackButton(CallbackCommands.MENU_MAIN)
                )
                .build();

        message.setReplyMarkup(markup);
        messageSender.sendMessage(message);
    }

    public void sendExpiredNotification(Long chatId, String messageText, Long taskId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText);
        message.setParseMode("HTML");

        InlineKeyboardMarkup markup = KeyboardMarkupBuilder.create()
                .addRow(
                        ButtonFactory.createPostponeButton(taskId),
                        ButtonFactory.createCompleteButton(taskId)
                )
                .build();

        message.setReplyMarkup(markup);
        messageSender.sendMessage(message);
    }
}