package in.gov.dilrmp.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Picks the data-entry values that applied to each state on a past date.
 * Entry backups are a change log (a row is saved only on days the state edited its form), so:
 * the latest history row on/before the date wins; otherwise the current entry if it was not edited
 * after the date; otherwise the earliest known values, flagged as approximate.
 * Rows are positional: [0] = state_id, [1] = history/last-update date, [2..] = values.
 */
public final class AsOnDateEntryResolver {

    public static final class Resolved {
        private final Object[] row;
        private final boolean approximate;

        Resolved(Object[] row, boolean approximate) {
            this.row = row;
            this.approximate = approximate;
        }

        public Object value(int valueIndex) {
            return row[valueIndex + 2];
        }

        public boolean isApproximate() {
            return approximate;
        }
    }

    private AsOnDateEntryResolver() {
    }

    /** History rows must be ordered by state, date, id. */
    public static Map<Long, Resolved> resolve(List<Object[]> history, List<Object[]> current, LocalDate asOnDate) {
        Map<Long, List<Object[]>> historyByState = new HashMap<>();
        for (Object[] r : history) {
            historyByState.computeIfAbsent(toLong(r[0]), k -> new ArrayList<>()).add(r);
        }
        Map<Long, Object[]> currentByState = new HashMap<>();
        for (Object[] r : current) {
            currentByState.put(toLong(r[0]), r);
        }

        Set<Long> stateIds = new HashSet<>(historyByState.keySet());
        stateIds.addAll(currentByState.keySet());

        Map<Long, Resolved> result = new HashMap<>();
        for (Long stateId : stateIds) {
            List<Object[]> rows = historyByState.getOrDefault(stateId, Collections.emptyList());
            Object[] latestOnOrBefore = null;
            for (Object[] h : rows) {
                LocalDate d = toLocalDate(h[1]);
                if (d != null && !d.isAfter(asOnDate)) {
                    latestOnOrBefore = h;
                }
            }
            if (latestOnOrBefore != null) {
                result.put(stateId, new Resolved(latestOnOrBefore, false));
                continue;
            }
            Object[] cur = currentByState.get(stateId);
            if (cur != null) {
                LocalDate lastEdit = toLocalDate(cur[1]);
                if (lastEdit == null || !lastEdit.isAfter(asOnDate)) {
                    result.put(stateId, new Resolved(cur, false));
                    continue;
                }
            }
            result.put(stateId, new Resolved(rows.isEmpty() ? cur : rows.get(0), true));
        }
        return result;
    }

    public static Long toLong(Object o) {
        return o == null ? null : ((Number) o).longValue();
    }

    public static Integer toInteger(Object o) {
        return o == null ? null : ((Number) o).intValue();
    }

    public static LocalDate toLocalDate(Object o) {
        if (o == null) return null;
        if (o instanceof LocalDate) return (LocalDate) o;
        if (o instanceof java.sql.Date) return ((java.sql.Date) o).toLocalDate();
        if (o instanceof Timestamp) return ((Timestamp) o).toLocalDateTime().toLocalDate();
        if (o instanceof LocalDateTime) return ((LocalDateTime) o).toLocalDate();
        return LocalDate.parse(o.toString().substring(0, 10));
    }

    public static java.util.Date toUtilDate(Object o) {
        if (o == null) return null;
        if (o instanceof java.util.Date) return (java.util.Date) o;
        if (o instanceof LocalDateTime) return Timestamp.valueOf((LocalDateTime) o);
        if (o instanceof LocalDate) return java.sql.Date.valueOf((LocalDate) o);
        return null;
    }
}
