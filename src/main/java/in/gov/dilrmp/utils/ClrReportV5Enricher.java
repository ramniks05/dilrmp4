package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.reportDTO.clr.DistrictClrReportView;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DistrictMISDataEntryRepository;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.StateMISDataEntryRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * //v5 CLR: RoR with Cadastral Map [6.1]; Auto-Mutation Facility Available [11].
 */
@Component
public class ClrReportV5Enricher {

    private final DistrictMISDataEntryRepository districtMISDataEntryRepository;
    private final StateMISDataEntryRepository stateMISDataEntryRepository;

    public ClrReportV5Enricher(DistrictMISDataEntryRepository districtMISDataEntryRepository,
                               StateMISDataEntryRepository stateMISDataEntryRepository) {
        this.districtMISDataEntryRepository = districtMISDataEntryRepository;
        this.stateMISDataEntryRepository = stateMISDataEntryRepository;
    }

    public void enrichStateReports(List<StateClrReportView> reports) {
        Map<Long, Integer> byStateId = toLongMap(districtMISDataEntryRepository.sumRorWithCadastralMapByStateId());
        Map<Long, String> autoMutationByState = toYesNoByStateMap(stateMISDataEntryRepository.findAutoMutationFacilityByStateId());
        String nationalAutoMutationCount = String.valueOf(
                nullSafeLong(stateMISDataEntryRepository.countAutoMutationFacilityYesNational()));
        reports.forEach(report -> {
            enrichStateReport(report, byStateId);
            enrichAutoMutationFacility(report, autoMutationByState, nationalAutoMutationCount);
        });
    }

    public void enrichDistrictReports(List<DistrictClrReportView> reports, Long stateId) {
        Map<Long, Integer> byDistrictId = toLongMap(
                districtMISDataEntryRepository.sumRorWithCadastralMapByDistrictForStateId(stateId));
        var stateMis = stateMISDataEntryRepository.findByState_Id(stateId);
        String autoMutation = yesNoFromBoolean(stateMis != null ? stateMis.getAutoMutationFacility() : null);
        reports.forEach(report -> {
            int withMap = byDistrictId.getOrDefault(report.getDistrictId(), 0);
            applyV5RorMetrics(report, withMap);
            report.setAutoMutationFacility(autoMutation);
        });
    }

    public void enrichStateReportListForDistrictPage(List<StateClrReportView> reports, Long stateId) {
        Map<Long, Integer> byStateId = toLongMap(districtMISDataEntryRepository.sumRorWithCadastralMapByStateId());
        Map<Long, String> autoMutationByState = toYesNoByStateMap(stateMISDataEntryRepository.findAutoMutationFacilityByStateId());
        reports.forEach(report -> {
            int withMap = byStateId.getOrDefault(stateId, 0);
            applyV5RorMetrics(report, withMap);
            report.setAutoMutationFacility(autoMutationByState.getOrDefault(stateId, "NO"));
        });
    }

    private void enrichStateReport(StateClrReportView report, Map<Long, Integer> byStateId) {
        int withMap = 0;
        if (report.getStateId() != null && report.getStateId().equals(999L)) {
            withMap = nullSafeInt(districtMISDataEntryRepository.sumRorWithCadastralMapNational());
        } else if (report.getLgdCode() != null && report.getLgdCode().equals(999)) {
            withMap = nullSafeInt(districtMISDataEntryRepository.sumRorWithCadastralMapNational());
        } else if (report.getStateId() != null) {
            withMap = byStateId.getOrDefault(report.getStateId(), 0);
        }
        applyV5RorMetrics(report, withMap);
    }

    private void enrichAutoMutationFacility(StateClrReportView report, Map<Long, String> byStateId,
                                            String nationalCount) {
        if (report.getStateId() != null && report.getStateId().equals(999L)) {
            report.setAutoMutationFacility(nationalCount);
        } else if (report.getLgdCode() != null && report.getLgdCode().equals(999)) {
            report.setAutoMutationFacility(nationalCount);
        } else if (report.getStateId() != null) {
            report.setAutoMutationFacility(byStateId.getOrDefault(report.getStateId(), "NO"));
        } else {
            report.setAutoMutationFacility("NO");
        }
    }

    private void applyV5RorMetrics(StateClrReportView report, int withCadastralMap) {
        int totalRor = nullSafeInt(report.getTotalRor());
        report.setRorWithCadastralMap(withCadastralMap);
        report.setFormattingRorWithCadastralMap(NumberFormatterUtil.formatWithCommas(withCadastralMap));
        report.setRorWithCadastralMapPercent(calculatePercent(withCadastralMap, totalRor));
    }

    private void applyV5RorMetrics(DistrictClrReportView report, int withCadastralMap) {
        int totalRor = nullSafeInt(report.getTotalRor());
        report.setRorWithCadastralMap(withCadastralMap);
        report.setRorWithCadastralMapPercent(calculatePercent(withCadastralMap, totalRor));
    }

    private static BigDecimal calculatePercent(int numerator, int denominator) {
        if (denominator <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(numerator * 100.0 / denominator).setScale(2, RoundingMode.HALF_UP);
    }

    private static int nullSafeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private static long nullSafeLong(Long value) {
        return value == null ? 0L : value;
    }

    private static String yesNoFromBoolean(Boolean value) {
        return Boolean.TRUE.equals(value) ? "YES" : "NO";
    }

    private static Map<Long, String> toYesNoByStateMap(List<Object[]> rows) {
        Map<Long, String> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 2 || row[0] == null) {
                continue;
            }
            long key = ((Number) row[0]).longValue();
            Boolean yes = row[1] instanceof Boolean ? (Boolean) row[1] : null;
            map.put(key, yesNoFromBoolean(yes));
        }
        return map;
    }

    private static Map<Long, Integer> toLongMap(List<Object[]> rows) {
        Map<Long, Integer> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 2 || row[0] == null) {
                continue;
            }
            long key = ((Number) row[0]).longValue();
            int sum = row[1] == null ? 0 : ((Number) row[1]).intValue();
            map.put(key, sum);
        }
        return map;
    }
}
