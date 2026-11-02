package in.gov.dilrmp.controllers.cna;

import in.gov.dilrmp.models.cna.CnaBankBalanceReport;
import in.gov.dilrmp.services.cna.CnaBankBalanceReportService;
import in.gov.dilrmp.utils.CnaBankBalanceReportExporter;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;
import java.math.BigDecimal;
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
            model.addAttribute("totalBalance", totalBalance(reports));
            model.addAttribute("reportTitle", latestDateTitle(reports));
            model.addAttribute("availableSharedOnDates", cnaBankBalanceReportService.getDistinctSharedOnDates());
            return "pages/cna/cnaBankBalanceReport :: cna-bank-balance";
        } catch (Exception e) {
            logger.error("Error loading CNA bank balance page", e);
            model.addAttribute("reports", List.of());
            model.addAttribute("totalBalance", "0");
            model.addAttribute("availableSharedOnDates", List.of());
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
            response.put("reportTitle", "Balance Date: " + asOnDate.format(DATE_FORMATTER));
            response.put("reports", toReportRows(reports));
            response.put("totalBalance", totalBalance(reports));
            if (reports.isEmpty()) {
                response.put("message", "No record found for Balance Date " + asOnDate.format(DATE_FORMATTER) + ".");
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error generating as-on-date CNA report", e);
            response.put("status", "error");
            response.put("message", "Failed to generate the Balance Date report.");
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
            response.put("reportTitle", "Balance Date: " + fromDate.format(DATE_FORMATTER)
                    + " to " + toDate.format(DATE_FORMATTER));
            response.put("reports", toReportRows(reports));
            response.put("totalBalance", totalBalance(reports));
            if (reports.isEmpty()) {
                response.put("message", "No records found for the selected Balance Dates.");
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

    @GetMapping("/cna-bank-balance/report/pdf")
    public void downloadPdf(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            HttpServletResponse response) throws IOException {
        writeDownload(date, fromDate, toDate, response, true);
    }

    @GetMapping("/cna-bank-balance/report/excel")
    public void downloadExcel(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            HttpServletResponse response) throws IOException {
        writeDownload(date, fromDate, toDate, response, false);
    }

    private void writeDownload(LocalDate date, LocalDate fromDate, LocalDate toDate,
                               HttpServletResponse response, boolean pdf) throws IOException {
        try {
            List<CnaBankBalanceReport> reports = cnaBankBalanceReportService.getReportForDownload(date, fromDate, toDate);
            String heading = downloadHeading(date, fromDate, toDate, reports);
            if (pdf) {
                CnaBankBalanceReportExporter.writePdf(response, reports, heading);
            } else {
                CnaBankBalanceReportExporter.writeExcel(response, reports, heading);
            }
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Error downloading CNA bank balance report", e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to download the report.");
            }
        }
    }

    private static String downloadHeading(LocalDate date, LocalDate fromDate, LocalDate toDate,
                                          List<CnaBankBalanceReport> reports) {
        if (date != null) {
            return "Balance Date: " + date.format(DATE_FORMATTER);
        }
        if (fromDate != null && toDate != null) {
            return "Balance Date: " + fromDate.format(DATE_FORMATTER) + " to " + toDate.format(DATE_FORMATTER);
        }
        return latestDateTitle(reports);
    }

    private static String latestDateTitle(List<CnaBankBalanceReport> reports) {
        if (reports == null || reports.isEmpty() || reports.get(0).getReportDate() == null) {
            return "Latest Balance Date";
        }
        return "Balance Date: " + reports.get(0).getReportDate().format(DATE_FORMATTER);
    }

    private List<Map<String, String>> toReportRows(List<CnaBankBalanceReport> reports) {
        List<Map<String, String>> rows = new ArrayList<>();
        int index = 1;
        for (CnaBankBalanceReport report : reports) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("serialNo", String.valueOf(index++));
            row.put("stateName", report.getStateName() != null ? report.getStateName() : "Not linked");
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

    private static String totalBalance(List<CnaBankBalanceReport> reports) {
        BigDecimal total = BigDecimal.ZERO;
        if (reports != null) {
            for (CnaBankBalanceReport report : reports) {
                if (report.getBankBalance() != null) {
                    total = total.add(report.getBankBalance());
                }
            }
        }
        return total.toPlainString();
    }
}
