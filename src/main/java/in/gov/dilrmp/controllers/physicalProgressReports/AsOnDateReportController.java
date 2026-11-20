package in.gov.dilrmp.controllers.physicalProgressReports;

import in.gov.dilrmp.controllers.pdfExcelPhysicalProgressReports.PhyscialProgressReportControllerPdf;
import in.gov.dilrmp.services.physicalProgressServices.StateReportsAsOnDateService;
import in.gov.dilrmp.services.physicalProgressServices.StateReportsAsOnDateService.Component;
import in.gov.dilrmp.utils.PdfExporter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
@Controller
@RequestMapping("physicalProgressReports/as-on-date")
public class AsOnDateReportController {

    private static final Logger logger = LoggerFactory.getLogger(AsOnDateReportController.class);
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

    /**
     * One ZIP of every component PDF that has data for the selected date.
     */
    @GetMapping("/zip")
    public void downloadAllZip(@RequestParam("asOnDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOnDate,
                               HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        LocalDate today = LocalDate.now();
        if (asOnDate.isAfter(today)) {
            asOnDate = today;
        }
        request.setAttribute(PdfExporter.EXTRA_HEADING_ATTRIBUTE, new String[]{"As on " + asOnDate.format(DISPLAY)});
        request.setAttribute(PdfExporter.FILE_NAME_SUFFIX_ATTRIBUTE, "_As_on_" + asOnDate.format(DISPLAY));

        List<NamedBytes> files = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        for (Component component : Component.values()) {
            LocalDate minDate = stateReportsAsOnDateService.getFirstAvailableDate(component);
            if (minDate != null && asOnDate.isBefore(minDate)) {
                skipped.add(component.getLabel() + ": data starts from " + minDate.format(DISPLAY));
                continue;
            }
            StateReportsAsOnDateService.Snapshot<?> snapshot = stateReportsAsOnDateService.getReportAsOn(component, asOnDate);
            if (snapshot.getSnapshotDate() == null) {
                skipped.add(component.getLabel() + ": no data for " + asOnDate.format(DISPLAY));
                continue;
            }
            try {
                byte[] pdf = renderComponentPdf(component, snapshot, response, session);
                if (pdf.length > 0) {
                    files.add(new NamedBytes(zipEntryName(component, asOnDate), pdf));
                } else {
                    skipped.add(component.getLabel() + ": report file was empty");
                }
            } catch (Exception e) {
                logger.error("Could not build as-on-date PDF for {}", component, e);
                skipped.add(component.getLabel() + ": could not create the report");
            }
        }

        String zipName = "Physical_Progress_As_on_" + asOnDate.format(DISPLAY) + ".zip";
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + "\"");
        try (ZipOutputStream zip = new ZipOutputStream(response.getOutputStream())) {
            for (NamedBytes file : files) {
                zip.putNextEntry(new ZipEntry(file.name));
                zip.write(file.bytes);
                zip.closeEntry();
            }
            if (files.isEmpty() || !skipped.isEmpty()) {
                zip.putNextEntry(new ZipEntry(files.isEmpty() ? "no-data.txt" : "not-included.txt"));
                StringBuilder note = new StringBuilder();
                if (files.isEmpty()) {
                    note.append("No component data is available for ").append(asOnDate.format(DISPLAY)).append(".\n");
                }
                for (String line : skipped) {
                    note.append(line).append('\n');
                }
                zip.write(note.toString().getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
            }
        }
    }

    private byte[] renderComponentPdf(Component component, StateReportsAsOnDateService.Snapshot<?> snapshot,
                                      HttpServletResponse response, HttpSession session) throws IOException {
        Object previousList = session.getAttribute(component.getListSessionKey());
        Object previousTotal = session.getAttribute(component.getGrandTotalSessionKey());
        session.setAttribute(component.getListSessionKey(), snapshot.getStateList());
        session.setAttribute(component.getGrandTotalSessionKey(), snapshot.getGrandTotal());
        CapturingResponse capture = new CapturingResponse(response);
        try {
            switch (component) {
                case CLR: physcialProgressReportControllerPdf.getCLRNEWPdf(capture, session); break;
                case AADHAAR: physcialProgressReportControllerPdf.getAadharpdf(capture, session); break;
                case MAP: physcialProgressReportControllerPdf.getMAPNewPdf(capture, session); break;
                case MRR: physcialProgressReportControllerPdf.getMRRPdf(capture, session); break;
                case SURVEY: physcialProgressReportControllerPdf.getSurveyNewpdf(capture, session); break;
                case RCMS: physcialProgressReportControllerPdf.getRCMSpdf(capture, session); break;
                case SRO: physcialProgressReportControllerPdf.getSROpdf(capture, session); break;
                case LEGACY: physcialProgressReportControllerPdf.getLegacyDigitizationPdf(capture, session); break;
                case SRO_MODERNIZATION: physcialProgressReportControllerPdf.getSroModernizationPdf(capture, session); break;
                case LEGACY_REVENUE: physcialProgressReportControllerPdf.getLegacyRevenuePdf(capture, session); break;
                default: throw new IllegalArgumentException("Unknown component " + component);
            }
            return capture.toByteArray();
        } finally {
            restore(session, component.getListSessionKey(), previousList);
            restore(session, component.getGrandTotalSessionKey(), previousTotal);
        }
    }

    private static String zipEntryName(Component component, LocalDate asOnDate) {
        String base = component.getLabel().replaceAll("[\\\\/:*?\"<>|]", "").replaceAll("\\s+", "_");
        return base + "_As_on_" + asOnDate.format(DISPLAY) + ".pdf";
    }

    private static void restore(HttpSession session, String key, Object previous) {
        if (previous == null) {
            session.removeAttribute(key);
        } else {
            session.setAttribute(key, previous);
        }
    }

    private static final class NamedBytes {
        private final String name;
        private final byte[] bytes;

        private NamedBytes(String name, byte[] bytes) {
            this.name = name;
            this.bytes = bytes;
        }
    }

    /** Keeps PDF bytes in memory so they can be added to a ZIP instead of the browser response. */
    private static final class CapturingResponse extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private ServletOutputStream stream;

        private CapturingResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setHeader(String name, String value) {
        }

        @Override
        public void addHeader(String name, String value) {
        }

        @Override
        public void setContentType(String type) {
        }

        @Override
        public void setContentLength(int len) {
        }

        @Override
        public void setContentLengthLong(long len) {
        }

        @Override
        public void flushBuffer() {
        }

        @Override
        public ServletOutputStream getOutputStream() {
            if (stream == null) {
                stream = new ServletOutputStream() {
                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(WriteListener writeListener) {
                    }

                    @Override
                    public void write(int b) {
                        buffer.write(b);
                    }
                };
            }
            return stream;
        }

        private byte[] toByteArray() {
            return buffer.toByteArray();
        }
    }

}
