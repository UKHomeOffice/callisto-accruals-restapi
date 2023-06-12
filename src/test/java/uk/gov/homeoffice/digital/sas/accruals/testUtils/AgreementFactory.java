package uk.gov.homeoffice.digital.sas.accruals.testUtils;

import java.time.LocalDate;
import java.util.UUID;
import uk.gov.homeoffice.digital.sas.accruals.model.Agreement;

public class AgreementFactory {

  public static Agreement createAgreement(LocalDate startDate, LocalDate endDate, UUID personId) {
    return Agreement.builder()
        .startDate(startDate)
        .endDate(endDate)
        .personId(personId)
        .build();
  }
}
