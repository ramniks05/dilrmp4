package in.gov.dilrmp.api.ogd;

import in.gov.dilrmp.api.common.ExternalShareApiSupport;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.services.physicalProgressServices.MapDigitizationReportService;
import in.gov.dilrmp.services.physicalProgressServices.StateAadharService;
import in.gov.dilrmp.services.physicalProgressServices.StateCLRService;
import in.gov.dilrmp.services.physicalProgressServices.StateRCMSService;
import in.gov.dilrmp.services.physicalProgressServices.StateSROService;
import in.gov.dilrmp.services.physicalProgressServices.StateSurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Single OGD API: one flat object per State/UT.
 * Field names match physical progress report numerator / denominator properties.
 */
@Service
public class OgdPlatformApiService {

    @Autowired
    private StateCLRService stateCLRService;
    @Autowired
    private MapDigitizationReportService mapDigitizationReportService;
    @Autowired
    private StateSROService stateSROService;
    @Autowired
    private StateSurveyService stateSurveyService;
    @Autowired
    private StateRCMSService stateRCMSService;
    @Autowired
    private StateAadharService stateAadharService;

    public Map<String, Object> allStates() {
        Map<String, Object> body = ExternalShareApiSupport.base("OGD",
                "State/UT-wise DILRMP progress for data.gov.in");
        body.put("components", Arrays.asList(
                "Computerization of Land Records (CLR)",
                "Map Digitization",
                "Computerization of Registration (SRO)",
                "Survey/Re-survey",
                "Computerization of Revenue Court (E-RCMS)",
                "Consent-based linkage of Aadhaar with RoR"
        ));
        body.put("excluded", Arrays.asList(
                "Static Excel file referred in Note #131 is not shared through this API."
        ));

        Map<Integer, MapDigitizationReport> mapByLgd = byLgd(mapDigitizationReportService.getAllMapDigitizationReport());
        Map<Long, SroReportDTO> sroByState = byStateId(stateSROService.getSroStateViwe());
        Map<Long, SurveyResurveyViewReport> surveyByState = byStateId(stateSurveyService.getAllSurveyResurveyList());
        Map<Long, RcmsReportDTO> rcmsByState = byStateId(stateRCMSService.getRcmsStateViwe());
        Map<Long, LinkedAadharViewReport> aadhaarByState = byStateId(stateAadharService.getAllAadhaarList());

        List<Map<String, Object>> rows = new ArrayList<>();
        for (StateClrReportView clr : stateCLRService.getClrStateViwe()) {
            if (clr.getStateId() == null || ExternalShareApiSupport.isNationalStateId(clr.getStateId())) {
                continue;
            }
            long stateId = clr.getStateId();
            Integer lgdCode = clr.getLgdCode();
            MapDigitizationReport map = lgdCode == null ? null : mapByLgd.get(lgdCode);
            if (map == null) {
                map = mapByLgd.get((int) stateId);
            }
            SroReportDTO sro = sroByState.get(stateId);
            SurveyResurveyViewReport survey = surveyByState.get(stateId);
            RcmsReportDTO rcms = rcmsByState.get(stateId);
            LinkedAadharViewReport aadhaar = aadhaarByState.get(stateId);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("lgdCode", lgdCode);
            row.put("stateName", clr.getStateName() == null ? "" : clr.getStateName());

            // CLR
            row.put("totalRor", ExternalShareApiSupport.intValue(clr.getTotalRor()));
            row.put("rorComputerized", ExternalShareApiSupport.intValue(clr.getRorComputerized()));
            row.put("rorComputerizedPercent", ExternalShareApiSupport.percent(clr.getRorComputerizedPercent()));

            // Map Digitization
            row.put("totalMapsFmbTippans",
                    ExternalShareApiSupport.intValue(map == null ? null : map.getTotalMapsFmbTippans()));
            row.put("totalDigitizedMapsFmbTippans",
                    ExternalShareApiSupport.intValue(map == null ? null : map.getTotalDigitizedMapsFmbTippans()));
            row.put("digitizedMapsFmbTippansPercent",
                    ExternalShareApiSupport.percent(map == null ? null : map.getDigitizedMapsFmbTippansPercent()));

            // SRO
            row.put("numberOfSROsInState",
                    ExternalShareApiSupport.intValue(sro == null ? null : sro.getNumberOfSROsInState()));
            row.put("numberOfSROsUsingOnlineRegistration",
                    ExternalShareApiSupport.intValue(sro == null ? null : sro.getNumberOfSROsUsingOnlineRegistration()));
            row.put("onlineRegistrationPercent",
                    ExternalShareApiSupport.percent(sro == null ? null : sro.getOnlineRegistrationPercent()));

            // Survey / Re-survey
            row.put("totalVillages",
                    ExternalShareApiSupport.intValue(survey == null ? null : survey.getTotalVillages()));
            row.put("villagesFinalPromulgationDone",
                    ExternalShareApiSupport.intValue(survey == null ? null : survey.getVillagesFinalPromulgationDone()));
            row.put("surveyCompletedVillagePercent",
                    ExternalShareApiSupport.percent(survey == null ? null : survey.getSurveyCompletedVillagePercent()));

            // RCMS
            row.put("totalRevenueCourts",
                    ExternalShareApiSupport.intValue(rcms == null ? null : rcms.getTotalRevenueCourts()));
            row.put("revenueCourtsComputerized",
                    ExternalShareApiSupport.intValue(rcms == null ? null : rcms.getRevenueCourtsComputerized()));
            row.put("revenueCourtsComputerizedPercent",
                    ExternalShareApiSupport.percent(rcms == null ? null : rcms.getRevenueCourtsComputerizedPercent()));

            // Aadhaar
            row.put("rorLinkedWithAadhaar",
                    ExternalShareApiSupport.intValue(aadhaar == null ? null : aadhaar.getRorLinkedWithAadhaar()));
            row.put("rorLinkedWithAadhaarPercent",
                    ExternalShareApiSupport.percent(aadhaar == null ? null : aadhaar.getRorLinkedWithAadhaarPercent()));

            rows.add(row);
        }

        body.put("totalStates", rows.size());
        body.put("states", rows);
        return body;
    }

    private static <T> Map<Long, T> byStateId(List<T> list) {
        Map<Long, T> map = new LinkedHashMap<>();
        if (list == null) {
            return map;
        }
        for (T item : list) {
            Long id = stateIdOf(item);
            if (id != null && !ExternalShareApiSupport.isNationalStateId(id)) {
                map.put(id, item);
            }
        }
        return map;
    }

    private static Map<Integer, MapDigitizationReport> byLgd(List<MapDigitizationReport> list) {
        Map<Integer, MapDigitizationReport> map = new LinkedHashMap<>();
        if (list == null) {
            return map;
        }
        for (MapDigitizationReport item : list) {
            if (item.getLgdCode() != null && !ExternalShareApiSupport.isNationalLgd(item.getLgdCode())) {
                map.put(item.getLgdCode(), item);
            }
        }
        return map;
    }

    private static Long stateIdOf(Object item) {
        if (item instanceof SroReportDTO) {
            return ((SroReportDTO) item).getStateId();
        }
        if (item instanceof SurveyResurveyViewReport) {
            return ((SurveyResurveyViewReport) item).getStateId();
        }
        if (item instanceof RcmsReportDTO) {
            return ((RcmsReportDTO) item).getStateId();
        }
        if (item instanceof LinkedAadharViewReport) {
            return ((LinkedAadharViewReport) item).getStateId();
        }
        return null;
    }
}
