# Task Manager Bot  

Телеграм-бот для управления задачами с напоминаниями. Позволяет создавать, просматривать, редактировать и удалять задачи с уведомлениями.  

## Основные возможности  
- 📅 Создание задач с указанием даты/времени  
- 🔍 Просмотр списка всех задач  
- ✏️ Редактирование существующих задач  
- 🗑️ Удаление задач  
- ⏰ Уведомления о предстоящих задачах (за 1 час)  
- ⏳ Возможность отложить задачу на час  
- ✅ Отметка задач как выполненных  

## Технологии  
- **Java 17**  
- **Spring Boot 3.5.0**  
- **PostgreSQL**
- **Telegram Bot API 6.5.0**  
- **Docker** 
- **Flyway**
- **Lombok** (упрощение кода)  
- **Spring Data JPA**
- **Hibernate**  

## Установка и запуск  

### Требования
- Docker и Docker Compose (для варианта с Docker)
- Java 17+ и Maven (для варианта без Docker)
- Telegram бот (токен от [@BotFather](https://t.me/BotFather))
- 
2. Запустите через Docker:  
```bash  
docker-compose up --build
```
Или без Docker (требуется установленная PostgreSQL):
```
./mvnw spring-boot:run
```
# Использование
После запуска бота в Telegram:

* Отправьте /start для инициализации
* Используйте меню для:
  * Добавления новых задач
  * Просмотра списка задач
  * Редактирования/удаления задач

# Команды
* /menu - главное меню
* /alltask - список всех задач
* /help - справка
* /updatetask - изменить задачу
* /deletetask - удалить задачу
# Уведомления
Бот автоматически уведомляет:
* За 1 час до наступления события
* При наступлении времени задачи (с возможностью продлить)
# Структура проекта
```src/  
├── config/       - конфигурация Spring  
├── controller/   - обработка Telegram-сообщений  
├── dto/          - объекты передачи данных  
├── entity/       - сущности базы данных  
├── handlers/     - обработчики команд  
├── repository/   - работа с базой данных  
├── service/      - бизнес-логика  
└── util/         - вспомогательные классы
    ├── builder
    ├── constant
    ├── factory
    ├── state
    ├── formatter  
```
# Структура БД
```sql
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    chat_id BIGINT NOT NULL,
    date_time TIMESTAMP NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    notified BOOLEAN DEFAULT FALSE
);

CREATE UNIQUE INDEX tasks_chat_id_task_id_unique ON tasks (chat_id, task_id);
```
# Миграции базы данных
Проект использует Flyway для управления миграциями. Все скрипты миграций находятся в src/main/resources/db/migration:
* V1__Create_tasks_table.sql - создает основную таблицу задач
* V2__Add_notified_column.sql - добавляет колонку для отслеживания уведомлений
