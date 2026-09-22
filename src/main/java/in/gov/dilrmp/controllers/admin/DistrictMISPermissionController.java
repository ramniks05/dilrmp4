package in.gov.dilrmp.controllers.admin;

import in.gov.dilrmp.models.dataEntryModel.DistrictMISPermissionRow;
import in.gov.dilrmp.services.DataEntryForm.DistrictMISDataEntryPermissionService;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("dolr")
public class DistrictMISPermissionController {

    private static final Logger logger = LoggerFactory.getLogger(DistrictMISPermissionController.class);

    @Autowired
    private StateService stateService;

    @Autowired
    private DistrictMISDataEntryPermissionService permissionService;

    @GetMapping("/district-mis-permissions")
    public String showPermissionForm(Model model) {
        model.addAttribute("states", stateService.findAllOrderByStateName());
        return "pages/admin/district_mis_permission :: district-mis-permission";
    }

    @PostMapping("/district-mis-permissions/load")
    public String loadDistrictPermissions(@RequestParam("stateId") Long stateId, Model model) {
        List<DistrictMISPermissionRow> districtRows = permissionService.getPermissionRowsForState(stateId);
        boolean allAllowDecrease = !districtRows.isEmpty()
                && districtRows.stream().allMatch(DistrictMISPermissionRow::isAllowDecrease);
        long allowedDistrictCount = districtRows.stream().filter(DistrictMISPermissionRow::isAllowDecrease).count();
        model.addAttribute("districtRows", districtRows);
        model.addAttribute("selectedStateId", stateId);
        model.addAttribute("allAllowDecrease", allAllowDecrease);
        model.addAttribute("allowedDistrictCount", allowedDistrictCount);
        model.addAttribute("totalDistrictCount", districtRows.size());
        return "pages/admin/district_mis_permission :: district-permission-list";
    }

    @PostMapping("/district-mis-permissions/save")
    public ResponseEntity<Map<String, String>> savePermissions(
            @RequestParam("stateId") Long stateId,
            @RequestParam(value = "allowDecreaseDistrictIds", required = false) List<Long> allowDecreaseDistrictIds) {
        try {
            permissionService.savePermissionsForState(stateId, allowDecreaseDistrictIds);
            logger.info("District MIS permissions saved for state ID: {}", stateId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "District MIS data entry permissions saved successfully. "
                            + "Increase & Decrease permission is valid for 24 hours only, after which districts revert to Increase Only."
            ));
        } catch (Exception e) {
            logger.error("Failed to save district MIS permissions for state ID: {}", stateId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to save permissions. Please try again."
            ));
        }
    }
}
