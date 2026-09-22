package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.reportDTO.sroModernization.SroModernizationReport;

import java.util.List;

/** //v5 SRO Modernization PDF/Excel row helpers. */
public final class SroModernizationExportUtil {

    private SroModernizationExportUtil() {
    }

    public static void appendStateFormatted(List<String> row, SroModernizationReport r) {
        row.add(nz(r.getFormattingSrosUsingOnlineRegistration()));
        row.add(nz(r.getFormattingSrosModernisedStateFunds()));
        row.add(pct(r.getSrosModernisedStateFundsPercent()));
        row.add(nz(r.getFormattingSrosDilrmpSanctioned()));
        row.add(nz(r.getFormattingSrosModernisedDilrmpFunds()));
        row.add(pct(r.getSrosModernisedDilrmpFundsPercent()));
        row.add(nz(r.getFormattingSrosModernisedTotal()));
        row.add(pct(r.getSrosModernisedTotalPercent()));
    }

    public static void appendStateRaw(List<String> row, SroModernizationReport r) {
        row.add(num(r.getSrosUsingOnlineRegistration()));
        row.add(num(r.getSrosModernisedStateFunds()));
        row.add(pct(r.getSrosModernisedStateFundsPercent()));
        row.add(num(r.getSrosDilrmpSanctioned()));
        row.add(num(r.getSrosModernisedDilrmpFunds()));
        row.add(pct(r.getSrosModernisedDilrmpFundsPercent()));
        row.add(num(r.getSrosModernisedTotal()));
        row.add(pct(r.getSrosModernisedTotalPercent()));
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
