package in.gov.dilrmp.services.naksha;

import in.gov.dilrmp.models.naksha.CapacityBuilding;


import in.gov.dilrmp.models.naksha.CapacityBuildingReportDTO;

import in.gov.dilrmp.repositories.administrativeBoundry.StateRepositry;
import in.gov.dilrmp.repositories.naksha.CapacityBuildingRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;




@Service
public class CapacityBuildingService {
    private static final Logger logger = LoggerFactory.getLogger(CapacityBuildingService.class);

    @Autowired
    private CapacityBuildingRepository repository;

    @Autowired
    StateRepositry stateRepositry;
    @Autowired
    private HttpSession session;
    private List<CapacityBuilding> buildings;

    public void capacityBuildingData() {
        buildings = repository.findAll();
    }

    public List<CapacityBuilding> getAllEntries() {
        buildings = buildings.stream()
                .sorted(Comparator.comparing(b -> b.getState().getName())) // ✅ Sort by state name
                .map(l -> {
                    l.setFormatedfieldTeamsTrained(String.format("%.0f", l.getFieldTeamsTrained()));
                    l.setFormatedpercentageTrained(String.format("%.2f", l.getPercentageTrained()));
                    return l;
                })
                .collect(Collectors.toList());

        session.setAttribute("capacityBuilding", buildings);

        session.setAttribute("grandTotal",capacityBuildingGrandTotal(buildings));
        return buildings;
    }

    public String saveCapacityBuilding(CapacityBuilding capacityBuilding) {
        /*try {
            capacityBuilding.setUpdateOnDate(LocalDate.now());
            repository.save(capacityBuilding);
            logger.info("Capacity Building data saved successfully for state: {}",
                    capacityBuilding.getState() != null ? capacityBuilding.getState().getName() : "Unknown");
            return "success";
        } catch (Exception e) {
            logger.error("Error saving Capacity Building data", e);
            return "failure";
        }*/

        try {
            int rows = repository.updateCapacityBuildingFields(capacityBuilding.getMuser().getId(), capacityBuilding.getMembersToBeTrained(),
                    LocalDate.now()
            );

            if (rows > 0) {
                logger.info("Successfully updated CapacityBuilding for muserId {}", capacityBuilding.getMuser().getId());
                return "success";
            } else {
                logger.warn("No CapacityBuilding record found for muserId {}", capacityBuilding.getMuser().getId());
                return "not_found";
            }
        } catch (Exception e) {
            logger.error("Error while updating CapacityBuilding", e);
            return "failure";
        }
    }
    public CapacityBuilding findById(Long id) {
        if (id == null) {
            logger.warn("findById called with null ID");
            return null;
        }

        return repository.findById(id).orElse(null);
    }

    public CapacityBuilding findByUlbMasterId(Long ulbMasterID) {
        return repository.findByUlbMasterId(ulbMasterID);
    }

    public LocalDate getUpdateOnDateByUserId(Long userId) {
        return repository.findUpdateOnDateByUserId(userId);
    }

    public List<CapacityBuilding> capacityBuildingGrandTotal(List<CapacityBuilding> list) {
       /* List<CapacityBuilding> list = repository.findAll();*/
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        int GrandTotalNigstMasterTrainers = list.stream()
                .mapToInt(entry -> entry.getNigstMasterTrainers())
                .sum();
        int GrandTotalCoeMasterTrainers = list.stream()
                .mapToInt(entry -> entry.getCoeMasterTrainers())
                .sum();
        int GrandTotalTotalMasterTrainers = list.stream()
                .mapToInt(entry -> entry.getTotalMasterTrainers())
                .sum();
        int GrandTotalFieldTeamsSanctioned = list.stream()
                .mapToInt(entry -> entry.getFieldTeamsSanctioned())
                .sum();
        int GrandTotalMembersTrained = list.stream()
                .mapToInt(entry -> entry.getMembersTrained())
                .sum();

        int GrandTotalMembersToBeTrained = list.stream()
                .mapToInt(entry -> entry.getMembersToBeTrained())
                .sum();

        list.forEach(entry -> {
            entry.setGrandTotalNigstMasterTrainers(GrandTotalNigstMasterTrainers);
            entry.setGrandTotalCoeMasterTrainers(GrandTotalCoeMasterTrainers);
            entry.setGrandTotalTotalMasterTrainers(GrandTotalTotalMasterTrainers);
            entry.setGrandTotalFieldTeamsSanctioned(GrandTotalFieldTeamsSanctioned);
            entry.setGrandTotalMembersTrained(GrandTotalMembersTrained);
            entry.setGrandTotalMembersToBeTrained(GrandTotalMembersToBeTrained);
        });

        return list;
    }


