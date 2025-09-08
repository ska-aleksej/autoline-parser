package ru.parser.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    public DataSource dataSource(DatabaseProperties dbProps) {
        logger.info("Creating DataSource with URL: {}", dbProps.getUrl());
        
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dbProps.getUrl());
        dataSource.setUsername(dbProps.getUsername());
        dataSource.setPassword(dbProps.getPassword());
        dataSource.setDriverClassName(dbProps.getDriverClassName());
        dataSource.setMaximumPoolSize(10);
        dataSource.setConnectionTimeout(30000);
        
        logger.info("DataSource created successfully");
        return dataSource;
    }

    @Bean
    public DatabaseInitializer databaseInitializer(DataSource dataSource) {
        return new DatabaseInitializer(dataSource);
    }

    public static class DatabaseInitializer {
        private final DataSource dataSource;
        private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

        public DatabaseInitializer(DataSource dataSource) {
            this.dataSource = dataSource;
            initDatabase();
        }

        private void initDatabase() {
            logger.info("Initializing database schema...");
            
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                stmt.execute("CREATE TABLE IF NOT EXISTS companies (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL," +
                        "link VARCHAR(255) NOT NULL," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
                
                logger.info("Database schema initialized successfully");
                
            } catch (SQLException e) {
                logger.error("Failed to initialize database schema", e);
                throw new RuntimeException("Database initialization failed", e);
            }
        }
    }
} 