package in.gov.dilrmp.controllers.physicalProgressReports;
import in.gov.dilrmp.models.reportDTO.clr.DistrictClrReportView;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.MRRViewReportRepository;
import in.gov.dilrmp.services.DataEntryForm.StateMISDataEntryService;
import in.gov.dilrmp.services.physicalProgressServices.DistrictMRRService;
import in.gov.dilrmp.services.physicalProgressServices.StateMRRService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/physicalProgressReports/mrr")
public class MRRReportController {

    @Autowired
    StateMRRService stateMRRService;
    @Autowired
    StateMISDataEntryService stateMISDataEntryService;
    @Autowired
    DistrictMRRService districtMRRService;

    @Autowired
    MRRViewReportRepository mrrViewReportRepository;

    @GetMapping("/state-level")
    public String getStateDistrictReport(Model model, HttpSession session) {
        Map<String, String> labels =stateMRRService.createLabels();
        model.addAttribute("labels", labels);
        List<MrrViewReport> mrrViewReportList = stateMRRService.getAllMrrFormatedList();
        List<MrrViewReport> grandTotal = stateMRRService.getStateMrrReportsFormatedGrandToatal();
        model.addAttribute("mrrViewReportList",mrrViewReportList);
        model.addAttribute("grandTotal",grandTotal);
        session.setAttribute("mrrViewReportList",mrrViewReportList);
        session.setAttribute("mrrGrandTotal",grandTotal);

        return "pages/misReports/state_MRR_ProgressReportNew";
    }

    @PostMapping("/sorting-mrr")
    public String filterAndSortStateMRR(@Valid MrrViewReport mrrViewReport,
                                        @RequestParam(name = "parameter", required = false) String parameter,
                                        @RequestParam(value = "ascDesc", required = false) String ascDesc,
                                        Model model, HttpSession session) {
        Map<String, String> labels =stateMRRService.createLabels();
        model.addAttribute("labels", labels);
        try {
            List<MrrViewReport> mrrViewReportList = stateMRRService.filterAndSortStateMRRReport(parameter, ascDesc);
            List<MrrViewReport> grandTotal = stateMRRService.getStateMrrReportsFormatedGrandToatal();
            model.addAttribute("mrrViewReportList",mrrViewReportList);
            model.addAttribute("grandTotal",grandTotal);
            session.setAttribute("mrrViewReportList",mrrViewReportList);
            session.setAttribute("mrrGrandTotal",grandTotal);
            return "pages/misReports/state_MRR_ProgressReportNew";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }


    @GetMapping("/district-level/{stateId}")
    public String getMRRDistrictReportView(@PathVariable("stateId") Long stateId, Model model, HttpSession session) {
        Map<String, String> labels = stateMRRService.createLabels();
        model.addAttribute("labels", labels);
        List<DistrictMrrViewReport> districtMrrData = districtMRRService.getDistrictReportsByStateId(stateId);
        List<MrrViewReport> stateMrrData= districtMRRService.getMrrListByStateId(stateId);
        model.addAttribute("districtMrrData",districtMrrData);
        session.setAttribute("districtMrrData",districtMrrData);
        model.addAttribute("stateMrrData",stateMrrData);
        session.setAttribute("stateMrrData",stateMrrData);
        model.addAttribute("stateName",districtMrrData.get(0).getStateName());
        session.setAttribute("stateName",districtMrrData.get(0).getStateName());
        return "pages/physicalprogress/district_MRR_ProgressReport";
    }

}
