import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.parser.config.AppConfig;

import static org.junit.jupiter.api.Assertions.*;

public class SpringContextTest {

    @Test
    void contextShouldStartSuccessfully() {

        assertDoesNotThrow(() -> {
            try (AnnotationConfigApplicationContext context =
                         new AnnotationConfigApplicationContext(AppConfig.class)) {
                assertNotNull(context, "Контекст не должен быть null");
                assertTrue(context.getBeanDefinitionCount() > 0,
                        "Контекст должен содержать хотя бы один бин");
             }

        }, "Создание и закрытие контекста не должно вызывать исключений");
    }
}
