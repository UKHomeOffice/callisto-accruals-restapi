package uk.gov.homeoffice.digital.sas.accruals.controllers;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.homeoffice.digital.sas.accruals.testUtils.AccrualFactory.createAccrualAnnualTargetHours;
import static uk.gov.homeoffice.digital.sas.accruals.testUtils.AgreementFactory.createAgreement;
import static uk.gov.homeoffice.digital.sas.accruals.testUtils.CommonUtils.objectAsJsonString;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.homeoffice.digital.sas.accruals.model.Accrual;
import uk.gov.homeoffice.digital.sas.accruals.model.Agreement;
import uk.gov.homeoffice.digital.sas.accruals.model.ImpactedAccrualsBody;


@SpringBootTest
@TestPropertySource(locations="classpath:postgres.properties")
@Testcontainers
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {AccrualsControllerIntegrationTest.Initializer.class})
class AccrualsControllerIntegrationTest {

  /**
   * Test class spins up a postgresTestContainer
   * Allowing the Json operators with AccrualsRepository to be run
   */
  @Container
  public static PostgreSQLContainer postgreSQLContainer =
       new PostgreSQLContainer<>("postgres:11.1")
          .withInitScript("init.sql")
          .withDatabaseName("integration-tests-db")
          .withUsername("sa")
          .withPassword("sa");

  static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
          "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
          "spring.datasource.username=" + postgreSQLContainer.getUsername(),
          "spring.datasource.password=" + postgreSQLContainer.getPassword()
      ).applyTo(configurableApplicationContext.getEnvironment());
    }
  }

  private static final String ACCRUAL_URL = "/resources/accruals";

  private static final String AGREEMENT_URL = "/resources/agreements";

  private static final UUID TENANT_ID =
      UUID.fromString("b7e813a2-bb28-11ec-8422-0242ac120002");

  private static final String TENANT_ID_PARAM = "?tenantId="+TENANT_ID;

  private static final UUID PERSON_ID =
      UUID.fromString("722875f4-e5de-40fa-b84d-eea99d1fba66");

  private static final UUID TIME_ENTRY_ID =
      UUID.fromString("4d254823-0a7d-43b4-b948-b43266c9cbc1");

  private static final LocalDate TIME_ENTRY_START_DATE = LocalDate.of(
      2023, 5, 1);

  private static final LocalDate AGREEMENT_START_DATE = LocalDate.of(
      2023,4,1);

  private static final LocalDate AGREEMENT_END_DATE = LocalDate.of(
      2024,4,1);

  private String agreementId;

  @Autowired
  MockMvc mvc;

  @BeforeEach
  void setUp() throws Exception {
    Agreement agreement = createAgreement(AGREEMENT_START_DATE, AGREEMENT_END_DATE, PERSON_ID);

    MvcResult result = postAgreement(agreement)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andReturn();

    agreementId = JsonPath.read(result.getResponse().getContentAsString(), "$.items[0].id");
  }

  @Test
  void getAccrualsImpactedByTimeEntry_shouldReturnEmptyList() throws Exception {
    mvc.perform(get(ACCRUAL_URL + "/" +TIME_ENTRY_ID + TENANT_ID_PARAM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectAsJsonString(buildBody())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", empty()))
        .andReturn();
  }

  @Test
  void getAccrualsImpactedByTimeEntry_shouldGetPriorAndAccrualWithCorrectId() throws Exception {

    Accrual priorAccrual = createAccrualAnnualTargetHours(PERSON_ID,
        TIME_ENTRY_START_DATE.minusDays(1),
        UUID.fromString("6a699394-693d-4ca7-ba9d-deba7a5e9c09"), UUID.fromString(agreementId));

    String priorAccrualId = postAccrualAndGetId(priorAccrual);


    Accrual accrual = createAccrualAnnualTargetHours(
        PERSON_ID, TIME_ENTRY_START_DATE, TIME_ENTRY_ID, UUID.fromString(agreementId));

    String accrualId = postAccrualAndGetId(accrual);

    mvc.perform(get(ACCRUAL_URL + "/" + TIME_ENTRY_ID + TENANT_ID_PARAM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectAsJsonString(buildBody())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andExpect(jsonPath("$.items.length()", is(2)))
        .andExpect(jsonPath("$.items[0].id", is(priorAccrualId)))
        .andExpect(jsonPath("$.items[1].id", is(accrualId)))
        .andReturn();
  }

  //contribution before time entry

  //contribution before and after time entry

  private ImpactedAccrualsBody buildBody() {
    return ImpactedAccrualsBody.builder()
        .timeEntryStartDate(TIME_ENTRY_START_DATE)
        .agreementEndDate(AGREEMENT_END_DATE)
        .personId(PERSON_ID.toString())
        .build();
  }

  private String postAccrualAndGetId(Accrual accrual) throws Exception {
    MvcResult result = mvc.perform(post(ACCRUAL_URL + TENANT_ID_PARAM)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectAsJsonString(accrual)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andReturn();

    return JsonPath.read(result.getResponse().getContentAsString(), "$.items[0].id");
  }

  private ResultActions postAgreement(Agreement agreement) throws Exception {
    return mvc.perform(post(AGREEMENT_URL + TENANT_ID_PARAM)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectAsJsonString(agreement)));
  }

}