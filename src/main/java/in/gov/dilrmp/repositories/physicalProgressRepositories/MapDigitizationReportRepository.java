package in.gov.dilrmp.repositories.physicalProgressRepositories;


import org.springframework.data.jpa.repository.JpaRepository;

import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import java.util.List;

public interface MapDigitizationReportRepository extends JpaRepository<MapDigitizationReport, Integer> {


    List<MapDigitizationReport> findAllByLgdCode(int lgdCode);


    List<MapDigitizationReport> findAllByLgdCode(Long lgdCode);

}

