package ru.parser.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.parser.config.AppProperties;
import ru.parser.model.Company;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
    @Value("${mail.token}")
    private String mailToken;
    @Value("${mail.username}")
    private String mailUsername;
    @Value("${mail.sender}")
    private String mailSender;
    @Value("${mail.smtp.host}")
    private String smtpHost;
    @Value("${mail.smtp.port}")
    private String smtpPort;
    @Value("${mail.smtp.auth}")
    private String smtpAuth;
    @Value("${mail.smtp.starttls.enable}")
    private String smtpStarttlsEnable;

    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public MailServiceImpl(TemplateEngine templateEngine, AppProperties appProperties) {
        this.templateEngine = templateEngine;
        this.appProperties = appProperties;
    }

    @Override
    public void sendHtmlEmail(List<Company> companies) throws MessagingException {
        Session session = createSession();

        String subject = "Результаты сканирования " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        // html контент
        Context context = new Context();
        context.setVariable("companies", companies);
        String htmlContent = templateEngine.process("email-report", context);

        // Текстовый контент
        String textContent = generateTextContent(companies);

        MimeMultipart multipart = createMultipartContent(textContent, htmlContent);

        for (String recipient : appProperties.getEmails()) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(mailSender));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                message.setSubject(subject);
                message.setContent(multipart);

                Transport.send(message);
                logger.info("Email отправлен для: {}", recipient);

            } catch (AddressException e) {
                logger.error("Неправильный e-mail: {}", recipient, e);
            } catch (MessagingException e) {
                logger.error("Ошибка отправки письма для: {}", recipient, e);
            }
        }
    }

    private MimeMultipart createMultipartContent(String textContent, String htmlContent)
            throws MessagingException {

        MimeMultipart multipart = new MimeMultipart("alternative");

        // Текстовая версия
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(textContent, "UTF-8");
        multipart.addBodyPart(textPart);

        // HTML версия
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
        multipart.addBodyPart(htmlPart);

        return multipart;
    }

    private String generateTextContent(List<Company> companies) {
        StringBuilder sb = new StringBuilder();
        sb.append("ОТЧЕТ О СКАНИРОВАНИИ\n");
        sb.append("====================\n\n");
        sb.append("Дата: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).append("\n");
        sb.append("Найдено новых компаний: ").append(companies.size()).append("\n\n");
        
        if (companies.isEmpty()) {
            sb.append("Новых компаний не найдено!\n");
        } else {
            sb.append("НОВЫЕ КОМПАНИИ:\n");
            for (int i = 0; i < companies.size(); i++) {
                Company company = companies.get(i);
                sb.append(i + 1).append(". ").append(company.getName()).append("\n");
                sb.append("   Ссылка: ").append(company.getLink()).append("\n\n");
            }
        }
        
        sb.append("---\n");
        sb.append("Автоматическое уведомление от системы мониторинга autoline.info");
        
        return sb.toString();
    }

    private Session createSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", smtpAuth);
        properties.put("mail.smtp.starttls.enable", smtpStarttlsEnable);

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailUsername, mailToken);
            }
        });
    }
}