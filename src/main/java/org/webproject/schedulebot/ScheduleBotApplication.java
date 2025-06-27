package org.webproject.schedulebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"org.webproject.schedulebot", "org.telegram.telegrambots"})
@EnableScheduling
public class ScheduleBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleBotApplication.class, args);
    }
}