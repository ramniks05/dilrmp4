package in.gov.dilrmp.controllers.reports;


import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.dashboard.DashBordDTO;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.DistrictMapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.DistrictClrReportView;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.DistrictLinkedAadhaarViewReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.DistrictRcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.DistrictSurveyResurveyViewReport;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import in.gov.dilrmp.services.dashboard.DashBoardService;
import in.gov.dilrmp.services.physicalProgressServices.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reports")
public class ReportsController {
    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @Autowired
    DashBoardService dashBoardService;

    @Autowired
    StateService stateService;
    @Autowired
    DistrictCLRService districtCLRService;

    @Autowired
    StateMAPService stateMAPService;
    @Autowired
    DistrictMAPService districtMAPService;

    @Autowired
    DistrictMRRService districtMRRService;
    @Autowired
    DistrictSurveyService districtSurveyService;
    @Autowired
    StateSROService stateSROService;
    @Autowired
    DistrictRCMSService districtRCMSService;
    @Autowired
    DistrictAadharSService districtAadharSService;

    @Autowired
    StateCLRService stateCLRService;
    @Autowired
    MapDigitizationReportService mapDigitizationReportService;

    @Autowired
    StateMRRService stateMRRService;
    @Autowired
    StateSurveyService stateSurveyService;
    @Autowired
    StateRCMSService stateRCMSService;
    @Autowired
    StateAadharService stateAadharService;


    @RequestMapping(value = "/physical-progress-report", method = RequestMethod.GET)
    public String physicalProgressReport() {
             return "pages/reports/document";
    }

    @RequestMapping(value = "/download-progress-report", method = RequestMethod.GET)
    public String downloadProgressReport(Model model) {
        try {
            List<State> stateList = stateService.findAllOrderByStateName();
            model.addAttribute("stateList", stateList);
            logger.info("Successfully retrieved state list for download-progress-report.");
            return "pages/reports/download_report";
        } catch (Exception e) {
            logger.error("Error while retrieving state list for download-progress-report: ", e);
            model.addAttribute("errorMessage", "An error occurred while loading the report. Please try again.");
            return "error";
        }
    }

    @GetMapping("/getDocument")
    public ResponseEntity<?> documentViews() {
        // Fetch the data as you were doing before
        DashBordDTO nationalDashBordDTOList = dashBoardService.getNationalDashBoardData();

        // Return the DTO in the response entity with HTTP OK status
        return ResponseEntity.ok(nationalDashBordDTOList);
    }

