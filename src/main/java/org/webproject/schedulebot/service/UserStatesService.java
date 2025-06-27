package org.webproject.schedulebot.service;

import org.springframework.stereotype.Service;
import org.webproject.schedulebot.util.state.UpdateState;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserStatesService {
    private final Map<Long, UpdateState> userStates = new ConcurrentHashMap<>();
    private final Map<Long, Long> taskIdToUpdate = new ConcurrentHashMap<>();
    private final Map<Long, LocalDateTime> tempTaskDates = new ConcurrentHashMap<>();

    public UpdateState getUserState(long chatId) {
        return userStates.getOrDefault(chatId, UpdateState.NONE);
    }

    public void setUserState(long chatId, UpdateState state) {
        userStates.put(chatId, state);
    }

    public void setTaskIdToUpdate(long chatId, long taskId) {
        taskIdToUpdate.put(chatId, taskId);
    }

    public long getTaskIdToUpdate(long chatId) {
        return taskIdToUpdate.get(chatId);
    }

    public void clearTaskIdToUpdate(long chatId) {
        taskIdToUpdate.remove(chatId);
    }

    public void setTempTaskDate(long chatId, LocalDateTime dateTime) {
        tempTaskDates.put(chatId, dateTime);
    }

    public LocalDateTime getTempTaskDate(long chatId) {
        return tempTaskDates.get(chatId);
    }

    public void clearTempTaskDate(long chatId) {
        tempTaskDates.remove(chatId);
    }

    public void clearAllForUser(long chatId) {
        userStates.remove(chatId);
        taskIdToUpdate.remove(chatId);
        tempTaskDates.remove(chatId);
    }
}