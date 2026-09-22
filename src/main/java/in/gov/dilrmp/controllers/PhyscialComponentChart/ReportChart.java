package in.gov.dilrmp.controllers.PhyscialComponentChart;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("chart")
public class ReportChart {
    @GetMapping("/clr-chart")
    public String clrChart(Model model) {
        return "physicalComponentChart/clrChart";
    }
    @GetMapping("/map-chart")
    public String mapChart(Model model) {
        return "physicalComponentChart/mapDigitizationChart";
    }
    @GetMapping("/mrr-chart")
    public String mrrChart(Model model) {
        return "physicalComponentChart/MRRChart";
    }
    @GetMapping("/sro-chart")
    public String sroChart(Model model) {
        return "physicalComponentChart/SROChart";
    }
    @GetMapping("/survey-chart")
    public String surveyChart(Model model) {
        return "physicalComponentChart/surveyResurveyChart";
    }
}