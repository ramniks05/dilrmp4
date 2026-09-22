package in.gov.dilrmp.repositories.physicalProgressRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;

import java.util.List;

public interface SurveyResurveyviewRepository extends JpaRepository<SurveyResurveyViewReport,Long> {

    List<SurveyResurveyViewReport> findAllSurveyByStateId(int stateId);

    List<SurveyResurveyViewReport> findAllSurveyByStateId(Long stateId);
}
