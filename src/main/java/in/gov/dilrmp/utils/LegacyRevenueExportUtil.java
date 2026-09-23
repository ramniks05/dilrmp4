package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.reportDTO.legacyRevenue.DistrictLegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.LegacyRevenueReport;

import java.util.List;

/** Legacy Revenue Records Digitisation PDF/Excel row helpers. */
public final class LegacyRevenueExportUtil {

    private LegacyRevenueExportUtil() {
    }

    public static void appendStateFormatted(List<String> row, LegacyRevenueReport r) {
        row.add(nz(r.getFormattingTotalPages()));
        row.add(nz(r.getFormattingDigitisedStateFundsPages()));
        row.add(pct(r.getDigitisedStateFundsPercent()));
        row.add(nz(r.getFormattingDilrmpSanctionedPages()));
        row.add(nz(r.getFormattingDigitisedDilrmpFundsPages()));
        row.add(pct(r.getDigitisedDilrmpFundsPercent()));
        row.add(nz(r.getFormattingTotalDigitisedPages()));
        row.add(pct(r.getTotalDigitisedPercent()));
        row.add(year(r.getDigitisedUptoYear()));
    }

    public static void appendStateRaw(List<String> row, LegacyRevenueReport r) {
        row.add(num(r.getTotalPages()));
        row.add(num(r.getDigitisedStateFundsPages()));
        row.add(pct(r.getDigitisedStateFundsPercent()));
        row.add(num(r.getDilrmpSanctionedPages()));
        row.add(num(r.getDigitisedDilrmpFundsPages()));
        row.add(pct(r.getDigitisedDilrmpFundsPercent()));
        row.add(num(r.getTotalDigitisedPages()));
        row.add(pct(r.getTotalDigitisedPercent()));
        row.add(year(r.getDigitisedUptoYear()));
    }

    public static void appendDistrict(List<String> row, DistrictLegacyRevenueReport r) {
        row.add(num(r.getTotalPages()));
        row.add(num(r.getDigitisedStateFundsPages()));
        row.add(pct(r.getDigitisedStateFundsPercent()));
        row.add(num(r.getDilrmpSanctionedPages()));
        row.add(num(r.getDigitisedDilrmpFundsPages()));
        row.add(pct(r.getDigitisedDilrmpFundsPercent()));
        row.add(num(r.getTotalDigitisedPages()));
        row.add(pct(r.getTotalDigitisedPercent()));
        row.add(year(r.getDigitisedUptoYear()));
    }

    private static String nz(String v) {
        return v == null || v.isEmpty() ? "0" : v;
    }

    private static String num(Integer v) {
        return v == null ? "0" : String.valueOf(v);
    }

    private static String year(Integer v) {
        return v == null || v == 0 ? "" : String.valueOf(v);
    }

    private static String pct(java.math.BigDecimal v) {
        return v == null ? "0.00" : v.toString();
    }
}
