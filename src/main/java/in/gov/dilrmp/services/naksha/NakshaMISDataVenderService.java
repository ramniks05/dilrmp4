package in.gov.dilrmp.services.naksha;

import in.gov.dilrmp.models.naksha.NakshaMISDataEntrySolrAndVender;
import in.gov.dilrmp.models.naksha.TimelineBackup;
import in.gov.dilrmp.models.naksha.ULBMaster;
import in.gov.dilrmp.repositories.naksha.NakshaMISDataVenderRepository;
import in.gov.dilrmp.repositories.naksha.TimelineBackupRepository;
import in.gov.dilrmp.repositories.naksha.ULBMsterRepository;
import in.gov.dilrmp.utils.Agency;
import in.gov.dilrmp.utils.CompletionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class NakshaMISDataVenderService {

    @Autowired
    NakshaMISDataVenderRepository vendorRepository;

    @Autowired
    private ULBMsterRepository ulbMasterRepository;

    @Autowired
    private TimelineBackupRepository timelineBackupRepository;

    private List<NakshaMISDataEntrySolrAndVender> vendor;

    public void loadVendorData() {
        vendor = vendorRepository.findAll();
    }

    public List<NakshaMISDataEntrySolrAndVender> getAllEntries() {
        return vendor;
    }
    private List<TimelineBackup> vendorTimeBackups;
    public void loadTimelineBackupData() {
        vendorTimeBackups = timelineBackupRepository.findAll();
    }

    public List<TimelineBackup> getAllTimelineEntries() {
        return vendorTimeBackups;
    }


    public List<Map<String, Object>> getContractorCountByState() {
        Map<String, Set<String>> contractorSetByState = new HashMap<>();

        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            String state = entry.getStateName();
            String contractor = entry.getContractor();
            if (state == null || contractor == null || contractor.trim().isEmpty()) continue;

            contractorSetByState
                    .computeIfAbsent(state, k -> new HashSet<>())
                    .add(contractor);
        }

        List<Map<String, Object>> result = new ArrayList<>();

        contractorSetByState.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // ORDER BY stateName
                .forEach(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("stateName", entry.getKey());
                    map.put("contractorCount", entry.getValue().size());
                    result.add(map);
                });

        return result;
    }


    public List<Map<String, Object>> getSanctionedAreaByState() {
        Map<String, Double> sanctionedAreaByState = new HashMap<>();

        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            String state = entry.getStateName();
            if (state == null) continue;

            double sanctionedArea = entry.getSanctionedArea() != null ? entry.getSanctionedArea() : 0.0;

            sanctionedAreaByState.merge(state, sanctionedArea, Double::sum);
        }

        List<Map<String, Object>> result = new ArrayList<>();

        sanctionedAreaByState.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // order by stateName
                .forEach(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("stateName", entry.getKey());
                    map.put("totalSanctionedArea", entry.getValue());
                    result.add(map);
                });

        return result;
    }


    public List<Map<String, Object>> getBufferAreaByState() {
        Map<String, Double> bufferAreaByState = new HashMap<>();

        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            String state = entry.getStateName();
            if (state == null) continue;

            double bufferArea = entry.getBufferAreaDataAcquisition() != null ? entry.getBufferAreaDataAcquisition() : 0.0;

            bufferAreaByState.merge(state, bufferArea, Double::sum);
        }

        List<Map<String, Object>> result = new ArrayList<>();

        bufferAreaByState.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // order by stateName
                .forEach(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("stateName", entry.getKey());
                    map.put("totalBufferArea", entry.getValue());
                    result.add(map);
                });

        return result;
    }


    public List<Map<String, Object>> getAreaSummaryByTechnology() {
        Map<String, double[]> techSummary = new HashMap<>();

        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            String tech = entry.getTechnology();
            if (tech == null) continue;

            double sanctioned = entry.getSanctionedArea() != null ? entry.getSanctionedArea() : 0.0;
            double buffer = entry.getBufferAreaDataAcquisition() != null ? entry.getBufferAreaDataAcquisition() : 0.0;

            techSummary
                    .computeIfAbsent(tech, k -> new double[2]);

            techSummary.get(tech)[0] += sanctioned;
            techSummary.get(tech)[1] += buffer;
        }

        List<Map<String, Object>> result = new ArrayList<>();

        techSummary.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // sort by technology
                .forEach(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("technology", entry.getKey());
                    map.put("totalSanctionedArea", entry.getValue()[0]);
                    map.put("totalBufferArea", entry.getValue()[1]);
                    result.add(map);
                });

        return result;
    }


    public List<Map<String, Object>> getAreaSummaryByStateAndTechnology() {
        Map<String, Map<String, double[]>> groupedData = new HashMap<>();

        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            String state = entry.getStateName();
            String tech = entry.getTechnology();

            if (state == null || tech == null) continue;

            double sanctioned = entry.getSanctionedArea() != null ? entry.getSanctionedArea() : 0.0;
            double buffer = entry.getBufferAreaDataAcquisition() != null ? entry.getBufferAreaDataAcquisition() : 0.0;

            groupedData
                    .computeIfAbsent(state, k -> new HashMap<>())
                    .computeIfAbsent(tech, k -> new double[2]);

            groupedData.get(state).get(tech)[0] += sanctioned;
            groupedData.get(state).get(tech)[1] += buffer;
        }

        List<Map<String, Object>> result = new ArrayList<>();

        groupedData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // sort by stateName
                .forEach(stateEntry -> {
                    String state = stateEntry.getKey();
                    Map<String, double[]> techMap = stateEntry.getValue();

                    techMap.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey()) // sort by technology
                            .forEach(techEntry -> {
                                String tech = techEntry.getKey();
                                double[] values = techEntry.getValue();

                                Map<String, Object> map = new HashMap<>();
                                map.put("stateName", state);
                                map.put("technology", tech);
                                map.put("totalSanctionedArea", values[0]);
                                map.put("totalBufferArea", values[1]);
                                result.add(map);
                            });
                });

        return result;
    }


    public Map<String, List<Map<String, Object>>> getAllSummaryData() {
        Map<String, List<Map<String, Object>>> all = new HashMap<>();
        all.put("contractorCountByState", getContractorCountByState());
        all.put("sanctionedAreaByState", getSanctionedAreaByState());
        all.put("bufferAreaByState", getBufferAreaByState());
        all.put("areaByTechnology", getAreaSummaryByTechnology());
        all.put("areaByStateAndTechnology", getAreaSummaryByStateAndTechnology());
        return all;
    }

    /**
     * Fetches the count of distinct technologies for each ULB.
     * Converts the raw Object[] result into a list of maps for easy usage in controller/response.
     */
    public List<Map<String, Object>> fetchUlbCountPerTechnology() {
        Map<String, Set<String>> techToUlbMap = new HashMap<>();

        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            String tech = entry.getTechnology();
            String ulb = entry.getUlbName();

            if (tech == null || tech.trim().isEmpty() || ulb == null || ulb.trim().isEmpty()) {
                continue;
            }

            techToUlbMap.computeIfAbsent(tech, k -> new HashSet<>()).add(ulb);
        }

        List<Map<String, Object>> result = techToUlbMap.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("technology", e.getKey());
                    map.put("ulbCount", e.getValue().size());
                    return map;
                })
                .sorted(Comparator.comparing(m -> String.valueOf(m.get("technology")))) // order by technology
                .collect(Collectors.toList());

        return result;
    }


    // Calls the repository to retrieve technology-wise area aggregation for comparison.
    public List<Object[]> getComAreaByTechnology() {
        try {
            Map<String, double[]> techAreaMap = new HashMap<>();

            for (NakshaMISDataEntrySolrAndVender entry : vendor) {
                String tech = entry.getTechnology();
                if (tech == null || tech.trim().isEmpty()) continue;

                double sanctionedArea = entry.getSanctionedArea() != null ? entry.getSanctionedArea().doubleValue() : 0.0;
                double bufferArea = entry.getBufferAreaDataAcquisition() != null ? entry.getBufferAreaDataAcquisition().doubleValue() : 0.0;

                techAreaMap
                        .computeIfAbsent(tech, k -> new double[2])[0] += sanctionedArea;

                techAreaMap.get(tech)[1] += bufferArea;
            }

            // Convert to List<Object[]> and sort by technology name (ascending)
            List<Object[]> result = techAreaMap.entrySet().stream()
                    .map(e -> new Object[]{e.getKey(), e.getValue()[0], e.getValue()[1]})
                    .sorted(Comparator.comparing(o -> (String) o[0])) // sort by technology ASC
                    .collect(Collectors.toList());

            return result;
        } catch (Exception e) {
            e.printStackTrace(); // optionally log this
            return List.of();
        }
    }

    public List<Object[]> getComAreaByContractor() {
        try {
            Map<String, double[]> contractorAreaMap = new HashMap<>();

            for (NakshaMISDataEntrySolrAndVender entry : vendor) {
                String contractor = entry.getContractor();
                if (contractor == null || contractor.trim().isEmpty()) continue;

                double sanctionedArea = entry.getSanctionedArea() != null ? entry.getSanctionedArea().doubleValue() : 0.0;
                double bufferArea = entry.getBufferAreaDataAcquisition() != null ? entry.getBufferAreaDataAcquisition().doubleValue() : 0.0;

                contractorAreaMap
                        .computeIfAbsent(contractor, k -> new double[2])[0] += sanctionedArea;

                contractorAreaMap.get(contractor)[1] += bufferArea;
            }

            // Convert map to List<Object[]> and sort descending by sanctioned area
            List<Object[]> result = contractorAreaMap.entrySet().stream()
                    .map(e -> new Object[]{e.getKey(), e.getValue()[0], e.getValue()[1]})
                    .sorted((a, b) -> Double.compare((Double) b[1], (Double) a[1])) // sort by sanctionedArea DESC
                    .collect(Collectors.toList());

            return result;
        } catch (Exception e) {
            e.printStackTrace(); // optionally log
            return List.of();
        }
    }


    public List<Object[]> getComAreaByStater() {
        try {
            Map<String, double[]> stateAreaMap = new HashMap<>();

            for (NakshaMISDataEntrySolrAndVender entry : vendor) {
                String stateName = entry.getStateName();
                if (stateName == null || stateName.trim().isEmpty()) continue;

                double sanctionedArea = entry.getSanctionedArea() != null ? entry.getSanctionedArea().doubleValue() : 0.0;
                double bufferArea = entry.getBufferAreaDataAcquisition() != null ? entry.getBufferAreaDataAcquisition().doubleValue() : 0.0;

                stateAreaMap
                        .computeIfAbsent(stateName, k -> new double[2])[0] += sanctionedArea;

                stateAreaMap.get(stateName)[1] += bufferArea;
            }

            // Convert to List<Object[]> and sort descending by sanctionedArea
            List<Object[]> result = stateAreaMap.entrySet().stream()
                    .map(e -> new Object[]{e.getKey(), e.getValue()[0], e.getValue()[1]})
                    .sorted((a, b) -> Double.compare((Double) b[1], (Double) a[1])) // sort by sanctionedArea DESC
                    .collect(Collectors.toList());

            return result;
        } catch (Exception e) {
            e.printStackTrace(); // optional: use a logger
            return List.of();
        }
    }


    // Fetches the count of distinct ULBs under each GD (Geographical Division),

    public List<Object[]> getUlbCountGroupedByGd() {
        Map<String, Set<String>> gdToUlbMap = new HashMap<>();

        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            String gdName = entry.getGdName();
            String ulbName = entry.getUlbName();

            // Skip null or empty values
            if (gdName != null && ulbName != null && !ulbName.trim().isEmpty()) {
                gdToUlbMap.computeIfAbsent(gdName, k -> new HashSet<>()).add(ulbName);
            }
        }

        // Convert to List<Object[]> and sort by count DESC
        List<Object[]> result = gdToUlbMap.entrySet().stream()
                .map(e -> new Object[]{e.getKey(), e.getValue().size()})
                .sorted((a, b) -> Integer.compare((int) b[1], (int) a[1]))
                .collect(Collectors.toList());

        return result;
    }

    public List<Object[]> getCumulativeData() {
        List<Object[]> result = new ArrayList<>();
        vendor.sort(Comparator.comparing(NakshaMISDataEntrySolrAndVender::getUlbName, Comparator.nullsLast(String::compareToIgnoreCase)));

        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            Object[] row = new Object[15];
            row[0] = entry.getId();
            row[1] = entry.getUlbName();
            row[2] = entry.getTech1Cumulative() != null ? entry.getTech1Cumulative() : 0;
            row[3] = entry.getTech2Cumulative() != null ? entry.getTech2Cumulative() : 0;
            row[4] = entry.getTech3Cumulative() != null ? entry.getTech3Cumulative() : 0;
            row[5] = entry.getTech1Percentage() != null ? entry.getTech1Percentage() : 0;
            row[6] = entry.getTech2Percentage() != null ? entry.getTech2Percentage() : 0;
            row[7] = entry.getTech3Percentage() != null ? entry.getTech3Percentage() : 0;
            row[8] = entry.getTechnology() != null ? entry.getTechnology() : "";

            row[9] = entry.getTech3LidarSensor() != null ? entry.getTech3LidarSensor() : 0;
            row[10] = entry.getTech3LidarSensorPercentage() != null ? entry.getTech3LidarSensorPercentage() : 0;

            // 🔽 Set default statuses
            String techStatus = CompletionStatus.fromCode(entry.getTechStatus() != null ? entry.getTechStatus() : CompletionStatus.NOT_STARTED.getCode()).getDescription();
            String tech2ObliqueStatus = CompletionStatus.fromCode(entry.getTech2ObliqueStatus() != null ? entry.getTech2ObliqueStatus() : CompletionStatus.NOT_STARTED.getCode()).getDescription();
            String tech3ObliqueStatus = CompletionStatus.fromCode(entry.getTech3ObliqueStatus() != null ? entry.getTech3ObliqueStatus() : CompletionStatus.NOT_STARTED.getCode()).getDescription();

            String lidarStatus = CompletionStatus.fromCode(entry.getTech3LidarSensorStatus() != null ? entry.getTech3LidarSensorStatus() : CompletionStatus.NOT_STARTED.getCode()).getDescription();

            String technology = entry.getTechnology();

            // 🔽 Apply custom logic based on ULB's technology
            if ("Tech 1".equalsIgnoreCase(technology)) {
                tech2ObliqueStatus = CompletionStatus.NOT_AVAILABLE.getDescription();
                tech3ObliqueStatus = CompletionStatus.NOT_AVAILABLE.getDescription();
            } else if ("Tech 2".equalsIgnoreCase(technology)) {
                techStatus = CompletionStatus.NOT_AVAILABLE.getDescription();
                tech3ObliqueStatus = CompletionStatus.NOT_AVAILABLE.getDescription();
            } else if ("Tech 3".equalsIgnoreCase(technology)) {
                techStatus = CompletionStatus.NOT_AVAILABLE.getDescription();
                tech2ObliqueStatus = CompletionStatus.NOT_AVAILABLE.getDescription();
            }

            row[11] = techStatus;
            row[12] = tech2ObliqueStatus;
            row[13] = tech3ObliqueStatus;
            row[14] = lidarStatus;

            result.add(row);
        }

        return result;
    }


    public List<Map<String, Object>> getStateSoiWiseSummaryFromCache() {
        return vendor.stream()
                .filter(e -> e.getStateName() != null) // avoid null states
                .collect(Collectors.groupingBy(NakshaMISDataEntrySolrAndVender::getStateName))
                .entrySet()
                .stream()
                .map(entry -> {
                    String state = entry.getKey();
                    List<NakshaMISDataEntrySolrAndVender> stateList = entry.getValue();

                    Map<String, Object> map = new HashMap<>();
                    map.put("state", state);
                    map.put("ulbs", stateList.stream().map(NakshaMISDataEntrySolrAndVender::getUlbName).filter(Objects::nonNull).collect(Collectors.toSet()).size());
                    map.put("vendors", stateList.stream().map(NakshaMISDataEntrySolrAndVender::getContractor).filter(Objects::nonNull).collect(Collectors.toSet()).size());
                    map.put("sanctionedArea", stateList.stream().map(NakshaMISDataEntrySolrAndVender::getSanctionedArea).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum());
                    map.put("bufferArea", stateList.stream().map(NakshaMISDataEntrySolrAndVender::getBufferAreaDataAcquisition).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum());
                    map.put("technologyCount", stateList.stream().map(NakshaMISDataEntrySolrAndVender::getTechnology).filter(Objects::nonNull).collect(Collectors.toSet()).size());
                    map.put("tech1", stateList.stream().map(NakshaMISDataEntrySolrAndVender::getTech1Cumulative).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum());
                    map.put("tech2", stateList.stream().map(NakshaMISDataEntrySolrAndVender::getTech2Cumulative).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum());
                    map.put("tech3", stateList.stream().map(NakshaMISDataEntrySolrAndVender::getTech3Cumulative).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum());
                    map.put("tech3Lidar", stateList.stream().map(NakshaMISDataEntrySolrAndVender::getTech3LidarSensor).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum());

                    return map;
                })
                .sorted(Comparator.comparing(m -> m.get("state").toString())) // sort by state name
                .collect(Collectors.toList());
    }


    public List<List<Object>> getGridSummary() {
        return vendor.stream()
                .sorted(Comparator.comparing(NakshaMISDataEntrySolrAndVender::getUlbName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(e -> {
                    List<Object> rowData = new ArrayList<>();
                    // rowData.add(e.getId()); // Uncomment if ID is needed
                    rowData.add(e.getId());
                    rowData.add(e.getUlbName());
                    rowData.add((e.getTotalGrids() != null && !e.getTotalGrids().trim().isEmpty()) ? e.getTotalGrids() : "0");
                    rowData.add((e.getGridsCompletedLastWeek() != null && !e.getGridsCompletedLastWeek().trim().isEmpty()) ? e.getGridsCompletedLastWeek() : "0");
                    rowData.add((e.getTotalGridsCompleted() != null && !e.getTotalGridsCompleted().trim().isEmpty()) ? e.getTotalGridsCompleted() : "0");


                    return rowData;
                })
                .collect(Collectors.toList());
    }


    public List<Object[]> getUlbDetailsById(Long id) {
        return vendor.stream()
                .filter(e -> e.getId() != null && e.getId().equals(id))
                .map(e -> new Object[]{
                        e.getUlbName(),
                        e.getStateName(),
                        e.getDistrict_Name(),
                        e.getGdName(),
                        e.getContractor(),
                        e.getTechnology(),
                        e.getSanctionedArea(),
                        e.getBufferAreaDataAcquisition()
                })
                .collect(Collectors.toList());
    }

 /*   public Map<String, Object> compareGdNames() {
        List<Object[]> rawUlbMasterData = ulbMasterRepository.getULCountByGD();

        Map<String, String> ulbGdStateMap = rawUlbMasterData.stream()
                .filter(row -> row[1] != null && row[2] != null)
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[1]),
                        row -> String.valueOf(row[2]),
                        (s1, s2) -> s1
                ));

        Map<String, String> vendorGdStateMap = vendor.stream()
                .filter(v -> v.getGdName() != null && v.getStateName() != null)
                .collect(Collectors.toMap(
                        NakshaMISDataEntrySolrAndVender::getGdName,
                        NakshaMISDataEntrySolrAndVender::getStateName,
                        (s1, s2) -> s1
                ));

        List<String> presentInVender = ulbGdStateMap.entrySet().stream()
                .filter(e -> vendorGdStateMap.containsKey(e.getKey()))
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .sorted()
                .collect(Collectors.toList());

        List<String> notInVender = ulbGdStateMap.entrySet().stream()
                .filter(e -> !vendorGdStateMap.containsKey(e.getKey()))
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .sorted()
                .collect(Collectors.toList());

        // ✅ Collect unique states only
        Set<String> uniqueStates = new HashSet<>();
        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            if (entry.getStateName() != null) {
                uniqueStates.add(entry.getStateName()); // ✅ make sure this is getStateName()
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("presentInVender", presentInVender);
        result.put("notInVender", notInVender);
        result.put("gdStates", new ArrayList<>(uniqueStates)); // ✅ now only state names

        return result;
    }
*/


    public Map<String, Object> compareGdNames() {
        // Step 1: Build GD -> States map from ULB Master (allow multiple states per GD)
        List<ULBMaster> ulbMasterList = ulbMasterRepository.findAll();

        Map<String, List<String>> ulbGdStateMap = ulbMasterList.stream()
                .filter(u -> u.getGdName() != null && u.getState_name() != null)
                .collect(Collectors.groupingBy(
                        ULBMaster::getGdName,
                        Collectors.mapping(ULBMaster::getState_name, Collectors.toList())
                ));

        // Step 2: Extract GD names from vendor list
        Set<String> vendorGdNames = vendor.stream()
                .map(NakshaMISDataEntrySolrAndVender::getGdName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Step 3: Compare sets (include ALL states in output)
        List<String> presentInVender = ulbGdStateMap.entrySet().stream()
                .filter(e -> vendorGdNames.contains(e.getKey()))
                .flatMap(e -> e.getValue().stream()
                        .map(state -> e.getKey() + " (" + state + ")"))
                .sorted()
                .collect(Collectors.toList());

        List<String> notInVender = ulbGdStateMap.entrySet().stream()
                .filter(e -> !vendorGdNames.contains(e.getKey()))
                .flatMap(e -> e.getValue().stream()
                        .map(state -> e.getKey() + " (" + state + ")"))
                .sorted()
                .collect(Collectors.toList());

        // Step 4: Collect unique states for dropdown
        List<String> gdStates = ulbGdStateMap.values().stream()
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .map(String::trim)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Step 5: Build result map
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("presentInVender", presentInVender);
        result.put("notInVender", notInVender);
        result.put("gdStates", gdStates);
        result.put("gdToStates", ulbGdStateMap); // ✅ keep full GD -> States mapping

        return result;
    }





    public Map<String, List<Map<String, Object>>> compareUlbNames() {
        // 1. Fetch ULB Master Data
        List<ULBMaster> ulbMaster = ulbMasterRepository.findAll();

        // 2. Extract all unique ULBs
        List<Map<String, Object>> rawUlbMaster = ulbMaster.stream()
                .filter(u -> u.getUlb_name() != null && !u.getUlb_name().trim().isEmpty())
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("ulbName", u.getUlb_name().trim());
                    map.put("stateId", u.getState_id());
                    map.put("stateName", u.getState_name());
                    return map;
                })
                .distinct()
                .sorted(Comparator.comparing(m -> m.get("ulbName").toString()))
                .collect(Collectors.toList());

        // 3. ULBs with flying_zone == 0
        List<Map<String, Object>> ulbNamesWithFlyingZoneZero = ulbMaster.stream()
                .filter(v -> v.getFlyingZone() != null && v.getFlyingZone() == 0)
                .filter(v -> v.getUlb_name() != null && !v.getUlb_name().trim().isEmpty())
                .map(v -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("ulbName", v.getUlb_name().trim());
                    map.put("stateId", v.getState_id());
                    map.put("stateName", v.getState_name());
                    return map;
                })
                .distinct()
                .sorted(Comparator.comparing(m -> m.get("ulbName").toString()))
                .collect(Collectors.toList());

        // 4. Vendor ULB names
        Set<String> nakshaNamesSet = vendor.stream()
                .map(NakshaMISDataEntrySolrAndVender::getUlbName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toSet());

        // 5. Present in Naksha
        List<Map<String, Object>> presentInNaksha = rawUlbMaster.stream()
                .filter(ulb -> nakshaNamesSet.contains(ulb.get("ulbName").toString()))
                .collect(Collectors.toList());

        // 6. Not in Naksha but flyingZone == 1
        List<Map<String, Object>> notInNaksha = ulbMaster.stream()
                .filter(u -> u.getFlyingZone() != null && u.getFlyingZone() == 1)
                .filter(u -> u.getUlb_name() != null && !u.getUlb_name().trim().isEmpty())
                .filter(u -> !nakshaNamesSet.contains(u.getUlb_name().trim()))
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("ulbName", u.getUlb_name().trim());
                    map.put("stateId", u.getState_id());
                    map.put("stateName", u.getState_name());
                    return map;
                })
                .distinct()
                .sorted(Comparator.comparing(m -> m.get("ulbName").toString()))
                .collect(Collectors.toList());

        // 7. Return
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("presentInNaksha", presentInNaksha);
        result.put("notInNaksha", notInNaksha);
        result.put("notflying_zone", ulbNamesWithFlyingZoneZero);
        return result;
    }



    public Map<String, Object> compareAgencyWithDatabase() {
        // Contractor -> { states: Set<String>, ulbsByState: Map<String, Set<String>> }
        Map<String, Map<String, Object>> contractorDetailsMap = new HashMap<>();

        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            String contractor = entry.getContractor();
            String stateName = entry.getStateName();
            String ulbName = entry.getUlbName();

            if (contractor != null && !contractor.trim().isEmpty()) {
                contractor = contractor.trim();

                // ✅ Initialize inner structure if absent
                contractorDetailsMap.computeIfAbsent(contractor, k -> {
                    Map<String, Object> details = new HashMap<>();
                    details.put("states", new HashSet<String>());
                    details.put("ulbsByState", new HashMap<String, Set<String>>());
                    return details;
                });

                Map<String, Object> details = contractorDetailsMap.get(contractor);

                // ✅ Add state
                if (stateName != null && !stateName.trim().isEmpty()) {
                    ((Set<String>) details.get("states")).add(stateName.trim());

                    // ✅ Add ULB under this state
                    if (ulbName != null && !ulbName.trim().isEmpty()) {
                        Map<String, Set<String>> ulbsByState = (Map<String, Set<String>>) details.get("ulbsByState");
                        ulbsByState.computeIfAbsent(stateName.trim(), k -> new HashSet<>()).add(ulbName.trim());
                    }
                }
            }
        }

        List<String> presentInDatabase = new ArrayList<>();
        List<String> notInDatabase = new ArrayList<>();

        for (Agency company : Agency.values()) {
            String displayName = company.getDisplayName().trim();

            if (contractorDetailsMap.containsKey(displayName)) {
                presentInDatabase.add(displayName);
            } else {
                notInDatabase.add(displayName);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("presentInDatabase", presentInDatabase.stream().sorted().collect(Collectors.toList()));
       /* result.put("notInDatabase", notInDatabase.stream().sorted().collect(Collectors.toList()));*/
        result.put("contractorDetails", contractorDetailsMap);

        return result;
    }





    /*public Map<String, List<String>> getStatesByContractor() {
        // Create a map where key = Contractor name, value = list of states
        Map<String, List<String>> contractorStatesMap = new HashMap<>();

        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            String contractor = entry.getContractor();
            String stateName = entry.getStateName();

            if (contractor != null && !contractor.trim().isEmpty() &&
                    stateName != null && !stateName.trim().isEmpty()) {

                contractor = contractor.trim();
                stateName = stateName.trim();

                // Group states under contractor
                contractorStatesMap
                        .computeIfAbsent(contractor, k -> new ArrayList<>())
                        .add(stateName);
            }
        }

        // Sort the states list for each contractor
        for (Map.Entry<String, List<String>> entry : contractorStatesMap.entrySet()) {
            entry.getValue().sort(String::compareToIgnoreCase);
        }

        return contractorStatesMap;
    }*/



    public Map<String, Integer> getProcessingCounts(String gdName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = "all".equalsIgnoreCase(gdName)
                ? vendor
                : vendor.stream()
                .filter(e -> gdName.equalsIgnoreCase(e.getGdName()))
                .collect(Collectors.toList());

        return buildProcessingCounts(filteredList);
    }

    public Map<String, Integer> getProcessingCountsByState(String stateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = "all".equalsIgnoreCase(stateName)
                ? vendor
                : vendor.stream()
                .filter(e -> stateName.equalsIgnoreCase(e.getStateName()))
                .collect(Collectors.toList());

        return buildProcessingCounts(filteredList);
    }

    private Map<String, Integer> buildProcessingCounts(List<NakshaMISDataEntrySolrAndVender> filteredList) {
        Map<String, Integer> counts = new LinkedHashMap<>();

        int uniqueUlbCount = (int) filteredList.stream()
                .map(NakshaMISDataEntrySolrAndVender::getUlbName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .distinct()
                .count();

        counts.put("ulb", uniqueUlbCount);


        // String-based status check
        putIfIntegerStatusAndDoubleValue(filteredList, NakshaMISDataEntrySolrAndVender::getTechStatus,
                counts, "t1", NakshaMISDataEntrySolrAndVender::getTech1Cumulative);


        putIfIntegerStatusAndDoubleValue(filteredList, NakshaMISDataEntrySolrAndVender::getTech2ObliqueStatus,
                counts, "t2", NakshaMISDataEntrySolrAndVender::getTech2Cumulative);

        putIfIntegerStatusAndDoubleValue(filteredList, NakshaMISDataEntrySolrAndVender::getTech3ObliqueStatus,
                counts, "t3", NakshaMISDataEntrySolrAndVender::getTech3Cumulative);

        putIfIntegerStatusAndDoubleValue(filteredList, NakshaMISDataEntrySolrAndVender::getTech3LidarSensorStatus,
                counts, "tl3", NakshaMISDataEntrySolrAndVender::getTech3LidarSensor);

        putIfStatus(filteredList, NakshaMISDataEntrySolrAndVender::getOriCumulativestatus,
                counts, "ori", NakshaMISDataEntrySolrAndVender::getSeparateOriDataProcessingCumulative);

        putIfStatus(filteredList, NakshaMISDataEntrySolrAndVender::getOriCumulativeQaQcStatus,
                counts, "ori_qaqc", NakshaMISDataEntrySolrAndVender::getSeparateOriQaqcDataProcessingCumulative);

        putIfStatus(filteredList, NakshaMISDataEntrySolrAndVender::getDsmSubmissionStatus,
                counts, "dsm", NakshaMISDataEntrySolrAndVender::getDsmDataProcessingCumulative);

        putIfStatus(filteredList, NakshaMISDataEntrySolrAndVender::getDsmSubmissionQaQcStatus,
                counts, "dsm_qaqc", NakshaMISDataEntrySolrAndVender::getDsmDataProcessingQaqcCumulative);

        putIfStatus(filteredList, NakshaMISDataEntrySolrAndVender::getDtmSubmissionStatus,
                counts, "dtm", NakshaMISDataEntrySolrAndVender::getDtmDataProcessingCumulative);

        putIfStatus(filteredList, NakshaMISDataEntrySolrAndVender::getDtmSubmissionQaQcStatus,
                counts, "dtm_qaqc", NakshaMISDataEntrySolrAndVender::getDtmDataProcessingQaqcCumulative);

        putIfStatus(filteredList, NakshaMISDataEntrySolrAndVender::getThreedMeshModelStatus,
                counts, "mesh", NakshaMISDataEntrySolrAndVender::getMeshDataProcessingCumulative);

        putIfStatus(filteredList, NakshaMISDataEntrySolrAndVender::getThreedMeshModelQaQcStatus,
                counts, "mesh_qaqct", NakshaMISDataEntrySolrAndVender::getMeshDataProcessingQaqcCumulative);

        // Double-based percentage check
        putIfIntegerStatusAndDoubleValue(filteredList, NakshaMISDataEntrySolrAndVender::getTwoDfeatureextractionStatus,
                counts, "feature_extraction_ori", NakshaMISDataEntrySolrAndVender::getFeatureExtractionOriCumulativeCompleted);

        putIfIntegerStatusAndDoubleValue(filteredList, NakshaMISDataEntrySolrAndVender::getTwoDfeatureextractionQaQcStatus,
                counts, "feature_extraction_ori_qaqc", NakshaMISDataEntrySolrAndVender::getFeatureExtractionOriQaqcCumulativeCompleted);

        putIfIntegerStatusAndDoubleValue(filteredList, NakshaMISDataEntrySolrAndVender::getTwoDFeatextrStereomodeStatus,
                counts, "2dFeatureStereo", NakshaMISDataEntrySolrAndVender::getTwoDFeatextrStereomodeCumulative);

        putIfIntegerStatusAndDoubleValue(filteredList, NakshaMISDataEntrySolrAndVender::getTwoDFeatextrStereomodeQaQcStatus,
                counts, "2dFeatureStereoExt", NakshaMISDataEntrySolrAndVender::getTwoDFeatextrStereomodeCumulativeQaQc);

        // Custom filtering for 3D Feature Extraction Submission to exclude Tech 1 (same logic as table)
        long count3dFeature = filteredList.stream()
                .filter(e -> {
                    Integer status = e.getThreeDFeatextrStatus();
                    return status != null && (
                            status == CompletionStatus.COMPLETED.getCode() ||
                                    status == CompletionStatus.RE_FLY_COMPLETED.getCode() ||
                                    status == CompletionStatus.COMPLETED_POST_CORRECTION.getCode() ||
                                    status == CompletionStatus.ACCEPTED.getCode() ||
                                    status == CompletionStatus.ACCEPTED_AFTER_CORRECTION.getCode()
                    );
                })
                /*.filter(e -> {
                    Double value = e.getThreeDFeatextrCumulative();
                    return value != null && value > 0.0;
                })*/
                .filter(e -> !isTech1(e.getTechnology())) // Exclude Tech 1 records to match table logic
                .count();
        counts.put("3dFeature", (int) count3dFeature);

//        putIfIntegerStatusAndDoubleValue(filteredList, NakshaMISDataEntrySolrAndVender::getThreeDFeatextrStatus,
//                counts, "3dFeature", NakshaMISDataEntrySolrAndVender::getThreeDFeatextrCumulative);

        // Custom filtering for 3D Feature Extraction QA/QC to exclude Tech 1 (same logic as table)
        long count3dFeatureExt = filteredList.stream()
                .filter(e -> {
                    Integer status = e.getThreeDFeatextrQaQcStatus();
                    return status != null && (
                            status == CompletionStatus.COMPLETED.getCode() ||
                                    status == CompletionStatus.RE_FLY_COMPLETED.getCode() ||
                                    status == CompletionStatus.COMPLETED_POST_CORRECTION.getCode() ||
                                    status == CompletionStatus.ACCEPTED.getCode() ||
                                    status == CompletionStatus.ACCEPTED_AFTER_CORRECTION.getCode()
                    );
                })
               /* .filter(e -> {
                    Double value = e.getThreeDFeatextrCumulativeQaQc();
                    return value != null && value > 0.0;
                })*/
                .filter(e -> !isTech1(e.getTechnology())) // Exclude Tech 1 records to match table logic
                .count();
        counts.put("3dFeatureExt", (int) count3dFeatureExt);


//        putIfIntegerStatusAndDoubleValue(filteredList, NakshaMISDataEntrySolrAndVender::getThreeDFeatextrQaQcStatus,
//                counts, "3dFeatureExt", NakshaMISDataEntrySolrAndVender::getThreeDFeatextrCumulativeQaQc);


        putIfIntegerStatusAndDoubleValue(
                filteredList,
                NakshaMISDataEntrySolrAndVender::getThreeDmilestoneStatus,
                counts,
                "3dFeatureExtMilestone",
                NakshaMISDataEntrySolrAndVender::getThreeDmilestoneCumulative,
                e -> !isTech1(e.getTechnology())
        );

        putIfIntegerStatusAndDoubleValue(
                filteredList,
                NakshaMISDataEntrySolrAndVender::getThreeDmilestoneQaQcStatus,
                counts,
                "3dFeatureExtQaQcMilestone",
                NakshaMISDataEntrySolrAndVender::getThreeDmilestoneCumulativeQaQc,
                e -> !isTech1(e.getTechnology())
        );

        /*putIfIntegerStatusAndDoubleValue(filteredList, NakshaMISDataEntrySolrAndVender::getThreeDmilestoneStatus,
                counts, "3dFeatureExtMilestone", NakshaMISDataEntrySolrAndVender::getThreeDmilestoneCumulative);

        putIfIntegerStatusAndDoubleValue(filteredList, NakshaMISDataEntrySolrAndVender::getThreeDmilestoneQaQcStatus,
                counts, "3dFeatureExtQaQcMilestone", NakshaMISDataEntrySolrAndVender::getThreeDmilestoneCumulativeQaQc);*/

        return counts;
    }

    private boolean isValidCompletedStatus(Integer status) {
        return status != null && (
                status == CompletionStatus.COMPLETED.getCode() ||
                        status == CompletionStatus.RE_FLY_COMPLETED.getCode() ||
                        status == CompletionStatus.COMPLETED_POST_CORRECTION.getCode() ||
                        status == CompletionStatus.ACCEPTED.getCode() ||
                        status == CompletionStatus.ACCEPTED_AFTER_CORRECTION.getCode()
        );
    }
    private <T> void putIfIntegerStatusAndDoubleValue(
            List<T> list,
            Function<T, Integer> statusGetter,
            Map<String, Integer> counts,
            String key,
            Function<T, Double> valueGetter,
            Predicate<T> extraFilter
    ) {
        long count = list.stream()
                .filter(extraFilter) // 👈 Tech1 or any rule
                .filter(e -> isValidCompletedStatus(statusGetter.apply(e)))
                .count();

        counts.put(key, (int) count);
    }

    // For String-based percentage fields
    private void putIfStatus(
            List<NakshaMISDataEntrySolrAndVender> list,
            Function<NakshaMISDataEntrySolrAndVender, Integer> statusGetter,
            Map<String, Integer> counts,
            String key,
            Function<NakshaMISDataEntrySolrAndVender, String> valueGetter) {

        long count = list.stream()
                .filter(e -> {
                    Integer status = statusGetter.apply(e);
                    return status != null &&
                            (status == CompletionStatus.COMPLETED.getCode() ||
                                    status == CompletionStatus.COMPLETED_POST_CORRECTION.getCode() ||
                                    status == CompletionStatus.ACCEPTED.getCode() ||
                                    status == CompletionStatus.ACCEPTED_AFTER_CORRECTION.getCode());

                })
            /*    .filter(e -> {
                    String value = valueGetter.apply(e);
                    return value != null && !value.trim().isEmpty() && !value.equals("0") && !value.equals("0.0");
                })*/
                .count();

        counts.put(key, (int) count);
    }

    private void putIfIntegerStatusAndDoubleValue(
            List<NakshaMISDataEntrySolrAndVender> list,
            Function<NakshaMISDataEntrySolrAndVender, Integer> statusGetter,
            Map<String, Integer> counts,
            String key,
            Function<NakshaMISDataEntrySolrAndVender, Double> valueGetter) {

        long count = list.stream()
                .filter(e -> {
                    Integer status = statusGetter.apply(e);
                    return status != null && (
                            status == CompletionStatus.COMPLETED.getCode() ||
                                    status == CompletionStatus.RE_FLY_COMPLETED.getCode() ||
                                    status == CompletionStatus.COMPLETED_POST_CORRECTION.getCode() ||
                                    status == CompletionStatus.ACCEPTED.getCode() ||
                                    status == CompletionStatus.ACCEPTED_AFTER_CORRECTION.getCode()
                    );
                })

                .filter(e -> {
                    Double value = valueGetter.apply(e);
                    return value != null && value > 0.0;
                })
                .count();

        counts.put(key, (int) count);
    }

    // For String-based percentage fields
    private void putIfPercentage100(
            List<NakshaMISDataEntrySolrAndVender> list,
            Function<NakshaMISDataEntrySolrAndVender, String> percentageGetter,
            Map<String, Integer> counts,
            String key,
            Function<NakshaMISDataEntrySolrAndVender, String> valueGetter) {

        long count = list.stream()
                .filter(e -> {
                    String percentStr = percentageGetter.apply(e);
                    if (percentStr == null || percentStr.trim().isEmpty()) {
                        return false;
                    }
                    try {
                        double percent = Double.parseDouble(percentStr.trim());
                        return percent == 100.0;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                })
                .filter(e -> {
                    String value = valueGetter.apply(e);
                    return value != null && !value.trim().isEmpty() && !value.equals("0") && !value.equals("0.0");
                })
                .count();

        counts.put(key, (int) count);
    }

    private void putIfPercentage100Double(
            List<NakshaMISDataEntrySolrAndVender> list,
            Function<NakshaMISDataEntrySolrAndVender, Double> percentageGetter,
            Map<String, Integer> counts,
            String key,
            Function<NakshaMISDataEntrySolrAndVender, Double> valueGetter) {

        long count = list.stream()
                .filter(e -> {
                    Double percent = percentageGetter.apply(e);
                    return percent != null && percent == 100.0;
                })
                .filter(e -> {
                    Double value = valueGetter.apply(e);
                    return value != null && value != 0.0;
                })
                .count();

        counts.put(key, (int) count);
    }


    // For String fields
    private int countNonZero(List<NakshaMISDataEntrySolrAndVender> list,
                             Function<NakshaMISDataEntrySolrAndVender, String> getter) {
        return (int) list.stream()
                .map(getter)
                .filter(value -> value != null
                        && !value.trim().isEmpty()
                        && !value.trim().equals("0")
                        && !value.trim().equals("0.0")
                        && !value.trim().equals("0.00"))
                .count();
    }


    // For Double fields
    private int countNonZeroDouble(List<NakshaMISDataEntrySolrAndVender> list,
                                   Function<NakshaMISDataEntrySolrAndVender, Double> getter) {
        return (int) list.stream()
                .map(getter)
                .filter(value -> value != null && value != 0.0)
                .count();
    }


    private int toInt(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return 0;
    }

   /* public List<Object[]> getGdWiseFlayingCompletionSummary() {
        return venderRepository.getGdWiseFlayingCompletionSummary();
    }*/

    public List<Object[]> getGdWiseFlayingCompletionSummary() {
        return vendor.stream()
                .filter(e -> e.getGdName() != null && e.getUlbName() != null)
                .collect(Collectors.groupingBy(NakshaMISDataEntrySolrAndVender::getGdName))
                .entrySet()
                .stream()
                .map(entry -> {
                    String gdName = entry.getKey();
                    List<NakshaMISDataEntrySolrAndVender> entries = entry.getValue();

                    Set<String> distinctUlbNames = entries.stream()
                            .map(NakshaMISDataEntrySolrAndVender::getUlbName)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    long tech1Count = entries.stream()
                            .filter(e -> e.getTechStatus() != null &&
                                    (e.getTechStatus().intValue() == CompletionStatus.COMPLETED.getCode() ||
                                            e.getTechStatus().intValue() == CompletionStatus.RE_FLY_COMPLETED.getCode()))
                            .count();

                    long tech2Count = entries.stream()
                            .filter(e -> e.getTech2ObliqueStatus() != null &&
                                    (e.getTech2ObliqueStatus().intValue() == CompletionStatus.COMPLETED.getCode() ||
                                            e.getTech2ObliqueStatus().intValue() == CompletionStatus.RE_FLY_COMPLETED.getCode()))
                            .count();

                    long tech3Count = entries.stream()
                            .filter(e -> e.getTech3ObliqueStatus() != null &&
                                    (e.getTech3ObliqueStatus().intValue() == CompletionStatus.COMPLETED.getCode() ||
                                            e.getTech3ObliqueStatus().intValue() == CompletionStatus.RE_FLY_COMPLETED.getCode()))
                            .count();

                    long tech3LidarCount = entries.stream()
                            .filter(e -> e.getTech3LidarSensorStatus() != null &&
                                    (e.getTech3LidarSensorStatus().intValue() == CompletionStatus.COMPLETED.getCode() ||
                                            e.getTech3LidarSensorStatus().intValue() == CompletionStatus.RE_FLY_COMPLETED.getCode()))
                            .count();


                    long maxTechCount = Math.max(tech1Count, Math.max(tech2Count, tech3Count));

                    return new Object[]{gdName, distinctUlbNames.size(), tech1Count, tech2Count, tech3Count, tech3LidarCount, maxTechCount};

                })
                .sorted(Comparator.comparing(o -> o[0].toString())) // sort by gdName
                .collect(Collectors.toList());
    }


    public List<Map<String, Object>> getProcessingCompletedSummaryRaw() {
        return Collections.unmodifiableList(
                vendor.stream()
                        .collect(Collectors.groupingBy(NakshaMISDataEntrySolrAndVender::getContractor))
                        .entrySet().stream()
                        .sorted(Map.Entry.comparingByKey()) // Sort by contractor name (ASC)
                        .map(entry -> {
                            String contractor = entry.getKey();
                            List<NakshaMISDataEntrySolrAndVender> list = entry.getValue();

                            // Count entries where all relevant status fields == 3
                            /* long all3StatusCount = list.stream()*/
                                   /* .filter(entryItem ->
                                            Integer.valueOf(3).equals(entryItem.getOriCumulativestatus()) &&
                                                    Integer.valueOf(3).equals(entryItem.getOriCumulativeQaQcStatus()) &&
                                                    Integer.valueOf(3).equals(entryItem.getDsmSubmissionStatus()) &&
                                                    Integer.valueOf(3).equals(entryItem.getDsmSubmissionQaQcStatus()) &&
                                                    Integer.valueOf(3).equals(entryItem.getDtmSubmissionStatus()) &&
                                                    Integer.valueOf(3).equals(entryItem.getDtmSubmissionQaQcStatus()) &&
                                                    Integer.valueOf(3).equals(entryItem.getThreedMeshModelStatus()) &&
                                                    Integer.valueOf(3).equals(entryItem.getThreedMeshModelQaQcStatus()) &&
                                                    Integer.valueOf(3).equals(entryItem.getTwoDfeatureextractionStatus()) &&
                                                    Integer.valueOf(3).equals(entryItem.getTwoDfeatureextractionQaQcStatus()) &&
                                                    Integer.valueOf(3).equals(entryItem.getThreeDFeatextrStatus()) &&
                                                    Integer.valueOf(3).equals(entryItem.getThreeDFeatextrQaQcStatus())
                                    )
                                    .count();*/

                            // Calculate tech1Count for each contractor
                            long tech1Count = list.stream()
                                    .filter(e -> {
                                        Integer status = e.getTechStatus();
                                        return status != null &&
                                                (status.intValue() == CompletionStatus.COMPLETED.getCode() ||
                                                        status.intValue() == CompletionStatus.RE_FLY_COMPLETED.getCode());
                                    })
                                    .count();

                            long tech2Count = list.stream()
                                    .filter(e -> {
                                        Integer status = e.getTech2ObliqueStatus();
                                        return status != null &&
                                                (status.intValue() == CompletionStatus.COMPLETED.getCode() ||
                                                        status.intValue() == CompletionStatus.RE_FLY_COMPLETED.getCode());
                                    })
                                    .count();

                            long tech3Count = list.stream()
                                    .filter(e -> {
                                        Integer status = e.getTech3ObliqueStatus();
                                        return status != null &&
                                                (status.intValue() == CompletionStatus.COMPLETED.getCode() ||
                                                        status.intValue() == CompletionStatus.RE_FLY_COMPLETED.getCode());
                                    })
                                    .count();


                            // Calculate tech3LidarCount for each contractor
                           /* long tech3LidarCount = list.stream()
                                    .filter(e -> e.getTech3LidarSensorStatus() != null &&
                                            (e.getTech3LidarSensorStatus().intValue() == CompletionStatus.COMPLETED.getCode() ||
                                                    e.getTech3LidarSensorStatus().intValue() == CompletionStatus.COMPLETED_POST_CORRECTION.getCode()))
                                    .count();
*/
                            // Total all tech counts
                            long totalTechCount = tech1Count + tech2Count + tech3Count;

                            Map<String, Object> row = new LinkedHashMap<>();
                            row.put("contractor", contractor);
                            row.put("total", list.size());
                            row.put("ori", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getOriCumulativestatus));
                            row.put("ori_qaqc", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getOriCumulativeQaQcStatus));
                            row.put("dsm", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getDsmSubmissionStatus));
                            row.put("dsm_qaqc", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getDsmSubmissionQaQcStatus));
                            row.put("dtm", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getDtmSubmissionStatus));
                            row.put("dtm_qaqc", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getDtmSubmissionQaQcStatus));
                            row.put("mesh", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getThreedMeshModelStatus));
                            row.put("mesh_qaqc", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getThreedMeshModelQaQcStatus));
                            row.put("fe_ori", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getTwoDfeatureextractionStatus));
                            row.put("fe_ori_qaqc", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getTwoDfeatureextractionQaQcStatus));

                            row.put("twoDFeatextrStereomode", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getTwoDFeatextrStereomodeStatus));
                            row.put("twoDFeatextrStereomodeQaQc", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getTwoDFeatextrStereomodeQaQcStatus));

                            row.put("threeD_fe", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getThreeDFeatextrStatus));
                            row.put("threeD_fe_qaqc", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getThreeDFeatextrQaQcStatus));

                            row.put("threeDmilestoneStatus", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getThreeDmilestoneStatus));
                            row.put("threeDmilestoneQaQcStatus", countStatusEqualsCompleted(list, NakshaMISDataEntrySolrAndVender::getThreeDmilestoneQaQcStatus));

                            /*row.put("all_status_3", all3StatusCount);*/

                            // Add the tech1Count, tech2Count, tech3Count, tech3LidarCount for each contractor
                            row.put("tech1_count", tech1Count);
                            row.put("tech2_count", tech2Count);
                            row.put("tech3_count", tech3Count);
                            /* row.put("tech3_lidar_count", tech3LidarCount);*/

                            // Add the total count of all tech statuses
                            row.put("total_tech_count", totalTechCount);

                            return row;
                        })
                        .collect(Collectors.toList())
        );
    }


    private long countStatusEqualsCompleted(List<NakshaMISDataEntrySolrAndVender> list,
                                            Function<NakshaMISDataEntrySolrAndVender, Integer> getter) {
        return list.stream()
                .map(getter)
                .filter(status -> status != null && (status == CompletionStatus.COMPLETED.getCode() || status == CompletionStatus.RE_FLY_COMPLETED.getCode() || status == CompletionStatus.ACCEPTED_AFTER_CORRECTION.getCode() ||
                        status == CompletionStatus.ACCEPTED.getCode() || status == CompletionStatus.COMPLETED_POST_CORRECTION.getCode()))
                .count();
    }


    private boolean is100(String value) {
        try {
            return value != null && Double.parseDouble(value.trim()) == 100.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private boolean is100(Double value) {
        return value != null && Double.compare(value, 100.0) == 0;
    }

    private boolean isValid(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    private long countPercentage(List<NakshaMISDataEntrySolrAndVender> list, Function<NakshaMISDataEntrySolrAndVender, String> getter) {
        return list.stream()
                .map(getter)
                .filter(p -> "100".equals(p))
                .count();
    }

    private long countPercentageDouble(List<NakshaMISDataEntrySolrAndVender> list,
                                       Function<NakshaMISDataEntrySolrAndVender, Double> getter) {
        return list.stream()
                .map(getter)
                .filter(p -> p != null && p == 100.0)
                .count();
    }


    public List<Object[]> getNakshaSummaryFromCache() {
        long distinctContractor = vendor.stream()
                .map(NakshaMISDataEntrySolrAndVender::getContractor)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        double totalSanctionedArea = vendor.stream()
                .mapToDouble(e -> e.getSanctionedArea() != null ? e.getSanctionedArea() : 0.0)
                .sum();

        double totalBufferArea = vendor.stream()
                .mapToDouble(e -> e.getBufferAreaDataAcquisition() != null ? e.getBufferAreaDataAcquisition() : 0.0)
                .sum();

        long distinctGdName = vendor.stream()
                .map(NakshaMISDataEntrySolrAndVender::getGdName)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        long distinctUlbName = vendor.stream()
                .map(NakshaMISDataEntrySolrAndVender::getUlbName)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        long tech1Count = vendor.stream()
                .filter(e -> e.getTech1Percentage() != null && e.getTech1Percentage() == 100.0)
                .count();

        long tech2Count = vendor.stream()
                .filter(e -> e.getTech2Percentage() != null && e.getTech2Percentage() == 100.0)
                .count();

        long tech3Count = vendor.stream()
                .filter(e -> e.getTech3Percentage() != null && e.getTech3Percentage() == 100.0)
                .count();

        List<Object[]> result = new ArrayList<>();
        result.add(new Object[]{
                distinctContractor,
                totalSanctionedArea,
                totalBufferArea,
                distinctGdName,
                distinctUlbName,
                tech1Count,
                tech2Count,
                tech3Count
        });

        return result;
    }


    public Map<String, Object> getSummaryData() {
        Map<String, Object> result = new HashMap<>();

        // Use cached data
        List<NakshaMISDataEntrySolrAndVender> cachedData = vendor;

        // Fetch ULB summary
        Object[] ulbSummary = ulbMasterRepository.getULBSummary().get(0);

        long uniqueContractorsCount = cachedData.stream()
                .map(NakshaMISDataEntrySolrAndVender::getContractor)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        long uniqueGDCount = cachedData.stream()
                .map(NakshaMISDataEntrySolrAndVender::getGdName)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        long totalULBsCount = cachedData.stream()
                .map(NakshaMISDataEntrySolrAndVender::getUlbName)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        double totalSanctionedArea = cachedData.stream()
                .mapToDouble(e -> e.getSanctionedArea() != null ? e.getSanctionedArea() : 0.0)
                .sum();

        double totalBufferArea = cachedData.stream()
                .mapToDouble(e -> e.getBufferAreaDataAcquisition() != null ? e.getBufferAreaDataAcquisition() : 0.0)
                .sum();

        long tech1Percentage = cachedData.stream()
                .filter(e -> {
                    Integer status = e.getTechStatus();
                    return status != null && (
                            status == CompletionStatus.COMPLETED.getCode() ||
                                    status == CompletionStatus.RE_FLY_COMPLETED.getCode()
                    );
                })
                .count();
        long totalTech1 = cachedData.stream()
                .filter(e -> "Tech 1".equals(e.getTechnology()))
                .count();
        long totalTech2 = cachedData.stream()
                .filter(e -> "Tech 2".equals(e.getTechnology()))
                .count();
        long totalTech3 = cachedData.stream()
                .filter(e -> "Tech 3".equals(e.getTechnology()))
                .count();


        long tech2Percentage = cachedData.stream()
                .filter(e -> {
                    Integer status = e.getTech2ObliqueStatus();
                    return status != null && (
                            status == CompletionStatus.COMPLETED.getCode() ||
                                    status == CompletionStatus.RE_FLY_COMPLETED.getCode()
                    );
                })
                .count();

        long tech3Percentage = cachedData.stream()
                .filter(e -> {
                    Integer status = e.getTech3ObliqueStatus();
                    return status != null && (
                            status == CompletionStatus.COMPLETED.getCode() ||
                                    status == CompletionStatus.RE_FLY_COMPLETED.getCode()
                    );
                })
                .count();


        long tech3LidarSensorPercentage = cachedData.stream()
                .filter(e -> {
                    Integer status = e.getTech3LidarSensorStatus();
                    return status != null &&
                            (status == CompletionStatus.COMPLETED.getCode() ||
                                    status == CompletionStatus.RE_FLY_COMPLETED.getCode());
                })
                .count();


        result.put("uniqueContractors", uniqueContractorsCount + " / " + Agency.values().length);
        result.put("uniqueGDCount", uniqueGDCount + " / " + ((Number) ulbSummary[1]).longValue());
        result.put("totalULBs", totalULBsCount + " / " + ((Number) ulbSummary[0]).longValue());

        result.put("tech1Percentage", tech1Percentage + " / " +totalTech1);
        result.put("tech2Percentage", tech2Percentage+ " / " +totalTech2);
        result.put("tech3Percentage", tech3Percentage+ " / " +totalTech3);
        result.put("tech3LidarSensorPercentage", tech3LidarSensorPercentage);
        result.put("totalSanctionedArea", Math.round(totalSanctionedArea));
        result.put("totalBufferArea", Math.round(totalBufferArea));
        long totalUlb=totalTech1+totalTech2+totalTech3;

        result.put("flyingCompleted", tech1Percentage+tech2Percentage+tech3Percentage+ " / " +totalUlb);



        int countDataUploadedByGD = 0;
        for (NakshaMISDataEntrySolrAndVender entry : cachedData) {
            if ("Yes".equalsIgnoreCase(entry.getNakshaUploadedWebportal())) {
                countDataUploadedByGD++;
            }
        }
        result.put("countDataUploadedByGD", countDataUploadedByGD);

        return result;
    }

    /*========= GD-wise Summary of Data Processing, QA/QC Details, and Feature Extraction ====================================*/
    private List<List<String>> buildOriTable(List<NakshaMISDataEntrySolrAndVender> sourceList,
                                             Function<NakshaMISDataEntrySolrAndVender, Object> valueExtractor,
                                             Function<NakshaMISDataEntrySolrAndVender, Object> percentageExtractor,
                                             Function<NakshaMISDataEntrySolrAndVender, Object> status) {
        List<List<String>> table = new ArrayList<>();

        sourceList.stream()
                .sorted(Comparator.comparing(NakshaMISDataEntrySolrAndVender::getStateName,
                        Comparator.nullsLast(String::compareToIgnoreCase)))
                .forEach(entry -> {
                    String valueStr = toSafeString(valueExtractor.apply(entry));

            /*    if (valueStr == null || valueStr.isEmpty()
                        || valueStr.equals("0") || valueStr.equals("0.0") || valueStr.equals("0.00")) {
                    return;
                }*/

                    // Safe status code extraction
                    Object statusObj = status.apply(entry);
                    Integer statusCode = null;
                    if (statusObj instanceof Integer) {
                        statusCode = (Integer) statusObj;
                    } else if (statusObj instanceof String) {
                        try {
                            statusCode = Integer.parseInt((String) statusObj);
                        } catch (NumberFormatException e) {
                            statusCode = CompletionStatus.NOT_STARTED.getCode();
                        }
                    } else {
                        statusCode = CompletionStatus.NOT_STARTED.getCode();
                    }

                    // Get enum description
                    String statusStr;
                    try {
                        statusStr = CompletionStatus.fromCode(statusCode).getDescription();
                    } catch (IllegalArgumentException e) {
                        statusStr = "Unknown";
                    }

                    String percentageStr = toSafeString(percentageExtractor.apply(entry));

                    List<String> row = Arrays.asList(
                            safe(entry.getStateName()),
                            safe(entry.getUlbName()),
                            safe(entry.getTechnology()),
                            safe(entry.getGdName()),
                            safe(entry.getContractor()),
                            safe(valueStr),
                            safe(statusStr),
                            safe(percentageStr + "%")
                    );
                    table.add(row);
                });

        return table;
    }

    private List<List<String>> buildulbTable(List<NakshaMISDataEntrySolrAndVender> sourceList,
                                             Function<NakshaMISDataEntrySolrAndVender, Object> valueExtractor,
                                             Function<NakshaMISDataEntrySolrAndVender, Object> percentageExtractor,
                                             Function<NakshaMISDataEntrySolrAndVender, Object> status) {
        List<List<String>> table = new ArrayList<>();

        sourceList.stream()
                .sorted(Comparator.comparing(NakshaMISDataEntrySolrAndVender::getStateName,
                        Comparator.nullsLast(String::compareToIgnoreCase)))
                .forEach(entry -> {
                    String valueStr = toSafeString(valueExtractor.apply(entry));
                    List<String> row = Arrays.asList(
                            safe(entry.getStateName()),
                            safe(entry.getUlbName()),
                            safe(entry.getGdName()),
                            safe(entry.getContractor()),
                            safe(entry.getTechnology()),
                            safe(entry.getSoipackage()),
                            safe(String.format("%.2f", entry.getBufferAreaDataAcquisition()))

                    );
                    table.add(row);
                });

        return table;
    }


    private Integer safeToInteger(Object obj) {
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String toSafeString(Object obj) {
        if (obj == null) return "0";

        if (obj instanceof String) {
            String str = ((String) obj).trim();
            if (str.isEmpty()) return "0"; // empty string → "0"
            return str;
        }

        if (obj instanceof Double || obj instanceof Float) {
            double d = ((Number) obj).doubleValue();
            return String.format("%.2f", d); // formats to 2 decimal places
        }

        return obj.toString().trim();
    }

    private List<NakshaMISDataEntrySolrAndVender> filterByGdName(String gdName) {
        if ("all".equalsIgnoreCase(gdName)) {
            return vendor;
        }
        return vendor.stream()
                .filter(e -> gdName.equalsIgnoreCase(e.getGdName()))
                .collect(Collectors.toList());
    }

    public List<List<String>> getUlbDetails(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList;
        if (gdStateName != null && !"all".equalsIgnoreCase(gdStateName)) {
            filteredList = vendor.stream()
                    .filter(e -> gdStateName.equalsIgnoreCase(e.getStateName()))
                    .collect(Collectors.toList());
        } else {
            String gd = gdName == null ? "all" : gdName;
            filteredList = "all".equalsIgnoreCase(gd)
                    ? vendor
                    : vendor.stream()
                    .filter(e -> gd.equalsIgnoreCase(e.getGdName()))
                    .collect(Collectors.toList());
        }
        return buildulbTable(
                filteredList,
                NakshaMISDataEntrySolrAndVender::getTechnology,
                NakshaMISDataEntrySolrAndVender::getSoipackage,
                NakshaMISDataEntrySolrAndVender::getBufferAreaDataAcquisition

        );
    }

    public List<List<String>> getTech1Nadir(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList;
        if (gdStateName != null && !"all".equalsIgnoreCase(gdStateName)) {
            filteredList = vendor.stream()
                    .filter(e -> gdStateName.equalsIgnoreCase(e.getStateName()))
                    .collect(Collectors.toList());
        } else {
            String gd = gdName == null ? "all" : gdName;
            filteredList = "all".equalsIgnoreCase(gd)
                    ? vendor
                    : vendor.stream()
                    .filter(e -> gd.equalsIgnoreCase(e.getGdName()))
                    .collect(Collectors.toList());
        }

        // ✅ Filter for Tech1
        filteredList = filteredList.stream()
                .filter(e -> "Tech 1".equalsIgnoreCase(e.getTechnology()))
                .collect(Collectors.toList());

        return buildOriTable(
                filteredList,
                NakshaMISDataEntrySolrAndVender::getTech1Cumulative,
                NakshaMISDataEntrySolrAndVender::getTech1Percentage,
                NakshaMISDataEntrySolrAndVender::getTechStatus
        );
    }

    public List<List<String>> getTech2Oblique(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList;
        if (gdStateName != null && !"all".equalsIgnoreCase(gdStateName)) {
            filteredList = vendor.stream()
                    .filter(e -> gdStateName.equalsIgnoreCase(e.getStateName()))
                    .collect(Collectors.toList());
        } else {
            String gd = gdName == null ? "all" : gdName;
            filteredList = "all".equalsIgnoreCase(gd)
                    ? vendor
                    : vendor.stream()
                    .filter(e -> gd.equalsIgnoreCase(e.getGdName()))
                    .collect(Collectors.toList());
        }

        // ✅ Filter for Tech2
        filteredList = filteredList.stream()
                .filter(e -> "Tech 2".equalsIgnoreCase(e.getTechnology()))
                .collect(Collectors.toList());

        return buildOriTable(
                filteredList,
                NakshaMISDataEntrySolrAndVender::getTech2Cumulative,
                NakshaMISDataEntrySolrAndVender::getTech2Percentage,
                NakshaMISDataEntrySolrAndVender::getTech2ObliqueStatus
        );
    }

    public List<List<String>> getTech3Oblique(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList;

        if (gdStateName != null && !"all".equalsIgnoreCase(gdStateName)) {
            filteredList = vendor.stream()
                    .filter(e -> gdStateName.equalsIgnoreCase(e.getStateName()))
                    .collect(Collectors.toList());
        } else {
            String gd = gdName == null ? "all" : gdName;
            filteredList = "all".equalsIgnoreCase(gd)
                    ? vendor
                    : vendor.stream()
                    .filter(e -> gd.equalsIgnoreCase(e.getGdName()))
                    .collect(Collectors.toList());
        }

        // ✅ Filter for Tech3
        filteredList = filteredList.stream()
                .filter(e -> "Tech 3".equalsIgnoreCase(e.getTechnology()))
                .collect(Collectors.toList());

        return buildOriTable(
                filteredList,
                e -> {
                    Double cumulative = e.getTech3Cumulative();
                    Double lidar = e.getTech3LidarSensor();
                    if (cumulative != null && lidar != null) {
                        return cumulative + lidar;
                    } else if (cumulative != null) {
                        return cumulative;
                    } else if (lidar != null) {
                        return lidar;
                    } else {
                        return 0;
                    }
                },
                NakshaMISDataEntrySolrAndVender::getTech3Percentage,
                NakshaMISDataEntrySolrAndVender::getTech3ObliqueStatus
        );
    }


    public List<List<String>> getTech3Lidar(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList;
        if (gdStateName != null && !"all".equalsIgnoreCase(gdStateName)) {
            filteredList = vendor.stream()
                    .filter(e -> gdStateName.equalsIgnoreCase(e.getStateName()))
                    .collect(Collectors.toList());
        } else {
            String gd = gdName == null ? "all" : gdName;
            filteredList = "all".equalsIgnoreCase(gd)
                    ? vendor
                    : vendor.stream()
                    .filter(e -> gd.equalsIgnoreCase(e.getGdName()))
                    .collect(Collectors.toList());
        }
        return buildOriTable(
                filteredList,
                NakshaMISDataEntrySolrAndVender::getTech3LidarSensor,
                NakshaMISDataEntrySolrAndVender::getTech3LidarSensorPercentage,
                NakshaMISDataEntrySolrAndVender::getTech3ObliqueStatus

        );
    }

    public List<List<String>> getOriDataProcessingCumulative(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                NakshaMISDataEntrySolrAndVender::getOriCumulativestatus
        );

        return buildOriTable(
                filteredList,
                NakshaMISDataEntrySolrAndVender::getSeparateOriDataProcessingCumulative,
                NakshaMISDataEntrySolrAndVender::getSeparateOriDataProcessingPercentage,
                NakshaMISDataEntrySolrAndVender::getOriCumulativestatus
        );
    }


    public List<List<String>> getOriQaQcData(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                NakshaMISDataEntrySolrAndVender::getOriCumulativeQaQcStatus
        );

        return buildOriTable(
                filteredList,
                NakshaMISDataEntrySolrAndVender::getSeparateOriQaqcDataProcessingCumulative,
                NakshaMISDataEntrySolrAndVender::getSeparateOriQaqcDataProcessingPercentage,
                NakshaMISDataEntrySolrAndVender::getOriCumulativeQaQcStatus
        );
    }

    public List<List<String>> getDsmData(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                NakshaMISDataEntrySolrAndVender::getDsmSubmissionStatus
        );
        return buildOriTable(
                filteredList,
                NakshaMISDataEntrySolrAndVender::getDsmDataProcessingCumulative,
                NakshaMISDataEntrySolrAndVender::getDsmDataProcessingPercentage,
                NakshaMISDataEntrySolrAndVender::getDsmSubmissionStatus
        );
    }

    public List<List<String>> getDsmQaQcData(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                NakshaMISDataEntrySolrAndVender::getDsmSubmissionQaQcStatus
        );
        return buildOriTable(
                filteredList,
                NakshaMISDataEntrySolrAndVender::getDsmDataProcessingQaqcCumulative,
                NakshaMISDataEntrySolrAndVender::getDsmDataProcessingQaqcPercentage,
                NakshaMISDataEntrySolrAndVender::getDsmSubmissionQaQcStatus
        );
    }


    public List<List<String>> getDtmData(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                NakshaMISDataEntrySolrAndVender::getDtmSubmissionStatus
        );
        return buildOriTable(
                filteredList,
                NakshaMISDataEntrySolrAndVender::getDtmDataProcessingCumulative,
                NakshaMISDataEntrySolrAndVender::getDtmDataProcessingPercentage,
                NakshaMISDataEntrySolrAndVender::getDtmSubmissionStatus
        );
    }

    public List<List<String>> getDtmQaQcData(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                NakshaMISDataEntrySolrAndVender::getDtmSubmissionQaQcStatus
        );
        return buildOriTable(
                filteredList,
                NakshaMISDataEntrySolrAndVender::getDtmDataProcessingQaqcCumulative,
                NakshaMISDataEntrySolrAndVender::getDtmDataProcessingQaqcPercentage,
                NakshaMISDataEntrySolrAndVender::getDtmSubmissionQaQcStatus
        );
    }


    public List<List<String>> getMeshData(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                e -> {
                    Object status = e.getThreedMeshModelStatus();
                    if (isTech1(e.getTechnology()) || status == null) {
                        return CompletionStatus.NOT_AVAILABLE.getCode();
                    }
                    return (Integer) status; // make sure to cast if it's stored as Integer
                }
        );

        return buildOriTable(
                filteredList.stream()
                        .filter(e -> !isTech1(e.getTechnology())) // ❌ remove Tech1
                        .collect(Collectors.toList()),
                NakshaMISDataEntrySolrAndVender::getMeshDataProcessingCumulative,
                NakshaMISDataEntrySolrAndVender::getMeshDataProcessingPercentage,
                e -> {
                    Object status = e.getThreedMeshModelStatus();
                    return status == null
                            ? CompletionStatus.NOT_AVAILABLE.getCode()
                            : (Integer) status;
                }
        );
    }


    public List<List<String>> getMeshQaQcData(String gdName, String gdStateName) {

        // Pass the custom status getter with Tech 1 / null → NOT_AVAILABLE logic
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                e -> {
                    Object status = e.getThreedMeshModelQaQcStatus();
                    if (isTech1(e.getTechnology()) || status == null) {
                        return CompletionStatus.NOT_AVAILABLE.getCode();
                    }
                    return (Integer) status; // cast to Integer if needed
                }
        );

        return buildOriTable(
                filteredList.stream()
                        .filter(e -> !isTech1(e.getTechnology())) // ✅ remove Tech1
                        .collect(Collectors.toList()),
                NakshaMISDataEntrySolrAndVender::getMeshDataProcessingQaqcCumulative,
                NakshaMISDataEntrySolrAndVender::getMeshDataProcessingQaqcCumulative,
                e -> {
                    Object status = e.getThreedMeshModelQaQcStatus();
                    return status == null
                            ? CompletionStatus.NOT_AVAILABLE.getCode()
                            : (Integer) status;
                }
        );

    }


    private boolean isTech1(String tech) {
        return tech != null && (tech.equalsIgnoreCase("Tech 1"));
    }


    public List<List<String>> getFeOricData(String gdName, String gdStateName) {

        // Apply Tech 2 or Tech 3 → NOT_AVAILABLE override
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                e -> {
                    Object status = e.getTwoDfeatureextractionStatus();
                    if (isTech2Or3(e.getTechnology()) || status == null) {
                        return CompletionStatus.NOT_AVAILABLE.getCode();
                    }
                    return (Integer) status; // ensure cast if needed
                }
        );

        return buildOriTable(
                filteredList.stream()
                        .filter(e -> !isTech2Or3(e.getTechnology())) // ✅ exclude Tech2/Tech3
                        .collect(Collectors.toList()),
                NakshaMISDataEntrySolrAndVender::getFeatureExtractionOriCumulativeCompleted,
                NakshaMISDataEntrySolrAndVender::getFeatureExtractionOriPercentageCompleted,
                e -> {
                    Object status = e.getTwoDfeatureextractionStatus();
                    return status == null
                            ? CompletionStatus.NOT_AVAILABLE.getCode()
                            : (Integer) status;
                }
        );
    }


    public List<List<String>> getFeOriQaQcData(String gdName, String gdStateName) {

        // Filter vendor list with Tech 2/3 override
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                e -> {
                    Object status = e.getTwoDfeatureextractionQaQcStatus();
                    if (isTech2Or3(e.getTechnology()) || status == null) {
                        return CompletionStatus.NOT_AVAILABLE.getCode();
                    }
                    return (Integer) status; // ensure cast if stored as Integer
                }
        );

        return buildOriTable(
                filteredList.stream()
                        .filter(e -> !isTech2Or3(e.getTechnology())) // ❌ exclude Tech2/Tech3
                        .collect(Collectors.toList()),
                NakshaMISDataEntrySolrAndVender::getFeatureExtractionOriQaqcCumulativeCompleted,
                NakshaMISDataEntrySolrAndVender::getFeatureExtractionOriQaqcPercentageCompleted,
                e -> {
                    Object status = e.getTwoDfeatureextractionQaQcStatus();
                    return status == null
                            ? CompletionStatus.NOT_AVAILABLE.getCode()
                            : (Integer) status;
                }
        );
    }


    public List<List<String>> get2dFeatureStereo(String gdName, String gdStateName) {

        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                e -> applyTechOverride(e.getTwoDFeatextrStereomodeStatus(), e.getTechnology(), false) // false = Tech 2/3 override
        );

        return buildOriTable(
                filteredList.stream()
                        .filter(e -> isTech1(e.getTechnology())) // ✅ ONLY Tech1
                        .collect(Collectors.toList()),
                NakshaMISDataEntrySolrAndVender::getTwoDFeatextrStereomodeCumulative,
                NakshaMISDataEntrySolrAndVender::getTwoDFeatextrStereomodePercentage,
                e -> applyTechOverride(
                        e.getTwoDFeatextrStereomodeStatus(),
                        e.getTechnology(),
                        false
                )
        );
    }

    private Integer applyTechOverride(Object status, String technology, boolean tech1NotAvailable) {
        if ((tech1NotAvailable && isTech1(technology)) ||
                (!tech1NotAvailable && isTech2Or3(technology)) ||
                status == null) {
            return CompletionStatus.NOT_AVAILABLE.getCode();
        }
        return (Integer) status;
    }


    public List<List<String>> get2dFeatureExtStereo(String gdName, String gdStateName) {

        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                e -> applyTechOverride(e.getTwoDFeatextrStereomodeQaQcStatus(), e.getTechnology(), false) // false = Tech 2/3 override
        );

        return buildOriTable(
                filteredList.stream()
                        .filter(e -> isTech1(e.getTechnology())) // ✅ only exclude Tech1 here
                        .collect(Collectors.toList()),
                NakshaMISDataEntrySolrAndVender::getTwoDFeatextrStereomodeCumulativeQaQc,
                NakshaMISDataEntrySolrAndVender::getTwoDFeatextrStereomodePercentageQaQc,
                e -> applyTechOverride(
                        e.getTwoDFeatextrStereomodeQaQcStatus(),
                        e.getTechnology(),
                        false
                )
        );
    }


    private boolean isTech2Or3(String tech) {
        return tech != null && (tech.equalsIgnoreCase("Tech 2") || tech.equalsIgnoreCase("Tech 3"));
    }


    public List<List<String>> get3dFeature(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                e -> {
                    Object status = e.getThreeDFeatextrStatus();
                    if (isTech1(e.getTechnology()) || status == null) {
                        return CompletionStatus.NOT_AVAILABLE.getCode();
                    }
                    return (Integer) status; // make sure to cast if it's stored as Integer
                }

        );


        return buildOriTable(
                filteredList.stream()
                        .filter(e -> !isTech1(e.getTechnology())) // ✅ exclude Tech1 completely
                        .collect(Collectors.toList()),
                NakshaMISDataEntrySolrAndVender::getThreeDFeatextrCumulative,
                NakshaMISDataEntrySolrAndVender::getThreeDFeatextrPercentage,
                e -> {
                    Object status = e.getThreeDFeatextrStatus();
                    return status == null
                            ? CompletionStatus.NOT_AVAILABLE.getCode()
                            : (Integer) status;
                }
        );
    }

    public List<List<String>> get3dFeatureExt(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                e -> {
                    Object status = e.getThreeDFeatextrQaQcStatus();
                    if (isTech1(e.getTechnology()) || status == null) {
                        return CompletionStatus.NOT_AVAILABLE.getCode();
                    }
                    return (Integer) status; // make sure to cast if it's stored as Integer
                }


        );
        return buildOriTable(
                filteredList.stream()
                        .filter(e -> !isTech1(e.getTechnology())) // ✅ remove Tech1 بالكامل
                        .collect(Collectors.toList()),
                NakshaMISDataEntrySolrAndVender::getThreeDFeatextrCumulativeQaQc,
                NakshaMISDataEntrySolrAndVender::getThreeDDFeatextrPercentageQaQc,
                e -> {
                    Object status = e.getThreeDFeatextrQaQcStatus();
                    return status == null
                            ? CompletionStatus.NOT_AVAILABLE.getCode()
                            : (Integer) status; // ✅ cast properly
                }
        );
    }

    public List<List<String>> get3dFeatureMilestone(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                e -> {
                    Object status = e.getThreeDmilestoneStatus();
                    return status == null
                            ? CompletionStatus.NOT_AVAILABLE.getCode()
                            : (Integer) status;
                }
        );