    @PostMapping("/download-physical-progress-report")
    public ResponseEntity<Map<String, String>> downloadPhysicalProgressReport(@RequestParam("stateId") Long stateId,
                                                                              HttpSession session) {
        try {
            Map<String, String> reportData=new HashMap<>();
        if(stateId==00){
            List<StateClrReportView> stateClrList = stateCLRService.getClrStateViwe();
            List<StateClrReportView> clrGrandTotal = stateCLRService.getStateClrReportsGrandToatal();

            session.setAttribute("stateClrList",stateClrList);
            session.setAttribute("clrGrandTotal",clrGrandTotal);

            List<MapDigitizationReport> mapDigitizationReports=mapDigitizationReportService.getAllMapDigitizationReportFormate();
            List<MapDigitizationReport> mapGrandTotal = mapDigitizationReportService.getStateMapReportsGrandToatalFormated();

            session.setAttribute("reportData", mapDigitizationReports);
            session.setAttribute("mapGrandTotal",mapGrandTotal);

            List<MrrViewReport> mrrViewReportList = stateMRRService.getAllMrrFormatedList();
            List<MrrViewReport> mrrGrandTotal = stateMRRService.getStateMrrReportsFormatedGrandToatal();
            session.setAttribute("mrrViewReportList",mrrViewReportList);
            session.setAttribute("mrrGrandTotal",mrrGrandTotal);

            List<SurveyResurveyViewReport> surveyResurveyViewReportList = stateSurveyService.getAllSurveyResurveyList();
            List<SurveyResurveyViewReport> surveyResurveyGrandTotal = stateSurveyService.getStateSurveyReportsGrandToatal();
            session.setAttribute("surveyResurveyViewReportList",surveyResurveyViewReportList);
            session.setAttribute("surveyResurveyGrandTotal",surveyResurveyGrandTotal);

            List<RcmsReportDTO> rcmsReportList = stateRCMSService.getRcmsStateViwe();
            List<RcmsReportDTO> rcmsGrandTotal = stateRCMSService.getStateRcmsReportsGrandToatal();
            session.setAttribute("rcmsReportList",rcmsReportList);
            session.setAttribute("rcmsGrandTotal",rcmsGrandTotal);

            List<LinkedAadharViewReport> linkedAadharViewReportList = stateAadharService.getAllAadhaarFormatList();
            List<LinkedAadharViewReport> linkedAadharGrandTotal = stateAadharService.getStateLinkedAadhaarReportsGrandFormateToatal();
            session.setAttribute("linkedAadharViewReportList",linkedAadharViewReportList);
            session.setAttribute("linkedAadharGrandTotal",linkedAadharGrandTotal);

            List<SroReportDTO> sroStateList = stateSROService.getSroStateViwe();
            List<SroReportDTO> sroGrandTotal = stateSROService.getStateSroReportsGrandToatal();
            session.setAttribute("sroStateList",sroStateList);
            session.setAttribute("sroGrandTotal",sroGrandTotal);


            reportData = getReportDataAllState();

        }else{
            List<DistrictClrReportView> districtClrData = districtCLRService.getDistrictReportsByStateId(stateId);
            List<StateClrReportView> stateClrData = districtCLRService.getClrListByStateId(stateId);
            List<StateClrReportView> stateGrandTotalClrData= districtCLRService.getClrListByStateId(stateId);
            List<DistrictMapDigitizationReport> mapDigitizationReport=districtMAPService.getDistrictMAPModelsByStateId(stateId);
            List<MapDigitizationReport> stateGrandTotalMapData = districtMAPService.getStateListByStateLgdCode(stateId);
            List<DistrictMrrViewReport> districtMrrData = districtMRRService.getDistrictReportsByStateId(stateId);
            List<MrrViewReport> stateGrandTotalMrrData= districtMRRService.getMrrListByStateId(stateId);
            List<DistrictSurveyResurveyViewReport> districtSurveyData = districtSurveyService.getDistrictListByStateId(stateId);
            List<SurveyResurveyViewReport> stateGrandTotalSurveyData = districtSurveyService.getStateListByStateId(stateId);

            List<DistrictRcmsReportDTO> districtRcmsData = districtRCMSService.getDistrictListByStateId(stateId);
            List<RcmsReportDTO>  stateGrandTotalRcmsData = districtRCMSService.getStateListByStateId(stateId);

            List<DistrictLinkedAadhaarViewReport> districtAadhaarData = districtAadharSService.getDistrictListByStateId(stateId);
            List<LinkedAadharViewReport> stateGrandTotalAadhaarData = districtAadharSService.getStateListByStateList(stateId);

            if (!stateClrData.isEmpty()) {
                StateClrReportView stateReport = stateClrData.get(0);
                session.setAttribute("stateName", stateReport.getStateName());

                String ownership = String.valueOf(stateReport.getDistrictsWithGenderBasedOwnership());
                session.setAttribute("Wonership", ownership);
            }

            session.setAttribute("districtClrData",districtClrData);
            session.setAttribute("stateClrData",stateGrandTotalClrData);
            session.setAttribute("reportData", mapDigitizationReport);
            session.setAttribute("stateMapData",stateGrandTotalMapData);
            session.setAttribute("districtMrrData",districtMrrData);
            session.setAttribute("stateMrrData",stateGrandTotalMrrData);
            session.setAttribute("districtSurveyData",districtSurveyData);
            session.setAttribute("stateSurveyData",stateGrandTotalSurveyData);

            session.setAttribute("districtRcmsData",districtRcmsData);
            session.setAttribute("stateRcmsData",stateGrandTotalRcmsData);
            session.setAttribute("districtAadhaarData",districtAadhaarData);
            session.setAttribute("stateAadhaarData",stateGrandTotalAadhaarData);
            // Fetch the report data for the given stateId
             reportData = getReportDataByStateId();

            if (reportData == null || reportData.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No data available for the selected state."));
            }

            // Return the report data in JSON format

        }
            return ResponseEntity.ok(reportData);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "An error occurred while fetching the report data."));
        }
    }


    public Map<String, String> getReportDataAllState() {
        // Dummy data for illustration; replace with actual data retrieval logic
        Map<String, String> data = new HashMap<>();
        data.put("clr", "Computerization of Land Records (CLR)");
        data.put("prf_clr_url", "/physcial/report/pdf/clr");
        data.put("excel_clr_url", "/physcial/report/excel/clr");



        data.put("map", "Digitized Mapsheets/FMBs/Tippans (MAP)");
        data.put("prf_map_url", "/physcial/report/pdf/map");
        data.put("excel_map_url", "/physcial/report/excel/map");

        data.put("mrr", "Completed Modern Record Room (MRR)");
        data.put("prf_mrr_url", "/physcial/report/pdf/mrr");
        data.put("excel_mrr_url", "/physcial/report/excel/mrr");

        data.put("survey_resurvey", "Survey/Resurvey - Physical Progress (Survey/Resurvey)");
        data.put("prf_survey_url", "/physcial/report/pdf/survey");
        data.put("excel_survey_url", "/physcial/report/excel/survey");

        data.put("rcm", "Computerization of Revenue Court Management (E-RCMS)");
        data.put("prf_rcm_url", "/physcial/report/pdf/rcms");
        data.put("excel_rcm_url", "/physcial/report/excel/rcms");

        data.put("aadhaar_linkage", "Consent-based linkage of Aadhaar with RoR (Linkage Aadhaar)");
        data.put("prf_aadhaar_linkage_url", "/physcial/report/pdf/aadhar-linkage");
        data.put("excel_aadhaar_linkage_url", "/physcial/report/excel/aadhar");

        data.put("sro", "Computerization of Registration (SRO)");
        data.put("prf_sro_url", "/physcial/report/pdf/sro");
        data.put("excel_sro_url", "/physcial/report/excel/sro");

        // Add more key-value pairs as needed based on your table structure
        return data;
    }
    public Map<String, String> getReportDataByStateId() {
        // Dummy data for illustration; replace with actual data retrieval logic
        Map<String, String> data = new HashMap<>();
        data.put("clr", "Computerization of Land Records (CLR)");
        data.put("prf_clr_url", "/physcial/report/district/pdf/district_clr");
        data.put("excel_clr_url", "/physcial/report/excel/clr-district");

        data.put("map", "Digitized Mapsheets/FMBs/Tippans (MAP)");
        data.put("prf_map_url", "/physcial/report/district/pdf/district_map");
        data.put("excel_map_url", "/physcial/report/excel/map-district");

        data.put("mrr", "Completed Modern Record Room (MRR)");
        data.put("prf_mrr_url", "/physcial/report/district/pdf/district_mrr");
        data.put("excel_mrr_url", "/physcial/report/excel/mrr-district");

        data.put("survey_resurvey", "Survey/Resurvey - Physical Progress (Survey/Resurvey)");
        data.put("prf_survey_url", "/physcial/report/district/pdf/district_survey_resurvey");
        data.put("excel_survey_url", "/physcial/report/excel/survey-district");

        data.put("rcm", "Computerization of Revenue Court Management (E-RCMS)");
        data.put("prf_rcm_url", "/physcial/report/district/pdf/district_rcms");
        data.put("excel_rcm_url", "/physcial/report/excel/rcms-district");

        data.put("aadhaar_linkage", "Consent-based linkage of Aadhaar with RoR (Linkage Aadhaar)");
        data.put("prf_aadhaar_linkage_url", "/physcial/report/district/pdf/district_aadhaar_linkage");
        data.put("excel_aadhaar_linkage_url", "/physcial/report/excel/aadhar-district");

        // Add more key-value pairs as needed based on your table structure
        return data;
    }


}
