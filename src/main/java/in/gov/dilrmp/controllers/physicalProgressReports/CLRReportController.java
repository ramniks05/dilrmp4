package in.gov.dilrmp.controllers.physicalProgressReports;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import in.gov.dilrmp.repositories.physicalProgressRepositories.StateClrReportViewRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import in.gov.dilrmp.models.reportDTO.clr.DistrictClrReportView;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.services.DataEntryForm.StateMISDataEntryService;
import in.gov.dilrmp.services.physicalProgressServices.DistrictCLRService;
import in.gov.dilrmp.services.physicalProgressServices.StateCLRAsOnDateService;
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
    @Autowired
    StateCLRAsOnDateService stateCLRAsOnDateService;

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
    @GetMapping("/state-level-as-on-date")
    public String getCLRReportAsOnDate(@RequestParam(name = "asOnDate", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOnDate,
                                       Model model) {
        LocalDate today = LocalDate.now();
        if (asOnDate == null || asOnDate.isAfter(today)) {
            asOnDate = today;
        }
        StateCLRAsOnDateService.AsOnDateResult result = stateCLRAsOnDateService.getReportAsOn(asOnDate);

        model.addAttribute("labels", stateCLRService.createLabels());
        model.addAttribute("stateClrList", result.getStateList());
        model.addAttribute("grandTotal", result.getGrandTotal());
        model.addAttribute("asOnDate", asOnDate);
        model.addAttribute("snapshotDate", result.getSnapshotDate());
        model.addAttribute("liveData", result.isLive());
        model.addAttribute("approximateStates", result.getApproximateStates());
        model.addAttribute("minDate", stateCLRAsOnDateService.getFirstAvailableDate());
        model.addAttribute("maxDate", today);
        return "pages/misReports/state_CLR_ProgressReportAsOnDate";
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
