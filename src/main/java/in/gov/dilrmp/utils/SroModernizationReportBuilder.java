package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.sroModernization.SroModernizationReport;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.IgrMISDataEntryRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * //v5 Build Modernization of SRO report rows from existing SRO view geo/metrics (A)
 * plus live IGR form aggregates for B/C/D. Form updates reflect on next report load.
 * DoLR sanctioned (C) is stored/shown but has no IGR form entry yet.
 */
@Component
public class SroModernizationReportBuilder {

    private final IgrMISDataEntryRepository igrMISDataEntryRepository;

    public SroModernizationReportBuilder(IgrMISDataEntryRepository igrMISDataEntryRepository) {
        this.igrMISDataEntryRepository = igrMISDataEntryRepository;
    }

    public SroModernizationReport fromSroView(SroReportDTO sro) {
        SroModernizationReport report = new SroModernizationReport();
        report.setStateId(sro.getStateId());
        report.setStateName(sro.getStateName());
        report.setLgdCode(sro.getLgdCode());
        report.setTotalDistricts(sro.getTotalDistricts());
        report.setTotalSros(sro.getNumberOfSROsInState());
        report.setSrosUsingOnlineRegistration(sro.getNumberOfSROsUsingOnlineRegistration());
        return report;
    }

    public void enrichStateReports(List<SroModernizationReport> reports) {
        Map<Long, int[]> byState = toMetricMap(igrMISDataEntryRepository.sumSroModernizationByStateId());
        int[] national = firstNational(igrMISDataEntryRepository.sumSroModernizationNational());
        reports.forEach(report -> {
            if (isNational(report)) {
                applyIgrMetrics(report, national);
            } else {
                int[] metrics = resolveStateMetrics(report, byState);
                if (metrics != null) {
                    applyIgrMetrics(report, metrics);
                } else {
                    applyModernizationOnly(report, 0, 0, 0);
                }
            }
            formatState(report);
        });
    }

    /** When IGR row exists: refresh A + total SROs + B/C/D from IGR. */
    private void applyIgrMetrics(SroModernizationReport report, int[] m) {
        report.setSrosUsingOnlineRegistration(m[0]);
        report.setTotalSros(m[4]);
        applyModernizationOnly(report, m[1], m[2], m[3]);
    }

    private void applyModernizationOnly(SroModernizationReport report, int b, int c, int d) {
        int a = nvl(report.getSrosUsingOnlineRegistration());
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

    private static BigDecimal percent(int numerator, int denominator) {
        if (denominator <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(numerator * 100.0 / denominator).setScale(2, RoundingMode.HALF_UP);
    }

    private static int nvl(Integer v) {
        return v == null ? 0 : v;
    }

    private static int[] zeros() {
        return new int[]{0, 0, 0, 0, 0};
    }

    private static int[] firstNational(List<Object[]> rows) {
        if (rows == null || rows.isEmpty() || rows.get(0) == null) {
            return zeros();
        }
        Object[] row = rows.get(0);
        return new int[]{toInt(row, 0), toInt(row, 1), toInt(row, 2), toInt(row, 3), toInt(row, 4)};
    }

    private static Map<Long, int[]> toMetricMap(List<Object[]> rows) {
        Map<Long, int[]> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 6 || row[0] == null) {
                continue;
            }
            // [0]=state_id, [1]=onlineA, [2]=B, [3]=C, [4]=D, [5]=totalSros
            map.put(((Number) row[0]).longValue(), new int[]{
                    toInt(row, 1), toInt(row, 2), toInt(row, 3), toInt(row, 4), toInt(row, 5)
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
