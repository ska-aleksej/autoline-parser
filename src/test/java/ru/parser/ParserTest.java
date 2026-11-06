package ru.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.parser.model.Company;
import ru.parser.model.CompanyDetails;
import ru.parser.model.ScanType;
import ru.parser.service.CompanyService;
import ru.parser.service.MailService;
import ru.parser.service.WebParserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParserTest {

    @Mock
    private CompanyService companyService;
    @Mock
    private MailService mailService;
    @Mock
    private WebParserService webParserService;

    @Captor
    private ArgumentCaptor<List<Company>> reportCaptor;

    private Parser parser() {
        return new Parser(companyService, mailService, webParserService, Optional.empty());
    }

    private Company company(String link) {
        return new Company("Company " + link, link);
    }

    @Test
    void detailsFailureDoesNotKillScanAndCompanyStaysInReport() throws Exception {
        List<Company> companies = List.of(company("https://a/"), company("https://b/"));
        when(companyService.isEmpty()).thenReturn(false);
        when(webParserService.getAllCompaniesByType(ScanType.FULL)).thenReturn(companies);
        when(companyService.findNewCompanies(companies)).thenReturn(new ArrayList<>(companies));
        when(webParserService.getCompanyDetails("https://a/"))
                .thenReturn(new CompanyDetails("10", "N/A", "5", "Москва"));
        when(webParserService.getCompanyDetails("https://b/"))
                .thenThrow(new RuntimeException("HTTP error fetching URL. Status=429"));

        assertThatCode(() -> parser().parse(ScanType.FULL)).doesNotThrowAnyException();

        verify(companyService).saveCompanies(companies);
        verify(mailService).sendHtmlEmail(reportCaptor.capture());
        List<Company> sent = reportCaptor.getValue();
        assertThat(sent).extracting(Company::getLink).containsExactly("https://a/", "https://b/");
        assertThat(sent.get(1).getDetails()).isNotNull();
        assertThat(sent.get(1).getDetails().getYearsOnSite()).isEqualTo("N/A");
    }

    @Test
    void allDetailsFailuresStillProduceReport() throws Exception {
        List<Company> companies = List.of(
                company("https://c1/"), company("https://c2/"), company("https://c3/"),
                company("https://c4/"), company("https://c5/"));
        when(companyService.isEmpty()).thenReturn(false);
        when(webParserService.getAllCompaniesByType(ScanType.ONLY_CHINESE)).thenReturn(companies);
        when(companyService.findNewCompanies(companies)).thenReturn(new ArrayList<>(companies));
        when(webParserService.getCompanyDetails(anyString()))
                .thenThrow(new RuntimeException("HTTP error fetching URL. Status=429"));

        assertThatCode(() -> parser().parse(ScanType.ONLY_CHINESE)).doesNotThrowAnyException();

        verify(webParserService, times(3)).getCompanyDetails(anyString());
        verify(mailService).sendHtmlEmail(reportCaptor.capture());
        List<Company> sent = reportCaptor.getValue();
        assertThat(sent).hasSize(5);
        assertThat(sent).allSatisfy(c -> {
            assertThat(c.getDetails()).isNotNull();
            assertThat(c.getDetails().getYearsOnSite()).isEqualTo("N/A");
        });
    }
}
