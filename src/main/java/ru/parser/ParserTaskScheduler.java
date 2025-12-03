package ru.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.parser.model.ScanType;

@Component
public class ParserTaskScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ParserTaskScheduler.class);
    final private Parser parser;

    public ParserTaskScheduler(Parser parser) {
        this.parser = parser;
    }

    @Scheduled(cron = "0 0 7,19 * * ?", zone = "Europe/Minsk")
    public void startFullScan() {
        try {
            logger.info("Запуск полного сканирования");
            parser.parse(ScanType.FULL);
            logger.info("Сканирование завершено");
        } catch (Exception ex) {
            logger.error("Ошибка выполнения сканирования", ex);
        }
    }

    @Scheduled(cron = "0 50 * * * ?", zone = "Europe/Minsk")
    public void startScanChinese() {
        try {
            logger.info("Запуск сканирования компаний из Китая");
            parser.parse(ScanType.ONLY_CHINESE);
            logger.info("Сканирование компаний из Китая завершено");
        } catch (Exception ex) {
            logger.error("Ошибка выполнения сканирования", ex);
        }
    }
}
