package org.webproject.shedulebot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.webproject.shedulebot.controller.BotController;

@Configuration
public class BotConfig {

    @Bean
    public TelegramLongPollingBot telegramBot(
            @Value("${telegram.bot.username}") String username,
            @Value("${telegram.bot.token}") String token) {
        return new BotController(username, token);
    }
}