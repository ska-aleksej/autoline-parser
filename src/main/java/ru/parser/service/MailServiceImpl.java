package ru.parser.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.parser.model.Company;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {

    @Value("${mail.token}")
    private String mailToken;
    @Value("${mail.username}")
    private String mailUsername;
    @Value("${mail.sender}")
    private String mailSender;
    @Value("${mail.to}")
    private String mailTo;
    @Value("${mail.smtp.host}")
    private String smtpHost;
    @Value("${mail.smtp.port}")
    private String smtpPort;
    @Value("${mail.smtp.auth}")
    private String smtpAuth;
    @Value("${mail.smtp.starttls.enable}")
    private String smtpStarttlsEnable;

    private final TemplateEngine templateEngine;

    public MailServiceImpl(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendHtmlEmail(List<Company> companies) throws MessagingException {
        Session session = createSession();
        Message message = new MimeMessage(session);
        
        message.setFrom(new InternetAddress(mailSender));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));

        String subject = "Результаты сканирования " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        message.setSubject(subject);
        
        // Создание контекста для шаблона
        Context context = new Context();
        context.setVariable("companies", companies);
        
        // Генерация HTML контента
        String htmlContent = templateEngine.process("email-report", context);
        
        // Создание multipart сообщения
        MimeMultipart multipart = new MimeMultipart("alternative");
        
        // Текстовая версия (простая)
        MimeBodyPart textPart = new MimeBodyPart();
        String textContent = generateTextContent(companies);
        textPart.setText(textContent, "UTF-8");
        multipart.addBodyPart(textPart);
        
        // HTML версия
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
        multipart.addBodyPart(htmlPart);
        
        message.setContent(multipart);
        Transport.send(message);
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