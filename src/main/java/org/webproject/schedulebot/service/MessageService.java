package org.webproject.schedulebot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.webproject.schedulebot.dto.TaskDTO;
import org.webproject.schedulebot.util.builder.KeyboardMarkupBuilder;
import org.webproject.schedulebot.util.constant.MessageTexts;
import org.webproject.schedulebot.util.formatter.CustomDateTimeFormatter;
import org.webproject.schedulebot.util.state.UpdateState;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static org.webproject.schedulebot.util.constant.CallbackCommands.*;

@Slf4j
@Service
public class MessageService {
    private final MessageSenderService messageSender;
    private final TaskService taskService;
    private final CustomDateTimeFormatter customDateTimeFormatter;
    private final UserStatesService userStatesService;

    public MessageService(MessageSenderService messageSender,
                          TaskService taskService,
                          CustomDateTimeFormatter customDateTimeFormatter,
                          UserStatesService userStatesService) {
        this.messageSender = messageSender;
        this.taskService = taskService;
        this.customDateTimeFormatter = customDateTimeFormatter;
        this.userStatesService = userStatesService;
    }

    public void handleCommand(long chatId, String text) {
        try {
            messageSender.deleteAllIncomingMessages(chatId);
            UpdateState currentState = userStatesService.getUserState(chatId);

            switch (currentState) {
                case WAITING_ID -> handleTaskIdInput(chatId, text);
                case WAITING_DATA -> handleTaskUpdate(chatId, text);
                case WAITING_DELETE_ID -> handleTaskDelete(chatId, text);
                case WAITING_DATE_DESCRIPTION -> handleTaskDescriptionInput(chatId, text);
                case WAITING_TASK_DATE -> handleTaskDateInput(chatId, text);
                case NONE -> processCommand(chatId, text);
            }
        } catch (Exception e) {
            log.error("Error handling command", e);
            sendMessage(chatId, MessageTexts.ERROR_OCCURRED + e.getMessage());
        }
    }

    private void processCommand(long chatId, String text) {
        if ("/start".equalsIgnoreCase(text)) {
            handleStartCommand(chatId);
        } else if ("/menu".equalsIgnoreCase(text)) {
            handleMenuCommand(chatId);
        } else if ("/alltask".equalsIgnoreCase(text)) {
            sendAllTasks(chatId);
        } else if ("/help".equalsIgnoreCase(text)) {
            sendHelp(chatId);
        } else if (text.startsWith("/updatetask")) {
            startUpdateTask(chatId);
        } else if (text.startsWith("/deletetask")) {
            startDeleteTask(chatId);
        } else {
            sendMessage(chatId, MessageTexts.UNKNOWN_COMMAND, MENU_MAIN);
        }
    }

    public void showMainMenu(long chatId) {
        try {
            messageSender.deleteLastTrackedMessage(chatId);

            Message menuMessage = messageSender.sendMessage(
                    chatId,
                    MessageTexts.MAIN_MENU,
                    KeyboardMarkupBuilder.createMainMenuMarkup()
            );
            messageSender.trackLastMessage(chatId, menuMessage.getMessageId());
        } catch (Exception e) {
            log.error("Error showing main menu", e);
            sendMessage(chatId, MessageTexts.MENU_ERROR);
        }
    }

    public void handleStartCommand(long chatId) {
        try {
            messageSender.deleteAllIncomingMessages(chatId);

            if (!messageSender.hasWelcomeMessage(chatId)) {
                Message welcomeMsg = messageSender.sendMessage(
                        chatId,
                        MessageTexts.WELCOME_MESSAGE,
                        null
                );
                messageSender.setWelcomeMessage(chatId, welcomeMsg.getMessageId());
            }

            showMainMenu(chatId);
        } catch (Exception e) {
            log.error("Error handling start command", e);
            sendMessage(chatId, MessageTexts.ERROR_OCCURRED + e.getMessage());
        }
    }

    public void handleMenuCommand(long chatId) {
        try {
            messageSender.deleteAllIncomingMessages(chatId);
            showMainMenu(chatId);
        } catch (Exception e) {
            log.error("Error handling menu command", e);
            sendMessage(chatId, MessageTexts.ERROR_OCCURRED + e.getMessage());
        }
    }
    public void sendAllTasks(long chatId) {
        List<TaskDTO> taskDTOS = taskService.getUserTasks(chatId);
        if (taskDTOS.isEmpty()) {
            sendMessage(chatId, MessageTexts.NO_TASKS, MENU_MAIN);
            return;
        }

        StringBuilder sb = new StringBuilder(MessageTexts.YOUR_TASKS_HEADER);
        taskDTOS.forEach(taskDTO -> sb.append(taskDTO.toString()).append("\n\n"));
        sendMessage(chatId, sb.toString(), MENU_MAIN);
    }