    public List<CapacityBuilding> getListCapacityBuildingReport() {
        List<CapacityBuilding> list = repository.findAll();
        list = list.stream()
                .map(l -> {
                    l.setFormatedfieldTeamsTrained(String.format("%.0f", l.getFieldTeamsTrained()));
                    l.setFormatedpercentageTrained(String.format("%.2f", l.getPercentageTrained()));
                    return l;
                })
                .collect(Collectors.toList());
        return list;
    }

    public CapacityBuildingReportDTO getCapacityBuildingGrandTotal(Long stateId) {
        List<CapacityBuilding> list = getAllEntries();

        if (list == null || list.isEmpty()) {
            return new CapacityBuildingReportDTO(); // return empty total object
        }

        if (stateId != null && stateId > 0) {
            list = list.stream()
                    .filter(e -> e.getState() != null && stateId.equals(e.getState().getId()))
                    .collect(Collectors.toList());
        }

        CapacityBuildingReportDTO total = new CapacityBuildingReportDTO();
        total.setState_name("Total");

        int grandTotalNigstMasterTrainers = list.stream().mapToInt(e -> e.getNigstMasterTrainers() != null ? e.getNigstMasterTrainers() : 0).sum();
        int grandTotalCoeMasterTrainers = list.stream().mapToInt(e -> e.getCoeMasterTrainers() != null ? e.getCoeMasterTrainers() : 0).sum();
        int grandTotalTotalMasterTrainers = list.stream().mapToInt(e -> e.getTotalMasterTrainers() != null ? e.getTotalMasterTrainers() : 0).sum();
        int grandTotalFieldTeamsSanctioned = list.stream().mapToInt(e -> e.getFieldTeamsSanctioned() != null ? e.getFieldTeamsSanctioned() : 0).sum();
        int grandTotalMembersTrained = list.stream().mapToInt(e -> e.getMembersTrained() != null ? e.getMembersTrained() : 0).sum();
        int grandTotalMembersToBeTrained = list.stream().mapToInt(e -> e.getMembersToBeTrained() != null ? e.getMembersToBeTrained() : 0).sum();

        total.setGrandTotalNigstMasterTrainers(grandTotalNigstMasterTrainers);
        total.setGrandTotalCoeMasterTrainers(grandTotalCoeMasterTrainers);
        total.setGrandTotalTotalMasterTrainers(grandTotalTotalMasterTrainers);
        total.setGrandTotalFieldTeamsSanctioned(grandTotalFieldTeamsSanctioned);
        total.setGrandTotalMembersTrained(grandTotalMembersTrained);
        total.setGrandTotalMembersToBeTrained(grandTotalMembersToBeTrained);

        // ✅ Percentage logic with two decimal places
        double percentage;
        if (stateId == null || stateId == 0) {
            if (grandTotalMembersToBeTrained > 0) {
                percentage = (grandTotalMembersToBeTrained * 100.0) / grandTotalMembersTrained;
            } else {
                percentage = 0.0;
            }
        } else {
            percentage = list.stream()
                    .mapToDouble(e -> e.getPercentageTrained() != null ? e.getPercentageTrained() : 0.0)
                    .sum();
        }

        // Round to 2 decimal places
        total.setPercentageTrained(Math.round(percentage * 100.0) / 100.0);

        return total;
    }




