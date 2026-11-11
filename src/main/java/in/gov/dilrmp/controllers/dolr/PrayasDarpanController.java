package in.gov.dilrmp.controllers.dolr;

import in.gov.dilrmp.api.prayas.darpan.PrayasDarpanLog;
import in.gov.dilrmp.api.prayas.darpan.PrayasDarpanLogRepository;
import in.gov.dilrmp.api.prayas.darpan.PrayasDarpanProperties;
import in.gov.dilrmp.api.prayas.darpan.PrayasDarpanPushService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DoLR report: PRAYAS / DARPAN push log and manual push.
 */
@Controller
@RequestMapping("/dolr/prayas-darpan")
public class PrayasDarpanController {

    private final PrayasDarpanPushService pushService;
    private final PrayasDarpanLogRepository logRepository;
    private final PrayasDarpanProperties properties;

    public PrayasDarpanController(PrayasDarpanPushService pushService,
                                  PrayasDarpanLogRepository logRepository,
                                  PrayasDarpanProperties properties) {
        this.pushService = pushService;
        this.logRepository = logRepository;
        this.properties = properties;
    }

    @GetMapping
    public String reportPage(Model model) {
        addReportModel(model);
        return "pages/dolr/prayas-darpan-log :: prayas-darpan-log";
    }

    @PostMapping("/push")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> pushNow() {
        Map<String, Object> result = pushService.pushNow("manual");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/logs")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> recentLogs() {
        List<PrayasDarpanLog> logs = logRepository.findTop200ByOrderByIdDesc();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "success");
        body.put("count", logs.size());
        body.put("logs", logs);
        return ResponseEntity.ok(body);
    }

    private void addReportModel(Model model) {
        model.addAttribute("logs", logRepository.findTop200ByOrderByIdDesc());
        model.addAttribute("pushEnabled", properties.isEnabled());
        model.addAttribute("projectCode", properties.getProjectCode());
        model.addAttribute("frequencyId", properties.getFrequencyId());
    }
}
