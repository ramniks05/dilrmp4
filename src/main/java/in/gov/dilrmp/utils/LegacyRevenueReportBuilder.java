package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.reportDTO.legacyRevenue.DistrictLegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.LegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DistrictMISDataEntryRepository;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DolrDistrictMisDataEntryRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Build Legacy Revenue Records Digitisation rows from MRR geography
 * plus district MIS aggregates. DoLR sanctioned pages are entered per district and summed for the state total.
 * Total digitised pages = State/UT funds + DILRMP completed.
 */
@Component
public class LegacyRevenueReportBuilder {

    private final DistrictMISDataEntryRepository districtMISDataEntryRepository;
    private final DolrDistrictMisDataEntryRepository dolrDistrictMisDataEntryRepository;

    public LegacyRevenueReportBuilder(DistrictMISDataEntryRepository districtMISDataEntryRepository,
                                      DolrDistrictMisDataEntryRepository dolrDistrictMisDataEntryRepository) {
        this.districtMISDataEntryRepository = districtMISDataEntryRepository;
        this.dolrDistrictMisDataEntryRepository = dolrDistrictMisDataEntryRepository;
    }

    public LegacyRevenueReport fromMrrState(MrrViewReport mrr) {
        LegacyRevenueReport report = new LegacyRevenueReport();
        report.setStateId(mrr.getStateId());
        report.setStateName(mrr.getStateName());
        report.setLgdCode(mrr.getLgdCode());
        report.setTotalDistrict(mrr.getTotalDistrict());
        report.setTotalTehsils(mrr.getTotalTehsils());
        return report;
    }

    public DistrictLegacyRevenueReport fromMrrDistrict(DistrictMrrViewReport mrr) {
        DistrictLegacyRevenueReport report = new DistrictLegacyRevenueReport();
        report.setDistrictId(mrr.getDistrictId());
        report.setDistrictName(mrr.getDistrictName());
        report.setStateId(mrr.getStateId());
        report.setStateName(mrr.getStateName());
        report.setTotalTehsils(mrr.getTotalTehsils());
        return report;
    }

    public void enrichStateReports(List<LegacyRevenueReport> reports) {
        Map<Long, int[]> byState = toMetricMap(districtMISDataEntryRepository.sumLegacyRevenueByStateId());
        Map<Long, Integer> dolrSanctionByState = dolrSanctionMap();
        int[] national = firstNational(districtMISDataEntryRepository.sumLegacyRevenueNational());
        int nationalDolrSanction = dolrSanctionNational();
        reports.forEach(report -> {
            int[] metrics;
            if (isNational(report)) {
                metrics = national.clone();
                metrics[2] = nationalDolrSanction;
            } else {
                metrics = resolveStateMetrics(report, byState).clone();
                metrics[2] = sanctionOrZero(resolveDolrSanction(report, dolrSanctionByState));
            }
            applyMetrics(report, metrics);
            formatState(report);
        });
    }

    public void enrichDistrictReports(List<DistrictLegacyRevenueReport> reports, Long stateId) {
        Map<Long, int[]> byDistrict = toMetricMap(
                districtMISDataEntryRepository.sumLegacyRevenueByDistrictForStateId(stateId));
        Map<Long, Integer> sanctions = districtSanctionMap(stateId);
        reports.forEach(report -> {
            int[] metrics = byDistrict.getOrDefault(report.getDistrictId(), zeros()).clone();
            metrics[2] = sanctions.getOrDefault(report.getDistrictId(), 0);
            applyMetrics(report, metrics);
        });
    }

    public void enrichStateTotalsForDistrictPage(List<LegacyRevenueReport> reports, Long stateId) {
        Map<Long, int[]> byState = toMetricMap(districtMISDataEntryRepository.sumLegacyRevenueByStateId());
        Map<Long, Integer> dolrSanctionByState = dolrSanctionMap();
        reports.forEach(report -> {
            int[] metrics = byState.getOrDefault(stateId, zeros()).clone();
            if (isAllZero(metrics) && report.getLgdCode() != null) {
                metrics = byState.getOrDefault(report.getLgdCode().longValue(), metrics).clone();
            }
            Integer dolrC = resolveDolrSanction(report, dolrSanctionByState);
            if (dolrC == null && stateId != null) {
                dolrC = dolrSanctionByState.get(stateId);
            }
            metrics[2] = sanctionOrZero(dolrC);
            applyMetrics(report, metrics);
            formatState(report);
        });
    }

    private Map<Long, Integer> dolrSanctionMap() {
        return sanctionMap(dolrDistrictMisDataEntryRepository.sumSanctionsByStateId(), 2);
    }

    private Map<Long, Integer> districtSanctionMap(Long stateId) {
        return sanctionMap(dolrDistrictMisDataEntryRepository.findSanctionsByStateId(stateId), 2);
    }

