package in.gov.dilrmp.controllers.naksha;

import in.gov.dilrmp.models.naksha.NakshaMISDataEntrySolrAndVender;
import in.gov.dilrmp.repositories.naksha.NakshaMISDataVenderRepository;
import in.gov.dilrmp.services.naksha.*;
import in.gov.dilrmp.utils.CompletionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("chart")
public class NakshaSoiDashboardController {

    @Autowired
    NakshaMISDataVenderService dataVenderService;

    @Autowired
    private NakshaMISDataService nakshaMISDataService;

    @Autowired
    private ULBMasterService ulbMasterService;

    @Autowired
    private TimelineBackupService timelineBackupService;


    private static final Logger logger = LoggerFactory.getLogger(NakshaSoiDashboardController.class);

    /**
     * Endpoint to get technology count per ULB. @return List of maps containing ULB name and count of technologies.
     */
    @GetMapping("/technology-wise-ulb-count")
    @ResponseBody
    public List<Map<String, Object>> getUlbCountPerTechnology() {
        try {
            return dataVenderService.fetchUlbCountPerTechnology();
        } catch (Exception e) {
            logger.error("Error occurred while fetching ULB count per technology", e);

            // You can return an empty list or create a custom error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unable to fetch data");
            errorResponse.put("details", e.getMessage());

            return Collections.singletonList(errorResponse);
        }
    }

