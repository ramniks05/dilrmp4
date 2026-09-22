package in.gov.dilrmp.controllers.physicalProgressReports;
import in.gov.dilrmp.models.reportDTO.ComparisonReportDTO.ComparisonReportDTO;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictAadhaarLinkingRepository;
import in.gov.dilrmp.services.physicalProgressServices.ComparisonDateByMisReportService;
import in.gov.dilrmp.services.physicalProgressServices.MapDigitizationReportService;
import in.gov.dilrmp.services.physicalProgressServices.StateMAPService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("dolr")
public class ComparisonReportController {

    private static final Logger logger = LoggerFactory.getLogger(ComparisonReportController.class);
    @Autowired
    private ComparisonDateByMisReportService comparisonService;

    @Autowired
    MapDigitizationReportService mapDigitizationReportService;
    @Autowired
    StateMAPService stateMAPService;

    @Autowired
    DistrictAadhaarLinkingRepository districtAadhaarLinkingRepository;

    @GetMapping("/date-wise-report-comparison")
    public String dateWiseReportComparison() {
        return "pages/physicalprogress/date_wise_report_comparison::date_wise_report_comparison";

    }


    @GetMapping("/date-wise-report-comparison-impComponenet")
    public String dateWiseReportComparisonimpComponenet(Model model) {
        List<Date> dateList=districtAadhaarLinkingRepository.findDistinctBackupDatesFromStateClrReport();
        model.addAttribute("dateList",dateList);
        return "pages/physicalprogress/date_wise_report_comparison_impComponenet::date_wise_report_comparison_impComponenet";

    }

    @GetMapping("/date-wise-report")
    public String dateWiseReport() {
        return "pages/physicalprogress/date_wise_report::date_wise_report";

    }


    @PostMapping("/comparison-report")
    public ResponseEntity<?> getComparisonReport(@RequestParam String date1, @RequestParam(required = false) String date2, HttpSession session) {
        try {
            if (date1 == null || date1.isEmpty()) {
                return new ResponseEntity<>("date1 must be provided", HttpStatus.BAD_REQUEST);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate1 = LocalDate.parse(date1, formatter);
            LocalDate localDate2 = (date2 != null && !date2.isEmpty()) ? LocalDate.parse(date2,formatter) : null;

            List<ComparisonReportDTO> comparisonData = comparisonService.getDataDifferences(localDate1, localDate2);

            if (comparisonData.isEmpty()) {
                return new ResponseEntity<>("No differences found for the given dates", HttpStatus.NOT_FOUND);
            }

            session.setAttribute("comparisonData", comparisonData);
            session.setAttribute("fromdate", date1);
            session.setAttribute("todate", date2);
            session.setAttribute("grandTotal", comparisonData.get(10));
            return ResponseEntity.ok(comparisonData);

        } catch (Exception e) {
            logger.error("Error occurred while fetching comparison report", e);
            return new ResponseEntity<>("An error occurred while processing your request. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



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
        return "pages/physicalprogress/state_SeparateMapDigitizationDolrReport";
    }


}
