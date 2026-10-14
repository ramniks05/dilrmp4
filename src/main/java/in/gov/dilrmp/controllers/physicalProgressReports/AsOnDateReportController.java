package in.gov.dilrmp.controllers.physicalProgressReports;

import in.gov.dilrmp.controllers.pdfExcelPhysicalProgressReports.PhyscialProgressReportControllerPdf;
import in.gov.dilrmp.services.physicalProgressServices.StateReportsAsOnDateService;
import in.gov.dilrmp.services.physicalProgressServices.StateReportsAsOnDateService.Component;
import in.gov.dilrmp.utils.PdfExporter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.Collections;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Controller
@RequestMapping("physicalProgressReports/as-on-date")
public class AsOnDateReportController {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    StateReportsAsOnDateService stateReportsAsOnDateService;
    @Autowired
    PhyscialProgressReportControllerPdf physcialProgressReportControllerPdf;

    @GetMapping
    public String downloadScreen(@RequestParam(name = "asOnDate", required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOnDate,
                                 @RequestParam(name = "component", required = false) Component component,
                                 @RequestParam(name = "error", required = false) String error,
                                 Model model) {
        if (component == null) {
            component = Component.CLR;
        }
        LocalDate today = LocalDate.now();
        LocalDate minDate = stateReportsAsOnDateService.getFirstAvailableDate(component);
        if (asOnDate == null || asOnDate.isAfter(today)) {
            asOnDate = today;
        } else if (minDate != null && asOnDate.isBefore(minDate)) {
            asOnDate = minDate;
        }
        fillReport(model, asOnDate, component);
        model.addAttribute("components", Component.values());
        model.addAttribute("selectedComponent", component);
        model.addAttribute("asOnDate", asOnDate);
        model.addAttribute("minDate", minDate);
        model.addAttribute("maxDate", today);
        model.addAttribute("componentMinDates", stateReportsAsOnDateService.firstAvailableDates());
        model.addAttribute("error", error);
        return "pages/misReports/asOnDateReportDownload";
    }

    /** Shared by the DoLR menu fragment and the full page. */
    public void fillReport(Model model, LocalDate asOnDate, Component component) {
        StateReportsAsOnDateService.Snapshot<?> snapshot = stateReportsAsOnDateService.getReportAsOn(component, asOnDate);
        model.addAttribute("labels", stateReportsAsOnDateService.labelsFor(component));
        model.addAttribute(component.getListSessionKey(), snapshot.getStateList());
        model.addAttribute("grandTotal", snapshot.getGrandTotal() != null ? snapshot.getGrandTotal() : Collections.emptyList());
        model.addAttribute("liveData", snapshot.isLive());
        model.addAttribute("snapshotDate", snapshot.getSnapshotDate());
        model.addAttribute("approximateStates", snapshot.getApproximateStates() != null
                ? snapshot.getApproximateStates() : Collections.emptyList());
        model.addAttribute("reportReady", snapshot.getSnapshotDate() != null);
        model.addAttribute("reportFragment", reportFragment(component));
        model.addAttribute("hideStateLinks", true);
    }

    private static String reportFragment(Component component) {
        switch (component) {
            case CLR: return "pages/misReports/state_CLR_ProgressReportAsOnDate";
            case AADHAAR: return "pages/physicalprogress/state_AadharLinkageReport";
            case MAP: return "pages/physicalprogress/state_MapDigitizationReport";
            case MRR: return "pages/misReports/state_MRR_ProgressReportNew";
            case SURVEY: return "pages/misReports/state_Survey_ProgresReportNew";
            case RCMS: return "pages/physicalprogress/state_RCMS_Report";
            case SRO: return "pages/physicalprogress/state_SRO_ProgressReport";
            case LEGACY: return "pages/physicalprogress/state_LegacyDigitizationReport";
            case SRO_MODERNIZATION: return "pages/physicalprogress/state_SroModernizationReport";
            case LEGACY_REVENUE: return "pages/physicalprogress/state_LegacyRevenueReport";
            default: throw new IllegalArgumentException("Unknown component " + component);
        }
    }

    @GetMapping("/pdf")
    public void downloadPdf(@RequestParam("asOnDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOnDate,
                            @RequestParam("component") Component component,
                            HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        LocalDate today = LocalDate.now();
        LocalDate minDate = stateReportsAsOnDateService.getFirstAvailableDate(component);
        if (asOnDate.isAfter(today)) {
            asOnDate = today;
        } else if (minDate != null && asOnDate.isBefore(minDate)) {
            asOnDate = minDate;
        }
        StateReportsAsOnDateService.Snapshot<?> snapshot = stateReportsAsOnDateService.getReportAsOn(component, asOnDate);
        if (snapshot.getSnapshotDate() == null) {
            String error = "No data available for " + asOnDate.format(DISPLAY) + ".";
            response.sendRedirect(request.getContextPath() + "/physicalProgressReports/as-on-date?asOnDate=" + asOnDate
                    + "&component=" + component.name() + "&error=" + URLEncoder.encode(error, StandardCharsets.UTF_8));
            return;
        }

        request.setAttribute(PdfExporter.EXTRA_HEADING_ATTRIBUTE, new String[]{"As on " + asOnDate.format(DISPLAY)});
        request.setAttribute(PdfExporter.FILE_NAME_SUFFIX_ATTRIBUTE, "_As_on_" + asOnDate.format(DISPLAY));

        // The PDF exports read their rows from the session; swap in the as-on-date rows for this call only.
        Object previousList = session.getAttribute(component.getListSessionKey());
        Object previousTotal = session.getAttribute(component.getGrandTotalSessionKey());
        session.setAttribute(component.getListSessionKey(), snapshot.getStateList());
        session.setAttribute(component.getGrandTotalSessionKey(), snapshot.getGrandTotal());
        try {
            switch (component) {
                case CLR: physcialProgressReportControllerPdf.getCLRNEWPdf(response, session); break;
                case AADHAAR: physcialProgressReportControllerPdf.getAadharpdf(response, session); break;
                case MAP: physcialProgressReportControllerPdf.getMAPNewPdf(response, session); break;
                case MRR: physcialProgressReportControllerPdf.getMRRPdf(response, session); break;
                case SURVEY: physcialProgressReportControllerPdf.getSurveyNewpdf(response, session); break;
                case RCMS: physcialProgressReportControllerPdf.getRCMSpdf(response, session); break;
                case SRO: physcialProgressReportControllerPdf.getSROpdf(response, session); break;
                case LEGACY: physcialProgressReportControllerPdf.getLegacyDigitizationPdf(response, session); break;
                case SRO_MODERNIZATION: physcialProgressReportControllerPdf.getSroModernizationPdf(response, session); break;
                case LEGACY_REVENUE: physcialProgressReportControllerPdf.getLegacyRevenuePdf(response, session); break;
                default: throw new IllegalArgumentException("Unknown component " + component);
            }
        } finally {
            restore(session, component.getListSessionKey(), previousList);
            restore(session, component.getGrandTotalSessionKey(), previousTotal);
        }
    }

    private static void restore(HttpSession session, String key, Object previous) {
        if (previous == null) {
            session.removeAttribute(key);
        } else {
            session.setAttribute(key, previous);
        }
    }

}
