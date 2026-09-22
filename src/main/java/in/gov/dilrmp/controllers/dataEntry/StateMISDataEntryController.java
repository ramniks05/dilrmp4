package in.gov.dilrmp.controllers.dataEntry;

import in.gov.dilrmp.models.dataEntryModel.StateMISDataEntry;
import in.gov.dilrmp.repositories.administrativeBoundry.StateRepositry;
import in.gov.dilrmp.services.DataEntryForm.StateMISDataEntryService;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("state")
public class StateMISDataEntryController {

    @Autowired
    private StateMISDataEntryService stateMISDataEntryService;
    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    StateRepositry stateRepositry;

    @GetMapping("/state-mis-data/form")
    public String showStateForm(Model model) {
        Long stateId = null;

        try {
            stateId = loogedInUserUtility.getLoggedinUser().getBoundry().getId();
            StateMISDataEntry stateMISDataEntry = stateMISDataEntryService.getStateMISDataEntryByStateId(stateId);
            if (stateMISDataEntry == null) {
                stateMISDataEntry = new StateMISDataEntry();
            }
            model.addAttribute("stateMISDataEntry", stateMISDataEntry);

            return "pages/state/state_mis_data_entry_form::state-mis-data";

        }catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error occurred while fetching selectable ranking report data", e);

            return "pages/error/error-page::error-page";
        }
    }

    @PostMapping("/state-mis-data/save")
    public ResponseEntity<String> saveStateMISData(@ModelAttribute("stateMISDataEntry") StateMISDataEntry stateMISDataEntry) {
        try {

            String message = stateMISDataEntryService.save(stateMISDataEntry);

            if ("success".equals(message)) {
                return ResponseEntity.ok("State MIS Data Saved successfully!.");
            } else {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong. Please try again.");
            }
        } catch (Exception e) {

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong. Please try again or contact technical support for MIS.");
        }
    }
}
