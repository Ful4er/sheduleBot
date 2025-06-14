package org.webproject.shedulebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.webproject.shedulebot", "org.telegram.telegrambots"})
public class SheduleBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SheduleBotApplication.class, args);
    }
}