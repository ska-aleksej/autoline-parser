package ru.parser;

import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.parser.model.Company;
import ru.parser.model.CompanyDetails;
import ru.parser.model.ScanType;
import ru.parser.service.CompanyService;
import ru.parser.service.MailService;
import ru.parser.service.TelegramBotService;
import ru.parser.service.WebParserService;

import java.util.List;
import java.util.Optional;


@Component
public class Parser {
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);
    private static final int MAX_CONSECUTIVE_DETAIL_FAILURES = 3;
    private final MailService mailService;
    private final CompanyService companyService;
    private final WebParserService webParserService;
    private final TelegramBotService telegramBotService;

    public Parser(
            CompanyService companyService,
            MailService mailService,
            WebParserService webParserService,
            Optional<TelegramBotService> telegramBotService
    ) {
        this.companyService = companyService;
        this.mailService = mailService;
        this.webParserService = webParserService;
        this.telegramBotService = telegramBotService.orElse(null);
    }

    public void parse(ScanType scanType) {
        boolean baseline = companyService.isEmpty();

        if (baseline && scanType != ScanType.FULL) {
            logger.info("БД пуста: сканирование {} пропущено, базовое наполнение выполнит FULL-скан", scanType);
            return;
        }

        List<Company> allCompanies = webParserService.getAllCompaniesByType(scanType);
        List<Company> newOnes = companyService.findNewCompanies(allCompanies);

        companyService.saveCompanies(newOnes);
        logger.info("Найденные записи сохранены в БД");

        if (baseline) {
            logger.info("Первый запуск: {} компаний сохранены как базовое наполнение, без уведомлений", newOnes.size());
            return;
        }

        fillCompanyDetails(newOnes);

        newOnes.removeIf((c) -> c.getDetails().notNewOnSite());

        if (scanType == ScanType.FULL || !newOnes.isEmpty()) {
            try {
                mailService.sendHtmlEmail(newOnes);
                if (telegramBotService != null) {
                    telegramBotService.sendReport(newOnes);
                }
            } catch (MessagingException e) {
                logger.error("Ошибка отправки письма", e);
                throw new RuntimeException(e);
            }
        }
    }

    private void fillCompanyDetails(List<Company> newOnes) {
        int failed = 0;
        int consecutiveFailures = 0;

        for (Company company : newOnes) {
            try {
                company.setDetails(webParserService.getCompanyDetails(company.getLink()));
                consecutiveFailures = 0;
            } catch (Exception e) {
                failed++;
                consecutiveFailures++;
                logger.error("Не удалось получить детали компании {}, компания останется в отчете без деталей",
                        company.getLink(), e);
                if (consecutiveFailures >= MAX_CONSECUTIVE_DETAIL_FAILURES) {
                    logger.warn("Детали не получены для {} компаний подряд — сайт недоступен или заблокировал запросы, " +
                            "пропускаем загрузку деталей для остальных компаний", consecutiveFailures);
                    break;
                }
            }
        }

        if (failed > 0) {
            logger.warn("Детали не получены для {} из {} новых компаний", failed, newOnes.size());
            fillMissingDetailsWithFallback(newOnes);
        }
    }

    private void fillMissingDetailsWithFallback(List<Company> newOnes) {
        for (Company company : newOnes) {
            if (company.getDetails() == null) {
                company.setDetails(new CompanyDetails("N/A", "N/A", "N/A", null));
            }
        }
    }
}