    private int dolrSanctionNational() {
        List<Object[]> rows = dolrDistrictMisDataEntryRepository.sumSanctionsNational();
        if (rows == null || rows.isEmpty() || rows.get(0) == null || rows.get(0).length < 2 || rows.get(0)[1] == null) {
            return 0;
        }
        return ((Number) rows.get(0)[1]).intValue();
    }

    private static Map<Long, Integer> sanctionMap(List<Object[]> rows, int valueIndex) {
        Map<Long, Integer> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (Object[] row : rows) {
            if (row == null || row.length <= valueIndex || row[0] == null) {
                continue;
            }
            map.put(((Number) row[0]).longValue(), row[valueIndex] == null ? 0 : ((Number) row[valueIndex]).intValue());
        }
        return map;
    }

    private static int sanctionOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private static Integer resolveDolrSanction(LegacyRevenueReport report, Map<Long, Integer> dolrSanctionByState) {
        if (report.getStateId() != null && dolrSanctionByState.containsKey(report.getStateId())) {
            return dolrSanctionByState.get(report.getStateId());
        }
        if (report.getLgdCode() != null && dolrSanctionByState.containsKey(report.getLgdCode().longValue())) {
            return dolrSanctionByState.get(report.getLgdCode().longValue());
        }
        return null;
    }

    private static int[] resolveStateMetrics(LegacyRevenueReport report, Map<Long, int[]> byState) {
        if (report.getStateId() != null) {
            int[] byId = byState.get(report.getStateId());
            if (byId != null && !isAllZero(byId)) {
                return byId;
            }
            if (byId != null) {
                if (report.getLgdCode() != null) {
                    int[] byLgd = byState.get(report.getLgdCode().longValue());
                    if (byLgd != null) {
                        return byLgd;
                    }
                }
                return byId;
            }
        }
        if (report.getLgdCode() != null) {
            return byState.getOrDefault(report.getLgdCode().longValue(), zeros());
        }
        return zeros();
    }

    private void applyMetrics(LegacyRevenueReport report, int[] m) {
        int total = m[0];
        int stateFunds = m[1];
        int sanctioned = m[2];
        int dilrmpDone = m[3];
        int totalDigitised = stateFunds + dilrmpDone;
        int year = m[4];
        report.setTotalPages(total);
        report.setDigitisedStateFundsPages(stateFunds);
        report.setDigitisedStateFundsPercent(percent(stateFunds, total));
        report.setDilrmpSanctionedPages(sanctioned);
        report.setDigitisedDilrmpFundsPages(dilrmpDone);
        report.setDigitisedDilrmpFundsPercent(percent(dilrmpDone, sanctioned));
        report.setTotalDigitisedPages(totalDigitised);
        report.setTotalDigitisedPercent(percent(totalDigitised, total));
        report.setDigitisedUptoYear(year);
    }

    private void applyMetrics(DistrictLegacyRevenueReport report, int[] m) {
        int total = m[0];
        int stateFunds = m[1];
        int sanctioned = m[2];
        int dilrmpDone = m[3];
        int totalDigitised = stateFunds + dilrmpDone;
        int year = m[4];
        report.setTotalPages(total);
        report.setDigitisedStateFundsPages(stateFunds);
        report.setDigitisedStateFundsPercent(percent(stateFunds, total));
        report.setDilrmpSanctionedPages(sanctioned);
        report.setDigitisedDilrmpFundsPages(dilrmpDone);
        report.setDigitisedDilrmpFundsPercent(percent(dilrmpDone, sanctioned));
        report.setTotalDigitisedPages(totalDigitised);
        report.setTotalDigitisedPercent(percent(totalDigitised, total));
        report.setDigitisedUptoYear(year);
    }

    private void formatState(LegacyRevenueReport report) {
        report.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(report.getTotalDistrict()));
        report.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(report.getTotalTehsils()));
        report.setFormattingTotalPages(NumberFormatterUtil.formatWithCommas(report.getTotalPages()));
        report.setFormattingDigitisedStateFundsPages(
                NumberFormatterUtil.formatWithCommas(report.getDigitisedStateFundsPages()));
        report.setFormattingDilrmpSanctionedPages(
                NumberFormatterUtil.formatWithCommas(report.getDilrmpSanctionedPages()));
        report.setFormattingDigitisedDilrmpFundsPages(
                NumberFormatterUtil.formatWithCommas(report.getDigitisedDilrmpFundsPages()));
        report.setFormattingTotalDigitisedPages(
                NumberFormatterUtil.formatWithCommas(report.getTotalDigitisedPages()));
    }

    private static boolean isNational(LegacyRevenueReport report) {
        return (report.getStateId() != null && report.getStateId().equals(999L))
                || (report.getLgdCode() != null && report.getLgdCode().equals(999));
    }

    private static boolean isAllZero(int[] m) {
        if (m == null) {
            return true;
        }
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
