package org.webproject.shedulebot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.webproject.shedulebot.controller.BotController;

@Configuration
public class BotConfig {
    @Value("${telegram.bot.username}")
    private String username;

    @Value("${telegram.bot.token}")
    private String token;

    @Bean
    public TelegramLongPollingBot telegramBot() {
        return new BotController(username, token);
    }
}