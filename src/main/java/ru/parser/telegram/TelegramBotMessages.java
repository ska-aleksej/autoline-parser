package ru.parser.telegram;

/**
 * Текстовые сообщения для Telegram бота
 */
public final class TelegramBotMessages {

    private TelegramBotMessages() {
        // Утилитарный класс
    }

    /**
     * Сообщения об ошибках и доступе
     */
    public static final class Access {
        public static final String UNAUTHORIZED = "К сожалению, у вас нет доступа...";
        public static final String GREETING = "Приветствую! Доступ разрешен.";

        private Access() {}
    }

    /**
     * Ответы на команды
     */
    public static final class Responses {
        public static final String BOT_WORKING = "Бот работает нормально";
        public static final String HELP_TEXT = """
                Доступные команды:
                /status - Проверить статус бота
                /help - Показать это сообщение""";

        private Responses() {}
    }

    /**
     * Системные сообщения
     */
    public static final class System {
        public static final String APP_STARTED = "Приложение запущено!";

        private System() {}
    }

    /**
     * Шаблоны для отчетов
     */
    public static final class ReportTemplates {
        public static final String SCAN_COMPLETE = "✅ Сканирование завершено. Найдено новых компаний: %d";
        public static final String NEW_COMPANIES_HEADER = "📋 Новые компании:\n\n";
        public static final String COMPANIES_BATCH_HEADER = "📋 Компании %d\\-%d из %d:\n\n";

        private ReportTemplates() {}
    }

    /**
     * Текстовые метки для информации о компании
     */
    public static final class CompanyInfo {
        public static final String ADS_COUNT_LABEL = "📋 Объявлений: ";
        public static final String YEARS_ON_SITE_LABEL = "🌐 Лет на сайте: ";
        public static final String YEARS_ON_MARKET_LABEL = "💼 Лет в продажах: ";
        public static final String ADDRESS_LABEL = "📍 Адрес: ";
        public static final String LINK_LABEL = "🔗 ";
        public static final String CHINA_WARNING = "⚠️ *Компания из Китая\\!*";

        private CompanyInfo() {}
    }
}