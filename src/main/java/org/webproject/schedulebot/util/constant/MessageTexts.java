package org.webproject.schedulebot.util.constant;

public class MessageTexts {
    public static final String MAIN_MENU = "📋 <b>Главное меню</b>\n\nВыберите действие:";
    public static final String NO_TASKS = "📭 У вас пока нет задач.";
    public static final String YOUR_TASKS_HEADER = "📋 <b>Ваши задачи:</b>\n\n";
    public static final String NO_TASKS_TO_UPDATE = "🔍 У вас пока нет задач для обновления.";
    public static final String NO_TASKS_TO_DELETE = "🗑️ У вас пока нет задач для удаления.";
    public static final String TASK_UPDATE_INSTRUCTIONS = """
            ✏️ <b>Обновление задачи</b>
            
            Введите новые данные в формате:
            <code>дата время описание</code>
            
            Пример:
            <code>31.12.2025 23:59 Встреча Нового Года</code>""";
    public static final String INPUT_TASK_ID = "🔢 Введите <b>ID задачи</b> для обновления:\n";
    public static final String INPUT_DELETE_TASK_ID = "🗑️ Введите <b>ID задачи</b> для удаления:\n";
    public static final String TASK_NOT_FOUND = "❌ Задача с ID <b>%d</b> не найдена.";
    public static final String INVALID_ID_FORMAT = "⚠️ Неверный формат ID. Введите число.";
    public static final String INVALID_TASK_FORMAT = """
            ⚠️ <b>Неверный формат</b>
            
            Требуется: <code>дата время описание</code>
            
            Пример:
            <code>31.12.2025 23:59 Поздравить друзей</code>""";
    public static final String INVALID_DATE_TIME_FORMAT = """
            ⚠️ <b>Ошибка формата даты/времени</b>
            
            Используйте формат: <code>ДД.ММ.ГГГГ ЧЧ:MM</code>""";
    public static final String TASK_UPDATED = """
            ✅ <b>Задача обновлена!</b>
            
            ID: <b>%d</b>
            %s""";
    public static final String UPDATE_FAILED = "❌ Не удалось обновить задачу с ID <b>%d</b>";
    public static final String UPDATE_ERROR = "⚠️ Ошибка при обновлении: ";
    public static final String TASK_DELETED = "✅ <b>Задача успешно удалена!</b>";
    public static final String INPUT_TASK_DATE = """
            📅 <b>Введите дату и время</b>
            
            Формат: <code>ДД.ММ.ГГГГ ЧЧ:MM</code>
            Пример: <code>31.12.2025 23:59</code>""";
    public static final String INPUT_TASK_DESCRIPTION = "📝 Введите <b>описание задачи</b>:";
    public static final String DATE_IN_PAST = "⏳ Дата не может быть в прошлом. Введите корректную дату:";
    public static final String INVALID_DATE_FORMAT = "⚠️ Неверный формат даты. Используйте <code>ДД.ММ.ГГГГ ЧЧ:MM</code>";
    public static final String EMPTY_DESCRIPTION = "⚠️ Описание не может быть пустым. Введите описание:";
    public static final String DATE_NOT_SAVED = "❌ Ошибка! Дата не сохранена. Попробуйте снова.";
    public static final String TASK_INFO_UNAVAILABLE = "⚠️ Информация о задаче недоступна";
    public static final String CREATION_CANCELLED = "🚫 Создание задачи отменено";
    public static final String TASK_CREATION_ERROR = "❌ Ошибка при создании задачи. Попробуйте снова.";
    public static final String MENU_ERROR = "⚠️ Ошибка при отображении меню";
    public static final String UNKNOWN_COMMAND = """
            🤷 <b>Неизвестная команда</b>
            
            Используйте /help для списка команд""";
    public static final String ERROR_OCCURRED = "⚠️ <b>Ошибка:</b> ";
    public static final String HELP_TEXT = """
            📚 <b>Помощь по боту</b>
            
            Все действия выполняются через меню:
            
            1️⃣ <b>Добавить задачу</b> - создание новой задачи
            2️⃣ <b>Список задач</b> - просмотр всех ваших задач
            3️⃣ <b>Изменить задачу</b> - обновление существующей задачи
            4️⃣ <b>Удалить задачу</b> - удаление задачи по ID
            
            🔄 В любой момент можно отменить действие кнопкой <b>"Назад"</b>
            """;
    public static final String REMINDER_TASK_FORMAT = """
            ⏰ <b>Напоминание!</b>
            
            Через час начинается:
            %s
            (<code>%s</code>)""";
    public static final String TASK_AUTO_DELETED_FORMAT = """
            ⌛️ <b>Задача просрочена</b>
            
            %s
            (была на <code>%s</code>)""";
    public static final String TASK_POSTPONED = "⏳ Задача отложена на 1 час";
    public static final String TASK_COMPLETED = "✅ Задача завершена и удалена";
    public static final String FAILED_TO_POSTPONE = "❌ Не удалось отложить задачу";
    public static final String FAILED_TO_COMPLETE = "❌ Не удалось завершить задачу";
    public static final String TASK_NOT_FOUND_NOTIFICATION = "⚠️ Задача не найдена";
    public static final String WELCOME_MESSAGE = "Добро пожаловать в Task Manager Bot!\n\n" +
            "Я помогу вам организовать ваши задачи и напомнить о них вовремя.\n" +
            "Используйте /menu для доступа к основным функциям.";
}