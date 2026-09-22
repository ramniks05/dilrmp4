package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.reportDTO.legacy.DistrictLegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.legacy.LegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DistrictMISDataEntryRepository;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DolrMisDataEntryRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * //v5 Build Legacy Digitization report rows from existing MRR geo (state/district/tehsil)
 * plus live MIS form aggregates. Form updates reflect on next report load.
 * DoLR sanctioned (C) comes from DoLR Generic MIS form when filled.
 */
@Component
public class LegacyDigitizationReportBuilder {

    private final DistrictMISDataEntryRepository districtMISDataEntryRepository;
    private final DolrMisDataEntryRepository dolrMisDataEntryRepository;

    public LegacyDigitizationReportBuilder(DistrictMISDataEntryRepository districtMISDataEntryRepository,
                                           DolrMisDataEntryRepository dolrMisDataEntryRepository) {
        this.districtMISDataEntryRepository = districtMISDataEntryRepository;
        this.dolrMisDataEntryRepository = dolrMisDataEntryRepository;
    }

    /** Geo skeleton from existing MRR master/view data (state, districts, tehsils). */
    public LegacyDigitizationReport fromMrrState(MrrViewReport mrr) {
        LegacyDigitizationReport report = new LegacyDigitizationReport();
        report.setStateId(mrr.getStateId());
        report.setStateName(mrr.getStateName());
        report.setLgdCode(mrr.getLgdCode());
        report.setTotalDistrict(mrr.getTotalDistrict());
        report.setTotalTehsils(mrr.getTotalTehsils());
        return report;
    }

    /** Geo skeleton from existing district MRR view (district name + tehsils). */
    public DistrictLegacyDigitizationReport fromMrrDistrict(DistrictMrrViewReport mrr) {
        DistrictLegacyDigitizationReport report = new DistrictLegacyDigitizationReport();
        report.setDistrictId(mrr.getDistrictId());
        report.setDistrictName(mrr.getDistrictName());
        report.setStateId(mrr.getStateId());
        report.setStateName(mrr.getStateName());
        report.setTotalTehsils(mrr.getTotalTehsils());
        return report;
    }

    public void enrichStateReports(List<LegacyDigitizationReport> reports) {
        Map<Long, int[]> byState = toMetricMap(districtMISDataEntryRepository.sumLegacyDigitizationByStateId());
        Map<Long, Integer> dolrSanctionByState = dolrLegacySanctionMap();
        int[] national = firstNational(districtMISDataEntryRepository.sumLegacyDigitizationNational());
        int nationalDolrSanction = dolrLegacySanctionNational();
        reports.forEach(report -> {
            int[] metrics;
            if (isNational(report)) {
                metrics = national.clone();
                metrics[2] = nationalDolrSanction;
            } else {
                metrics = resolveStateMetrics(report, byState).clone();
                Integer dolrC = resolveDolrSanction(report, dolrSanctionByState);
                if (dolrC != null) {
                    metrics[2] = dolrC;
                }
            }
            applyMetrics(report, metrics);
            formatState(report);
        });
    }

    public void enrichDistrictReports(List<DistrictLegacyDigitizationReport> reports, Long stateId) {
        Map<Long, int[]> byDistrict = toMetricMap(
                districtMISDataEntryRepository.sumLegacyDigitizationByDistrictForStateId(stateId));
        reports.forEach(report -> applyMetrics(report, byDistrict.getOrDefault(report.getDistrictId(), zeros())));
    }

    public void enrichStateTotalsForDistrictPage(List<LegacyDigitizationReport> reports, Long stateId) {
        Map<Long, int[]> byState = toMetricMap(districtMISDataEntryRepository.sumLegacyDigitizationByStateId());
        Map<Long, Integer> dolrSanctionByState = dolrLegacySanctionMap();
        reports.forEach(report -> {
            int[] metrics = byState.getOrDefault(stateId, zeros()).clone();
            if (isAllZero(metrics) && report.getLgdCode() != null) {
                metrics = byState.getOrDefault(report.getLgdCode().longValue(), metrics).clone();
            }
            Integer dolrC = resolveDolrSanction(report, dolrSanctionByState);
            if (dolrC == null && stateId != null) {
                dolrC = dolrSanctionByState.get(stateId);
            }
            if (dolrC != null) {
                metrics[2] = dolrC;
            }
            applyMetrics(report, metrics);
            formatState(report);
        });
    }

