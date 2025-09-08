package ru.parser.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Value("${target.base-url}")
    private String baseUrl;

    @Value("${target.companies-path}")
    private String CompaniesListUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getCompaniesListUrl() {
        return CompaniesListUrl;
    }
}
