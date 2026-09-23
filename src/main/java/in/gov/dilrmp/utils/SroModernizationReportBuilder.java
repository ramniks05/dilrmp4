package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.sroModernization.DistrictSroModernizationReport;
import in.gov.dilrmp.models.reportDTO.sroModernization.SroModernizationReport;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DistrictMISDataEntryRepository;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DolrDistrictMisDataEntryRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SRO modernization report:
 * District MIS = total SROs, computerized (A), modernized State/PPP (B), modernized DILRMP (D).
 * DoLR district entry = DILRMP sanctioned (C) only.
 */
@Component
public class SroModernizationReportBuilder {

    private final DistrictMISDataEntryRepository districtMISDataEntryRepository;
    private final DolrDistrictMisDataEntryRepository dolrDistrictMisDataEntryRepository;

    public SroModernizationReportBuilder(DistrictMISDataEntryRepository districtMISDataEntryRepository,
                                         DolrDistrictMisDataEntryRepository dolrDistrictMisDataEntryRepository) {
        this.districtMISDataEntryRepository = districtMISDataEntryRepository;
        this.dolrDistrictMisDataEntryRepository = dolrDistrictMisDataEntryRepository;
    }

    public SroModernizationReport fromSroView(SroReportDTO sro) {
        SroModernizationReport report = new SroModernizationReport();
        report.setStateId(sro.getStateId());
        report.setStateName(sro.getStateName());
        report.setLgdCode(sro.getLgdCode());
        report.setTotalDistricts(sro.getTotalDistricts());
        report.setTotalSros(0);
        report.setSrosUsingOnlineRegistration(0);
        return report;
    }

    public DistrictSroModernizationReport fromMrrDistrict(DistrictMrrViewReport mrr) {
        DistrictSroModernizationReport report = new DistrictSroModernizationReport();
        report.setDistrictId(mrr.getDistrictId());
        report.setDistrictName(mrr.getDistrictName());
        report.setStateId(mrr.getStateId());
        report.setStateName(mrr.getStateName());
        report.setTotalTehsils(mrr.getTotalTehsils());
        return report;
    }

    public void enrichStateReports(List<SroModernizationReport> reports) {
        Map<Long, int[]> misByState = toMisStateMap(districtMISDataEntryRepository.sumSroModernizationByStateId());
        Map<Long, Integer> dolrCByState = dolrSanctionMap(dolrDistrictMisDataEntryRepository.sumSroSanctionedByStateId());
        int[] nationalMis = firstMisNational(districtMISDataEntryRepository.sumSroModernizationNational());
        int nationalC = dolrSanctionNational();
        reports.forEach(report -> {
            int[] metrics;
            if (isNational(report)) {
                metrics = merge(nationalMis, nationalC);
            } else {
                int[] mis = resolveStateMetrics(report, misByState);
                Integer c = resolveDolrSanction(report, dolrCByState);
                metrics = merge(mis != null ? mis : misZeros(), c != null ? c : 0);
            }
            applyMetrics(report, metrics);
            formatState(report);
        });
    }

    public void enrichDistrictReports(List<DistrictSroModernizationReport> reports, Long stateId) {
        Map<Long, int[]> misByDistrict = toMisDistrictMap(
                districtMISDataEntryRepository.findSroModernizationByStateId(stateId));
        Map<Long, Integer> dolrCByDistrict = dolrSanctionMap(
                dolrDistrictMisDataEntryRepository.findSroSanctionedByStateId(stateId));
        reports.forEach(report -> {
            int[] mis = misByDistrict.getOrDefault(report.getDistrictId(), misZeros());
            int c = dolrCByDistrict.getOrDefault(report.getDistrictId(), 0);
            applyDistrictMetrics(report, merge(mis, c));
        });
    }

    public void enrichStateTotalsForDistrictPage(List<SroModernizationReport> reports, Long stateId) {
        Map<Long, int[]> misByState = toMisStateMap(districtMISDataEntryRepository.sumSroModernizationByStateId());
        Map<Long, Integer> dolrCByState = dolrSanctionMap(dolrDistrictMisDataEntryRepository.sumSroSanctionedByStateId());
        reports.forEach(report -> {
            int[] mis = misByState.getOrDefault(stateId, misZeros());
            if (isAllZero(mis) && report.getLgdCode() != null) {
                mis = misByState.getOrDefault(report.getLgdCode().longValue(), mis);
            }
            Integer c = resolveDolrSanction(report, dolrCByState);
            if (c == null && stateId != null) {
                c = dolrCByState.get(stateId);
            }
            applyMetrics(report, merge(mis, c != null ? c : 0));
            formatState(report);
        });
    }

    /** m: [totalSros, onlineA, B, C, D] */
    private void applyMetrics(SroModernizationReport report, int[] m) {
        report.setTotalSros(m[0]);
        report.setSrosUsingOnlineRegistration(m[1]);
        int a = m[1];
        int b = m[2];
        int c = m[3];
        int d = m[4];
        report.setSrosModernisedStateFunds(b);
        report.setSrosModernisedStateFundsPercent(percent(b, a));
        report.setSrosDilrmpSanctioned(c);
        report.setSrosModernisedDilrmpFunds(d);
        report.setSrosModernisedDilrmpFundsPercent(percent(d, c));
        int total = b + d;
        report.setSrosModernisedTotal(total);
        report.setSrosModernisedTotalPercent(percent(total, a));
    }

