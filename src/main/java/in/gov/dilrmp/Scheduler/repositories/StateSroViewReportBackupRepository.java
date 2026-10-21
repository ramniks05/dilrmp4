package in.gov.dilrmp.Scheduler.repositories;


import in.gov.dilrmp.Scheduler.models.SroViewReportBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StateSroViewReportBackupRepository extends JpaRepository<SroViewReportBackup,Long> {

    @Query("SELECT MAX(b.backupDate) FROM SroViewReportBackup b WHERE b.backupDate <= :asOnDate")
    LocalDate findLatestBackupDateOnOrBefore(@Param("asOnDate") LocalDate asOnDate);

    @Query(value = "SELECT DISTINCT ON (state_id) * FROM state_registration_report_view_backup " +
            "WHERE backup_date = :backupDate ORDER BY state_id, id DESC", nativeQuery = true)
    List<SroViewReportBackup> findSnapshotByBackupDate(@Param("backupDate") LocalDate backupDate);
}
