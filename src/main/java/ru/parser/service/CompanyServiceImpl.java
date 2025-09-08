package ru.parser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.parser.model.Company;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CompanyServiceImpl implements CompanyService {
    private static final Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private final DataSource dataSource;

    public CompanyServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveCompanies(List<Company> companies) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // Начинаем транзакцию
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {

                // Вставляем новые данные
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO companies (name, link) VALUES (?, ?)")) {
                    for (Company company : companies) {
                        pstmt.setString(1, company.getName());
                        pstmt.setString(2, company.getLink());
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<Company> getSavedCompanies() throws SQLException {
        List<Company> companies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, link FROM companies")) {

            while (rs.next()) {
                companies.add(new Company(
                        rs.getString("name"),
                        rs.getString("link")
                ));
            }
        }

        return companies;
    }

    @Override
    public List<Company> findNewCompanies(List<Company> parsedCompanies) throws SQLException {
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