// ✅ Remove Tech1 entries completely
        List<NakshaMISDataEntrySolrAndVender> finalList = filteredList.stream()
                .filter(e -> !isTech1(e.getTechnology()))
                .collect(Collectors.toList());
        return buildOriTable(
                filteredList.stream()
                        .filter(e -> !isTech1(e.getTechnology())) // exclude Tech1
                        .collect(Collectors.toList()),
                NakshaMISDataEntrySolrAndVender::getThreeDmilestoneCumulative,
                NakshaMISDataEntrySolrAndVender::getThreeDmilestonePercentage,
                e -> {
                    Object status = e.getThreeDmilestoneStatus();
                    return status == null
                            ? CompletionStatus.NOT_AVAILABLE.getCode()
                            : (Integer) status; // ✅ cast properly
                }
        );
    }

    public List<List<String>> get3dFeatureQaQcMilestone(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                e -> {
                    Object status = e.getThreeDmilestoneQaQcStatus();
                    if (isTech1(e.getTechnology()) || status == null) {
                        return CompletionStatus.NOT_AVAILABLE.getCode();
                    }
                    return (Integer) status; // make sure to cast if it's stored as Integer
                }

        );

        return buildOriTable(
                filteredList.stream()
                        .filter(e -> !isTech1(e.getTechnology())) // ❗ remove Tech1
                        .collect(Collectors.toList()),
                NakshaMISDataEntrySolrAndVender::getThreeDmilestoneCumulativeQaQc,
                NakshaMISDataEntrySolrAndVender::getThreeDmilestonePercentageQaQc,
                e -> {
                    Object status = e.getThreeDmilestoneQaQcStatus();
                    return status == null
                            ? CompletionStatus.NOT_AVAILABLE.getCode()
                            : (Integer) status;
                }
        );
    }


    private String safe(String val) {
        return (val == null || val.trim().isEmpty()
                || val.equals("0") || val.equals("0.0") || val.equals("0.00")) ? "0" : val;
    }


    public Map<String, List<String>> getFlyingCompletedT1AndT2() {
        List<NakshaMISDataEntrySolrAndVender> vendorList = getAllEntries();

        List<String> tech1_completed_list = new ArrayList<>();
        List<String> tech1_under_processList = new ArrayList<>();
        List<String> tech1_not_started = new ArrayList<>();


        List<String> tech2_completed_list = new ArrayList<>();
        List<String> tech2_under_processList = new ArrayList<>();
        List<String> tech2_not_started = new ArrayList<>();


        for (NakshaMISDataEntrySolrAndVender entry : vendorList) {
            String ulbName = entry.getUlbName();
            String stateName = entry.getStateName();
            String stateAndUlb = stateName + " - " + ulbName;

            Integer tech1 = entry.getTechStatus();
            Integer tech2 = entry.getTech2ObliqueStatus();

            // Tech1 categorization
            if (tech1 != null && (tech1.equals(CompletionStatus.COMPLETED.getCode())
                    || tech1.equals(CompletionStatus.RE_FLY_COMPLETED.getCode()))) {
                tech1_completed_list.add(stateAndUlb);
            } else if (tech1 != null && (tech1.equals(CompletionStatus.UNDER_PROCESS.getCode())
                    || tech1.equals(CompletionStatus.RE_FLY_UNDER_PROGRESS.getCode())
                    || tech1.equals(CompletionStatus.RETURNED.getCode()))) {
                tech1_under_processList.add(stateAndUlb);
            } else if (tech1 != null && tech1.equals(CompletionStatus.NOT_STARTED.getCode())) {
                tech1_not_started.add(stateAndUlb);
            }

            // Tech2 categorization
            if (tech2 != null && (tech2.equals(CompletionStatus.COMPLETED.getCode())
                    || tech2.equals(CompletionStatus.RE_FLY_COMPLETED.getCode()))) {
                tech2_completed_list.add(stateAndUlb);
            } else if (tech2 != null && (tech2.equals(CompletionStatus.UNDER_PROCESS.getCode())
                    || tech2.equals(CompletionStatus.RE_FLY_UNDER_PROGRESS.getCode())
                    || tech2.equals(CompletionStatus.RETURNED.getCode()))) {
                tech2_under_processList.add(stateAndUlb);
            } else if (tech2 != null && tech2.equals(CompletionStatus.NOT_STARTED.getCode())) {
                tech2_not_started.add(stateAndUlb);
            }
        }


        Collections.sort(tech1_completed_list);
        Collections.sort(tech1_under_processList);
        Collections.sort(tech1_not_started);


        Collections.sort(tech2_completed_list);
        Collections.sort(tech2_under_processList);
        Collections.sort(tech2_not_started);


        List<String> ulbNamesWithFlyingZoneZero = getUlbNamesWithFlyingZoneZero(); // ← reused

        Map<String, List<String>> result = new LinkedHashMap<>();
        result.put("tech1_completed", tech1_completed_list);
        result.put("tech1_under_process", tech1_under_processList);
        result.put("tech1_not_started", tech1_not_started);


        result.put("tech2_completed", tech2_completed_list);
        result.put("tech2_under_process", tech2_under_processList);
        result.put("tech2_not_started", tech2_not_started);


        result.put("notFlyingZone", ulbNamesWithFlyingZoneZero); // ← added

        return result;
    }


    public Map<String, List<String>> getFlyingCompletedT3() {
        List<NakshaMISDataEntrySolrAndVender> vendorList = getAllEntries();

        List<String> tech3_completed_list = new ArrayList<>();
        List<String> tech3_under_processList = new ArrayList<>();
        List<String> tech3_not_started = new ArrayList<>();

        for (NakshaMISDataEntrySolrAndVender entry : vendorList) {
            String ulbName = entry.getUlbName();
            String stateName = entry.getStateName();
            String stateAndUlb = stateName + " - " + ulbName;
            Integer tech3 = entry.getTech3ObliqueStatus();

            if (tech3 != null && (tech3.equals(CompletionStatus.COMPLETED.getCode())
                    || tech3.equals(CompletionStatus.RE_FLY_COMPLETED.getCode()))) {
                tech3_completed_list.add(stateAndUlb);
            } else if (tech3 != null && (tech3.equals(CompletionStatus.UNDER_PROCESS.getCode())
                    || tech3.equals(CompletionStatus.RE_FLY_UNDER_PROGRESS.getCode())
                    || tech3.equals(CompletionStatus.RETURNED.getCode()))) {
                tech3_under_processList.add(stateAndUlb);
            } else if (tech3 != null && tech3.equals(CompletionStatus.NOT_STARTED.getCode())) {
                tech3_not_started.add(stateAndUlb);
            }

        }

        // Sort all lists alphabetically
        Collections.sort(tech3_completed_list);
        Collections.sort(tech3_under_processList);
        Collections.sort(tech3_not_started);

        List<String> ulbNamesWithFlyingZoneZero = getUlbNamesWithFlyingZoneZero();

        Map<String, List<String>> result = new LinkedHashMap<>();
        result.put("tech3_completed", tech3_completed_list);
        result.put("tech3_under_process", tech3_under_processList);
        result.put("tech3_not_started", tech3_not_started);
        result.put("notFlyingZone", ulbNamesWithFlyingZoneZero);

        return result;
    }

    public Map<String, List<String>> getTech3LidarPercentage() {
        List<NakshaMISDataEntrySolrAndVender> vendorList = getAllEntries();

        List<String> tech3_100List = new ArrayList<>();
        List<String> tech3_lessThan100List = new ArrayList<>();
        List<String> tech3_nullOrZeroList = new ArrayList<>();

        for (NakshaMISDataEntrySolrAndVender entry : vendorList) {
            String ulbName = entry.getUlbName();
            Integer tech3 = entry.getTech3LidarSensorStatus();

            if (tech3 != null && (tech3.equals(CompletionStatus.COMPLETED.getCode())
                    || tech3.equals(CompletionStatus.RE_FLY_COMPLETED.getCode()))) {
                tech3_100List.add(ulbName);
            } else if (tech3 != null && (tech3.equals(CompletionStatus.UNDER_PROCESS.getCode())
                    || tech3.equals(CompletionStatus.RE_FLY_UNDER_PROGRESS.getCode())
                    || tech3.equals(CompletionStatus.RETURNED.getCode()))) {
                tech3_lessThan100List.add(ulbName);
            } else if (tech3 != null && tech3.equals(CompletionStatus.NOT_STARTED.getCode())) {
                tech3_nullOrZeroList.add(ulbName);
            }
        }

        // Sort all lists alphabetically
        Collections.sort(tech3_100List);
        Collections.sort(tech3_lessThan100List);
        Collections.sort(tech3_nullOrZeroList);

        List<String> ulbNamesWithFlyingZoneZero = getUlbNamesWithFlyingZoneZero();

        Map<String, List<String>> result = new LinkedHashMap<>();
        result.put("tech3_100", tech3_100List);
        result.put("tech3_lessThan100", tech3_lessThan100List);
        result.put("tech3_nullOrZero", tech3_nullOrZeroList);
        result.put("notFlyingZone", ulbNamesWithFlyingZoneZero);

        return result;
    }

    public List<String> getUlbNamesWithFlyingZoneZero() {
        List<ULBMaster> ulbMaster = ulbMasterRepository.findAll();

        return ulbMaster.stream()
                .filter(v -> v.getFlyingZone() != null && v.getFlyingZone() == 0)
                .filter(v -> v.getUlb_name() != null && !v.getUlb_name().trim().isEmpty())
                .filter(v -> v.getState_name() != null && !v.getState_name().trim().isEmpty())
                .map(v -> v.getState_name() + " - " + v.getUlb_name())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }


    public List<Map<String, Object>> getDataByStateAsMapList(String stateName) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

        try {
            vendor.stream()
                    .filter(e -> stateName.equalsIgnoreCase(e.getStateName()))
                    .forEach(e -> {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("ulbName", e.getUlbName());

                        row.put("package", e.getSoipackage());
                        row.put("technology", e.getTechnology());
                        row.put("contractor", e.getContractor());
                        row.put("commencement", formatDate(e.getDateOfCommecement(), formatter));
                        row.put("bufferArea", e.getBufferAreaDataAcquisition());



                        // 🔽 Always provide both values: Completion and Tentative (independently)
                        row.put("completionDate", formatDate(e.getCompletionDate(), formatter));
                        row.put("tentativeDate", formatDate(e.getTentativeDate(), formatter));

                        row.put("oriTentativeDate", formatDate(e.getOriTentativeDate(), formatter));
                        row.put("oriCompletionDate", formatDate(e.getOriCompletionDate(), formatter));

                        row.put("oriQaqcTentativeDate", formatDate(e.getOriqaQcTentativeDate(), formatter));
                        row.put("oriQaqcCompletionDate", formatDate(e.getOriqaQcCompletionDate(), formatter));

                        row.put("milestone2Completion", formatDate(e.getMilestone2CompletionDate(), formatter));
                        row.put("milestone2Tentative", formatDate(e.getMilestone2TentativeDate(), formatter));

                        row.put("qaQcMilestone2Completion", formatDate(e.getQaQcmilestone2CompletionDate(), formatter));
                        row.put("qaQcMilestone2Tentative", formatDate(e.getQaQcmilestone2TentativeDate(), formatter));

                        row.put("feature2DCompletion", formatDate(e.getFeatureExtraction2DCompletionDate(), formatter));
                        row.put("feature2DTentative", formatDate(e.getFeatureExtraction2DTentativeDate(), formatter));

                        row.put("qaQc2DCompletion", formatDate(e.getQaQc2DCompletionDate(), formatter));
                        row.put("qaQc2DTentative", formatDate(e.getQaQc2DTentativeDate(), formatter));

                        row.put("stereo2DCompletion", formatDate(e.getStereo2DCompletionDate(), formatter));
                        row.put("stereo2DTentative", formatDate(e.getStereo2DTentativeDate(), formatter));

                        row.put("qaQcStereo2DCompletion", formatDate(e.getQaQcStereo2DCompletionDate(), formatter));
                        row.put("qaQcStereo2DTentative", formatDate(e.getQaQcStereo2DTentativeDate(), formatter));

                        row.put("threeDFeatExtCompletion", formatDate(e.getThreeDFeatExtCompletionDate(), formatter));
                        row.put("threeDFeatExtTentative", formatDate(e.getThreeDFeatExtTentativeDate(), formatter));

                        row.put("threeDFeatExtQaQcCompletion", formatDate(e.getThreeDFeatExtQaQcCompletionDate(), formatter));
                        row.put("threeDFeatExtQaQcTentative", formatDate(e.getThreeDFeatExtQaQcTentativeDate(), formatter));

                        row.put("threeDMilestoneCompletion", formatDate(e.getThreeDMilestoneCompletionDate(), formatter));
                        row.put("threeDMilestoneTentative", formatDate(e.getThreeDMilestoneTentativeDate(), formatter));

                        row.put("threeDMilestoneQaQcCompletion", formatDate(e.getThreeDMilestoneQaQcCompletionDate(), formatter));
                        row.put("threeDMilestoneQaQcTentative", formatDate(e.getThreeDMilestoneQaQcTentativeDate(), formatter));

                        resultList.add(row);
                    });
        } catch (Exception e) {
            System.err.println("Error while processing vendor data for state: " + stateName);
            e.printStackTrace();
        }

        return resultList;
    }

    private String formatDate(LocalDate date, DateTimeFormatter formatter) {
        if (date == null) return null;
        try {
            return date.format(formatter);
        } catch (Exception e) {
            return null;
        }
    }

    private List<NakshaMISDataEntrySolrAndVender> filterVendorList(
            List<NakshaMISDataEntrySolrAndVender> vendorList,
            String gdName,
            String gdStateName,
            Function<NakshaMISDataEntrySolrAndVender, Integer> statusGetter) {

        Set<Integer> allowedStatuses = Set.of(
                CompletionStatus.COMPLETED.getCode(),
                CompletionStatus.RE_FLY_COMPLETED.getCode(),
                CompletionStatus.COMPLETED_POST_CORRECTION.getCode(),
                CompletionStatus.ACCEPTED.getCode(),
                CompletionStatus.ACCEPTED_AFTER_CORRECTION.getCode()
        );

        boolean isGdAll = gdName == null || "all".equalsIgnoreCase(gdName);
        boolean isStateAll = gdStateName == null || "all".equalsIgnoreCase(gdStateName);

        if (!isStateAll) {
            return vendorList.stream()
                    .filter(e -> gdStateName.equalsIgnoreCase(e.getStateName()))
                    .collect(Collectors.toList());
        } else if (!isGdAll) {
            return vendorList.stream()
                    .filter(e -> gdName.equalsIgnoreCase(e.getGdName()))
                    .collect(Collectors.toList());
        } else {
            return vendorList.stream()
                    .filter(e -> allowedStatuses.contains(statusGetter.apply(e)))
                    .collect(Collectors.toList());
        }
    }


    public Map<String, Object> getFlyingCompletedT1AndT2(String stateName) {
        List<NakshaMISDataEntrySolrAndVender> vendorList = getAllEntries();

        // Filter by state name if provided and not "all"
        if (stateName != null && !"all".equalsIgnoreCase(stateName.trim())) {
            vendorList = vendorList.stream()
                    .filter(v -> stateName.equalsIgnoreCase(v.getStateName()))
                    .collect(Collectors.toList());
        }

        // Allowed status codes
        Set<Integer> completedStatuses = Set.of(
                CompletionStatus.COMPLETED.getCode(),
                CompletionStatus.RE_FLY_COMPLETED.getCode(),
                CompletionStatus.COMPLETED_POST_CORRECTION.getCode(),
                CompletionStatus.ACCEPTED.getCode(),
                CompletionStatus.ACCEPTED_AFTER_CORRECTION.getCode()
        );

        // Count for each tech
        long totalTech1Completed = vendorList.stream()
                .filter(v -> v.getTechStatus() != null && completedStatuses.contains(v.getTechStatus()))
                .count();

        long totalTech2Completed = vendorList.stream()
                .filter(v -> v.getTech2ObliqueStatus() != null && completedStatuses.contains(v.getTech2ObliqueStatus()))
                .count();

        long totalTech3Completed = vendorList.stream()
                .filter(v -> v.getTech3ObliqueStatus() != null && completedStatuses.contains(v.getTech3ObliqueStatus()))
                .count();


        // Prepare the list of selected fields
        List<Map<String, Object>> detailsList = vendorList.stream()
                .map(v -> {
                    // Convert status codes to descriptions or set N/A if null
                    String techStatus = (v.getTechStatus() == null)
                            ? CompletionStatus.NOT_AVAILABLE.getDescription()
                            : CompletionStatus.fromCode(v.getTechStatus()).getDescription();

                    String tech2ObliqueStatus = (v.getTech2ObliqueStatus() == null)
                            ? CompletionStatus.NOT_AVAILABLE.getDescription()
                            : CompletionStatus.fromCode(v.getTech2ObliqueStatus()).getDescription();

                    String tech3ObliqueStatus = (v.getTech3ObliqueStatus() == null)
                            ? CompletionStatus.NOT_AVAILABLE.getDescription()
                            : CompletionStatus.fromCode(v.getTech3ObliqueStatus()).getDescription();

                    // Handle numeric defaults
                    double tech1Cumulative = (v.getTech1Cumulative() == null) ? 0 : v.getTech1Cumulative();
                    double tech1Percentage = (v.getTech1Percentage() == null) ? 0 : v.getTech1Percentage();

                    double tech2Cumulative = (v.getTech2Cumulative() == null) ? 0 : v.getTech2Cumulative();
                    double tech2Percentage = (v.getTech2Percentage() == null) ? 0 : v.getTech2Percentage();

                    double tech3Cumulative = (v.getTech3Cumulative() == null) ? 0 : v.getTech3Cumulative();
                    double tech3Percentage = (v.getTech3Percentage() == null) ? 0 : v.getTech3Percentage();

                    // Build map
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("ulbName", v.getUlbName());
                    map.put("technology", v.getTechnology());
                    map.put("bufferAreaDataAcquisition", v.getBufferAreaDataAcquisition());

                    map.put("tech1Cumulative", tech1Cumulative);
                    map.put("techStatus", techStatus);
                    map.put("tech1Percentage", tech1Percentage);

                    map.put("tech2Cumulative", tech2Cumulative);
                    map.put("tech2ObliqueStatus", tech2ObliqueStatus);
                    map.put("tech2Percentage", tech2Percentage);

                    map.put("tech3Cumulative", tech3Cumulative);
                    map.put("tech3ObliqueStatus", tech3ObliqueStatus);
                    map.put("tech3Percentage", tech3Percentage);

                    return map;
                })
                .collect(Collectors.toList());


        // Prepare final response
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalTech1Completed", totalTech1Completed);
        result.put("totalTech2Completed", totalTech2Completed);
        result.put("totalTech3Completed", totalTech3Completed);
        result.put("details", detailsList);

        return result;
    }


    public List<List<String>> getOriAndQaQcData(String gdName, String gdStateName) {
        List<NakshaMISDataEntrySolrAndVender> filteredList = filterVendorList(
                vendor,
                gdName,
                gdStateName,
                null // we don't filter by status here, we want all rows
        );

        List<List<String>> table = new ArrayList<>();

        filteredList.stream()
                .sorted(Comparator.comparing(NakshaMISDataEntrySolrAndVender::getStateName,
                        Comparator.nullsLast(String::compareToIgnoreCase)))
                .forEach(entry -> {
                    // Data Processing values
                    String dpCumulative = safe(toSafeString(entry.getSeparateOriDataProcessingCumulative()));
                    String dpPercentage = safe(toSafeString(entry.getSeparateOriDataProcessingPercentage()) + "%");
                    String dpStatus = safe(getStatusDescription(entry.getOriCumulativestatus()));

                    // QA/QC values
                    String qaCumulative = safe(toSafeString(entry.getSeparateOriQaqcDataProcessingCumulative()));
                    String qaPercentage = safe(toSafeString(entry.getSeparateOriQaqcDataProcessingPercentage()) + "%");
                    String qaStatus = safe(getStatusDescription(entry.getOriCumulativeQaQcStatus()));

                    List<String> row = Arrays.asList(
                            safe(entry.getUlbName()),
                            safe(entry.getTechnology()),
                            dpCumulative,
                            dpPercentage,
                            dpStatus,
                            qaCumulative,
                            qaPercentage,
                            qaStatus
                    );

                    table.add(row);
                });

        return table;
    }

    // Helper to convert status code to text
    private String getStatusDescription(Object statusObj) {
        Integer statusCode = null;
        if (statusObj instanceof Integer) {
            statusCode = (Integer) statusObj;
        } else if (statusObj instanceof String) {
            try {
                statusCode = Integer.parseInt((String) statusObj);
            } catch (NumberFormatException e) {
                statusCode = CompletionStatus.NOT_STARTED.getCode();
            }
        } else {
            statusCode = CompletionStatus.NOT_STARTED.getCode();
        }

        try {
            return CompletionStatus.fromCode(statusCode).getDescription();
        } catch (IllegalArgumentException e) {
            return "Unknown";
        }
    }


    public Map<String, List<?>> stateWisSoiSummary(String state) {

        List<NakshaMISDataEntrySolrAndVender> vendorList = getAllEntries();

        // Filter by state if not "all"
        if (state != null && !"all".equalsIgnoreCase(state)) {
            vendorList = vendorList.stream()
                    .filter(v -> state.equalsIgnoreCase(v.getStateName()))
                    .collect(Collectors.toList());
        }

        // Return empty if no data
        if (vendorList.isEmpty()) {
            return Map.of("table", List.of(), "contractors", List.of());
        }

        List<List<String>> table = new ArrayList<>();
        List<String> contractors = new ArrayList<>();

        List<List<String>> aerialList = new ArrayList<>();
        List<List<String>> oriList = new ArrayList<>();
        List<List<String>> dsmList = new ArrayList<>();
        List<List<String>> dtmList = new ArrayList<>();
        List<List<String>> meshList = new ArrayList<>();
        List<List<String>> twoDfeatList = new ArrayList<>();
        List<List<String>> threeDfeatList = new ArrayList<>();

        for (NakshaMISDataEntrySolrAndVender v : vendorList) {
            contractors.add(v.getContractor());

            List<String> mainRow = buildAerialRow(v);
            List<String> rowAerial = buildAerialRow(v);
            List<String> rowOri = buildOriRow(v);
            List<String> rowDsm = buildDsmRow(v);
            List<String> rowDtm = buildDtmRow(v);
            List<String> rowMesh = buildMeshRow(v);
            List<String> row2DFeat = build2DFeatRow(v);
            List<String> row3DFeat = build3DFeatRow(v);

            // Append category-specific data to main row
            mainRow.addAll(rowOri.subList(3, rowOri.size()));
            mainRow.addAll(rowDsm.subList(3, rowDsm.size()));
            mainRow.addAll(rowDtm.subList(3, rowDtm.size()));
            mainRow.addAll(rowMesh.subList(3, rowMesh.size()));
            mainRow.addAll(row2DFeat.subList(3, row2DFeat.size()));
            mainRow.addAll(row3DFeat.subList(3, row3DFeat.size()));

            // Add to master lists
            table.add(mainRow);
            aerialList.add(rowAerial);
            oriList.add(rowOri);
            dsmList.add(rowDsm);
            dtmList.add(rowDtm);
            meshList.add(rowMesh);
            twoDfeatList.add(row2DFeat);
            threeDfeatList.add(row3DFeat);
        }

        return Map.of(
                "table", table,
                "aerialList", aerialList,
                "oriList", oriList,
                "dsmList", dsmList,
                "dtmList", dtmList,
                "meshList", meshList,
                "twoDfeatList", twoDfeatList,
                "threeDfeatList", threeDfeatList,
                "contractors", contractors
        );
    }

