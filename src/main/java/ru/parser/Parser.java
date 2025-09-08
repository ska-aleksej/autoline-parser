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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
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

        saveToFile(newOnes, getFileName("report-new"));

        try {
            companyService.saveCompanies(newOnes);
            logger.info("Найденные записи сохранены в БД");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        allCompanies.sort(Comparator.comparing(Company::getName));
        saveToFile(allCompanies, getFileName("report"));

        fillCompanyDetails(newOnes);

        try {
            mailService.sendHtmlEmail(newOnes);
            logger.info("Письмо отправлено");
        } catch (MessagingException e) {
            logger.error("Ошибка отправки письма", e);
            throw new RuntimeException(e);
        }
    }

    private void fillCompanyDetails(List<Company> newOnes) {
        newOnes.forEach(c -> c.setDetails(webParserService.getCompanyDetails(c.getLink())));
    }

    public static String getFileName(String prefix) {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        return "./"+ prefix+ "-" + timestamp + ".txt";
    }

    // Метод для сохранения коллекции в файл
    public static void saveToFile(List<Company> companies, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Company company : companies) {
                writer.write(company.getName());
                writer.write("|");
                writer.write(company.getLink());
                writer.newLine();
            }
            logger.info("Данные успешно сохранены в файл: {}", filename);
        } catch (IOException e) {
            logger.error("Ошибка записи в файл", e);
        }
    }
}
