package uk.gov.homeoffice.digital.sas.accruals.controllers;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.homeoffice.digital.sas.accruals.testUtils.AccrualFactory.createAccrualAnnualTargetHours;
import static uk.gov.homeoffice.digital.sas.accruals.testUtils.AccrualFactory.createContribution;
import static uk.gov.homeoffice.digital.sas.accruals.testUtils.AgreementFactory.createAgreement;
import static uk.gov.homeoffice.digital.sas.accruals.testUtils.CommonUtils.objectAsJsonString;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.homeoffice.digital.sas.accruals.model.Accrual;
import uk.gov.homeoffice.digital.sas.accruals.model.Agreement;
import uk.gov.homeoffice.digital.sas.accruals.repositories.AccrualsRepository;


@SpringBootTest
@TestPropertySource(locations="classpath:postgres.properties")
@Testcontainers
@AutoConfigureMockMvc
@Sql(scripts = "file:db/sql/003-function-get-impacted-accruals.sql")
class AccrualsControllerIntegrationTest {

  /**
   * Test class spins up a postgresTestContainer
   * Allowing the Json operators with AccrualsRepository to be run
   * Please ensure you have docker running to allow access to the docker daemon
   */
  @Container
  public static PostgreSQLContainer container =
      new PostgreSQLContainer<>("postgres:13.1")
            .withInitScript("init.sql");

  @DynamicPropertySource
  public static void overrideDbProperties(DynamicPropertyRegistry registry){
    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.username", container::getUsername);
    registry.add("spring.datasource.password", container::getPassword);
  }

  private static final String ACCRUAL_URL = "/resources/accruals";

  private static final String AGREEMENT_URL = "/resources/agreements";

  private static final UUID TENANT_ID =
      UUID.fromString("b7e813a2-bb28-11ec-8422-0242ac120002");

  private static final String TENANT_ID_PARAM = "?tenantId="+TENANT_ID;

  private static final UUID PERSON_ID =
      UUID.fromString("722875f4-e5de-40fa-b84d-eea99d1fba66");

  private static final LocalDate TIME_ENTRY_START_DATE = LocalDate.of(
      2023, 4, 2);

  private static final LocalDate AGREEMENT_START_DATE = LocalDate.of(
      2023,4,1);

  private static final LocalDate AGREEMENT_END_DATE = LocalDate.of(
      2023,  4,3);

  private String agreementId;
  @Autowired
  MockMvc mvc;

  @Autowired
  AccrualsRepository accrualsRepository;

  @BeforeEach
  public void setUp() throws Exception {
    Agreement agreement = createAgreement(AGREEMENT_START_DATE, AGREEMENT_END_DATE, PERSON_ID);

    MvcResult result = postAgreement(agreement)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andReturn();

    agreementId = JsonPath.read(result.getResponse().getContentAsString(), "$.items[0].id");
  }

  @AfterEach
  void tearDown() {
    accrualsRepository.deleteAll();
  }


  @Test
  void getAccrualsImpactedByTimeEntry_endPointIsCalled_shouldReturnEmptyList()
      throws Exception {
     String timeEntryId = "4d254823-0a7d-43b4-b948-b43266c9cbc1";

        getImpactedAccruals(timeEntryId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", empty()))
        .andReturn();
  }

  @Test
  void getAccrualsImpactedByTimeEntry_priorAccrualIsPresent_shouldGetPriorAndAccrualWithContribution()
      throws Exception {

    setUpAccrualsData(LocalDate.of(2023, 4, 1), 1, agreementId);

    String timeEntryId = getStoredTimeEntryId(LocalDate.of(2023,4,1));

    getImpactedAccruals(timeEntryId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(2)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2023-04-01")))
        .andExpect((jsonPath("$.items[0].contributions.timeEntries", hasKey(timeEntryId))))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2023-04-02")))
        .andReturn();
  }

  @Test
  void getAccrualsImpactedByTimeEntry_earlierContributionIsPresent_shouldGetContributionBeforeTimeEntry()
      throws Exception {

    setUpAccrualsData(LocalDate.of(2023, 3, 31), 2, agreementId);

    String timeEntryId = getStoredTimeEntryId(LocalDate.of(2023,4,1));

    getImpactedAccruals(timeEntryId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(3)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2023-03-31")))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2023-04-01")))
        .andExpect((jsonPath("$.items[1].contributions.timeEntries", hasKey(timeEntryId))))
        .andExpect(jsonPath("$.items[2].accrualDate", is("2023-04-02")));
  }

