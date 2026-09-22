package in.gov.dilrmp.controllers.cna;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.cna.ComponentRowInput;
import in.gov.dilrmp.services.cna.DilrmpComponentExpenditureStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dolr")
public class DilrmpComponentExpenditureStatusController {

    private static final Logger logger = LoggerFactory.getLogger(DilrmpComponentExpenditureStatusController.class);

    @Autowired
    private DilrmpComponentExpenditureStatusService componentExpenditureService;

    @GetMapping("/dilrmp-expenditure-status/form")
    public String showForm(Model model) {
        Long stateId = resolveDefaultStateId();
        model.addAttribute("dilrmpPhases", DilrmpComponentExpenditureStatusService.DILRMP_PHASES);
        model.addAttribute("financialYears", DilrmpComponentExpenditureStatusService.FINANCIAL_YEARS);
        model.addAttribute("states", componentExpenditureService.getStates());
        model.addAttribute("selectedPhase", DilrmpComponentExpenditureStatusService.DEFAULT_PHASE);
        model.addAttribute("selectedStateId", stateId);
        model.addAttribute("selectedFinancialYear", DilrmpComponentExpenditureStatusService.DEFAULT_FINANCIAL_YEAR);
        if (stateId != null) {
            model.addAttribute("componentRows", componentExpenditureService.getComponentRows(
                    DilrmpComponentExpenditureStatusService.DEFAULT_PHASE,
                    stateId,
                    DilrmpComponentExpenditureStatusService.DEFAULT_FINANCIAL_YEAR));
        } else {
            model.addAttribute("componentRows", List.of());
        }
        return "pages/cna/dilrmpComponentExpenditureStatus :: dilrmp-component-expenditure";
    }

    @PostMapping("/dilrmp-expenditure-status/form/load")
    public String loadFormData(@RequestParam("dilrmpPhase") String dilrmpPhase,
                               @RequestParam("stateId") Long stateId,
                               @RequestParam("financialYear") String financialYear,
                               Model model) {
        populateFormModel(model, dilrmpPhase, stateId, financialYear);
        return "pages/cna/dilrmpComponentExpenditureStatus :: component-expenditure-table";
    }

    @PostMapping("/dilrmp-expenditure-status/form/save")
    @ResponseBody
    public ResponseEntity<Map<String, String>> saveFormData(@RequestBody Map<String, Object> payload) {
        Map<String, String> response = new LinkedHashMap<>();
        try {
            String dilrmpPhase = (String) payload.get("dilrmpPhase");
            String financialYear = (String) payload.get("financialYear");
            Long stateId = Long.valueOf(payload.get("stateId").toString());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) payload.get("rows");
            List<ComponentRowInput> inputs = rows.stream()
                    .map(row -> new ComponentRowInput(
                            row.get("serialNo") != null ? Integer.valueOf(row.get("serialNo").toString()) : null,
                            stringValue(row.get("sanctionIssued")),
                            stringValue(row.get("sanctionCancelled")),
                            stringValue(row.get("fundReleased")),
                            stringValue(row.get("fundReleasedDate")),
                            stringValue(row.get("fundBalance"))
                    ))
                    .collect(Collectors.toList());

            componentExpenditureService.saveComponentRows(dilrmpPhase, stateId, financialYear, inputs);
            response.put("status", "success");
            response.put("message", "DILRMP Expenditure Status saved successfully.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error saving DILRMP component expenditure status", e);
            response.put("status", "error");
            response.put("message", "Failed to save expenditure status. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private void populateFormModel(Model model, String dilrmpPhase, Long stateId, String financialYear) {
        model.addAttribute("dilrmpPhases", DilrmpComponentExpenditureStatusService.DILRMP_PHASES);
        model.addAttribute("financialYears", DilrmpComponentExpenditureStatusService.FINANCIAL_YEARS);
        model.addAttribute("states", componentExpenditureService.getStates());
        model.addAttribute("selectedPhase", dilrmpPhase);
        model.addAttribute("selectedStateId", stateId);
        model.addAttribute("selectedFinancialYear", financialYear);
        model.addAttribute("componentRows", componentExpenditureService.getComponentRows(dilrmpPhase, stateId, financialYear));
    }

    private Long resolveDefaultStateId() {
        Optional<State> defaultState = componentExpenditureService.findDefaultState();
        if (defaultState.isPresent()) {
            return defaultState.get().getId();
        }
        List<State> states = componentExpenditureService.getStates();
        return states.isEmpty() ? null : states.get(0).getId();
    }

    private String stringValue(Object value) {
        return value != null ? value.toString() : null;
    }
}
