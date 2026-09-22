package in.gov.dilrmp.controllers.naksha;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.naksha.CapacityBuilding;

import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.services.naksha.CapacityBuildingService;

import in.gov.dilrmp.services.naksha.NakshaMISDataService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("ulb")
public class CapacityBuildingController {

    @Autowired
    private CapacityBuildingService service;
    @Autowired
    NakshaMISDataService nakshaMISDataService;

    private static final Logger logger = LoggerFactory.getLogger(NaskhaMISDataEntryVenderController.class);



    @GetMapping("/capacity-building/form")
    public String capacityBuilding(Model model, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        CapacityBuilding existingData = service.findByUlbMasterId(userId);

        CapacityBuilding capacityBuilding = existingData != null ? existingData : new CapacityBuilding();
        model.addAttribute("capacityBuilding", capacityBuilding);

        if (capacityBuilding.getState() != null) {
            String stateName = capacityBuilding.getState().getName();
            Long stateId = capacityBuilding.getState().getId();

            model.addAttribute("stateName", stateName);

            // Store state ID in session
            session.setAttribute("stateId", stateId);
        } else {
            model.addAttribute("stateName", "Unknown State");
            session.setAttribute("stateId", null);
        }


        return "pages/naksha/capacity_building";
    }


    @PostMapping("/capacity-building")
    public ResponseEntity<String> saveCapacityBuilding(@ModelAttribute("capacityBuilding") CapacityBuilding capacityBuilding,
                                                       HttpSession session) {
        try {
            // Retrieve userId and stateId from session
            Long userId = (Long) session.getAttribute("userId");
            Long stateId = (Long) session.getAttribute("stateId");

            // Set User and State using only their IDs
            if (userId != null) {
                User user = new User();
                user.setId(userId);
                capacityBuilding.setMuser(user);
            }

            if (stateId != null) {
                State state = new State();
                state.setId(stateId);
                capacityBuilding.setState(state);
            }

            // Save the CapacityBuilding entity
            String result = service.saveCapacityBuilding(capacityBuilding);

            if ("success".equalsIgnoreCase(result)) {
                logger.info("Capacity Building data saved successfully for state ID: {}", stateId);
                return ResponseEntity.ok("Capacity Building data saved successfully.");
            } else {
                logger.warn("Failed to save Capacity Building data.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to save data. Please try again.");
            }
        } catch (Exception e) {
            logger.error("Exception while saving Capacity Building data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again or contact technical support.");
        }
    }


    @GetMapping("/last-update-date")
    @ResponseBody
    public ResponseEntity<String> getUpdateOnDate(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            logger.warn("User ID not found in session.");
            return ResponseEntity.badRequest().body("User ID not found in session");
        }

        try {
            LocalDate date = service.getUpdateOnDateByUserId(userId);

            if (date != null) {
                String formattedDate = date.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")); // e.g. 04 July 2025
                logger.info("Successfully fetched updateOnDate for userId={}", userId);
                return ResponseEntity.ok(formattedDate);
            } else {
                logger.warn("No updateOnDate found for userId={}", userId);
                return ResponseEntity.status(404).body("Update date not found");
            }

        } catch (Exception e) {
            logger.error("Error fetching updateOnDate for userId={}", userId, e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
    @GetMapping("/state-rovers-summary")
    public ResponseEntity<List<Map<String, Object>>> getStateRoversSummary(HttpSession session) {
        try {
            // Get user ID from session
            Long userId = (Long) session.getAttribute("userId");

            if (userId == null) {
                logger.warn("User ID not found in session.");
                return ResponseEntity.internalServerError().build();
            }

            List<Map<String, Object>> data = nakshaMISDataService.getStateRoversSummary(userId);

            return ResponseEntity.ok(data);

        } catch (Exception e) {
            logger.error("Error fetching state rover summary", e);
            return ResponseEntity.internalServerError().build();
        }
    }




}
