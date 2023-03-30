package uk.gov.homeoffice.digital.sas.accruals.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uk.gov.homeoffice.digital.sas.accruals.enums.SalaryBasis;
import uk.gov.homeoffice.digital.sas.accruals.enums.TermsAndConditions;
import uk.gov.homeoffice.digital.sas.jparest.annotation.Resource;
import uk.gov.homeoffice.digital.sas.jparest.models.BaseEntity;

@Resource(path = "agreements")
@Entity(name = "agreement")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Agreement extends BaseEntity {

  @NotNull(message = "Person ID should not be null")
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID personId;

  //TODO: check with Arcs if correct field
  @NotNull(message = "Version should not be null")
  private Integer version;

  @DecimalMin(value = "0.0", inclusive = false)
  @Digits(integer = 1, fraction = 4)
  @DecimalMax(value = "1.0")
  private BigDecimal fteValue;

  @NotNull(message = "Terms and conditions should not be empty")
  @Enumerated(EnumType.STRING)
  private TermsAndConditions termsAndConditions;

  @NotNull(message = "Salary basis should not be empty")
  @Enumerated(EnumType.STRING)
  private SalaryBasis salaryBasis;

  @NotNull(message = "Start date should not be empty")
  private LocalDate startDate;

  @NotNull(message = "End date should not be empty")
  private LocalDate endDate;

  @OneToMany(mappedBy = "agreement")
  @JsonIgnore
  @EqualsAndHashCode.Exclude
  private List<AgreementTarget> agreementTargets;

  @OneToMany(mappedBy = "agreement")
  @JsonIgnore
  @EqualsAndHashCode.Exclude
  private List<Accrual> accruals;

}
