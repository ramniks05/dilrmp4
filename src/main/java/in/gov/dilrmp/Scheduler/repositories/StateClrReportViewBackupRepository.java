package in.gov.dilrmp.Scheduler.repositories;

import in.gov.dilrmp.Scheduler.models.StateClrReportViewBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StateClrReportViewBackupRepository extends JpaRepository<StateClrReportViewBackup,Long> {

    @Query("SELECT MAX(b.backupDate) FROM StateClrReportViewBackup b WHERE b.backupDate <= :asOnDate")
    LocalDate findLatestBackupDateOnOrBefore(@Param("asOnDate") LocalDate asOnDate);

    @Query("SELECT MIN(b.backupDate) FROM StateClrReportViewBackup b")
    LocalDate findFirstBackupDate();

    // A date can hold more than one snapshot (scheduler ran twice); keep the latest row per state.
    @Query(value = "SELECT DISTINCT ON (state_id) * FROM state_clr_report_view_backup " +
            "WHERE backup_date = :backupDate ORDER BY state_id, id DESC", nativeQuery = true)
    List<StateClrReportViewBackup> findSnapshotByBackupDate(@Param("backupDate") LocalDate backupDate);
}