    private void applyDistrictMetrics(DistrictSroModernizationReport report, int[] m) {
        report.setTotalSros(m[0]);
        report.setSrosUsingOnlineRegistration(m[1]);
        int a = m[1];
        int b = m[2];
        int c = m[3];
        int d = m[4];
        report.setSrosModernisedStateFunds(b);
        report.setSrosModernisedStateFundsPercent(percent(b, a));
        report.setSrosDilrmpSanctioned(c);
        report.setSrosModernisedDilrmpFunds(d);
        report.setSrosModernisedDilrmpFundsPercent(percent(d, c));
        int total = b + d;
        report.setSrosModernisedTotal(total);
        report.setSrosModernisedTotalPercent(percent(total, a));
    }

    private void formatState(SroModernizationReport report) {
        report.setFormattingTotalDistricts(NumberFormatterUtil.formatWithCommas(report.getTotalDistricts()));
        report.setFormattingTotalSros(NumberFormatterUtil.formatWithCommas(report.getTotalSros()));
        report.setFormattingSrosUsingOnlineRegistration(
                NumberFormatterUtil.formatWithCommas(report.getSrosUsingOnlineRegistration()));
        report.setFormattingSrosModernisedStateFunds(
                NumberFormatterUtil.formatWithCommas(report.getSrosModernisedStateFunds()));
        report.setFormattingSrosDilrmpSanctioned(
                NumberFormatterUtil.formatWithCommas(report.getSrosDilrmpSanctioned()));
        report.setFormattingSrosModernisedDilrmpFunds(
                NumberFormatterUtil.formatWithCommas(report.getSrosModernisedDilrmpFunds()));
        report.setFormattingSrosModernisedTotal(
                NumberFormatterUtil.formatWithCommas(report.getSrosModernisedTotal()));
    }

    /** MIS row → [total, A, B, D]; merge with DoLR C → [total, A, B, C, D] */
    private static int[] merge(int[] mis, int c) {
        return new int[]{mis[0], mis[1], mis[2], c, mis[3]};
    }

    private int dolrSanctionNational() {
        Integer n = dolrDistrictMisDataEntryRepository.sumSroSanctionedNational();
        return n != null ? n : 0;
    }

    private static Map<Long, Integer> dolrSanctionMap(List<Object[]> rows) {
        Map<Long, Integer> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 2 || row[0] == null) {
                continue;
            }
            map.put(((Number) row[0]).longValue(), toInt(row, 1));
        }
        return map;
    }

    private static Integer resolveDolrSanction(SroModernizationReport report, Map<Long, Integer> byState) {
        if (report.getStateId() != null && byState.containsKey(report.getStateId())) {
            return byState.get(report.getStateId());
        }
        if (report.getLgdCode() != null && byState.containsKey(report.getLgdCode().longValue())) {
            return byState.get(report.getLgdCode().longValue());
        }
        return null;
    }

    private static int[] resolveStateMetrics(SroModernizationReport report, Map<Long, int[]> byState) {
        if (report.getStateId() != null && byState.containsKey(report.getStateId())) {
            return byState.get(report.getStateId());
        }
        if (report.getLgdCode() != null && byState.containsKey(report.getLgdCode().longValue())) {
            return byState.get(report.getLgdCode().longValue());
        }
        return null;
    }

    private static boolean isNational(SroModernizationReport report) {
        return (report.getStateId() != null && report.getStateId().equals(999L))
                || (report.getLgdCode() != null && report.getLgdCode().equals(999));
    }

    private static boolean isAllZero(int[] m) {
        for (int v : m) {
            if (v != 0) {
                return false;
            }
        }
        return true;
    }

    private static BigDecimal percent(int numerator, int denominator) {
        if (denominator <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(numerator * 100.0 / denominator).setScale(2, RoundingMode.HALF_UP);
    }

    private static int[] misZeros() {
        return new int[]{0, 0, 0, 0};
    }

    private static int[] firstMisNational(List<Object[]> rows) {
        if (rows == null || rows.isEmpty() || rows.get(0) == null) {
            return misZeros();
        }
        Object[] row = rows.get(0);
        return new int[]{toInt(row, 0), toInt(row, 1), toInt(row, 2), toInt(row, 3)};
    }

    /** [0]=stateId, [1]=total, [2]=A, [3]=B, [4]=D */
    private static Map<Long, int[]> toMisStateMap(List<Object[]> rows) {
        Map<Long, int[]> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 5 || row[0] == null) {
                continue;
            }
            map.put(((Number) row[0]).longValue(), new int[]{
                    toInt(row, 1), toInt(row, 2), toInt(row, 3), toInt(row, 4)
            });
        }
        return map;
    }

    /** [0]=districtId, [1]=total, [2]=A, [3]=B, [4]=D */
    private static Map<Long, int[]> toMisDistrictMap(List<Object[]> rows) {
        Map<Long, int[]> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 5 || row[0] == null) {
                continue;
            }
            map.put(((Number) row[0]).longValue(), new int[]{
                    toInt(row, 1), toInt(row, 2), toInt(row, 3), toInt(row, 4)
            });
        }
        return map;
    }

    private static int toInt(Object[] row, int index) {
        if (row == null || index >= row.length || row[index] == null) {
            return 0;
        }
        return ((Number) row[index]).intValue();
    }
}
