package ru.parser.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.parser.config.AppConfig;
import ru.parser.model.CompanyDetails;

import static org.junit.jupiter.api.Assertions.*;

public class WebParserServiceImplIT {

    private static final Logger logger = LoggerFactory.getLogger(WebParserServiceImplIT.class);

    @Test
    @DisplayName("Интеграционный вызов getCompanyDetails с реальным URL")
    void getCompanyDetails_realUrl_shouldWork() {
        String companyUrl = "https://autoline.info/scand-ukmachines/";

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            WebParserService service = context.getBean(WebParserService.class);

            CompanyDetails details = service.getCompanyDetails(companyUrl);

            assertNotNull(details, "Детали компании не должны быть null");
            // Мягкие проверки, т.к. контент страницы может меняться
            assertNotNull(details.getAddress(), "Адрес не должен быть null");
            assertNotNull(details.getAdsCount(), "Количество объявлений не должно быть null");
            assertNotNull(details.getYearsOnSite(), "Годы на сайте не должны быть null");
            assertNotNull(details.getYearsOnMarket(), "Годы на рынке не должны быть null");

            logger.info("Получены детали: address='{}', adsCount='{}', yearsOnSite='{}', yearsOnMarket='{}'",
                    details.getAddress(), details.getAdsCount(), details.getYearsOnSite(), details.getYearsOnMarket());
        }
    }
}


