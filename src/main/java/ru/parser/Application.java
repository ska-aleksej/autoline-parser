package ru.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.parser.config.AppConfig;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Запуск Spring контекста...");
        try( AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {

            Parser parser = context.getBean(Parser.class);
            parser.parse();

        }
        logger.info("Контекст закрыт.");
    }
}
