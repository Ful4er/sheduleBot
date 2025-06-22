package org.webproject.shedulebot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.webproject.shedulebot.controller.BotController;
import org.webproject.shedulebot.service.TaskService;
import org.webproject.shedulebot.util.CustomDateTimeFormatter;

@Configuration
public class BotConfig {

    @Bean
    public CustomDateTimeFormatter customDateTimeFormatter() {
        return new CustomDateTimeFormatter();
    }

    @Bean
    public TelegramLongPollingBot telegramBot(
            @Value("${telegram.bot.username}") String username,
            @Value("${telegram.bot.token}") String token,
            TaskService taskService,
            CustomDateTimeFormatter customDateTimeFormatter) {
        return new BotController(username, token, taskService, customDateTimeFormatter);
    }
}