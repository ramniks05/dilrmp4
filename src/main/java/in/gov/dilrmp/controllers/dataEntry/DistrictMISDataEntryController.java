package in.gov.dilrmp.controllers.dataEntry;

import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.dataEntryModel.DistrictMISDataEntry;
import in.gov.dilrmp.models.dataEntryModel.DistrictMISPermissionStatus;
import in.gov.dilrmp.services.DataEntryForm.DistrictMISDataEntryPermissionService;
import in.gov.dilrmp.services.DataEntryForm.DistrictMISDataEntryService;
import in.gov.dilrmp.services.administrativeBoundry.DistrictService;
import in.gov.dilrmp.services.user.UserService;
import in.gov.dilrmp.utils.DateUtils;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;


@Controller
@RequestMapping("state")
public class DistrictMISDataEntryController {

    @Autowired
    private DistrictService districtService;
    @Autowired
    LoogedInUserUtility loogedInUserUtility;

    @Autowired
    private DistrictMISDataEntryService districtMISDataEntryService;

    @Autowired
    private DistrictMISDataEntryPermissionService permissionService;

    Logger logger = LoggerFactory.getLogger(DistrictMISDataEntryController.class);
    @GetMapping("/district-mis-data/form")
    public String showForm(Model model, HttpServletRequest request, HttpServletResponse response) {
        UserService userService=loogedInUserUtility.getLoggedinUser();
        Long stateId = userService.getBoundry().getId();
        Long loginId = userService.getUserID();

        // Initialize districtMISDataEntry with a new instance
        DistrictMISDataEntry districtMISDataEntry = new DistrictMISDataEntry();
        districtMISDataEntry.setDistrict(new District());  // Initialize district to avoid null pointer exception

        if (loogedInUserUtility.hasRole("ROLE_DISTRICT") && loginId != null) {
            District district = districtService.findDistrictByUserID(loginId);
            model.addAttribute("districts", district);

            // Fetch district data, if available
            districtMISDataEntry = districtMISDataEntryService.getDistrictData(district.getId());

            // Ensure districtMISDataEntry is not null
            if (districtMISDataEntry == null) {
                districtMISDataEntry = new DistrictMISDataEntry();
                districtMISDataEntry.setDistrict(district); // Set district to avoid null pointer exception
            }
        }else if (loogedInUserUtility.hasRole("ROLE_ADC") && loginId != null) {
            // For state-level users, fetch all districts by state ID
            model.addAttribute("districts", districtService.findAllDistrictByAdcID(districtService.findBoundryByAdcUserID(loginId).getId()));

            // Ensure districtMISDataEntry has a valid District object
            districtMISDataEntry.setDistrict(new District());
        } else if (stateId != null) {
            // For state-level users, fetch all districts by state ID
            model.addAttribute("districts", districtService.findAllDistrictByStateID(stateId));

            // Ensure districtMISDataEntry has a valid District object
            districtMISDataEntry.setDistrict(new District());
        }

        // Add districtMISDataEntry to the model
        model.addAttribute("districtMISDataEntry", districtMISDataEntry);
        addPermissionAttributes(model, districtMISDataEntry);

        // Return the Thymeleaf template fragment for rendering
        return "pages/state/district_mis_data_entry_form::district-mis-data";
    }

    private void addPermissionAttributes(Model model, DistrictMISDataEntry districtMISDataEntry) {
        Long districtId = districtMISDataEntry.getDistrict() != null ? districtMISDataEntry.getDistrict().getId() : null;
        DistrictMISPermissionStatus permissionStatus = districtId != null
                ? permissionService.getPermissionStatus(districtId)
                : DistrictMISPermissionStatus.increaseOnly();
        model.addAttribute("allowDecrease", permissionStatus.isAllowDecrease());
        model.addAttribute("increaseOnlyPolicyActive", districtId != null && !permissionStatus.isAllowDecrease());
        model.addAttribute("decreaseAllowedUntilDisplay", permissionStatus.getDecreaseAllowedUntilDisplay());
        model.addAttribute("decreaseAllowedUntilEpoch", permissionStatus.getDecreaseAllowedUntil() != null
                ? permissionStatus.getDecreaseAllowedUntil()
                        .atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                : 0);
        model.addAttribute("showPermissionExpiredAlert", permissionStatus.isPermissionJustExpired());
        boolean districtSelected = districtId != null && districtId > 0;
        model.addAttribute("showPermissionAllowedAlert",
                districtSelected && permissionStatus.isAllowDecrease());
    }

    @PostMapping("/district-mis-data/save")
    public ResponseEntity<String> saveDistrictMISData(@ModelAttribute DistrictMISDataEntry districtMISDataEntry,
                                                      RedirectAttributes redirectAttributes) {
        String message="";
        try {
            if(districtMISDataEntry.getDistrict().getId()!=0){
                message = districtMISDataEntryService.save(districtMISDataEntry);
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please select a district.");
            }

            if ("success".equals(message)) {
                return ResponseEntity.ok("District MIS Data Saved successfully!.");
            } else {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong. Please try again.");
            }
        } catch (Exception e) {

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong. Please try again or contact technical support for MIS.");
        }
    }



    @PostMapping("/district-mis-data")
    public String getDistrictData(@RequestParam("district.id") Long districtId, Model model, RedirectAttributes redirectAttributes) {
        try {
            DistrictMISDataEntry entry = districtMISDataEntryService.getDistrictData(districtId);
            DistrictMISDataEntry modelEntry;

            if (entry != null) {
                if (entry.getMapsUpdatePeriod() != null) {
                    entry.setMapsUpdatePeriodDate(DateUtils.formatDate(entry.getMapsUpdatePeriod()));
                }
                modelEntry = entry;
            } else {
                modelEntry = new DistrictMISDataEntry();
                modelEntry.setDistrict(districtService.findById(districtId).orElseThrow());
            }
            model.addAttribute("districtMISDataEntry", modelEntry);
            addPermissionAttributes(model, modelEntry);

            if (loogedInUserUtility.hasRole("ROLE_ADC") && modelEntry.getDistrict().getAdc() != null
                    && modelEntry.getDistrict().getAdc().getId() != null) {
                model.addAttribute("districts", districtService.findAllDistrictByAdcID(modelEntry.getDistrict().getAdc().getId()));
            } else {
                model.addAttribute("districts", districtService.findAllDistrictByStateID(
                        loogedInUserUtility.getLoggedinUser().getBoundry().getId()));
                logger.info("Successfully loaded district MIS data for district ID: {}", districtId);
            }

        } catch (NoSuchElementException e) {
            logger.error("No district found with ID: {}", districtId, e);
            return "redirect:/state/district-mis-data/form";
        } catch (Exception e) {
            logger.error("Error occurred while fetching district data for ID: {}", districtId, e);
            redirectAttributes.addFlashAttribute("message", "Something went wrong. Please try again or contact support technical support for MIS.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/state/district-mis-data/form";
        }

        return "pages/state/district_mis_data_entry_form::district-mis-data";
    }



}