  @Test
  void getAccrualsImpactedByTimeEntry_timeEntryIsBetweenCurrentAccrual_shouldGetContributionCoveringTimeEntry()
      throws Exception {
    String timeEntryId = "4d254823-0a7d-43b4-b948-b43266c9cbc1";

    Accrual accrualDay1 = createAccrualAnnualTargetHours(
        PERSON_ID,
        LocalDate.of(2023, 3,30),
        UUID.fromString("4d254823-0a7d-43b4-b948-b43266c9cbc1"),
        UUID.fromString(agreementId));

    postAccrual(accrualDay1);

    Accrual accrualDay2 = createAccrualAnnualTargetHours(PERSON_ID,
        LocalDate.of(2023, 3,31),
        UUID.fromString("4d254823-0a7d-43b4-b948-b43266c9cbc1"),
        UUID.fromString(agreementId));

    postAccrual(accrualDay2);

    setUpAccrualsData(LocalDate.of(2023, 4, 1), 2, agreementId);

    getImpactedAccruals(timeEntryId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(5)))
        .andExpect(jsonPath("$.items[0].accrualDate", is("2023-03-30")))
        .andExpect((jsonPath("$.items[0].contributions.timeEntries", hasKey(timeEntryId))))
        .andExpect(jsonPath("$.items[1].accrualDate", is("2023-03-31")))
        .andExpect((jsonPath("$.items[1].contributions.timeEntries", hasKey(timeEntryId))))
        .andExpect(jsonPath("$.items[4].accrualDate", is("2023-04-03")));
  }

  private ResultActions getImpactedAccruals(String timeEntryId) throws Exception {
    return mvc.perform(get(ACCRUAL_URL
            + "?&tenantId=" + TENANT_ID
            + "&timeEntryId=" + timeEntryId
            + "&timeEntryStartDate=" + TIME_ENTRY_START_DATE
            + "&agreementEndDate=" + AGREEMENT_END_DATE)
            .contentType(MediaType.APPLICATION_JSON));
  }

  private void postAccrual(Accrual accrual) throws Exception {
    mvc.perform(post(ACCRUAL_URL + TENANT_ID_PARAM)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectAsJsonString(accrual)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andReturn();
  }

  private ResultActions postAgreement(Agreement agreement) throws Exception {
    return mvc.perform(post(AGREEMENT_URL + TENANT_ID_PARAM)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectAsJsonString(agreement)));
  }

  private String getStoredTimeEntryId(LocalDate timeEntryDate) throws Exception {

    final String result = mvc.perform(get(ACCRUAL_URL + TENANT_ID_PARAM +
        "&filter=accrualDate=='" + timeEntryDate + "'"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Set<String> timeEntryIdSet  =
        JsonPath.read(result, "$.items[0].contributions.timeEntries.keys()");

    return timeEntryIdSet.iterator().next();
  }

  private void setUpAccrualsData(LocalDate accrualEarliestContribution,
                                        int accrualPastEarliestDate, String agreementId)
      throws Exception {

    Accrual accrual = createAccrualAnnualTargetHours(PERSON_ID, accrualEarliestContribution,
        UUID.fromString(agreementId));

    for (int i = 0; i <= accrualPastEarliestDate; i++ ) {

      if (i > 0) {
        accrual.setAccrualDate(accrual.getAccrualDate().plusDays(1));
        accrual.setContributions(createContribution(UUID.randomUUID()));
      }

      mvc.perform(post(ACCRUAL_URL + TENANT_ID_PARAM)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectAsJsonString(accrual)))
          .andExpect(status().isOk());
    }
  }

}