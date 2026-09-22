package in.gov.dilrmp.repositories.physicalProgressRepositories;

import in.gov.dilrmp.models.reportDTO.rcms.DistrictRcmsReportDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictRcmsViewRepository extends JpaRepository<DistrictRcmsReportDTO,Long> {

    List<DistrictRcmsReportDTO> findByStateId(Long stateId);
}
