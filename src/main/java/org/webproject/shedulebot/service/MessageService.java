package org.webproject.shedulebot.service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.webproject.shedulebot.dto.Task;
import org.webproject.shedulebot.util.CallbackCommands;
import org.webproject.shedulebot.util.CustomDateTimeFormatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.webproject.shedulebot.util.CallbackCommands.*;

public class MessageService {
    private enum UpdateState {
        NONE, WAITING_ID, WAITING_DATA, WAITING_DELETE_ID,
        WAITING_TASK_DATE, WAITING_DATE_DESCRIPTION
    }

    private final Map<Long, UpdateState> userStates = new ConcurrentHashMap<>();
    private final Map<Long, Long> taskIdToUpdate = new ConcurrentHashMap<>();
    private final TelegramLongPollingBot bot;
    private final TaskService taskService;
    private final CustomDateTimeFormatter customDateTimeFormatter;
    private final Map<Long, Integer> lastMessageIds = new ConcurrentHashMap<>();
    private final Map<Long,LocalDateTime> tempTaskDates = new ConcurrentHashMap<>();
    
    public MessageService(TelegramLongPollingBot bot) {
        this.bot = bot;
        this.taskService = new TaskService();
        this.customDateTimeFormatter = new CustomDateTimeFormatter();
    }

    public void handleCommand(long chatId, String text) {
        try {
            UpdateState currentState = userStates.getOrDefault(chatId, UpdateState.NONE);

            switch (currentState) {
                case WAITING_ID -> handleTaskIdInput(chatId, text);
                case WAITING_DATA -> handleTaskUpdate(chatId, text);
                case WAITING_DELETE_ID -> handleTaskDelete(chatId, text);
                case WAITING_DATE_DESCRIPTION -> handleTaskDescriptionInput(chatId,text);
                case WAITING_TASK_DATE -> handleTaskDateInput(chatId,text);
                case NONE -> processCommand(chatId, text);
            }
        } catch (Exception e) {
            sendMessage(chatId, "Произошла ошибка: " + e.getMessage());
        }
    }