    @GetMapping("/technology-wise-area-summary")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getTechnologyWiseAreaSummary() {
        try {
             List<Object[]> rawData = dataVenderService.getComAreaByTechnology();

            List<Map<String, Object>> response = new ArrayList<>();

            for (Object[] row : rawData) {
                Map<String, Object> map = new HashMap<>();
                map.put("technology", row[0]);
                map.put("totalSanctionedArea",Math.round(((Number) row[1]).doubleValue()));
                map.put("totalBufferArea", Math.round(((Number) row[2]).doubleValue()));
                response.add(map);
            }

            logger.info("Returning {} records for technology-wise area summary", response.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching technology-wise area summary", e);
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }

    @GetMapping("/state-wise-area-summary")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getStateWiseAreaSummary() {
        try {
            List<Object[]> rawData = dataVenderService.getComAreaByStater();

            List<Map<String, Object>> response = new ArrayList<>();

            for (Object[] row : rawData) {
                Map<String, Object> map = new HashMap<>();
                map.put("stateName", row[0]);
                double sanctionedArea = row[1] != null ? Math.round(((Number) row[1]).doubleValue()) : 0;
                map.put("totalSanctionedArea", sanctionedArea);

                double bufferArea = row[2] != null ? Math.round(((Number) row[2]).doubleValue()) : 0;
                map.put("totalBufferArea", bufferArea);
                response.add(map);
            }

            logger.info("Returning {} records for Contractor-wise area summary", response.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching Contractor-wise area summary", e);
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }



    @GetMapping("/contractor-wise-area-summary")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getContractorWiseAreaSummary() {
        try {
            List<Object[]> rawData = dataVenderService.getComAreaByContractor();

            List<Map<String, Object>> response = new ArrayList<>();

            for (Object[] row : rawData) {
                Map<String, Object> map = new HashMap<>();
                map.put("contractor", row[0]);
                map.put("totalSanctionedArea",Math.round(((Number) row[1]).doubleValue()));
                map.put("totalBufferArea", Math.round(((Number) row[2]).doubleValue()));
                response.add(map);
            }

            logger.info("Returning {} records for Contractor-wise area summary", response.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching Contractor-wise area summary", e);
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }

// GET endpoint to fetch the count of distinct ULBs under each GD,
    @GetMapping("/ulb-under-each-gd")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getUlbCountByGd() {
        try {
            List<Object[]> rawData = dataVenderService.getUlbCountGroupedByGd();
            List<Map<String, Object>> response = new ArrayList<>();

            for (Object[] row : rawData) {
                Map<String, Object> map = new HashMap<>();
                map.put("gdName", row[0]);
                map.put("ulbCount", row[1]);
                response.add(map);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Log error properly in real app
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
//=== Aerial Data Acquisition  ================================
    @GetMapping("/cumulative-data")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> showCumulativeData() {
        try {
            List<Object[]> data = dataVenderService.getCumulativeData();
            List<Map<String, Object>> jsonList = new ArrayList<>();

            for (Object[] row : data) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", row[0]);
                map.put("ulbName", row[1]);
                map.put("tech1Cumulative", row[2] != null ? row[2] : 0);
                map.put("tech2Cumulative", row[3] != null ? row[3] : 0);
                map.put("tech3Cumulative", row[4] != null ? row[4] : 0);
                map.put("tech1Percentage", row[5] != null ? row[5] : 0);
                map.put("tech2Percentage", row[6] != null ? row[6] : 0);
                map.put("tech3Percentage", row[7] != null ? row[7] : 0);
                map.put("tech3LidarSensor", row[9] != null ? row[9] : 0);
                map.put("tech3LidarPercentage", row[10] != null ? row[10] : 0);
                map.put("getTech1Status", row[11]);
                map.put("getTech2Status", row[12] );
                map.put("getTech3CumulativeStatus", row[13] );
                map.put("getTech3LidarStatus", row[14] );

                jsonList.add(map);
            }

            return ResponseEntity.ok(jsonList);

        } catch (Exception e) {
            // Log error
            LoggerFactory.getLogger(getClass()).error("Error fetching cumulative data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }


    @GetMapping("/state-soi-summary")
    public ResponseEntity<?> getStateSoiSummary() {
        try {
            List<Map<String, Object>> summary = dataVenderService.getStateSoiWiseSummaryFromCache();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error fetching state-wise SOI summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("status", "error", "message", "Unable to fetch data", "details", e.getMessage())
            );
        }
    }


    @GetMapping("/grid-summary")
    public ResponseEntity<?> getGridSummary() {
        try {
            List<List<Object>> summary = dataVenderService.getGridSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error in controller while getting grid summary", e);
            return ResponseEntity.status(500).body("Failed to fetch grid summary");
        }
    }


    @GetMapping("/ulb-details/{id}")
    public ResponseEntity<?> getUlbDetails(@PathVariable("id") Long id) {
        try {
            List<Object[]> details = dataVenderService.getUlbDetailsById(id);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch ULB details");
        }
    }


    @GetMapping("/compare-gd-names")
    public ResponseEntity<Map<String, Object>> compareGdNames() {
        try {
            Map<String, Object> result = dataVenderService.compareGdNames();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error occurred while comparing GD names", e);
            return ResponseEntity.internalServerError().build();
        }
    }




    @GetMapping("/compare-ulb-names")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> compareUlbNames() {
        try {
            Map<String, List<Map<String, Object>>> result = dataVenderService.compareUlbNames();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace(); // or logger.error("Error in compareUlbNames", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/compare-agency-name")
    @ResponseBody
    public Map<String, Object> compareAgencyWithDatabase() {
        try {
            Map<String, Object> result = dataVenderService.compareAgencyWithDatabase();

            if (result != null) {
                logger.info("Comparison completed successfully. Found: {} present, {} missing",
                        ((List<?>) result.getOrDefault("presentInDatabase", Collections.emptyList())).size(),
                        ((List<?>) result.getOrDefault("notInDatabase", Collections.emptyList())).size());
                return result;
            } else {
                logger.warn("Comparison result is null from dataVenderService.");
                return Collections.singletonMap("error", List.of("Comparison result is null."));
            }
        } catch (Exception e) {
            logger.error("Error occurred while comparing agency data with database", e);
            return Collections.singletonMap("error", List.of("An error occurred during comparison."));
        }
    }



    @GetMapping("/gd-name")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getGdName() {
        try {
            List<Object[]> rawData = dataVenderService.getUlbCountGroupedByGd();
            List<Map<String, Object>> response = new ArrayList<>();

            for (Object[] row : rawData) {
                Map<String, Object> map = new HashMap<>();
                map.put("gdName", row[0]);
                map.put("ulbCount", row[1]);
                response.add(map);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Log error properly in real app
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/gd-names")
    public ResponseEntity<List<Map<String, Object>>> getGdNames() {
        try {
            List<Map<String, Object>> gdNames = ulbMasterService.getGdNames();
            return ResponseEntity.ok(gdNames);
        } catch (Exception e) {
            logger.error("Error fetching GD Names", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }


    @GetMapping("/submitted-data-processing")
    public ResponseEntity<Map<String, Integer>> getSubmittedDataProcessingCounts(
            @RequestParam(required = false) String gdName,
            @RequestParam(required = false) String gdStateName) {
        try {
            Map<String, Integer> counts;
            if (gdName != null) {
                counts = dataVenderService.getProcessingCounts(gdName);
            } else if (gdStateName != null) {
                counts = dataVenderService.getProcessingCountsByState(gdStateName);
            } else {
                counts = Collections.emptyMap();
            }

            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            logger.error("Error occurred while fetching processing counts", e);
            return ResponseEntity.internalServerError().body(Collections.emptyMap());
        }
    }


    @GetMapping("/gd-wise-flaying-summary")
    @ResponseBody
    public List<Object[]> getGdWiseFlayingSummary() {
        try {
            return dataVenderService.getGdWiseFlayingCompletionSummary();
        } catch (Exception e) {
            logger.error("Error fetching GD-wise tech completion summary", e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/processing-completed-summary")
    public @ResponseBody List<Map<String, Object>> getProcessingCompletedSummaryRaw() {
        try {
              return dataVenderService.getProcessingCompletedSummaryRaw();
        } catch (Exception e) {
            logger.error("Error while fetching processing summary", e);
            return Collections.singletonList(Collections.singletonMap("error", "Internal Server Error"));
        }
    }


    @GetMapping("/ulb-feature")
    public ResponseEntity<List<List<String>>> getUlbDetails(
            @RequestParam(name = "gdName", required = false) String gdName,
            @RequestParam(name = "gdStateName", required = false) String gdStateName){
        try {
            List<List<String>> table = dataVenderService.getUlbDetails(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/tech1-nadir")
    public ResponseEntity<List<List<String>>> getTech1Nadir(
            @RequestParam(name = "gdName", required = false) String gdName,
            @RequestParam(name = "gdStateName", required = false) String gdStateName){
        try {
            List<List<String>> table = dataVenderService.getTech1Nadir(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    @GetMapping("/tech2-oblique")
    public ResponseEntity<List<List<String>>> getTech2Oblique(
            @RequestParam(name = "gdName", required = false) String gdName,
            @RequestParam(name = "gdStateName", required = false) String gdStateName){
        try {
            List<List<String>> table = dataVenderService.getTech2Oblique(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    @GetMapping("/tech3-oblique")
    public ResponseEntity<List<List<String>>> getTech3Oblique(
            @RequestParam(name = "gdName", required = false) String gdName,
            @RequestParam(name = "gdStateName", required = false) String gdStateName){
        try {
            List<List<String>> table = dataVenderService.getTech3Oblique(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/tech3-lidar")
    public ResponseEntity<List<List<String>>> getTech3Lidar(
            @RequestParam(name = "gdName", required = false) String gdName,
            @RequestParam(name = "gdStateName", required = false) String gdStateName){
        try {
            List<List<String>> table = dataVenderService.getTech3Lidar(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }



    @GetMapping("/ori-data")
    public ResponseEntity<List<List<String>>> getOriDataGrid(
            @RequestParam(name = "gdName", required = false) String gdName,
            @RequestParam(name = "gdStateName", required = false) String gdStateName){
        try {
            List<List<String>> table = dataVenderService.getOriDataProcessingCumulative(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/ori-qaqc-data")
    public ResponseEntity<List<List<String>>> getOriQaQcData(
            @RequestParam(name = "gdName", required = false) String gdName,
            @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.getOriQaQcData(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

   /* @GetMapping("/dem-data")
    public ResponseEntity<List<List<String>>> getDemData(@RequestParam(name = "gdName", defaultValue = "all") String gdName) {
        try {
            List<List<String>> table = dataVenderService.getDemData(gdName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/dem-qaqc-data")
    public ResponseEntity<List<List<String>>> getDemQaQcData(@RequestParam(name = "gdName", defaultValue = "all") String gdName) {
        try {
            List<List<String>> table = dataVenderService.getDemQaQcData(gdName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }*/

    @GetMapping("/dsm-data")
    public ResponseEntity<List<List<String>>> getDsmData(@RequestParam(name = "gdName", required = false) String gdName,
                                                         @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.getDsmData(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/2d-feature-stereo")
    public ResponseEntity<List<List<String>>> get2dFeatureStereo(@RequestParam(name = "gdName", required = false) String gdName,
                                                                 @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.get2dFeatureStereo(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/2d-feature-stereo-ext")
    public ResponseEntity<List<List<String>>> get2dFeatureExtStereo(@RequestParam(name = "gdName", required = false) String gdName,
                                                                    @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.get2dFeatureExtStereo(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/3d-feature")
    public ResponseEntity<List<List<String>>> get3dFeature(@RequestParam(name = "gdName", required = false) String gdName,
                                                           @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.get3dFeature(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/3d-feature-ext")
    public ResponseEntity<List<List<String>>> get3dFeatureExt(@RequestParam(name = "gdName", required = false) String gdName,
                                                              @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.get3dFeatureExt(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    @GetMapping("/3d-feature-ext-milestone")
    public ResponseEntity<List<List<String>>> get3dFeatureMilestone(@RequestParam(name = "gdName", required = false) String gdName,
                                                                    @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.get3dFeatureMilestone(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/3d-feature-ext-quack-milestone")
    public ResponseEntity<List<List<String>>> get3dFeatureQaQcMilestone(@RequestParam(name = "gdName", required = false) String gdName,
                                                                        @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.get3dFeatureQaQcMilestone(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }






    @GetMapping("/dsm-qaqc-data")
    public ResponseEntity<List<List<String>>> getDsmQaQcData(@RequestParam(name = "gdName", required = false) String gdName,
                                                             @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.getDsmQaQcData(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/dtm-data")
    public ResponseEntity<List<List<String>>> getDtmData(@RequestParam(name = "gdName", required = false) String gdName,
                                                         @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.getDtmData(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/dtm-qaqc-data")
    public ResponseEntity<List<List<String>>> getDtmQaQcData(@RequestParam(name = "gdName", required = false) String gdName,
                                                             @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.getDtmQaQcData(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/mesh-data")
    public ResponseEntity<List<List<String>>> getMeshData(@RequestParam(name = "gdName", required = false) String gdName,
                                                          @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.getMeshData(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/mesh-qaqc-data")
    public ResponseEntity<List<List<String>>> getMeshQaQcData(@RequestParam(name = "gdName", required = false) String gdName,
                                                              @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.getMeshQaQcData(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/feori-data")
    public ResponseEntity<List<List<String>>> getFeOricData(@RequestParam(name = "gdName", required = false) String gdName,
                                                            @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.getFeOricData(gdName, gdStateName);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/feori-qaqc-data")
    public ResponseEntity<List<List<String>>> getFeOriQaQcData(@RequestParam(name = "gdName", required = false) String gdName,
                                                               @RequestParam(name = "gdStateName", required = false) String gdStateName) {
        try {
            List<List<String>> table = dataVenderService.getFeOriQaQcData(gdName, gdStateName);
            Map<String, List<String>> ulbMap = dataVenderService.getFlyingCompletedT1AndT2();
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            logger.error("Error fetching ORI data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    @GetMapping("/compare-tech3-percentage")
    @ResponseBody
    public Map<String, List<String>> getTech3PercentageCategories() {
        try {
            Map<String, List<String>> result = dataVenderService.getFlyingCompletedT3();
            return result;
        } catch (Exception e) {
            logger.error("Error while fetching tech3Percentage data", e);
            return Map.of(
                    "tech3_100", List.of("Error occurred"),
                    "tech3_lessThan100", List.of(),
                    "tech3_nullOrZero", List.of()
            );
        }
    }


   /* @GetMapping("/tech3-lidar-percentage")
    @ResponseBody
    public Map<String, List<String>> getTech3LidarPercentage() {
        try {
            Map<String, List<String>> result = dataVenderService.getTech3LidarPercentage();
            return result;
        } catch (Exception e) {
            logger.error("Error while fetching tech3Percentage data", e);
            return Map.of(
                    "tech3_100", List.of("Error occurred"),
                    "tech3_lessThan100", List.of(),
                    "tech3_nullOrZero", List.of()
            );
        }
    }*/

    @GetMapping("/compare-tech1-tech2-percentage")
    @ResponseBody
    public Map<String, List<String>> getTech1AndTech2Comparison() {
        try {
            Map<String, List<String>> result = dataVenderService.getFlyingCompletedT1AndT2();
             return result;
        } catch (Exception e) {
            logger.error("Error occurred while fetching Tech1/Tech2 comparison", e);
            return Collections.emptyMap();
        }
    }


    @GetMapping("/completion-statuses")
    @ResponseBody
    public List<Map<String, String>> getAllCompletionStatuses() {
        return Arrays.stream(CompletionStatus.values())
                .map(status -> Map.of(
                        "code", String.valueOf(status.getCode()),
                        "description", status.getDescription()
                ))
                .collect(Collectors.toList());
    }





    @GetMapping("/getStates/UTs")
    @ResponseBody
    public List<Map<String, Object>> getAllStatesAndUt() {
        try {
            List<Object[]> rawData = nakshaMISDataService.getStateIdAndStateName();
            return rawData.stream()
                    .map(row -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("stateName", row[1]);
                        return map;
                    })
                    .sorted(Comparator.comparing(m -> m.get("stateName").toString()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error ", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/flying-status-timelines")
    @ResponseBody
    public ResponseEntity<?> getStateReportObject(@RequestParam("state") String stateName) {
        Logger logger = LoggerFactory.getLogger(getClass());

        try {
            List<Map<String, Object>> results = dataVenderService.getDataByStateAsMapList(stateName);

            double totalArea = results.stream()
                    .mapToDouble(r -> {
                        Object val = r.get("bufferArea");
                        return val instanceof Double ? (Double) val : 0.0;
                    })
                    .sum();

            logger.info("Report generated for state: {}, ULBs: {}, Total Area: {}", stateName, results.size(), totalArea);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", "success");
            response.put("stateName", stateName);
            response.put("ulbCount", results.size());
            response.put("totalArea", totalArea);
            response.put("data", results);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error while generating report for state: {}", stateName, e);

            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to fetch report data.");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/flying-completed")
    @ResponseBody
    public Map<String, Object> getFlyingCompletedT1AndT2(@RequestParam(required = false, defaultValue = "all") String stateName) {
        logger.info("Received request for flying completed data. StateName: {}", stateName);

        try {
            Map<String, Object> result = dataVenderService.getFlyingCompletedT1AndT2(stateName);

            logger.info("Returning result: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Error while fetching flying completed data for state: {}", stateName, e);

            return Map.of(
                    "error", "Unable to fetch flying completed data",
                    "message", e.getMessage()
            );
        }
    }


    @GetMapping("/state-wise-soi-summary")
    @ResponseBody
    public Map<String, Object> getStateWiseSummary(
            @RequestParam(required = false, defaultValue = "all") String state) {

        List<NakshaMISDataEntrySolrAndVender> vendorList = dataVenderService.getAllEntries();

        if (state != null && !"all".equalsIgnoreCase(state)) {
            vendorList = vendorList.stream()
                    .filter(v -> state.equalsIgnoreCase(v.getStateName()))
                    .collect(Collectors.toList());
        }

        String stateName = state;
        String gdName = vendorList.isEmpty() ? "" : vendorList.get(0).getGdName();

        Double totalBufferArea = 0.0;
        for (NakshaMISDataEntrySolrAndVender v : vendorList) {
            if (v.getBufferAreaDataAcquisition() != null) {
                totalBufferArea += v.getBufferAreaDataAcquisition();
            }
        }

        // Call service method (returns table + contractors)
        Map<String, List<?>> summaryData = dataVenderService.stateWisSoiSummary(state);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("stateName", stateName);
        response.put("gdName", gdName);
        response.put("allComp", summaryData.get("table"));
        response.put("aerial", summaryData.get("aerialList"));
        response.put("ori", summaryData.get("oriList"));
        response.put("dsm", summaryData.get("dsmList"));
        response.put("dtm", summaryData.get("dtmList"));
        response.put("mesh", summaryData.get("meshList"));
        response.put("twoDfeat", summaryData.get("twoDfeatList"));
        response.put("threeDfeat", summaryData.get("threeDfeatList"));
        response.put("contractors", summaryData.get("contractors"));
        response.put("totalUlb", vendorList.size());
        response.put("totalBufferArea", totalBufferArea);

        return response;
    }

    @GetMapping("/states-soi-summary")
    @ResponseBody
    public Map<String, Object> getStateSoiWiseSummary(@RequestParam(value = "stateName", required = false, defaultValue = "all") String stateName) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> details = dataVenderService.getStateSoiWiseSummary(stateName);

            if (details == null || details.isEmpty()) {
                response.put("status", "error");
                response.put("message", "No data found");
            } else {
                response.put("status", "success");
                response.put("data", details);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Internal Server Error");
        }
        return response;
    }

    @GetMapping("/soi-state-area-summary")
    @ResponseBody
    public List<Map<String, Object>> getAreaSummary() {
        try {

            List<Map<String, Object>> summary = dataVenderService.getAllEntriesAsListOfMap();
            logger.info("Fetched {} records successfully", summary.size());
            return summary;
        } catch (Exception e) {
            logger.error("Error while fetching area summary", e);

            // You can also return an error response map instead of null
            return List.of(Map.of("error", "Failed to fetch area summary"));
        }
    }


    @GetMapping("/sio-state-vendor-status-summary")
    @ResponseBody
    public ResponseEntity<?> getVendorStats(@RequestParam(required = false) String stateName) {
        try {
            if (stateName != null && !stateName.trim().isEmpty()) {
                // 🔹 Fetch stats for one state
                Map<String, Object> stats = dataVenderService.getStateWiseVendorStats(stateName);
                logger.info("Successfully fetched vendor stats for state: {}", stateName);
                return ResponseEntity.ok(stats);
            } else {
                // 🔹 Fetch stats for all states
                List<Map<String, Object>> allStats = dataVenderService.getAllContractorWiseStats();
                logger.info("Successfully fetched vendor stats for all states");
                return ResponseEntity.ok(allStats);
            }
        } catch (Exception ex) {
            logger.error("Error fetching vendor stats", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Unable to fetch vendor stats"));
        }
    }

    @GetMapping("/sio-state-gd-status-summary")
    @ResponseBody
    public ResponseEntity<?> getGdStats(@RequestParam(required = false) String stateName) {
        try {
            if (stateName != null && !stateName.trim().isEmpty()) {
                // 🔹 Fetch stats for one state
                Map<String, Object> stats = dataVenderService.getStateWiseGdStats(stateName);
                logger.info("Successfully fetched vendor stats for state: {}", stateName);
                return ResponseEntity.ok(stats);
            } else {
                // 🔹 Fetch stats for all states
                List<Map<String, Object>> allStats = dataVenderService.getAllGdWiseStats();
                logger.info("Successfully fetched vendor stats for all states");
                return ResponseEntity.ok(allStats);
            }
        } catch (Exception ex) {
            logger.error("Error fetching vendor stats", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Unable to fetch vendor stats"));
        }
    }


    // ✅ Controller Layer
    @GetMapping("/backup-timelines-dates")
    public ResponseEntity<List<String>> getBackupDates() {
        try {
            List<String> dates = timelineBackupService.getAllBackupDates();
            return ResponseEntity.ok(dates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }



    @GetMapping("/flying-status-timelines-backup")
    @ResponseBody
    public ResponseEntity<?> geFlyingStatusTimelinesBackup(@RequestParam("state") String stateName, @RequestParam("backupDate") String backupDate) {
        Logger logger = LoggerFactory.getLogger(getClass());

        try {
            List<Map<String, Object>> results = dataVenderService.getStateTimelineAsMapList(stateName,backupDate);

            double totalArea = results.stream()
                    .mapToDouble(r -> {
                        Object val = r.get("bufferArea");
                        return val instanceof Double ? (Double) val : 0.0;
                    })
                    .sum();

            logger.info("Report generated for state: {}, ULBs: {}, Total Area: {}", stateName, results.size(), totalArea);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("backupStatus", "success");
            response.put("backupStateName", stateName);
            response.put("backupUlbCount", results.size());
            response.put("backupTotalArea", totalArea);
            response.put("backupDdata", results);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error while generating report for state: {}", stateName, e);

            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("backupStatus", "error");
            errorResponse.put("message", "Failed to fetch report data.");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/compare-tech1-tech2-tech3-percentage")
    @ResponseBody
    public Map<String, List<String>> getFlyingCompletedT1T2T3() {
        try {
            logger.info("Fetching Flying Completed data for Tech1, Tech2, Tech3...");
            Map<String, List<String>> result = dataVenderService.getFlyingCompletedT1AndT2AndT3();
            logger.info("Flying Completed data fetched successfully. Sizes: {}",
                    result.entrySet().stream()
                            .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size())));
            return result;
        } catch (Exception ex) {
            logger.error("Error while fetching Flying Completed T1/T2/T3 data", ex);
            // return empty map instead of breaking JS
            return Collections.emptyMap();
        }
    }



    @GetMapping("/data-uploaded-by-gd-naksha-portal")
    @ResponseBody
    public Map<String, Object> dataUploadedByGDsOnNAKSHAPorta() {
        Map<String, Object> result = new HashMap<>();
        try {
            result = dataVenderService.dataUploadedByGDsOnNAKSHAPorta();
            logger.info("Data Uploaded by GDs on NAKSHA Portal, count: {}", result.get("count"));
        } catch (Exception e) {
            logger.error("Error occurred while fetching data from database", e);
            result.put("error", "Internal server error occurred");
        }
        return result;
    }


}
