package ru.parser.service;

import ru.parser.model.Company;
import ru.parser.model.CompanyDetails;

import java.util.List;

public interface WebParserService {
    
    /**
     * Получает список всех компаний со всех страниц
     * @return список всех найденных компаний
     */
    List<Company> getAllCompanies();
    
    /**
     * Получает список компаний с конкретной страницы
     * @param pageNumber номер страницы (начиная с 1)
     * @return список компаний с указанной страницы
     */
    List<Company> getCompaniesFromPage(int pageNumber);
    
    /**
     * Получает детальную информацию о компании по её ссылке
     * @param companyLink ссылка на страницу компании
     * @return объект Company с дополнительной информацией
     */
    CompanyDetails getCompanyDetails(String companyLink);
    
    /**
     * Получает общее количество страниц с компаниями
     * @return количество страниц
     */
    int getTotalPages();
} 