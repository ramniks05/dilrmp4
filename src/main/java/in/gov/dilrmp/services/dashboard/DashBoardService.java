package in.gov.dilrmp.services.dashboard;


import in.gov.dilrmp.models.dashboard.DashBordDTO;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.services.physicalProgressServices.*;
import in.gov.dilrmp.utils.DataNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashBoardService {

  @Autowired
  StateCLRService stateCLRService;
  @Autowired
  MapDigitizationReportService mapDigitizationReportService;
  @Autowired
  StateSROService stateSROService;
  @Autowired
  StateRCMSService stateRCMSService;
  @Autowired
  StateSurveyService stateSurveyService;
  @Autowired
  StateMRRService stateMRRService;
  @Autowired
  StateAadharService stateAadharService;



    public DashBordDTO getNationalDashBoardData() {

        // Create a single DashBordDTO instance
        DashBordDTO nationalDTO = new DashBordDTO();

        // Fetch and populate CLR data
        List<StateClrReportView> nationalCLRModels = stateCLRService.getStateClrReportsGrandToatal();
        if (!nationalCLRModels.isEmpty()) {
            StateClrReportView nationalCLRMoodel = nationalCLRModels.get(0);
            nationalDTO.setTotalDistrict(nationalCLRMoodel.getTotalDistrict());
            nationalDTO.setTotalTehsil(nationalCLRMoodel.getTotalTehsils());
            nationalDTO.setTotalVillage(nationalCLRMoodel.getTotalVillages());
            nationalDTO.setTotalRor(nationalCLRMoodel.getTotalRor());
            nationalDTO.setVillagesComputerizationCompleted(nationalCLRMoodel.getVillagesClrCompleted());
            nationalDTO.setVillagesComputerizationCompletedPercent(nationalCLRMoodel.getClrCompletionPercent());
            nationalDTO.setRorComputerized(nationalCLRMoodel.getRorComputerized());
            nationalDTO.setRorComputerizedPercent(nationalCLRMoodel.getRorComputerizedPercent());
            nationalDTO.setTotalLandOwners(nationalCLRMoodel.getTotalLandOwners());
        } else {
            throw new DataNotFoundException("No national CLR data found.");
        }

        // Fetch and populate MAP data
        List<MapDigitizationReport> nationalMAPMoodels = mapDigitizationReportService.getStateMapReportsGrandToatal();
        if (!nationalMAPMoodels.isEmpty()) {
            MapDigitizationReport nationalMAPMoodel = nationalMAPMoodels.get(0);
            nationalDTO.setTotalMapsFmbTippans(nationalMAPMoodel.getTotalMapsFmbTippans());
            nationalDTO.setDigitizedCadastralMaps(nationalMAPMoodel.getDigitizedCadastralMaps());
            nationalDTO.setDigitizedCadastralMapsPercent(nationalMAPMoodel.getDigitizedCadastralMapsPercent());
            nationalDTO.setTotalDigitizedMapsFmbTippans(nationalMAPMoodel.getTotalDigitizedMapsFmbTippans());
            nationalDTO.setDigitizedMapsFmbTippansPercent(nationalMAPMoodel.getDigitizedMapsFmbTippansPercent());
            nationalDTO.setVillagesWithUlipn(nationalMAPMoodel.getVillagesWithUlipn());
            nationalDTO.setVillagesWithUlipnPercent(nationalMAPMoodel.getVillagesWithUlipnPercent());
            nationalDTO.setVillagesLinkedWithRor(nationalMAPMoodel.getVillagesLinkedWithRor());
            nationalDTO.setVillagesGeoreferenced(nationalMAPMoodel.getVillagesGeoreferenced());
            nationalDTO.setVillagesGeoreferencedPercent(nationalMAPMoodel.getVillagesGeoreferencedPercent());
        } else {
            throw new DataNotFoundException("No national MAP data found.");
        }

        // Fetch and populate SRO data
        List<SroReportDTO> nationalSROModels = stateSROService.getStateSroReportsGrandToatal();
        if (!nationalSROModels.isEmpty()) {
            SroReportDTO nationalSROModel = nationalSROModels.get(0);
            nationalDTO.setTotalSro(nationalSROModel.getNumberOfSROsInState());
            nationalDTO.setSroComputerized(nationalSROModel.getNumberOfSROsUsingOnlineRegistration());
            nationalDTO.setSroComputerizedPercent(nationalSROModel.getOnlineRegistrationPercent());
        } else {
            throw new DataNotFoundException("No national SRO data found.");
        }

        // Fetch and populate Survey Resurvey data
        List<SurveyResurveyViewReport> nationalSurveyModels = stateSurveyService.getStateSurveyReportsGrandToatal();
        if (!nationalSurveyModels.isEmpty()) {
            SurveyResurveyViewReport nationalSurveyModel = nationalSurveyModels.get(0);
            nationalDTO.setVillagesMap1Generated(nationalSurveyModel.getVillagesMap1Generated());
            nationalDTO.setVillagesDroneFlyingCompleted(nationalSurveyModel.getVillagesDroneFlyingCompleted());
            if (nationalSurveyModel.getTotalVillages() != 0) {
                nationalDTO.setVillagesDroneFlyingCompletedPercent(
                        BigDecimal.valueOf(nationalSurveyModel.getVillagesDroneFlyingCompleted() * 100.0 / nationalSurveyModel.getTotalVillages())
                );
            } else {
                nationalDTO.setVillagesDroneFlyingCompletedPercent(BigDecimal.ZERO); // Or handle this case as needed
            }
        }
        else {
            throw new DataNotFoundException("No national Survey data found.");
        }

        // RCMS National Level the entity to DTO
        List<RcmsReportDTO> nationalRCMSModels = stateRCMSService.getStateRcmsReportsGrandToatal();
        if(!nationalRCMSModels.isEmpty()) {
            RcmsReportDTO nationalRCMSModel = nationalRCMSModels.get(0);
            nationalDTO.setTotalRevenueCourts(nationalRCMSModel.getTotalRevenueCourts());
            nationalDTO.setRevenueCourtsComputerized(nationalRCMSModel.getRevenueCourtsComputerized());
            nationalDTO.setTotalRevenueCourtsPercent(BigDecimal.valueOf(Double.valueOf(nationalRCMSModel.getRevenueCourtsComputerizedPercent())));
        }else {
            throw new DataNotFoundException("No national RCMS data found.");
        }

         // Aadhar National Level the entity to DTO
        List<LinkedAadharViewReport> nationalAadharModels =  stateAadharService.getStateLinkedAadhaarReportsGrandToatal();
        if(!nationalAadharModels.isEmpty()) {
            LinkedAadharViewReport nationalAadharModel = nationalAadharModels.get(0);
            nationalDTO.setVillagesWith100PercentRorLinkedAadhaar(nationalAadharModel.getVillagesWith100PercentRorLinkedAadhaar());
            nationalDTO.setVillagesWith100PercentRorLinkedAadhaarPercent(nationalAadharModel.getVillagesWith100PercentRorLinkedAadhaarPercent());
            nationalDTO.setRorLinkedWithAadhaar(nationalAadharModel.getRorLinkedWithAadhaar());
            nationalDTO.setRorLinkedWithAadhaarPercent(nationalAadharModel.getRorLinkedWithAadhaarPercent());
            nationalDTO.setRorLinkedWithMobileNumber(nationalAadharModel.getRorLinkedWithMobileNumber());
            nationalDTO.setRorLinkedWithMobileNumberPercent(nationalAadharModel.getRorLinkedWithMobileNumberPercent());
        }else {
            throw new DataNotFoundException("No national AADHAAR data found.");
        }


        // MRR National Level the entity to DTO
        List<MrrViewReport> nationalMRRModels = stateMRRService.getStateMrrReportsGrandToatal();
        if(!nationalMRRModels.isEmpty()) {
            MrrViewReport nationalMRRModel = nationalMRRModels.get(0);
            nationalDTO.setMrrCompleted(nationalMRRModel.getMrrCompleted());
            nationalDTO.setMrrSanctioned(nationalMRRModel.getMrrSanctioned());
            nationalDTO.setMrrCompletedOutOfTotalTehsilsPercent(nationalMRRModel.getMrrCompletedOutOfTotalTehsilsPercent());
            nationalDTO.setMrrCompletedOutOfTotalSanctionedPercentforDashboaerd(nationalMRRModel.getMrrCompletedOutOfSanctionedPercent());
        }else {
            throw new DataNotFoundException("No national MRR data found.");
        }

        // Return the populated DTO
        return nationalDTO;
    }

}

