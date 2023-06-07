package uk.gov.homeoffice.digital.sas.accruals.controllers;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.homeoffice.digital.sas.accruals.model.Accrual;
import uk.gov.homeoffice.digital.sas.accruals.services.AccrualService;
import uk.gov.homeoffice.digital.sas.jparest.web.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/resources/accruals")
public class AccrualsController {

  private final AccrualService accrualService;

  public AccrualsController(AccrualService accrualService) {
    this.accrualService = accrualService;
  }

  @GetMapping(params = {"tenantId", "timeEntryId", "timeEntryStartDate", "agreementEndDate"})
  public ApiResponse<Accrual> getAccrualsImpactedByTimeEntry(
      @RequestParam String tenantId,
      @RequestParam String timeEntryId,
      @RequestParam LocalDate timeEntryStartDate,
      @RequestParam LocalDate agreementEndDate) {

    return new ApiResponse<>(accrualService.getAccrualsImpactedByTimeEntry(tenantId,
        timeEntryId, timeEntryStartDate, agreementEndDate));
  }
}
