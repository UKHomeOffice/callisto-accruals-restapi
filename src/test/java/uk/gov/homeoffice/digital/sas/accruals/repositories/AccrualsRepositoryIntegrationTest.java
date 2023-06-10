package uk.gov.homeoffice.digital.sas.accruals.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.homeoffice.digital.sas.accruals.model.Accrual;

@SpringBootTest
@TestPropertySource(locations = "classpath:postgres.properties")
@Testcontainers
@Transactional
@Sql(scripts = {"file:db/sql/003-function-get-impacted-accruals.sql", "/create-data.sql"})
class AccrualsRepositoryIntegrationTest {

  private static final String TENANT_ID = "00000000-0000-0000-0000-000000000000";
  private static final String PERSON_ID = "00000000-0000-0000-0000-000000000001";

  @Container
  public static PostgreSQLContainer<?> container = createContainer();

  @Autowired
  private AccrualsRepository accrualsRepository;

  private static PostgreSQLContainer<?> createContainer() {
    try (PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:13.1")
        .withInitScript("init.sql")) {
      return container;
    }
  }

  @DynamicPropertySource
  public static void overrideDbProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.username", container::getUsername);
    registry.add("spring.datasource.password", container::getPassword);
  }

  @Test
  void getAccrualsImpactedByTimeEntryWithPreviousDay_timeEntryIdContributionBeforeStartDate() {
    LocalDate timeEntryStartDate = LocalDate.parse("2022-04-03");
    LocalDate timeEntryEndDate = LocalDate.parse("2022-04-03");
    String timeEntryId = "10000000-0000-0000-0000-000000000002";
    List<Accrual> accruals =
        accrualsRepository.getAccrualsImpactedByTimeEntryWithPreviousDay(TENANT_ID,
            PERSON_ID,
            timeEntryId,
            timeEntryStartDate,
            timeEntryEndDate);
    assertThat(accruals).hasSize(5);
  }

  @Test
  void getAccrualsImpactedByTimeEntryWithPreviousDay_timeEntryIdContributionOnStartDate() {
    LocalDate timeEntryStartDate = LocalDate.parse("2022-04-03");
    LocalDate timeEntryEndDate = LocalDate.parse("2022-04-03");
    String timeEntryId = "10000000-0000-0000-0000-000000000003";
    List<Accrual> accruals =
        accrualsRepository.getAccrualsImpactedByTimeEntryWithPreviousDay(TENANT_ID,
            PERSON_ID,
            timeEntryId,
            timeEntryStartDate,
            timeEntryEndDate);
    assertThat(accruals).hasSize(4);
  }

  @Test
  void getAccrualsImpactedByTimeEntryWithPreviousDay_timeEntrySpansTwoAgreementPeriods() {
    LocalDate timeEntryStartDate = LocalDate.parse("2022-04-05");
    LocalDate timeEntryEndDate = LocalDate.parse("2022-04-06");
    String timeEntryId = "10000000-0000-0000-0000-000000000005";
    List<Accrual> accruals =
        accrualsRepository.getAccrualsImpactedByTimeEntryWithPreviousDay(TENANT_ID,
            PERSON_ID,
            timeEntryId,
            timeEntryStartDate,
            timeEntryEndDate);
    assertThat(accruals).hasSize(7);
  }
}