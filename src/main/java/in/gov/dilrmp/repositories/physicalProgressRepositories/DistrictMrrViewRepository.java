package in.gov.dilrmp.repositories.physicalProgressRepositories;

import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictMrrViewRepository extends JpaRepository<DistrictMrrViewReport,Long> {

    List<DistrictMrrViewReport> findByStateId(Long stateId);
}
