package uk.gov.homeoffice.digital.sas.accruals.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.homeoffice.digital.sas.jparest.annotation.Resource;
import uk.gov.homeoffice.digital.sas.jparest.models.BaseEntity;

@Resource(path = "agreement-targets")
@Entity(name = "agreement_target")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@Getter
@Setter
public class AgreementTarget extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "agreement_id", nullable = false, updatable = false)
  @JsonIgnore
  private Agreement agreement;

  @OneToOne
  @JoinColumn(name = "accrual_type_id", nullable = false, updatable = false)
  @JsonIgnore
  private AccrualType accrualType;

  @NotNull(message = "Target total should not be empty")
  private BigDecimal targetTotal;

}
