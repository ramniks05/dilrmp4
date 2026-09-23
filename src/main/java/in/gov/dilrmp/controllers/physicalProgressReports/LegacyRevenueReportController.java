package in.gov.dilrmp.controllers.physicalProgressReports;

import in.gov.dilrmp.models.reportDTO.legacyRevenue.DistrictLegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.LegacyRevenueReport;
import in.gov.dilrmp.services.physicalProgressServices.LegacyRevenueReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/physicalProgressReports/legacy-revenue")
public class LegacyRevenueReportController {

    @Autowired
    LegacyRevenueReportService legacyRevenueReportService;

    @GetMapping("/state-level")
    public String getStateReport(Model model, HttpSession session) {
        Map<String, String> labels = legacyRevenueReportService.createLabels();
        model.addAttribute("labels", labels);
        List<LegacyRevenueReport> list = legacyRevenueReportService.getAllFormattedList();
        List<LegacyRevenueReport> grandTotal = legacyRevenueReportService.getGrandTotalFormatted();
        model.addAttribute("legacyRevenueList", list);
        model.addAttribute("grandTotal", grandTotal);
        session.setAttribute("legacyRevenueList", list);
        session.setAttribute("legacyRevenueGrandTotal", grandTotal);
        return "pages/physicalprogress/state_LegacyRevenueReport";
    }

    @GetMapping("/district-level/{stateId}")
    public String getDistrictReport(@PathVariable("stateId") Long stateId, Model model, HttpSession session) {
        Map<String, String> labels = legacyRevenueReportService.createLabels();
        model.addAttribute("labels", labels);
        List<DistrictLegacyRevenueReport> districtList =
                legacyRevenueReportService.getDistrictListByStateId(stateId);
        List<LegacyRevenueReport> stateTotal =
                legacyRevenueReportService.getStateListForDistrictPage(stateId);
        model.addAttribute("districtLegacyRevenueData", districtList);
        model.addAttribute("stateLegacyRevenueData", stateTotal);
        session.setAttribute("districtLegacyRevenueData", districtList);
        session.setAttribute("stateLegacyRevenueData", stateTotal);
        String stateName = districtList.isEmpty() || districtList.get(0).getStateName() == null
                ? "" : districtList.get(0).getStateName();
        if (stateName.isBlank() && !stateTotal.isEmpty() && stateTotal.get(0).getStateName() != null) {
            stateName = stateTotal.get(0).getStateName();
        }
        model.addAttribute("stateName", stateName);
        session.setAttribute("legacyRevenueStateName", stateName);
        session.setAttribute("legacyRevenueStateId", stateId);
        return "pages/physicalprogress/district_LegacyRevenueReport";
    }
}
