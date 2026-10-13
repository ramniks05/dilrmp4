package in.gov.dilrmp.Scheduler.repositories;

import in.gov.dilrmp.Scheduler.models.MapDigitizationReportBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MapDigitizationReportBackupRepository extends JpaRepository<MapDigitizationReportBackup,Long> {

    @Query("SELECT MAX(b.backupDate) FROM MapDigitizationReportBackup b WHERE b.backupDate <= :asOnDate")
    LocalDate findLatestBackupDateOnOrBefore(@Param("asOnDate") LocalDate asOnDate);

    @Query(value = "SELECT DISTINCT ON (lgd_code) * FROM map_digitization_report_view_backup " +
            "WHERE backup_date = :backupDate ORDER BY lgd_code, id DESC", nativeQuery = true)
    List<MapDigitizationReportBackup> findSnapshotByBackupDate(@Param("backupDate") LocalDate backupDate);
}
