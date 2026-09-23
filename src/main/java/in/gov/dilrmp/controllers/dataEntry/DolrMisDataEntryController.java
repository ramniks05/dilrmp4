package in.gov.dilrmp.controllers.dataEntry;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.dataEntryModel.DolrDistrictMisDataEntry;
import in.gov.dilrmp.models.dataEntryModel.DolrMisDataEntry;
import in.gov.dilrmp.services.DataEntryForm.DolrMisDataEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dolr")
public class DolrMisDataEntryController {

    private static final Logger logger = LoggerFactory.getLogger(DolrMisDataEntryController.class);

    @Autowired
    DolrMisDataEntryService dolrMisDataEntryService;

    @GetMapping("/generic-mis-data/form")
    public String showForm(Model model) {
        List<State> states = dolrMisDataEntryService.getStates();
        Long stateId = states.isEmpty() ? null : states.get(0).getId();
        populate(model, stateId, null);
        return "pages/dolr/dolr_generic_mis_data_entry_form :: dolr-generic-mis-data";
    }

    @PostMapping("/generic-mis-data/form/load")
    public String loadForm(@RequestParam("stateId") Long stateId, Model model) {
        populate(model, stateId, null);
        return "pages/dolr/dolr_generic_mis_data_entry_form :: dolr-generic-mis-fields";
    }

    @PostMapping("/generic-mis-data/form/district")
    public String loadDistrict(@RequestParam("stateId") Long stateId,
                               @RequestParam(value = "districtId", required = false) String districtId,
                               Model model) {
        populate(model, stateId, toLong(districtId));
        return "pages/dolr/dolr_generic_mis_data_entry_form :: dolr-district-sanction-fields";
    }

    @PostMapping("/generic-mis-data/save")
    @ResponseBody
    public ResponseEntity<Map<String, String>> save(@RequestBody Map<String, Object> payload) {
        Map<String, String> response = new LinkedHashMap<>();
        try {
            Long stateId = Long.valueOf(payload.get("stateId").toString());
            Integer sroSanctioned = toInt(payload.get("srosDilrmpSanctioned"));
            dolrMisDataEntryService.saveSroSanction(stateId, sroSanctioned);
            Long districtId = toLong(payload.get("districtId"));
            if (districtId != null) {
                dolrMisDataEntryService.saveDistrictSanctions(
                        stateId,
                        districtId,
                        toInt(payload.get("legacyDilrmpSanctionedPages")),
                        toInt(payload.get("revenueLegacyDilrmpSanctionedPages")));
            }
            response.put("status", "success");
            response.put("message", districtId == null
                    ? "SRO sanction saved for the State/UT."
                    : "DoLR sanctions saved.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error saving DoLR generic MIS data", e);
            response.put("status", "error");
            response.put("message", "Failed to save. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private void populate(Model model, Long stateId, Long districtId) {
        if (districtId != null && !dolrMisDataEntryService.districtBelongsToState(stateId, districtId)) {
            districtId = null;
        }
        model.addAttribute("states", dolrMisDataEntryService.getStates());
        model.addAttribute("districts", dolrMisDataEntryService.getDistricts(stateId));
        model.addAttribute("selectedStateId", stateId);
        model.addAttribute("selectedDistrictId", districtId);
        DolrMisDataEntry entry = stateId == null
                ? new DolrMisDataEntry()
                : dolrMisDataEntryService.getByStateId(stateId);
        model.addAttribute("dolrMisDataEntry", entry);
        DolrDistrictMisDataEntry districtEntry = districtId == null
                ? new DolrDistrictMisDataEntry()
                : dolrMisDataEntryService.getByDistrictId(districtId);
        model.addAttribute("dolrDistrictEntry", districtEntry);
    }

    private Integer toInt(Object value) {
        if (value == null || value.toString().isBlank()) {
            return 0;
        }
        return Integer.valueOf(value.toString());
    }

    private Long toLong(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        return Long.valueOf(value.toString());
    }
}
