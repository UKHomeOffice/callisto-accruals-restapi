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

class AgreementTermsTest {
  
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void whenSerializingAhaAgreement_thenCorrectJsonProduced()
      throws JsonProcessingException {
    AhaAgreementTerms agreementTerms = AhaAgreementTerms.builder()
        .fteValue(BigDecimal.TEN)
        .termsAndConditions(TermsAndConditions.MODERNISED)
        .salaryBasis(SalaryBasis.NATIONAL)
        .build();

    Agreement agreement = Agreement.builder()
        .agreementTerms(agreementTerms)
        .build();


    String actual = objectMapper.writeValueAsString(agreement);

    String expected = """
    { "type":"AHA",
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
    {"agreementTerms":
      {
        "type":"AHA",
        "fteValue":1,
        "termsAndConditions":"MODERNISED",
        "salaryBasis":"NATIONAL"
      }
    }
    """;

    Agreement agreement = objectMapper
        .readerFor(Agreement.class)
        .readValue(json);

    AgreementTerms agreementTerms = agreement.getAgreementTerms();
    assertThat(agreementTerms).isInstanceOf(AhaAgreementTerms.class);

    AhaAgreementTerms ahaAgreementTerms = (AhaAgreementTerms) agreementTerms;
    assertThat(ahaAgreementTerms.getFteValue()).isEqualTo(BigDecimal.ONE);
    assertThat(ahaAgreementTerms.getTermsAndConditions()).isEqualTo(TermsAndConditions.MODERNISED);
    assertThat(ahaAgreementTerms.getSalaryBasis()).isEqualTo(SalaryBasis.NATIONAL);
  }

  @Test
  void whenDeserializingJsonWithUnknownAgreementType_thenThrowException() {
    String json = """
    {"agreementTerms":
      {
        "type":"UNKNOWN",
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
