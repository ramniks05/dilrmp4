package in.gov.dilrmp.controllers.consolidated;


import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.reportDTO.profile.DistrictProfileSection;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.legacy.LegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.services.administrativeBoundry.DistrictService;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import in.gov.dilrmp.services.physicalProgressServices.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/current-status")
public class ProfileController {

   @Autowired
   StateCLRService stateCLRService;
   @Autowired
   StateMAPService stateMAPService;
   @Autowired
   MapDigitizationReportService mapDigitizationReportService;
   @Autowired
   StateSROService stateSROService;
   @Autowired
   StateSurveyService stateSurveyService;
   @Autowired
   StateRCMSService stateRCMSService;
   @Autowired
   StateMRRService stateMRRService;
   @Autowired
   StateAadharService stateAadharService;
   @Autowired
   LegacyDigitizationReportService legacyDigitizationReportService;
   @Autowired
   StateService stateService;
   @Autowired
   DistrictService districtService;
   @Autowired
   DistrictProfileService districtProfileService;


    @GetMapping("/national_profile")
    public String nationalProfileReport(Model model){
        Map<String, String> clrLabels =stateCLRService.createLabels();
        Map<String, String> mapLabels = stateMAPService.createLabels();
        Map<String, String> sroLabels = stateSROService.createLabels();
        Map<String, String> surveyLabels = stateSurveyService.createLabels();
        Map<String, String> rcmsLabels = stateRCMSService.createLabels();
        Map<String, String> mrrLabels = stateMRRService.createLabels();
        Map<String, String> aadharLabels = stateAadharService.createLabels();
        Map<String, String> legacyLabels = legacyDigitizationReportService.createLabels();

        model.addAttribute("clrlabels",clrLabels);
        model.addAttribute("maplabels",mapLabels);
        model.addAttribute("srolabels",sroLabels);
        model.addAttribute("surveylabels",surveyLabels);
        model.addAttribute("rcmslabels",rcmsLabels);
        model.addAttribute("mrrlabels",mrrLabels);
        model.addAttribute("addharlabels",aadharLabels);
        model.addAttribute("legacylabels", legacyLabels);

        model.addAttribute("clrdata", stateCLRService.getStateClrReportsGrandToatal());
        model.addAttribute("mapdata",mapDigitizationReportService.getStateMapReportsGrandToatal());
        model.addAttribute("srodata", stateSROService.getStateSroReportsGrandToatal());
        model.addAttribute("surveydata", stateSurveyService.getStateSurveyReportsGrandToatal());
        model.addAttribute("rcmsdata", stateRCMSService.getStateRcmsReportsGrandToatal());
        model.addAttribute("mrrdata", stateMRRService.getStateMrrReportsGrandToatal());
        model.addAttribute("aadhardata", stateAadharService.getStateLinkedAadhaarReportsGrandToatal());
        model.addAttribute("legacydata", legacyDigitizationReportService.getGrandTotalRaw());
        return "pages/consolidatedReports/national-profile::national-profile";

    }



    @GetMapping("/state_profile")
    public String stateProfileReport(Model model){


        List<State> stateList = stateService.findAllOrderByStateName();
        model.addAttribute("stateList",stateList);

        Map<String, String> clrLabels =stateCLRService.createLabels();
        Map<String, String> mapLabels = stateMAPService.createLabels();
        Map<String, String> sroLabels = stateSROService.createLabels();
        Map<String, String> surveyLabels = stateSurveyService.createLabels();
        Map<String, String> rcmsLabels = stateRCMSService.createLabels();
        Map<String, String> mrrLabels = stateMRRService.createLabels();
        Map<String, String> aadharLabels = stateAadharService.createLabels();
        Map<String, String> legacyLabels = legacyDigitizationReportService.createLabels();

        model.addAttribute("clrlabels",clrLabels);
        model.addAttribute("maplabels",mapLabels);
        model.addAttribute("srolabels",sroLabels);
        model.addAttribute("surveylabels",surveyLabels);
        model.addAttribute("rcmslabels",rcmsLabels);
        model.addAttribute("mrrlabels",mrrLabels);
        model.addAttribute("addharlabels",aadharLabels);
        model.addAttribute("legacylabels", legacyLabels);


        return "pages/consolidatedReports/state-profile::state-profile";
    }

