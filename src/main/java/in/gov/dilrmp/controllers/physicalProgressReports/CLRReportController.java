package in.gov.dilrmp.controllers.physicalProgressReports;

import java.util.List;
import java.util.Map;

import in.gov.dilrmp.repositories.physicalProgressRepositories.StateClrReportViewRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import in.gov.dilrmp.models.reportDTO.clr.DistrictClrReportView;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.services.DataEntryForm.StateMISDataEntryService;
import in.gov.dilrmp.services.physicalProgressServices.DistrictCLRService;
import in.gov.dilrmp.services.physicalProgressServices.StateCLRService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("physicalProgressReports/clr")
public class CLRReportController {

    @Autowired
    StateCLRService stateCLRService;
    @Autowired
    StateMISDataEntryService stateMISDataEntryService;
    @Autowired
    DistrictCLRService districtCLRService;

    @Autowired
    StateClrReportViewRepository stateClrReportViewRepository;

    @GetMapping("/state-level")
    public String getCLRReportView(Model model, HttpSession session) {
        Map<String, String> labels = stateCLRService.createLabels();
        model.addAttribute("labels", labels);
        List<StateClrReportView> stateClrList = stateCLRService.getClrStateViwe();
        List<StateClrReportView> grandTotal = stateCLRService.getStateClrReportsGrandToatal();
        model.addAttribute("stateClrList",stateClrList);
        model.addAttribute("grandTotal",grandTotal);
        session.setAttribute("stateClrList",stateClrList);
        session.setAttribute("clrGrandTotal",grandTotal);

        return "pages/misReports/state_CLR_ProgressReportNew";
    }
    @PostMapping("/sorting-clr")
    public String filterAndSortStateCLR(@Valid StateClrReportView stateClrReportView,
                                        @RequestParam(name = "parameter", required = false) String parameter,
                                        @RequestParam(value = "ascDesc", required = false) String ascDesc,
                                        Model model, HttpSession session) {
        Map<String, String> labels = stateCLRService.createLabels();
        model.addAttribute("labels", labels);
        try {
            List<StateClrReportView> stateClrList = stateCLRService.filterAndSortStateClrReport(parameter, ascDesc);
            List<StateClrReportView> grandTotal = stateCLRService.getStateClrReportsGrandToatal();
            model.addAttribute("stateClrList", stateClrList);
            model.addAttribute("grandTotal", grandTotal);
            session.setAttribute("stateClrList", stateClrList);
            session.setAttribute("clrGrandTotal", grandTotal);
            return "pages/misReports/state_CLR_ProgressReportNew";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }


    @GetMapping("/district-level/{stateId}")
    public String getCLRDistrict(@PathVariable("stateId") Long stateId, Model model, HttpSession session) {
        Map<String, String> labels = stateCLRService.createLabels();
        model.addAttribute("labels", labels);
       List<DistrictClrReportView>districtClrData=districtCLRService.getDistrictReportsByStateId(stateId);
       List<StateClrReportView> stateClrData= districtCLRService.getClrListByStateId(stateId);
       model.addAttribute("districtClrData", districtClrData);
       session.setAttribute("districtClrData",districtClrData);
       model.addAttribute("stateClrData", stateClrData);
       session.setAttribute("stateClrData",stateClrData);
        if (!stateClrData.isEmpty()) {
            StateClrReportView stateReport = stateClrData.get(0);
            model.addAttribute("stateName", stateReport.getStateName());
            session.setAttribute("stateName", stateReport.getStateName());

            String ownership = String.valueOf(stateReport.getDistrictsWithGenderBasedOwnership());
            model.addAttribute("Wonership", ownership);
            session.setAttribute("Wonership", ownership);
        } else {

            model.addAttribute("error", "No data found for the given stateId.");
        }

        return "pages/physicalprogress/district_CLR_ProgressReport";
    }



}
