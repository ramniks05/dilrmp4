package in.gov.dilrmp.repositories.physicalProgressRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;

import java.util.List;

public interface LinkedAadhaarReportRepository extends JpaRepository<LinkedAadharViewReport,Long> {

    List<LinkedAadharViewReport> findAllAadhaarByStateId(int stateId);

    List<LinkedAadharViewReport> findAllAadhaarByStateId(Long stateId);
}
