package org.webproject.schedulebot.util.builder;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.webproject.schedulebot.util.factory.ButtonFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyboardMarkupBuilder {
    private final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    public KeyboardMarkupBuilder addRow(InlineKeyboardButton... buttons) {
        keyboard.add(Arrays.asList(buttons));
        return this;
    }

    public InlineKeyboardMarkup build() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    public static KeyboardMarkupBuilder create() {
        return new KeyboardMarkupBuilder();
    }

    public static InlineKeyboardMarkup createMainMenuMarkup() {
        return KeyboardMarkupBuilder.create()
                .addRow(
                        ButtonFactory.createAddTaskButton(),
                        ButtonFactory.createViewTasksButton()
                )
                .addRow(
                        ButtonFactory.createUpdateTaskButton(),
                        ButtonFactory.createDeleteTaskButton()
                )
                .addRow(
                        ButtonFactory.createHelpButton()
                )
                .build();
    }

    public static InlineKeyboardMarkup createBackMarkup(String backCommand) {
        return KeyboardMarkupBuilder.create()
                .addRow(ButtonFactory.createBackButton(backCommand))
                .build();
    }

    public static InlineKeyboardMarkup createCancelMarkup() {
        return KeyboardMarkupBuilder.create()
                .addRow(ButtonFactory.createCancelButton())
                .build();
    }
}