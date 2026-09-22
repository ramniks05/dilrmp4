package in.gov.dilrmp.controllers.oomf;
import in.gov.dilrmp.controllers.physicalProgressReports.ComparisonReportController;
import in.gov.dilrmp.models.oomf.OOMFReport;
import in.gov.dilrmp.models.reportDTO.ComparisonReportDTO.ComparisonReportDTO;
import in.gov.dilrmp.repositories.oomf.OOMFReportRepository;
import in.gov.dilrmp.services.oomf.OOMFReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dolr")
public class OOMFController {
    private static final Logger logger = LoggerFactory.getLogger(OOMFController.class);
    @Autowired
    OOMFReportService oomfReportService;

    @Autowired
    OOMFReportRepository oomfReportRepository;

    @GetMapping("/omf-report")
    public String omfReport(Model model) {
        List<OOMFReport> oomfReport = oomfReportRepository.findAll();

        List<String> distinctFinancialYears = oomfReportService.getFinancialYears();
        List<String> distinctQuarters = oomfReportService.getQuarters();
        OOMFReport oomf=new OOMFReport();

        model.addAttribute("distinctFinancialYears", distinctFinancialYears);
        model.addAttribute("distinctQuarters", distinctQuarters);
        model.addAttribute("oomfReport", oomfReport);
        model.addAttribute("oomf", oomf);
        return "pages/oomfReport/oomf :: oomf-report";
    }

    @PostMapping("/omf-report-filter")
    public ResponseEntity<?> getOmfReportByFilter(@RequestParam String financialYear, @RequestParam String quarter, HttpSession session) {
        try {
            if (financialYear == null || quarter.isEmpty()) {
                return new ResponseEntity<>("Financial Year and Quarter must be provided", HttpStatus.BAD_REQUEST);
            }
            List<OOMFReport> oomfReport=new ArrayList<>();
            if ("ALL".equals(quarter)) {
                oomfReport = oomfReportService.getOOMFReportListByYear(financialYear);
            } else {
                oomfReport = oomfReportService.getOOMFReportListByYearQuarter(financialYear, quarter);
            }

            if (oomfReport.isEmpty()) {
                return new ResponseEntity<>("No found for the given dates", HttpStatus.NOT_FOUND);
            }
            session.setAttribute("oomfReport", oomfReport);
            session.setAttribute("selectedyear", financialYear);
            session.setAttribute("selectedquater", quarter);
            return ResponseEntity.ok(oomfReport);

        } catch (Exception e) {
            logger.error("Error occurred while fetching the OOMF report", e);
            return new ResponseEntity<>("An error occurred while processing your request. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/data-entry")
    public String showDataEntryForm(@RequestParam(name = "success", required = false) Boolean success,
                                    @RequestParam(name = "error", required = false) String error,
                                    Model model) {
        List<String> distinctFinancialYears = oomfReportService.getFinancialYears();
        List<String> distinctQuarters = oomfReportService.getQuarters();
        model.addAttribute("distinctFinancialYears", distinctFinancialYears);
        model.addAttribute("distinctQuarters", distinctQuarters);
        if (success != null && success) {
            model.addAttribute("success", "Data saved successfully!");
        }
        if (error != null) {
            model.addAttribute("error", error);
        }

        return "pages/oomfReport/oomfTargetDataEntryForm ::target-entry";
    }


    @PostMapping("/save-data-entry")
    public ResponseEntity<String> saveDataEntry(@RequestBody Map<String, String> requestData) {

        // Create logger instance for the controller
        Logger logger = LoggerFactory.getLogger(getClass());

        try {
            // Log the incoming request data
            logger.info("Received data for OOMF Report: Financial Year: {}, Quarter: {}, Component Name: {}, Target: {}",
                    requestData.get("financialYear"),
                    requestData.get("quarter"),
                    requestData.get("componentName"),
                    requestData.get("target"));

            String financialYear = requestData.get("financialYear");
            String quarter = requestData.get("quarter");
            String componentName = requestData.get("componentName");
            String target = requestData.get("target");

            OOMFReport existingReport = oomfReportService.findByFinancialYearAndQuarter(financialYear, quarter);

            // Check if report exists and log accordingly
            if (existingReport != null) {
                logger.info("Found existing report for Financial Year: {} and Quarter: {}", financialYear, quarter);
                setTarget(existingReport, componentName, target);
                oomfReportService.saveOomfReportDataEntry(existingReport);
                logger.info("OOMF Target Data  data saved for existing report: {}", existingReport);
            } else {
                OOMFReport oomfReport = new OOMFReport();
                logger.info("No existing report found, creating new OOMF Report for Financial Year: {} and Quarter: {}",
                        financialYear, quarter);
                setTarget(oomfReport, componentName, target);
                oomfReportService.saveOomfReportDataEntry(oomfReport);
                logger.info("New OOMF Target Data  data saved: {}", oomfReport);
            }

            return ResponseEntity.ok("OOMF Target Data Saved successfully!");

        } catch (Exception e) {
            // Log the exception details
            logger.error("Error occurred while saving OOMF Data: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong. Please try again.");
        }
    }

    private void setTarget(OOMFReport report, String componentName, String target) {
        int targetValue = Integer.parseInt(target);
        switch (componentName) {
            case "target1":
                report.setTarget1(targetValue);
                break;
            case "target2":
                report.setTarget2(targetValue);
                break;
            case "target3":
                report.setTarget3(targetValue);
                break;
            case "target4":
                report.setTarget4(targetValue);
                break;
            case "target5":
                report.setTarget5(targetValue);
                break;
            case "target6":
                report.setTarget6(targetValue);
                break;
            case "target7":
                report.setTarget7(targetValue);
                break;
            case "target8":
                report.setTarget8(targetValue);
                break;
            case "target9":
                report.setTarget9(targetValue);
                break;
            case "target10":
                report.setTarget10(targetValue);
                break;
            case "target11":
                report.setTarget11(targetValue);
                break;
            default:
                throw new IllegalArgumentException("Invalid component name: " + componentName);
        }
    }

}


