package in.gov.dilrmp.controllers.consolidated;

import in.gov.dilrmp.models.profile.StateProfileWordEntry;
import in.gov.dilrmp.models.reportDTO.profile.StateProfileWordInput;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import in.gov.dilrmp.services.physicalProgressServices.StateProfileWordEntryService;
import in.gov.dilrmp.services.physicalProgressServices.StateProfileWordService;
import in.gov.dilrmp.services.physicalProgressServices.StateReportsAsOnDateService;
import in.gov.dilrmp.services.physicalProgressServices.StateReportsAsOnDateService.Component;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("dolr/state-profile-word")
public class StateProfileWordController {

    private static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String PAGE = "pages/consolidatedReports/state-profile-word";

    @Autowired
    StateService stateService;
    @Autowired
    StateProfileWordService stateProfileWordService;
    @Autowired
    StateProfileWordEntryService stateProfileWordEntryService;
    @Autowired
    StateReportsAsOnDateService stateReportsAsOnDateService;

    @GetMapping
    public String form(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("asOnDate", today);
        model.addAttribute("stateList", stateService.findAllOrderByStateName());
        model.addAttribute("minDate", stateReportsAsOnDateService.getFirstAvailableDate(Component.CLR));
        model.addAttribute("maxDate", today);
        return PAGE + "::state-profile-word";
    }

    @PostMapping("/load")
    public String load(@RequestParam(value = "stateId", required = false) Long stateId,
                       @RequestParam(value = "asOnDate", required = false) String asOnDate, Model model) {
        if (stateId == null || stateId <= 0) {
            model.addAttribute("error", "Please select a State/UT.");
            return PAGE + "::profileBody";
        }
        LocalDate date = clampDate(parse(asOnDate));
        Optional<StateProfileWordEntry> saved = stateProfileWordEntryService.find(stateId, date);
        StateProfileWordEntry source = saved.orElse(null);
        if (source == null) {
            source = stateProfileWordEntryService.latest(stateId).orElse(null);
            if (source != null) {
                model.addAttribute("prefilledFrom", source.getAsOnDate());
            }
        }
        fillBody(model, stateProfileWordEntryService.toInput(source, stateId, date), saved.orElse(null));
        return PAGE + "::profileBody";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute StateProfileWordInput input, @RequestParam("action") String action,
                       Principal principal, Model model) {
        if (input.getStateId() == null || input.getStateId() <= 0) {
            model.addAttribute("error", "Please select a State/UT.");
            return PAGE + "::profileBody";
        }
        input.setAsOnDate(clampDate(input.getAsOnDate()));
        boolean finalSave = "final".equalsIgnoreCase(action);
        StateProfileWordEntry entry;
        try {
            entry = stateProfileWordEntryService.save(input, finalSave, principal == null ? null : principal.getName());
            model.addAttribute("message", finalSave
                    ? "Final saved. The profile is now locked; download the Word file to edit further."
                    : "Saved as draft.");
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            entry = stateProfileWordEntryService.find(input.getStateId(), input.getAsOnDate()).orElse(null);
            input = stateProfileWordEntryService.toInput(entry, input.getStateId(), input.getAsOnDate());
        }
        fillBody(model, input, entry);
        return PAGE + "::profileBody";
    }

    @PostMapping("/download")
    public void download(@ModelAttribute StateProfileWordInput input, HttpServletResponse response) throws IOException {
        if (input.getStateId() == null || input.getStateId() <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Select a State/UT.");
            return;
        }
        input.setAsOnDate(clampDate(input.getAsOnDate()));
        Optional<StateProfileWordEntry> saved = stateProfileWordEntryService.find(input.getStateId(), input.getAsOnDate());
        if (saved.isPresent() && StateProfileWordEntry.FINAL.equals(saved.get().getStatus())) {
            input = stateProfileWordEntryService.toInput(saved.get(), input.getStateId(), input.getAsOnDate());
        }

        byte[] file = stateProfileWordService.build(input);
        String fileName = "State_Profile_" + stateProfileWordService.stateName(input.getStateId()).trim().replaceAll("[^A-Za-z0-9]+", "_")
                + "_" + input.getAsOnDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".docx";
        response.setContentType(DOCX);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''"
                + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        response.setContentLength(file.length);
        response.getOutputStream().write(file);
        response.flushBuffer();
    }

    private void fillBody(Model model, StateProfileWordInput input, StateProfileWordEntry entry) {
        model.addAttribute("input", input);
        model.addAttribute("entry", entry);
        model.addAttribute("locked", entry != null && StateProfileWordEntry.FINAL.equals(entry.getStatus()));
        StateProfileWordService.MisData mis = stateProfileWordService.misData(input.getStateId(), input.getAsOnDate());
        Map<String, String[]> entered = input.ulbDetailsByName();
        List<String[]> ulbRows = new ArrayList<>();
        for (String[] ulb : mis.getUlbs()) {
            String[] d = entered.getOrDefault(ulb[0], new String[]{"", "", ""});
            ulbRows.add(new String[]{ulb[0], ulb[1], d[0], d[1], d[2]});
        }
        model.addAttribute("mis", mis);
        model.addAttribute("ulbRows", ulbRows);
    }

    private LocalDate clampDate(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate minDate = stateReportsAsOnDateService.getFirstAvailableDate(Component.CLR);
        if (date == null || date.isAfter(today)) {
            return today;
        }
        return minDate != null && date.isBefore(minDate) ? minDate : date;
    }

    private static LocalDate parse(String iso) {
        try {
            return iso == null || iso.isBlank() ? null : LocalDate.parse(iso.trim());
        } catch (RuntimeException e) {
            return null;
        }
    }
}
