package ru.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.parser.model.ScanType;

@Component
@ConditionalOnProperty(name = "app.startup-scan-enabled", havingValue = "true")
public class StartupScanRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupScanRunner.class);
    private final Parser parser;

    public StartupScanRunner(Parser parser) {
        this.parser = parser;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Запуск однократного сканирования при старте (app.startup-scan-enabled=true)");
        try {
            parser.parse(ScanType.FULL);
            logger.info("Стартовое сканирование завершено");
        } catch (Exception ex) {
            logger.error("Ошибка стартового сканирования", ex);
        }
    }
}
