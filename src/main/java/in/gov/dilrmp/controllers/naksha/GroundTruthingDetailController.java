package in.gov.dilrmp.controllers.naksha;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.naksha.GroundTruthingDetail;
import in.gov.dilrmp.models.naksha.ULBMaster;
import in.gov.dilrmp.repositories.naksha.GroundTruthingDetailRepository;
import in.gov.dilrmp.repositories.naksha.ULBMsterRepository;
import in.gov.dilrmp.services.naksha.GroundTruthingDetailService;
import in.gov.dilrmp.utils.DropdownOptions;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("ulb")
public class GroundTruthingDetailController {

    @Autowired
    private GroundTruthingDetailService groundTruthingDetailService;

    @Autowired
    private ULBMsterRepository ulbMsterRepository;

    @Autowired
    private GroundTruthingDetailRepository truthingDetailRepository;

    private static final Logger logger = LoggerFactory.getLogger(GroundTruthingDetailController.class);

    @GetMapping("/ulb-gt-mis-data/form")
    public String showGTForm(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<ULBMaster> ulbMasterList = ulbMsterRepository.findBymuser_id(userId);
        model.addAttribute("ulbMasterList", ulbMasterList);

        if (!ulbMasterList.isEmpty()) {
            model.addAttribute("stateName", ulbMasterList.get(0).getState_name());
        }

        List<GroundTruthingDetail> existingDataList = groundTruthingDetailService.findByUlbMuserId(userId);
        GroundTruthingDetail nakshaGTDataEntry = existingDataList.isEmpty() ? new GroundTruthingDetail() : existingDataList.get(0);

        model.addAttribute("nakshaGTDataEntry", nakshaGTDataEntry);
        model.addAttribute("survyUnitTypeOptions", DropdownOptions.SurveyUnitType.values());
        model.addAttribute("surveyTypeOptions", DropdownOptions.SurveyType.values());
        model.addAttribute("surveyUnitSurveyTypeOptions", DropdownOptions.SurveyUnitSurveyType.values());
        model.addAttribute("workValidationOptions", DropdownOptions.WorkValidationStatus.values());
        return "pages/naksha/ground_truthingEntryForm";
    }


    @PostMapping("/naksha-gt-data/save")
    public ResponseEntity<String> saveGTData(
            @ModelAttribute("nakshaGTDataEntry") GroundTruthingDetail detail,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Session expired. Please log in again.");
        }

