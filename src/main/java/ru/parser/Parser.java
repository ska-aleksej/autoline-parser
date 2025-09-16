package ru.parser;

import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.parser.model.Company;
import ru.parser.service.CompanyService;
import ru.parser.service.MailService;
import ru.parser.service.WebParserService;

import java.sql.SQLException;
import java.util.List;


@Component
public class Parser {
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);
    private final MailService mailService;
    private final CompanyService companyService;
    private final WebParserService webParserService;

    @Autowired
    public Parser(
            CompanyService companyService,
            MailService mailService,
            WebParserService webParserService
    ) {
        this.companyService = companyService;
        this.mailService = mailService;
        this.webParserService = webParserService;
    }

    public void parse() {
        List<Company> allCompanies = webParserService.getAllCompanies();

        List<Company> newOnes;
        try {
            newOnes = companyService.findNewCompanies(allCompanies);
        } catch (SQLException e) {
            logger.error("Ошибка получения новых записей", e);
            throw new RuntimeException(e);
        }

        try {
            companyService.saveCompanies(newOnes);
            logger.info("Найденные записи сохранены в БД");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        fillCompanyDetails(newOnes);

        newOnes.removeIf((c) -> c.getDetails().notNewOnSite());

        try {
            mailService.sendHtmlEmail(newOnes);
        } catch (MessagingException e) {
            logger.error("Ошибка отправки письма", e);
            throw new RuntimeException(e);
        }
    }

    private void fillCompanyDetails(List<Company> newOnes) {
        newOnes.forEach(c -> c.setDetails(webParserService.getCompanyDetails(c.getLink())));
    }
}
