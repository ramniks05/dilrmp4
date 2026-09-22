package in.gov.dilrmp.repositories.physicalProgressRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;

import java.util.List;


public interface StateSroViewRepository extends JpaRepository<SroReportDTO,Long> {

     List<SroReportDTO> findAllSroByStateId(int stateId);
}
