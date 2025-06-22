package org.webproject.shedulebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.webproject.shedulebot.entity.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByChatId(Long chatId);
    Optional<Task> findByIdAndChatId(Long id, Long chatId);
    void deleteByIdAndChatId(Long id, Long chatId);
    boolean existsByIdAndChatId(Long id, Long chatId);

    @Query("SELECT MAX(t.taskId) FROM Task t WHERE t.chatId = :chatId")
    Optional<Long> findMaxTaskIdByChatId(long chatId);
}