package uk.gov.homeoffice.digital.sas.accruals.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Setter
@Getter
public class Contribution {

  // TODO: check if this annotation is needed
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID timeEntryId;

  private BigDecimal value;
}
