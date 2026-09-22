package in.gov.dilrmp.controllers.physicalProgressReports;

import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.DistrictMapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.services.physicalProgressServices.DistrictMAPService;
import in.gov.dilrmp.services.physicalProgressServices.MapDigitizationReportService;
import in.gov.dilrmp.services.physicalProgressServices.StateMAPService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/physicalProgressReports/map")
public class MapDigitizationReportController {
    @Autowired
    MapDigitizationReportService mapDigitizationReportService;
    @Autowired
    StateMAPService stateMAPService;
    @Autowired
    DistrictMAPService districtMAPService;

    @GetMapping("/state-level")
    public String getMapDigitizationReportView(Model model, HttpSession session) {
        Map<String, String> labels = stateMAPService.createLabels();
        model.addAttribute("labels", labels);
        List<MapDigitizationReport> mapDigitizationReport=mapDigitizationReportService.getAllMapDigitizationReportFormate();
        model.addAttribute("reportData", mapDigitizationReport);
        session.setAttribute("reportData", mapDigitizationReport);
        List<MapDigitizationReport> grandTotal = mapDigitizationReportService.getStateMapReportsGrandToatalFormated();
        model.addAttribute("grandTotal",grandTotal);
        session.setAttribute("mapGrandTotal",grandTotal);
        return "pages/physicalprogress/state_MapDigitizationReport";
    }

    @PostMapping("/sorting-map")
    public String filterAndSortStateCLR(@Valid StateClrReportView stateClrReportView,
                                        @RequestParam(name = "parameter", required = false) String parameter,
                                        @RequestParam(value = "ascDesc", required = false) String ascDesc,
                                        Model model, HttpSession session) {
        Map<String, String> labels = stateMAPService.createLabels();
        model.addAttribute("labels", labels);
        try {
            List<MapDigitizationReport> mapDigitizationReport = mapDigitizationReportService.filterAndSortStateClrReport(parameter, ascDesc);
            List<MapDigitizationReport> grandTotal = mapDigitizationReportService.getStateMapReportsGrandToatalFormated();
            model.addAttribute("reportData", mapDigitizationReport);
            session.setAttribute("reportData", mapDigitizationReport);
            model.addAttribute("grandTotal",grandTotal);
            session.setAttribute("mapGrandTotal",grandTotal);
            return "pages/physicalprogress/state_MapDigitizationReport";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }



    @GetMapping("/district-level/{lgdCode}")
    public String getMRRDistrictReportView(@PathVariable("lgdCode") Long lgdCode, Model model, HttpSession session) {
        Map<String, String> labels = stateMAPService.createLabels();
        model.addAttribute("labels", labels);
        List<DistrictMapDigitizationReport> mapDigitizationReport=districtMAPService.getDistrictMAPModelsByStateId(lgdCode);
        model.addAttribute("reportData", mapDigitizationReport);
        session.setAttribute("reportData", mapDigitizationReport);
        List<MapDigitizationReport> stateMapData = districtMAPService.getStateListByStateLgdCode(lgdCode);
        model.addAttribute("stateMapData",stateMapData);
        session.setAttribute("stateMapData",stateMapData);
        model.addAttribute("stateName",mapDigitizationReport.get(0).getStateName());
        session.setAttribute("stateName",mapDigitizationReport.get(0).getStateName());
        return "pages/physicalprogress/district_MapDigitizationReport";
    }


}
