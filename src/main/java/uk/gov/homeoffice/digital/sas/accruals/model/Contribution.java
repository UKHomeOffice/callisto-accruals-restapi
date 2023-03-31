package uk.gov.homeoffice.digital.sas.accruals.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Setter
@Getter
public class Contribution {

  private UUID timeEntryId;

  @EqualsAndHashCode.Exclude
  private BigDecimal value;
}
