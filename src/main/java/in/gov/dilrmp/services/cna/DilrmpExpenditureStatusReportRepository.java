package in.gov.dilrmp.services.cna;

import in.gov.dilrmp.models.cna.DilrmpExpenditureStatusReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DilrmpExpenditureStatusReportRepository extends JpaRepository<DilrmpExpenditureStatusReport, Long> {

    List<DilrmpExpenditureStatusReport> findAllByOrderBySerialNoAscStateNameAsc();

    @Query("SELECT MAX(r.uploadedOn) FROM DilrmpExpenditureStatusReport r")
    LocalDateTime findLatestUploadOn();

    @Query("SELECT r FROM DilrmpExpenditureStatusReport r WHERE r.uploadedOn >= :fromDateTime AND r.uploadedOn < :toDateTime " +
            "ORDER BY r.serialNo ASC, r.stateName ASC")
    List<DilrmpExpenditureStatusReport> findByUploadedOnBetween(@Param("fromDateTime") LocalDateTime fromDateTime,
                                                                @Param("toDateTime") LocalDateTime toDateTime);

    List<DilrmpExpenditureStatusReport> findByReportAsOnDateOrderBySerialNoAscStateNameAsc(LocalDate reportAsOnDate);

    long countByReportAsOnDate(LocalDate reportAsOnDate);

    void deleteByReportAsOnDate(LocalDate reportAsOnDate);

    @Query("SELECT DISTINCT r.reportAsOnDate FROM DilrmpExpenditureStatusReport r WHERE r.reportAsOnDate IS NOT NULL ORDER BY r.reportAsOnDate DESC")
    List<LocalDate> findDistinctReportAsOnDates();

    @Query("SELECT COUNT(r) FROM DilrmpExpenditureStatusReport r WHERE r.uploadedOn >= :startOfDay AND r.uploadedOn < :endOfDay")
    long countByUploadDateRange(@Param("startOfDay") LocalDateTime startOfDay,
                                @Param("endOfDay") LocalDateTime endOfDay);

    void deleteByUploadedOnGreaterThanEqualAndUploadedOnLessThan(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
