package ru.parser.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.parser.model.Company;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private CompanyServiceImpl service() {
        return new CompanyServiceImpl(jdbcTemplate);
    }

    @Test
    void findNewCompaniesSkipsSavedAndDuplicatesInParsedList() {
        List<Company> saved = List.of(new Company("Old", "https://old/"));
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(saved);

        List<Company> parsed = List.of(
                new Company("A", "https://a/"),
                new Company("A копия с премиум-размещения", "https://a/"),
                new Company("Old", "https://old/"),
                new Company("B", "https://b/")
        );

        List<Company> result = service().findNewCompanies(parsed);

        assertThat(result).extracting(Company::getLink)
                .containsExactly("https://a/", "https://b/");
    }

    @Test
    void findNewCompaniesReturnsEmptyWhenAllSaved() {
        List<Company> saved = List.of(new Company("A", "https://a/"));
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(saved);

        List<Company> parsed = List.of(new Company("A", "https://a/"));

        assertThat(service().findNewCompanies(parsed)).isEmpty();
    }
}
