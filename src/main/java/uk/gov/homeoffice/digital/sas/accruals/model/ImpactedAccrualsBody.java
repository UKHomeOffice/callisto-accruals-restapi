package uk.gov.homeoffice.digital.sas.accruals.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImpactedAccrualsBody {

  @NotNull(message = "Time Entry Start Date should not be empty")
  LocalDate timeEntryStartDate;

  @NotNull(message = "Agreement End Date should not be empty")
  LocalDate agreementEndDate;

  @NotNull(message = "Person Id should not be empty")
  String personId;
}
