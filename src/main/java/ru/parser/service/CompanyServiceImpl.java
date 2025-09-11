package ru.parser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.parser.model.Company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CompanyServiceImpl implements CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);
    private static final String INSERT_COMPANIES_SQL = "INSERT INTO companies (name, link) VALUES (?, ?)";
    private static final String SELECT_ALL_COMPANIES_SQL = "SELECT name, link FROM companies";
    private final JdbcTemplate jdbcTemplate;

    public CompanyServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveCompanies(List<Company> companies) {
        jdbcTemplate.batchUpdate(INSERT_COMPANIES_SQL, companies, companies.size(),
                (ps, company) -> {
                    ps.setString(1, company.getName());
                    ps.setString(2, company.getLink());
                });
        logger.info("Сохранено {} компаний", companies.size());
    }

    @Override
    public List<Company> getSavedCompanies() {
        return jdbcTemplate.query(SELECT_ALL_COMPANIES_SQL, (rs, rowNum) ->
                new Company(
                        rs.getString("name"),
                        rs.getString("link")
                ));
    }

    @Override
    public List<Company> findNewCompanies(List<Company> parsedCompanies) {
        List<Company> savedCompanies = getSavedCompanies();
        List<Company> result = new ArrayList<>();

        Set<String> savedLinks = new HashSet<>();
        for (Company company : savedCompanies) {
            savedLinks.add(company.getLink()); // Считаем, что link - уникальный идентификатор
        }

        for (Company company : parsedCompanies) {
            if (!savedLinks.contains(company.getLink())) {
                result.add(company);
            }
        }

        return result;
    }
} 