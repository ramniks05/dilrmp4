package in.gov.dilrmp.Scheduler.repositories;

import in.gov.dilrmp.Scheduler.models.RcmsReportViewReportBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RemsReportViewReportBackupRepository extends JpaRepository<RcmsReportViewReportBackup,Long> {

    @Query("SELECT MAX(b.backupDate) FROM RcmsReportViewReportBackup b WHERE b.backupDate <= :asOnDate")
    LocalDate findLatestBackupDateOnOrBefore(@Param("asOnDate") LocalDate asOnDate);

    @Query(value = "SELECT DISTINCT ON (state_id) * FROM rcms_report_view_backup " +
            "WHERE backup_date = :backupDate ORDER BY state_id, id DESC", nativeQuery = true)
    List<RcmsReportViewReportBackup> findSnapshotByBackupDate(@Param("backupDate") LocalDate backupDate);
}
