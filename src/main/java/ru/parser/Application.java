package ru.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private final Parser parser;

    public Application(Parser parser) {
        this.parser = parser;
    }

    public static void main(String[] args) {
        logger.info("Запуск приложения");
        SpringApplication app = new SpringApplication(Application.class);
        app.setWebApplicationType(WebApplicationType.NONE); // Отключаем web
        app.run(args);
    }


    @Override
    public void run(String... args) {
        logger.info("Запуск парсинга...");
        parser.parse();
        logger.info("Парсинг завершен.");
        logger.info("Завершение работы приложения.");
        System.exit(0);
    }
}
