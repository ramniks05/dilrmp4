package in.gov.dilrmp.services.naksha;

import in.gov.dilrmp.models.naksha.NakshaMISDataEntrySolrAndVender;
import in.gov.dilrmp.models.naksha.ULBMaster;
import in.gov.dilrmp.repositories.naksha.ULBMsterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ULBMasterService {

    @Autowired
    private ULBMsterRepository ulbMasterRepository;


    private List<ULBMaster> vendor;

    public void loadVendorData() {
        vendor = ulbMasterRepository.findAll();
    }

    public List<ULBMaster> getAllEntries() {
        return vendor;
    }


    // ✅ Get distinct state names from vendor list
    public List<String> getAllStateNames() {
        if (vendor == null || vendor.isEmpty()) {
            loadVendorData(); // ensure data is loaded
        }

        return vendor.stream()
                .map(ULBMaster::getState_name)  // extract state name
                .filter(Objects::nonNull)       // remove nulls
                .distinct()                     // remove duplicates
                .sorted()                       // optional: sort alphabetically
                .collect(Collectors.toList());
    }

    // ✅ Get distinct state names from vendor list
    public List<Map<String, String>> getAllUlbWithState() {
        if (vendor == null || vendor.isEmpty()) {
            loadVendorData();
        }

        return vendor.stream()
                .filter(v -> v.getUlb_name() != null && v.getState_name() != null)
                .sorted(Comparator.comparing(ULBMaster::getUlb_name)) // sort by ULB name
                .map(v -> {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("ulbName", v.getUlb_name());   // first ULB
                    map.put("stateName", v.getState_name()); // then State
                    map.put("population", String.valueOf(
                            v.getPopulation() != null ? v.getPopulation() : 0));
                    map.put("fieldSurveyArea", String.format("%.2f", v.getFieldSurveyArea() != null ? v.getFieldSurveyArea().doubleValue() : 0.0));
                    map.put("aerialSurveyArea", String.format("%.2f", v.getAerialSurveyArea() != null ? v.getAerialSurveyArea().doubleValue() : 0.0));
                    map.put("bufferGridAreaForFlying", String.format("%.2f", v.getBufferGridAreaForFlying() != null ? v.getBufferGridAreaForFlying().doubleValue() : 0.0));

                    return map;
                })
                .collect(Collectors.toList());
    }





    public List<Object[]> getAllStateIdAndName() {
        return ulbMasterRepository.findStateIdAndStateName();
    }

    public List<Map<String, Object>> getULBCountByGD() {
        List<Object[]> rawData = ulbMasterRepository.getULCountByGD();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rawData) {
            Map<String, Object> map = new HashMap<>();
            map.put("gdId", row[0]);
            map.put("gdName", row[1]);
            map.put("ulbCount", row[2]);
            result.add(map);
        }

        return result;
    }


    public List<Map<String, String>> getGroupedULBsByGdId(Long gdId) {
        try {
            List<Object[]> results = ulbMasterRepository.getGroupedULBsByGdId(gdId);
            List<Map<String, String>> formatted = new ArrayList<>();

            for (Object[] row : results) {
                Map<String, String> map = new HashMap<>();
                map.put("gdName", String.valueOf(row[0]));
                map.put("stateName", String.valueOf(row[1]));
                map.put("ulbNames", String.valueOf(row[2]));
                formatted.add(map);
            }

            return formatted;

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Map<String, Object>> getGdNames() {

        List<Object[]> rawData = ulbMasterRepository.getULCountByGD();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rawData) {
            Map<String, Object> map = new HashMap<>();
            map.put("gdName", row[1]);
           result.add(map);
        }
        result.sort(Comparator.comparing(m -> m.get("gdName").toString()));
        return result;
    }




}
