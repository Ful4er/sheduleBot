package org.webproject.schedulebot.util.constant;

public class MessageTexts {
    public static final String MAIN_MENU = "Главное меню. Выберите действие:";
    public static final String NO_TASKS = "У вас пока нет задач.";
    public static final String YOUR_TASKS_HEADER = "Ваши задачи:\n\n";
    public static final String NO_TASKS_TO_UPDATE = "У вас пока нет задач для обновления.";
    public static final String NO_TASKS_TO_DELETE = "У вас пока нет задач для удаления.";
    public static final String TASK_UPDATE_INSTRUCTIONS = """
            Введите новые данные задачи в формате:
            дата время описание
            Пример: 31.12.2025 23:59 Новый Год""";
    public static final String INPUT_TASK_ID = "Введите ID задачи для обновления:\n";
    public static final String INPUT_DELETE_TASK_ID = "Введите ID задачи для удаления:\n";
    public static final String TASK_NOT_FOUND = "Задача с ID %d не найдена.";
    public static final String INVALID_ID_FORMAT = "Неверный формат ID. Введите число.";
    public static final String INVALID_TASK_FORMAT = "Неверный формат. Нужно: дата время описание\nПример: 31.12.2025 23:59 Новый Год";
    public static final String INVALID_DATE_TIME_FORMAT = "Ошибка формата даты/времени. Используйте формат: ДД.ММ.ГГГГ ЧЧ:MM";
    public static final String TASK_UPDATED = "Задача ID %d успешно обновлена!\n\n%s";
    public static final String UPDATE_FAILED = "Не удалось обновить задачу с ID %d";
    public static final String UPDATE_ERROR = "Ошибка при обновлении: ";
    public static final String TASK_DELETED = "Задача успешно удалена!";
    public static final String INPUT_TASK_DATE = "Введите дату и время задачи в формате ДД.ММ.ГГГГ ЧЧ:MM";
    public static final String INPUT_TASK_DESCRIPTION = "Введите описание задачи:";
    public static final String DATE_IN_PAST = "Дата не может быть в прошлом. Введите корректную дату:";
    public static final String INVALID_DATE_FORMAT = "Неверный формат даты. Введите в формате ДД.ММ.ГГГГ ЧЧ:MM";
    public static final String EMPTY_DESCRIPTION = "Описание задачи не может быть пустым. Введите описание:";
    public static final String DATE_NOT_SAVED = "Ошибка! Дата не сохранена. Повторите заново.";
    public static final String TASK_INFO_UNAVAILABLE = "Информация о задаче недоступна";
    public static final String CREATION_CANCELLED = "Создание задачи отменено";
    public static final String TASK_CREATION_ERROR = "Произошла ошибка при создании задачи. Попробуйте снова.";
    public static final String MENU_ERROR = "Ошибка при отображении меню";
    public static final String UNKNOWN_COMMAND = "Неизвестная команда. Используй /help";
    public static final String ERROR_OCCURRED = "Произошла ошибка: ";
    public static final String HELP_TEXT = """
            📚 Доступные функции:
            
            Все действия выполняются через меню:
            
            1. Добавить задачу - введите дату и описание по шагам
            2. Список задач - просмотр всех ваших задач
            3. Изменить задачу - обновление существующей задачи
            4. Удалить задачу - удаление задачи по ID
            
            В любой момент можно отменить действие кнопкой "Назад"
            """;
    public static final String REMINDER_TASK_FORMAT = "⏰ Напоминание: в ближайший час начнётся задача - %s (%s)";
    public static final String TASK_AUTO_DELETED_FORMAT = "⌛️ Задача просрочена: %s (была на %s)";
    public static final String TASK_POSTPONED =  "Задача отложена на 1 час";
    public static final String TASK_COMPLETED =  "Задача завершена и удалена";
    public static final String FAILED_TO_POSTPONE =  "Не удалось отложить задачу";
    public static final String FAILED_TO_COMPLETE =  "Не удалось завершить задачу";
    public static final String TASK_NOT_FOUND_NOTIFICATION =  "Задача не найдена";

}