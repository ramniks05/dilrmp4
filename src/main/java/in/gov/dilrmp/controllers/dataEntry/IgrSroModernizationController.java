package in.gov.dilrmp.controllers.dataEntry;

import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.dataEntryModel.DistrictMISDataEntry;
import in.gov.dilrmp.services.DataEntryForm.DistrictMISDataEntryService;
import in.gov.dilrmp.services.administrativeBoundry.DistrictService;
import in.gov.dilrmp.services.user.UserService;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/** IGR enters SRO Modernization district-wise for its own State; the District MIS form shows these values read-only. */
@Controller
@RequestMapping("igr/sro-modernization")
public class IgrSroModernizationController {

    private static final String PAGE = "pages/igr/igr_sro_modernization_form";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    DistrictService districtService;
    @Autowired
    DistrictMISDataEntryService districtMISDataEntryService;

    @GetMapping
    public String form(Model model) {
        Long stateId = igrStateId();
        if (stateId == null) {
            return "pages/error/error-page::error-page";
        }
        model.addAttribute("districts", districtService.findAllDistrictByStateID(stateId));
        return PAGE + "::igr-sro-modernization";
    }

    @PostMapping("/load")
    public String load(@RequestParam(value = "districtId", required = false) Long districtId, Model model) {
        Long stateId = igrStateId();
        District district = districtId == null ? null : districtService.findById(districtId).orElse(null);
        if (stateId == null || district == null || district.getState() == null || !stateId.equals(district.getState().getId())) {
            model.addAttribute("error", "Please select a district.");
            return PAGE + "::sroFields";
        }
        DistrictMISDataEntry entry = districtMISDataEntryService.getDistrictData(districtId);
        model.addAttribute("district", district);
        model.addAttribute("entry", entry != null ? entry : new DistrictMISDataEntry());
        return PAGE + "::sroFields";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<String> save(@RequestParam("districtId") Long districtId,
                                       @RequestParam(value = "totalSRO", required = false) Integer totalSro,
                                       @RequestParam(value = "sroComputerized", required = false) Integer computerized,
                                       @RequestParam(value = "srosModernisedStateFunds", required = false) Integer stateFunds,
                                       @RequestParam(value = "srosModernisedDilrmpFunds", required = false) Integer dilrmpFunds) {
        try {
            Long stateId = igrStateId();
            if (stateId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("State could not be determined for the logged-in user.");
            }
            String error = districtMISDataEntryService.saveSroModernization(stateId, districtId, totalSro, computerized,
                    stateFunds, dilrmpFunds);
            if (error != null) {
                return ResponseEntity.badRequest().body(error);
            }
            return ResponseEntity.ok("SRO Modernization data saved successfully!");
        } catch (Exception e) {
            logger.error("Error saving SRO Modernization for district ID: {}", districtId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong. Please try again or contact technical support for MIS.");
        }
    }

    private Long igrStateId() {
        try {
            UserService user = loogedInUserUtility.getLoggedinUser();
            if (user.getIgr() != null && user.getIgr().getState() != null) {
                return user.getIgr().getState().getId();
            }
            return user.getBoundry() != null ? user.getBoundry().getId() : null;
        } catch (RuntimeException e) {
            logger.error("Could not resolve IGR state", e);
            return null;
        }
    }
}
