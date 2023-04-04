package uk.gov.homeoffice.digital.sas.accruals.model;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class Contributions {

  @JdbcTypeCode(SqlTypes.JSON)
  private Set<Contribution> timeEntries;

  @Min(0)
  private BigDecimal total;
}
