package in.gov.dilrmp.controllers.site;

import in.gov.dilrmp.models.reportDTO.legacy.LegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.LegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.sroModernization.SroModernizationReport;
import in.gov.dilrmp.services.physicalProgressServices.LegacyDigitizationReportService;
import in.gov.dilrmp.services.physicalProgressServices.LegacyRevenueReportService;
import in.gov.dilrmp.services.physicalProgressServices.SroModernizationReportService;
import in.gov.dilrmp.services.site.HomeInsightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/site")
public class SiteInfoController {

    @Autowired
    private SroModernizationReportService sroModernizationReportService;

    @Autowired
    private LegacyDigitizationReportService legacyDigitizationReportService;

    @Autowired
    private LegacyRevenueReportService legacyRevenueReportService;

    @Autowired
    private HomeInsightsService homeInsightsService;

    @GetMapping("/website-policies")
    public String websitePolicies() {
        return "pages/site/website-policies";
    }

    @GetMapping("/help")
    public String help() {
        return "pages/site/help";
    }

    @GetMapping("/sitemap")
    public String sitemap() {
        return "pages/site/sitemap";
    }

    @GetMapping("/feedback")
    public String feedback() {
        return "pages/site/feedback";
    }

    @GetMapping("/home-preview")
    public String homePreview() {
        return "pages/site/home-preview";
    }

    @GetMapping("/home-preview/component-totals")
    public ResponseEntity<Map<String, Object>> componentTotals() {
        Map<String, Object> totals = new HashMap<>();

        List<SroModernizationReport> sro = sroModernizationReportService.getGrandTotalFormatted();
        if (!sro.isEmpty()) {
            SroModernizationReport s = sro.get(0);
            totals.put("srosModernisedTotal", s.getSrosModernisedTotal());
            totals.put("srosModernisedTotalPercent", s.getSrosModernisedTotalPercent());
            totals.put("srosUsingOnlineRegistration", s.getSrosUsingOnlineRegistration());
        }

        List<LegacyDigitizationReport> legacy = legacyDigitizationReportService.getGrandTotalFormatted();
        if (!legacy.isEmpty()) {
            LegacyDigitizationReport l = legacy.get(0);
            totals.put("legacyTotalDigitisedPages", l.getLegacyTotalDigitisedPages());
            totals.put("legacyTotalDigitisedPercent", l.getLegacyTotalDigitisedPercent());
            totals.put("legacyTotalPages", l.getLegacyTotalPages());
        }

        List<LegacyRevenueReport> revenue = legacyRevenueReportService.getGrandTotalFormatted();
        if (!revenue.isEmpty()) {
            LegacyRevenueReport r = revenue.get(0);
            totals.put("revenueTotalDigitisedPages", r.getTotalDigitisedPages());
            totals.put("revenueTotalDigitisedPercent", r.getTotalDigitisedPercent());
            totals.put("revenueTotalPages", r.getTotalPages());
        }

        return ResponseEntity.ok(totals);
    }

    @GetMapping("/home-preview/insights")
    public ResponseEntity<Map<String, Object>> homeInsights() {
        return ResponseEntity.ok(homeInsightsService.insights());
    }

    @GetMapping("/home-preview/state/{stateId}")
    public ResponseEntity<Map<String, Object>> stateDetail(@PathVariable("stateId") long stateId) {
        Map<String, Object> detail = homeInsightsService.stateDetail(stateId);
        return detail == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(detail);
    }

    @GetMapping("/home-preview/state/{stateId}/districts")
    public ResponseEntity<List<Map<String, Object>>> stateDistricts(@PathVariable("stateId") long stateId,
                                                                    @RequestParam("component") String component) {
        if (!HomeInsightsService.isMapComponent(component)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(homeInsightsService.districtPercents(stateId, component));
    }

    @GetMapping("/home-preview/district/{districtLgd}")
    public ResponseEntity<Map<String, Object>> districtDetail(@PathVariable("districtLgd") long districtLgd) {
        Map<String, Object> detail = homeInsightsService.districtDetail(districtLgd);
        return detail == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(detail);
    }

    @GetMapping("/home-preview/state-progress.csv")
    public ResponseEntity<byte[]> stateProgressCsv() {
        StringBuilder csv = new StringBuilder("\uFEFF");
        for (String[] row : homeInsightsService.stateProgressRows()) {
            for (int i = 0; i < row.length; i++) {
                if (i > 0) {
                    csv.append(',');
                }
                csv.append('"').append(row[i] == null ? "" : row[i].replace("\"", "\"\"")).append('"');
            }
            csv.append("\r\n");
        }
        String fileName = "dilrmp-state-progress-" + LocalDate.now() + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
}
