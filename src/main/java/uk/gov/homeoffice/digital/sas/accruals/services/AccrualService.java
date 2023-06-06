package uk.gov.homeoffice.digital.sas.accruals.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.homeoffice.digital.sas.accruals.model.Accrual;
import uk.gov.homeoffice.digital.sas.accruals.model.ImpactedAccrualsBody;
import uk.gov.homeoffice.digital.sas.accruals.repositories.AccrualsRepository;
import uk.gov.homeoffice.digital.sas.jparest.exceptions.UnknownResourcePropertyException;

@Slf4j
@Service
public class AccrualService {

  private final AccrualsRepository accrualsRepository;

  @Autowired
  ObjectMapper objectMapper;

  public AccrualService(AccrualsRepository accrualsRepository) {
    this.accrualsRepository = accrualsRepository;
  }

  public List<Accrual> getAccrualsImpactedByTimeEntry(
      String timeEntryId, String tenantId, String body
  ) {
    ImpactedAccrualsBody mappedBody;
    try {
      mappedBody = objectMapper.readValue(body, ImpactedAccrualsBody.class);
    } catch (JsonProcessingException ex) {
      throw new UnknownResourcePropertyException();
    }

    return accrualsRepository.getAccrualsImpactedByTimeEntryWithPreviousDay(
        timeEntryId, mappedBody.getTimeEntryStartDate(),
        mappedBody.getAgreementEndDate(), mappedBody.getPersonId(),
        tenantId);
  }
}