    public void startUpdateTask(long chatId) {
        List<TaskDTO> taskDTOS = taskService.getUserTasks(chatId);
        if (taskDTOS.isEmpty()) {
            sendMessage(chatId, MessageTexts.NO_TASKS_TO_UPDATE, MENU_MAIN);
            return;
        }

        sendMessage(chatId, MessageTexts.INPUT_TASK_ID + getAllTasksAsString(chatId), MENU_MAIN);
        userStatesService.setUserState(chatId, UpdateState.WAITING_ID);
    }

    private void handleTaskIdInput(long chatId, String text) {
        try {
            long taskId = Long.parseLong(text);
            Optional<TaskDTO> task = taskService.findTaskById(chatId, taskId);

            if (task.isPresent()) {
                userStatesService.setTaskIdToUpdate(chatId, taskId);
                sendMessage(chatId, MessageTexts.TASK_UPDATE_INSTRUCTIONS, MENU_UPDATE_TASK);
                userStatesService.setUserState(chatId, UpdateState.WAITING_DATA);
            } else {
                sendMessage(chatId, String.format(MessageTexts.TASK_NOT_FOUND, taskId), MENU_UPDATE_TASK);
                userStatesService.setUserState(chatId, UpdateState.NONE);
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, MessageTexts.INVALID_ID_FORMAT, MENU_UPDATE_TASK);
        }
    }

    private void handleTaskUpdate(long chatId, String text) {
        try {
            long taskId = userStatesService.getTaskIdToUpdate(chatId);
            String[] parts = text.split("\\s+", 3);

            if (parts.length < 3) {
                sendMessage(chatId, MessageTexts.INVALID_TASK_FORMAT, MENU_UPDATE_TASK);
                return;
            }

            LocalDateTime newDateTime = customDateTimeFormatter.parse(parts[0] + " " + parts[1]);
            String description = parts[2];

            boolean updated = taskService.updateTask(chatId, taskId, newDateTime, description);

            if (updated) {
                Optional<TaskDTO> updatedTask = taskService.findTaskById(chatId, taskId);
                String taskInfo = updatedTask.map(TaskDTO::toString).orElse(MessageTexts.TASK_INFO_UNAVAILABLE);
                sendMessage(chatId, String.format(MessageTexts.TASK_UPDATED, taskId, taskInfo), MENU_MAIN);
            } else {
                sendMessage(chatId, String.format(MessageTexts.UPDATE_FAILED, taskId), MENU_UPDATE_TASK);
            }
        } catch (DateTimeParseException e) {
            sendMessage(chatId, MessageTexts.INVALID_DATE_TIME_FORMAT, MENU_UPDATE_TASK);
        } catch (Exception e) {
            sendMessage(chatId, MessageTexts.UPDATE_ERROR + e.getMessage(), MENU_UPDATE_TASK);
        } finally {
            userStatesService.setUserState(chatId, UpdateState.NONE);
            userStatesService.clearTaskIdToUpdate(chatId);
        }
    }

    public void startDeleteTask(long chatId) {
        List<TaskDTO> taskDTOS = taskService.getUserTasks(chatId);
        if (taskDTOS.isEmpty()) {
            sendMessage(chatId, MessageTexts.NO_TASKS_TO_DELETE, MENU_MAIN);
            return;
        }

        sendMessage(chatId, MessageTexts.INPUT_DELETE_TASK_ID + getAllTasksAsString(chatId), MENU_MAIN);
        userStatesService.setUserState(chatId, UpdateState.WAITING_DELETE_ID);
    }

    private void handleTaskDelete(long chatId, String text) {
        try {
            long taskId = Long.parseLong(text);
            boolean deleted = taskService.deleteTask(chatId, taskId);

            if (deleted) {
                sendMessage(chatId, MessageTexts.TASK_DELETED, MENU_MAIN);
            } else {
                sendMessage(chatId, String.format(MessageTexts.TASK_NOT_FOUND, taskId), MENU_DELETE_TASK);
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, MessageTexts.INVALID_ID_FORMAT, MENU_DELETE_TASK);
        } finally {
            userStatesService.clearAllForUser(chatId);
        }
    }

    public void startCreateTask(long chatId) {
        try {
            userStatesService.clearTempTaskDate(chatId);
            userStatesService.setUserState(chatId, UpdateState.WAITING_TASK_DATE);

            messageSender.deleteLastTrackedMessage(chatId);
            Message message = messageSender.sendMessage(
                    chatId,
                    MessageTexts.INPUT_TASK_DATE,
                    KeyboardMarkupBuilder.createCancelMarkup()
            );
            messageSender.trackLastMessage(chatId, message.getMessageId());
        } catch (Exception e) {
            log.error("Error starting task creation", e);
            sendMessage(chatId, MessageTexts.TASK_CREATION_ERROR, MENU_MAIN);
        }
    }

