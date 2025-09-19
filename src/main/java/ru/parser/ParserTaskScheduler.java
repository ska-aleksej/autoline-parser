package ru.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ParserTaskScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ParserTaskScheduler.class);
    final private Parser parser;

    public ParserTaskScheduler(Parser parser) {
        this.parser = parser;
    }

    @Scheduled(cron = "0 0 7,19 * * ?", zone = "Europe/Minsk")
    public void startScan() {
        try {
            logger.info("Запуск сканирования");
            parser.parse();
            logger.info("Сканирование завершено");
        } catch (Exception ex) {
            logger.error("Ошибка выполнения сканирования", ex);
        }
    }
}
