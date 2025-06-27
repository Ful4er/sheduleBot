package org.webproject.schedulebot.util.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.webproject.schedulebot.util.constant.CallbackCommands;

public class ButtonFactory {

    public static InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    public static InlineKeyboardButton createBackButton(String callbackData) {
        return createButton("⬅ Назад", callbackData);
    }

    public static InlineKeyboardButton createCancelButton() {
        return createButton("❌ Отменить", CallbackCommands.CANCEL_CREATION);
    }

    public static InlineKeyboardButton createAddTaskButton() {
        return createButton("➕ Добавить задачу", CallbackCommands.MENU_ADD_TASK);
    }

    public static InlineKeyboardButton createViewTasksButton() {
        return createButton("📋 Список задач", CallbackCommands.MENU_VIEW_TASKS);
    }

    public static InlineKeyboardButton createUpdateTaskButton() {
        return createButton("✏️ Изменить задачу", CallbackCommands.MENU_UPDATE_TASK);
    }

    public static InlineKeyboardButton createDeleteTaskButton() {
        return createButton("🗑️ Удалить задачу", CallbackCommands.MENU_DELETE_TASK);
    }

    public static InlineKeyboardButton createHelpButton() {
        return createButton("❓ Помощь", CallbackCommands.MENU_HELP);
    }

    public static InlineKeyboardButton createPostponeButton(Long taskId) {
        return createButton("⏳ Продлить на час", CallbackCommands.POSTPONE_PREFIX + taskId);
    }

    public static InlineKeyboardButton createCompleteButton(Long taskId) {
        return createButton("✅ Завершить", CallbackCommands.COMPLETE_PREFIX + taskId);
    }


}