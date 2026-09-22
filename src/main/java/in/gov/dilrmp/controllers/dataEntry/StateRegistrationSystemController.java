package in.gov.dilrmp.controllers.dataEntry;



import in.gov.dilrmp.models.dataEntryModel.StateRegistrationSystem;
import in.gov.dilrmp.services.DataEntryForm.IgrMISDataEntryService;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("igr")
public class StateRegistrationSystemController {

    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    IgrMISDataEntryService igrMISDataEntryService;


    @GetMapping("/igr-mis-data/form")
    public String showIGRForm(Model model) {
        Long stateId = null;
        Long stateID = null;

        try {
            if (loogedInUserUtility.getLoggedinUser().getBoundry() != null) {
                stateId = loogedInUserUtility.getLoggedinUser().getBoundry().getId();
            }

            if (loogedInUserUtility.getLoggedinUser().getIgr() != null) {
                stateID = loogedInUserUtility.getLoggedinUser().getIgr().getState().getId();
            }
            Long effectiveStateId = stateID != null ? stateID : stateId;

            if (effectiveStateId == null) {
                throw new IllegalStateException("No state ID found for the logged-in user.");
            }
            StateRegistrationSystem igrMISDataEntry = igrMISDataEntryService.getStateRegistrationSystemByStateId(effectiveStateId);
            if (igrMISDataEntry == null) {
                igrMISDataEntry = new StateRegistrationSystem();
            }
            model.addAttribute("igrMISDataEntry", igrMISDataEntry);

            return "pages/igr/igr_mis_data_entry_form::igr-mis-data";

        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error occurred while fetching selectable ranking report data", e);

            return "pages/error/error-page::error-page";
        }
    }


    @PostMapping("/igr-mis-data/save")
    public ResponseEntity<String> saveIGRMISData(@ModelAttribute StateRegistrationSystem stateRegistrationSystem,
                                                      RedirectAttributes redirectAttributes) {
        try {

            String message= igrMISDataEntryService.save(stateRegistrationSystem);

            if ("success".equals(message)) {
                return ResponseEntity.ok(" IGR MIS Data Saved successfully!.");
            } else {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong. Please try again.");
            }
        } catch (Exception e) {

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong. Please try again or contact technical support for MIS.");
        }
    }

}
