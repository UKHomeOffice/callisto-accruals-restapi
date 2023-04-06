package uk.gov.homeoffice.digital.sas.accruals.model;

import static uk.gov.homeoffice.digital.sas.accruals.constants.AgreementTypes.AHA_AGREEMENT_TYPE;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "agreementType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AhaContractualTerms.class, name = AHA_AGREEMENT_TYPE)
})
public interface ContractualTerms {
}
