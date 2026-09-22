package in.gov.dilrmp.controllers.physicalProgressReports;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.DistrictRcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.services.physicalProgressServices.DistrictRCMSService;
import in.gov.dilrmp.services.physicalProgressServices.StateRCMSService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/physicalProgressReports/rcms")
public class RCMSController {
  @Autowired
  StateRCMSService stateRCMSService;
  @Autowired
  DistrictRCMSService districtRCMSService;


    @GetMapping("/state-level")
    public String getRCMSReportView(Model model, HttpSession session) {
        Map<String, String> labels = stateRCMSService.createLabels();
        model.addAttribute("labels", labels);
        List<RcmsReportDTO> rcmsReportList = stateRCMSService.getRcmsStateViwe();
        List<RcmsReportDTO> grandTotal = stateRCMSService.getStateRcmsReportsGrandToatal();
        model.addAttribute("rcmsReportList",rcmsReportList);
        model.addAttribute("grandTotal",grandTotal);
        session.setAttribute("rcmsReportList",rcmsReportList);
        session.setAttribute("rcmsGrandTotal",grandTotal);

        return "pages/physicalprogress/state_RCMS_Report";
    }

    @PostMapping("/sorting-rcms")
    public String filterAndSortStateMRR(@Valid RcmsReportDTO rcmsReportDTO,
                                        @RequestParam(name = "parameter", required = false) String parameter,
                                        @RequestParam(value = "ascDesc", required = false) String ascDesc,
                                        Model model, HttpSession session) {
        Map<String, String> labels = stateRCMSService.createLabels();
        model.addAttribute("labels", labels);
        try {
            List<RcmsReportDTO> rcmsReportList = stateRCMSService.filterAndSortStateSurveyReport(parameter, ascDesc);
            List<RcmsReportDTO> grandTotal = stateRCMSService.getStateRcmsReportsGrandToatal();
            model.addAttribute("rcmsReportList",rcmsReportList);
            model.addAttribute("grandTotal",grandTotal);
            session.setAttribute("rcmsReportList",rcmsReportList);
            session.setAttribute("rcmsGrandTotal",grandTotal);
            return "pages/physicalprogress/state_RCMS_Report";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }


    @GetMapping("/district-level/{stateId}")
    public String getRCMSDistrictReportView(@PathVariable("stateId") Long stateId, Model model, HttpSession session) {
        Map<String, String> labels = stateRCMSService.createLabels();
        model.addAttribute("labels", labels);
        List<DistrictRcmsReportDTO> districtRcmsData = districtRCMSService.getDistrictListByStateId(stateId);
        model.addAttribute("districtRcmsData",districtRcmsData);
        session.setAttribute("districtRcmsData",districtRcmsData);
        List<RcmsReportDTO>  stateRcmsData = districtRCMSService.getStateListByStateId(stateId);
        model.addAttribute("stateRcmsData",stateRcmsData);
        session.setAttribute("stateRcmsData",stateRcmsData);
        model.addAttribute("stateName",districtRcmsData.get(0).getStateName());
        session.setAttribute("stateName",districtRcmsData.get(0).getStateName());
        return "pages/physicalprogress/district_RCMS_Report";
    }
}
