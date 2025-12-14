package ru.parser.telegram;

import ru.parser.model.Company;
import ru.parser.model.CompanyDetails;

import java.util.List;

/**
 * Утилитарный класс для форматирования сообщений Telegram бота
 */
public final class TelegramMessageFormatter {

    private TelegramMessageFormatter() {
        // Утилитарный класс
    }

    /**
     * Экранирует специальные символы для MarkdownV2
     *
     * @param text текст для экранирования
     * @return экранированный текст
     */
    public static String escapeMarkdown(String text) {
        if (text == null) {
            return "";
        }
        // Экранируем специальные символы для MarkdownV2, но не экранируем \n
        return text.replaceAll(TelegramBotConstants.MarkdownChars.SPECIAL_CHARS_PATTERN, "\\\\$1");
    }

    /**
     * Форматирует информацию о компании в Markdown
     *
     * @param company компания для форматирования
     * @return отформатированная строка с информацией о компании
     */
    public static String formatCompanyInfo(Company company) {
        StringBuilder builder = new StringBuilder();

        // Название компании (жирным)
        builder.append("*").append(escapeMarkdown(company.getName())).append("*\n");

        // Ссылка
        builder.append(TelegramBotMessages.CompanyInfo.LINK_LABEL)
                .append(escapeMarkdown(company.getLink())).append("\n");

        // Дополнительная информация
        if (company.getDetails() != null) {
            CompanyDetails details = company.getDetails();
            appendDetailIfExists(builder, TelegramBotMessages.CompanyInfo.ADS_COUNT_LABEL, details.getAdsCount());
            appendDetailIfExists(builder, TelegramBotMessages.CompanyInfo.YEARS_ON_SITE_LABEL, details.getYearsOnSite());
            appendDetailIfExists(builder, TelegramBotMessages.CompanyInfo.YEARS_ON_MARKET_LABEL, details.getYearsOnMarket());

            if (details.getAddress() != null) {
                builder.append(TelegramBotMessages.CompanyInfo.ADDRESS_LABEL)
                        .append(escapeMarkdown(details.getAddress())).append("\n");

                if (details.hasChinaAddress()) {
                    builder.append(TelegramBotMessages.CompanyInfo.CHINA_WARNING).append("\n");
                }
            }
        }

        return builder.toString();
    }

    /**
     * Форматирует заголовок для порции компаний
     *
     * @param startIndex начальный индекс
     * @param endIndex конечный индекс
     * @param total общее количество компаний
     * @return отформатированный заголовок
     */
    public static String formatBatchHeader(int startIndex, int endIndex, int total) {
        if (total > TelegramBotConstants.Batch.COMPANY_BATCH_SIZE) {
            return String.format(TelegramBotMessages.ReportTemplates.COMPANIES_BATCH_HEADER,
                    startIndex, endIndex, total);
        } else {
            return TelegramBotMessages.ReportTemplates.NEW_COMPANIES_HEADER;
        }
    }

    /**
     * Форматирует список компаний в одно сообщение
     *
     * @param companies список компаний
     * @param startIndex начальный индекс в общем списке
     * @param total общее количество компаний
     * @return отформатированное сообщение
     */
    public static String formatCompanyBatch(List<Company> companies, int startIndex, int total) {
        StringBuilder builder = new StringBuilder();

        // Заголовок порции
        int endIndex = startIndex + companies.size();
        builder.append(formatBatchHeader(startIndex + 1, endIndex, total));

        // Компании
        for (int i = 0; i < companies.size(); i++) {
            builder.append(formatCompanyInfo(companies.get(i)));

            // Разделитель между компаниями (кроме последней в порции)
            if (i < companies.size() - 1) {
                builder.append("\n")
                        .append(escapeMarkdown(TelegramBotConstants.MarkdownChars.SEPARATOR))
                        .append("\n\n");
            }
        }

        return builder.toString();
    }

    /**
     * Форматирует сообщение о завершении сканирования
     *
     * @param newCompaniesCount количество новых компаний
     * @return отформатированное сообщение
     */
    public static String formatScanCompleteMessage(int newCompaniesCount) {
        return String.format(TelegramBotMessages.ReportTemplates.SCAN_COMPLETE, newCompaniesCount);
    }

    /**
     * Вспомогательный метод для добавления детальной информации
     */
    private static void appendDetailIfExists(StringBuilder builder, String label, String value) {
        if (value != null) {
            builder.append(label).append(escapeMarkdown(value)).append("\n");
        }
    }
}