package uk.gov.homeoffice.digital.sas.accruals.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
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

@Resource(path = "accruals")
@Entity(name = "accrual")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Accrual extends BaseEntity {

  @NotNull(message = "Agreement ID should not be null")
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "agreement_id")
  private UUID agreementId;

  @ManyToOne
  @JoinColumn(name = "agreement_id", nullable = false, insertable = false, updatable = false)
  @JsonIgnore
  private Agreement agreement;

  @NotNull(message = "Date should not be null")
  private LocalDate date;

  @NotNull(message = "Accrual type ID should not be null")
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "accrual_type_id")
  private UUID accrualTypeId;

  // TODO: check if cardinality is correct
  @ManyToOne
  @JoinColumn(name = "accrual_type_id", nullable = false, insertable = false, updatable = false)
  @JsonIgnore
  private AccrualType accrualType;

  @NotNull(message = "Balance should not be null")
  // TODO: check what min and max are
  private BigDecimal balance;

  @NotNull(message = "Target should not be null")
  // TODO: check what min and max are
  private BigDecimal target;

}
