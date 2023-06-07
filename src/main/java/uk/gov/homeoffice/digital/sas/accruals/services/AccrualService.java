package uk.gov.homeoffice.digital.sas.accruals.services;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.homeoffice.digital.sas.accruals.model.Accrual;
import uk.gov.homeoffice.digital.sas.accruals.repositories.AccrualsRepository;

@Slf4j
@Service
@Transactional
public class AccrualService {

  private final AccrualsRepository accrualsRepository;

  public AccrualService(AccrualsRepository accrualsRepository) {
    this.accrualsRepository = accrualsRepository;
  }

  @Transactional(readOnly = true)
  public List<Accrual> getAccrualsImpactedByTimeEntry(String tenantId,
      String timeEntryId, LocalDate timeEntryStartDate, LocalDate agreementEndDate) {

    return accrualsRepository.getAccrualsImpactedByTimeEntryWithPreviousDay(tenantId,
        timeEntryId, timeEntryStartDate, agreementEndDate);
  }
}
