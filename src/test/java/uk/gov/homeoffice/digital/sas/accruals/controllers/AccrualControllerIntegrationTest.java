package uk.gov.homeoffice.digital.sas.accruals.controllers;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.homeoffice.digital.sas.accruals.repositories.AccrualRepository;


@SpringBootTest
@TestPropertySource(locations="classpath:postgres.properties")
@Testcontainers
@AutoConfigureMockMvc
@Sql(scripts = {"file:db/sql/003-function-get-impacted-accruals.sql",
    "/controller-it-set-up-data.sql"})
class AccrualControllerIntegrationTest {

  /**
   * Test class spins up a postgresTestContainer
   * Allowing the Json operators with AccrualsRepository to be run
   * Please ensure you have docker running to allow access to the docker daemon
   */
  @Container
  public static PostgreSQLContainer container = createContainer();

  private static PostgreSQLContainer<?> createContainer() {
    try (PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:13.1")
        .withInitScript("init.sql")) {
      return container;
    }
  }

  @DynamicPropertySource
  public static void overrideDbProperties(DynamicPropertyRegistry registry){
    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.username", container::getUsername);
    registry.add("spring.datasource.password", container::getPassword);
  }

  private static final String ACCRUAL_URL = "/resources/accruals";

  private static final UUID TENANT_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000000");

  private static final UUID PERSON_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000001");


  @Autowired
  MockMvc mvc;

  @Autowired
  AccrualRepository accrualRepository;


  @Test
  void getAccrualsImpactedByTimeEntry_endPointIsCalled_shouldReturnEmptyList()
      throws Exception {
     String timeEntryId = "4d254823-0a7d-43b4-b948-b43266c9cbc1";
    LocalDate timeEntryStartDate = LocalDate.of(2022, 3, 31);
    LocalDate timeEntryEndDate = LocalDate.of(2022, 3, 31);

        getImpactedAccruals(timeEntryId, timeEntryStartDate, timeEntryEndDate)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", empty()))
        .andReturn();
  }

  @Test
  void getAccrualsImpactedByTimeEntry_priorAccrualIsPresent_shouldGetPriorAndAccrualUntilEndOfAgreement()
      throws Exception {

    String timeEntryId = "10000000-0000-0000-0000-000000000002";
    LocalDate timeEntryStartDate = LocalDate.of(2022, 4, 2);
    LocalDate timeEntryEndDate = LocalDate.of(2022, 4, 2);

    getImpactedAccruals(timeEntryId, timeEntryStartDate, timeEntryEndDate)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(11)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2022-04-01")))
        .andExpect((jsonPath("$.items[1].contributions.timeEntries", hasKey(timeEntryId))))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2022-04-02")))
        .andExpect(jsonPath("$.items[10].accrualDate", is("2022-04-11")))
        .andReturn();
  }

