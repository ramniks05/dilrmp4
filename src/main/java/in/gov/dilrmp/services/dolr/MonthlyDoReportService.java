package in.gov.dilrmp.services.dolr;

import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.services.physicalProgressServices.StateReportsAsOnDateService;
import in.gov.dilrmp.services.physicalProgressServices.StateReportsAsOnDateService.Component;
import in.gov.dilrmp.services.physicalProgressServices.StateReportsAsOnDateService.Snapshot;
import in.gov.dilrmp.utils.NumberFormatterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds the monthly D.O. letter report from national MIS totals
 * (CLR + Map Digitization grand-total rows) as on month-end dates.
 */
@Service
public class MonthlyDoReportService {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    private StateReportsAsOnDateService stateReportsAsOnDateService;

    public LocalDate firstAvailableDate() {
        LocalDate clr = stateReportsAsOnDateService.getFirstAvailableDate(Component.CLR);
        LocalDate map = stateReportsAsOnDateService.getFirstAvailableDate(Component.MAP);
        if (clr == null) {
            return map;
        }
        if (map == null) {
            return clr;
        }
        return clr.isAfter(map) ? clr : map;
    }

    public YearMonth defaultMonth() {
        LocalDate today = LocalDate.now();
        YearMonth current = YearMonth.from(today);
        // Prefer the previous completed month when today is not month-end.
        if (today.getDayOfMonth() < today.lengthOfMonth()) {
            return current.minusMonths(1);
        }
        return current;
    }

    public Report build(YearMonth month) {
        if (month == null) {
            month = defaultMonth();
        }
        LocalDate today = LocalDate.now();
        YearMonth maxMonth = YearMonth.from(today);
        if (month.isAfter(maxMonth)) {
            month = maxMonth;
        }
        LocalDate minDate = firstAvailableDate();
        YearMonth minMonth = minDate == null ? null : YearMonth.from(minDate);
        if (minMonth != null && month.isBefore(minMonth)) {
            month = minMonth;
        }

        LocalDate currentAsOn = asOnForMonth(month, today);
        LocalDate previousAsOn = asOnForMonth(month.minusMonths(1), today);
        LocalDate previousFyAsOn = asOnForMonth(month.minusYears(1), today);

        NationalTotals previous = nationalTotals(previousAsOn);
        NationalTotals current = nationalTotals(currentAsOn);
        NationalTotals previousFy = nationalTotals(previousFyAsOn);

        List<Row> rows = new ArrayList<>();
        rows.add(row("1", "Total No. of Record of Rights",
                previous.totalRor, current.totalRor, previousFy.totalRor));
        rows.add(row("2", "No. of RoRs computerized",
                previous.rorComputerized, current.rorComputerized, previousFy.rorComputerized));
        rows.add(row("3", "Total No. of Cadastral maps",
                previous.totalCadastralMaps, current.totalCadastralMaps, previousFy.totalCadastralMaps));
        rows.add(row("4", "No. of Cadastral Maps Digitized",
                previous.digitizedCadastralMaps, current.digitizedCadastralMaps, previousFy.digitizedCadastralMaps));
        rows.add(row("5", "Total No. Land parcels",
                previous.totalLandParcels, current.totalLandParcels, previousFy.totalLandParcels));
        rows.add(row("6", "Number of Land Parcels assigned ULPIN",
                previous.landParcelsWithUlipn, current.landParcelsWithUlipn, previousFy.landParcelsWithUlipn));

        Report report = new Report();
        report.month = month;
        report.monthLabel = month.getMonth().name().charAt(0)
                + month.getMonth().name().substring(1).toLowerCase()
                + " " + month.getYear();
        report.previousAsOn = previousAsOn;
        report.currentAsOn = currentAsOn;
        report.previousFyAsOn = previousFyAsOn;
        report.previousAsOnLabel = previousAsOn.format(DISPLAY);
        report.currentAsOnLabel = currentAsOn.format(DISPLAY);
        report.previousFyAsOnLabel = previousFyAsOn.format(DISPLAY);
        report.minMonth = minMonth;
        report.maxMonth = maxMonth;
        report.rows = rows;
        report.note = "Above Data is based on physical progress reported by States/UTs on DILRMP MIS Portal.";
        return report;
    }

    private static LocalDate asOnForMonth(YearMonth month, LocalDate today) {
        LocalDate end = month.atEndOfMonth();
        return end.isAfter(today) ? today : end;
    }

