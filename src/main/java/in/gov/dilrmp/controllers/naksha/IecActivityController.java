package in.gov.dilrmp.controllers.naksha;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.naksha.IECActivities;
import in.gov.dilrmp.models.naksha.ULBMaster;
import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.services.naksha.IecActivityService;
import in.gov.dilrmp.services.naksha.NakshaMISDataService;
import in.gov.dilrmp.services.user.UserService;
import in.gov.dilrmp.utils.IECActivityDropdowns;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/ulb")
public class IecActivityController {

    private static final Logger logger = LoggerFactory.getLogger(IecActivityController.class);

    @Autowired
    private IecActivityService iecActivityService;

    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    private NakshaMISDataService nakshaMISDataService;

    // ========== GET FORM ==========
    @GetMapping("/iec-activity/form")
    public String loadIecActivityForm(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        // Fetch existing IECActivities by userId (like capacityBuilding)
        IECActivities existingData = iecActivityService.findByUlbMasterId(userId);
        IECActivities iecActivity = existingData != null ? existingData : new IECActivities();

        model.addAttribute("iecActivity", iecActivity);

        if (iecActivity.getState() != null) {
            model.addAttribute("stateName", iecActivity.getState().getName());
            model.addAttribute("lastUpdateDate", iecActivity.getUpdateOnDate());
            session.setAttribute("stateId", iecActivity.getState().getId());
        } else {
            // If no state in IECActivities, fallback to user's first ULBMaster state if available
            UserService userService = loogedInUserUtility.getLoggedinUser();
            User user = userService.getUser();
            List<ULBMaster> ulbMasterList = nakshaMISDataService.findBymuser_id(user.getId());

            if (!ulbMasterList.isEmpty()) {
                model.addAttribute("stateName", ulbMasterList.get(0).getState_name());
                session.setAttribute("stateId", ulbMasterList.get(0).getState_id());
            } else {
                model.addAttribute("stateName", "Unknown State");
                session.setAttribute("stateId", null);
            }
        }

        // Add all dropdown lists as model attributes
        model.addAttribute("iecMaterialStatusList", IECActivityDropdowns.IECMaterialStatus.getAll());
        model.addAttribute("iecMediaTypeList", IECActivityDropdowns.IECMediaType.getAll());
        model.addAttribute("iecMediaVarietyList", IECActivityDropdowns.IECMediaVariety.getAll());
        model.addAttribute("iecActivityStatusList", IECActivityDropdowns.IECActivityStatus.getAll());
        model.addAttribute("iecActivityConductedList", IECActivityDropdowns.IECActivityConducted.getAll());
        model.addAttribute("iecActivityTypeList", IECActivityDropdowns.IECActivityType.getAll());

        return "pages/naksha/iec_activities_dataEntry";
    }

    // ========== POST FORM ==========
    @PostMapping("/iec-activity")
    public ResponseEntity<String> saveIecActivity(@ModelAttribute("iecActivity") IECActivities iecActivity,
                                                  HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            Long stateId = (Long) session.getAttribute("stateId");

            if (userId != null) {
                User user = new User();
                user.setId(userId);
                iecActivity.setMuser(user);

                // ✅ Fetch logged-in user’s state name using LoogedInUserUtility
                UserService userService = loogedInUserUtility.getLoggedinUser();
                User loggedInUser = userService.getUser();

                List<ULBMaster> ulbMasterList = nakshaMISDataService.findBymuser_id(loggedInUser.getId());
                if (!ulbMasterList.isEmpty()) {
                    ULBMaster ulb = ulbMasterList.get(0);

                    // Set state and state name
                    State state = new State();
                    state.setId(ulb.getState_id());
                    iecActivity.setState(state);

                    iecActivity.setStateName(ulb.getState_name());

                    // Also update session
                    session.setAttribute("stateId", ulb.getState_id());
                }
            }

            String result = iecActivityService.saveIecActivity(iecActivity);

            if ("success".equalsIgnoreCase(result)) {
                logger.info("IEC Activity saved for user ID: {}", userId);
                return ResponseEntity.ok("IEC Activity data saved successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to save IEC data. Please try again.");
            }

        } catch (Exception e) {
            logger.error("Error saving IEC Activity", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred.");
        }

    }


    // ========== LAST UPDATE FETCH ==========
    @GetMapping("/iec-activity/last-update-date")
    @ResponseBody
    public ResponseEntity<String> getIecUpdateDate(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID not found");
        }

        try {
            LocalDate date = iecActivityService.getUpdateOnDateByUserId(userId);

            if (date != null) {
                String formattedDate = date.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"));
                return ResponseEntity.ok(formattedDate);
            } else {
                return ResponseEntity.status(404).body("Update date not found");
            }

        } catch (Exception e) {
            logger.error("Error fetching update date", e);
            return ResponseEntity.status(500).body("Internal error");
        }
    }

}