    private Map<Long, Integer> dolrLegacySanctionMap() {
        Map<Long, Integer> map = new HashMap<>();
        List<Object[]> rows = dolrMisDataEntryRepository.findAllSanctionPairsByStateId();
        if (rows == null) {
            return map;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 2 || row[0] == null) {
                continue;
            }
            map.put(((Number) row[0]).longValue(), row[1] == null ? 0 : ((Number) row[1]).intValue());
        }
        return map;
    }

    private int dolrLegacySanctionNational() {
        List<Object[]> rows = dolrMisDataEntryRepository.sumSanctionsNational();
        if (rows == null || rows.isEmpty() || rows.get(0) == null || rows.get(0)[0] == null) {
            return 0;
        }
        return ((Number) rows.get(0)[0]).intValue();
    }

    private static Integer resolveDolrSanction(LegacyDigitizationReport report, Map<Long, Integer> dolrSanctionByState) {
        if (report.getStateId() != null && dolrSanctionByState.containsKey(report.getStateId())) {
            return dolrSanctionByState.get(report.getStateId());
        }
        if (report.getLgdCode() != null && dolrSanctionByState.containsKey(report.getLgdCode().longValue())) {
            return dolrSanctionByState.get(report.getLgdCode().longValue());
        }
        return null;
    }

    /** Prefer state_id match; fall back to LGD code if MIS keys differ. */
    private static int[] resolveStateMetrics(LegacyDigitizationReport report, Map<Long, int[]> byState) {
        if (report.getStateId() != null) {
            int[] byId = byState.get(report.getStateId());
            if (byId != null && !isAllZero(byId)) {
                return byId;
            }
            if (byId != null) {
                // keep zero row if key exists but empty — still try lgd fallback for data
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

    private void applyMetrics(LegacyDigitizationReport report, int[] m) {
        int total = m[0];
        int stateFunds = m[1];
        int sanctioned = m[2];
        int dilrmpDone = m[3];
        int totalDigitised = m[4];
        int year = m[5];
        report.setLegacyTotalPages(total);
        report.setLegacyDigitisedStateFundsPages(stateFunds);
        report.setLegacyDigitisedStateFundsPercent(percent(stateFunds, total));
        report.setLegacyDilrmpSanctionedPages(sanctioned);
        report.setLegacyDigitisedDilrmpFundsPages(dilrmpDone);
        report.setLegacyDigitisedDilrmpFundsPercent(percent(dilrmpDone, sanctioned));
        report.setLegacyTotalDigitisedPages(totalDigitised);
        report.setLegacyTotalDigitisedPercent(percent(totalDigitised, total));
        report.setLegacyDigitisedUptoYear(year);
    }

    private void applyMetrics(DistrictLegacyDigitizationReport report, int[] m) {
        int total = m[0];
        int stateFunds = m[1];
        int sanctioned = m[2];
        int dilrmpDone = m[3];
        int totalDigitised = m[4];
        int year = m[5];
        report.setLegacyTotalPages(total);
        report.setLegacyDigitisedStateFundsPages(stateFunds);
        report.setLegacyDigitisedStateFundsPercent(percent(stateFunds, total));
        report.setLegacyDilrmpSanctionedPages(sanctioned);
        report.setLegacyDigitisedDilrmpFundsPages(dilrmpDone);
        report.setLegacyDigitisedDilrmpFundsPercent(percent(dilrmpDone, sanctioned));
        report.setLegacyTotalDigitisedPages(totalDigitised);
        report.setLegacyTotalDigitisedPercent(percent(totalDigitised, total));
        report.setLegacyDigitisedUptoYear(year);
    }

    private void formatState(LegacyDigitizationReport report) {
        report.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(report.getTotalDistrict()));
        report.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(report.getTotalTehsils()));
        report.setFormattingLegacyTotalPages(NumberFormatterUtil.formatWithCommas(report.getLegacyTotalPages()));
        report.setFormattingLegacyDigitisedStateFundsPages(
                NumberFormatterUtil.formatWithCommas(report.getLegacyDigitisedStateFundsPages()));
        report.setFormattingLegacyDilrmpSanctionedPages(
                NumberFormatterUtil.formatWithCommas(report.getLegacyDilrmpSanctionedPages()));
        report.setFormattingLegacyDigitisedDilrmpFundsPages(
                NumberFormatterUtil.formatWithCommas(report.getLegacyDigitisedDilrmpFundsPages()));
        report.setFormattingLegacyTotalDigitisedPages(
                NumberFormatterUtil.formatWithCommas(report.getLegacyTotalDigitisedPages()));
    }

    private static boolean isNational(LegacyDigitizationReport report) {
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
        return new int[]{0, 0, 0, 0, 0, 0};
    }

    private static int[] firstNational(List<Object[]> rows) {
        if (rows == null || rows.isEmpty() || rows.get(0) == null) {
            return zeros();
        }
        Object[] row = rows.get(0);
        return new int[]{toInt(row, 0), toInt(row, 1), toInt(row, 2), toInt(row, 3), toInt(row, 4), toInt(row, 5)};
    }

    private static Map<Long, int[]> toMetricMap(List<Object[]> rows) {
        Map<Long, int[]> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 7 || row[0] == null) {
                continue;
            }
            map.put(((Number) row[0]).longValue(), new int[]{
                    toInt(row, 1), toInt(row, 2), toInt(row, 3), toInt(row, 4), toInt(row, 5), toInt(row, 6)
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
