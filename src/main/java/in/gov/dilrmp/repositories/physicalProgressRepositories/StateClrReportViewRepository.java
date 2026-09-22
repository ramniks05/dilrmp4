package in.gov.dilrmp.repositories.physicalProgressRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;

public interface StateClrReportViewRepository extends JpaRepository<StateClrReportView,Long> {

    List<StateClrReportView> findAllByStateId(int stateId);
    List<StateClrReportView> findAllByStateId(Long stateId);
}
