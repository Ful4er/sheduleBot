package org.webproject.schedulebot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.webproject.schedulebot.controller.BotController;
import org.webproject.schedulebot.repository.TaskRepository;
import org.webproject.schedulebot.service.*;
import org.webproject.schedulebot.util.formatter.CustomDateTimeFormatter;

@Configuration
public class BotConfig {

    @Bean
    public CustomDateTimeFormatter customDateTimeFormatter() {
        return new CustomDateTimeFormatter();
    }

    @Bean
    public UserStatesService userStatesService() {
        return new UserStatesService();
    }

    @Bean
    public TelegramLongPollingBot telegramBot(
            @Value("${telegram.bot.username}") String username,
            @Value("${telegram.bot.token}") String token,
            TaskService taskService,
            CustomDateTimeFormatter customDateTimeFormatter,
            UserStatesService userStatesService) {
        return new BotController(username, token, taskService, customDateTimeFormatter, userStatesService);
    }

    @Bean
    public MessageSenderService messageSenderService(TelegramLongPollingBot telegramBot) {
        return new MessageSenderService(telegramBot);
    }

    @Bean
    public NotificationMessageService notificationMessageService(MessageSenderService messageSenderService) {
        return new NotificationMessageService(messageSenderService);
    }

    @Bean
    public NotificationService notificationService(
            TaskRepository taskRepository,
            NotificationMessageService notificationMessageService,
            CustomDateTimeFormatter customDateTimeFormatter) {
        return new NotificationService(taskRepository, notificationMessageService, customDateTimeFormatter);
    }
}