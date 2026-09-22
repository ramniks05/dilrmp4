package in.gov.dilrmp.repositories.physicalProgressRepositories;

import in.gov.dilrmp.models.reportDTO.surveyresurvey.DistrictSurveyResurveyViewReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictSurveyResurveyRepository extends JpaRepository<DistrictSurveyResurveyViewReport,Long> {

    List<DistrictSurveyResurveyViewReport> findAllByStateId(Long stateId);
}
