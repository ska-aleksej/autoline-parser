package ru.parser.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.parser.config.AppProperties;
import ru.parser.model.Company;
import ru.parser.model.CompanyDetails;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class WebParserServiceImpl implements WebParserService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebParserServiceImpl.class);
    
    private final AppProperties appProperties;
    
    public WebParserServiceImpl(AppProperties appProperties) {
        this.appProperties = appProperties;
    }
    
    @Override
    public List<Company> getAllCompanies() {
        String baseURL = appProperties.getBaseUrl();
        String path = appProperties.getCompaniesListUrl();
        
        Document companiesPage = getPageWithCompanies(baseURL + path);
        int pageCount = getPageCount(companiesPage);
        List<Company> allCompanies = new LinkedList<>(extractCompanies(companiesPage));
        
        logger.info("Обрабатываем {} страниц с компаниями", pageCount);

        for (int page = 2; page <= pageCount; page++) {
            companiesPage = getPageWithCompanies(baseURL + path + "?page=" + page);
            List<Company> pageCompanies = extractCompanies(companiesPage);
            allCompanies.addAll(pageCompanies);
        }
        
        logger.info("Всего найдено компаний: {}", allCompanies.size());
        return allCompanies;
    }
    
    @Override
    public List<Company> getCompaniesFromPage(int pageNumber) {
        String baseURL = appProperties.getBaseUrl();
        String path = appProperties.getCompaniesListUrl();
        
        String url = baseURL + path;
        if (pageNumber > 1) {
            url += "?page=" + pageNumber;
        }
        
        Document companiesPage = getPageWithCompanies(url);
        return extractCompanies(companiesPage);
    }
    
    @Override
    public CompanyDetails getCompanyDetails(String companyLink) {
        logger.debug("Получаем детали компании: {}", companyLink);
        
        try {
            Document companyPage = Jsoup.connect(companyLink)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .timeout(10000)
                    .get();

            String address = "";
            Element addressBlock = companyPage.selectFirst("div.address.dealer-contact-row");

            if (addressBlock != null) {
                Element addressTextDiv = addressBlock.selectFirst("div.text");
                if (addressTextDiv != null) {
                    address = addressTextDiv.text().trim();
                }
            }

            String adsCount = "N/A";
            String yearsOnSite = "N/A";
            String yearsOnMarket = "N/A";
            Element infoBlock = companyPage.selectFirst("div.dealer-main-info");
            if (infoBlock != null) {
                Element adsElement = infoBlock.selectFirst("span.number-of-ads");
                if (adsElement != null) {
                    adsCount = adsElement.text().trim();
                }

                Element yearsOnSiteElement = infoBlock.selectFirst("div.years-on-site-container strong");
                if (yearsOnSiteElement != null) {
                    yearsOnSite = yearsOnSiteElement.text().trim();
                }


                Element yearsOnMarketElement = infoBlock.selectFirst("div.years-on-market-container strong");
                if (yearsOnMarketElement != null) {
                    yearsOnMarket = yearsOnMarketElement.text().trim();
                }
            }

            return new CompanyDetails(adsCount, yearsOnSite, yearsOnMarket, address);
            
        } catch (IOException e) {
            logger.error("Ошибка при получении деталей компании: {}", companyLink, e);
            throw new RuntimeException("Не удалось получить детали компании", e);
        }
    }
    
    @Override
    public int getTotalPages() {
        String baseURL = appProperties.getBaseUrl();
        String path = appProperties.getCompaniesListUrl();
        
        Document companiesPage = getPageWithCompanies(baseURL + path);
        return getPageCount(companiesPage);
    }
    
    private List<Company> extractCompanies(Document companiesPage) {
        Elements dealerLinks = companiesPage.select("div.dealers-list-view a");
        logger.trace("На странице найдено {} компаний", dealerLinks.size());
        List<Company> companies = new LinkedList<>();

        for (Element link: dealerLinks) {
            String href = link.attr("href");

            String dealerName = link.selectFirst("h3.dealer-info-block--title") != null ?
                    link.selectFirst("h3.dealer-info-block--title").text() : null;
            companies.add(new Company(dealerName, href));
        }
        return companies;
    }

    private int getPageCount(Document companiesPage) {
        Element lastPageLink = companiesPage.selectFirst("a.pgn-last");

        if (lastPageLink != null) {
            String pageNumberText = lastPageLink.text();
            int totalPages = 0;
            try {
                totalPages = Integer.parseInt(pageNumberText);
                logger.debug("Общее количество страниц: {}", totalPages);
            } catch (NumberFormatException e) {
                logger.error("Ошибка: текст \"{}\" не является числом.", pageNumberText);
            }
            return totalPages;
        } else {
            logger.error("Элемент последней страницы не найден.");
            throw new RuntimeException("Элемент последней страницы не найден.");
        }
    }

    private Document getPageWithCompanies(String url) {
        logger.debug("Загружаем страницу с компаниями {}", url);
        Document document;
        try {
            document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .timeout(10000)
                    .get();
        } catch (IOException e) {
            logger.error("Ошибка загрузки страницы", e);
            throw new RuntimeException(e);
        }
        logger.trace("Загружена страница с компаниями");
        return document;
    }
} 