package in.gov.dilrmp.controllers.naksha;


import in.gov.dilrmp.models.naksha.NakshaMISDataEntrySolrAndVender;
import in.gov.dilrmp.models.naksha.NakshaMisStateDTO;
import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.services.naksha.NakshaMISDataService;

import in.gov.dilrmp.services.user.UserService;
import in.gov.dilrmp.utils.CompletionStatus;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("ulb")
public class NaskhaMISDataEntryVenderController {

    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    private NakshaMISDataService nakshaMISDataService;
    private static final Logger logger = LoggerFactory.getLogger(NaskhaMISDataEntryVenderController.class);



    @GetMapping("/naksha-mis-venderdata/form")
    public String showNakhsaVenderForm(Model model) {
        try {
            UserService userService = loogedInUserUtility.getLoggedinUser();
            User user = userService.getUser();

            // Fetch states and gdName name
            List<NakshaMisStateDTO> nakshaMisStateDTOList = nakshaMISDataService.findStatesByGdID(user.getId().intValue());
            String gdName = nakshaMISDataService.getGdNameByZoneId(user.getId().intValue());
            String zoneName = nakshaMISDataService.getGdNamezoneByZoneId(user.getId().intValue());

            model.addAttribute("gdName", gdName);
            model.addAttribute("zoneName", zoneName);
            model.addAttribute("states", nakshaMisStateDTOList);
            model.addAttribute("nakshaMISDataEntry", new NakshaMISDataEntrySolrAndVender());
            model.addAttribute("completionStatuses", CompletionStatus.getTechStatuses());
            model.addAttribute("completionStatusesForVendor", CompletionStatus.getVendorStatuses());
            model.addAttribute("completionStatusesForQaQc", CompletionStatus.getQaQcStatuses());

            // These come from the enum
            List<Integer> completedStatusCodes = List.of(
                    CompletionStatus.COMPLETED.getCode(),
                    CompletionStatus.COMPLETED_POST_CORRECTION.getCode(),
                    CompletionStatus.RE_FLY_COMPLETED.getCode(),
                    CompletionStatus.ACCEPTED.getCode(),
                    CompletionStatus.ACCEPTED_AFTER_CORRECTION.getCode()
            );

            List<Integer> inProgressStatusCodes = List.of(
                    CompletionStatus.NOT_STARTED.getCode(),
                    CompletionStatus.UNDER_PROCESS.getCode(),
                    CompletionStatus.RE_FLY_UNDER_PROGRESS.getCode(),
                    CompletionStatus.RETURNED.getCode(),
                    CompletionStatus.UNDER_CORRECTION.getCode()

            );

// Add to model
            model.addAttribute("completedStatusCodes", completedStatusCodes);
            model.addAttribute("inProgressStatusCodes", inProgressStatusCodes);

            logger.info("Successfully loaded Naksha Vendor Form for user ID: {}", user.getId());

            return "pages/naksha/naksha_data_entry_solAndvender";
        } catch (Exception e) {
            logger.error("Error occurred while loading Naksha Vendor Form", e);
            return "error_page";
        }
    }

  /*  @GetMapping("/naksha-mis-venderdata/monitor")
    public String showNakhsaVendermonitor(Model model) {
        try {
            UserService userService = loogedInUserUtility.getLoggedinUser();
            User user = userService.getUser();

            // Fetch states and zone name
            List<NakshaMisStateDTO> nakshaMisStateDTOList = nakshaMISDataService.findStatesByGdID(user.getId().intValue());
            String gdName = nakshaMISDataService.getGdNameByZoneId(user.getId().intValue());

            model.addAttribute("zoneName", gdName);
            model.addAttribute("states", nakshaMisStateDTOList);
            model.addAttribute("nakshaMISDataEntry", new NakshaMISDataEntrySolrAndVender());

            logger.info("Successfully loaded Naksha Vendor Form for user ID: {}", user.getId());

            return "pages/naksha/naksha_data_entry_monitor";
        } catch (Exception e) {
            logger.error("Error occurred while loading Naksha Vendor Form", e);
            return "error_page";
        }
    }*/



    @PostMapping("/naksha-mis-venderdata/save")
    public ResponseEntity<String> saveNakhsaVenderData(NakshaMISDataEntrySolrAndVender nakshaMISDataEntrySolrAndVender) {
        try {
             nakshaMISDataEntrySolrAndVender.setUpdateOnDate(new Date());
            String message =nakshaMISDataService.saveVerder(nakshaMISDataEntrySolrAndVender);

            if ("success".equals(message)) {
                logger.info("Naksha Vendor Data saved successfully.");
                return ResponseEntity.ok("Naksha For SoI & Vendor Data Saved successfully!.");
            } else {
                logger.warn("Failed to save Naksha Vendor Data.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong. Please try again.");
            }
        } catch (Exception e) {
            logger.error("Error occurred while saving Naksha Vendor Data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong. Please try again or contact technical support for MIS.");
        }
    }


    @GetMapping("/districts/{stateId}")
    @ResponseBody
    public List<NakshaMisStateDTO> getDistrictsByState(@PathVariable Long stateId) {
        try {
            List<NakshaMisStateDTO> districts = nakshaMISDataService.getDistrictsByStateId(stateId);
            logger.info("Fetched {} districts for state ID: {}", districts.size(), stateId);
            return districts;
        } catch (Exception e) {
            logger.error("Error occurred while fetching districts for state ID: {}", stateId, e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/ulbName/{districtId}")
    @ResponseBody
    public List<NakshaMisStateDTO> getUlbNameByDistrictId(@PathVariable Long districtId) {
        try {
            List<NakshaMisStateDTO> ulbs = nakshaMISDataService.getUlbNameByDistrictId(districtId);
            logger.info("Fetched {} ULB names for district ID: {}", ulbs.size(), districtId);
            return ulbs;
        } catch (Exception e) {
            logger.error("Error occurred while fetching ULB names for district ID: {}", districtId, e);
            return Collections.emptyList();
        }
    }


    @GetMapping("/ulb-details/{ulbId}")
    @ResponseBody
    public NakshaMISDataEntrySolrAndVender getUlbDetails(@PathVariable Long ulbId) {
        try {
            // Fetch ULB details by ID
            return nakshaMISDataService.getVenderByUlbMasterId(ulbId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching ULB details for ID: " + ulbId, e);
        }
    }


    @GetMapping("/search")
    public ResponseEntity<List<NakshaMISDataEntrySolrAndVender>> getFilteredData(
            @RequestParam(required = false) Long stateId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long ulbMasterId) {

        logger.info("Fetching filtered data with stateId: {}, districtId: {}, ulbMasterId: {}", stateId, districtId, ulbMasterId);

        try {
            List<NakshaMISDataEntrySolrAndVender> result = nakshaMISDataService.getFilteredData(stateId, districtId, ulbMasterId);

            if (result.isEmpty()) {
                logger.warn("No data found for the given filters: stateId={}, districtId={}, ulbMasterId={}", stateId, districtId, ulbMasterId);
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error while fetching filtered data: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }




}
