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

  @Query(
      value = "WITH t1 AS ("
          + "SELECT min(ac.accrual_date) as accrual_date, "
          + "TO_DATE(:startDate, 'YYYY-MM-DD') as timeentrystartdate "
          + "FROM accruals.accrual as ac "
          + "WHERE (ac.contributions -> 'timeEntries') ->> :timeEntryId != 'null') "
          + "SELECT ac.* "
          + "FROM accruals.accrual ac "
          + "WHERE ac.accrual_date BETWEEN "
          + "(SELECT least(t1.accrual_date, t1.timeentrystartdate) - 1 FROM t1) "
          + "AND :agreementEndDate "
          + "AND ac.person_id = :personId "
          + "AND ac.tenant_id = :tenantId "
          + "ORDER BY ac.accrual_date;",
      nativeQuery = true)
  List<Accrual> getAccrualsImpactedByTimeEntryWithPreviousDay(
      @Param("timeEntryId") String timeEntryId,
      @Param("startDate") LocalDate timeEntryStartDate,
      @Param("agreementEndDate") LocalDate agreementEndDate,
      @Param("personId") String personId,
      @Param("tenantId") String tenantId
  );
}
