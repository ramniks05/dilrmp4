package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.reportDTO.legacy.DistrictLegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.legacy.LegacyDigitizationReport;

import java.util.List;

/** //v5 Legacy Digitization PDF/Excel row helpers. */
public final class LegacyDigitizationExportUtil {

    private LegacyDigitizationExportUtil() {
    }

    public static void appendStateFormatted(List<String> row, LegacyDigitizationReport r) {
        row.add(nz(r.getFormattingLegacyTotalPages()));
        row.add(nz(r.getFormattingLegacyDigitisedStateFundsPages()));
        row.add(pct(r.getLegacyDigitisedStateFundsPercent()));
        row.add(nz(r.getFormattingLegacyDilrmpSanctionedPages()));
        row.add(nz(r.getFormattingLegacyDigitisedDilrmpFundsPages()));
        row.add(pct(r.getLegacyDigitisedDilrmpFundsPercent()));
        row.add(nz(r.getFormattingLegacyTotalDigitisedPages()));
        row.add(pct(r.getLegacyTotalDigitisedPercent()));
        row.add(r.getLegacyDigitisedUptoYear() != null ? String.valueOf(r.getLegacyDigitisedUptoYear()) : "0");
    }

    public static void appendStateRaw(List<String> row, LegacyDigitizationReport r) {
        row.add(num(r.getLegacyTotalPages()));
        row.add(num(r.getLegacyDigitisedStateFundsPages()));
        row.add(pct(r.getLegacyDigitisedStateFundsPercent()));
        row.add(num(r.getLegacyDilrmpSanctionedPages()));
        row.add(num(r.getLegacyDigitisedDilrmpFundsPages()));
        row.add(pct(r.getLegacyDigitisedDilrmpFundsPercent()));
        row.add(num(r.getLegacyTotalDigitisedPages()));
        row.add(pct(r.getLegacyTotalDigitisedPercent()));
        row.add(r.getLegacyDigitisedUptoYear() != null ? String.valueOf(r.getLegacyDigitisedUptoYear()) : "0");
    }

    public static void appendDistrict(List<String> row, DistrictLegacyDigitizationReport r) {
        row.add(num(r.getLegacyTotalPages()));
        row.add(num(r.getLegacyDigitisedStateFundsPages()));
        row.add(pct(r.getLegacyDigitisedStateFundsPercent()));
        row.add(num(r.getLegacyDilrmpSanctionedPages()));
        row.add(num(r.getLegacyDigitisedDilrmpFundsPages()));
        row.add(pct(r.getLegacyDigitisedDilrmpFundsPercent()));
        row.add(num(r.getLegacyTotalDigitisedPages()));
        row.add(pct(r.getLegacyTotalDigitisedPercent()));
        row.add(r.getLegacyDigitisedUptoYear() != null ? String.valueOf(r.getLegacyDigitisedUptoYear()) : "0");
    }

    private static String nz(String v) {
        return v == null || v.isEmpty() ? "0" : v;
    }

    private static String num(Integer v) {
        return v == null ? "0" : String.valueOf(v);
    }

    private static String pct(java.math.BigDecimal v) {
        return v == null ? "0.0" : v.toString();
    }
}