    private NationalTotals nationalTotals(LocalDate asOnDate) {
        Snapshot<?> clrSnap = stateReportsAsOnDateService.getReportAsOn(Component.CLR, asOnDate);
        Snapshot<?> mapSnap = stateReportsAsOnDateService.getReportAsOn(Component.MAP, asOnDate);
        StateClrReportView clr = first(clrSnap);
        MapDigitizationReport map = first(mapSnap);

        NationalTotals totals = new NationalTotals();
        if (clr != null) {
            totals.totalRor = toLong(clr.getTotalRor());
            totals.rorComputerized = toLong(clr.getRorComputerized());
        }
        if (map != null) {
            totals.totalCadastralMaps = toLong(map.getTotalCadastralMaps());
            totals.digitizedCadastralMaps = toLong(map.getDigitizedCadastralMaps());
            totals.totalLandParcels = toLong(map.getTotalLandParcels());
            totals.landParcelsWithUlipn = toLong(map.getLandParcelsWithUlipn());
        }
        return totals;
    }

    @SuppressWarnings("unchecked")
    private static <T> T first(Snapshot<?> snapshot) {
        if (snapshot == null || snapshot.getGrandTotal() == null || snapshot.getGrandTotal().isEmpty()) {
            return null;
        }
        return (T) snapshot.getGrandTotal().get(0);
    }

    private static Long toLong(Number value) {
        return value == null ? null : value.longValue();
    }

    private static Row row(String sno, String indicator, Long previous, Long current, Long previousFy) {
        Row row = new Row();
        row.sno = sno;
        row.indicator = indicator;
        row.previousMonth = previous;
        row.currentMonth = current;
        row.currentMonthPreviousFy = previousFy;
        // Stock indicators: YTD is the same as the month-end national total.
        row.ytdCurrentFy = current;
        row.ytdPreviousFy = previousFy;
        row.previousMonthFormatted = format(previous);
        row.currentMonthFormatted = format(current);
        row.currentMonthPreviousFyFormatted = format(previousFy);
        row.ytdCurrentFyFormatted = format(current);
        row.ytdPreviousFyFormatted = format(previousFy);
        row.percentChange = percentChange(current, previousFy);
        row.percentChangeFormatted = formatPercent(row.percentChange);
        return row;
    }

    private static BigDecimal percentChange(Long current, Long previous) {
        if (current == null || previous == null || previous == 0L) {
            return null;
        }
        return BigDecimal.valueOf(current - previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(previous), 2, RoundingMode.HALF_UP);
    }

    private static String format(Long value) {
        if (value == null) {
            return "-";
        }
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
            String formatted = NumberFormatterUtil.formatWithCommas(value.intValue());
            return formatted != null ? formatted : String.valueOf(value);
        }
        return formatLongIndian(value);
    }

    private static String formatLongIndian(long value) {
        boolean negative = value < 0;
        String digits = String.valueOf(Math.abs(value));
        StringBuilder out = new StringBuilder();
        int a = 0;
        int b = 0;
        for (int i = digits.length() - 1; i >= 0; i--) {
            if (a < 3) {
                out.append(digits.charAt(i));
                a++;
            } else if (b < 2) {
                if (b == 0) {
                    out.append(',');
                }
                out.append(digits.charAt(i));
                b++;
                if (b == 2) {
                    b = 0;
                }
            }
        }
        if (negative) {
            out.append('-');
        }
        return out.reverse().toString();
    }

    private static String formatPercent(BigDecimal value) {
        if (value == null) {
            return "-";
        }
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
    }

    private static final class NationalTotals {
        private Long totalRor;
        private Long rorComputerized;
        private Long totalCadastralMaps;
        private Long digitizedCadastralMaps;
        private Long totalLandParcels;
        private Long landParcelsWithUlipn;
    }

    public static class Report {
        public YearMonth month;
        public String monthLabel;
        public LocalDate previousAsOn;
        public LocalDate currentAsOn;
        public LocalDate previousFyAsOn;
        public String previousAsOnLabel;
        public String currentAsOnLabel;
        public String previousFyAsOnLabel;
        public YearMonth minMonth;
        public YearMonth maxMonth;
        public List<Row> rows;
        public String note;
    }

    public static class Row {
        public String sno;
        public String indicator;
        public Long previousMonth;
        public Long currentMonth;
        public Long currentMonthPreviousFy;
        public Long ytdCurrentFy;
        public Long ytdPreviousFy;
        public String previousMonthFormatted;
        public String currentMonthFormatted;
        public String currentMonthPreviousFyFormatted;
        public String ytdCurrentFyFormatted;
        public String ytdPreviousFyFormatted;
        public BigDecimal percentChange;
        public String percentChangeFormatted;
    }
}
