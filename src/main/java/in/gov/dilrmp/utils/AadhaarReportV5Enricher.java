package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.reportDTO.linkedaadhaar.DistrictLinkedAadhaarViewReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DistrictMISDataEntryRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * //v5 Aadhaar: RoR Linked with Address [43] + No. of Land owners block [44]-[46].
 */
@Component
public class AadhaarReportV5Enricher {

    private final DistrictMISDataEntryRepository districtMISDataEntryRepository;

    public AadhaarReportV5Enricher(DistrictMISDataEntryRepository districtMISDataEntryRepository) {
        this.districtMISDataEntryRepository = districtMISDataEntryRepository;
    }

    public void enrichStateReports(List<LinkedAadharViewReport> reports) {
        Map<Long, Integer> addressByState = toLongMap(districtMISDataEntryRepository.sumRorLinkedWithAddressByStateId());
        Map<Long, int[]> landByState = toLandOwnerMap(districtMISDataEntryRepository.sumLandOwnerLinkageByStateId());
        int[] nationalLand = firstLandRow(districtMISDataEntryRepository.sumLandOwnerLinkageNational());
        reports.forEach(report -> {
            enrichStateAddress(report, addressByState);
            enrichStateLandOwners(report, landByState, nationalLand);
        });
    }

    public void enrichDistrictReports(List<DistrictLinkedAadhaarViewReport> reports, Long stateId) {
        Map<Long, Integer> addressByDistrict = toLongMap(
                districtMISDataEntryRepository.sumRorLinkedWithAddressByDistrictForStateId(stateId));
        Map<Long, int[]> landByDistrict = toLandOwnerMap(
                districtMISDataEntryRepository.sumLandOwnerLinkageByDistrictForStateId(stateId));
        reports.forEach(report -> {
            applyAddressMetrics(report, addressByDistrict.getOrDefault(report.getDistrictId(), 0));
            applyLandOwnerMetrics(report, landByDistrict.getOrDefault(report.getDistrictId(), zeros()));
        });
    }

    public void enrichStateReportListForDistrictPage(List<LinkedAadharViewReport> reports, Long stateId) {
        Map<Long, Integer> addressByState = toLongMap(districtMISDataEntryRepository.sumRorLinkedWithAddressByStateId());
        Map<Long, int[]> landByState = toLandOwnerMap(districtMISDataEntryRepository.sumLandOwnerLinkageByStateId());
        reports.forEach(report -> {
            applyAddressMetrics(report, addressByState.getOrDefault(stateId, 0));
            applyLandOwnerMetrics(report, landByState.getOrDefault(stateId, zeros()));
        });
    }

    private void enrichStateAddress(LinkedAadharViewReport report, Map<Long, Integer> byStateId) {
        int linked = 0;
        if (isNational(report)) {
            linked = nullSafeInt(districtMISDataEntryRepository.sumRorLinkedWithAddressNational());
        } else if (report.getStateId() != null) {
            linked = byStateId.getOrDefault(report.getStateId(), 0);
        }
        applyAddressMetrics(report, linked);
    }

    private void enrichStateLandOwners(LinkedAadharViewReport report, Map<Long, int[]> byStateId, int[] national) {
        int[] values = zeros();
        if (isNational(report)) {
            values = national;
        } else if (report.getStateId() != null) {
            values = byStateId.getOrDefault(report.getStateId(), zeros());
        }
        applyLandOwnerMetrics(report, values);
    }

    private static boolean isNational(LinkedAadharViewReport report) {
        return (report.getStateId() != null && report.getStateId().equals(999L))
                || (report.getLgdCode() != null && report.getLgdCode().equals(999));
    }

    private void applyAddressMetrics(LinkedAadharViewReport report, int linkedWithAddress) {
        int totalRor = nullSafeInt(report.getTotalRor());
        report.setRorLinkedWithAddress(linkedWithAddress);
        report.setFormattingrorLinkedWithAddress(NumberFormatterUtil.formatWithCommas(linkedWithAddress));
        report.setRorLinkedWithAddressPercent(calculatePercent(linkedWithAddress, totalRor));
    }

    private void applyAddressMetrics(DistrictLinkedAadhaarViewReport report, int linkedWithAddress) {
        int totalRor = report.getTotalRor() == null ? 0 : report.getTotalRor().intValue();
        report.setRorLinkedWithAddress(linkedWithAddress);
        report.setRorLinkedWithAddressPercent(calculatePercent(linkedWithAddress, totalRor).doubleValue());
    }

    private void applyLandOwnerMetrics(LinkedAadharViewReport report, int[] values) {
        int total = values[0];
        int aadhaar = values[1];
        int mobile = values[2];
        int address = values[3];
        report.setTotalLandOwners(total);
        report.setFormattingTotalLandOwners(NumberFormatterUtil.formatWithCommas(total));
        report.setLandOwnersLinkedWithAadhaar(aadhaar);
        report.setFormattingLandOwnersLinkedWithAadhaar(NumberFormatterUtil.formatWithCommas(aadhaar));
        report.setLandOwnersLinkedWithAadhaarPercent(calculatePercent(aadhaar, total));
        report.setLandOwnersLinkedWithMobile(mobile);
        report.setFormattingLandOwnersLinkedWithMobile(NumberFormatterUtil.formatWithCommas(mobile));
        report.setLandOwnersLinkedWithMobilePercent(calculatePercent(mobile, total));
        report.setLandOwnersLinkedWithAddress(address);
        report.setFormattingLandOwnersLinkedWithAddress(NumberFormatterUtil.formatWithCommas(address));
        report.setLandOwnersLinkedWithAddressPercent(calculatePercent(address, total));
    }

    private void applyLandOwnerMetrics(DistrictLinkedAadhaarViewReport report, int[] values) {
        int total = values[0];
        int aadhaar = values[1];
        int mobile = values[2];
        int address = values[3];
        report.setTotalLandOwners(total);
        report.setLandOwnersLinkedWithAadhaar(aadhaar);
        report.setLandOwnersLinkedWithAadhaarPercent(calculatePercent(aadhaar, total).doubleValue());
        report.setLandOwnersLinkedWithMobile(mobile);
        report.setLandOwnersLinkedWithMobilePercent(calculatePercent(mobile, total).doubleValue());
        report.setLandOwnersLinkedWithAddress(address);
        report.setLandOwnersLinkedWithAddressPercent(calculatePercent(address, total).doubleValue());
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

    private static int[] zeros() {
        return new int[]{0, 0, 0, 0};
    }

    private static int[] firstLandRow(List<Object[]> rows) {
        if (rows == null || rows.isEmpty() || rows.get(0) == null) {
            return zeros();
        }
        Object[] row = rows.get(0);
        return new int[]{
                toInt(row.length > 0 ? row[0] : null),
                toInt(row.length > 1 ? row[1] : null),
                toInt(row.length > 2 ? row[2] : null),
                toInt(row.length > 3 ? row[3] : null)
        };
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
            map.put(((Number) row[0]).longValue(), toInt(row[1]));
        }
        return map;
    }

    private static Map<Long, int[]> toLandOwnerMap(List<Object[]> rows) {
        Map<Long, int[]> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 5 || row[0] == null) {
                continue;
            }
            map.put(((Number) row[0]).longValue(), new int[]{
                    toInt(row[1]), toInt(row[2]), toInt(row[3]), toInt(row[4])
            });
        }
        return map;
    }

    private static int toInt(Object value) {
        return value == null ? 0 : ((Number) value).intValue();
    }
}