/* ------------------------
   Helper Methods
   ------------------------ */

    private List<String> buildAerialRow(NakshaMISDataEntrySolrAndVender v) {
        List<String> row = baseInfo(v);
        row.add(defaultIfNull(v.getTech1Cumulative()));
        row.add(getDescriptionOrNA(v.getTechStatus()));
        row.add(defaultIfNull(v.getTech1Percentage()) + "%");

        row.add(defaultIfNull(v.getTech2Cumulative()));
        row.add(getDescriptionOrNA(v.getTech2ObliqueStatus()));
        row.add(defaultIfNull(v.getTech2Percentage()) + "%");

        row.add(defaultIfNull(v.getTech3Cumulative()));
        row.add(defaultIfNull(v.getTech3Percentage()) + "%");
        row.add(defaultIfNull(v.getTech3LidarSensor()));
        row.add(defaultIfNull(v.getTech3LidarSensorPercentage()) + "%");
        row.add(getDescriptionOrNA(v.getTech3ObliqueStatus()));
        return row;
    }

    private List<String> buildOriRow(NakshaMISDataEntrySolrAndVender v) {
        List<String> row = baseInfo(v);
        row.add(safe(v.getSeparateOriDataProcessingCumulative()));
        row.add(getDescriptionOrNA(v.getOriCumulativestatus()));
        row.add(safe(v.getSeparateOriDataProcessingPercentage()) + "%");

        row.add(safe(v.getSeparateOriQaqcDataProcessingCumulative()));
        row.add(getDescriptionOrNA(v.getOriCumulativeQaQcStatus()));
        row.add(safe(v.getSeparateOriQaqcDataProcessingPercentage()) + "%");
        return row;
    }

    private List<String> buildDsmRow(NakshaMISDataEntrySolrAndVender v) {
        List<String> row = baseInfo(v);
        row.add(safe(v.getDsmDataProcessingCumulative()));
        row.add(getDescriptionOrNA(v.getDsmSubmissionStatus()));
        row.add(safe(v.getDsmDataProcessingPercentage()) + "%");

        row.add(safe(v.getDsmDataProcessingQaqcCumulative()));
        row.add(getDescriptionOrNA(v.getDsmSubmissionQaQcStatus()));
        row.add(safe(v.getDsmDataProcessingQaqcPercentage()) + "%");
        return row;
    }

    private List<String> buildDtmRow(NakshaMISDataEntrySolrAndVender v) {
        List<String> row = baseInfo(v);
        row.add(safe(v.getDtmDataProcessingCumulative()));
        row.add(getDescriptionOrNA(v.getDtmSubmissionStatus()));
        row.add(safe(v.getDtmDataProcessingPercentage()) + "%");

        row.add(safe(v.getDtmDataProcessingQaqcCumulative()));
        row.add(getDescriptionOrNA(v.getDtmSubmissionQaQcStatus()));
        row.add(safe(v.getDtmDataProcessingQaqcPercentage()) + "%");
        return row;
    }

    private List<String> buildMeshRow(NakshaMISDataEntrySolrAndVender v) {
        List<String> row = baseInfo(v);
        row.add(safe(v.getMeshDataProcessingCumulative()));
        row.add(getDescriptionOrNA(v.getThreedMeshModelStatus()));
        row.add(safe(v.getMeshDataProcessingPercentage()) + "%");

        row.add(safe(v.getMeshDataProcessingQaqcCumulative()));
        row.add(getDescriptionOrNA(v.getThreedMeshModelQaQcStatus()));
        row.add(safe(v.getMeshDataProcessingQaqcPercentage()) + "%");
        return row;
    }

    private List<String> build2DFeatRow(NakshaMISDataEntrySolrAndVender v) {
        List<String> row = baseInfo(v);
        row.add(defaultIfNull(v.getFeatureExtractionOriCumulativeCompleted()));
        row.add(getDescriptionOrNA(v.getTwoDfeatureextractionStatus()));
        row.add(defaultIfNull(v.getFeatureExtractionOriPercentageCompleted()) + "%");

        row.add(defaultIfNull(v.getFeatureExtractionOriQaqcCumulativeCompleted()));
        row.add(getDescriptionOrNA(v.getTwoDfeatureextractionQaQcStatus()));
        row.add(defaultIfNull(v.getFeatureExtractionOriQaqcPercentageCompleted()) + "%");

        row.add(defaultIfNull(v.getTwoDFeatextrStereomodeCumulative()));
        row.add(getDescriptionOrNA(v.getTwoDFeatextrStereomodeStatus()));
        row.add(defaultIfNull(v.getTwoDFeatextrStereomodePercentage()) + "%");

        row.add(defaultIfNull(v.getTwoDFeatextrStereomodeCumulativeQaQc()));
        row.add(getDescriptionOrNA(v.getTwoDFeatextrStereomodeQaQcStatus()));
        row.add(defaultIfNull(v.getTwoDFeatextrStereomodePercentageQaQc()) + "%");
        return row;
    }

    private List<String> build3DFeatRow(NakshaMISDataEntrySolrAndVender v) {
        List<String> row = baseInfo(v);
        row.add(defaultIfNull(v.getThreeDFeatextrCumulative()));
        row.add(getDescriptionOrNA(v.getThreeDFeatextrStatus()));
        row.add(defaultIfNull(v.getThreeDFeatextrPercentage()) + "%");

        row.add(defaultIfNull(v.getThreeDFeatextrCumulativeQaQc()));
        row.add(getDescriptionOrNA(v.getThreeDFeatextrQaQcStatus()));
        row.add(defaultIfNull(v.getThreeDDFeatextrPercentageQaQc()) + "%");

        row.add(defaultIfNull(v.getThreeDmilestoneCumulative()));
        row.add(getDescriptionOrNA(v.getThreeDmilestoneStatus()));
        row.add(defaultIfNull(v.getThreeDmilestonePercentage()) + "%");

        row.add(defaultIfNull(v.getThreeDmilestoneCumulativeQaQc()));
        row.add(getDescriptionOrNA(v.getThreeDmilestoneQaQcStatus()));
        row.add(defaultIfNull(v.getThreeDmilestonePercentageQaQc()) + "%");
        return row;
    }

    private List<String> baseInfo(NakshaMISDataEntrySolrAndVender v) {
        return new ArrayList<>(List.of(
                v.getUlbName(),
                v.getTechnology(),
                v.getContractor()
        ));
    }



    public static String getDescriptionOrNA(Integer code) {
        return (code == null)
                ? CompletionStatus.NOT_AVAILABLE.getDescription()
                : CompletionStatus.fromCode(code).getDescription();
    }

    private String defaultIfNull(Double value) {
        return (value == null) ? "0.0" : String.valueOf(value);
    }




    public List<Map<String, Object>> getStateSoiWiseSummary(String state_name) {
        if (state_name == null || state_name.equalsIgnoreCase("all")) {
            Map<String, Object> map = new HashMap<>();

            map.put("Total ULBs", vendor.stream().map(NakshaMISDataEntrySolrAndVender::getUlbName)
                    .filter(Objects::nonNull).collect(Collectors.toSet()).size());
            map.put("3rd Party Agency", vendor.stream().map(NakshaMISDataEntrySolrAndVender::getContractor)
                    .filter(Objects::nonNull).collect(Collectors.toSet()).size());

            // Format all "sq km" values with 2 decimal places
            map.put("DoLR Sanctioned Area(in sq km)", String.format("%.2f",
                    vendor.stream().map(NakshaMISDataEntrySolrAndVender::getSanctionedArea)
                            .filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum()));
            map.put("Buffer Area Data Acquisition(in sq km)", String.format("%.2f",
                    vendor.stream().map(NakshaMISDataEntrySolrAndVender::getBufferAreaDataAcquisition)
                            .filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum()));
            map.put(" Total Technology", vendor.stream().map(NakshaMISDataEntrySolrAndVender::getTechnology)
                    .filter(Objects::nonNull).collect(Collectors.toSet()).size());
            map.put("Tech 1 Nadir Area Data Acquisition(in sq km)", String.format("%.2f",
                    vendor.stream().map(NakshaMISDataEntrySolrAndVender::getTech1Cumulative)
                            .filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum()));
            map.put("Tech 2 Oblique Area Data Acquisition(in sq km)", String.format("%.2f",
                    vendor.stream().map(NakshaMISDataEntrySolrAndVender::getTech2Cumulative)
                            .filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum()));
            map.put("Tech 3 Oblique Area Data Acquisition(in sq km)", String.format("%.2f",
                    vendor.stream().map(NakshaMISDataEntrySolrAndVender::getTech3Cumulative)
                            .filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum()));
            map.put("Tech 3  Lidar Sensor Area Data Acquisition(in sq km)", String.format("%.2f",
                    vendor.stream().map(NakshaMISDataEntrySolrAndVender::getTech3LidarSensor)
                            .filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum()));

            return Collections.singletonList(map);
        }

        return vendor.stream()
                .filter(e -> e.getStateName() != null && e.getStateName().equalsIgnoreCase(state_name))
                .collect(Collectors.groupingBy(NakshaMISDataEntrySolrAndVender::getStateName))
                .values()
                .stream()
                .map(stateList -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Total ULBs", stateList.stream().map(NakshaMISDataEntrySolrAndVender::getUlbName)
                            .filter(Objects::nonNull).collect(Collectors.toSet()).size());
                    map.put("3rd Party Agency", stateList.stream().map(NakshaMISDataEntrySolrAndVender::getContractor)
                            .filter(Objects::nonNull).collect(Collectors.toSet()).size());

                    // Apply formatting
                    map.put("DoLR Sanctioned Area(in sq km)", String.format("%.2f",
                            stateList.stream().map(NakshaMISDataEntrySolrAndVender::getSanctionedArea)
                                    .filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum()));
                    map.put("Buffer Area Data Acquisition(in sq km)", String.format("%.2f",
                            stateList.stream().map(NakshaMISDataEntrySolrAndVender::getBufferAreaDataAcquisition)
                                    .filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum()));
                    map.put(" Total Technology", stateList.stream().map(NakshaMISDataEntrySolrAndVender::getTechnology)
                            .filter(Objects::nonNull).collect(Collectors.toSet()).size());
                    map.put("Tech 1 Nadir Area Data Acquisition(in sq km)", String.format("%.2f",
                            stateList.stream().map(NakshaMISDataEntrySolrAndVender::getTech1Cumulative)
                                    .filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum()));
                    map.put("Tech 2 Oblique Area Data Acquisition(in sq km)", String.format("%.2f",
                            stateList.stream().map(NakshaMISDataEntrySolrAndVender::getTech2Cumulative)
                                    .filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum()));
                    map.put("Tech 3 Oblique Area Data Acquisition(in sq km)", String.format("%.2f",
                            stateList.stream().map(NakshaMISDataEntrySolrAndVender::getTech3Cumulative)
                                    .filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum()));
                    map.put("Tech 3  Lidar Sensor Area Data Acquisition(in sq km)", String.format("%.2f",
                            stateList.stream().map(NakshaMISDataEntrySolrAndVender::getTech3LidarSensor)
                                    .filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum()));

                    return map;
                })
                .collect(Collectors.toList());
    }




    public List<Map<String, Object>> getAllEntriesAsListOfMap() {
        List<Map<String, Object>> result = new ArrayList<>();

        double totalSanctionedArea = 0.0;
        double totalBufferArea = 0.0;

        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            Map<String, Object> row = new HashMap<>();
            row.put("ulbName", entry.getUlbName());
            row.put("stateName", entry.getStateName());
            row.put("gdName", entry.getGdName());
            row.put("contractor", entry.getContractor());
            row.put("technology", entry.getTechnology());

            double sanctioned = entry.getSanctionedArea() != null ? entry.getSanctionedArea() : 0.0;
            row.put("sanctionedArea", String.format("%.2f", sanctioned));
            totalSanctionedArea += sanctioned;

            double buffer = entry.getBufferAreaDataAcquisition() != null ? entry.getBufferAreaDataAcquisition() : 0.0;
            row.put("bufferAreaDataAcquisition", String.format("%.2f", buffer));
            totalBufferArea += buffer;

            result.add(row);
        }



        return result;
    }

    // ✅ All States/UTs
 /*   public List<Map<String, Object>> getAllStateWiseVendorStats() {
        return vendor.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getStateName() == null ? "UNKNOWN" : e.getStateName(),
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    String stateName = entry.getKey();
                    List<NakshaMISDataEntrySolrAndVender> stateVendors = entry.getValue();

                    // Stats
                    Map<String, Long> stats = calculateStatsForSubmissionByVendor(stateVendors);

                    // Contractor names (distinct list)
                    List<String> contractorNames = stateVendors.stream()
                            .map(NakshaMISDataEntrySolrAndVender::getContractor)
                            .filter(Objects::nonNull)
                            .distinct()
                            .sorted()
                            .collect(Collectors.toList());

                    // Prepare final result
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("state_name", stateName);
                    result.putAll(stats);
                    result.put("contractor_names", contractorNames); // ✅ added list of contractors
                    return result;
                })
                .collect(Collectors.toList());
    }*/

    public List<Map<String, Object>> getAllContractorWiseStats() {
        return vendor.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getContractor() == null ? "UNKNOWN" : e.getContractor(),
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    String contractorName = entry.getKey();
                    List<NakshaMISDataEntrySolrAndVender> contractorEntries = entry.getValue();

                    // Get unique state names and ULB names for this contractor
                    String stateNames = contractorEntries.stream()
                            .map(NakshaMISDataEntrySolrAndVender::getStateName)
                            .filter(Objects::nonNull)
                            .distinct()
                            .sorted()
                            .collect(Collectors.joining(", "));
                    
                    String ulbNamesWithStates = contractorEntries.stream()
                            .filter(e -> e.getUlbName() != null && e.getStateName() != null)
                            .map(e -> {
                                String stateName = e.getStateName();
                                // Convert common abbreviations to full names
                                switch (stateName.toUpperCase()) {
                                    case "UT":
                                    case "UNION TERRITORY":
                                        // Try to determine specific UT based on ULB name or other context
                                        String ulbName = e.getUlbName().toUpperCase();
                                        if (ulbName.contains("JAMMU") || ulbName.contains("SRINAGAR") || 
                                            ulbName.contains("ANANTNAG") || ulbName.contains("BARAMULLA") ||
                                            ulbName.contains("BISHNAH") || ulbName.contains("AWANTIPORA") ||
                                            ulbName.contains("KATRA") || ulbName.contains("PATTAN")) {
                                            stateName = "Jammu & Kashmir (UT)";
                                        } else if (ulbName.contains("LEH") || ulbName.contains("KARGIL")) {
                                            stateName = "Ladakh (UT)";
                                        } else if (ulbName.contains("DELHI") || ulbName.contains("NEW DELHI")) {
                                            stateName = "Delhi (UT)";
                                        } else if (ulbName.contains("CHANDIGARH")) {
                                            stateName = "Chandigarh (UT)";
                                        } else if (ulbName.contains("PONDICHERRY") || ulbName.contains("PUDUCHERRY") ||
                                                 ulbName.contains("MURUNGAPAKKAM")) {
                                            stateName = "Puducherry (UT)";
                                        } else if (ulbName.contains("DADRA") || ulbName.contains("NAGAR HAVELI") || 
                                                 ulbName.contains("DAMAN") || ulbName.contains("DIU")) {
                                            stateName = "Dadra and Nagar Haveli and Daman and Diu (UT)";
                                        } else if (ulbName.contains("PORT BLAIR") || ulbName.contains("ANDAMAN")) {
                                            stateName = "Andaman and Nicobar Islands (UT)";
                                        } else {
                                            stateName = "Union Territory";
                                        }
                                        break;
                                    case "J&K":
                                    case "JK":
                                        stateName = "Jammu & Kashmir (UT)";
                                        break;
                                    case "LD":
                                        stateName = "Ladakh (UT)";
                                        break;
                                    case "DL":
                                        stateName = "Delhi (UT)";
                                        break;
                                    case "CH":
                                        stateName = "Chandigarh (UT)";
                                        break;
                                    case "PY":
                                        stateName = "Puducherry (UT)";
                                        break;
                                    case "DN":
                                        stateName = "Dadra and Nagar Haveli and Daman and Diu (UT)";
                                        break;
                                    case "AN":
                                        stateName = "Andaman and Nicobar Islands (UT)";
                                        break;
                                    default:
                                        // Keep the original state name if it's already full
                                        break;
                                }
                                return e.getUlbName() + " (" + stateName + ")";
                            })
                            .distinct()
                            .sorted()
                            .collect(Collectors.joining(", "));

                    // Stats for this contractor (aggregated across all states)
                    Map<String, Long> stats = calculateStatsForSubmissionByVendor(contractorEntries);

                    // Clean up state names for the main table display
                    String cleanStateNames = stateNames;
                    if (stateNames != null && !stateNames.isEmpty()) {
                        cleanStateNames = Arrays.stream(stateNames.split(", "))
                                .map(state -> {
                                    // Apply the same state name mapping logic
                                    String cleanState = state;
                                    switch (state.toUpperCase()) {
                                        case "UT":
                                        case "UNION TERRITORY":
                                            cleanState = "Union Territory";
                                            break;
                                        case "J&K":
                                        case "JK":
                                            cleanState = "Jammu & Kashmir (UT)";
                                            break;
                                        case "LD":
                                            cleanState = "Ladakh (UT)";
                                            break;
                                        case "DL":
                                            cleanState = "Delhi (UT)";
                                            break;
                                        case "CH":
                                            cleanState = "Chandigarh (UT)";
                                            break;
                                        case "PY":
                                            cleanState = "Puducherry (UT)";
                                            break;
                                        case "DN":
                                            cleanState = "Dadra and Nagar Haveli and Daman and Diu (UT)";
                                            break;
                                        case "AN":
                                            cleanState = "Andaman and Nicobar Islands (UT)";
                                            break;
                                        default:
                                            // Remove (UT) suffix if present
                                            if (state.matches(".*\\s*\\(UT\\)$")) {
                                                cleanState = state.replaceAll("\\s*\\(UT\\)$", "").trim();
                                            }
                                            // Handle cases like "Jammu & Kashmir (UT)" -> "Jammu and Kashmir"
                                            if (cleanState.contains("Jammu") && cleanState.contains("Kashmir")) {
                                                cleanState = "Jammu and Kashmir";
                                            }
                                            break;
                                    }
                                    return cleanState;
                                })
                                .distinct()
                                .sorted()
                                .collect(Collectors.joining(", "));
                    }

                    // Prepare final result
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("contractor_name", contractorName);
                    result.put("state_name", cleanStateNames);
                    result.put("ulb_name_count", contractorEntries.stream()
                            .map(NakshaMISDataEntrySolrAndVender::getUlbName)
                            .filter(Objects::nonNull)
                            .distinct()
                            .count());
                    result.put("ulb_names", ulbNamesWithStates); // Add ULB names with states for display
                    result.putAll(stats);

                    return result;
                })
                .sorted((a, b) -> {
                    // Sort by contractor name only
                    String contractorA = (String) a.get("contractor_name");
                    String contractorB = (String) b.get("contractor_name");
                    return contractorA.compareTo(contractorB);
                })
                .collect(Collectors.toList());
    }



    // ✅ Single State
    public Map<String, Object> getStateWiseVendorStats(String stateName) {
        List<NakshaMISDataEntrySolrAndVender> filtered = vendor.stream()
                .filter(e -> stateName.equalsIgnoreCase(e.getStateName()))
                .collect(Collectors.toList());

        // Stats
        Map<String, Long> stats = calculateStatsForSubmissionByVendor(filtered);

        // Contractor names (distinct list)
        List<String> contractorNames = filtered.stream()
                .map(NakshaMISDataEntrySolrAndVender::getContractor)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Prepare final result
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("state_name", stateName);
        result.putAll(stats);
        result.put("contractor_names", contractorNames); // ✅ added list of contractors
        return result;
    }


    // ✅ Reuse your existing logic for one state
    private Map<String, Long> calculateStatsForSubmissionByVendor(List<NakshaMISDataEntrySolrAndVender> filtered) {
        Map<String, Long> stats = new HashMap<>();

        stats.put("ulb_name_count", filtered.stream().map(NakshaMISDataEntrySolrAndVender::getUlbName).filter(Objects::nonNull).distinct().count());
        stats.put("contractor_count", filtered.stream().map(NakshaMISDataEntrySolrAndVender::getContractor).filter(Objects::nonNull).distinct().count());

        long technologyCount = filtered.stream()
                .filter(e -> e.getTechnology() != null)
                .count();
        stats.put("technology_count", technologyCount);

        stats.put("technology_completed_count", filtered.stream().filter(e ->
                ("Tech 1".equals(e.getTechnology()) && e.getTechStatus() != null && (e.getTechStatus() == 3 || e.getTechStatus() == 10)) ||
                        ("Tech 2".equals(e.getTechnology()) && e.getTech2ObliqueStatus() != null && (e.getTech2ObliqueStatus() == 3 || e.getTech2ObliqueStatus() == 10)) ||
                        ("Tech 3".equals(e.getTechnology()) && e.getTech3ObliqueStatus() != null && (e.getTech3ObliqueStatus() == 3 || e.getTech3ObliqueStatus() == 10))
        ).count());

        stats.put("ori_cumulative_status_count", filtered.stream().filter(e -> e.getOriCumulativestatus() != null).count());
        stats.put("ori_cumulative_status_3_5_count", filtered.stream().filter(e -> e.getOriCumulativestatus() != null && (e.getOriCumulativestatus() == 3 || e.getOriCumulativestatus() == 5)).count());

        stats.put("dsm_submission_status_count", filtered.stream().filter(e -> e.getDsmSubmissionStatus() != null).count());
        stats.put("dsm_submission_status_3_5_count", filtered.stream().filter(e -> e.getDsmSubmissionStatus() != null && (e.getDsmSubmissionStatus() == 3 || e.getDsmSubmissionStatus() == 5)).count());

        stats.put("dtm_submission_status_count", filtered.stream().filter(e -> e.getDtmSubmissionStatus() != null).count());
        stats.put("dtm_submission_status_3_5_count", filtered.stream().filter(e -> e.getDtmSubmissionStatus() != null && (e.getDtmSubmissionStatus() == 3 || e.getDtmSubmissionStatus() == 5)).count());

        stats.put("threeD_Meshmodel_status_count", filtered.stream().filter(e -> Arrays.asList("Tech 2","Tech 3").contains(e.getTechnology()) && e.getThreedMeshModelStatus() != null).count());
        stats.put("threeD_Meshmodel_status_3_5_count", filtered.stream().filter(e -> e.getThreedMeshModelStatus() != null && (e.getThreedMeshModelStatus() == 3 || e.getThreedMeshModelStatus() == 5)).count());

        stats.put("twoD_featureextraction_status_count", filtered.stream().filter(e -> "Tech 1".equals(e.getTechnology()) && e.getTwoDfeatureextractionStatus() != null).count());
        stats.put("twoD_featureextraction_status_3_5_count", filtered.stream().filter(e -> e.getTwoDfeatureextractionStatus() != null && (e.getTwoDfeatureextractionStatus() == 3 || e.getTwoDfeatureextractionStatus() == 5)).count());

        stats.put("twoD_featextr_stereomode_status_count", filtered.stream().filter(e -> "Tech 1".equals(e.getTechnology()) && e.getTwoDFeatextrStereomodeStatus() != null).count());
        stats.put("twoD_featextr_stereomode_status_3_5_count", filtered.stream().filter(e -> e.getTwoDFeatextrStereomodeStatus() != null && (e.getTwoDFeatextrStereomodeStatus() == 3 || e.getTwoDFeatextrStereomodeStatus() == 5)).count());

        stats.put("threeD_featextr_status_count", filtered.stream().filter(e -> e.getThreeDFeatextrStatus() != null).count());
        stats.put("threeD_featextr_status_3_5_count", filtered.stream().filter(e -> e.getThreeDFeatextrStatus() != null && (e.getThreeDFeatextrStatus() == 3 || e.getThreeDFeatextrStatus() == 5)).count());

        stats.put("threeD_milestone_status_count", filtered.stream().filter(e -> e.getThreeDmilestoneStatus() != null).count());
        stats.put("threeD_milestone_status_3_5_count", filtered.stream().filter(e -> e.getThreeDmilestoneStatus() != null && (e.getThreeDmilestoneStatus() == 3 || e.getThreeDmilestoneStatus() == 5)).count());

        return stats;
    }


  /*  public List<Map<String, Object>> getAllStateWiseGdStats() {
        return vendor.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getStateName() == null ? "UNKNOWN" : e.getStateName(),
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    String stateName = entry.getKey();
                    List<NakshaMISDataEntrySolrAndVender> stateVendors = entry.getValue();

                    // Stats
                    Map<String, Long> stats = calculateStatsForQaQcByGd(stateVendors);

                    // Contractor names (distinct list)
                    List<String> contractorNames = stateVendors.stream()
                            .map(NakshaMISDataEntrySolrAndVender::getGdName)
                            .filter(Objects::nonNull)
                            .distinct()
                            .sorted()
                            .collect(Collectors.toList());

                    // Prepare final result
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("state_name", stateName);
                    result.putAll(stats);
                    result.put("contractor_names", contractorNames); // ✅ added list of contractors
                    return result;
                })
                .collect(Collectors.toList());
    }
*/

    public List<Map<String, Object>> getAllGdWiseStats() {
        return vendor.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getGdName() == null ? "UNKNOWN" : e.getGdName(),
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    String gdName = entry.getKey();
                    List<NakshaMISDataEntrySolrAndVender> gdEntries = entry.getValue();

                    // Stats for this GD
                    Map<String, Long> stats = calculateStatsForQaQcByGd(gdEntries);

                    // Prepare final result
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("gd_name", gdName);
                    result.putAll(stats);

                    return result;
                })
                .collect(Collectors.toList());
    }

    // ✅ Single State
    public Map<String, Object> getStateWiseGdStats(String stateName) {
        List<NakshaMISDataEntrySolrAndVender> filtered = vendor.stream()
                .filter(e -> stateName.equalsIgnoreCase(e.getStateName()))
                .collect(Collectors.toList());

        // Stats
        Map<String, Long> stats = calculateStatsForQaQcByGd(filtered);

        // Contractor names (distinct list)
        List<String> contractorNames = filtered.stream()
                .map(NakshaMISDataEntrySolrAndVender::getContractor)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Prepare final result
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("state_name", stateName);
        result.putAll(stats);
        result.put("contractor_names", contractorNames); // ✅ added list of contractors
        return result;
    }

    private Map<String, Long> calculateStatsForQaQcByGd(List<NakshaMISDataEntrySolrAndVender> filtered) {
        Map<String, Long> stats = new HashMap<>();

        stats.put("ulb_name_count", filtered.stream().map(NakshaMISDataEntrySolrAndVender::getUlbName).filter(Objects::nonNull).distinct().count());
        stats.put("contractor_count", filtered.stream().map(NakshaMISDataEntrySolrAndVender::getContractor).filter(Objects::nonNull).distinct().count());

        long technologyCount = filtered.stream()
                .filter(e -> e.getTechnology() != null)
                .count();
        stats.put("technology_count", technologyCount);

        stats.put("technology_completed_count", filtered.stream().filter(e ->
                ("Tech 1".equals(e.getTechnology()) && e.getTechStatus() != null && (e.getTechStatus() == 3 || e.getTechStatus() == 10)) ||
                        ("Tech 2".equals(e.getTechnology()) && e.getTech2ObliqueStatus() != null && (e.getTech2ObliqueStatus() == 3 || e.getTech2ObliqueStatus() == 10)) ||
                        ("Tech 3".equals(e.getTechnology()) && e.getTech3ObliqueStatus() != null && (e.getTech3ObliqueStatus() == 3 || e.getTech3ObliqueStatus() == 10))
        ).count());

        stats.put("ori_cumulative_status_count", filtered.stream().filter(e -> e.getOriCumulativeQaQcStatus() != null).count());
        stats.put("ori_cumulative_status_6_8_count", filtered.stream().filter(e -> e.getOriCumulativeQaQcStatus() != null && (e.getOriCumulativeQaQcStatus() == 6 || e.getOriCumulativeQaQcStatus() == 8)).count());

        stats.put("dsm_submission_status_count", filtered.stream().filter(e -> e.getDsmSubmissionQaQcStatus() != null).count());
        stats.put("dsm_submission_status_6_8_count", filtered.stream().filter(e -> e.getDsmSubmissionQaQcStatus() != null && (e.getDsmSubmissionQaQcStatus() == 6 || e.getDsmSubmissionQaQcStatus() == 8)).count());

        stats.put("dtm_submission_status_count", filtered.stream().filter(e -> e.getDtmSubmissionQaQcStatus() != null).count());
        stats.put("dtm_submission_status_6_8_count", filtered.stream().filter(e -> e.getDtmSubmissionQaQcStatus() != null && (e.getDtmSubmissionQaQcStatus() == 6 || e.getDtmSubmissionQaQcStatus() == 8)).count());

        stats.put("threeD_Meshmodel_status_count", filtered.stream().filter(e -> Arrays.asList("Tech 2","Tech 3").contains(e.getTechnology()) && e.getThreedMeshModelQaQcStatus() != null).count());
        stats.put("threeD_Meshmodel_status_6_8_count", filtered.stream().filter(e -> e.getThreedMeshModelStatus() != null && (e.getThreedMeshModelQaQcStatus() == 6 || e.getThreedMeshModelQaQcStatus() == 8)).count());

        stats.put("twoD_featureextraction_status_count", filtered.stream().filter(e -> "Tech 1".equals(e.getTechnology()) && e.getTwoDfeatureextractionQaQcStatus() != null).count());
        stats.put("twoD_featureextraction_status_6_8_count", filtered.stream().filter(e -> e.getTwoDfeatureextractionQaQcStatus() != null && (e.getTwoDfeatureextractionQaQcStatus() == 6 || e.getTwoDfeatureextractionQaQcStatus() == 8)).count());

        stats.put("twoD_featextr_stereomode_status_count", filtered.stream().filter(e -> "Tech 1".equals(e.getTechnology()) && e.getTwoDFeatextrStereomodeQaQcStatus() != null).count());
        stats.put("twoD_featextr_stereomode_status_6_8_count", filtered.stream().filter(e -> e.getTwoDFeatextrStereomodeQaQcStatus() != null && (e.getTwoDFeatextrStereomodeQaQcStatus() == 6 || e.getTwoDFeatextrStereomodeQaQcStatus() == 8)).count());

        stats.put("threeD_featextr_status_count", filtered.stream().filter(e -> e.getThreeDFeatextrQaQcStatus() != null).count());
        stats.put("threeD_featextr_status_6_8_count", filtered.stream().filter(e -> e.getThreeDFeatextrQaQcStatus() != null && (e.getThreeDFeatextrQaQcStatus() == 6 || e.getThreeDFeatextrQaQcStatus() == 8)).count());

        stats.put("threeD_milestone_status_count", filtered.stream().filter(e -> e.getThreeDmilestoneQaQcStatus() != null).count());
        stats.put("threeD_milestone_status_6_8_count", filtered.stream().filter(e -> e.getThreeDmilestoneQaQcStatus() != null && (e.getThreeDmilestoneQaQcStatus() == 6 || e.getThreeDmilestoneQaQcStatus() == 8)).count());

        return stats;
    }


    public List<String> getTimelineUniqueBackupDates() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

        return vendorTimeBackups.stream()
                .map(TimelineBackup::getBackupDate)               // extract LocalDate
                .filter(Objects::nonNull)                        // avoid nulls
                .distinct()                                      // remove duplicates
                .sorted(Comparator.reverseOrder())               // sort descending
                .map(date -> date.format(formatter))             // format each LocalDate
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getStateTimelineAsMapList(String stateName, String backupDateStr) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);
       try {
            // ✅ Convert String -> LocalDate
            LocalDate backupDate = LocalDate.parse(backupDateStr, formatter);

            vendorTimeBackups.stream()
                    .filter(e -> stateName.equalsIgnoreCase(e.getStateName()))
                    .filter(e -> backupDate.equals(e.getBackupDate())) // ✅ match on backupDate
                    .forEach(e -> {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("ulbName", e.getUlbName());
                        row.put("package", e.getSoipackage());
                        row.put("technology", e.getTechnology());
                        row.put("contractor", e.getContractor());
                        row.put("commencement", formatDate(e.getDateOfCommecement(), formatter));
                        row.put("bufferArea", e.getBufferAreaDataAcquisition());

                        // 🔽 Always provide both values: Completion and Tentative
                        row.put("completionDate", formatDate(e.getCompletionDate(), formatter));
                        row.put("tentativeDate", formatDate(e.getTentativeDate(), formatter));

                        row.put("oriTentativeDate", formatDate(e.getOriTentativeDate(), formatter));
                        row.put("oriCompletionDate", formatDate(e.getOriCompletionDate(), formatter));

                        row.put("oriQaqcTentativeDate", formatDate(e.getOriqaQcTentativeDate(), formatter));
                        row.put("oriQaqcCompletionDate", formatDate(e.getOriqaQcCompletionDate(), formatter));

                        row.put("milestone2Completion", formatDate(e.getMilestone2CompletionDate(), formatter));
                        row.put("milestone2Tentative", formatDate(e.getMilestone2TentativeDate(), formatter));

                        row.put("qaQcMilestone2Completion", formatDate(e.getQaQcmilestone2CompletionDate(), formatter));
                        row.put("qaQcMilestone2Tentative", formatDate(e.getQaQcmilestone2TentativeDate(), formatter));

                        row.put("feature2DCompletion", formatDate(e.getFeatureExtraction2DCompletionDate(), formatter));
                        row.put("feature2DTentative", formatDate(e.getFeatureExtraction2DTentativeDate(), formatter));

                        row.put("qaQc2DCompletion", formatDate(e.getQaQc2DCompletionDate(), formatter));
                        row.put("qaQc2DTentative", formatDate(e.getQaQc2DTentativeDate(), formatter));

                        row.put("stereo2DCompletion", formatDate(e.getStereo2DCompletionDate(), formatter));
                        row.put("stereo2DTentative", formatDate(e.getStereo2DTentativeDate(), formatter));

                        row.put("qaQcStereo2DCompletion", formatDate(e.getQaQcStereo2DCompletionDate(), formatter));
                        row.put("qaQcStereo2DTentative", formatDate(e.getQaQcStereo2DTentativeDate(), formatter));

                        row.put("threeDFeatExtCompletion", formatDate(e.getThreeDFeatExtCompletionDate(), formatter));
                        row.put("threeDFeatExtTentative", formatDate(e.getThreeDFeatExtTentativeDate(), formatter));

                        row.put("threeDFeatExtQaQcCompletion", formatDate(e.getThreeDFeatExtQaQcCompletionDate(), formatter));
                        row.put("threeDFeatExtQaQcTentative", formatDate(e.getThreeDFeatExtQaQcTentativeDate(), formatter));

                        row.put("threeDMilestoneCompletion", formatDate(e.getThreeDMilestoneCompletionDate(), formatter));
                        row.put("threeDMilestoneTentative", formatDate(e.getThreeDMilestoneTentativeDate(), formatter));

                        row.put("threeDMilestoneQaQcCompletion", formatDate(e.getThreeDMilestoneQaQcCompletionDate(), formatter));
                        row.put("threeDMilestoneQaQcTentative", formatDate(e.getThreeDMilestoneQaQcTentativeDate(), formatter));

                        resultList.add(row);
                    });
        } catch (Exception e) {
            System.err.println("Error while processing vendor data for state: " + stateName + " and backupDate: " + backupDateStr);
            e.printStackTrace();
        }

        return resultList;
    }

    public Map<String, List<String>> getFlyingCompletedT1AndT2AndT3() {
        List<NakshaMISDataEntrySolrAndVender> vendorList = getAllEntries();

        // buckets for each technology
        Map<String, List<String>> result = new LinkedHashMap<>();
        result.put("tech1_completed", new ArrayList<>());
        result.put("tech1_under_process", new ArrayList<>());
        result.put("tech1_not_started", new ArrayList<>());

        result.put("tech2_completed", new ArrayList<>());
        result.put("tech2_under_process", new ArrayList<>());
        result.put("tech2_not_started", new ArrayList<>());

        result.put("tech3_completed", new ArrayList<>());
        result.put("tech3_under_process", new ArrayList<>());
        result.put("tech3_not_started", new ArrayList<>());

        for (NakshaMISDataEntrySolrAndVender entry : vendorList) {
            String ulbName = entry.getUlbName();
            String stateName = entry.getStateName();
            String stateAndUlb = stateName + " - " + ulbName;

            // categorize Tech1, Tech2, Tech3
            categorize(entry.getTechStatus(), "tech1", stateAndUlb, result);
            categorize(entry.getTech2ObliqueStatus(), "tech2", stateAndUlb, result);
            categorize(entry.getTech3ObliqueStatus(), "tech3", stateAndUlb, result);
        }

        // sort all lists alphabetically
        result.values().forEach(Collections::sort);

        // not flying zone
        List<String> ulbNamesWithFlyingZoneZero = getUlbNamesWithFlyingZoneZero();
        result.put("notFlyingZone", ulbNamesWithFlyingZoneZero);

        return result;
    }

    /**
     * Helper method: categorizes one technology status into Completed/Under Process/Not Started
     */
    private void categorize(Integer status, String techPrefix, String stateAndUlb, Map<String, List<String>> result) {
        if (status == null) {
            return;
        }

        if (status.equals(CompletionStatus.COMPLETED.getCode()) ||
                status.equals(CompletionStatus.RE_FLY_COMPLETED.getCode())) {
            result.get(techPrefix + "_completed").add(stateAndUlb);
        } else if (status.equals(CompletionStatus.UNDER_PROCESS.getCode()) ||
                status.equals(CompletionStatus.RE_FLY_UNDER_PROGRESS.getCode()) ||
                status.equals(CompletionStatus.RETURNED.getCode())) {
            result.get(techPrefix + "_under_process").add(stateAndUlb);
        } else if (status.equals(CompletionStatus.NOT_STARTED.getCode())) {
            result.get(techPrefix + "_not_started").add(stateAndUlb);
        }
    }





    public Map<String, Object> dataUploadedByGDsOnNAKSHAPorta() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> gdList = new ArrayList<>();
        long count = 0;
        Set<String> states = new HashSet<>();

        for (NakshaMISDataEntrySolrAndVender entry : vendor) {
            String gd = entry.getGdName();
            String stateName = entry.getStateName();
            String ulbName = entry.getUlbName();
            String nakshaUploadedWebportal = entry.getNakshaUploadedWebportal();

            // Treat null as "No"
            if (nakshaUploadedWebportal == null) {
                nakshaUploadedWebportal = "No";
            }

            if ("Yes".equalsIgnoreCase(nakshaUploadedWebportal)) {
                count++;
            }

            states.add(stateName);

            Map<String, String> map = new HashMap<>();
            map.put("state", stateName);
            map.put("gd", gd);
            map.put("ulb", ulbName);
            map.put("uploaded", nakshaUploadedWebportal);
            gdList.add(map);
        }

        // Sort the list by state name
        gdList.sort((a, b) -> a.get("state").compareToIgnoreCase(b.get("state")));

        // Sort the states list as well
        List<String> sortedStates = new ArrayList<>(states);
        sortedStates.sort(String::compareToIgnoreCase);

        result.put("gdList", gdList);
        result.put("count", count);
        result.put("states", sortedStates);

        return result;
    }




}



