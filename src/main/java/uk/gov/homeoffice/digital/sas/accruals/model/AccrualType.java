package uk.gov.homeoffice.digital.sas.accruals.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.homeoffice.digital.sas.accruals.enums.MeasurementUnit;
import uk.gov.homeoffice.digital.sas.jparest.annotation.Resource;
import uk.gov.homeoffice.digital.sas.jparest.models.BaseEntity;

@Resource(path = "accrual-types")
@Entity(name = "accrual_type")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@Getter
@Setter
public class AccrualType extends BaseEntity {

  // TODO: check if enum or length restrictions
  @NotNull(message = "Accrual type name should not be empty")
  private String name;

  @NotNull(message = "Measurement unit should not be empty")
  @Enumerated(EnumType.STRING)
  private MeasurementUnit measurementUnit;
}
