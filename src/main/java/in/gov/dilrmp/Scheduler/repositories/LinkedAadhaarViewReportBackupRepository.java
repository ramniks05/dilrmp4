package in.gov.dilrmp.Scheduler.repositories;

import in.gov.dilrmp.Scheduler.models.LinkedAadharViewReportBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LinkedAadhaarViewReportBackupRepository extends JpaRepository<LinkedAadharViewReportBackup,Long> {

    @Query("SELECT MAX(b.backupDate) FROM LinkedAadharViewReportBackup b WHERE b.backupDate <= :asOnDate")
    LocalDate findLatestBackupDateOnOrBefore(@Param("asOnDate") LocalDate asOnDate);

    @Query(value = "SELECT DISTINCT ON (state_id) * FROM linked_aadhaar_report_view_backup " +
            "WHERE backup_date = :backupDate ORDER BY state_id, id DESC", nativeQuery = true)
    List<LinkedAadharViewReportBackup> findSnapshotByBackupDate(@Param("backupDate") LocalDate backupDate);
}
