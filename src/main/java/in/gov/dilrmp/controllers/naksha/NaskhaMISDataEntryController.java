package in.gov.dilrmp.controllers.naksha;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntry;
import in.gov.dilrmp.models.naksha.StateNakshaSLCMeetingDate;
import in.gov.dilrmp.models.naksha.ULBMaster;
import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.repositories.naksha.SLCMeetingRepository;
import in.gov.dilrmp.services.naksha.NakshaMISDataService;
import in.gov.dilrmp.services.administrativeBoundry.DistrictService;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import in.gov.dilrmp.services.user.UserService;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import in.gov.dilrmp.utils.DropdownOptions;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("ulb")
public class NaskhaMISDataEntryController {

    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    private NakshaMISDataService nakshaMISDataService;

    @Autowired
    private StateService stateService;

    @Autowired
    private SLCMeetingRepository slcMeetingRepository;

    @Autowired
    DistrictService districtService;

    @GetMapping("/ulb-naksha-mis-data/form")
    public String showNakhsaForm(Model model) {
        model.addAttribute("nakshaMISDataEntry", new NakshaMISDataEntry());
        UserService userService = loogedInUserUtility.getLoggedinUser();
        User user = userService.getUser();
        List<ULBMaster> ulbMasterList= nakshaMISDataService.findBymuser_id(user.getId());
        NakshaMISDataEntry existingDataOptional = nakshaMISDataService.findByUlbMuserId(user.getId());
        if(existingDataOptional !=null){
            model.addAttribute("stateName", existingDataOptional.getState().getName());
            model.addAttribute("lastUpdateDate", existingDataOptional.getUpdateOnDate());
            model.addAttribute("nakshaMISDataEntry", existingDataOptional);

            List<StateNakshaSLCMeetingDate> slcMeetingDates = slcMeetingRepository.findByNakshaMISDataEntry(existingDataOptional);
            model.addAttribute("slcMeetingDates", slcMeetingDates);

        }else {
            model.addAttribute("stateName", ulbMasterList.get(0).getState_name());
            model.addAttribute("nakshaMISDataEntry", new NakshaMISDataEntry());
            model.addAttribute("slcMeetingDates", List.of()); // empty list
        }

        // ✅ Add dropdown enum values to model
        model.addAttribute("roverProcurementOptions", DropdownOptions.RoverProcurementStatus.values());
        model.addAttribute("legalFrameworkOptions", DropdownOptions.LegalFrameworkUrbanSurveyStatus.values());
        model.addAttribute("legalAmendmentOptions", DropdownOptions.LegalFrameworkAmendmentStatus.values());
        model.addAttribute("slcMeetingOptions", DropdownOptions.SLCMeetingStatus.values());
        model.addAttribute("survyUnitTypeOptions", DropdownOptions.SurveyUnitType.values());
        model.addAttribute("wardLevelShapefileStatusOptions", DropdownOptions.WardLevelShapefileStatus.values());
        model.addAttribute("ulbMasterList", ulbMasterList);

        return "pages/naksha/naksha_data_entry_formNew";
    }

//    @PostMapping("/naksha-mis-data/save")
//    public ResponseEntity<String> saveNakhsaData(
//            @ModelAttribute("stateMISDataEntry") NakshaMISDataEntry nakshaMISDataEntry,
//            @RequestParam(value = "slcMeetingDates", required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            List<LocalDate> slcMeetingDates) {
//
//        try {
//            UserService userService = loogedInUserUtility.getLoggedinUser();
//            User user = userService.getUser();
//
//            List<ULBMaster> ulbMasterList = nakshaMISDataService.findBymuser_id(user.getId());
//            if (ulbMasterList.isEmpty()) {
//                return ResponseEntity.badRequest().body("No ULB found for the user.");
//            }
//
//            ULBMaster ulbMaster = ulbMasterList.get(0);
//            State state = new State();
//            state.setId(ulbMaster.getState_id());
//
//            nakshaMISDataEntry.setUlbMaster(ulbMaster);
//            nakshaMISDataEntry.setState(state);
//            nakshaMISDataEntry.setMuser(user);
//            nakshaMISDataEntry.setUpdateOnDate(new Date());
//
//            boolean conducted = nakshaMISDataEntry.getSlcMeetingConducted() != null &&
//                    nakshaMISDataEntry.getSlcMeetingConducted() == 1;
//
//            nakshaMISDataEntry.setSlcMeetingConducted(conducted ? 1 : 0);
//
//            // Save the parent entry first
//            String message = nakshaMISDataService.save(nakshaMISDataEntry);
//
//            if ("success".equalsIgnoreCase(message)) {
//                if (conducted && slcMeetingDates != null && !slcMeetingDates.isEmpty()) {
//                    for (LocalDate date : slcMeetingDates) {
//                        boolean exists = nakshaMISDataService.existsByEntryAndMeetingDate(nakshaMISDataEntry, date);
//                        if (!exists) {
//                            StateNakshaSLCMeetingDate slcDate = new StateNakshaSLCMeetingDate();
//                            slcDate.setNakshaMISDataEntry(nakshaMISDataEntry);
//                            slcDate.setMeetingDate(date);
//                            nakshaMISDataService.saveDate(slcDate);
//                        }
//                    }
//                }
//
//                // If conducted = No, delete any previously saved meeting dates
//                if (!conducted) {
//                    slcMeetingRepository.deleteAllByEntry(nakshaMISDataEntry);
//
//                    System.out.println("data delete");
//                }
//
//                return ResponseEntity.ok("State ULB data and SLC meeting dates saved successfully.");
//            } else {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("Error while saving MIS data.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An unexpected error occurred. Please contact support.");
//        }
//    }



