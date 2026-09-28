package in.gov.dilrmp.controllers.unspentBalance;

import in.gov.dilrmp.models.administrativeBoundry.AdministrativeBoundry;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.services.unspentBalance.UnspentBalanceService;
import in.gov.dilrmp.services.user.UserService;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import in.gov.dilrmp.utils.UnspentBalanceReportExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/state")
public class StateUnspentBalanceController {

    private static final Logger logger = LoggerFactory.getLogger(StateUnspentBalanceController.class);

    @Autowired
    private UnspentBalanceService unspentBalanceService;

    @Autowired
    private LoogedInUserUtility loogedInUserUtility;

    @GetMapping("/unspent-balance/form")
    public String showForm(Model model) {
        try {
            State state = loggedInState();
            model.addAttribute("stateName", state.getName());
            model.addAttribute("reportTitle", UnspentBalanceService.REPORT_TITLE);
            model.addAttribute("letterReference", UnspentBalanceService.LETTER_REFERENCE);
            model.addAttribute("componentNote", UnspentBalanceService.COMPONENT_NOTE);
            model.addAttribute("lastDate", unspentBalanceService.getLastDateText());
            model.addAttribute("entryOpen", unspentBalanceService.isEntryOpen());
            model.addAttribute("amountHeader", unspentBalanceService.getAmountHeader());
            model.addAttribute("rows", unspentBalanceService.getRows(state.getId()));
            model.addAttribute("lastUpdatedOn", UnspentBalanceReportExporter.lastUpdatedText(
                    unspentBalanceService.getLastUpdatedOn(state.getId())));
            return "pages/unspentBalance/state_unspent_balance_form :: unspent-balance-form";
        } catch (Exception e) {
            logger.error("Error loading unspent balance form", e);
            return "pages/error/error-page::error-page";
        }
    }

    @PostMapping("/unspent-balance/form/save")
    @ResponseBody
    public ResponseEntity<Map<String, String>> save(@RequestBody Map<String, Object> payload) {
        Map<String, String> response = new LinkedHashMap<>();
        try {
            State state = loggedInState();

            Map<String, String> amounts = new HashMap<>();
            Object rawAmounts = payload.get("amounts");
            if (rawAmounts instanceof Map) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) rawAmounts).entrySet()) {
                    amounts.put(String.valueOf(entry.getKey()), entry.getValue() != null ? entry.getValue().toString() : null);
                }
            }

            UserService user = loogedInUserUtility.getLoggedinUser();
            unspentBalanceService.save(state.getId(), amounts, user != null ? user.getUsername() : null);
            response.put("status", "success");
            response.put("message", "Unspent balance as on " + unspentBalanceService.getAsOnDateText() + " saved successfully.");
            response.put("lastUpdatedOn", UnspentBalanceReportExporter.lastUpdatedText(
                    unspentBalanceService.getLastUpdatedOn(state.getId())));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalStateException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            logger.error("Error saving unspent balance", e);
            response.put("status", "error");
            response.put("message", "Failed to save unspent balance. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/unspent-balance/pdf")
    public void downloadPdf(HttpServletResponse response) throws IOException {
        try {
            State state = loggedInState();
            UnspentBalanceReportExporter.writePdf(response, state.getName(), unspentBalanceService.getAmountHeader(),
                    unspentBalanceService.getLastUpdatedOn(state.getId()), unspentBalanceService.getRows(state.getId()));
        } catch (IllegalStateException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/unspent-balance/excel")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        try {
            State state = loggedInState();
            UnspentBalanceReportExporter.writeExcel(response, state.getName(), unspentBalanceService.getAmountHeader(),
                    unspentBalanceService.getLastUpdatedOn(state.getId()), unspentBalanceService.getRows(state.getId()));
        } catch (IllegalStateException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private State loggedInState() {
        UserService user = loogedInUserUtility.getLoggedinUser();
        AdministrativeBoundry boundary = user != null ? user.getBoundry() : null;
        if (!(boundary instanceof State)) {
            throw new IllegalStateException("Unspent balance can be entered only from State login.");
        }
        return (State) boundary;
    }
}
