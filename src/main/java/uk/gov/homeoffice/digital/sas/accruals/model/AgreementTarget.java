package uk.gov.homeoffice.digital.sas.accruals.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uk.gov.homeoffice.digital.sas.jparest.annotation.Resource;
import uk.gov.homeoffice.digital.sas.jparest.models.BaseEntity;

@Resource(path = "agreement-targets")
@Entity(name = "agreement_target")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AgreementTarget extends BaseEntity {

  @NotNull(message = "Agreement ID should not be empty")
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "agreement_id")
  private UUID agreementId;

  @ManyToOne
  @JoinColumn(name = "agreement_id", nullable = false, insertable = false, updatable = false)
  @JsonIgnore
  private Agreement agreement;

  @NotNull(message = "Accrual type ID should not be empty")
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID accrualTypeId;

  @NotNull(message = "Target total should not be empty")
  @Min(0)
  private BigDecimal targetTotal;

}