    //Remove SLC Date Validation -implementation
    @PostMapping("/naksha-mis-data/save")
    public ResponseEntity<String> saveNakhsaData(
            @ModelAttribute("stateMISDataEntry") NakshaMISDataEntry nakshaMISDataEntry,
            @RequestParam(value = "slcMeetingDates", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            List<LocalDate> slcMeetingDates,
            @RequestParam(value = "deletedSlcMeetingDateIds", required = false) List<Long> deletedDateIds,
            @RequestParam(value = "deletedSlcMeetingDateValues", required = false) List<String> deletedDateValues
    ) {

        try {
            UserService userService = loogedInUserUtility.getLoggedinUser();
            User user = userService.getUser();

            List<ULBMaster> ulbMasterList = nakshaMISDataService.findBymuser_id(user.getId());
            if (ulbMasterList.isEmpty()) {
                return ResponseEntity.badRequest().body("No ULB found for the user.");
            }

            ULBMaster ulbMaster = ulbMasterList.get(0);
            State state = new State();
            state.setId(ulbMaster.getState_id());

            nakshaMISDataEntry.setUlbMaster(ulbMaster);
            nakshaMISDataEntry.setState(state);
            nakshaMISDataEntry.setMuser(user);
            nakshaMISDataEntry.setUpdateOnDate(new Date());

            // Check if Yes (1) or No (0) is selected for field [18]
            boolean conducted = nakshaMISDataEntry.getSlcMeetingConducted() != null &&
                    nakshaMISDataEntry.getSlcMeetingConducted() == 1;

            // Set slcMeetingConducted value directly
            nakshaMISDataEntry.setSlcMeetingConducted(
                    nakshaMISDataEntry.getSlcMeetingConducted() != null ? nakshaMISDataEntry.getSlcMeetingConducted() : 0
            );

            // Save the parent entry first
            String message = nakshaMISDataService.save(nakshaMISDataEntry);

            if ("success".equalsIgnoreCase(message)) {
                // 1) Process deletions first (so re-added same dates can be saved cleanly)
                try {
                    // By Ids
                    if (deletedDateIds != null && !deletedDateIds.isEmpty()) {
                        for (Long id : deletedDateIds) {
                            if (id != null) {
                                slcMeetingRepository.deleteById(id);
                            }
                        }
                    }
                    // By date values (yyyy-MM-dd)
                    if (deletedDateValues != null && !deletedDateValues.isEmpty()) {
                        List<StateNakshaSLCMeetingDate> existingDates = slcMeetingRepository.findByNakshaMISDataEntry(nakshaMISDataEntry);
                        for (String dv : deletedDateValues) {
                            if (dv == null || dv.trim().isEmpty()) continue;
                            LocalDate ld = null;
                            try { ld = LocalDate.parse(dv.trim()); } catch (Exception ignore) {}
                            if (ld == null) continue;
                            for (StateNakshaSLCMeetingDate item : existingDates) {
                                if (ld.equals(item.getMeetingDate())) {
                                    slcMeetingRepository.delete(item);
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // 2) Save dates regardless of slcMeetingConducted status (dates are independent)
                if (slcMeetingDates != null && !slcMeetingDates.isEmpty()) {
                    for (LocalDate date : slcMeetingDates) {
                        boolean exists = nakshaMISDataService.existsByEntryAndMeetingDate(nakshaMISDataEntry, date);
                        if (!exists) {
                            StateNakshaSLCMeetingDate slcDate = new StateNakshaSLCMeetingDate();
                            slcDate.setNakshaMISDataEntry(nakshaMISDataEntry);
                            slcDate.setMeetingDate(date);
                            nakshaMISDataService.saveDate(slcDate);
                        }
                    }
                }

                // 3) If "No" is selected, delete any previously saved meeting dates
                if (!conducted) {
                    slcMeetingRepository.deleteAllByEntry(nakshaMISDataEntry);
                    System.out.println("SLC Meeting dates deleted as 'No' was selected");
                }

                return ResponseEntity.ok("State ULB data and SLC meeting dates saved successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error while saving MIS data.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please contact support.");
        }
    }




    @GetMapping("/getUlbsByDistrict/{districtId}")
    @ResponseBody
    public List<Map<String, String>> getUlbsByDistrict(@PathVariable Long districtId) {
        List<ULBMaster> ulbs = nakshaMISDataService.findByDistrictId(districtId);
        return ulbs.stream()
                .map(ulb -> Map.of("id", String.valueOf(ulb.getId()), "name", ulb.getUlb_name()))
                .collect(Collectors.toList());
    }

    @GetMapping("/getNakshaMISData/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUlbDetails(@PathVariable Long id) {
        NakshaMISDataEntry nakshaMISDataEntry = nakshaMISDataService.findByUlbMasterId(id);
        if (nakshaMISDataEntry != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", nakshaMISDataEntry.getId());
            response.put("nodalDepartmentName", nakshaMISDataEntry.getNodalDepartmentName());
            response.put("nodalOfficerName", nakshaMISDataEntry.getNodalOfficerName());
            response.put("phonenumber", nakshaMISDataEntry.getPhonenumber());
            response.put("email", nakshaMISDataEntry.getEmail());
            response.put("slcMeetingConducted", nakshaMISDataEntry.getSlcMeetingConducted());
            response.put("nodalOfficerAppointed", nakshaMISDataEntry.getNodalOfficerAppointed());
            response.put("stateLevelMonitoringCommittee", nakshaMISDataEntry.getStateLevelMonitoringCommittee());
            response.put("totalSPMUPositionsSanctioned", nakshaMISDataEntry.getTotalSPMUPositionsSanctioned());
            response.put("spmuRecruitmentCompleted", nakshaMISDataEntry.getSpmuRecruitmentCompleted());
            response.put("totalProfessionalsRecruited", nakshaMISDataEntry.getTotalProfessionalsRecruited());
            response.put("teamsFormedForsanctioned", nakshaMISDataEntry.getTeamsFormedForsanctioned());
            response.put("teamsFormedForFieldSurvey", nakshaMISDataEntry.getTeamsFormedForFieldSurvey());
            response.put("roversSanctioned", nakshaMISDataEntry.getRoversSanctioned());
            response.put("roversProcuredForFieldSurvey", nakshaMISDataEntry.getRoversProcuredForFieldSurvey());
            response.put("propertyTaxDataObtained", nakshaMISDataEntry.getPropertyTaxDataObtained());
            response.put("propertyTaxDataDigitized", nakshaMISDataEntry.getPropertyTaxDataDigitized());
            response.put("slcMeetingDate", nakshaMISDataEntry.getSlcMeetingDate());
            response.put("slcStatus", nakshaMISDataEntry.getSlcStatus());
            response.put("updateOnDate", nakshaMISDataEntry.getUpdateOnDate());
            response.put("wardLevelShapefileStatus", nakshaMISDataEntry.getWardLevelShapefileStatus());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }



}
