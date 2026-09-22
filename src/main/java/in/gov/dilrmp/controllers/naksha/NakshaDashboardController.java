package in.gov.dilrmp.controllers.naksha;


import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.naksha.CapacityBuildingReportDTO;
import in.gov.dilrmp.models.naksha.GroundTruthingDetail;
import in.gov.dilrmp.models.naksha.IECActivities;
import in.gov.dilrmp.models.naksha.NakshaMISReportDTO;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import in.gov.dilrmp.services.naksha.*;
import in.gov.dilrmp.utils.IECActivityDropdowns;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.util.*;

@Controller
@RequestMapping("chart")
public class NakshaDashboardController {

    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    private NakshaMISDataService nakshaMISDataService;

    @Autowired
    private NakshaMISDataVenderService summaryService;

    @Autowired
    private ULBMasterService ulbMasterService;

    @Autowired
    private CapacityBuildingService buildingService;

    @Autowired
    StateService stateService;


    @Autowired
    IecActivityService iecActivityService;

    @Autowired
    GroundTruthingDetailService groundTruthingDetailService;

    private static final Logger logger = LoggerFactory.getLogger(NakshaDashboardController.class);

//================ Naksha For State Dashboard=============================================
    @GetMapping("/distinct-states")
    public ResponseEntity<List<String>> getDistinctStates() {
        try {
            List<String> stateNames = nakshaMISDataService.getDistinctStateNames();
            return ResponseEntity.ok(stateNames);
        } catch (Exception e) {
            logger.error("Error in controller while fetching distinct states", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/ulb-names")
    public ResponseEntity<List<String>> getGdNamesByState(@RequestParam String stateName) {
        try {
            List<String> gdNames = nakshaMISDataService.getGdNamesByState(stateName);
            return ResponseEntity.ok(gdNames);
        } catch (Exception e) {
            logger.error("Error in controller while fetching GD names for state: {}", stateName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @GetMapping("/ulb-data-chart")
    @ResponseBody
    public Map<String, Map<String, String>> getChartData(@RequestParam String ulbName) {
        return nakshaMISDataService.getBarChartData(ulbName);
    }

    @GetMapping("/naksha-vender")
    public ResponseEntity<Map<String, Object>> getNakshaVenderSummary() {
        try {
            logger.info("Fetching Naksha Vender summary...");
            Map<String, Object> summary = nakshaMISDataService.getSummary();
            logger.info("Naksha Vender summary fetched successfully.");
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error fetching Naksha Vender summary", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "An error occurred while fetching Naksha Vender summary."));
        }
    }

    @GetMapping("/rovers-summary")
    public ResponseEntity<Map<String, Object>> getRoversSummary(
            @RequestParam(required = false) Long stateId) {
        try {
             Map<String, Object> summary = nakshaMISDataService.getRoversSummary(stateId);
            logger.info("Rovers summary fetched successfully.");
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error fetching rovers summary", e);
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "Failed to fetch rovers summary."));
        }
    }



    @GetMapping("/state-rovers-summary/all")
    public ResponseEntity<List<Map<String, Object>>> getAllStatesSummaryByMetric(@RequestParam String metric) {
        try {
            List<Map<String, Object>> data = nakshaMISDataService.getAllStatesRoversSummarySortedByMetric(metric);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            logger.error("Error fetching rovers summary all states", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/getStates")
    @ResponseBody
    public List<Map<String, Object>> getAllStates() {
        try {
            List<Object[]> rawData = nakshaMISDataService.getStateIdAndStateName();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Object[] row : rawData) {
                Map<String, Object> map = new HashMap<>();
                map.put("stateId", row[0]);
                map.put("stateName", row[1]);
                result.add(map);
            }
            result.sort(Comparator.comparing(m -> m.get("stateName").toString(), String.CASE_INSENSITIVE_ORDER));

            return result;
        } catch (Exception e) {
            logger.error("Error ", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

 /*   @GetMapping("/naksha-state-mis")
    public ResponseEntity<?> getNakshaMISReport() {
        try {
            List<NakshaMISReportDTO> reportList = nakshaMISDataService.getNakshaMISReport();
            return ResponseEntity.ok(reportList); // HTTP 200 with JSON
        } catch (Exception e) {
            logger.error("Error fetching Naksha MIS report", e);
            return ResponseEntity
                    .internalServerError()
                    .body("{\"error\": \"Failed to load Naksha MIS report. Please try again later.\"}");
        }
    }*/


    @GetMapping("/naksha-state-mis")
    @ResponseBody
    public Map<String, Object> getNakshaMISReport() {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching Naksha MIS report");

            List<Map<String, Object>> reportData = nakshaMISDataService.getNakshaMISReport();

            if (reportData == null || reportData.isEmpty()) {
                logger.warn("No data found for Naksha MIS report");
                response.put("status", "error");
                response.put("message", "No data found");
            } else {
                logger.info("Naksha MIS report fetched successfully. Records count: {}", reportData.size());
                response.put("status", "success");
                response.put("data", reportData);
            }
        } catch (Exception e) {
            logger.error("Error fetching Naksha MIS report", e);
            response.put("status", "error");
            response.put("message", "Internal Server Error");
        }
        return response;
    }

    @GetMapping("/state-compare")
    public ResponseEntity<List<Map<String, Object>>> getStateComparison(
            @RequestParam String metric1,
            @RequestParam(required = false) String metric2) {
        try {
            List<Map<String, Object>> data = nakshaMISDataService.getComparisonByMetric(metric1, metric2);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            logger.error("Error fetching comparison for metric: " + metric1, e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/naksha-all-state-compare")
    public ResponseEntity<List<Map<String, Object>>> getStateComparison() {
        try {
            List<Map<String, Object>> data = nakshaMISDataService.getStateRoversSummary();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            logger.error("Error fetching all state comparison : " + e);
            return ResponseEntity.internalServerError().build();
        }
    }


    // ======= Naksha For SoI Dashboard ===================================================



    @GetMapping(value = "/naksha-soi-dashboard")
    public String nakshaDashboard(Model model) {
        try {
            summaryService.loadVendorData();
            summaryService.loadTimelineBackupData();
            Map<String, Object> summaryData = summaryService.getSummaryData();

            // Add summary data to the model
            model.addAttribute("totalULBs", summaryData.get("totalULBs"));
            model.addAttribute("uniqueGDCount", summaryData.get("uniqueGDCount"));
            model.addAttribute("uniqueContractors", summaryData.get("uniqueContractors"));
            model.addAttribute("totalSanctionedArea", summaryData.get("totalSanctionedArea"));
            model.addAttribute("totalBufferArea", summaryData.get("totalBufferArea"));


            model.addAttribute("tech1Completed", summaryData.get("tech1Percentage"));
            model.addAttribute("tech2Completed", summaryData.get("tech2Percentage"));
            model.addAttribute("tech3Completed", summaryData.get("tech3Percentage"));
            model.addAttribute("flyingCompleted", summaryData.get("flyingCompleted"));
            model.addAttribute("countDataUploadedByGD", summaryData.get("countDataUploadedByGD"));




            return "pages/naksha/nakshaSoiDashboard.html";

        } catch (Exception e) {
            logger.error("Error while preparing Naksha SOI Dashboard summary data", e);
            model.addAttribute("error", "Unable to load dashboard data.");
            return "pages/naksha/nakshaSoiDashboard";
        }
    }

//====================================================

    @GetMapping("/contractor-by-state")
    @ResponseBody
    public List<Map<String, Object>> getContractorCountByState() {
        try {
            return summaryService.getContractorCountByState();
        } catch (Exception e) {
            logger.error("Error fetching contractor count by state", e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/sanctioned-area-by-state")
    @ResponseBody
    public List<Map<String, Object>> getSanctionedAreaByState() {
        try {
            return summaryService.getSanctionedAreaByState();
        } catch (Exception e) {
            logger.error("Error fetching sanctioned area by state", e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/buffer-area-by-state")
    @ResponseBody
    public List<Map<String, Object>> getBufferAreaByState() {
        try {
            return summaryService.getBufferAreaByState();
        } catch (Exception e) {
            logger.error("Error fetching buffer area by state", e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/area-by-technology")
    @ResponseBody
    public List<Map<String, Object>> getAreaSummaryByTechnology() {
        try {
            return summaryService.getAreaSummaryByTechnology();
        } catch (Exception e) {
            logger.error("Error fetching area summary by technology", e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/area-by-state-technology")
    @ResponseBody
    public List<Map<String, Object>> getAreaSummaryByStateAndTechnology() {
        try {
            return summaryService.getAreaSummaryByStateAndTechnology();
        } catch (Exception e) {
            logger.error("Error fetching area summary by state and technology", e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/all")
    @ResponseBody
    public Map<String, List<Map<String, Object>>> getAllSummaryData() {
        try {
            return summaryService.getAllSummaryData();
        } catch (Exception e) {
            logger.error("Error fetching all summary data", e);
            return new HashMap<>();
        }
    }
    @GetMapping("/states")
    @ResponseBody
    public List<String> getAllStateNames() {
        try {
             List<String> states = ulbMasterService.getAllStateNames();
            logger.info("Total states found: {}", states.size());
            return states;
        } catch (Exception e) {
            logger.error("Error occurred while fetching state names", e);
            // return empty list if failure
            return Collections.emptyList();
        }
    }

    @GetMapping("/ulb-detail")
    @ResponseBody
    public List<Map<String, String>> getAllUlbWithState() {
        try {
            List<Map<String, String>> ulbs = ulbMasterService.getAllUlbWithState();
            logger.info("Total ULBs found: {}", ulbs.size());
            return ulbs;
        } catch (Exception e) {
            logger.error("Error occurred while fetching ULBs with states", e);
            // return empty list if failure
            return Collections.emptyList();
        }
    }



    @GetMapping("/ulb-count-by-gd")
    @ResponseBody
    public List<Map<String, Object>> getULBCountByGD() {
        try {
            List<Map<String, Object>> result = ulbMasterService.getULBCountByGD();
            logger.info("Returning {} GD entries", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Exception in getULBCountByGD()", e);
            return Collections.emptyList();  // Or consider throwing a custom error if required
        }
    }

    @GetMapping("/grouped-ulbs")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getGroupedULBs(@RequestParam Long gdId) {
        try {
            List<Map<String, String>> result = ulbMasterService.getGroupedULBsByGdId(gdId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error in /grouped-ulbs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/state-names")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getStates() {
        try {
            List<State> states = stateService.findAllOrderByStateName();

            List<Map<String, Object>> result = states.stream()
                    .map(state -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", state.getId());
                        map.put("name", state.getName());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching State Names", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/capacity-building-report")
    @ResponseBody
    public ResponseEntity<?> getCapacityBuildingReportJson(@RequestParam(required = false) Long stateId) {
        try {
            CapacityBuildingReportDTO reportList = buildingService.getCapacityBuildingGrandTotal(stateId);

            logger.info("Capacity building report fetched successfully. Total states: {}");

            return ResponseEntity.ok(reportList);
        } catch (Exception ex) {
            logger.error("Error fetching capacity building report", ex);

            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch report. Please try again later.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/capacity-building/entries")
    @ResponseBody
    public ResponseEntity<?> getSelectedCapacityBuildingData() {
        try {
            // Fetch both lists
            List<IECActivities> iecActivitiesList = iecActivityService.findAll();

            // Convert IECActivities list to a Map for quick lookup by state name
            Map<String, IECActivities> iecMap = iecActivitiesList.stream()
                    .collect(Collectors.toMap(
                            ia -> ia.getState().getName(),
                            ia -> ia,
                            (existing, replacement) -> replacement // in case of duplicates, keep the latest
                    ));

            // Build result list from buildingService
            List<Map<String, Object>> result = buildingService.getAllEntries().stream()
                    .sorted(Comparator.comparing(e -> e.getState().getName())) // ✅ sort by state name
                    .map(e -> {
                        Map<String, Object> map = new HashMap<>();
                        String stateName = e.getState().getName();

                        // Add BuildingService fields
                        map.put("stateName", stateName);
                        map.put("nameOfCoE", e.getNameOfCoE());
                        map.put("nigstMasterTrainers", e.getNigstMasterTrainers());
                        map.put("coeMasterTrainers", e.getCoeMasterTrainers());
                        map.put("totalMasterTrainers", e.getTotalMasterTrainers());
                        map.put("fieldTeamsSanctioned", e.getFieldTeamsSanctioned());
                        map.put("membersTrained", e.getMembersTrained());
                        map.put("membersToBeTrained", e.getMembersToBeTrained());
                        map.put("fieldTeamsTrained", e.getFieldTeamsTrained());
                        map.put("percentageTrained", e.getPercentageTrained());

                        // ✅ Add IECActivities fields if present for the same state
                        IECActivities ia = iecMap.get(stateName);
                        if (ia != null) {
                            map.put("iecMaterialStatus", ia.getIecMaterialStatus() != null
                                    ? IECActivityDropdowns.IECActivityType.fromCode(ia.getIecMaterialStatus()).getDescription()
                                    : "N/A");

                            map.put("iecMediaType", ia.getIecMediaType() != null
                                    ? IECActivityDropdowns.IECMediaType.fromCode(ia.getIecMediaType()).getDescription()
                                    : "N/A");

                            map.put("iecMediaVariety", ia.getIecMediaVariety() != null
                                    ? ia.getIecMediaVariety()
                                    : "N/A");

                            map.put("iecActivityStatus", ia.getIecActivityStatus() != null
                                    ? IECActivityDropdowns.IECActivityStatus.fromCode(ia.getIecActivityStatus()).getDescription()
                                    : "N/A");

                            /*      map.put("iecMaterialStatus",
                                    IECActivityDropdowns.IECActivityType.fromCode(
                                            ia.getIecMaterialStatus() != null ? ia.getIecMaterialStatus()
                                                    : IECActivityDropdowns.IECActivityType.NOT_AVAILABLE.getCode()
                                    ).getDescription()
                            );

                            map.put("iecMediaType",
                                    IECActivityDropdowns.IECMediaType.fromCode(
                                            ia.getIecMediaType() != null ? ia.getIecMediaType()
                                                    : IECActivityDropdowns.IECMediaType.NOT_AVAILABLE.getCode()
                                    ).getDescription()
                            );

                            map.put("iecMediaVariety",
                                    IECActivityDropdowns.IECMediaVariety.fromCode(
                                            ia.getIecMediaVariety() != null ? ia.getIecMediaVariety()
                                                    : IECActivityDropdowns.IECActivityType.NOT_AVAILABLE.getCode()
                                    ).getDescription()
                            );

                            map.put("iecActivityStatus",
                                    IECActivityDropdowns.IECActivityStatus.fromCode(
                                            ia.getIecActivityStatus() != null ? ia.getIecActivityStatus()
                                                    : IECActivityDropdowns.IECActivityStatus.NOT_AVAILABLE.getCode()
                                    ).getDescription()
                            );*/





                        } else {
                            // If no IEC record exists, put nulls (optional)
                            map.put("iecMaterialStatus", "-");
                            map.put("iecMediaType", "-");
                            map.put("iecMediaVariety", "-");
                            map.put("iecActivityStatus", "-");
                        }

                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching selected capacity building data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to fetch data."));
        }
    }


    @GetMapping("/nigst-data")
    @ResponseBody
    public ResponseEntity<?> getNigstData(@RequestParam(required = false) Long stateId) {
        try {
            return ResponseEntity.ok(buildingService.getNigstData(stateId));
        } catch (Exception e) {
            logger.error("Error fetching NIGST data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch data"));
        }
    }

    @GetMapping("/coe-data")
    @ResponseBody
    public ResponseEntity<?> getCoeData(@RequestParam(required = false) Long stateId) {
        try {
            return ResponseEntity.ok(buildingService.getCoeData(stateId));
        } catch (Exception e) {
            logger.error("Error fetching NIGST data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch data"));
        }
    }
    @GetMapping("/master-trainer-data")
    @ResponseBody
    public ResponseEntity<?> getTotalMasterTrainerData(@RequestParam(required = false) Long stateId) {
        try {
            return ResponseEntity.ok(buildingService.getTotalMasterTrainerData(stateId));
        } catch (Exception e) {
            logger.error("Error fetching NIGST data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch data"));
        }
    }
    @GetMapping("/field-teams-data")
    @ResponseBody
    public ResponseEntity<?> getFieldTeamsData(@RequestParam(required = false) Long stateId) {
        try {
            return ResponseEntity.ok(buildingService.getFieldTeamsData(stateId));
        } catch (Exception e) {
            logger.error("Error fetching NIGST data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch data"));
        }
    }
    @GetMapping("/trained-data")
    @ResponseBody
    public ResponseEntity<?> getTrainedData(@RequestParam(required = false) Long stateId) {
        try {
            return ResponseEntity.ok(buildingService.getTrainedData(stateId));
        } catch (Exception e) {
            logger.error("Error fetching NIGST data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch data"));
        }
    }
    @GetMapping("/to-be-trained-data")
    @ResponseBody
    public ResponseEntity<?> getToBeTrainedData(@RequestParam(required = false) Long stateId) {
        try {
            return ResponseEntity.ok(buildingService.getToBeTrainedData(stateId));
        } catch (Exception e) {
            logger.error("Error fetching NIGST data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch data"));
        }
    }
    @GetMapping("/to-be-percentage-trained-data")
    @ResponseBody
    public ResponseEntity<?> getToBePercentageTrainedData(@RequestParam(required = false) Long stateId) {
        try {
            return ResponseEntity.ok(buildingService.getToBePercentageTrainedData(stateId));
        } catch (Exception e) {
            logger.error("Error fetching NIGST data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch data"));
        }
    }




    @GetMapping("/capacity-building-state-summary")
    @ResponseBody
    public List<Map<String, Object>> getCapacityBuildingStateSummary() {
        return buildingService.getStateWiseSummary();
    }


    @GetMapping("/coe-state-wise-summary")
    @ResponseBody
    public List<Map<String, Object>> getCoEStateWiseSummary() {
        try {
            logger.info("Fetching CoE-wise state summary data...");
            return buildingService.getCoEWithStateWiseData();
        } catch (Exception ex) {
            logger.error("Error while fetching CoE-wise state summary data", ex);
            return Collections.emptyList(); // Graceful fallback
        }
    }

    @GetMapping("/unique-coe-names")
    @ResponseBody
    public ResponseEntity<List<String>> getUniqueCoENames() {
        try {
            logger.info("Fetching unique CoE names...");
            List<String> coeNames = buildingService.getUniqueNameOfCoEs();
            return ResponseEntity.ok(coeNames);
        } catch (Exception ex) {
            logger.error("Error occurred while fetching unique CoE names", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

/*
    @GetMapping("/state-mis-comparison")
    @ResponseBody
    public Map<String, Object> getStateMisDetailsByStateId(@RequestParam(value = "stateId", required = false) Long stateId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching MIS details for stateId: {}", stateId);

            Map<String, Integer> details = nakshaMISDataService.getStateMisDetails(stateId);

            if (details == null || details.isEmpty()) {
                logger.warn("No data found for stateId: {}", stateId);
                response.put("status", "error");
                response.put("message", "No data found");
            } else {
                response.put("status", "success");
                response.put("data", details);
            }

        } catch (Exception e) {
            logger.error("Error fetching MIS details for stateId: {}", stateId, e);
            response.put("status", "error");
            response.put("message", "Internal Server Error");
        }
        return response;
    }
*/

    @GetMapping("/state-mis-comparison")
    @ResponseBody
    public Map<String, Object> getStateMisDetailsByStateId(
            @RequestParam(value = "stateId", required = false) Long stateId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching MIS details for stateId: {}", stateId);

            // ✅ Service already returns { totalUlb, details }
            Map<String, Object> serviceResponse = nakshaMISDataService.getStateMisDetails(stateId);

            if (serviceResponse == null || ((List<?>) serviceResponse.get("details")).isEmpty()) {
                logger.warn("No data found for stateId: {}", stateId);
                response.put("status", "error");
                response.put("message", "No data found");
            } else {
                response.put("status", "success");
                response.putAll(serviceResponse);  // ✅ merge service response (totalUlb + details)
            }

        } catch (Exception e) {
            logger.error("Error fetching MIS details for stateId: {}", stateId, e);
            response.put("status", "error");
            response.put("message", "Internal Server Error");
        }
        return response;
    }



    @GetMapping("/ground-truthing-detail-des")
    @ResponseBody
    public ResponseEntity<?> getGroundTruthingDetail() {
        try {
            // Fetch all GroundTruthingDetail records
            List<GroundTruthingDetail> groundTruthingDetailList = groundTruthingDetailService.findAll();

            // Convert into list of maps
            List<Map<String, Object>> result = groundTruthingDetailList.stream()
                    .sorted(Comparator.comparing(g -> g.getState().getName())) // ✅ sort by state name
                    .map(g -> {
                        Map<String, Object> map = new HashMap<>();

                        // ✅ Add state & ULB name
                        map.put("stateName", g.getState() != null ? g.getState().getName() : null);
                        map.put("ulbName", g.getUlbMaster() != null ? g.getUlbMaster().getUlb_name() : null);

                        // ✅ Add all entity fields
                        map.put("ulbWiseFieldSurveyTeamsFormed", g.getUlbWiseFieldSurveyTeamsFormed());
                        map.put("surveyUnitNameOptions", g.getSurveyUnitNameOptions());
                        map.put("totalPlotsToSurvey", g.getTotalPlotsToSurvey());
                        map.put("plotsSurveyedCompleted", g.getPlotsSurveyedCompleted());
                        map.put("surveyCompletionPercent", g.getSurveyCompletionPercent());
                        map.put("totalUrProCardIssued", g.getTotalUrProCardIssued());
                        map.put("claimsObjectionsReceived", g.getClaimsObjectionsReceived());
                        map.put("totalFinalUrProCardIssued", g.getTotalFinalUrProCardIssued());

                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching ground truthing detail data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to fetch data."));
        }
    }

    @GetMapping("/unified-report")
    @ResponseBody
    public ResponseEntity<?> getUnifiedReportData() {
        try {
            // Call the unified service method
            List<Map<String, Object>> reportData = nakshaMISDataService.getUnifiedReport();

            // Return OK with data
            return ResponseEntity.ok(reportData);
        } catch (Exception e) {
            // Log the error
            logger.error("Error fetching unified report data", e);

            // Return 500 with error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch unified report data.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/state-with-existing-rovers")
    public @ResponseBody List<Map<String, Object>> getStatesWithProcuredButNotSanctioned() {
        try {
            // Call your service method
            return nakshaMISDataService.getStatesWithProcuredButNotSanctioned();
        } catch (Exception e) {
            // Log the error
            logger.error("Error fetching states with procured but not sanctioned rovers", e);
            // Return an empty list or you could throw a ResponseStatusException
            return List.of();
        }
    }

    @GetMapping("/ground-truthing-dashboard-data")
    @ResponseBody
    public List<Map<String, Object>> getGroundTruthingReportData() {
        try {
            logger.info("Fetching Ground Truthing Report Data...");
            List<Map<String, Object>> reportData = groundTruthingDetailService.getGroundTruthingReportData();

            if (reportData == null || reportData.isEmpty()) {
                logger.warn("No Ground Truthing data found.");
            } else {
                logger.info("Successfully fetched {} records.", reportData.size());
            }

            return reportData;

        } catch (Exception ex) {
            logger.error("Error while fetching Ground Truthing report data: {}", ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

}


