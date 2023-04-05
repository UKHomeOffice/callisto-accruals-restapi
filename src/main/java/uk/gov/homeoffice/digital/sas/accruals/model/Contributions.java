package uk.gov.homeoffice.digital.sas.accruals.model;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@AllArgsConstructor
@Builder
@Getter
public class Contributions {

  @JdbcTypeCode(SqlTypes.JSON)
  private Map<UUID, BigDecimal> timeEntries;

  @Min(0)
  private BigDecimal total;
}
