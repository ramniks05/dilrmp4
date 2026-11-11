package in.gov.dilrmp.api.prayas;

import in.gov.dilrmp.api.common.ExternalShareApiSupport;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.services.physicalProgressServices.MapDigitizationReportService;
import in.gov.dilrmp.services.physicalProgressServices.StateCLRService;
import in.gov.dilrmp.services.physicalProgressServices.StateSROService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Single PRAYAS API with the five document indicators.
 * One flat object per State/UT; field names match physical progress reports.
 */
@Service
public class PrayasPortalApiService {

    @Autowired
    private StateCLRService stateCLRService;
    @Autowired
    private MapDigitizationReportService mapDigitizationReportService;
    @Autowired
    private StateSROService stateSROService;

    public Map<String, Object> allStates() {
        Map<String, Object> body = ExternalShareApiSupport.base("PRAYAS",
                "Indicators shared with PRAYAS Portal");
        body.put("indicators", Arrays.asList(
                "I. CLR Completed Villages",
                "II. Digitized Map Sheets",
                "III. Cadastral Maps Linked with RoR",
                "IV. Computerized SROs",
                "V. SRO Integrated with Land Records"
        ));

        Map<Integer, MapDigitizationReport> mapByLgd = byLgd(mapDigitizationReportService.getAllMapDigitizationReport());
        Map<Long, SroReportDTO> sroByState = byStateId(stateSROService.getSroStateViwe());

        List<Map<String, Object>> rows = new ArrayList<>();
        for (StateClrReportView clr : stateCLRService.getClrStateViwe()) {
            if (clr.getStateId() == null || ExternalShareApiSupport.isNationalStateId(clr.getStateId())) {
                continue;
            }
            long stateId = clr.getStateId();
            Integer lgdCode = clr.getLgdCode();
            MapDigitizationReport map = lgdCode == null ? null : mapByLgd.get(lgdCode);
            if (map == null) {
                map = mapByLgd.get((int) stateId);
            }
            rows.add(row(clr, map, sroByState.get(stateId), false));
        }

        body.put("national", national());
        body.put("totalStates", rows.size());
        body.put("states", rows);
        return body;
    }

    private Map<String, Object> national() {
        return row(
                first(stateCLRService.getStateClrReportsGrandToatal()),
                first(mapDigitizationReportService.getStateMapReportsGrandToatal()),
                first(stateSROService.getStateSroReportsGrandToatal()),
                true);
    }

    private static Map<String, Object> row(StateClrReportView clr, MapDigitizationReport map,
                                          SroReportDTO sro, boolean national) {
        Map<String, Object> row = new LinkedHashMap<>();
        if (national) {
            row.put("stateName", "All India");
        } else {
            row.put("lgdCode", clr == null ? null : clr.getLgdCode());
            row.put("stateName", clr == null || clr.getStateName() == null ? "" : clr.getStateName());
        }

        // I. CLR Completed Villages
        row.put("totalVillages", ExternalShareApiSupport.intValue(clr == null ? null : clr.getTotalVillages()));
        row.put("villagesClrCompleted",
                ExternalShareApiSupport.intValue(clr == null ? null : clr.getVillagesClrCompleted()));
        row.put("clrCompletionPercent",
                ExternalShareApiSupport.percent(clr == null ? null : clr.getClrCompletionPercent()));

        // II. Digitized Map Sheets
        row.put("totalMapsFmbTippans",
                ExternalShareApiSupport.intValue(map == null ? null : map.getTotalMapsFmbTippans()));
        row.put("totalDigitizedMapsFmbTippans",
                ExternalShareApiSupport.intValue(map == null ? null : map.getTotalDigitizedMapsFmbTippans()));
        row.put("digitizedMapsFmbTippansPercent",
                ExternalShareApiSupport.percent(map == null ? null : map.getDigitizedMapsFmbTippansPercent()));

        // III. Cadastral Maps Linked with RoR
        row.put("villagesLinkedWithRor",
                ExternalShareApiSupport.intValue(map == null ? null : map.getVillagesLinkedWithRor()));
        row.put("villagesLinkedWithRorPercent",
                ExternalShareApiSupport.percent(map == null ? null : map.getVillagesLinkedWithRorPercent()));

        // IV. Computerized SROs
        int sroTotal = ExternalShareApiSupport.intValue(sro == null ? null : sro.getNumberOfSROsInState());
        row.put("numberOfSROsInState", sroTotal);
        row.put("numberOfSROsUsingOnlineRegistration",
                ExternalShareApiSupport.intValue(sro == null ? null : sro.getNumberOfSROsUsingOnlineRegistration()));
        row.put("onlineRegistrationPercent",
                ExternalShareApiSupport.percent(sro == null ? null : sro.getOnlineRegistrationPercent()));

        // V. SRO Integrated with Land Records
        int integrated = ExternalShareApiSupport.intValue(
                sro == null ? null : sro.getNumberOfSROsIntegratedWithLandRecords());
        row.put("numberOfSROsIntegratedWithLandRecords", integrated);
        row.put("srosIntegratedWithLandRecordsPercent",
                ExternalShareApiSupport.percent(integrated, sroTotal));

        return row;
    }

    private static Map<Integer, MapDigitizationReport> byLgd(List<MapDigitizationReport> list) {
        Map<Integer, MapDigitizationReport> map = new LinkedHashMap<>();
        if (list == null) {
            return map;
        }
        for (MapDigitizationReport item : list) {
            if (item.getLgdCode() != null && !ExternalShareApiSupport.isNationalLgd(item.getLgdCode())) {
                map.put(item.getLgdCode(), item);
            }
        }
        return map;
    }

    private static Map<Long, SroReportDTO> byStateId(List<SroReportDTO> list) {
        Map<Long, SroReportDTO> map = new LinkedHashMap<>();
        if (list == null) {
            return map;
        }
        for (SroReportDTO item : list) {
            if (item.getStateId() != null && !ExternalShareApiSupport.isNationalStateId(item.getStateId())) {
                map.put(item.getStateId(), item);
            }
        }
        return map;
    }

    private static <T> T first(List<T> list) {
        return list == null || list.isEmpty() ? null : list.get(0);
    }
}
