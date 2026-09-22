package in.gov.dilrmp.repositories.naksha;

import in.gov.dilrmp.models.naksha.TimelineBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TimelineBackupRepository extends JpaRepository<TimelineBackup, Long> {

    @Query("SELECT COUNT(t) > 0 FROM TimelineBackup t WHERE t.backupDate = :backupDate")
    boolean existsByUpdateOnDate(@Param("backupDate") LocalDate backupDate);

    @Query("SELECT t.timelineMeetingDate FROM TimelineBackup t WHERE t.updateOnDate = :currentDate")
    List<LocalDate> findTimelineMeetingDatesByUpdateOnDate(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT t.backupDate FROM TimelineBackup t GROUP BY t.backupDate ORDER BY t.backupDate DESC")
    List<LocalDate> findDistinctBackupDates();

}