    @PostMapping("/get-state-profile")
    public String getStateProfile(@RequestParam("stateLgdCode") Integer stateLgdCode, Model model, HttpSession session) {
        List<State> stateList = stateService.findAllOrderByStateName();
        model.addAttribute("stateList", stateList);
        model.addAttribute("selectedStateLgdCode", stateLgdCode);

        Map<String, String> clrLabels = stateCLRService.createLabels();
        Map<String, String> mapLabels = stateMAPService.createLabels();
        Map<String, String> sroLabels = stateSROService.createLabels();
        Map<String, String> surveyLabels = stateSurveyService.createLabels();
        Map<String, String> rcmsLabels = stateRCMSService.createLabels();
        Map<String, String> mrrLabels = stateMRRService.createLabels();
        Map<String, String> aadharLabels = stateAadharService.createLabels();
        Map<String, String> legacyLabels = legacyDigitizationReportService.createLabels();

        model.addAttribute("clrlabels", clrLabels);
        model.addAttribute("maplabels", mapLabels);
        model.addAttribute("srolabels", sroLabels);
        model.addAttribute("surveylabels", surveyLabels);
        model.addAttribute("rcmslabels", rcmsLabels);
        model.addAttribute("mrrlabels", mrrLabels);
        model.addAttribute("addharlabels", aadharLabels);
        model.addAttribute("legacylabels", legacyLabels);

        List<StateClrReportView> clrData = stateCLRService.getClrStateDataByStateId(stateLgdCode);
        List<MapDigitizationReport> mapData = mapDigitizationReportService.getMapStateDataByStateId(stateLgdCode);
        List<SroReportDTO> sroData = stateSROService.getSroStateDataByStateId(stateLgdCode);
        List<SurveyResurveyViewReport> surveyData = stateSurveyService.getSurveyStateDataByStateId(stateLgdCode);
        List<RcmsReportDTO> rcmsData = stateRCMSService.getrcmsStateDataByStateId(stateLgdCode);
        List<LinkedAadharViewReport> aadhaarData = stateAadharService.getAadhaarStateDataByStateId(stateLgdCode);
        List<MrrViewReport> mrrData = stateMRRService.getMrrStateDataByStateId(stateLgdCode);
        List<LegacyDigitizationReport> legacyData = legacyDigitizationReportService.getByStateId(stateLgdCode);

        model.addAttribute("clrData", clrData);
        session.setAttribute("clrData", clrData);
        model.addAttribute("mapData", mapData);
        session.setAttribute("mapData", mapData);
        model.addAttribute("sroData", sroData);
        session.setAttribute("sroData", sroData);
        model.addAttribute("surveyData", surveyData);
        session.setAttribute("surveyData", surveyData);
        model.addAttribute("rcmsData", rcmsData);
        session.setAttribute("rcmsData", rcmsData);
        model.addAttribute("aadhaarData", aadhaarData);
        session.setAttribute("aadhaarData", aadhaarData);
        model.addAttribute("mrrData", mrrData);
        session.setAttribute("mrrData", mrrData);
        model.addAttribute("legacyData", legacyData);
        session.setAttribute("legacyData", legacyData);
        session.setAttribute("stateName", clrData.get(0).getStateName());
        return "pages/consolidatedReports/state-profile::state-profile";
    }

    @GetMapping("/district_profile")
    public String districtProfileReport(@RequestParam(value = "stateId", required = false) Long stateId, Model model) {
        model.addAttribute("stateList", stateService.findAllOrderByStateName());
        model.addAttribute("districtList", districtsOfState(stateId));
        model.addAttribute("selectedStateId", stateId);
        return "pages/consolidatedReports/district-profile::district-profile";
    }

    @PostMapping("/get-district-profile")
    public String getDistrictProfile(@RequestParam("stateId") Long stateId,
                                     @RequestParam("districtId") Long districtId,
                                     Model model, HttpSession session) {
        if (districtId == null || districtId == 0) {
            return districtProfileReport(stateId, model);
        }
        List<State> stateList = stateService.findAllOrderByStateName();
        List<District> districtList = districtsOfState(stateId);
        model.addAttribute("stateList", stateList);
        model.addAttribute("districtList", districtList);
        model.addAttribute("selectedStateId", stateId);
        model.addAttribute("selectedDistrictId", districtId);

        String stateName = stateList.stream()
                .filter(s -> stateId.equals(s.getId())).map(State::getName).findFirst().orElse("");
        String districtName = districtList.stream()
                .filter(d -> districtId.equals(d.getId())).map(District::getName).findFirst().orElse("");
        List<DistrictProfileSection> sections = districtProfileService.buildProfile(stateId, districtId);

        model.addAttribute("profileStateName", stateName);
        model.addAttribute("profileDistrictName", districtName);
        model.addAttribute("profileSections", sections);
        session.setAttribute("districtProfileStateName", stateName);
        session.setAttribute("districtProfileDistrictName", districtName);
        session.setAttribute("districtProfileSections", sections);
        return "pages/consolidatedReports/district-profile::district-profile";
    }

    private List<District> districtsOfState(Long stateId) {
        if (stateId == null || stateId == 0) {
            return new ArrayList<>();
        }
        List<District> districts = new ArrayList<>(districtService.findAllDistrictByStateID(stateId));
        districts.sort(Comparator.comparing(d -> d.getName() == null ? "" : d.getName(), String.CASE_INSENSITIVE_ORDER));
        return districts;
    }

}