    private void handleTaskDateInput(long chatId, String text) {
        try {
            LocalDateTime dateTime = customDateTimeFormatter.parse(text);
            if (dateTime.isBefore(LocalDateTime.now())) {
                sendMessage(chatId, MessageTexts.DATE_IN_PAST, CANCEL_CREATION);
                return;
            }
            userStatesService.setTempTaskDate(chatId, dateTime);
            sendMessage(chatId, MessageTexts.INPUT_TASK_DESCRIPTION, CANCEL_CREATION);
            userStatesService.setUserState(chatId, UpdateState.WAITING_DATE_DESCRIPTION);
        } catch (DateTimeParseException e) {
            sendMessage(chatId, MessageTexts.INVALID_DATE_FORMAT, CANCEL_CREATION);
        }
    }

    private void handleTaskDescriptionInput(long chatId, String text) {
        if (text == null || text.trim().isEmpty()) {
            sendMessage(chatId, MessageTexts.EMPTY_DESCRIPTION, CANCEL_CREATION);
            return;
        }

        LocalDateTime dateTime = userStatesService.getTempTaskDate(chatId);
        if (dateTime == null) {
            sendMessage(chatId, MessageTexts.DATE_NOT_SAVED, MENU_MAIN);
            userStatesService.setUserState(chatId, UpdateState.NONE);
            return;
        }

        TaskDTO taskDTO = taskService.createTask(chatId, dateTime, text);
        sendMessage(chatId, taskDTO.saveTaskEvent(), MENU_MAIN);
        userStatesService.clearTempTaskDate(chatId);
        userStatesService.setUserState(chatId, UpdateState.NONE);
    }

    public void cancelCreation(long chatId) {
        userStatesService.clearTempTaskDate(chatId);
        userStatesService.setUserState(chatId, UpdateState.NONE);
        sendMessage(chatId, MessageTexts.CREATION_CANCELLED, MENU_MAIN);
    }

    private String getAllTasksAsString(long chatId) {
        List<TaskDTO> taskDTOS = taskService.getUserTasks(chatId);
        StringBuilder sb = new StringBuilder();
        taskDTOS.forEach(taskDTO -> sb.append(taskDTO.toString()).append("\n"));
        return sb.toString();
    }

    public void sendHelp(long chatId) {
        sendMessage(chatId, MessageTexts.HELP_TEXT, MENU_MAIN);
    }

    public void sendMessage(long chatId, String text, String backCommand) {
        try {
            int lastMessageId = messageSender.getLastMessageId(chatId);
            if (!messageSender.isWelcomeMessage(chatId, lastMessageId)) {
                messageSender.deleteLastTrackedMessage(chatId);
            }

            Message message = messageSender.sendMessage(
                    chatId,
                    text,
                    backCommand != null ? KeyboardMarkupBuilder.createBackMarkup(backCommand) : null
            );
            messageSender.trackLastMessage(chatId, message.getMessageId());
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }

    public void sendMessage(long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    public void postponeTask(long chatId, long taskId) {
        try {
            Optional<TaskDTO> task = taskService.findTaskById(chatId, taskId);
            if (task.isPresent()) {
                LocalDateTime newDateTime = task.get().getDateTime().plusHours(1);
                boolean updated = taskService.updateTask(chatId, taskId, newDateTime, task.get().getDescription());

                if (updated) {
                    sendMessage(chatId, MessageTexts.TASK_POSTPONED, MENU_MAIN);
                } else {
                    sendMessage(chatId, MessageTexts.FAILED_TO_POSTPONE, MENU_MAIN);
                }
            } else {
                sendMessage(chatId, MessageTexts.TASK_NOT_FOUND_NOTIFICATION, MENU_MAIN);
            }
        } catch (Exception e) {
            log.error("Error postponing task", e);
            sendMessage(chatId, MessageTexts.ERROR_OCCURRED + e.getMessage());
        }
    }

    public void completeTask(long chatId, long taskId) {
        try {
            boolean deleted = taskService.deleteTask(chatId, taskId);
            if (deleted) {
                sendMessage(chatId, MessageTexts.TASK_COMPLETED, MENU_MAIN);
            } else {
                sendMessage(chatId, MessageTexts.FAILED_TO_COMPLETE, MENU_MAIN);
            }
        } catch (Exception e) {
            log.error("Error completing task", e);
            sendMessage(chatId, MessageTexts.ERROR_OCCURRED + e.getMessage());
        }
    }
}