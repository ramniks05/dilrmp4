package in.gov.dilrmp.controllers.unspentBalance;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.services.unspentBalance.UnspentBalanceService;
import in.gov.dilrmp.utils.UnspentBalanceReportExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/dolr")
public class DolrUnspentBalanceReportController {

    private static final Logger logger = LoggerFactory.getLogger(DolrUnspentBalanceReportController.class);

    @Autowired
    private UnspentBalanceService unspentBalanceService;

    @GetMapping("/unspent-balance/report")
    public String showReportPage(Model model) {
        model.addAttribute("states", unspentBalanceService.getStates());
        return "pages/unspentBalance/dolr_unspent_balance_report :: unspent-balance-report";
    }

    @PostMapping("/unspent-balance/report/load")
    public String loadReport(@RequestParam(value = "stateId", required = false) Long stateId, Model model) {
        try {
            State state = unspentBalanceService.resolveState(stateId);

            model.addAttribute("stateId", state.getId());
            model.addAttribute("stateName", state.getName());
            model.addAttribute("reportTitle", UnspentBalanceService.REPORT_TITLE);
            model.addAttribute("componentNote", UnspentBalanceService.COMPONENT_NOTE);
            model.addAttribute("amountHeader", unspentBalanceService.getAmountHeader());
            model.addAttribute("submitted", unspentBalanceService.getLastUpdatedOn(state.getId()) != null);
            model.addAttribute("rows", unspentBalanceService.getRows(state.getId()));
        } catch (IllegalArgumentException e) {
            model.addAttribute("reportError", e.getMessage());
        }
        return "pages/unspentBalance/dolr_unspent_balance_report :: unspent-balance-report-table";
    }

    @GetMapping("/unspent-balance/report/pdf")
    public void downloadPdf(@RequestParam("stateId") Long stateId, HttpServletResponse response) throws IOException {
        try {
            State state = unspentBalanceService.resolveState(stateId);
            UnspentBalanceReportExporter.writePdf(response, state.getName(),
                    unspentBalanceService.getAmountHeader(), unspentBalanceService.getRows(state.getId()));
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Error generating unspent balance PDF", e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to generate PDF.");
            }
        }
    }

    @GetMapping("/unspent-balance/report/all-states/excel")
    public void downloadAllStatesExcel(HttpServletResponse response) throws IOException {
        try {
            UnspentBalanceReportExporter.writeAllStatesExcel(response, unspentBalanceService.getAmountHeader(),
                    unspentBalanceService.getRowsForAllStates());
        } catch (Exception e) {
            logger.error("Error generating all-states unspent balance Excel", e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to generate Excel.");
            }
        }
    }

    @GetMapping("/unspent-balance/report/excel")
    public void downloadExcel(@RequestParam("stateId") Long stateId, HttpServletResponse response) throws IOException {
        try {
            State state = unspentBalanceService.resolveState(stateId);
            UnspentBalanceReportExporter.writeExcel(response, state.getName(),
                    unspentBalanceService.getAmountHeader(), unspentBalanceService.getRows(state.getId()));
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Error generating unspent balance Excel", e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to generate Excel.");
            }
        }
    }
}
