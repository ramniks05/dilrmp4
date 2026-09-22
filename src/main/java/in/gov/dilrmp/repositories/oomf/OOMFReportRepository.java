package in.gov.dilrmp.repositories.oomf;
import in.gov.dilrmp.models.oomf.OOMFReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OOMFReportRepository extends JpaRepository<OOMFReport, Long> {

    List<OOMFReport> findByFinancialYearAndQuarter(String financialYear, String quarter);

    @Query("SELECT o FROM OOMFReport o WHERE o.financialYear = :financialYear AND o.quarter = :quarter")
    OOMFReport findByFinancialYearAndQuarter1(@Param("financialYear") String financialYear, @Param("quarter") String quarter);


    List<OOMFReport> findByFinancialYear(String financialYear);

    @Query("SELECT DISTINCT o.financialYear FROM OOMFReport o")
    List<String> findDistinctFinancialYears();

    @Query("SELECT DISTINCT o.quarter FROM OOMFReport o")
    List<String> findDistinctQuarters();


}
