package in.gov.dilrmp.utils;

import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.DistrictMapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;

import java.text.DecimalFormat;
import java.util.List;

/** //v5 Cadastral Maps export columns for PDF/Excel (aligned with HTML report). */
public final class MapDigitizationReportExportV5Util {

    private MapDigitizationReportExportV5Util() {
    }

    public static String[] cadastralLeafHeaderConstants() {
        return new String[]{
                ReportLabels.TOTAL,
                ReportLabels.TOTAL_DAMAGED_MISSING_MAPS,
                ReportLabels.MAP_IN_GOOD_CONDITION,
                ReportLabels.DIGITIZED,
                ReportLabels.DIGITIZED_PERCENT_OF_TOTAL_CADASTRAL_MAPS,
                ReportLabels.DIGITIZED_PERCENT_OF_GOOD_CONDITION_MAPS
        };
    }

    public static void appendCadastralMapRowV5(List<String> row, MapDigitizationReport map, DecimalFormat df) {
        row.add(nullToZeroText(map.getFormattingtotalCadastralMaps()));
        row.add(nullToZeroText(map.getFormattingTotalDamagedMissingMaps()));
        row.add(nullToZeroText(map.getFormattingMapsInGoodCondition()));
        row.add(nullToZeroText(map.getFormattingdigitizedCadastralMaps()));
        row.add(formatPercent(df, map.getDigitizedCadastralMapsPercent()));
        row.add(formatPercent(df, map.getDigitizedCadastralMapsPercentGoodCondition()));
    }

    public static void appendCadastralMapRowV5(List<String> row, DistrictMapDigitizationReport map, DecimalFormat df) {
        row.add(Integer.toString(nullSafe(map.getTotalCadastralMaps())));
        row.add(Integer.toString(nullSafe(map.getTotalDamagedMissingMaps())));
        row.add(Integer.toString(nullSafe(map.getMapsInGoodCondition())));
        row.add(Integer.toString(nullSafe(map.getDigitizedCadastralMaps())));
        row.add(formatPercent(df, map.getDigitizedCadastralMapsPercent()));
        row.add(formatPercent(df, map.getDigitizedCadastralMapsPercentGoodCondition()));
    }

    public static void appendCadastralMapRowV5Raw(List<String> row, MapDigitizationReport map, DecimalFormat df) {
        row.add(Integer.toString(nullSafe(map.getTotalCadastralMaps())));
        row.add(Integer.toString(nullSafe(map.getTotalDamagedMissingMaps())));
        row.add(Integer.toString(nullSafe(map.getMapsInGoodCondition())));
        row.add(Integer.toString(nullSafe(map.getDigitizedCadastralMaps())));
        row.add(formatPercent(df, map.getDigitizedCadastralMapsPercent()));
        row.add(formatPercent(df, map.getDigitizedCadastralMapsPercentGoodCondition()));
    }

    private static int nullSafe(Integer value) {
        return value == null ? 0 : value;
    }

    private static String nullToZeroText(String value) {
        return value == null || value.isEmpty() ? "0" : value;
    }

    private static String formatPercent(DecimalFormat df, Double value) {
        return df.format(value == null ? 0.0 : value);
    }
}
