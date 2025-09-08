package ru.parser.service;

import jakarta.mail.MessagingException;
import ru.parser.model.Company;
import java.util.List;

public interface MailService {

    void sendHtmlEmail(List<Company> companies) throws MessagingException;

}