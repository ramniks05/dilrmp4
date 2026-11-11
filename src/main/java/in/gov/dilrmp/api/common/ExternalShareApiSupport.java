package in.gov.dilrmp.api.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shared helpers for PRAYAS / OGD external share APIs.
 */
public final class ExternalShareApiSupport {

    private ExternalShareApiSupport() {
    }

    public static String asOnDate() {
        return LocalDate.now().toString();
    }

    public static int intValue(Number value) {
        return value == null ? 0 : value.intValue();
    }

    public static long longValue(Number value) {
        return value == null ? 0L : value.longValue();
    }

    public static String percent(Number completed, Number total) {
        int c = intValue(completed);
        int t = intValue(total);
        if (t <= 0) {
            return "0.00";
        }
        return BigDecimal.valueOf(c * 100.0 / t).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String percent(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String percent(Double value) {
        if (value == null) {
            return "0.00";
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static Map<String, Object> base(String platform, String dataset) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("platform", platform);
        body.put("dataset", dataset);
        body.put("asOnDate", asOnDate());
        body.put("source", "DILRMP-MIS 4.0");
        return body;
    }

    public static boolean isNationalStateId(Number stateId) {
        return stateId != null && stateId.longValue() == 999L;
    }

    public static boolean isNationalLgd(Number lgdCode) {
        return lgdCode != null && lgdCode.intValue() == 999;
    }
}
