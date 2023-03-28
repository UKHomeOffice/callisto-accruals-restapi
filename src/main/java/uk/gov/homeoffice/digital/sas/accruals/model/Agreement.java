package uk.gov.homeoffice.digital.sas.accruals.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.homeoffice.digital.sas.accruals.enums.SalaryBasis;
import uk.gov.homeoffice.digital.sas.accruals.enums.TermsAndConditions;
import uk.gov.homeoffice.digital.sas.jparest.annotation.Resource;
import uk.gov.homeoffice.digital.sas.jparest.models.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Resource(path = "agreements")
@Entity(name = "agreement")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@Getter
@Setter
public class Agreement extends BaseEntity {

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




}
