package uk.gov.homeoffice.digital.sas.accruals;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.jayway.jsonpath.JsonPath;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.homeoffice.digital.sas.accruals.enums.AccrualType;
import uk.gov.homeoffice.digital.sas.accruals.enums.SalaryBasis;
import uk.gov.homeoffice.digital.sas.accruals.enums.TermsAndConditions;
import uk.gov.homeoffice.digital.sas.accruals.model.Accrual;
import uk.gov.homeoffice.digital.sas.accruals.model.Agreement;
import uk.gov.homeoffice.digital.sas.accruals.model.AgreementTarget;
import uk.gov.homeoffice.digital.sas.accruals.model.Contributions;
import uk.gov.homeoffice.digital.sas.jparest.models.BaseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ComponentScan("uk.gov.homeoffice.digital.sas.accruals")
class CallistoAccrualsRestApiIntegrationTest {

  private static final UUID TENANT_ID_UUID = UUID.randomUUID();
  private static final String TENANT_ID = TENANT_ID_UUID.toString();
  private static final String TENANT_ID_PARAM = "?tenantId=" + TENANT_ID;
  private static final UUID PERSON_ID_UUID = UUID.randomUUID();
  private static final String PERSON_ID = PERSON_ID_UUID.toString();
  private static final String ACCRUAL_URL = "/resources/accruals";
  private static final String AGREEMENT_URL = "/resources/agreements";
  private static final String AGREEMENT_TARGET_URL = "/resources/agreement-targets";

  private final Faker faker = new Faker();

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldPostAccrualResourcesSuccessfully() throws Exception {

    Agreement agreement = Agreement.builder()
        .personId(UUID.fromString(PERSON_ID))
        .fteValue(randomBigDecimal(4, 0, 1))
        .termsAndConditions(randomEnum(TermsAndConditions.class))
        .salaryBasis(randomEnum(SalaryBasis.class))
        .startDate(LocalDate.of(2023, Month.APRIL, 1))
        .endDate(LocalDate.of(2024, Month.MARCH, 31))
        .build();
    agreement.setTenantId(TENANT_ID_UUID);

    MvcResult result = postResource(agreement, AGREEMENT_URL)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andReturn();
    UUID agreementId = getResourceId(result);

    AgreementTarget agreementTarget = AgreementTarget.builder()
        .agreementId(agreementId)
        .accrualTypeId(AccrualType.NIGHT_HOURS.getId())
        .targetTotal(randomBigDecimal(2, 0, 5000))
        .build();
    agreementTarget.setTenantId(TENANT_ID_UUID);

    postResource(agreementTarget, AGREEMENT_TARGET_URL)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andReturn();

    Contributions contributions = Contributions.builder()
        .timeEntries(Map.of(UUID.randomUUID(), BigDecimal.ONE))
        .total(BigDecimal.TEN)
        .build();

    Accrual accrual = Accrual.builder()
        .agreementId(agreementId)
        .date(LocalDate.of(2023, Month.APRIL, 15))
        .accrualTypeId(AccrualType.NIGHT_HOURS.getId())
        .cumulativeTotal(randomBigDecimal(2, 0, 5000))
        .cumulativeTarget(randomBigDecimal(2, 0, 5000))
        .contributions(contributions)
        .build();
    accrual.setTenantId(TENANT_ID_UUID);

    postResource(accrual, ACCRUAL_URL)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", not(empty())))
        .andReturn();
  }

  private ResultActions postResource(BaseEntity resource, String url) throws Exception {
    return mvc.perform(post(url + TENANT_ID_PARAM)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(resource)));
  }

  private UUID getResourceId(MvcResult result) throws UnsupportedEncodingException {
    return UUID.fromString(
        JsonPath.read(result.getResponse().getContentAsString(), "$.items[0].id"));
  }

  private BigDecimal randomBigDecimal(int maxNumberOfDecimals, long min, long max) {
    return new BigDecimal(
        String.valueOf(faker.number().randomDouble(maxNumberOfDecimals, min, max)));
  }

  private <E extends Enum<E>> E randomEnum(Class<E> enumClass) {
    return enumClass.getEnumConstants()[new Random().nextInt(enumClass.getEnumConstants().length)];
  }
}
