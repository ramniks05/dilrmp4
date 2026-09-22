package in.gov.dilrmp.controllers.cna;

import in.gov.dilrmp.models.cna.CnaBankBalanceReport;
import in.gov.dilrmp.services.cna.CnaBankBalanceReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dolr")
public class CnaBankBalanceReportController {

    private static final Logger logger = LoggerFactory.getLogger(CnaBankBalanceReportController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    private CnaBankBalanceReportService cnaBankBalanceReportService;

    @GetMapping("/cna-bank-balance")
    public String showUploadPage(Model model) {
        try {
            List<CnaBankBalanceReport> reports = cnaBankBalanceReportService.getLatestUploadReport();
            model.addAttribute("reports", reports);
            model.addAttribute("reportTitle", "Latest Upload");
            return "pages/cna/cnaBankBalanceReport :: cna-bank-balance";
        } catch (Exception e) {
            logger.error("Error loading CNA bank balance page", e);
            model.addAttribute("reports", List.of());
            model.addAttribute("reportTitle", "Latest Upload");
            model.addAttribute("pageError", "Unable to load CNA bank balance report. Please try again.");
            return "pages/cna/cnaBankBalanceReport :: cna-bank-balance";
        }
    }

    @PostMapping("/cna-bank-balance/upload")
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadExcel(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            int count = cnaBankBalanceReportService.uploadFromExcel(file);
            response.put("status", "success");
            response.put("message", count + " CNA bank balance record(s) uploaded successfully.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error uploading CNA bank balance report", e);
            response.put("status", "error");
            String detail = e.getMessage() != null ? e.getMessage() : "Unknown error";
            response.put("message", "Failed to upload Excel file: " + detail);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/cna-bank-balance/report/as-on-date")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAsOnDateReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOnDate) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<CnaBankBalanceReport> reports = cnaBankBalanceReportService.getReportAsOnDate(asOnDate);
            response.put("status", "success");
            response.put("reportTitle", "As On Date: " + asOnDate.format(DATE_FORMATTER));
            response.put("reports", toReportRows(reports));
            if (reports.isEmpty()) {
                response.put("message", "No upload found on " + asOnDate.format(DATE_FORMATTER) + ".");
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error generating as-on-date CNA report", e);
            response.put("status", "error");
            response.put("message", "Failed to generate As On Date report.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/cna-bank-balance/report/between-dates")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBetweenDatesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<CnaBankBalanceReport> reports = cnaBankBalanceReportService.getReportBetweenDates(fromDate, toDate);
            response.put("status", "success");
            response.put("reportTitle", "Between Dates: " + fromDate.format(DATE_FORMATTER)
                    + " to " + toDate.format(DATE_FORMATTER));
            response.put("reports", toReportRows(reports));
            if (reports.isEmpty()) {
                response.put("message", "No uploads found between selected dates.");
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error generating between-dates CNA report", e);
            response.put("status", "error");
            response.put("message", "Failed to generate Between Dates report.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private List<Map<String, String>> toReportRows(List<CnaBankBalanceReport> reports) {
        List<Map<String, String>> rows = new ArrayList<>();
        int index = 1;
        for (CnaBankBalanceReport report : reports) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("serialNo", String.valueOf(index++));
            row.put("cnaCode", report.getCnaCode());
            row.put("cnaName", report.getCnaName());
            row.put("bankBalance", report.getBankBalance() != null ? report.getBankBalance().toPlainString() : "-");
            row.put("reportDate", report.getReportDate() != null ? report.getReportDate().format(DATE_FORMATTER) : "-");
            row.put("uploadedOn", report.getUploadedOn() != null
                    ? report.getUploadedOn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "-");
            rows.add(row);
        }
        return rows;
    }
}
