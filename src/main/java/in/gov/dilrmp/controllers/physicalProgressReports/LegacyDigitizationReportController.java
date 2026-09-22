package in.gov.dilrmp.controllers.physicalProgressReports;

import in.gov.dilrmp.models.reportDTO.legacy.DistrictLegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.legacy.LegacyDigitizationReport;
import in.gov.dilrmp.services.physicalProgressServices.LegacyDigitizationReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/physicalProgressReports/legacy-digitization")
public class LegacyDigitizationReportController {

    @Autowired
    LegacyDigitizationReportService legacyDigitizationReportService;

    @GetMapping("/state-level")
    public String getStateReport(Model model, HttpSession session) {
        Map<String, String> labels = legacyDigitizationReportService.createLabels();
        model.addAttribute("labels", labels);
        List<LegacyDigitizationReport> list = legacyDigitizationReportService.getAllFormattedList();
        List<LegacyDigitizationReport> grandTotal = legacyDigitizationReportService.getGrandTotalFormatted();
        model.addAttribute("legacyList", list);
        model.addAttribute("grandTotal", grandTotal);
        session.setAttribute("legacyList", list);
        session.setAttribute("legacyGrandTotal", grandTotal);
        return "pages/physicalprogress/state_LegacyDigitizationReport";
    }

    @PostMapping("/sorting")
    public String sortStateReport(@RequestParam(name = "parameter", required = false) String parameter,
                                  @RequestParam(value = "ascDesc", required = false) String ascDesc,
                                  Model model, HttpSession session) {
        Map<String, String> labels = legacyDigitizationReportService.createLabels();
        model.addAttribute("labels", labels);
        List<LegacyDigitizationReport> list = legacyDigitizationReportService.filterAndSort(parameter, ascDesc);
        List<LegacyDigitizationReport> grandTotal = legacyDigitizationReportService.getGrandTotalFormatted();
        model.addAttribute("legacyList", list);
        model.addAttribute("grandTotal", grandTotal);
        session.setAttribute("legacyList", list);
        session.setAttribute("legacyGrandTotal", grandTotal);
        return "pages/physicalprogress/state_LegacyDigitizationReport";
    }

    @GetMapping("/district-level/{stateId}")
    public String getDistrictReport(@PathVariable("stateId") Long stateId, Model model, HttpSession session) {
        Map<String, String> labels = legacyDigitizationReportService.createLabels();
        model.addAttribute("labels", labels);
        List<DistrictLegacyDigitizationReport> districtList =
                legacyDigitizationReportService.getDistrictListByStateId(stateId);
        List<LegacyDigitizationReport> stateTotal =
                legacyDigitizationReportService.getStateListForDistrictPage(stateId);
        model.addAttribute("districtLegacyData", districtList);
        model.addAttribute("stateLegacyData", stateTotal);
        session.setAttribute("districtLegacyData", districtList);
        session.setAttribute("stateLegacyData", stateTotal);
        String stateName = districtList.isEmpty() ? "" : districtList.get(0).getStateName();
        model.addAttribute("stateName", stateName);
        session.setAttribute("stateName", stateName);
        return "pages/physicalprogress/district_LegacyDigitizationReport";
    }
}
