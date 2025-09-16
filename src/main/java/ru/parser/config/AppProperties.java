package ru.parser.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private List<String> emails = new ArrayList<>();
    private String targetBaseUrl;
    private String targetCompaniesPath;

    public String getTargetBaseUrl() {
        return targetBaseUrl;
    }

    public String getTargetCompaniesListUrl() {
        return targetCompaniesPath;
    }

    public void setTargetBaseUrl(String targetBaseUrl) {
        this.targetBaseUrl = targetBaseUrl;
    }

    public String getTargetCompaniesPath() {
        return targetCompaniesPath;
    }

    public void setTargetCompaniesPath(String targetCompaniesPath) {
        this.targetCompaniesPath = targetCompaniesPath;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> mails) {
        this.emails = mails;
    }
}
