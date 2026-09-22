package in.gov.dilrmp.services.naksha;
import in.gov.dilrmp.models.naksha.*;
import in.gov.dilrmp.repositories.naksha.NakshaMISDataRepository;
import in.gov.dilrmp.repositories.naksha.NakshaMISDataVenderRepository;
import in.gov.dilrmp.repositories.naksha.SLCMeetingRepository;
import in.gov.dilrmp.repositories.naksha.ULBMsterRepository;

import in.gov.dilrmp.utils.DropdownOptions;
import in.gov.dilrmp.utils.IECActivityDropdowns;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NakshaMISDataService {


    @Autowired
    private NakshaMISDataRepository nakshaMISDataRepository;

    @Autowired
    NakshaMISDataVenderRepository misDataVenderRepository;


    @Autowired
    SLCMeetingRepository slcMeetingRepository;

    @Autowired
    ULBMsterRepository ulbMsterRepository;
    @Autowired
    ULBMasterService ulbMasterService;

    @Autowired
    GroundTruthingDetailService groundTruthingDetailService;
    @Autowired
    IecActivityService iecActivityService;
    @Autowired
    private CapacityBuildingService buildingService;

    private List<NakshaMISDataEntry> vendor;

    public void loadMisVendorData() {
        vendor = nakshaMISDataRepository.findAll();
    }

    public List<NakshaMISDataEntry> getAllEntries() {
        return vendor;
    }

   /* public Map<String, Integer> getStateMisDetailsByStateId(Long stateId) {
        // Load NakshaMISDataEntry list
        Stream<NakshaMISDataEntry> stream = vendor.stream();

        // Load ULB list
        List<ULBMaster> ulbList = ulbMasterService.getAllEntries();

        if (stateId != null) {
            // ✅ Filter MIS data by stateId
            stream = stream.filter(e -> e.getState() != null && e.getState().getId().equals(stateId));

            // ✅ Filter ULB list by stateId
            ulbList = ulbList.stream()
                    .filter(u -> u.getState_id() != null && u.getState_id().equals(stateId))
                    .collect(Collectors.toList());
        }

        // ✅ Reduce to sums
        int roversSanctioned = stream.mapToInt(e -> e.getRoversSanctioned() != null ? e.getRoversSanctioned() : 0).sum();
        int roversProcured = vendor.stream()
                .filter(e -> stateId == null || (e.getState() != null && e.getState().getId().equals(stateId)))
                .mapToInt(e -> e.getRoversProcuredForFieldSurvey() != null ? e.getRoversProcuredForFieldSurvey() : 0)
                .sum();
        int totalSPMU = vendor.stream()
                .filter(e -> stateId == null || (e.getState() != null && e.getState().getId().equals(stateId)))
                .mapToInt(e -> e.getTotalSPMUPositionsSanctioned() != null ? e.getTotalSPMUPositionsSanctioned() : 0)
                .sum();
        int professionals = vendor.stream()
                .filter(e -> stateId == null || (e.getState() != null && e.getState().getId().equals(stateId)))
                .mapToInt(e -> e.getTotalProfessionalsRecruited() != null ? e.getTotalProfessionalsRecruited() : 0)
                .sum();
        int teamsSanctioned = vendor.stream()
                .filter(e -> stateId == null || (e.getState() != null && e.getState().getId().equals(stateId)))
                .mapToInt(e -> e.getTeamsFormedForsanctioned() != null ? e.getTeamsFormedForsanctioned() : 0)
                .sum();
        int teamsField = vendor.stream()
                .filter(e -> stateId == null || (e.getState() != null && e.getState().getId().equals(stateId)))
                .mapToInt(e -> e.getTeamsFormedForFieldSurvey() != null ? e.getTeamsFormedForFieldSurvey() : 0)
                .sum();

        // ✅ Count distinct ULBs
        long ulbCount = ulbList.stream()
                .map(ULBMaster::getUlb_name)  // extract name
                .filter(Objects::nonNull)    // skip nulls
                .distinct()                  // only unique names
                .count();

        // ✅ Put into response map
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("roversSanctioned", roversSanctioned);
        result.put("roversProcuredForFieldSurvey", roversProcured);
        result.put("totalSPMUPositionsSanctioned", totalSPMU);
        result.put("totalProfessionalsRecruited", professionals);
        result.put("teamsFormedForsanctioned", teamsSanctioned);
        result.put("teamsFormedForFieldSurvey", teamsField);
        result.put("ulbCount", (int) ulbCount);  // 👈 Added ULB count

        return result;
    }*/

    public Map<String, Object> getStateMisDetails(Long stateId) {
        // ✅ Start with vendor list
        List<NakshaMISDataEntry> misEntries = vendor;

        // ✅ Filter by state if stateId is provided
        if (stateId != null) {
            misEntries = misEntries.stream()
                    .filter(e -> e.getState() != null && e.getState().getId().equals(stateId))
                    .collect(Collectors.toList());
        }

        // Load ULB list
        List<ULBMaster> ulbList = ulbMasterService.getAllEntries();
        if (stateId != null) {
            // ✅ Filter ULB list by stateId
            ulbList = ulbList.stream()
                    .filter(u -> u.getState_id() != null && u.getState_id().equals(stateId))
                    .collect(Collectors.toList());
        }

        // ✅ Group entries by State (skip null states just in case)
        Map<String, List<NakshaMISDataEntry>> groupedByState = misEntries.stream()
                .filter(e -> e.getState() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getState().getName(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Map.Entry<String, List<NakshaMISDataEntry>> entry : groupedByState.entrySet()) {
            String stateName = entry.getKey();
            List<NakshaMISDataEntry> stateEntries = entry.getValue();

            Map<String, Object> row = new LinkedHashMap<>();
            if (stateName != null && !stateName.isEmpty()) {
                stateName = stateName.substring(0, 1).toUpperCase() + stateName.substring(1).toLowerCase();
            }
            row.put("stateName", stateName);

            // ✅ ULB Count (distinct by name)
            long ulbCount = ulbList.stream()
                    .filter(u -> u.getState_id() != null && u.getState_id().equals(stateEntries.get(0).getState().getId()))
                    .map(ULBMaster::getUlb_name)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count();
            row.put("ulbCount", ulbCount);

            // ✅ Aggregate values safely (summing up fields)
            int roversSanctioned = stateEntries.stream()
                    .mapToInt(e -> e.getRoversSanctioned() != null ? e.getRoversSanctioned() : 0)
                    .sum();
            int roversProcured = stateEntries.stream()
                    .mapToInt(e -> e.getRoversProcuredForFieldSurvey() != null ? e.getRoversProcuredForFieldSurvey() : 0)
                    .sum();
            int totalSPMU = stateEntries.stream()
                    .mapToInt(e -> e.getTotalSPMUPositionsSanctioned() != null ? e.getTotalSPMUPositionsSanctioned() : 0)
                    .sum();
            int professionals = stateEntries.stream()
                    .mapToInt(e -> e.getTotalProfessionalsRecruited() != null ? e.getTotalProfessionalsRecruited() : 0)
                    .sum();
            int teamsSanctioned = stateEntries.stream()
                    .mapToInt(e -> e.getTeamsFormedForsanctioned() != null ? e.getTeamsFormedForsanctioned() : 0)
                    .sum();
            int teamsField = stateEntries.stream()
                    .mapToInt(e -> e.getTeamsFormedForFieldSurvey() != null ? e.getTeamsFormedForFieldSurvey() : 0)
                    .sum();

            String nodalDepartment = stateEntries.stream()
                    .map(NakshaMISDataEntry::getNodalDepartmentName)   // extract department name
                    .filter(Objects::nonNull)                          // skip nulls
                    .distinct()                                        // keep only unique names
                    .findFirst()                                       // pick first available
                    .orElse("N/A");                                       // default if none

            String nodalOfficer = stateEntries.stream()
                    .map(NakshaMISDataEntry::getNodalOfficerName)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("N/A");



            row.put("nodalDepartment", nodalDepartment);
            row.put("nodalOfficer", nodalOfficer);

            row.put("roversSanctioned", roversSanctioned);
            row.put("roversProcured", roversProcured);
            row.put("totalSPMU", totalSPMU);
            row.put("professionals", professionals);
            row.put("teamsSanctioned", teamsSanctioned);
            row.put("teamsField", teamsField);

            resultList.add(row);
        }

        // ✅ Final response with total states and details
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalStates", groupedByState.size());
        response.put("details", resultList);

        return response;
    }







    public String save(NakshaMISDataEntry nakshaMISDataEntry) {
        try {
            // Attempt to save the data
            nakshaMISDataRepository.save(nakshaMISDataEntry);
            return "success";  // Return success if no exception occurs
        } catch (Exception e) {
            // Log the exception (you can use a logger or print the stack trace for debugging)
            e.printStackTrace();  // or use: logger.error("Error while saving data", e);
            return "failure";  // Return failure or an appropriate error message
        }
    }


   /* public Optional<NakshaMISDataEntry> findByState(State state) {
        return nakshaMISDataRepository.findByState(state);
    }*/

    public NakshaMISDataEntry findByUlbMasterId(Long ulbMasterID) {
        return nakshaMISDataRepository.findByUlbMasterId(ulbMasterID);
    }

    public NakshaMISDataEntry findByUlbMuserId(Long ulbMasterID) {
        return nakshaMISDataRepository.findByUlbMuserId(ulbMasterID);
    }

    public List<ULBMaster> findBymuser_id(Long userId) {
        return ulbMsterRepository.findBymuser_id(userId);
    }



    public ULBMaster findBymid(Long userId) {
        return ulbMsterRepository.findBymid(userId);
    }


    public String saveVerder(NakshaMISDataEntrySolrAndVender nakshaMISDataEntrySolrAndVender) {
        try {
            // Attempt to fetch ULBMaster by ID
            Optional<ULBMaster> ulbMaster = getUlbMasterByUlbMasterId(nakshaMISDataEntrySolrAndVender.getUlbMasterId());
            NakshaMISDataEntrySolrAndVender solrAndVender =
                    misDataVenderRepository.getVenderByUlbMasterId(nakshaMISDataEntrySolrAndVender.getUlbMasterId());

            String technology = nakshaMISDataEntrySolrAndVender.getTechnology();

            if ("Tech 1".equals(technology)) {
                if (nakshaMISDataEntrySolrAndVender.getTechStatus() == null) {
                    Integer value = (solrAndVender != null && solrAndVender.getTechStatus() != null)
                            ? solrAndVender.getTechStatus()
                            : 1;
                    nakshaMISDataEntrySolrAndVender.setTechStatus(value);
                }
            } else if ("Tech 2".equals(technology)) {
                if (nakshaMISDataEntrySolrAndVender.getTech2ObliqueStatus() == null) {
                    Integer value = (solrAndVender != null && solrAndVender.getTech2ObliqueStatus() != null)
                            ? solrAndVender.getTech2ObliqueStatus()
                            : 1;
                    nakshaMISDataEntrySolrAndVender.setTech2ObliqueStatus(value);
                }
            } else if ("Tech 3".equals(technology)) {
                if (nakshaMISDataEntrySolrAndVender.getTech3ObliqueStatus() == null) {
                    Integer value = (solrAndVender != null && solrAndVender.getTech3ObliqueStatus() != null)
                            ? solrAndVender.getTech3ObliqueStatus()
                            : 1;
                    nakshaMISDataEntrySolrAndVender.setTech3ObliqueStatus(value);
                }
            }







            // If ULBMaster is not present, throw an exception
            ULBMaster master = ulbMaster.orElseThrow(() ->
                    new IllegalArgumentException("ULBMaster not found for ID: " + nakshaMISDataEntrySolrAndVender.getUlbMasterId()));

            // Populate fields from ULBMaster
            nakshaMISDataEntrySolrAndVender.setStateName(master.getState_name());
            nakshaMISDataEntrySolrAndVender.setDistrict_Name(master.getDistrict_name());
            nakshaMISDataEntrySolrAndVender.setGdID(master.getGdID());
            nakshaMISDataEntrySolrAndVender.setGdName(master.getGdName());
            nakshaMISDataEntrySolrAndVender.setUlbName(master.getUlb_name());
            nakshaMISDataEntrySolrAndVender.setZoneName(master.getZoneName());

            // Save the data
            misDataVenderRepository.save(nakshaMISDataEntrySolrAndVender);

            return "success";  // Return success if everything is fine
        } catch (IllegalArgumentException e) {
            // Log and return specific error for missing ULBMaster

            return "failure";
        } catch (Exception e) {
            // Log and handle generic exceptions
            return "failure";
        }
    }


    public NakshaMISDataEntrySolrAndVender getVenderByUlbMasterId(Long ulbMasterId) {
        return misDataVenderRepository.getVenderByUlbMasterId(ulbMasterId);
    }


    public void saveDate(StateNakshaSLCMeetingDate meetingDate) {
        slcMeetingRepository.save(meetingDate);
    }

    public boolean existsByEntryAndMeetingDate(NakshaMISDataEntry entry, LocalDate meetingDate) {
        return slcMeetingRepository.existsByNakshaMISDataEntryAndMeetingDate(entry, meetingDate);
    }

//    public void deleteMeetingDatesByEntry(Long id) {
//        List<StateNakshaSLCMeetingDate> dates = slcMeetingRepository.deleteAllById(id);
//    }


    public List<ULBMaster> findByDistrictId(Long districtId) {
        return ulbMsterRepository.findULBsByDistrictId(districtId);
    }


    public List<NakshaMisStateDTO> findStatesByGdID(Integer zoneID) {
        List<Object[]> results = ulbMsterRepository.findStatesByGdID(zoneID);
        return results.stream()
                .map(result -> new NakshaMisStateDTO((Long) result[0], (String) result[1]))
                .collect(Collectors.toList());
    }

    public List<NakshaMisStateDTO> getDistrictsByStateId(Long stateId) {
        List<Object[]> results = ulbMsterRepository.getDistrictsByStateId(stateId);
        return results.stream()
                .map(result -> new NakshaMisStateDTO((String) result[1],(Long) result[0]))
                .collect(Collectors.toList());
    }

    public List<NakshaMisStateDTO> getUlbNameByDistrictId(Long stateId) {
        List<Object[]> results = ulbMsterRepository.getUlbNameByDistrictId(stateId);
        return results.stream()
                .map(result -> new NakshaMisStateDTO((Long) result[0], (String) result[1],(String) result[1]))
                .collect(Collectors.toList());
    }

    public String getGdNameByZoneId(Integer gdId){
        return ulbMsterRepository.getGdNameByZoneId(gdId);
    }

    public String getGdNamezoneByZoneId(Integer gdId){
        return ulbMsterRepository.getzoneNameZoneId(gdId);
    }

    public Optional<ULBMaster> getUlbMasterByUlbMasterId(Long ulbMasterId) {
        return ulbMsterRepository.getUlbMasterByUlbMasterId(ulbMasterId);
    }


    public List<NakshaMISDataEntrySolrAndVender> getFilteredData(Long stateId, Long districtId, Long ulbMasterId) {
        if (ulbMasterId != null) {
            return misDataVenderRepository.getUlbMasterByUlbMasterId(ulbMasterId);
        } else if (districtId != null) {
            List<Long> ulbIds = ulbMsterRepository.findULBsByDistrictId(districtId)
                    .stream()
                    .map(ULBMaster::getId)
                    .collect(Collectors.toList());
            return ulbIds.isEmpty() ? Collections.emptyList() : misDataVenderRepository.findByUlbMasterIdIn(ulbIds);
        } else if (stateId != null) {
            List<Long> ulbIds = ulbMsterRepository.findByStateId(stateId)
                    .stream()
                    .map(ULBMaster::getId)
                    .collect(Collectors.toList());
            return ulbIds.isEmpty() ? Collections.emptyList() : misDataVenderRepository.findByUlbMasterIdIn(ulbIds);
        }
        return Collections.emptyList();
    }

    public List<NakshaMisStateDTO> findStates() {
        return ulbMsterRepository.findAll().stream()
                .map(result -> new NakshaMisStateDTO(result.getState_id(), result.getState_name()))
                .distinct() // Ensures unique entries
                .sorted(Comparator.comparing(NakshaMisStateDTO::getStateName)) // Sort by state_name
                .collect(Collectors.toList());
    }

    public List<NakshaMISDataEntry> getReportsGrandTotal() {
        List<NakshaMISDataEntry> list = nakshaMISDataRepository.findAll();
        if (list == null) {
            list = new ArrayList<>();
        }
        int grandTotalSPMUPositionsSanctioned = list.stream()
                .mapToInt(entry -> entry.getTotalSPMUPositionsSanctioned() != null ? entry.getTotalSPMUPositionsSanctioned() : 0)
                .sum();

        int grandTotalProfessionalsRecruited = list.stream()
                .mapToInt(entry -> entry.getTotalProfessionalsRecruited() != null ? entry.getTotalProfessionalsRecruited() : 0)
                .sum();

        int grandTotalTeamsFormedForSanctioned = list.stream()
                .mapToInt(entry -> entry.getTeamsFormedForsanctioned() != null ? entry.getTeamsFormedForsanctioned() : 0)
                .sum();

        int grandTotalRoversSanctioned = list.stream()
                .mapToInt(entry -> entry.getRoversSanctioned() != null ? entry.getRoversSanctioned() : 0)
                .sum();

        int grandTotalTeamsFormedForFieldSurvey = list.stream()
                .mapToInt(entry -> entry.getTeamsFormedForFieldSurvey() != null ? entry.getTeamsFormedForFieldSurvey() : 0)
                .sum();

        int grandTotalRoversProcuredForFieldSurvey = list.stream()
                .mapToInt(entry -> entry.getRoversProcuredForFieldSurvey() != null ? entry.getRoversProcuredForFieldSurvey() : 0)
                .sum();

        int grandtotalulbsGroundTruthingCompleted = list.stream()
                .mapToInt(entry -> entry.getUlbsGroundTruthingCompleted() != null ? entry.getUlbsGroundTruthingCompleted() : 0)
                .sum();

        int grandtotalUrProCardIssued = list.stream()
                .mapToInt(entry -> entry.getTotalUrProCardIssued() != null ? entry.getTotalUrProCardIssued() : 0)
                .sum();

        int grandtotaltotalPlotsToSurvey = list.stream()
                .mapToInt(entry -> entry.getTotalPlotsToSurvey() != null ? entry.getTotalPlotsToSurvey() : 0)
                .sum();
        int grandtotalplotsSurveyedCompleted = list.stream()
                .mapToInt(entry -> entry.getPlotsSurveyedCompleted() != null ? entry.getPlotsSurveyedCompleted() : 0)
                .sum();

        // Map the entries to add the grand total values
        list = list.stream()
                .map(entry -> {
                    entry.setGrandTotalSPMUPositionsSanctioned(grandTotalSPMUPositionsSanctioned);
                    entry.setGrandtotalProfessionalsRecruited(grandTotalProfessionalsRecruited);
                    entry.setGrandtotalteamsFormedForsanctioned(grandTotalTeamsFormedForSanctioned);
                    entry.setGrandtotalroversSanctioned(grandTotalRoversSanctioned);
                    entry.setGrandtotalteamsFormedForFieldSurvey(grandTotalTeamsFormedForFieldSurvey);
                    entry.setGrandtotalroversProcuredForFieldSurvey(grandTotalRoversProcuredForFieldSurvey);
                    entry.setGrandtotalulbsGroundTruthingCompleted(grandtotalulbsGroundTruthingCompleted);
                    entry.setGrandtotalUrProCardIssued(grandtotalUrProCardIssued);
                    entry.setGrandtotaltotalPlotsToSurvey(grandtotaltotalPlotsToSurvey);
                    entry.setGrandtotalplotsSurveyedCompleted(grandtotalplotsSurveyedCompleted);
                    return entry;
                })
                .collect(Collectors.toList());

        return list;
    }

    public List<NakshaMISDataEntrySolrAndVender> soiDataProcessingGrandTotal() {
        List<NakshaMISDataEntrySolrAndVender> list = misDataVenderRepository.findAll();
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        double grandTotalSeparateOriDataProcessingCumulative = list.stream()
                .mapToDouble(entry -> parseDoubleOrZero(entry.getSeparateOriDataProcessingCumulative()))
                .sum();

        double grandTotalDemDataProcessingCumulative = list.stream()
                .mapToDouble(entry -> parseDoubleOrZero(entry.getDemDataProcessingCumulative()))
                .sum();

        double grandTotalDsmDataProcessingCumulative = list.stream()
                .mapToDouble(entry -> parseDoubleOrZero(entry.getDsmDataProcessingCumulative()))
                .sum();

        double grandTotalDtmDataProcessingCumulative = list.stream()
                .mapToDouble(entry -> parseDoubleOrZero(entry.getDtmDataProcessingCumulative()))
                .sum();

        double grandTotalMeshDataProcessingCumulative = list.stream()
                .mapToDouble(entry -> parseDoubleOrZero(entry.getMeshDataProcessingCumulative()))
                .sum();

        double grandTotalSeparateOriQaqcDataProcessingCumulative = list.stream()
                .mapToDouble(entry -> parseDoubleOrZero(entry.getSeparateOriQaqcDataProcessingCumulative()))
                .sum();

        double grandTotalDemDataProcessingQaqcCumulative = list.stream()
                .mapToDouble(entry -> parseDoubleOrZero(entry.getDemDataProcessingQaqcCumulative()))
                .sum();

        double grandTotalDsmDataProcessingQaqcCumulative = list.stream()
                .mapToDouble(entry -> parseDoubleOrZero(entry.getDsmDataProcessingQaqcCumulative()))
                .sum();

        double grandTotalDtmDataProcessingQaqcCumulative = list.stream()
                .mapToDouble(entry -> parseDoubleOrZero(entry.getDtmDataProcessingQaqcCumulative()))
                .sum();

        double grandTotalMeshDataProcessingQaqcCumulative = list.stream()
                .mapToDouble(entry -> parseDoubleOrZero(entry.getMeshDataProcessingQaqcCumulative()))
                .sum();

        double grandTotalFeatureExtractionOriCumulativeCompleted = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getFeatureExtractionOriCumulativeCompleted()).orElse(0.0))
                .sum();

        double grandTotalFeatureExtractionOriQaqcCumulativeCompleted = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getFeatureExtractionOriQaqcCumulativeCompleted()).orElse(0.0))
                .sum();

        double grandTotalMeshFeatureExtractionCumulativeCompleted = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getMeshFeatureExtractionCumulativeCompleted()).orElse(0.0))
                .sum();

        double grandTotalMeshFeatureExtractionQaqcCumulativeCompleted = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getMeshFeatureExtractionQaqcCumulativeCompleted()).orElse(0.0))
                .sum();

        int grandTotalTotalGC = list.stream()
                .mapToInt(entry -> parseIntOrZero(entry.getTotalGC()))
                .sum();

        double grandTotaltwoDFeatextrStereomodeCumulative = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getTwoDFeatextrStereomodeCumulative()).orElse(0.0))
                .sum();

        double grandTotaltwoDFeatextrStereomodeCumulativeQaQc = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getTwoDFeatextrStereomodeCumulativeQaQc()).orElse(0.0))
                .sum();


        double grandTotalthreeDFeatextrCumulative = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getThreeDFeatextrCumulative()).orElse(0.0))
                .sum();

        double grandTotalthreeDFeatextrCumulativeQaQc = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getThreeDFeatextrCumulativeQaQc()).orElse(0.0))
                .sum();



        list.forEach(entry -> {
            entry.setGrandTotalSeparateOriDataProcessingCumulative(grandTotalSeparateOriDataProcessingCumulative);
            entry.setGrandTotalDemDataProcessingCumulative(grandTotalDemDataProcessingCumulative);
            entry.setGrandTotalDsmDataProcessingCumulative(grandTotalDsmDataProcessingCumulative);
            entry.setGrandTotalDtmDataProcessingCumulative(grandTotalDtmDataProcessingCumulative);
            entry.setGrandTotalMeshDataProcessingCumulative(grandTotalMeshDataProcessingCumulative);

            entry.setGrandTotalSeparateOriQaqcDataProcessingCumulative(grandTotalSeparateOriQaqcDataProcessingCumulative);
            entry.setGrandTotalDemDataProcessingQaqcCumulative(grandTotalDemDataProcessingQaqcCumulative);
            entry.setGrandTotalDsmDataProcessingQaqcCumulative(grandTotalDsmDataProcessingQaqcCumulative);
            entry.setGrandTotalDtmDataProcessingQaqcCumulative(grandTotalDtmDataProcessingQaqcCumulative);
            entry.setGrandTotalMeshDataProcessingQaqcCumulative(grandTotalMeshDataProcessingQaqcCumulative);

            entry.setGrandTotalFeatureExtractionOriCumulativeCompleted(grandTotalFeatureExtractionOriCumulativeCompleted);
            entry.setGrandTotalFeatureExtractionOriQaqcCumulativeCompleted(grandTotalFeatureExtractionOriQaqcCumulativeCompleted);
            entry.setGrandTotalMeshFeatureExtractionCumulativeCompleted(grandTotalMeshFeatureExtractionCumulativeCompleted);
            entry.setGrandTotalMeshFeatureExtractionQaqcCumulativeCompleted(grandTotalMeshFeatureExtractionQaqcCumulativeCompleted);

            entry.setGrandTotaltwoDFeatextrStereomodeCumulative(grandTotaltwoDFeatextrStereomodeCumulative);
            entry.setGrandTotaltwoDFeatextrStereomodeCumulativeQaQc(grandTotaltwoDFeatextrStereomodeCumulativeQaQc);
            entry.setGrandTotalthreeDFeatextrCumulative(grandTotalthreeDFeatextrCumulative);
            entry.setGrandTotalthreeDFeatextrCumulativeQaQc(grandTotalthreeDFeatextrCumulativeQaQc);

            entry.setGrandTotalTotalGC(grandTotalTotalGC);
        });

        return list;
    }


    public List<NakshaMISDataEntrySolrAndVender> soiGrandTotal() {
        List<NakshaMISDataEntrySolrAndVender> list = misDataVenderRepository.findAll();
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        double grandTotalSanctionedArea = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getSanctionedArea()).orElse(0.0))
                .sum();


        double grandTotalBufferAreaDataAcquisition = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getBufferAreaDataAcquisition()).orElse(0.0))
                .sum();

        int grandTotalTotalGrids = list.stream()
                .mapToInt(entry -> parseIntOrZero(entry.getTotalGrids()))
                .sum();

        int grandTotalGridsCompletedLastWeek = list.stream()
                .mapToInt(entry -> parseIntOrZero(entry.getGridsCompletedLastWeek()))
                .sum();

        int grandTotalTotalGridsCompleted = list.stream()
                .mapToInt(entry -> parseIntOrZero(entry.getTotalGridsCompleted()))
                .sum();

        double grandTotalTech1Cumulative = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getTech1Cumulative()).orElse(0.0))
                .sum();

        double grandTotalTech2Cumulative = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getTech2Cumulative()).orElse(0.0))
                .sum();

        double grandTotalTech3Cumulative = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getTech3Cumulative()).orElse(0.0))
                .sum();

        double grandTotaltech3LidarSensor = list.stream()
                .mapToDouble(entry -> Optional.ofNullable(entry.getTech3LidarSensor()).orElse(0.0))
                .sum();

        // Assign grand totals to one entry (can also create a separate "summary" object if needed)
        list.forEach(entry -> {
            entry.setGrandTotalSanctionedArea(grandTotalSanctionedArea);
            entry.setGrandTotalBufferAreaDataAcquisition(grandTotalBufferAreaDataAcquisition);
            entry.setGrandTotalTech1Cumulative(grandTotalTech1Cumulative);
            entry.setGrandTotalTech2Cumulative(grandTotalTech2Cumulative);
            entry.setGrandTotalTech3Cumulative(grandTotalTech3Cumulative);
            entry.setGrandTotalTotalGrids(grandTotalTotalGrids);
            entry.setGrandTotalGridsCompletedLastWeek(grandTotalGridsCompletedLastWeek);
            entry.setGrandTotalTotalGridsCompleted(grandTotalTotalGridsCompleted);
            entry.setGrandTotaltech3LidarSensor(grandTotaltech3LidarSensor);
        });

        return list;
    }

    private int parseIntOrZero(String value) {
        try {
            return value != null ? Integer.parseInt(value.trim()) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseDoubleOrZero(String value) {
        try {
            return value != null ? Double.parseDouble(value.trim()) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public List<String> getDistinctStateNames() {
        return misDataVenderRepository.findDistinctStateNames();
    }
    public List<String> getGdNamesByState(String stateName) {
        try {
            return misDataVenderRepository.findGdNameByStateNames(stateName);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }


    public Map<String, Map<String, String>> getBarChartData(String ulbName) {
        NakshaMISDataEntrySolrAndVender entry = misDataVenderRepository.findByUlbName(ulbName);
        if (entry == null) return Collections.emptyMap();

        Map<String, Map<String, String>> chartData = new LinkedHashMap<>();

        // -------------------- Cumulative Completed (Tech) --------------------
        Map<String, String> techMap = new LinkedHashMap<>();
        techMap.put("Tech 1", entry.getTech1Cumulative() != null ? entry.getTech1Cumulative().toString() : "0");
        techMap.put("Tech 2", entry.getTech2Cumulative() != null ? entry.getTech2Cumulative().toString() : "0");
        techMap.put("Tech 3", entry.getTech3Cumulative() != null ? entry.getTech3Cumulative().toString() : "0");
        chartData.put("Cumulative Completed (Tech)", techMap);

        // -------------------- Grid Completion --------------------
        Map<String, String> gridMap = new LinkedHashMap<>();
        gridMap.put("Total Grids", getValueOrZero(entry.getTotalGrids()));
        gridMap.put("Grids Completed Last Week", getValueOrZero(entry.getGridsCompletedLastWeek()));
        gridMap.put("Total Grids Completed", getValueOrZero(entry.getTotalGridsCompleted()));
        chartData.put("Grid Completion", gridMap);

        // -------------------- Data Acquisition (ORI) --------------------
        Map<String, String> oriMap = new LinkedHashMap<>();
        oriMap.put("Last Week", getValueOrZero(entry.getSeparateOriDataProcessingLastWeek()));
        oriMap.put("Cumulative", getValueOrZero(entry.getSeparateOriDataProcessingCumulative()));
        chartData.put("Data Acquisition (ORI)", oriMap);

        // -------------------- Data Acquisition (DSM) --------------------
        Map<String, String> demMap = new LinkedHashMap<>();
        demMap.put("Last Week", getValueOrZero(entry.getDsmDataProcessingLastWeek()));
        demMap.put("Cumulative", getValueOrZero(entry.getDsmDataProcessingCumulative()));
        chartData.put("Data Acquisition (DEM/DSM/DTM)", demMap);

        // -------------------- Data Acquisition (3D Mesh Model) --------------------
        Map<String, String> meshMap = new LinkedHashMap<>();
        meshMap.put("Last Week", getValueOrZero(entry.getMeshDataProcessingLastWeek()));
        meshMap.put("Cumulative", getValueOrZero(entry.getMeshDataProcessingCumulative()));
        chartData.put("Data Acquisition (3D Mesh Model)", meshMap);

        // -------------------- Data Acquisition (QA/QC) --------------------
        Map<String, String> qaqcMap = new LinkedHashMap<>();
        qaqcMap.put("Last Week", getValueOrZero(entry.getMeshDataProcessingQaqcLastWeek()));
        qaqcMap.put("Cumulative", getValueOrZero(entry.getMeshDataProcessingQaqcCumulative()));
        chartData.put("Data Acquisition (QA/QC)", qaqcMap);

        return chartData;
    }

    private String getValueOrZero(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "0";
    }



    public Map<String, Object> getSummary() {
        try {
            List<Object[]> resultList = misDataVenderRepository.getNakshaVenderSummaryJPQL();

            Object[] result = resultList.get(0); // only one row expected

            Map<String, Object> summary = new HashMap<>();
            summary.put("distinctStates", ((Number) result[0]).longValue());
            summary.put("distinctUlbs", ((Number) result[1]).longValue());
            summary.put("totalBufferAreaDataAcquisition", result[2] != null ? ((Number) result[2]).doubleValue() : 0.0);
            summary.put("totalSanctionedArea", result[3] != null ? ((Number) result[3]).doubleValue() : 0.0);

            return summary;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public Map<String, Object> getRoversSummary(Long stateId) {
        try {
            List<Object[]> resultList = nakshaMISDataRepository.getRoversSummaryJPQL(stateId);
            Object[] ulbSummary = ulbMsterRepository.getULBSummary().get(0);

            if (resultList.isEmpty()) {
                return Collections.emptyMap();
            }

            Object[] result = resultList.get(0); // single row expected

            Map<String, Object> summary = new HashMap<>();
           /* summary.put("totalStates", result[0] != null ? ((Number) result[0]).intValue() : 0);
            summary.put("totalRoversSanctioned", result[1] != null ? ((Number) result[1]).intValue() : 0);
            summary.put("totalRoversProcured", result[2] != null ? ((Number) result[2]).intValue() : 0);
            summary.put("totalSPMUPositionsSanctioned", result[3] != null ? ((Number) result[3]).intValue() : 0);
            summary.put("totalProfessionalsRecruited", result[4] != null ? ((Number) result[4]).intValue() : 0);
            summary.put("teamsFormedForsanctioned", result[5] != null ? ((Number) result[5]).intValue() : 0);
            summary.put("teamsFormedForFieldSurvey", result[6] != null ? ((Number) result[6]).intValue() : 0);
            summary.put("totalUlb", ulbSummary[0] != null ? ((Number) ulbSummary[0]).intValue() : 0);*/
            summary.put("totalStates", result[0] != null ? ((Number) result[0]).intValue() : 0);


            summary.put("totalRoversSanctioned",
                    (result[2] != null ? ((Number) result[2]).intValue() : 0) + "/" +
                            (result[1] != null ? ((Number) result[1]).intValue() : 0)
            );

            summary.put("totalSPMUPositionsSanctioned",
                    (result[4] != null ? ((Number) result[4]).intValue() : 0) + "/" +
                            (result[3] != null ? ((Number) result[3]).intValue() : 0)
            );

            summary.put("teamsFormedForsanctioned",
                    (result[6] != null ? ((Number) result[6]).intValue() : 0) + "/" +
                            (result[5] != null ? ((Number) result[5]).intValue() : 0)
            );

            summary.put("totalUlb", ulbSummary[0] != null ? ((Number) ulbSummary[0]).intValue() : 0);


            return summary;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public List<Map<String, Object>> getAllStatesRoversSummarySortedByMetric(String metric) {
        List<Object[]> results = nakshaMISDataRepository.getRoversSummaryJPQL(metric);

        // Convert to Map for easier sorting & returning
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("stateName", row[0] != null ? (String) row[0] : "");
            map.put("roversSanctioned", row[1] != null ? ((Number) row[1]).intValue() : 0);
            map.put("roversProcured", row[2] != null ? ((Number) row[2]).intValue() : 0);
            map.put("totalSPMUPositionsSanctioned", row[3] != null ? ((Number) row[3]).intValue() : 0);
            map.put("totalProfessionalsRecruited", row[4] != null ? ((Number) row[4]).intValue() : 0);
            map.put("teamsFormedForsanctioned", row[5] != null ? ((Number) row[5]).intValue() : 0);
            map.put("teamsFormedForFieldSurvey", row[6] != null ? ((Number) row[6]).intValue() : 0);
            list.add(map);
        }

        // Sort descending by metric
        list.sort((a, b) -> Integer.compare(
                (int) b.getOrDefault(metric, 0),
                (int) a.getOrDefault(metric, 0)
        ));

        return list;
    }






    public List<Object[]> getStateIdAndStateName() {
        try {
            return ulbMsterRepository.findStateIdAndStateName();
        } catch (Exception e) {

            return Collections.emptyList();
        }
    }

   /* public List<NakshaMISReportDTO> getNakshaMISReport() {
        return nakshaMISDataRepository.fetchNakshaMISReport();
    }*/

    public List<Map<String, Object>> getNakshaMISReport() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

        try {
            // Iterate over each vendor object
            for (NakshaMISDataEntry vendorData : vendor) {
                Map<String, Object> row = new HashMap<>();

                row.put("stateId", vendorData.getState().getId());
                row.put("state_name", vendorData.getState().getName());
                row.put("spmuRecruitmentCompleted",
                        vendorData.getSpmuRecruitmentCompleted() != null ? vendorData.getSpmuRecruitmentCompleted() : 0);
                row.put("totalSPMUPositionsSanctioned",
                        vendorData.getTotalSPMUPositionsSanctioned() != null ? vendorData.getTotalSPMUPositionsSanctioned() : 0);
                row.put("totalProfessionalsRecruited",
                        vendorData.getTotalProfessionalsRecruited() != null ? vendorData.getTotalProfessionalsRecruited() : 0);
                row.put("teamsFormedForsanctioned",
                        vendorData.getTeamsFormedForsanctioned() != null ? vendorData.getTeamsFormedForsanctioned() : 0);
                row.put("teamsFormedForFieldSurvey",
                        vendorData.getTeamsFormedForFieldSurvey() != null ? vendorData.getTeamsFormedForFieldSurvey() : 0);
                row.put("roversSanctioned",
                        vendorData.getRoversSanctioned() != null ? vendorData.getRoversSanctioned() : 0);
                row.put("roversProcuredForFieldSurvey",
                        vendorData.getRoversProcuredForFieldSurvey() != null ? vendorData.getRoversProcuredForFieldSurvey() : 0);
                row.put("slcMeetingConducted",
                        vendorData.getSlcMeetingConducted() != null ? vendorData.getSlcMeetingConducted() : "No");

                // Format date if not null
                if (vendorData.getSlcMeetingDate() != null) {
                    row.put("slcMeetingDate", vendorData.getSlcMeetingDate().format(formatter));
                } else {
                    row.put("slcMeetingDate", "-");
                }
                row.put("legalFrameworkUrbanSurveyStatus", vendorData.getLegalFrameworkUrbanSurveyStatus()!= null ? vendorData.getLegalFrameworkUrbanSurveyStatus() : "Adequate Provisions Available");

                row.put("legalFrameworkAmendmentStatus", DropdownOptions.RoverProcurementStatus.fromCode(vendorData.getLegalFrameworkAmendmentStatus()!= null ? vendorData.getLegalFrameworkAmendmentStatus() : DropdownOptions.RoverProcurementStatus. NOT_AVAILABLE.getCode()).getDescription());


                resultList.add(row);
            }

            // Sort resultList by state_name ascending
            resultList.sort((a, b) -> {
                String stateA = a.get("state_name") != null ? a.get("state_name").toString() : "";
                String stateB = b.get("state_name") != null ? b.get("state_name").toString() : "";
                return stateA.compareToIgnoreCase(stateB);
            });

        } catch (Exception e) {
            System.err.println("Error while processing vendor data");
            e.printStackTrace();
        }

        return resultList;
    }









    public List<Map<String, Object>> getComparisonByMetric(String metric1, String metric2) {
        List<Object[]> results;

        // Decide which query to call
        if (List.of("roversSanctioned", "roversProcuredForFieldSurvey").contains(metric1)) {
            results = nakshaMISDataRepository.getRoversCompare();
        } else if (List.of("totalSPMUPositionsSanctioned", "totalProfessionalsRecruited").contains(metric1)) {
            results = nakshaMISDataRepository.getSPMUPositionsCompare();
        } else if (List.of("teamsFormedForsanctioned", "teamsFormedForFieldSurvey").contains(metric1)) {
            results = nakshaMISDataRepository.getForSanctionedTeamsCompare();
        } else {
            throw new IllegalArgumentException("Invalid metric: " + metric1);
        }

        // Convert result to map
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("stateName", row[0] != null ? (String) row[0] : "");
            if (row.length > 1 && metric1 != null)
                map.put(metric1, row[1] != null ? ((Number) row[1]).intValue() : 0);
            if (row.length > 2 && metric2 != null)
                map.put(metric2, row[2] != null ? ((Number) row[2]).intValue() : 0);
            list.add(map);
        }

        // Sort descending by metric1
        list.sort((a, b) -> Integer.compare(
                (int) b.getOrDefault(metric1, 0),
                (int) a.getOrDefault(metric1, 0)
        ));

        return list;
    }


    public List<Map<String, Object>> getStateRoversSummary() {
        List<Object[]> results = nakshaMISDataRepository.getStateRoversSummary();

        // Convert to Map for easier sorting & returning
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("stateName", row[0] != null ? (String) row[0] : "");
            map.put("roversSanctioned", row[1] != null ? ((Number) row[1]).intValue() : 0);
            map.put("roversProcured", row[2] != null ? ((Number) row[2]).intValue() : 0);
            map.put("totalSPMUPositionsSanctioned", row[3] != null ? ((Number) row[3]).intValue() : 0);
            map.put("totalProfessionalsRecruited", row[4] != null ? ((Number) row[4]).intValue() : 0);
            map.put("teamsFormedForsanctioned", row[5] != null ? ((Number) row[5]).intValue() : 0);
            map.put("teamsFormedForFieldSurvey", row[6] != null ? ((Number) row[6]).intValue() : 0);
            list.add(map);
        }

        // Sort by stateName ascending
        list.sort(Comparator.comparing(m -> ((String) m.get("stateName"))));

        return list;
    }

    public List<Map<String, Object>> getStateRoversSummary(Long muserid) {
        List<Object[]> results = nakshaMISDataRepository.getStateRoversSummary(muserid);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("roversSanctioned", row[0] != null ? ((Number) row[0]).intValue() : 0);
            map.put("roversProcured", row[1] != null ? ((Number) row[1]).intValue() : 0);
            map.put("totalSPMUPositionsSanctioned", row[2] != null ? ((Number) row[2]).intValue() : 0);
            map.put("totalProfessionalsRecruited", row[3] != null ? ((Number) row[3]).intValue() : 0);
            map.put("teamsFormedForsanctioned", row[4] != null ? ((Number) row[4]).intValue() : 0);
            map.put("teamsFormedForFieldSurvey", row[5] != null ? ((Number) row[5]).intValue() : 0);
            map.put("nodalDepartmentName", row[6] != null ? (row[6]) : "");
            list.add(map);
        }

        return list;
    }


/*==========================================================*/
public List<Map<String, Object>> getUnifiedReport() {
    List<Map<String, Object>> resultList = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

    try {
        // -------------------
        // 1️⃣ NakshaMISDataEntry
        // -------------------
        for (NakshaMISDataEntry vendorData : vendor) {
            Map<String, Object> row = new HashMap<>();

            row.put("entityType", "NakshaMIS");
            String stateName = vendorData.getState() != null ? vendorData.getState().getName() : null;
            if (stateName != null) {
                stateName = WordUtils.capitalizeFully(stateName);
            }
            row.put("stateName", stateName);
            row.put("stateId", vendorData.getState().getId());
            row.put("spmuRecruitmentCompleted", vendorData.getSpmuRecruitmentCompleted() != null ? vendorData.getSpmuRecruitmentCompleted() : 0);
            row.put("totalSPMUPositionsSanctioned", vendorData.getTotalSPMUPositionsSanctioned() != null ? vendorData.getTotalSPMUPositionsSanctioned() : 0);
            row.put("totalProfessionalsRecruited", vendorData.getTotalProfessionalsRecruited() != null ? vendorData.getTotalProfessionalsRecruited() : 0);
            row.put("teamsFormedForsanctioned", vendorData.getTeamsFormedForsanctioned() != null ? vendorData.getTeamsFormedForsanctioned() : 0);
            row.put("teamsFormedForFieldSurvey", vendorData.getTeamsFormedForFieldSurvey() != null ? vendorData.getTeamsFormedForFieldSurvey() : 0);
            row.put("roversSanctioned", vendorData.getRoversSanctioned() != null ? vendorData.getRoversSanctioned() : 0);
            row.put("roversProcuredForFieldSurvey", vendorData.getRoversProcuredForFieldSurvey() != null ? vendorData.getRoversProcuredForFieldSurvey() : 0);
            Integer slcCode = vendorData.getSlcMeetingConducted();
            String slcMeetingConducted = "_";

            if (slcCode != null) {
                slcMeetingConducted = (slcCode == 1) ? "Yes" : "No";
            }

            row.put("slcMeetingConducted", slcMeetingConducted);

            row.put("slcMeetingDate", vendorData.getSlcMeetingDate() != null ? vendorData.getSlcMeetingDate().format(formatter) : "_");

          /*  row.put("legalFrameworkUrbanSurveyStatus", vendorData.getLegalFrameworkUrbanSurveyStatus() != null ? vendorData.getLegalFrameworkUrbanSurveyStatus() : "Adequate Provisions Available");
*/
            String legalFrameworkStatus = vendorData.getLegalFrameworkUrbanSurveyStatus() != null
                    ? DropdownOptions.LegalFrameworkUrbanSurveyStatus
                    .fromCode(vendorData.getLegalFrameworkUrbanSurveyStatus())
                    .getDescription()
                    : "_";

            row.put("legalFrameworkUrbanSurveyStatus", legalFrameworkStatus);


            row.put("legalFrameworkAmendmentStatus", DropdownOptions.RoverProcurementStatus.fromCode(vendorData.getLegalFrameworkAmendmentStatus() != null ? vendorData.getLegalFrameworkAmendmentStatus() : DropdownOptions.RoverProcurementStatus.NOT_AVAILABLE.getCode()).getDescription());

            resultList.add(row);
        }

        // -------------------
        // 2️⃣ GroundTruthingDetail
        // -------------------
        for (GroundTruthingDetail g : groundTruthingDetailService.findAll()) {
            Map<String, Object> row = new HashMap<>();
            row.put("entityType", "GroundTruthing");
            String stateName = g.getState() != null && g.getState().getName() != null
                    ? Arrays.stream(g.getState().getName().split("\\s+"))
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "))
                    : "-";

            row.put("stateName", stateName);
            row.put("ulbName", g.getUlbMaster() != null ? g.getUlbMaster().getUlb_name() : "-");
            row.put("ulbWiseFieldSurveyTeamsFormed", g.getUlbWiseFieldSurveyTeamsFormed());
            row.put("ulbFieldTeamsSanctioned", g.getUlbMaster().getNoOfTeams());


            String surveyUnitOption = g.getSurveyUnitNameOptions() != null
                    ? DropdownOptions.SurveyUnitType.fromCode(g.getSurveyUnitNameOptions()).getDescription()
                    : "_";

            row.put("surveyUnitNameOptions", surveyUnitOption);


            row.put("totalPlotsToSurvey", g.getTotalPlotsToSurvey());
            row.put("plotsSurveyedCompleted", g.getPlotsSurveyedCompleted());
            row.put("surveyCompletionPercent", g.getSurveyCompletionPercent());
            row.put("totalUrProCardIssued", g.getTotalUrProCardIssued());
            row.put("claimsObjectionsReceived", g.getClaimsObjectionsReceived());
            row.put("totalFinalUrProCardIssued", g.getTotalFinalUrProCardIssued());

            resultList.add(row);
        }

        // -------------------
        // 3️⃣ BuildingService + IECActivities
        // -------------------
        List<IECActivities> iecActivitiesList = iecActivityService.findAll();
        Map<String, IECActivities> iecMap = iecActivitiesList.stream()
                .collect(Collectors.toMap(
                        ia -> ia.getState().getName(),
                        ia -> ia,
                        (existing, replacement) -> replacement
                ));

        for (CapacityBuilding e : buildingService.getAllEntries()) {
            Map<String, Object> row = new HashMap<>();
            String stateName = "-";
            if (e.getState() != null && e.getState().getName() != null) {
                stateName = Arrays.stream(e.getState().getName().split("\\s+"))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                        .collect(Collectors.joining(" "));
            }


            row.put("entityType", "CapacityBuilding");
            row.put("stateName", stateName);
            row.put("nameOfCoE", e.getNameOfCoE());
            row.put("nigstMasterTrainers", e.getNigstMasterTrainers());
            row.put("coeMasterTrainers", e.getCoeMasterTrainers());
            row.put("totalMasterTrainers", e.getTotalMasterTrainers());
            row.put("fieldTeamsSanctioned", e.getFieldTeamsSanctioned());
            row.put("membersTrained", e.getMembersTrained());
            row.put("membersToBeTrained", e.getMembersToBeTrained());
            row.put("fieldTeamsTrained", String.format("%.0f", e.getFieldTeamsTrained()));
            row.put("percentageTrained", e.getPercentageTrained());

            IECActivities ia = iecMap.get(stateName);
            if (ia != null) {
                row.put("iecMaterialStatus", ia.getIecMaterialStatus() != null
                        ? IECActivityDropdowns.IECActivityType.fromCode(ia.getIecMaterialStatus()).getDescription()
                        : "N/A");

                row.put("iecMediaType", ia.getIecMediaType() != null
                        ? IECActivityDropdowns.IECMediaType.fromCode(ia.getIecMediaType()).getDescription()
                        : "N/A");

                row.put("iecMediaVariety", ia.getIecMediaVariety() != null
                        ? ia.getIecMediaVariety()
                        : "N/A");

                row.put("iecActivityStatus", ia.getIecActivityStatus() != null
                        ? IECActivityDropdowns.IECActivityStatus.fromCode(ia.getIecActivityStatus()).getDescription()
                        : "N/A");

            } else {
                row.put("iecMaterialStatus", "-");
                row.put("iecMediaType", "-");
                row.put("iecMediaVariety", "-");
                row.put("iecActivityStatus", "-");
            }

            resultList.add(row);
        }

        // -------------------
        // Sort entire result by state name
        // -------------------
        resultList.sort((a, b) -> {
            String stateA = a.get("stateName") != null ? a.get("stateName").toString() : "";
            String stateB = b.get("stateName") != null ? b.get("stateName").toString() : "";
            return stateA.compareToIgnoreCase(stateB);
        });

    } catch (Exception e) {

    }

    return resultList;
}


    public int getTotalRoversFromExistingStates() {
        int total = 0;

        for (NakshaMISDataEntry vendorData : vendor) {
            if ((vendorData.getRoversSanctioned() == null || vendorData.getRoversSanctioned() == 0) &&
                    vendorData.getRoversProcuredForFieldSurvey() != null &&
                    vendorData.getRoversProcuredForFieldSurvey() > 0) {

                total += vendorData.getRoversProcuredForFieldSurvey(); // sum rovers
            }
        }

        return total;
    }





    public List<Map<String, Object>> getStatesWithProcuredButNotSanctioned() {
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (NakshaMISDataEntry vendorData : vendor) {
            if ((vendorData.getRoversSanctioned() == null || vendorData.getRoversSanctioned() == 0) &&
                    vendorData.getRoversProcuredForFieldSurvey() != null &&
                    vendorData.getRoversProcuredForFieldSurvey() > 0) {

                Map<String, Object> row = new HashMap<>();
                row.put("stateName", vendorData.getState().getName());
                row.put("roversProcuredForFieldSurvey", vendorData.getRoversProcuredForFieldSurvey());
                resultList.add(row);
            }
        }

        // ✅ Sort in descending order
        resultList.sort((m1, m2) ->
                Integer.compare((Integer) m2.get("roversProcuredForFieldSurvey"),
                        (Integer) m1.get("roversProcuredForFieldSurvey"))
        );

        return resultList;
    }





}