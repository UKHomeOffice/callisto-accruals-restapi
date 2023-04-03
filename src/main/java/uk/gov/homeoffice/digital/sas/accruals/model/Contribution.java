package uk.gov.homeoffice.digital.sas.accruals.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Contribution {

  private UUID timeEntryId;
  private BigDecimal value;
}
