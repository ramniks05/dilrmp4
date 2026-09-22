package in.gov.dilrmp.repositories.physicalProgressRepositories;

import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.DistrictMapDigitizationReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictMapViewRepository extends JpaRepository<DistrictMapDigitizationReport,Long> {

    List<DistrictMapDigitizationReport> findAllByStateId(Long stateId);
}
