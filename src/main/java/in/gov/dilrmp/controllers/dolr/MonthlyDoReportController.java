package in.gov.dilrmp.controllers.dolr;

import in.gov.dilrmp.services.dolr.MonthlyDoReportService;
import in.gov.dilrmp.services.dolr.MonthlyDoReportService.Report;
import in.gov.dilrmp.utils.MonthlyDoReportExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.YearMonth;

@Controller
@RequestMapping("/dolr")
public class MonthlyDoReportController {

    private static final Logger logger = LoggerFactory.getLogger(MonthlyDoReportController.class);

    @Autowired
    private MonthlyDoReportService monthlyDoReportService;

    @GetMapping("/monthly-do-report")
    public String showReport(@RequestParam(name = "month", required = false)
                             @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
                             Model model) {
        Report report = monthlyDoReportService.build(month);
        model.addAttribute("report", report);
        model.addAttribute("selectedMonth", report.month.toString());
        model.addAttribute("minMonth", report.minMonth == null ? "" : report.minMonth.toString());
        model.addAttribute("maxMonth", report.maxMonth == null ? "" : report.maxMonth.toString());
        return "pages/dolr/monthly-do-report :: monthly-do-report";
    }

    @GetMapping("/monthly-do-report/pdf")
    public void downloadPdf(@RequestParam(name = "month", required = false)
                            @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
                            HttpServletResponse response) throws IOException {
        try {
            MonthlyDoReportExporter.writePdf(response, monthlyDoReportService.build(month));
        } catch (IOException e) {
            logger.error("Monthly DO Report PDF failed for month {}", month, e);
            throw e;
        }
    }

    @GetMapping("/monthly-do-report/excel")
    public void downloadExcel(@RequestParam(name = "month", required = false)
                              @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
                              HttpServletResponse response) throws IOException {
        try {
            MonthlyDoReportExporter.writeExcel(response, monthlyDoReportService.build(month));
        } catch (IOException e) {
            logger.error("Monthly DO Report Excel failed for month {}", month, e);
            throw e;
        }
    }
}
