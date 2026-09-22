package in.gov.dilrmp.repositories.physicalProgressRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;

import java.util.List;

public interface StateRcmsViewRepository extends JpaRepository<RcmsReportDTO,Long> {

    List<RcmsReportDTO> findAllRcmsByStateId(int stateId);

    List<RcmsReportDTO> findAllRcmsByStateId(Long stateId);
}
