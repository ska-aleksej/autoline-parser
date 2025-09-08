package ru.parser.service;

import ru.parser.model.Company;
import java.sql.SQLException;
import java.util.List;

public interface CompanyService {
    
    /**
     * Сохраняет список компаний в базу данных
     * @param companies список компаний для сохранения
     * @throws SQLException при ошибке работы с БД
     */
    void saveCompanies(List<Company> companies) throws SQLException;
    
    /**
     * Получает все сохраненные компании из базы данных
     * @return список всех сохраненных компаний
     * @throws SQLException при ошибке работы с БД
     */
    List<Company> getSavedCompanies() throws SQLException;
    
    /**
     * Находит новые компании среди спарсенных
     * @param parsedCompanies список спарсенных компаний
     * @return список новых компаний, которых нет в БД
     * @throws SQLException при ошибке работы с БД
     */
    List<Company> findNewCompanies(List<Company> parsedCompanies) throws SQLException;
}
