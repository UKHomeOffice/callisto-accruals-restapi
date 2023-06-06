package uk.gov.homeoffice.digital.sas.accruals.testUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import uk.gov.homeoffice.digital.sas.accruals.enums.AccrualType;
import uk.gov.homeoffice.digital.sas.accruals.model.Accrual;
import uk.gov.homeoffice.digital.sas.accruals.model.Contributions;

public class AccrualFactory {

  public static Accrual createAccrualAnnualTargetHours(UUID personId, LocalDate accrualDate) {
    return createAccrualAnnualTargetHours(personId, accrualDate, UUID.randomUUID(), UUID.randomUUID());
  }

  public static Accrual createAccrualAnnualTargetHours(UUID personId, LocalDate accrualDate,
                                                       UUID timeEntryId, UUID agreementId) {
    return Accrual.builder()
        .personId(personId)
        .accrualDate(accrualDate)
        .accrualTypeId(AccrualType.ANNUAL_TARGET_HOURS.getId())
        .agreementId(agreementId)
        .cumulativeTotal(BigDecimal.valueOf(360))
        .cumulativeTarget(BigDecimal.valueOf(1000))
        .contributions(createContribution(timeEntryId))
        .build();
  }

  private static Contributions createContribution(UUID timeEntryId) {
    BigDecimal value = BigDecimal.valueOf(360);
    Map<UUID, BigDecimal> timeEntries = new HashMap<>();
    timeEntries.put(timeEntryId, value);
    BigDecimal total = BigDecimal.valueOf(360);

    return Contributions.builder()
        .timeEntries(timeEntries)
        .total(total)
        .build();

  }
}
