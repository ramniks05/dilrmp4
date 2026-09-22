package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.DistrictMapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DistrictMISDataEntryRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * //v5 Cadastral Maps: [11.1], good condition ([11]-[11.1]), digitized % vs total [11] and vs good condition.
 */
@Component
public class MapDigitizationReportV5Enricher {

    private final DistrictMISDataEntryRepository districtMISDataEntryRepository;

    public MapDigitizationReportV5Enricher(DistrictMISDataEntryRepository districtMISDataEntryRepository) {
        this.districtMISDataEntryRepository = districtMISDataEntryRepository;
    }

    public void enrichStateReports(List<MapDigitizationReport> reports) {
        Map<Integer, Integer> damagedByStateLgd = toIntMap(districtMISDataEntryRepository.sumDamagedMissingMapsByStateLgd());
        reports.forEach(report -> enrichStateReport(report, damagedByStateLgd));
    }

    public void enrichStateReportListForDistrictPage(List<MapDigitizationReport> reports, Integer stateLgdCode) {
        Map<Integer, Integer> damagedByStateLgd = toIntMap(districtMISDataEntryRepository.sumDamagedMissingMapsByStateLgd());
        reports.forEach(report -> {
            Integer damaged = damagedByStateLgd.getOrDefault(stateLgdCode, 0);
            applyV5CadastralMetrics(report, damaged);
        });
    }

    public void enrichDistrictReports(List<DistrictMapDigitizationReport> reports, Integer stateLgdCode) {
        Map<Long, Integer> damagedByDistrictId = toLongMap(
                districtMISDataEntryRepository.sumDamagedMissingMapsByDistrictForStateLgd(stateLgdCode));
        reports.forEach(report -> {
            int damaged = damagedByDistrictId.getOrDefault(report.getDistrictId(), 0);
            applyV5CadastralMetrics(report, damaged);
        });
    }

    private void enrichStateReport(MapDigitizationReport report, Map<Integer, Integer> damagedByStateLgd) {
        int damaged = 0;
        if (report.getLgdCode() != null && report.getLgdCode().equals(999)) {
            damaged = nullSafeInt(districtMISDataEntryRepository.sumDamagedMissingMapsNational());
        } else if (report.getLgdCode() != null) {
            damaged = damagedByStateLgd.getOrDefault(report.getLgdCode(), 0);
        }
        applyV5CadastralMetrics(report, damaged);
    }

    private void applyV5CadastralMetrics(MapDigitizationReport report, int damagedMissing) {
        int totalCadastral = nullSafeInt(report.getTotalCadastralMaps());
        int digitized = nullSafeInt(report.getDigitizedCadastralMaps());
        int goodCondition = Math.max(0, totalCadastral - damagedMissing);

        report.setTotalDamagedMissingMaps(damagedMissing);
        report.setFormattingTotalDamagedMissingMaps(NumberFormatterUtil.formatWithCommas(damagedMissing));
        report.setMapsInGoodCondition(goodCondition);
        report.setFormattingMapsInGoodCondition(NumberFormatterUtil.formatWithCommas(goodCondition));
        report.setDigitizedCadastralMapsPercent(calculateDigitizedPercent(digitized, totalCadastral));
        report.setDigitizedCadastralMapsPercentGoodCondition(calculateDigitizedPercent(digitized, goodCondition));
    }

    private void applyV5CadastralMetrics(DistrictMapDigitizationReport report, int damagedMissing) {
        int totalCadastral = nullSafeInt(report.getTotalCadastralMaps());
        int digitized = nullSafeInt(report.getDigitizedCadastralMaps());
        int goodCondition = Math.max(0, totalCadastral - damagedMissing);

        report.setTotalDamagedMissingMaps(damagedMissing);
        report.setMapsInGoodCondition(goodCondition);
        report.setDigitizedCadastralMapsPercent(calculateDigitizedPercent(digitized, totalCadastral));
        report.setDigitizedCadastralMapsPercentGoodCondition(calculateDigitizedPercent(digitized, goodCondition));
    }

    private static double calculateDigitizedPercent(int digitized, int denominator) {
        if (denominator <= 0) {
            return 0.0;
        }
        return Math.round(digitized * 10000.0 / denominator) / 100.0;
    }

    private static int nullSafeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private static Map<Integer, Integer> toIntMap(List<Object[]> rows) {
        Map<Integer, Integer> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 2 || row[0] == null) {
                continue;
            }
            int lgd = ((Number) row[0]).intValue();
            int sum = row[1] == null ? 0 : ((Number) row[1]).intValue();
            map.put(lgd, sum);
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
            long districtId = ((Number) row[0]).longValue();
            int sum = row[1] == null ? 0 : ((Number) row[1]).intValue();
            map.put(districtId, sum);
        }
        return map;
    }
}
