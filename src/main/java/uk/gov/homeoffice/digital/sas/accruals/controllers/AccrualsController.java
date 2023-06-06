package uk.gov.homeoffice.digital.sas.accruals.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

  @GetMapping("/{timeEntryId}")
  public ApiResponse<Accrual> getAccrualsImpactedByTimeEntry(
      @PathVariable("timeEntryId") String timeEntryId,
      @RequestBody String body,
      @RequestParam("tenantId") String tenantId) {

    return new ApiResponse<>(accrualService.getAccrualsImpactedByTimeEntry(
        timeEntryId, tenantId, body));

  }
}