    public List<CapacityBuilding> getCapacityBuildings() {
        if (buildings == null || buildings.isEmpty()) {
            return Collections.emptyList();
        }

        return buildings.stream()
                .filter(e -> e.getState() != null && e.getState().getName() != null)
                .sorted(Comparator.comparing(e -> e.getState().getName().toLowerCase()))
                .collect(Collectors.toList());
    }


    public List<Map<String, Object>> getCapacityTrainerData(Long stateId, String trainerType) {
        List<CapacityBuilding> list = getAllEntries();

        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        if (stateId != null && stateId > 0) {
            list = list.stream()
                    .filter(e -> e.getState() != null && stateId.equals(e.getState().getId()))
                    .collect(Collectors.toList());
        }

        return list.stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("stateName", e.getState() != null ? e.getState().getName() : "");
            map.put("nameOfCoE", e.getNameOfCoE() != null ? e.getNameOfCoE() : "");
            map.put("fieldTeamsTrained", e.getFieldTeamsTrained() != null ? String.format("%.0f", e.getFieldTeamsTrained()) : "0");
            map.put("percentageTrained", e.getPercentageTrained() != null ? String.format("%.2f", e.getPercentageTrained()) : "0.00");


            // Dynamically choose trainer type
            int trainerValue = 0;
            switch (trainerType) {
                case "nigst":
                    trainerValue = e.getNigstMasterTrainers() != null ? e.getNigstMasterTrainers() : 0;
                    map.put("nigstMasterTrainers", trainerValue);
                    break;
                case "coe":
                    trainerValue = e.getCoeMasterTrainers() != null ? e.getCoeMasterTrainers() : 0;
                    map.put("coeMasterTrainers", trainerValue);
                    break;
                case "total":
                    trainerValue = e.getTotalMasterTrainers() != null ? e.getTotalMasterTrainers() : 0;
                    map.put("totalMasterTrainers", trainerValue);
                    break;
                case "fieldTeams":
                    trainerValue = e.getFieldTeamsSanctioned() != null ? e.getFieldTeamsSanctioned() : 0;
                    map.put("fieldTeamsSanctioned", trainerValue);
                    break;
                case "trained":
                    trainerValue = e.getMembersTrained() != null ? e.getMembersTrained() : 0;
                    map.put("membersTrained", trainerValue);
                    break;
                case "toBeTrained":
                    trainerValue = e.getMembersToBeTrained() != null ? e.getMembersToBeTrained() : 0;
                    map.put("membersToBeTrained", trainerValue);
                    break;
                case "toPercentageTrainedData":
                    trainerValue = e.getMembersTrained() != null ? e.getMembersTrained() : 0;
                    map.put("membersTrained", trainerValue);
                    trainerValue = e.getMembersToBeTrained() != null ? e.getMembersToBeTrained() : 0;
                    map.put("membersToBeTrained", trainerValue);
                    break;

                default:
                    map.put("error", "Invalid trainer type");
            }

            return map;
        }).collect(Collectors.toList());
    }
    public List<Map<String, Object>> getNigstData(Long stateId) {
        return getCapacityTrainerData(stateId, "nigst");
    }

    public List<Map<String, Object>> getCoeData(Long stateId) {
        return getCapacityTrainerData(stateId, "coe");
    }

    public List<Map<String, Object>> getTotalMasterTrainerData(Long stateId) {
        return getCapacityTrainerData(stateId, "total");
    }

    public List<Map<String, Object>> getFieldTeamsData(Long stateId) {
        return getCapacityTrainerData(stateId, "fieldTeams");
    }

    public List<Map<String, Object>> getTrainedData(Long stateId) {
        return getCapacityTrainerData(stateId, "trained");
    }

    public List<Map<String, Object>> getToBeTrainedData(Long stateId) {
        return getCapacityTrainerData(stateId, "toBeTrained");
    }

    public List<Map<String, Object>> getToBePercentageTrainedData(Long stateId) {
        return getCapacityTrainerData(stateId, "toPercentageTrainedData");
    }


    public List<Map<String, Object>> getStateWiseSummary() {
        List<CapacityBuilding> list = getAllEntries();

        if (list == null || list.isEmpty()) return List.of();

        return list.stream()
                .filter(e -> e.getState() != null)
                .collect(Collectors.groupingBy(e -> e.getState().getName()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // ✅ Sort by state name ascending
                .map(entry -> {
                    String stateName = entry.getKey();
                    List<CapacityBuilding> entries = entry.getValue();

                    Map<String, Object> map = new HashMap<>();
                    map.put("state", stateName);
                    map.put("nigst", entries.stream().mapToInt(e -> e.getNigstMasterTrainers() != null ? e.getNigstMasterTrainers() : 0).sum());
                    map.put("coe", entries.stream().mapToInt(e -> e.getCoeMasterTrainers() != null ? e.getCoeMasterTrainers() : 0).sum());
                    map.put("total", entries.stream().mapToInt(e -> e.getTotalMasterTrainers() != null ? e.getTotalMasterTrainers() : 0).sum());
                    map.put("teams", entries.stream().mapToInt(e -> e.getFieldTeamsSanctioned() != null ? e.getFieldTeamsSanctioned() : 0).sum());
                    map.put("trained", entries.stream().mapToInt(e -> e.getMembersTrained() != null ? e.getMembersTrained() : 0).sum());
                    map.put("toBeTrained", entries.stream().mapToInt(e -> e.getMembersToBeTrained() != null ? e.getMembersToBeTrained() : 0).sum());

                    double avgFieldTeamsTrained = entries.stream()
                            .filter(e -> e.getFieldTeamsTrained() != null)
                            .mapToDouble(CapacityBuilding::getFieldTeamsTrained)
                            .average()
                            .orElse(0.0);

                    double avgPercentageTrained = entries.stream()
                            .filter(e -> e.getPercentageTrained() != null)
                            .mapToDouble(CapacityBuilding::getPercentageTrained)
                            .average()
                            .orElse(0.0);

                    map.put("fieldTeamsTrained", String.format("%.2f", avgFieldTeamsTrained));
                    map.put("percentageTrained", String.format("%.2f", avgPercentageTrained));

                    return map;
                })
                .collect(Collectors.toList());
    }


    public List<Map<String, Object>> getCoEWithStateWiseData() {
        List<CapacityBuilding> list = getAllEntries();

        if (list == null || list.isEmpty()) return List.of();

        return list.stream()
                .filter(e -> e.getNameOfCoE() != null && e.getState() != null)
                .collect(Collectors.groupingBy(CapacityBuilding::getNameOfCoE))
                .entrySet().stream()
                .map(entry -> {
                    String coeName = entry.getKey();
                    List<CapacityBuilding> coeEntries = entry.getValue();

                    // Create list of state-wise data
                    List<Map<String, Object>> stateData = coeEntries.stream()
                            .map(e -> {
                                Map<String, Object> stateMap = new HashMap<>();
                                stateMap.put("state", e.getState().getName());
                                stateMap.put("fieldTeamsSanctioned", e.getFieldTeamsSanctioned() != null ? e.getFieldTeamsSanctioned() : 0);
                                stateMap.put("fieldTeamsTrained",
                                        String.format("%.0f", e.getFieldTeamsTrained() != null ? e.getFieldTeamsTrained() : 0)
                                );
                                stateMap.put("masterTrainers", e.getTotalMasterTrainers() != null ? e.getTotalMasterTrainers() : 0);
                                return stateMap;
                            })
                            .collect(Collectors.toList());

                    Map<String, Object> coeMap = new HashMap<>();
                    coeMap.put("nameOfCoE", coeName);
                    coeMap.put("data", stateData);
                    return coeMap;
                })
                .collect(Collectors.toList());
    }
    public List<String> getUniqueNameOfCoEs() {
        List<CapacityBuilding> list = getAllEntries();

        if (list == null || list.isEmpty()) return List.of();

        return list.stream()
                .map(CapacityBuilding::getNameOfCoE)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }



}
