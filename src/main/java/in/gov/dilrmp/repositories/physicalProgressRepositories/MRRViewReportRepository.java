package in.gov.dilrmp.repositories.physicalProgressRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;

import java.util.List;

public interface MRRViewReportRepository extends JpaRepository<MrrViewReport,Long> {

    List<MrrViewReport> findAllMrrByStateId(int stateId);

    List<MrrViewReport> findAllMrrByStateId(Long stateId);
}
