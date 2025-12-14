package ru.parser.telegram;

/**
 * Константы для Telegram бота
 */
public final class TelegramBotConstants {

    private TelegramBotConstants() {
        // Утилитарный класс
    }

    /**
     * Команды бота
     */
    public static final class Commands {
        public static final String START = "/start";
        public static final String STATUS = "/status";
        public static final String HELP = "/help";

        private Commands() {}
    }

    /**
     * Настройки формата сообщений
     */
    public static final class Format {
        public static final String MARKDOWN_V2 = "MarkdownV2";

        private Format() {}
    }

    /**
     * Настройки пакетной обработки
     */
    public static final class Batch {
        public static final int COMPANY_BATCH_SIZE = 8;
        public static final int MESSAGE_DELAY_MS = 1000;

        private Batch() {}
    }

    /**
     * Специальные символы для экранирования в MarkdownV2
     */
    public static final class MarkdownChars {
        public static final String SPECIAL_CHARS_PATTERN = "([_*\\[\\]()~`>#+\\-=|{}.!])";
        public static final String SEPARATOR = "──────────────────";

        private MarkdownChars() {}
    }
}