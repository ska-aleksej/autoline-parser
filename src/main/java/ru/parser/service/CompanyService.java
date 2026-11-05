package ru.parser.service;

import ru.parser.model.Company;
import java.util.List;

public interface CompanyService {

    /**
     * Проверяет, пуста ли таблица компаний (режим первого запуска)
     * @return true, если в БД нет ни одной компании
     */
    boolean isEmpty();

    /**
     * Сохраняет список компаний в базу данных
     * @param companies список компаний для сохранения
     */
    void saveCompanies(List<Company> companies);

    /**
     * Получает все сохраненные компании из базы данных
     * @return список всех сохраненных компаний
     */
    List<Company> getSavedCompanies();

    /**
     * Находит новые компании среди спарсенных
     * @param parsedCompanies список спарсенных компаний
     * @return список новых компаний, которых нет в БД
     */
    List<Company> findNewCompanies(List<Company> parsedCompanies);
}
