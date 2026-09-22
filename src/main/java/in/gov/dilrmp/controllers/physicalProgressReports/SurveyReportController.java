package in.gov.dilrmp.controllers.physicalProgressReports;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.DistrictSurveyResurveyViewReport;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.services.physicalProgressServices.DistrictSurveyService;
import in.gov.dilrmp.services.physicalProgressServices.StateSurveyService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/physicalProgressReports/survey")
public class SurveyReportController {

    @Autowired
    StateSurveyService stateSurveyService;
    @Autowired
    DistrictSurveyService districtSurveyService;


    @GetMapping("/state-level")
    public String getSurveyReportView(Model model, HttpSession session) {
        Map<String, String> labels = stateSurveyService.createLabels();
        model.addAttribute("labels", labels);
        List<SurveyResurveyViewReport> surveyResurveyViewReportList = stateSurveyService.getAllSurveyResurveyList();
        List<SurveyResurveyViewReport> grandTotal = stateSurveyService.getStateSurveyReportsGrandToatal();
        model.addAttribute("surveyResurveyViewReportList",surveyResurveyViewReportList);
        model.addAttribute("grandTotal",grandTotal);
        session.setAttribute("surveyResurveyViewReportList",surveyResurveyViewReportList);
        session.setAttribute("surveyResurveyGrandTotal",grandTotal);
        return "pages/misReports/state_Survey_ProgresReportNew";

    }


    @PostMapping("/sorting-survey")
    public String filterAndSortStateMRR(@Valid SurveyResurveyViewReport surveyResurveyViewReport,
                                        @RequestParam(name = "parameter", required = false) String parameter,
                                        @RequestParam(value = "ascDesc", required = false) String ascDesc,
                                        Model model, HttpSession session) {
        Map<String, String> labels = stateSurveyService.createLabels();
        model.addAttribute("labels", labels);
        try {
            List<SurveyResurveyViewReport> surveyResurveyViewReportList= stateSurveyService.filterAndSortStateSurveyReport(parameter, ascDesc);
            List<SurveyResurveyViewReport> grandTotal = stateSurveyService.getStateSurveyReportsGrandToatal();
            model.addAttribute("surveyResurveyViewReportList",surveyResurveyViewReportList);
            model.addAttribute("grandTotal",grandTotal);
            session.setAttribute("surveyResurveyViewReportList",surveyResurveyViewReportList);
            session.setAttribute("surveyResurveyGrandTotal",grandTotal);
            return "pages/misReports/state_Survey_ProgresReportNew";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @GetMapping("/district-level/{stateId}")
    public String getMRRDistrictReportView(@PathVariable("stateId") Long stateId, Model model, HttpSession session) {
        Map<String, String> labels = stateSurveyService.createLabels();
        model.addAttribute("labels", labels);
        List<DistrictSurveyResurveyViewReport> districtSurveyData = districtSurveyService.getDistrictListByStateId(stateId);
        model.addAttribute("districtSurveyData",districtSurveyData);
        session.setAttribute("districtSurveyData",districtSurveyData);
        List<SurveyResurveyViewReport> stateSurveyData = districtSurveyService.getStateListByStateId(stateId);
        model.addAttribute("stateSurveyData",stateSurveyData);
        session.setAttribute("stateSurveyData",stateSurveyData);
        model.addAttribute("stateName",districtSurveyData.get(0).getStateName());
        session.setAttribute("stateName",districtSurveyData.get(0).getStateName());
        return "pages/physicalprogress/district_Survey_ProgresReport";
    }

}