    private void processCommand(long chatId, String text) {
        if ("/start".equalsIgnoreCase(text) || "/menu".equalsIgnoreCase(text)) {
            lastMessageIds.remove(chatId);
            mainMenu(chatId);
        } else if ("/alltask".equalsIgnoreCase(text)) {
            sendAllTasks(chatId);
        } else if ("/help".equalsIgnoreCase(text)) {
            sendHelp(chatId);
        } else if (text.startsWith("/updatetask")) {
            startUpdateTask(chatId);
        } else if (text.startsWith("/deletetask")) {
            startDeleteTask(chatId);
        }else if (text.equals("/menu")){
            mainMenu(chatId);
        } else {
            sendMessage(chatId, "Неизвестная команда. Используй /help",MENU_MAIN);
        }
    }
    public void mainMenu(long chatId) {
        try {
            if (lastMessageIds.containsKey(chatId)) {
                deleteMessage(chatId, lastMessageIds.get(chatId));
            }

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("Главное меню. Выберите действие:");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            List<InlineKeyboardButton> row1 = new ArrayList<>();

            InlineKeyboardButton addTaskBtn = new InlineKeyboardButton();
            addTaskBtn.setText("➕ Добавить задачу");
            addTaskBtn.setCallbackData(CallbackCommands.MENU_ADD_TASK);
            row1.add(addTaskBtn);

            InlineKeyboardButton viewTasksBtn = new InlineKeyboardButton();
            viewTasksBtn.setText("📋 Список задач");
            viewTasksBtn.setCallbackData(CallbackCommands.MENU_VIEW_TASKS);
            row1.add(viewTasksBtn);

            List<InlineKeyboardButton> row2 = new ArrayList<>();

            InlineKeyboardButton updateTaskBtn = new InlineKeyboardButton();
            updateTaskBtn.setText("✏️ Изменить задачу");
            updateTaskBtn.setCallbackData(CallbackCommands.MENU_UPDATE_TASK);
            row2.add(updateTaskBtn);

            InlineKeyboardButton deleteTaskBtn = new InlineKeyboardButton();
            deleteTaskBtn.setText("🗑️ Удалить задачу");
            deleteTaskBtn.setCallbackData(CallbackCommands.MENU_DELETE_TASK);
            row2.add(deleteTaskBtn);

            List<InlineKeyboardButton> row3 = new ArrayList<>();

            InlineKeyboardButton helpBtn = new InlineKeyboardButton();
            helpBtn.setText("❓ Помощь");
            helpBtn.setCallbackData(CallbackCommands.MENU_HELP);
            row3.add(helpBtn);

            keyboard.add(row1);
            keyboard.add(row2);
            keyboard.add(row3);
            markup.setKeyboard(keyboard);
            message.setReplyMarkup(markup);

            Message sentMessage = bot.execute(message);
            lastMessageIds.put(chatId, sentMessage.getMessageId());

        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "Ошибка при отображении меню");
        }
    }
    public void sendAllTasks(long chatId) {
        List<Task> tasks = taskService.getUserTasks(chatId);
        if (tasks.isEmpty()) {
            sendMessage(chatId, "У вас пока нет задач.", MENU_MAIN);
            return;
        }

        StringBuilder sb = new StringBuilder("Ваши задачи:\n\n");
        tasks.forEach(task -> sb.append(task.toString()).append("\n\n"));
        sendMessage(chatId, sb.toString(), CallbackCommands.MENU_MAIN);
    }

    public void startUpdateTask(long chatId) {
        List<Task> tasks = taskService.getUserTasks(chatId);
        if (tasks.isEmpty()) {
            sendMessage(chatId, "У вас пока нет задач для обновления.",MENU_MAIN);
            return;
        }

        sendMessage(chatId, "Введите ID задачи для обновления:\n" + getAllTasksAsString(chatId), CallbackCommands.MENU_MAIN);
        userStates.put(chatId, UpdateState.WAITING_ID);
    }

    private void handleTaskIdInput(long chatId, String text) {
        try {
            long taskId = Long.parseLong(text);
            Optional<Task> task = taskService.findTaskById(chatId, taskId);

            if (task.isPresent()) {
                taskIdToUpdate.put(chatId, taskId);
                sendMessage(chatId, """
                        Введите новые данные задачи в формате:
                        дата время описание
                        Пример: 31.12.2025 23:59 Новый Год""",BACK_UPDATE);
                userStates.put(chatId, UpdateState.WAITING_DATA);
            } else {
                sendMessage(chatId, "Задача с ID " + taskId + " не найдена.", BACK_UPDATE);
                userStates.put(chatId, UpdateState.NONE);
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Неверный формат ID. Введите число.", CallbackCommands.MENU_MAIN);
        }
    }

    private void handleTaskUpdate(long chatId, String text) {
        try {
            long taskId = taskIdToUpdate.get(chatId);
            String[] parts = text.split("\\s+", 3);

            if (parts.length < 3) {
                sendMessage(chatId, "Неверный формат. Нужно: дата время описание\nПример: 31.12.2025 23:59 Новый Год",BACK_UPDATE);
                return;
            }

            LocalDateTime newDateTime = customDateTimeFormatter.parse(parts[0] + " " + parts[1]);
            String description = parts[2];

            boolean updated = taskService.updateTask(chatId, taskId, newDateTime, description);

            if (updated) {
                Optional<Task> updatedTask = taskService.findTaskById(chatId, taskId);
                String taskInfo = updatedTask.map(Task::toString).orElse("Информация о задаче недоступна");
                sendMessage(chatId, "Задача ID " + taskId + " успешно обновлена!\n\n" + taskInfo, CallbackCommands.MENU_MAIN);
            } else {
                sendMessage(chatId, "Не удалось обновить задачу с ID " + taskId, BACK_UPDATE);
            }
        } catch (DateTimeParseException e) {
            sendMessage(chatId, "Ошибка формата даты/времени. Используйте формат: ДД.ММ.ГГГГ ЧЧ:MM", BACK_UPDATE);
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при обновлении: " + e.getMessage(), BACK_UPDATE);
        } finally {
            userStates.put(chatId, UpdateState.NONE);
            taskIdToUpdate.remove(chatId);
        }
    }

    public void startDeleteTask(long chatId) {
        List<Task> tasks = taskService.getUserTasks(chatId);
        if (tasks.isEmpty()) {
            sendMessage(chatId, "У вас пока нет задач для удаления.",MENU_MAIN);
            return;
        }

        sendMessage(chatId, "Введите ID задачи для удаления:\n" + getAllTasksAsString(chatId), CallbackCommands.MENU_MAIN);
        userStates.put(chatId, UpdateState.WAITING_DELETE_ID);
    }

    private void handleTaskDelete(long chatId, String text) {
        try {
            long taskId = Long.parseLong(text);
            boolean deleted = taskService.deleteTask(chatId, taskId);

            if (deleted) {
                sendMessage(chatId, "Задача успешно удалена!",MENU_MAIN);
            } else {
                sendMessage(chatId, "Задача с ID " + taskId + " не найдена.", BACK_DELETE);
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Неверный формат ID. Введите число.",BACK_DELETE);
        } finally {
            userStates.put(chatId, UpdateState.NONE);
        }
    }

    public void startCreateTask(long chatId) {
        try {
            tempTaskDates.remove(chatId);
            userStates.put(chatId, UpdateState.WAITING_TASK_DATE);

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("Введите дату и время задачи в формате ДД.ММ.ГГГГ ЧЧ:MM");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton cancelButton = new InlineKeyboardButton();
            cancelButton.setText("❌ Отменить");
            cancelButton.setCallbackData(CANCEL_CREATION);
            row.add(cancelButton);

            keyboard.add(row);
            markup.setKeyboard(keyboard);
            message.setReplyMarkup(markup);

            Message sentMessage = bot.execute(message);
            lastMessageIds.put(chatId, sentMessage.getMessageId());

        } catch (Exception e) {
            System.err.println("Ошибка при старте создания задачи: " + e.getMessage());
            sendMessage(chatId, "Произошла ошибка при создании задачи. Попробуйте снова.", MENU_MAIN);
        }
    }

    private void handleTaskDateInput(long chatId, String text){
        try{
            LocalDateTime dateTime = customDateTimeFormatter.parse(text);
            if(dateTime.isBefore(LocalDateTime.now())){
                sendMessage(chatId,"Дата не может быть в прошлом. Введите корректную дату:",CANCEL_CREATION);
                return;
            }
            tempTaskDates.put(chatId,dateTime);
            sendMessage(chatId,"Введите описание задачи:",CANCEL_CREATION);
            userStates.put(chatId,UpdateState.WAITING_DATE_DESCRIPTION);
        }catch (DateTimeParseException e) {
            sendMessage(chatId, "Неверный формат даты. Введите в формате ДД.ММ.ГГГГ ЧЧ:MM", CANCEL_CREATION);
        }
    }
    private void handleTaskDescriptionInput(long chatId, String text){
        if (text == null || text.trim().isEmpty()) {
            sendMessage(chatId, "Описание задачи не может быть пустым. Введите описание:", CANCEL_CREATION);
            return;
        }

        LocalDateTime dateTime = tempTaskDates.get(chatId);
        if(dateTime == null){
            sendMessage(chatId,"Ошибка! Дата не сохранена. Повторите заново.",MENU_MAIN);
            userStates.put(chatId,UpdateState.NONE);
            return;
        }

        Task task = taskService.createTask(chatId,dateTime,text);
        sendMessage(chatId,task.saveTaskEvent(),MENU_MAIN);
        tempTaskDates.remove(chatId);
        userStates.put(chatId,UpdateState.NONE);
    }
    public void cancelCreation(long chatId){
        tempTaskDates.remove(chatId);
        userStates.put(chatId,UpdateState.NONE);
        sendMessage(chatId,"Создание задачи отменено",MENU_MAIN);
    }
    private String getTaskCreationErrorMessage(IllegalArgumentException e) {
        return "Ошибка: " + e.getMessage() + "\n\n" +
                "Правильный формат команды:\n" +
                "ДД.ММ.ГГГГ ЧЧ:MM описание\n\n" +
                "Примеры:\n" +
                "31.12.2030 23:59 Встреча Нового Года\n" +
                "Убедитесь, что:\n" +
                "1. Дата и время в будущем\n" +
                "2. Правильный формат (ДД.ММ.ГГГГ ЧЧ:MM)\n" +
                "3. Описание не содержит только специальные символы\n";
    }

    private String getAllTasksAsString(long chatId) {
        List<Task> tasks = taskService.getUserTasks(chatId);
        StringBuilder sb = new StringBuilder();
        tasks.forEach(task -> sb.append(task.toString()).append("\n"));
        return sb.toString();
    }

    public void sendHelp(long chatId) {
        String helpText = """
            📚 Доступные функции:
            
            Все действия выполняются через меню:
            
            1. Добавить задачу - введите дату и описание по шагам
            2. Список задач - просмотр всех ваших задач
            3. Изменить задачу - обновление существующей задачи
            4. Удалить задачу - удаление задачи по ID
            
            В любой момент можно отменить действие кнопкой "Назад"
            """;

        sendMessage(chatId, helpText, MENU_MAIN);
    }

    public void sendMessage(long chatId, String text,String backCommand) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(text);

            if(backCommand!=null){
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> keybord = new ArrayList<>();

                InlineKeyboardButton backButton = new InlineKeyboardButton();
                backButton.setText("⬅ Назад");
                backButton.setCallbackData(backCommand);

                keybord.add(Collections.singletonList(backButton));
                markup.setKeyboard(keybord);
                message.setReplyMarkup(markup);
            }
            if(lastMessageIds.containsKey(chatId)){
                deleteMessage(chatId,lastMessageIds.get(chatId));
            }
            Message sendMessage = bot.execute(message);
            lastMessageIds.put(chatId,sendMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
        }
    }
    public void sendMessage(long chatId, String text){
        sendMessage(chatId,text,null);
    }

    public void deleteMessage(long chatId, int messageId) {
        try {
            if (messageId <= 0) return;
            DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), messageId);
            Boolean result = bot.execute(deleteMessage);
            if (result == null || !result) {
                System.err.println("Не удалось удалить сообщение " + messageId + " в чате " + chatId);
            }
        } catch (TelegramApiException e) {
            if (!e.getMessage().contains("message to delete not found")) {
                System.err.println("Ошибка при удалении сообщения: " + e.getMessage());
            }
        }
    }
}