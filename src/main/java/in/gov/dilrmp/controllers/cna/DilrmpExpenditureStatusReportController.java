package in.gov.dilrmp.controllers.cna;

import in.gov.dilrmp.models.cna.DilrmpExpenditureStatusReport;
import in.gov.dilrmp.services.cna.DilrmpExpenditureStatusReportService;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dolr")
public class DilrmpExpenditureStatusReportController {

    private static final Logger logger = LoggerFactory.getLogger(DilrmpExpenditureStatusReportController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter REPORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    private DilrmpExpenditureStatusReportService expenditureStatusReportService;

    @GetMapping("/dilrmp-expenditure-status")
    public String showUploadPage(Model model) {
        try {
            List<DilrmpExpenditureStatusReport> reports = expenditureStatusReportService.getLatestUploadReport();
            populateReportModel(model, reports, "Latest Upload");
            model.addAttribute("availableReportDates", expenditureStatusReportService.getDistinctReportAsOnDates());
            return "pages/cna/dilrmpExpenditureStatusReport :: dilrmp-expenditure-status";
        } catch (Exception e) {
            logger.error("Error loading DILRMP expenditure status page", e);
            model.addAttribute("reports", List.of());
            model.addAttribute("reportTitle", "DILRMP Expenditure Status");
            model.addAttribute("financialYear", "");
            model.addAttribute("reportAsOnDateLabel", "");
            model.addAttribute("expenditureLabel1", "Expenditure in April");
            model.addAttribute("expenditureLabel2", "Expenditure in May");
            model.addAttribute("expenditureLabel3", "Expenditure in June");
            model.addAttribute("openingBalanceHeader", "CNA Opening Balance");
            model.addAttribute("totalExpenditureHeader", "Total Expenditure till");
            model.addAttribute("cnaBalanceHeader", "CNA Balance");
            model.addAttribute("totals", emptyTotals());
            model.addAttribute("availableReportDates", List.of());
            model.addAttribute("pageError", "Unable to load DILRMP expenditure status report. Please try again.");
            return "pages/cna/dilrmpExpenditureStatusReport :: dilrmp-expenditure-status";
        }
    }

    @PostMapping("/dilrmp-expenditure-status/upload")
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadExcel(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            int count = expenditureStatusReportService.uploadFromExcel(file);
            response.put("status", "success");
            response.put("message", count + " state record(s) uploaded successfully. You can upload another report with a different as on date.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error uploading DILRMP expenditure status report", e);
            response.put("status", "error");
            String detail = e.getMessage() != null ? e.getMessage() : "Unknown error";
            response.put("message", "Failed to upload Excel file: " + detail);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/dilrmp-expenditure-status/delete/latest")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteLatestUpload() {
        Map<String, String> response = new HashMap<>();
        try {
            int count = expenditureStatusReportService.deleteLatestUpload();
            response.put("status", "success");
            response.put("message", count + " record(s) deleted from the latest upload.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error deleting latest DILRMP expenditure report", e);
            response.put("status", "error");
            response.put("message", "Failed to delete latest upload.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/dilrmp-expenditure-status/delete/by-date")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteByReportAsOnDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportAsOnDate) {
        Map<String, String> response = new HashMap<>();
        try {
            int count = expenditureStatusReportService.deleteByReportAsOnDate(reportAsOnDate);
            response.put("status", "success");
            response.put("message", count + " record(s) deleted for DILRMP Expenditure Status as on "
                    + reportAsOnDate.format(REPORT_DATE_FORMATTER) + ".");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error deleting DILRMP expenditure report by as on date", e);
            response.put("status", "error");
            response.put("message", "Failed to delete report for selected date.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/dilrmp-expenditure-status/report/as-on-date")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAsOnDateReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOnDate) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<DilrmpExpenditureStatusReport> reports = expenditureStatusReportService.getReportAsOnDate(asOnDate);
            response.put("status", "success");
            response.put("reportTitle", buildReportTitle(reports, "As On Date: " + asOnDate.format(DATE_FORMATTER)));
            response.put("financialYear", extractFinancialYear(reports));
            response.put("reportAsOnDateLabel", extractReportAsOnDateLabel(reports));
            addHeaderLabelsToResponse(response, reports);
            response.put("reports", toReportRows(reports));
            response.put("totals", computeTotals(reports));
            if (reports.isEmpty()) {
                response.put("message", "No report found for DILRMP Expenditure Status as on "
                        + asOnDate.format(REPORT_DATE_FORMATTER) + ".");
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error generating as-on-date expenditure report", e);
            response.put("status", "error");
            response.put("message", "Failed to generate As On Date report.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private void populateReportModel(Model model, List<DilrmpExpenditureStatusReport> reports, String fallbackTitle) {
        model.addAttribute("reports", reports);
        model.addAttribute("reportTitle", buildReportTitle(reports, fallbackTitle));
        model.addAttribute("financialYear", extractFinancialYear(reports));
        model.addAttribute("reportAsOnDateLabel", extractReportAsOnDateLabel(reports));
        model.addAttribute("expenditureLabel1", extractExpenditureLabel(reports, 1));
        model.addAttribute("expenditureLabel2", extractExpenditureLabel(reports, 2));
        model.addAttribute("expenditureLabel3", extractExpenditureLabel(reports, 3));
        model.addAttribute("openingBalanceHeader", extractOpeningBalanceHeader(reports));
        model.addAttribute("totalExpenditureHeader", extractTotalExpenditureHeader(reports));
        model.addAttribute("cnaBalanceHeader", extractCnaBalanceHeader(reports));
        model.addAttribute("totals", computeTotals(reports));
    }

    private void addHeaderLabelsToResponse(Map<String, Object> response, List<DilrmpExpenditureStatusReport> reports) {
        response.put("expenditureLabel1", extractExpenditureLabel(reports, 1));
        response.put("expenditureLabel2", extractExpenditureLabel(reports, 2));
        response.put("expenditureLabel3", extractExpenditureLabel(reports, 3));
        response.put("openingBalanceHeader", extractOpeningBalanceHeader(reports));
        response.put("totalExpenditureHeader", extractTotalExpenditureHeader(reports));
        response.put("cnaBalanceHeader", extractCnaBalanceHeader(reports));
    }

    private String extractExpenditureLabel(List<DilrmpExpenditureStatusReport> reports, int index) {
        if (reports == null || reports.isEmpty()) {
            return defaultExpenditureLabel(index);
        }
        DilrmpExpenditureStatusReport report = reports.get(0);
        String label = null;
        if (index == 1) {
            label = report.getExpenditureLabel1();
        } else if (index == 2) {
            label = report.getExpenditureLabel2();
        } else if (index == 3) {
            label = report.getExpenditureLabel3();
        }
        if (label != null && !label.isBlank()) {
            return label;
        }
        return defaultExpenditureLabel(index);
    }

    private String defaultExpenditureLabel(int index) {
        if (index == 1) {
            return "Expenditure in April";
        }
        if (index == 2) {
            return "Expenditure in May";
        }
        if (index == 3) {
            return "Expenditure in June";
        }
        return "Expenditure";
    }

    private String extractOpeningBalanceHeader(List<DilrmpExpenditureStatusReport> reports) {
        if (reports == null || reports.isEmpty()) {
            return "CNA Opening Balance";
        }
        String header = reports.get(0).getOpeningBalanceHeader();
        return header != null && !header.isBlank() ? header : "CNA Opening Balance";
    }

    private String extractTotalExpenditureHeader(List<DilrmpExpenditureStatusReport> reports) {
        if (reports == null || reports.isEmpty()) {
            return "Total Expenditure till";
        }
        String header = reports.get(0).getTotalExpenditureHeader();
        if (header != null && !header.isBlank()) {
            return header;
        }
        String dateLabel = extractReportAsOnDateLabel(reports);
        return dateLabel.isBlank() ? "Total Expenditure till" : "Total Expenditure till (" + dateLabel + ")";
    }

    private String extractCnaBalanceHeader(List<DilrmpExpenditureStatusReport> reports) {
        if (reports == null || reports.isEmpty()) {
            return "CNA Balance";
        }
        String header = reports.get(0).getCnaBalanceHeader();
        if (header != null && !header.isBlank()) {
            return header;
        }
        String dateLabel = extractReportAsOnDateLabel(reports);
        return dateLabel.isBlank() ? "CNA Balance" : "CNA Balance (" + dateLabel + ")";
    }

    private String buildReportTitle(List<DilrmpExpenditureStatusReport> reports, String fallbackTitle) {
        if (reports == null || reports.isEmpty()) {
            return "DILRMP Expenditure Status";
        }
        LocalDate asOnDate = reports.get(0).getReportAsOnDate();
        if (asOnDate != null) {
            return "DILRMP Expenditure Status as on " + asOnDate.format(REPORT_DATE_FORMATTER);
        }
        return fallbackTitle;
    }

    private String extractFinancialYear(List<DilrmpExpenditureStatusReport> reports) {
        if (reports == null || reports.isEmpty()) {
            return "FY-2026-27";
        }
        String fy = reports.get(0).getFinancialYear();
        return fy != null && !fy.isBlank() ? fy : "FY-2026-27";
    }

    private String extractReportAsOnDateLabel(List<DilrmpExpenditureStatusReport> reports) {
        if (reports == null || reports.isEmpty() || reports.get(0).getReportAsOnDate() == null) {
            return "";
        }
        return reports.get(0).getReportAsOnDate().format(REPORT_DATE_FORMATTER);
    }

    private List<Map<String, String>> toReportRows(List<DilrmpExpenditureStatusReport> reports) {
        List<Map<String, String>> rows = new ArrayList<>();
        int index = 1;
        for (DilrmpExpenditureStatusReport report : reports) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("serialNo", report.getSerialNo() != null ? report.getSerialNo().toString() : String.valueOf(index));
            row.put("stateName", report.getStateName());
            row.put("releaseByCenter", formatAmount(report.getReleaseByCenter()));
            row.put("cnaOpeningBalance", formatAmount(report.getCnaOpeningBalance()));
            row.put("expenditureApril", formatAmount(report.getExpenditureApril()));
            row.put("expenditureMay", formatAmount(report.getExpenditureMay()));
            row.put("expenditureJune", formatAmount(report.getExpenditureJune()));
            row.put("totalExpenditure", formatAmount(report.getTotalExpenditure()));
            row.put("cnaBalance", formatAmount(report.getCnaBalance()));
            rows.add(row);
            index++;
        }
        return rows;
    }

    private Map<String, String> computeTotals(List<DilrmpExpenditureStatusReport> reports) {
        Map<String, String> totals = emptyTotals();
        if (reports == null || reports.isEmpty()) {
            return totals;
        }

        BigDecimal releaseByCenter = BigDecimal.ZERO;
        BigDecimal cnaOpeningBalance = BigDecimal.ZERO;
        BigDecimal expenditureApril = BigDecimal.ZERO;
        BigDecimal expenditureMay = BigDecimal.ZERO;
        BigDecimal expenditureJune = BigDecimal.ZERO;
        BigDecimal totalExpenditure = BigDecimal.ZERO;
        BigDecimal cnaBalance = BigDecimal.ZERO;

        for (DilrmpExpenditureStatusReport report : reports) {
            releaseByCenter = releaseByCenter.add(safeAmount(report.getReleaseByCenter()));
            cnaOpeningBalance = cnaOpeningBalance.add(safeAmount(report.getCnaOpeningBalance()));
            expenditureApril = expenditureApril.add(safeAmount(report.getExpenditureApril()));
            expenditureMay = expenditureMay.add(safeAmount(report.getExpenditureMay()));
            expenditureJune = expenditureJune.add(safeAmount(report.getExpenditureJune()));
            totalExpenditure = totalExpenditure.add(safeAmount(report.getTotalExpenditure()));
            cnaBalance = cnaBalance.add(safeAmount(report.getCnaBalance()));
        }

        totals.put("releaseByCenter", formatAmount(releaseByCenter));
        totals.put("cnaOpeningBalance", formatAmount(cnaOpeningBalance));
        totals.put("expenditureApril", formatAmount(expenditureApril));
        totals.put("expenditureMay", formatAmount(expenditureMay));
        totals.put("expenditureJune", formatAmount(expenditureJune));
        totals.put("totalExpenditure", formatAmount(totalExpenditure));
        totals.put("cnaBalance", formatAmount(cnaBalance));
        return totals;
    }

    private Map<String, String> emptyTotals() {
        Map<String, String> totals = new LinkedHashMap<>();
        totals.put("releaseByCenter", "0.00");
        totals.put("cnaOpeningBalance", "0.00");
        totals.put("expenditureApril", "0.00");
        totals.put("expenditureMay", "0.00");
        totals.put("expenditureJune", "0.00");
        totals.put("totalExpenditure", "0.00");
        totals.put("cnaBalance", "0.00");
        return totals;
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String formatAmount(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
