package in.gov.dilrmp.services.cna;

import in.gov.dilrmp.models.cna.CnaBankBalanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CnaBankBalanceReportRepository extends JpaRepository<CnaBankBalanceReport, Long> {

    List<CnaBankBalanceReport> findAllByOrderByCnaNameAsc();

    @Query("SELECT MAX(r.uploadedOn) FROM CnaBankBalanceReport r")
    LocalDateTime findLatestUploadOn();

    @Query("SELECT r FROM CnaBankBalanceReport r WHERE r.uploadedOn >= :fromDateTime AND r.uploadedOn < :toDateTime " +
            "ORDER BY r.uploadedOn DESC, r.cnaName ASC")
    List<CnaBankBalanceReport> findByUploadedOnBetween(@Param("fromDateTime") LocalDateTime fromDateTime,
                                                         @Param("toDateTime") LocalDateTime toDateTime);

    @Query("SELECT COUNT(r) FROM CnaBankBalanceReport r WHERE r.uploadedOn >= :startOfDay AND r.uploadedOn < :endOfDay")
    long countByUploadDateRange(@Param("startOfDay") LocalDateTime startOfDay,
                                @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT DISTINCT r.uploadedOn FROM CnaBankBalanceReport r ORDER BY r.uploadedOn DESC")
    List<LocalDateTime> findDistinctUploadedOn();
}