        try {
            // Get selected ULB ID from the form
            Long selectedUlbId = detail.getUlbMaster().getId();
            if (selectedUlbId == null) {
                return ResponseEntity.badRequest().body("Please select a ULB.");
            }

            // Fetch full ULBMaster entity by ID
            Optional<ULBMaster> ulbOpt = ulbMsterRepository.findById(selectedUlbId);
            if (ulbOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid ULB selected.");
            }

            ULBMaster ulbMaster = ulbOpt.get();
            detail.setUlbMaster(ulbMaster);

            // Set State (if present in ULBMaster)
            if (ulbMaster.getState_id() != null) {
                State state = new State();
                state.setId(ulbMaster.getState_id());
                detail.setState(state);
            }

            // 🔍 Check if data already exists for this ULB
            Optional<GroundTruthingDetail> existingDetailOpt =
                    truthingDetailRepository.findByUlbMaster_Id(ulbMaster.getId());

            GroundTruthingDetail toSave;

            if (existingDetailOpt.isPresent()) {
                GroundTruthingDetail existing = existingDetailOpt.get();
                // Update fields
                existing.setUlbWiseFieldSurveyTeamsFormed(detail.getUlbWiseFieldSurveyTeamsFormed());
                existing.setSurveyUnitNameOptions(detail.getSurveyUnitNameOptions());
                existing.setSurveyUnitText(detail.getSurveyUnitText());
                existing.setTypeOfSurveyDone(detail.getTypeOfSurveyDone());
                existing.setFieldSurveyArea(detail.getFieldSurveyArea());
                existing.setAreaFieldSurveyCompletedULB(detail.getAreaFieldSurveyCompletedULB());
                existing.setAreaWiseSurveyCompletionPercent(detail.getAreaWiseSurveyCompletionPercent());
                
                // Survey Unit tracking fields (16-19)
                existing.setTotalSurveyUnits(detail.getTotalSurveyUnits());
                existing.setSurveyUnitsSurveyed(detail.getSurveyUnitsSurveyed());
                existing.setSurveyUnitsPending(detail.getSurveyUnitsPending());
                existing.setSurveyUnitCompletionPercent(detail.getSurveyUnitCompletionPercent());
                
                existing.setTypeOfSurveyDoneForSurveyUnit(detail.getTypeOfSurveyDoneForSurveyUnit());
                existing.setTotalPropertiesForFieldSurveyInSurveyUnit(detail.getTotalPropertiesForFieldSurveyInSurveyUnit());
                existing.setNoOfPropertiesSurveyedInSurveyUnit(detail.getNoOfPropertiesSurveyedInSurveyUnit());
                existing.setPropertyWiseFieldSurveyCompletionPercent(detail.getPropertyWiseFieldSurveyCompletionPercent());
                existing.setTotalAreaForFieldSurveyInSurveyUnit(detail.getTotalAreaForFieldSurveyInSurveyUnit());
                existing.setAreaWhereFieldSurveyCompletedInSurveyUnit(detail.getAreaWhereFieldSurveyCompletedInSurveyUnit());
                existing.setAreaWiseFieldSurveyCompletionPercentSurveyUnit(detail.getAreaWiseFieldSurveyCompletionPercentSurveyUnit());
                existing.setWorkValidatedBySupervisoryOfficer(detail.getWorkValidatedBySupervisoryOfficer());
                existing.setNoOfClaimsWithBoundaryDisputes(detail.getNoOfClaimsWithBoundaryDisputes());
                existing.setNoOfClaimsWithOwnershipConflicts(detail.getNoOfClaimsWithOwnershipConflicts());
                existing.setNoOfClaimsWithDataErrors(detail.getNoOfClaimsWithDataErrors());
                existing.setNoOfClaimsWithAdministrativeIssues(detail.getNoOfClaimsWithAdministrativeIssues());
                existing.setNoOfAnyOtherClaims(detail.getNoOfAnyOtherClaims());
                existing.setNoOfClaimsObjectionsResolved(detail.getNoOfClaimsObjectionsResolved());
                existing.setNoOfClaimsObjectionsPending(detail.getNoOfClaimsObjectionsPending());
                existing.setPercentOfClaimsDisputesResolution(detail.getPercentOfClaimsDisputesResolution());
                existing.setTotalPlotsToSurvey(detail.getTotalPlotsToSurvey());
                existing.setPlotsSurveyedCompleted(detail.getPlotsSurveyedCompleted());
                existing.setSurveyCompletionPercent(detail.getSurveyCompletionPercent());
                existing.setTotalUrProCardIssued(detail.getTotalUrProCardIssued());
                existing.setClaimsObjectionsReceived(detail.getClaimsObjectionsReceived());
                existing.setClaimsObjectionsResolved(detail.getClaimsObjectionsResolved());
                existing.setClaimsResolutionPercentage(detail.getClaimsResolutionPercentage());
                existing.setTotalFinalUrProCardIssued(detail.getTotalFinalUrProCardIssued());
                // Preserve existing Ground Truthing Commencement Date if it was already saved
                if (existing.getGroundTruthingCommencementDate() != null) {
                    // Keep the existing date, don't update it
                } else {
                    // Only set if it doesn't exist yet
                    existing.setGroundTruthingCommencementDate(detail.getGroundTruthingCommencementDate());
                }
                existing.setGroundTruthingCompletionDate(detail.getGroundTruthingCompletionDate());
                existing.setUpdateOnDate(LocalDate.now());
                toSave = existing;

            } else {
                // Insert new data
                detail.setCreateOnDate(LocalDate.now());
                detail.setUpdateOnDate(LocalDate.now());
                toSave = detail;
            }

            groundTruthingDetailService.save(toSave);
            return ResponseEntity.ok("ULB data saved successfully.");

        } catch (Exception e) {
            logger.error("Save error", e);
            return ResponseEntity.status(500).body("Unexpected error. Contact admin.");
        }
    }

    @GetMapping("/getGTMISData/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUlbDetails(@PathVariable Long id) {
        try {
            // First, try to get ULB master data (this should always be available)
            Optional<ULBMaster> ulbMasterOpt = ulbMsterRepository.findById(id);
            if (!ulbMasterOpt.isPresent()) {
                logger.warn("ULB Master not found for ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            ULBMaster ulbMaster = ulbMasterOpt.get();
            Map<String, Object> response = new HashMap<>();

            // Always include ULB master data (fields 1-5)
            response.put("ulbId", ulbMaster.getId());
            response.put("population", ulbMaster.getPopulation());
            response.put("fieldSurveyArea", ulbMaster.getFieldSurveyArea());
            response.put("aerialSurveyArea", ulbMaster.getAerialSurveyArea());
            response.put("bufferGridAreaForFlying", ulbMaster.getBufferGridAreaForFlying());
            response.put("noOfTeams", ulbMaster.getNoOfTeams());

            // Try to get existing GroundTruthingDetail data
            List<GroundTruthingDetail> detailList = groundTruthingDetailService.findByUlbMuserId(id);
            
            if (!detailList.isEmpty()) {
                GroundTruthingDetail detail = detailList.get(0); // Assuming only one record per ULB
                
                // Add existing GroundTruthingDetail fields
                response.put("id", detail.getId());
                response.put("stateId", detail.getState() != null ? detail.getState().getId() : null);
                response.put("ulbWiseFieldSurveyTeamsFormed", detail.getUlbWiseFieldSurveyTeamsFormed());
                response.put("surveyUnitNameOptions", detail.getSurveyUnitNameOptions());
                response.put("surveyUnitText", detail.getSurveyUnitText());
                response.put("typeOfSurveyDone", detail.getTypeOfSurveyDone());
                response.put("areaFieldSurveyCompletedULB", detail.getAreaFieldSurveyCompletedULB());
                response.put("areaWiseSurveyCompletionPercent", detail.getAreaWiseSurveyCompletionPercent());
                
                // Survey Unit tracking fields (16-19)
                response.put("totalSurveyUnits", detail.getTotalSurveyUnits());
                response.put("surveyUnitsSurveyed", detail.getSurveyUnitsSurveyed());
                response.put("surveyUnitsPending", detail.getSurveyUnitsPending());
                response.put("surveyUnitCompletionPercent", detail.getSurveyUnitCompletionPercent());
                
                response.put("typeOfSurveyDoneForSurveyUnit", detail.getTypeOfSurveyDoneForSurveyUnit());
                response.put("totalPropertiesForFieldSurveyInSurveyUnit", detail.getTotalPropertiesForFieldSurveyInSurveyUnit());
                response.put("noOfPropertiesSurveyedInSurveyUnit", detail.getNoOfPropertiesSurveyedInSurveyUnit());
                response.put("propertyWiseFieldSurveyCompletionPercent", detail.getPropertyWiseFieldSurveyCompletionPercent());
                response.put("totalAreaForFieldSurveyInSurveyUnit", detail.getTotalAreaForFieldSurveyInSurveyUnit());
                response.put("areaWhereFieldSurveyCompletedInSurveyUnit", detail.getAreaWhereFieldSurveyCompletedInSurveyUnit());
                response.put("areaWiseFieldSurveyCompletionPercentSurveyUnit", detail.getAreaWiseFieldSurveyCompletionPercentSurveyUnit());
                response.put("workValidatedBySupervisoryOfficer", detail.getWorkValidatedBySupervisoryOfficer());
                response.put("noOfClaimsWithBoundaryDisputes", detail.getNoOfClaimsWithBoundaryDisputes());
                response.put("noOfClaimsWithOwnershipConflicts", detail.getNoOfClaimsWithOwnershipConflicts());
                response.put("noOfClaimsWithDataErrors", detail.getNoOfClaimsWithDataErrors());
                response.put("noOfClaimsWithAdministrativeIssues", detail.getNoOfClaimsWithAdministrativeIssues());
                response.put("noOfAnyOtherClaims", detail.getNoOfAnyOtherClaims());
                response.put("noOfClaimsObjectionsResolved", detail.getNoOfClaimsObjectionsResolved());
                response.put("noOfClaimsObjectionsPending", detail.getNoOfClaimsObjectionsPending());
                response.put("percentOfClaimsDisputesResolution", detail.getPercentOfClaimsDisputesResolution());
                response.put("totalPlotsToSurvey", detail.getTotalPlotsToSurvey());
                response.put("plotsSurveyedCompleted", detail.getPlotsSurveyedCompleted());
                response.put("surveyCompletionPercent", detail.getSurveyCompletionPercent());
                response.put("totalUrProCardIssued", detail.getTotalUrProCardIssued());
                response.put("claimsObjectionsReceived", detail.getClaimsObjectionsReceived());
                response.put("claimsObjectionsResolved", detail.getClaimsObjectionsResolved());
                response.put("claimsResolutionPercentage", detail.getClaimsResolutionPercentage());
                response.put("totalFinalUrProCardIssued", detail.getTotalFinalUrProCardIssued());
                response.put("groundTruthingCommencementDate", detail.getGroundTruthingCommencementDate());
                response.put("groundTruthingCompletionDate", detail.getGroundTruthingCompletionDate());
                response.put("createOnDate", detail.getCreateOnDate());
                response.put("updateOnDate", detail.getUpdateOnDate());
                
                logger.info("Returning existing GroundTruthingDetail data for ULB ID: {}", id);
            } else {
                // No existing GroundTruthingDetail record, return only ULB master data
                logger.info("No existing GroundTruthingDetail found for ULB ID: {}, returning only ULB master data", id);
            }

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching ULB details for ID: {}", id, e);
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }



}









