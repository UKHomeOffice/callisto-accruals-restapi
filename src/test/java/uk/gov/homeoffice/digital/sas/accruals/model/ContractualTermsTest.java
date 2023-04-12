package uk.gov.homeoffice.digital.sas.accruals.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import uk.gov.homeoffice.digital.sas.accruals.enums.SalaryBasis;
import uk.gov.homeoffice.digital.sas.accruals.enums.TermsAndConditions;

class ContractualTermsTest {
  
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void whenSerializingAhaAgreement_thenCorrectJsonProduced()
      throws JsonProcessingException {
    AhaContractualTerms contractualTerms = AhaContractualTerms.builder()
        .fteValue(BigDecimal.TEN)
        .termsAndConditions(TermsAndConditions.MODERNISED)
        .salaryBasis(SalaryBasis.NATIONAL)
        .build();

    Agreement agreement = Agreement.builder()
        .contractualTerms(contractualTerms)
        .build();


    String actual = objectMapper.writeValueAsString(agreement);

    String expected = """
    {
      "agreementType":"AHA",
      "fteValue":10,
      "termsAndConditions":"MODERNISED",
      "salaryBasis":"NATIONAL"
    }
    """;

    assertThat(objectMapper.readTree(actual)).contains(objectMapper.readTree(expected));
  }

  @Test
  void whenDeserializingJsonWithValidAgreementType_thenCorrectObjectProduced() throws JsonProcessingException {
    String json = """
    {"contractualTerms":
      {
        "agreementType":"AHA",
        "fteValue":1,
        "termsAndConditions":"MODERNISED",
        "salaryBasis":"NATIONAL"
      }
    }
    """;

    Agreement agreement = objectMapper
        .readerFor(Agreement.class)
        .readValue(json);

    ContractualTerms contractualTerms = agreement.getContractualTerms();
    assertThat(contractualTerms).isInstanceOf(AhaContractualTerms.class);

    AhaContractualTerms ahaContractualTerms = (AhaContractualTerms) contractualTerms;
    assertThat(ahaContractualTerms.getFteValue()).isEqualTo(BigDecimal.ONE);
    assertThat(ahaContractualTerms.getTermsAndConditions()).isEqualTo(TermsAndConditions.MODERNISED);
    assertThat(ahaContractualTerms.getSalaryBasis()).isEqualTo(SalaryBasis.NATIONAL);
  }

  @Test
  void whenDeserializingJsonWithUnknownAgreementType_thenThrowException() {
    String json = """
    {"contractualTerms":
      {
        "agreementType":"UNKNOWN",
        "fteValue":1,
        "termsAndConditions":"MODERNISED",
        "salaryBasis":"NATIONAL"
      }
    }
    """;

    Throwable thrown = catchThrowable(()-> objectMapper.readerFor(Agreement.class).readValue(json));
    assertThat(thrown).isInstanceOf(InvalidTypeIdException.class);
  }
}
