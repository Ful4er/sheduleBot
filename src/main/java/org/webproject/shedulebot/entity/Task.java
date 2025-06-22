package org.webproject.shedulebot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tasks")
public class Task{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @Column(name="task_id",nullable = false)
    private Long taskId;

    @Column(name="chat_id",nullable = false)
    private Long chatId;

    @Column(name = "date_time",nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private String description;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
