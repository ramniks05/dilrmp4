package in.gov.dilrmp.controllers.physicalProgressReports;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.DistrictLinkedAadhaarViewReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.services.physicalProgressServices.DistrictAadharSService;
import in.gov.dilrmp.services.physicalProgressServices.StateAadharService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/physicalProgressReports/aadhar-link")
public class AadharLinkageReportController {

    @Autowired
    StateAadharService stateAadharService;
    @Autowired
    DistrictAadharSService districtAadharSService;

    @GetMapping("/state-level")
    public String getRCMSReportView(Model model, HttpSession session) {
        Map<String, String> labels = stateAadharService.createLabels();
        model.addAttribute("labels", labels);
        List<LinkedAadharViewReport> linkedAadharViewReportList = stateAadharService.getAllAadhaarFormatList();
        List<LinkedAadharViewReport> grandTotal = stateAadharService.getStateLinkedAadhaarReportsGrandFormateToatal();
        model.addAttribute("linkedAadharViewReportList",linkedAadharViewReportList);
        model.addAttribute("grandTotal",grandTotal);
        session.setAttribute("linkedAadharViewReportList",linkedAadharViewReportList);
        session.setAttribute("linkedAadharGrandTotal",grandTotal);
        return "pages/physicalprogress/state_AadharLinkageReport";
    }

    @PostMapping("/sorting-aadhar")
    public String filterAndSortStateMRR(@Valid MrrViewReport mrrViewReport,
                                        @RequestParam(name = "parameter", required = false) String parameter,
                                        @RequestParam(value = "ascDesc", required = false) String ascDesc,
                                        Model model, HttpSession session) {
        Map<String, String> labels = stateAadharService.createLabels();
        model.addAttribute("labels", labels);
        try {
            List<LinkedAadharViewReport> linkedAadharViewReportList = stateAadharService.filterAndSortStateAadharReport(parameter, ascDesc);
            List<LinkedAadharViewReport> grandTotal = stateAadharService.getStateLinkedAadhaarReportsGrandFormateToatal();
            model.addAttribute("linkedAadharViewReportList",linkedAadharViewReportList);
            model.addAttribute("grandTotal",grandTotal);
            session.setAttribute("linkedAadharViewReportList",linkedAadharViewReportList);
            session.setAttribute("linkedAadharGrandTotal",grandTotal);
            return "pages/physicalprogress/state_AadharLinkageReport";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @GetMapping("/district-level/{stateId}")
    public String getAadharDistrictReportView(@PathVariable("stateId") Long stateId, Model model, HttpSession session) {
        Map<String, String> labels = stateAadharService.createLabels();
        model.addAttribute("labels", labels);
        List<DistrictLinkedAadhaarViewReport> districtAadhaarData = districtAadharSService.getDistrictListByStateId(stateId);
        model.addAttribute("districtAadhaarData",districtAadhaarData);
        session.setAttribute("districtAadhaarData",districtAadhaarData);
        List<LinkedAadharViewReport> stateAadhaarData = districtAadharSService.getStateListByStateList(stateId);
        model.addAttribute("stateAadhaarData",stateAadhaarData);
        session.setAttribute("stateAadhaarData",stateAadhaarData);
        model.addAttribute("stateName",districtAadhaarData.get(0).getStateName());
        session.setAttribute("stateName",districtAadhaarData.get(0).getStateName());
        return "pages/physicalprogress/district_AadharLinkageReport";
    }

}
