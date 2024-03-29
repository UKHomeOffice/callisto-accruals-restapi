package uk.gov.homeoffice.digital.sas.accruals.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.homeoffice.digital.sas.accruals.model.Accrual;

@Repository
public interface AccrualRepository extends JpaRepository<Accrual, UUID> {

  @Query(value =
      "SELECT * FROM "
          + "accruals.get_impacted_accruals("
          + ":tenantId, :personId, :timeEntryId, :timeEntryStartDate,  :timeEntryEndDate);",
      nativeQuery = true)
  List<Accrual> getAccrualsImpactedByTimeEntryWithPreviousDay(
      @Param("tenantId") String tenantId,
      @Param("personId") String personId,
      @Param("timeEntryId") String timeEntryId,
      @Param("timeEntryStartDate") LocalDate timeEntryStartDate,
      @Param("timeEntryEndDate") LocalDate timeEntryEndDate
  );
}
