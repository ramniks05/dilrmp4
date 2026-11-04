package in.gov.dilrmp.services.cna;

import in.gov.dilrmp.models.cna.CnaBankBalanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CnaBankBalanceReportRepository extends JpaRepository<CnaBankBalanceReport, Long> {

    List<CnaBankBalanceReport> findAllByOrderByCnaNameAsc();

    @Query("SELECT MAX(r.asOnDate) FROM CnaBankBalanceReport r")
    LocalDate findLatestAsOnDate();

    @Query("SELECT DISTINCT r.asOnDate FROM CnaBankBalanceReport r WHERE r.asOnDate IS NOT NULL ORDER BY r.asOnDate DESC")
    List<LocalDate> findDistinctAsOnDates();

    List<CnaBankBalanceReport> findByAsOnDate(LocalDate asOnDate);

    @Query("SELECT r FROM CnaBankBalanceReport r WHERE r.asOnDate >= :fromDate AND r.asOnDate <= :toDate")
    List<CnaBankBalanceReport> findByAsOnDateBetween(@Param("fromDate") LocalDate fromDate,
                                                     @Param("toDate") LocalDate toDate);

    @Query("SELECT COUNT(r) FROM CnaBankBalanceReport r WHERE r.asOnDate = :asOnDate")
    long countByAsOnDate(@Param("asOnDate") LocalDate asOnDate);

    @Query("SELECT DISTINCT r.uploadedOn FROM CnaBankBalanceReport r ORDER BY r.uploadedOn DESC")
    List<LocalDateTime> findDistinctUploadedOn();

    List<CnaBankBalanceReport> findByUploadedOnOrderByIdAsc(LocalDateTime uploadedOn);
}
