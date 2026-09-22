package in.gov.dilrmp.services.physicalProgressServices;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.DistrictSurveyResurveyViewReport;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictSurveyResurveyRepository;
import in.gov.dilrmp.repositories.physicalProgressRepositories.SurveyResurveyviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DistrictSurveyService {

    @Autowired
    SurveyResurveyviewRepository surveyResurveyviewRepository;

    @Autowired
    DistrictSurveyResurveyRepository districtSurveyResurveyRepository;

    private Logger logger = LoggerFactory.getLogger(DistrictSurveyService.class);

    public List<DistrictSurveyResurveyViewReport> getDistrictListByStateId(Long stateId) {

        return districtSurveyResurveyRepository.findAllByStateId(stateId);
    }

    public List<SurveyResurveyViewReport> getStateListByStateId(Long stateId) {

        return surveyResurveyviewRepository.findAllSurveyByStateId(stateId);
    }

}
