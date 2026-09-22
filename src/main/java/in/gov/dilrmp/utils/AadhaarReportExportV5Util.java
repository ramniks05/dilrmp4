package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.reportDTO.linkedaadhaar.DistrictLinkedAadhaarViewReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;

import java.math.BigDecimal;
import java.util.List;

/** //v5 Aadhaar: append RoR Address + Land owners block for PDF/Excel. */
public final class AadhaarReportExportV5Util {

    private AadhaarReportExportV5Util() {
    }

    public static void appendAddressRowFormatted(List<String> row, LinkedAadharViewReport map) {
        row.add(map.getFormattingrorLinkedWithAddress() != null ? map.getFormattingrorLinkedWithAddress() : "0");
        row.add(percentOrZero(map.getRorLinkedWithAddressPercent()));
    }

    public static void appendAddressRowRaw(List<String> row, LinkedAadharViewReport map) {
        row.add(map.getRorLinkedWithAddress() != null ? String.valueOf(map.getRorLinkedWithAddress()) : "0");
        row.add(percentOrZero(map.getRorLinkedWithAddressPercent()));
    }

    public static void appendAddressRowDistrict(List<String> row, DistrictLinkedAadhaarViewReport map) {
        row.add(map.getRorLinkedWithAddress() != null ? String.valueOf(map.getRorLinkedWithAddress()) : "0");
        row.add(map.getRorLinkedWithAddressPercent() != null ? String.valueOf(map.getRorLinkedWithAddressPercent()) : "0.0");
    }

    public static void appendLandOwnerRowFormatted(List<String> row, LinkedAadharViewReport map) {
        row.add(map.getFormattingTotalLandOwners() != null ? map.getFormattingTotalLandOwners() : "0");
        row.add(map.getFormattingLandOwnersLinkedWithAadhaar() != null ? map.getFormattingLandOwnersLinkedWithAadhaar() : "0");
        row.add(percentOrZero(map.getLandOwnersLinkedWithAadhaarPercent()));
        row.add(map.getFormattingLandOwnersLinkedWithMobile() != null ? map.getFormattingLandOwnersLinkedWithMobile() : "0");
        row.add(percentOrZero(map.getLandOwnersLinkedWithMobilePercent()));
        row.add(map.getFormattingLandOwnersLinkedWithAddress() != null ? map.getFormattingLandOwnersLinkedWithAddress() : "0");
        row.add(percentOrZero(map.getLandOwnersLinkedWithAddressPercent()));
    }

    public static void appendLandOwnerRowRaw(List<String> row, LinkedAadharViewReport map) {
        row.add(map.getTotalLandOwners() != null ? String.valueOf(map.getTotalLandOwners()) : "0");
        row.add(map.getLandOwnersLinkedWithAadhaar() != null ? String.valueOf(map.getLandOwnersLinkedWithAadhaar()) : "0");
        row.add(percentOrZero(map.getLandOwnersLinkedWithAadhaarPercent()));
        row.add(map.getLandOwnersLinkedWithMobile() != null ? String.valueOf(map.getLandOwnersLinkedWithMobile()) : "0");
        row.add(percentOrZero(map.getLandOwnersLinkedWithMobilePercent()));
        row.add(map.getLandOwnersLinkedWithAddress() != null ? String.valueOf(map.getLandOwnersLinkedWithAddress()) : "0");
        row.add(percentOrZero(map.getLandOwnersLinkedWithAddressPercent()));
    }

    public static void appendLandOwnerRowDistrict(List<String> row, DistrictLinkedAadhaarViewReport map) {
        row.add(map.getTotalLandOwners() != null ? String.valueOf(map.getTotalLandOwners()) : "0");
        row.add(map.getLandOwnersLinkedWithAadhaar() != null ? String.valueOf(map.getLandOwnersLinkedWithAadhaar()) : "0");
        row.add(map.getLandOwnersLinkedWithAadhaarPercent() != null ? String.valueOf(map.getLandOwnersLinkedWithAadhaarPercent()) : "0.0");
        row.add(map.getLandOwnersLinkedWithMobile() != null ? String.valueOf(map.getLandOwnersLinkedWithMobile()) : "0");
        row.add(map.getLandOwnersLinkedWithMobilePercent() != null ? String.valueOf(map.getLandOwnersLinkedWithMobilePercent()) : "0.0");
        row.add(map.getLandOwnersLinkedWithAddress() != null ? String.valueOf(map.getLandOwnersLinkedWithAddress()) : "0");
        row.add(map.getLandOwnersLinkedWithAddressPercent() != null ? String.valueOf(map.getLandOwnersLinkedWithAddressPercent()) : "0.0");
    }

    private static String percentOrZero(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) == 0) {
            return "0.0";
        }
        return value.toString();
    }
}