  @Test
  void getAccrualsImpactedByTimeEntry_earlierContributionIsPresent_shouldGetContributionBeforeTimeEntry()
      throws Exception {

    String timeEntryId = "10000000-0000-0000-0000-000000000002";
    LocalDate timeEntryStartDate = LocalDate.of(2022, 4, 3);
    LocalDate timeEntryEndDate = LocalDate.of(2022, 4, 3);

    getImpactedAccruals(timeEntryId, timeEntryStartDate, timeEntryEndDate)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(11)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2022-04-01")))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2022-04-02")))
        .andExpect((jsonPath("$.items[1].contributions.timeEntries", hasKey(timeEntryId))))
        .andExpect(jsonPath("$.items[2].accrualDate", is("2022-04-03")))
        .andExpect(jsonPath("$.items[10].accrualDate", is("2022-04-11")));
  }

  @Test
  void getAccrualsImpactedByTimeEntry_currentAccrualSpansTwoDays_shouldGetContributionBeforeCurrentAccrual()
      throws Exception {

    String timeEntryId = "10000000-0000-0000-0000-000000000008";
    LocalDate timeEntryStartDate = LocalDate.of(2022, 4, 9);
    LocalDate timeEntryEndDate = LocalDate.of(2022, 4, 9);


    getImpactedAccruals(timeEntryId, timeEntryStartDate, timeEntryEndDate)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(5)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2022-04-07")))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2022-04-08")))
        .andExpect((jsonPath("$.items[1].contributions.timeEntries", hasKey(timeEntryId))))
        .andExpect(jsonPath("$.items[2].accrualDate", is("2022-04-09")))
        .andExpect((jsonPath("$.items[2].contributions.timeEntries", hasKey(timeEntryId))))
        .andExpect(jsonPath("$.items[4].accrualDate", is("2022-04-11")));
  }

  @Test
  void getAccrualsImpactedByTimeEntry_currentAccrualEndsOnAgreement_timeEntryUpdatedToDayBefore()
      throws Exception {
    LocalDate timeEntryStartDate = LocalDate.parse("2022-04-10");
    LocalDate timeEntryEndDate = LocalDate.parse("2022-04-10");
    String timeEntryId = "10000000-0000-0000-0000-000000000011";

    getImpactedAccruals(timeEntryId, timeEntryStartDate, timeEntryEndDate)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(3)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2022-04-09")))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2022-04-10")))
        .andExpect(jsonPath("$.items[2].accrualDate", is("2022-04-11")))
        .andExpect((jsonPath("$.items[2].contributions.timeEntries", hasKey(timeEntryId))));
  }

  @Test
  void getAccrualsImpactedByTimeEntry_currentAccrualOnFirstDayAgreement_timeEntryUpdatedToEndNextDay()
      throws Exception {
    LocalDate timeEntryStartDate = LocalDate.parse("2022-04-13");
    LocalDate timeEntryEndDate = LocalDate.parse("2022-04-13");
    String timeEntryId = "10000000-0000-0000-0000-000000000012";

    getImpactedAccruals(timeEntryId, timeEntryStartDate, timeEntryEndDate)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(4)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2022-04-11")))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2022-04-12")))
        .andExpect(jsonPath("$.items[1].contributions.timeEntries", hasKey(timeEntryId)))
        .andExpect(jsonPath("$.items[2].accrualDate", is("2022-04-13")))
        .andExpect(jsonPath("$.items[3].accrualDate", is("2022-04-14")));
  }

  @Test
  void getAccrualsImpactedByTimeEntry_currentAccrualOvernightOnLastDayOfAgreement_timeEntryUpdatedToDayEarlier()
      throws Exception {
    LocalDate timeEntryStartDate = LocalDate.parse("2022-04-09");
    LocalDate timeEntryEndDate = LocalDate.parse("2022-04-10");
    String timeEntryId = "10000000-0000-0000-0000-000000000010";

    getImpactedAccruals(timeEntryId, timeEntryStartDate, timeEntryEndDate)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(4)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2022-04-08")))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2022-04-09")))
        .andExpect(jsonPath("$.items[2].accrualDate", is("2022-04-10")))
        .andExpect(jsonPath("$.items[2].contributions.timeEntries", hasKey(timeEntryId)))
        .andExpect(jsonPath("$.items[3].accrualDate", is("2022-04-11")));
  }

  @Test
  void getAccrualsImpactedByTimeEntry_currentAccrualOvernightOnLastDayOfAgreement_timeEntryUpdatedToDayLater()
      throws Exception {
    LocalDate timeEntryStartDate = LocalDate.parse("2022-04-10");
    LocalDate timeEntryEndDate = LocalDate.parse("2022-04-11");
    String timeEntryId = "10000000-0000-0000-0000-000000000010";

    getImpactedAccruals(timeEntryId, timeEntryStartDate, timeEntryEndDate)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(3)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2022-04-09")))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2022-04-10")))
        .andExpect(jsonPath("$.items[1].contributions.timeEntries", hasKey(timeEntryId)))
        .andExpect(jsonPath("$.items[2].accrualDate", is("2022-04-11")));
  }

  @Test
  void getAccrualsImpactedByTimeEntry_timeEntryEndsInDifferentAgreement_returnEndOfSecondAgreement()
      throws Exception {
    LocalDate timeEntryStartDate = LocalDate.parse("2022-04-11");
    LocalDate timeEntryEndDate = LocalDate.parse("2022-04-11");
    String timeEntryId = "10000000-0000-0000-0000-000000000111";

    getImpactedAccruals(timeEntryId, timeEntryStartDate, timeEntryEndDate)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(5)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2022-04-10")))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2022-04-11")))
        .andExpect(jsonPath("$.items[1].contributions.timeEntries", hasKey(timeEntryId)))
        .andExpect(jsonPath("$.items[2].accrualDate", is("2022-04-12")))
        .andExpect(jsonPath("$.items[4].accrualDate", is("2022-04-14")));
  }

  @Test
  void getAccrualsImpactedByTimeEntry_previousContributionIsInPreviousAgreement_returnEndOfSecondAgreement()
      throws Exception {
    LocalDate timeEntryStartDate = LocalDate.parse("2022-04-12");
    LocalDate timeEntryEndDate = LocalDate.parse("2022-04-12");
    String timeEntryId = "10000000-0000-0000-0000-000000000011";
    getImpactedAccruals(timeEntryId, timeEntryStartDate, timeEntryEndDate)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(5)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2022-04-10")))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2022-04-11")))
        .andExpect(jsonPath("$.items[1].contributions.timeEntries", hasKey(timeEntryId)))
        .andExpect(jsonPath("$.items[2].accrualDate", is("2022-04-12")))
        .andExpect(jsonPath("$.items[4].accrualDate", is("2022-04-14")));
  }

  @Test
  void getAccrualsImpactedByTimeEntry_previousContributionIsInFutureAgreement_returnEndOfFirstAgreement()
      throws Exception {
    LocalDate timeEntryStartDate = LocalDate.parse("2022-04-11");
    LocalDate timeEntryEndDate = LocalDate.parse("2022-04-11");
    String timeEntryId = "10000000-0000-0000-0000-000000000012";

    getImpactedAccruals(timeEntryId, timeEntryStartDate, timeEntryEndDate)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(5)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2022-04-10")))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2022-04-11")))
        .andExpect(jsonPath("$.items[2].accrualDate", is("2022-04-12")))
        .andExpect(jsonPath("$.items[2].contributions.timeEntries", hasKey(timeEntryId)))
        .andExpect(jsonPath("$.items[4].accrualDate", is("2022-04-14")));
  }

  private ResultActions getImpactedAccruals(String timeEntryId, LocalDate timeEntryStartDate,
                                            LocalDate timeEntryEndDate) throws Exception {
    return mvc.perform(get(ACCRUAL_URL
            + "?tenantId=" + TENANT_ID
            + "&personId=" + PERSON_ID
            + "&timeEntryId=" + timeEntryId
            + "&timeEntryStartDate=" + timeEntryStartDate
            + "&timeEntryEndDate=" + timeEntryEndDate)
            .contentType(MediaType.APPLICATION_JSON));
  }
}