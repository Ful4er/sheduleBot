package org.webproject.schedulebot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class MessageSenderService {
    private final TelegramLongPollingBot bot;
    private final Map<Long, Integer> lastMessageIds = new ConcurrentHashMap<>();

    public MessageSenderService(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void sendMessage(SendMessage message) throws TelegramApiException {
        bot.execute(message);
    }

    public Message sendMessage(long chatId, String text, InlineKeyboardMarkup markup) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        if (markup != null) {
            message.setReplyMarkup(markup);
        }
        return bot.execute(message);
    }

    public void deleteMessage(long chatId, int messageId) {
        try {
            if (messageId <= 0) return;
            DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), messageId);
            Boolean result = bot.execute(deleteMessage);
            if (result == null || !result) {
                log.warn("Failed to delete message {} in chat {}", messageId, chatId);
            }
        } catch (TelegramApiException e) {
            if (!e.getMessage().contains("message to delete not found")) {
                log.error("Error deleting message", e);
            }
        }
    }

    public void trackLastMessage(long chatId, int messageId) {
        lastMessageIds.put(chatId, messageId);
    }

    public void deleteLastTrackedMessage(long chatId) {
        if (lastMessageIds.containsKey(chatId)) {
            deleteMessage(chatId, lastMessageIds.get(chatId));
        }
    }
}