package org.webproject.schedulebot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class MessageSenderService {
    private final TelegramLongPollingBot bot;
    private final Map<Long, Integer> lastMessageIds = new ConcurrentHashMap<>();
    private final Map<Long, List<Integer>> lastIncomingMessageIds = new ConcurrentHashMap<>();
    private final Map<Long, Integer> welcomeMessageIds = new ConcurrentHashMap<>();

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
        message.setParseMode("HTML");
        if (markup != null) {
            message.setReplyMarkup(markup);
        }
        return bot.execute(message);
    }

    public void deleteMessage(long chatId, int messageId) {
        try {
            if (messageId <= 0 || isWelcomeMessage(chatId, messageId)) return;
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

    public void trackIncomingMessage(long chatId, int incomingId) {
        lastIncomingMessageIds
                .computeIfAbsent(chatId, k -> new ArrayList<>())
                .add(incomingId);
    }

    public void deleteAllIncomingMessages(long chatId) {
        List<Integer> ids = lastIncomingMessageIds.remove(chatId);
        if (ids != null) {
            ids.forEach(id -> deleteMessage(chatId, id));
        }
    }

    public void setWelcomeMessage(long chatId, int messageId) {
        welcomeMessageIds.put(chatId, messageId);
    }

    public boolean isWelcomeMessage(long chatId, int messageId) {
        return messageId == welcomeMessageIds.getOrDefault(chatId, -1);
    }

    public int getLastMessageId(long chatId) {
        return lastMessageIds.getOrDefault(chatId, -1);
    }

    public boolean hasWelcomeMessage(long chatId) {
        return welcomeMessageIds.containsKey(chatId);
    }
}