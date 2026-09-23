package in.gov.dilrmp.controllers.physicalProgressReports;

import in.gov.dilrmp.models.reportDTO.sroModernization.DistrictSroModernizationReport;
import in.gov.dilrmp.models.reportDTO.sroModernization.SroModernizationReport;
import in.gov.dilrmp.services.physicalProgressServices.SroModernizationReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/physicalProgressReports/sro-modernization")
public class SroModernizationReportController {

    @Autowired
    SroModernizationReportService sroModernizationReportService;

    @GetMapping("/state-level")
    public String getStateReport(Model model, HttpSession session) {
        Map<String, String> labels = sroModernizationReportService.createLabels();
        model.addAttribute("labels", labels);
        List<SroModernizationReport> list = sroModernizationReportService.getAllFormattedList();
        List<SroModernizationReport> grandTotal = sroModernizationReportService.getGrandTotalFormatted();
        model.addAttribute("sroModernizationList", list);
        model.addAttribute("grandTotal", grandTotal);
        session.setAttribute("sroModernizationList", list);
        session.setAttribute("sroModernizationGrandTotal", grandTotal);
        return "pages/physicalprogress/state_SroModernizationReport";
    }

    @GetMapping("/district-level/{stateId}")
    public String getDistrictReport(@PathVariable("stateId") Long stateId, Model model, HttpSession session) {
        Map<String, String> labels = sroModernizationReportService.createLabels();
        model.addAttribute("labels", labels);
        List<DistrictSroModernizationReport> districtData =
                sroModernizationReportService.getDistrictListByStateId(stateId);
        List<SroModernizationReport> stateTotals =
                sroModernizationReportService.getStateTotalsForDistrictPage(stateId);
        String stateName = stateTotals.isEmpty() || stateTotals.get(0).getStateName() == null
                ? ""
                : stateTotals.get(0).getStateName();
        model.addAttribute("districtSroModernizationData", districtData);
        model.addAttribute("stateSroModernizationData", stateTotals);
        model.addAttribute("stateName", stateName);
        session.setAttribute("districtSroModernizationData", districtData);
        session.setAttribute("stateSroModernizationData", stateTotals);
        session.setAttribute("stateName", stateName);
        return "pages/physicalprogress/district_SroModernizationReport";
    }
}
