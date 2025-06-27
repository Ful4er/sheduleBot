package org.webproject.schedulebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.webproject.schedulebot.entity.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByChatId(Long chatId);
    Optional<Task> findByTaskIdAndChatId(Long taskId, Long chatId);
    void deleteByTaskIdAndChatId(Long taskId, Long chatId);
    boolean existsByTaskIdAndChatId(Long taskId, Long chatId);

    @Query("SELECT MAX(t.taskId) FROM Task t WHERE t.chatId = :chatId")
    Optional<Long> findMaxTaskIdByChatId(long chatId);

    @Query("SELECT t FROM Task t WHERE t.dateTime BETWEEN :start AND :end AND t.notified = false")
    List<Task> findByDateTimeBetweenAndNotifiedFalse(LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM Task t WHERE t.dateTime < :currentTime")
    List<Task> findExpiredTasks(LocalDateTime currentTime);
}