package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.models.reportDTO.rcms.DistrictRcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictRcmsViewRepository;
import in.gov.dilrmp.repositories.physicalProgressRepositories.StateRcmsViewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistrictRCMSService {

   @Autowired
   DistrictRcmsViewRepository districtRcmsViewRepository;
   @Autowired
    StateRcmsViewRepository stateRcmsViewRepository;

    private Logger logger = LoggerFactory.getLogger(StateRCMSService.class);

    public List<DistrictRcmsReportDTO> getDistrictListByStateId(Long stateId) {
        List<DistrictRcmsReportDTO> districtRcmsList = districtRcmsViewRepository.findByStateId(stateId);

        List<RcmsReportDTO> stateRcmsList = stateRcmsViewRepository.findAllRcmsByStateId(stateId);

        if (!districtRcmsList.isEmpty() && !stateRcmsList.isEmpty()) {
            RcmsReportDTO stateReport = stateRcmsList.get(0);

            for (DistrictRcmsReportDTO districtReport : districtRcmsList) {
                districtReport.setERcmsAvailable(stateReport.getERcmsAvailable());
                districtReport.setAffidavitFilingOnline(stateReport.getAffidavitFilingOnline());
                districtReport.setCauseListGenerationOnline(stateReport.getCauseListGenerationOnline());
                districtReport.setNoticeToDefendantsOnline(stateReport.getNoticeToDefendantsOnline());
                districtReport.setCounterAffidavitFilingOnline(stateReport.getCounterAffidavitFilingOnline());
                districtReport.setProceedingsTypingDirectlyOnline(stateReport.getProceedingsTypingDirectlyOnline());
                districtReport.setUploadingRevenueCourtOrdersOnline(stateReport.getUploadingRevenueCourtOrdersOnline());
                districtReport.setLandRecordsOnlineFromRevenueCourtSystem(stateReport.getLandRecordsOnlineFromRevenueCourtSystem());
                districtReport.setRevenueCourtProceedingsPaperless(stateReport.getRevenueCourtProceedingsPaperless());
                districtReport.setLandRecordsOnlineForCivilCourts(stateReport.getLandRecordsOnlineForCivilCourts());
                districtReport.setCaseFilingRedFlaggedInLandRecordsFromCivilCourts(stateReport.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts());
                districtReport.setCaseFilingRedFlaggedInLandRecords(stateReport.getCaseFilingRedFlaggedInLandRecords());
            }
        }

        return districtRcmsList;
    }

    public List<RcmsReportDTO> getStateListByStateId(Long stateId){

        return  stateRcmsViewRepository.findAllRcmsByStateId(stateId);
    }



}
