package ru.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import ru.parser.model.Company;
import ru.parser.model.CompanyDetails;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailTemplateTest {

    private TemplateEngine templateEngine;

    @BeforeEach
    void setUp() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        engine.setTemplateResolver(resolver);
        engine.addDialect(new Java8TimeDialect());

        this.templateEngine = engine;
    }

    @Test
    void shouldGenerateEmailWithMinskTime() {
        ZonedDateTime minskTime = ZonedDateTime.now(ZoneId.of("Europe/Minsk"));

        Context context = new Context();
        context.setVariable("scanDate", minskTime);
        context.setVariable("companies", getTestCompanies());

        String htmlContent = templateEngine.process("email-report", context);

        assertThat(htmlContent).contains("Дата сканирования:");
    }

    private List<Company> getTestCompanies() {
        var companies = new ArrayList<Company>(1);
        Company testCompany = new Company("New company", "some url");
        CompanyDetails details = new CompanyDetails("0 ads","0", "0", "some street");
        testCompany.setDetails(details);
        companies.add(testCompany);
        return companies;
    }
}