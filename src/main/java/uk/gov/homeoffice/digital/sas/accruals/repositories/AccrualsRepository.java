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
public interface AccrualsRepository extends JpaRepository<Accrual, UUID> {

  @Query(value =
      "SELECT * FROM "
          + "get_impacted_accruals(:startDate, :timeEntryId, :agreementEndDate);",
      nativeQuery = true)
  List<Accrual> getAccrualsImpactedByTimeEntryWithPreviousDay(
      @Param("timeEntryId") String timeEntryId,
      @Param("startDate") LocalDate startDate,
      @Param("agreementEndDate") LocalDate agreementEndDate
  );
}
