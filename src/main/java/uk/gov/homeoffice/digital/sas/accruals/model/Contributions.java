package uk.gov.homeoffice.digital.sas.accruals.model;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Contributions {

  private Set<Contribution> items;
  private BigDecimal totalValue;
}
