package in.gov.dilrmp.repositories.physicalProgressRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.gov.dilrmp.models.reportDTO.clr.DistrictClrReportView;

public interface DistrictClrViewRepository extends JpaRepository<DistrictClrReportView,Long> {


    
    List<DistrictClrReportView> findAllByStateId(Long stateId);


}
