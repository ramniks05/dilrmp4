package in.gov.dilrmp.services.site;

import in.gov.dilrmp.models.reportDTO.legacy.LegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.LegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.sroModernization.SroModernizationReport;
import in.gov.dilrmp.services.physicalProgressServices.LegacyDigitizationReportService;
import in.gov.dilrmp.services.physicalProgressServices.LegacyRevenueReportService;
import in.gov.dilrmp.services.physicalProgressServices.SroModernizationReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/** Aggregate, public-safe figures for the home page. All queries are read-only and cached. */
@Service
public class HomeInsightsService {

    private static final Logger log = LoggerFactory.getLogger(HomeInsightsService.class);
    private static final long CACHE_MILLIS = 15 * 60 * 1000L;
    private static final long NATIONAL = 999L;

    /** {group, label, view, column} */
    private static final String[][] SERVICES = {
            {"Land records", "RoR available online", "state_clr_report_view", "ror_available_online"},
            {"Land records", "Digitally signed RoR", "state_clr_report_view", "digitally_signed_ror_available"},
            {"Land records", "Online mutation", "state_clr_report_view", "online_mutation_facility"},
            {"Land records", "Auto mutation after registration", "state_clr_report_view", "auto_trigger_mutation"},
            {"Land records", "Land records mobile app", "state_clr_report_view", "mobile_app_available"},
            {"Registration", "Circle rates visible to public", "state_registration_report_view", "circle_rates_visible_to_public"},
            {"Registration", "e-Stamp integration", "state_registration_report_view", "integration_with_e_stamp_system"},
            {"Registration", "e-KYC of parties", "state_registration_report_view", "ekyc_option_available"},
            {"Registration", "Online grievance redressal", "state_registration_report_view", "online_grievance_redressal_system"},
            {"Registration", "Digital signatures", "state_registration_report_view", "signatures_obtained_digitally"},
            {"Registration", "Documents in DigiLocker", "state_registration_report_view", "store_documents_in_digilocker"},
            {"Revenue courts", "e-RCMS available", "rcms_report_view", "e_rcms_available"},
            {"Revenue courts", "Cause list online", "rcms_report_view", "cause_list_generation_online"},
            {"Revenue courts", "Court orders uploaded online", "rcms_report_view", "uploading_revenue_court_orders_online"},
            {"Revenue courts", "Paperless proceedings", "rcms_report_view", "revenue_court_proceedings_paperless"},
            {"Revenue courts", "Notices to defendants online", "rcms_report_view", "notice_to_defendants_online"}
    };

    /** {key, label, view, key column, percent column} */
    private static final String[][] COMPONENTS = {
            {"clr", "Computerization of Land Records (villages)", "state_clr_report_view", "state_id", "clr_completion_percent"},
            {"maps", "Digitization of Maps / FMBs / Tippans", "map_digitization_report_view", "lgd_code", "digitized_maps_fmb_tippans_percent"},
            {"mapsRor", "Integration of RoR with cadastral maps", "map_digitization_report_view", "lgd_code", "villages_linked_with_ror_percent"},
            {"sro", "Computerization of Registration Process (SROs)", "state_registration_report_view", "state_id", "online_registration_percent"},
            {"mrr", "Modern Record Room (Tehsil)", "mrr_report_view", "state_id", "mrr_completed_out_of_sanctioned_percent"},
            {"survey", "Survey / Re-survey", "survey_resurvey_report_view", "state_id", "survey_completed_village_percent"},
            {"rcms", "Computerization of Revenue Courts", "rcms_report_view", "state_id", "revenue_courts_computerized_percent"},
            {"aadhaar", "Consent-based Aadhaar integration with RoR", "linked_aadhaar_report_view", "state_id", "villages_with_100_percent_ror_linked_aadhaar_percent"}
    };

    /** {label, backup table, key column, percent column} */
    private static final String[][] TRENDS = {
            {"Computerization of Land Records (villages)", "state_clr_report_view_backup", "state_id", "clr_completion_percent"},
            {"Maps / FMBs / Tippans digitized", "map_digitization_report_view_backup", "lgd_code", "digitized_maps_fmb_tippans_percent"},
            {"Computerization of Registration Process (SROs)", "state_registration_report_view_backup", "state_id", "online_registration_percent"},
            {"Modern Record Room (Tehsil)", "mrr_report_view_backup", "state_id", "mrr_completed_out_of_sanctioned_percent"},
            {"Survey / Re-survey", "survey_resurvey_report_view_backup", "state_id", "survey_completed_village_percent"},
            {"Computerization of Revenue Courts", "rcms_report_view_backup", "state_id", "revenue_courts_computerized_percent"},
            {"Consent-based Aadhaar integration", "linked_aadhaar_report_view_backup", "state_id", "villages_with100percent_ror_linked_aadhaar_percent"}
    };

    /** Map component ids used by statemap.js: {map id, label, state view, state key, state column, district view, district column}.
        A null district view means the component is reported at State/UT level only. */
    private static final String[][] MAP_COMPONENTS = {
            {"clr", "Computerization of Land Records", "state_clr_report_view", "state_id", "clr_completion_percent",
                    "district_clr_report_view", "clr_completion_percent"},
            {"MapDigitization", "Digitization of Maps / FMBs / Tippans", "map_digitization_report_view", "lgd_code", "digitized_maps_fmb_tippans_percent",
                    "district_map_digitization_report_view", "digitized_maps_fmb_tippans_percent"},
            {"sro", "Computerization of Registration Process (SROs)", "state_registration_report_view", "state_id", "online_registration_percent",
                    null, null},
            {"mrr", "Modern Record Room (Tehsil)", "mrr_report_view", "state_id", "mrr_completed_out_of_sanctioned_percent",
                    "district_mrr_report_view", "mrr_completed_out_of_sanctioned_percent"},
            {"surveyReservey", "Survey / Re-survey", "survey_resurvey_report_view", "state_id", "survey_completed_village_percent",
                    "district_survey_resurvey_report_view", "survey_completed_village_percent"},
            {"rccms", "Computerization of Revenue Courts", "rcms_report_view", "state_id", "revenue_courts_computerized_percent",
                    "district_rcms_report_view", "revenue_courts_computerized_percent"},
            {"linkedwithaadhaar", "Consent-based Aadhaar integration with RoR", "linked_aadhaar_report_view", "state_id", "villages_with_100_percent_ror_linked_aadhaar_percent",
                    "district_linked_aadhaar_report_view", "villages_with_100_percent_ror_linked_aadhaar_percent"}
    };

    /** District MIS sums for the components without a report view: {map id, label, numerator, denominator}. */
    private static final String[][] MIS_COMPONENTS = {
            {"legacyRegistered", "Scanning of Legacy Registered Documents", "legacy_total_digitised_pages", "legacy_total_pages"},
            {"legacyRevenue", "Scanning of Legacy Revenue Records", "coalesce(revenue_legacy_digitised_state_funds_pages, 0) + coalesce(revenue_legacy_digitised_dilrmp_funds_pages, 0)",
                    "revenue_legacy_total_pages"},
            {"sroModernization", "Modernization of SROs", "coalesce(sros_modernised_state_funds, 0) + coalesce(sros_modernised_dilrmp_funds, 0)", "sro_computerized"}
    };

    /** Order of components in the State/UT and district panels; SRO components come last as in the District MIS form. */
    private static final List<String> PANEL_ORDER = java.util.Arrays.asList("clr", "MapDigitization", "mrr", "surveyReservey", "rccms",
            "linkedwithaadhaar", "legacyRegistered", "legacyRevenue", "sro", "sroModernization");

    private static void sortForPanel(List<Map<String, Object>> components) {
        components.sort(Comparator.comparingInt(c -> PANEL_ORDER.indexOf(String.valueOf(c.get("key")))));
    }

    private final Map<Long, Object[]> stateCache = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<Long, Object[]> districtCache = new java.util.concurrent.ConcurrentHashMap<>();

    private final JdbcTemplate jdbc;
    private final SroModernizationReportService sroModernizationReportService;
    private final LegacyDigitizationReportService legacyDigitizationReportService;
    private final LegacyRevenueReportService legacyRevenueReportService;

    private volatile Map<String, Object> cached;
    private volatile long cachedAt;

    public HomeInsightsService(JdbcTemplate jdbc,
                               SroModernizationReportService sroModernizationReportService,
                               LegacyDigitizationReportService legacyDigitizationReportService,
                               LegacyRevenueReportService legacyRevenueReportService) {
        this.jdbc = jdbc;
        this.sroModernizationReportService = sroModernizationReportService;
        this.legacyDigitizationReportService = legacyDigitizationReportService;
        this.legacyRevenueReportService = legacyRevenueReportService;
    }

    public synchronized Map<String, Object> insights() {
        if (cached != null && System.currentTimeMillis() - cachedAt < CACHE_MILLIS) {
            return cached;
        }
        Map<Long, String> stateNames = stateNames();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("asOn", LocalDate.now().toString());
        out.put("services", safe("services", () -> services(stateNames)));
        out.put("survey", safe("survey", this::survey));
        out.put("chain", safe("chain", this::chain));
        out.put("women", safe("women", this::women));
        out.put("leaders", safe("leaders", () -> leaders(stateNames)));
        out.put("trend", safe("trend", this::trend));
        out.put("freshness", safe("freshness", () -> freshness(stateNames)));
        out.put("modernisation", safe("modernisation", this::modernisation));
        out.put("safeguards", safe("safeguards", this::safeguards));
        out.put("map", safe("map", () -> mapPercents(stateNames)));
        cached = out;
        cachedAt = System.currentTimeMillis();
        return out;
    }

    public static boolean isMapComponent(String mapId) {
        return mapComponent(mapId) != null || misComponent(mapId) != null;
    }

    private static String[] misComponent(String mapId) {
        for (String[] m : MIS_COMPONENTS) {
            if (m[0].equals(mapId)) {
                return m;
            }
        }
        return null;
    }

    /** Per-State/UT percentage of a District MIS component, plus All India under key 999. */
    private Map<Long, Double> misStatePercents(String[] m) {
        Map<Long, Double> values = new LinkedHashMap<>();
        jdbc.queryForList("select state_id, sum(" + m[2] + ") num, sum(" + m[3] + ") den from district_mis_data_entry group by state_id")
                .forEach(r -> {
                    Double p = ratio(r.get("num"), r.get("den"));
                    if (r.get("state_id") != null && p != null) {
                        values.put(toLong(r.get("state_id")), p);
                    }
                });
        Map<String, Object> india = jdbc.queryForMap("select sum(" + m[2] + ") num, sum(" + m[3] + ") den from district_mis_data_entry");
        Double p = ratio(india.get("num"), india.get("den"));
        if (p != null) {
            values.put(NATIONAL, p);
        }
        return values;
    }

    private static String[] mapComponent(String mapId) {
        for (String[] c : MAP_COMPONENTS) {
            if (c[0].equals(mapId)) {
                return c;
            }
        }
        return null;
    }

    /** State/UT list and per-State/UT percentage of every map component, for search and the clickable legend. */
    private Map<String, Object> mapPercents(Map<Long, String> stateNames) {
        List<Map<String, Object>> states = new ArrayList<>();
        stateNames.forEach((id, name) -> {
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("id", id);
            s.put("name", name);
            states.add(s);
        });
        Map<String, Map<Long, Double>> percents = new LinkedHashMap<>();
        for (String[] c : MAP_COMPONENTS) {
            Map<Long, Double> values = new LinkedHashMap<>();
            jdbc.queryForList("select " + c[3] + " id, " + c[4] + " pct from " + c[2] + " where " + c[3] + " <> 999")
                    .forEach(r -> {
                        if (r.get("id") != null && r.get("pct") != null) {
                            values.put(toLong(r.get("id")), round2(toDouble(r.get("pct"))));
                        }
                    });
            percents.put(c[0], values);
        }
        for (String[] m : MIS_COMPONENTS) {
            Map<Long, Double> values = misStatePercents(m);
            values.remove(NATIONAL);
            percents.put(m[0], values);
        }
        List<String> stateLevelOnly = new ArrayList<>();
        for (String[] c : MAP_COMPONENTS) {
            if (c[5] == null) {
                stateLevelOnly.add(c[0]);
            }
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("states", states);
        out.put("percents", percents);
        out.put("stateLevelOnly", stateLevelOnly);
        return out;
    }

    /** Everything the details panel shows for one State/UT. Returns null for an unknown id. */
    public Map<String, Object> stateDetail(long stateId) {
        Object[] hit = stateCache.get(stateId);
        if (hit != null && System.currentTimeMillis() - (Long) hit[0] < CACHE_MILLIS) {
            @SuppressWarnings("unchecked")
            Map<String, Object> cachedDetail = (Map<String, Object>) hit[1];
            return cachedDetail;
        }
        List<Map<String, Object>> clrRows = jdbc.queryForList("select * from state_clr_report_view where state_id = ? and state_id <> 999", stateId);
        if (clrRows.isEmpty()) {
            return null;
        }
        Map<String, Object> clr = clrRows.get(0);

        List<Map<String, Object>> components = new ArrayList<>();
        for (String[] c : MAP_COMPONENTS) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("key", c[0]);
            item.put("label", c[1]);
            item.put("onMap", true);
            item.put("state", percentOf(c[2], c[3], c[4], stateId));
            item.put("india", percentOf(c[2], c[3], c[4], NATIONAL));
            components.add(item);
        }
        for (int k = 0; k < MIS_COMPONENTS.length; k++) {
            String[] m = MIS_COMPONENTS[k];
            Map<String, Object> state = jdbc.queryForMap("select sum(" + m[2] + ") num, sum(" + m[3] + ") den from district_mis_data_entry where state_id = ?", stateId);
            Map<String, Object> india = jdbc.queryForMap("select sum(" + m[2] + ") num, sum(" + m[3] + ") den from district_mis_data_entry");
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("key", m[0]);
            item.put("label", m[1]);
            item.put("onMap", true);
            item.put("state", ratio(state.get("num"), state.get("den")));
            item.put("india", ratio(india.get("num"), india.get("den")));
            components.add(item);
        }
        sortForPanel(components);

        Map<String, Map<String, Object>> serviceRows = new LinkedHashMap<>();
        for (String view : new String[]{"state_clr_report_view", "state_registration_report_view", "rcms_report_view"}) {
            List<Map<String, Object>> rows = jdbc.queryForList("select * from " + view + " where state_id = ?", stateId);
            serviceRows.put(view, rows.isEmpty() ? new LinkedHashMap<>() : rows.get(0));
        }
        List<Map<String, Object>> services = new ArrayList<>();
        for (String[] s : SERVICES) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("group", s[0]);
            item.put("label", s[1]);
            item.put("yes", "YES".equalsIgnoreCase(String.valueOf(serviceRows.get(s[2]).get(s[3])).trim()));
            services.add(item);
        }

        Long totalDistricts = jdbc.queryForObject("select count(*) from district where state_id = ?", Long.class, stateId);
        Map<String, Object> reported = jdbc.queryForMap("select count(*) n, max(last_update_on_date)::date last_upd from district_mis_data_entry where state_id = ?", stateId);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("id", stateId);
        out.put("name", clr.get("state_name"));
        out.put("districts", toLong(clr.get("total_district")));
        out.put("tehsils", toLong(clr.get("total_tehsils")));
        out.put("villages", toLong(clr.get("total_villages")));
        out.put("components", components);
        out.put("services", services);
        out.put("districtsTotal", totalDistricts == null ? 0 : totalDistricts);
        out.put("districtsReported", toLong(reported.get("n")));
        out.put("lastUpdate", reported.get("last_upd") == null ? null : reported.get("last_upd").toString());
        stateCache.put(stateId, new Object[]{System.currentTimeMillis(), out});
        return out;
    }

    /** Component progress of one district (by LGD code) against its State/UT and All India. Returns null if unknown. */
    public Map<String, Object> districtDetail(long districtLgd) {
        Object[] hit = districtCache.get(districtLgd);
        if (hit != null && System.currentTimeMillis() - (Long) hit[0] < CACHE_MILLIS) {
            @SuppressWarnings("unchecked")
            Map<String, Object> cachedDetail = (Map<String, Object>) hit[1];
            return cachedDetail;
        }
        List<Map<String, Object>> rows = jdbc.queryForList("select district_id, district_name, state_id, state_name, total_tehsils, total_villages"
                + " from district_clr_report_view where district_lgd_code = ? limit 1", districtLgd);
        if (rows.isEmpty()) {
            return null;
        }
        Map<String, Object> d = rows.get(0);
        long districtId = toLong(d.get("district_id"));
        long stateId = toLong(d.get("state_id"));

        List<Map<String, Object>> components = new ArrayList<>();
        for (String[] c : MAP_COMPONENTS) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("key", c[0]);
            item.put("label", c[1]);
            item.put("onMap", true);
            if (c[5] == null) {
                item.put("district", null);
                item.put("stateLevelOnly", true);
            } else {
                List<Map<String, Object>> v = jdbc.queryForList("select " + c[6] + " pct from " + c[5] + " where district_lgd_code = ?", districtLgd);
                item.put("district", v.isEmpty() || v.get(0).get("pct") == null ? null : round2(toDouble(v.get(0).get("pct"))));
            }
            item.put("state", percentOf(c[2], c[3], c[4], stateId));
            item.put("india", percentOf(c[2], c[3], c[4], NATIONAL));
            components.add(item);
        }
        for (int k = 0; k < MIS_COMPONENTS.length; k++) {
            String[] m = MIS_COMPONENTS[k];
            String sums = "select sum(" + m[2] + ") num, sum(" + m[3] + ") den from district_mis_data_entry";
            Map<String, Object> district = jdbc.queryForMap(sums + " where district_id = ?", districtId);
            Map<String, Object> state = jdbc.queryForMap(sums + " where state_id = ?", stateId);
            Map<String, Object> india = jdbc.queryForMap(sums);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("key", m[0]);
            item.put("label", m[1]);
            item.put("onMap", true);
            item.put("district", ratio(district.get("num"), district.get("den")));
            item.put("state", ratio(state.get("num"), state.get("den")));
            item.put("india", ratio(india.get("num"), india.get("den")));
            components.add(item);
        }
        sortForPanel(components);
        Map<String, Object> mis = jdbc.queryForMap("select max(last_update_on_date)::date last_upd, count(*) n from district_mis_data_entry where district_id = ?", districtId);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("lgd", districtLgd);
        out.put("name", d.get("district_name"));
        out.put("stateId", stateId);
        out.put("stateName", d.get("state_name"));
        out.put("tehsils", toLong(d.get("total_tehsils")));
        out.put("villages", toLong(d.get("total_villages")));
        out.put("reported", toLong(mis.get("n")) > 0);
        out.put("lastUpdate", mis.get("last_upd") == null ? null : mis.get("last_upd").toString());
        out.put("components", components);
        districtCache.put(districtLgd, new Object[]{System.currentTimeMillis(), out});
        return out;
    }

    /** District-wise percentage of one map component within a State/UT, highest first. */
    public List<Map<String, Object>> districtPercents(long stateId, String mapId) {
        List<Map<String, Object>> out = new ArrayList<>();
        String[] m = misComponent(mapId);
        if (m != null) {
            jdbc.queryForList("select d.name district_name, d.lgd_code, sum(" + m[2] + ") num, sum(" + m[3] + ") den"
                    + " from district_mis_data_entry e join district d on d.id = e.district_id"
                    + " where e.state_id = ? group by d.name, d.lgd_code", stateId)
                    .forEach(r -> {
                        Map<String, Object> d = new LinkedHashMap<>();
                        d.put("name", r.get("district_name"));
                        d.put("lgd", r.get("lgd_code"));
                        d.put("percent", ratio(r.get("num"), r.get("den")));
                        out.add(d);
                    });
            out.sort(Comparator.comparing((Map<String, Object> d) -> (Double) d.get("percent"), Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(d -> String.valueOf(d.get("name"))));
            return out;
        }
        String[] c = mapComponent(mapId);
        if (c == null || c[5] == null) {
            return out;
        }
        jdbc.queryForList("select district_name, district_lgd_code, " + c[6] + " pct from " + c[5] + " where state_id = ? order by " + c[6] + " desc nulls last, district_name", stateId)
                .forEach(r -> {
                    Map<String, Object> d = new LinkedHashMap<>();
                    d.put("name", r.get("district_name"));
                    d.put("lgd", r.get("district_lgd_code"));
                    d.put("percent", r.get("pct") == null ? null : round2(toDouble(r.get("pct"))));
                    out.add(d);
                });
        return out;
    }

    private Double percentOf(String view, String keyColumn, String column, long id) {
        List<Map<String, Object>> rows = jdbc.queryForList("select " + column + " pct from " + view + " where " + keyColumn + " = ?", id);
        if (rows.isEmpty() || rows.get(0).get("pct") == null) {
            return null;
        }
        return round2(toDouble(rows.get(0).get("pct")));
    }

    private static Double ratio(Object numerator, Object denominator) {
        double d = toDouble(denominator);
        return d > 0 ? round2(Math.min(100.0, toDouble(numerator) * 100.0 / d)) : null;
    }

    /** State/UT-wise component percentages for the open-data download. */
    public List<String[]> stateProgressRows() {
        Map<Long, String> names = stateNames();
        Map<String, Map<Long, Double>> byComponent = componentPercents();
        List<String[]> rows = new ArrayList<>();
        String[] header = new String[COMPONENTS.length + 1];
        header[0] = "State/UT";
        for (int i = 0; i < COMPONENTS.length; i++) {
            header[i + 1] = COMPONENTS[i][1] + " (%)";
        }
        rows.add(header);
        List<Long> ids = new ArrayList<>(names.keySet());
        ids.sort(Comparator.comparing(names::get));
        ids.add(NATIONAL);
        for (Long id : ids) {
            String[] row = new String[COMPONENTS.length + 1];
            row[0] = id == NATIONAL ? "All India" : names.get(id);
            for (int i = 0; i < COMPONENTS.length; i++) {
                Double v = byComponent.get(COMPONENTS[i][0]).get(id);
                row[i + 1] = v == null ? "" : String.format("%.2f", v);
            }
            rows.add(row);
        }
        return rows;
    }

    private Map<Long, String> stateNames() {
        Map<Long, String> names = new LinkedHashMap<>();
        jdbc.queryForList("select state_id, state_name from state_clr_report_view where state_id <> 999 order by state_name")
                .forEach(r -> names.put(toLong(r.get("state_id")), String.valueOf(r.get("state_name"))));
        return names;
    }

    private Map<String, Object> services(Map<Long, String> stateNames) {
        Map<String, Map<Long, Map<String, Object>>> rowsByView = new LinkedHashMap<>();
        for (String view : new String[]{"state_clr_report_view", "state_registration_report_view", "rcms_report_view"}) {
            Map<Long, Map<String, Object>> rows = new LinkedHashMap<>();
            jdbc.queryForList("select * from " + view + " where state_id <> 999")
                    .forEach(r -> rows.put(toLong(r.get("state_id")), r));
            rowsByView.put(view, rows);
        }

        List<Map<String, Object>> serviceList = new ArrayList<>();
        int[] counts = new int[SERVICES.length];
        List<Map<String, Object>> states = new ArrayList<>();
        for (Map.Entry<Long, String> state : stateNames.entrySet()) {
            int[] flags = new int[SERVICES.length];
            for (int i = 0; i < SERVICES.length; i++) {
                Map<String, Object> row = rowsByView.get(SERVICES[i][2]).get(state.getKey());
                boolean yes = row != null && "YES".equalsIgnoreCase(String.valueOf(row.get(SERVICES[i][3])).trim());
                flags[i] = yes ? 1 : 0;
                counts[i] += flags[i];
            }
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("name", state.getValue());
            s.put("flags", flags);
            states.add(s);
        }
        for (int i = 0; i < SERVICES.length; i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("group", SERVICES[i][0]);
            item.put("label", SERVICES[i][1]);
            item.put("count", counts[i]);
            serviceList.add(item);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalStates", stateNames.size());
        out.put("items", serviceList);
        out.put("states", states);
        return out;
    }

    private Map<String, Object> survey() {
        Map<String, Object> r = jdbc.queryForMap("select total_villages, villages_survey_sanction_not_started, villages_drone_flying_completed,"
                + " villages_map1_generated, villages_draft_map_published, villages_final_promulgation_done,"
                + " total_rural_revenue_area, area_sanctioned_for_survey, area_drone_flying_completed"
                + " from survey_resurvey_report_view where state_id = 999");
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalVillages", toLong(r.get("total_villages")));
        out.put("notStarted", toLong(r.get("villages_survey_sanction_not_started")));
        List<Map<String, Object>> stages = new ArrayList<>();
        stages.add(stage("Drone flying completed", toLong(r.get("villages_drone_flying_completed"))));
        stages.add(stage("Map-1 generated", toLong(r.get("villages_map1_generated"))));
        stages.add(stage("Draft map published", toLong(r.get("villages_draft_map_published"))));
        stages.add(stage("Final promulgation", toLong(r.get("villages_final_promulgation_done"))));
        out.put("stages", stages);
        out.put("totalArea", toDouble(r.get("total_rural_revenue_area")));
        out.put("areaSanctioned", toDouble(r.get("area_sanctioned_for_survey")));
        out.put("areaFlown", toDouble(r.get("area_drone_flying_completed")));
        return out;
    }

    private Map<String, Object> chain() {
        Map<String, Object> clr = jdbc.queryForMap("select total_ror, ror_computerized from state_clr_report_view where state_id = 999");
        Map<String, Object> map = jdbc.queryForMap("select total_maps_fmb_tippans, total_digitized_maps_fmb_tippans, villages_linked_with_ror,"
                + " total_villages, total_land_parcels, georeferenced_land_parcels, land_parcels_with_ulipn"
                + " from map_digitization_report_view where lgd_code = 999");
        Map<String, Object> aadhaar = jdbc.queryForMap("select total_ror, ror_linked_with_aadhaar, ror_linked_with_mobile_number"
                + " from linked_aadhaar_report_view where state_id = 999");
        List<Map<String, Object>> steps = new ArrayList<>();
        steps.add(step("Records of Rights computerized", "RoRs", clr.get("ror_computerized"), clr.get("total_ror")));
        steps.add(step("Maps / FMBs / Tippans digitized", "maps / FMBs / tippans", map.get("total_digitized_maps_fmb_tippans"), map.get("total_maps_fmb_tippans")));
        steps.add(step("RoR integrated with cadastral maps", "villages", map.get("villages_linked_with_ror"), map.get("total_villages")));
        steps.add(step("Parcels geo-referenced", "land parcels", map.get("georeferenced_land_parcels"), map.get("total_land_parcels")));
        steps.add(step("Parcels with Bhu-Aadhaar (ULPIN)", "land parcels", map.get("land_parcels_with_ulipn"), map.get("total_land_parcels")));
        steps.add(step("RoR linked with Aadhaar (consent-based)", "RoRs", aadhaar.get("ror_linked_with_aadhaar"), aadhaar.get("total_ror")));
        steps.add(step("RoR linked with mobile number", "RoRs", aadhaar.get("ror_linked_with_mobile_number"), aadhaar.get("total_ror")));
        return Map.of("steps", steps);
    }

    private Map<String, Object> women() {
        Map<String, Object> n = jdbc.queryForMap("select total_female_land_owners f, total_male_land_owners m from state_clr_report_view where state_id = 999");
        List<Map<String, Object>> top = new ArrayList<>();
        jdbc.queryForList("select state_name, total_female_land_owners f, total_male_land_owners m from state_clr_report_view"
                + " where state_id <> 999 and coalesce(total_female_land_owners, 0) + coalesce(total_male_land_owners, 0) > 0"
                + " order by total_female_land_owners::numeric / (total_female_land_owners + total_male_land_owners) desc limit 5")
                .forEach(r -> {
                    long f = toLong(r.get("f"));
                    long m = toLong(r.get("m"));
                    Map<String, Object> s = new LinkedHashMap<>();
                    s.put("name", r.get("state_name"));
                    s.put("share", round2(f * 100.0 / (f + m)));
                    top.add(s);
                });
        Integer reporting = jdbc.queryForObject("select count(*) from state_clr_report_view where state_id <> 999"
                + " and coalesce(total_female_land_owners, 0) + coalesce(total_male_land_owners, 0) > 0", Integer.class);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("female", toLong(n.get("f")));
        out.put("male", toLong(n.get("m")));
        out.put("reportingStates", reporting);
        out.put("top", top);
        return out;
    }

    private Map<String, Map<Long, Double>> componentPercents() {
        Map<String, Map<Long, Double>> out = new LinkedHashMap<>();
        for (String[] c : COMPONENTS) {
            Map<Long, Double> values = new LinkedHashMap<>();
            jdbc.queryForList("select " + c[3] + " id, " + c[4] + " pct from " + c[2])
                    .forEach(r -> {
                        if (r.get("id") != null && r.get("pct") != null) {
                            values.put(toLong(r.get("id")), toDouble(r.get("pct")));
                        }
                    });
            out.put(c[0], values);
        }
        return out;
    }

    private List<Map<String, Object>> leaders(Map<Long, String> stateNames) {
        Map<String, Map<Long, Double>> byComponent = componentPercents();
        List<Map<String, Object>> out = new ArrayList<>();
        for (String[] c : COMPONENTS) {
            Map<Long, Double> values = byComponent.get(c[0]);
            List<String> complete = new ArrayList<>();
            int above90 = 0;
            for (Map.Entry<Long, String> s : stateNames.entrySet()) {
                Double v = values.get(s.getKey());
                if (v == null) {
                    continue;
                }
                if (v >= 99.5) {
                    complete.add(s.getValue());
                }
                if (v >= 90) {
                    above90++;
                }
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("key", c[0]);
            item.put("label", c[1]);
            item.put("national", values.get(NATIONAL));
            item.put("complete", complete);
            item.put("above90", above90);
            out.add(item);
        }
        for (String[] m : MIS_COMPONENTS) {
            Map<Long, Double> values = misStatePercents(m);
            List<String> complete = new ArrayList<>();
            int above90 = 0;
            for (Map.Entry<Long, String> s : stateNames.entrySet()) {
                Double v = values.get(s.getKey());
                if (v == null) {
                    continue;
                }
                if (v >= 99.5) {
                    complete.add(s.getValue());
                }
                if (v >= 90) {
                    above90++;
                }
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("key", m[0]);
            item.put("label", m[1]);
            item.put("national", values.get(NATIONAL));
            item.put("complete", complete);
            item.put("above90", above90);
            out.add(item);
        }
        return out;
    }

    private Map<String, Object> trend() {
        TreeSet<String> months = new TreeSet<>();
        List<Map<String, Object>> series = new ArrayList<>();
        for (String[] t : TRENDS) {
            Map<String, Double> points = new LinkedHashMap<>();
            jdbc.queryForList("select to_char(mon, 'YYYY-MM') mon, val from ("
                    + " select distinct on (date_trunc('month', backup_date)) date_trunc('month', backup_date) mon, " + t[3] + " val"
                    + " from " + t[1] + " where " + t[2] + " = 999"
                    + " order by date_trunc('month', backup_date), backup_date desc) x order by mon")
                    .forEach(r -> {
                        if (r.get("val") != null) {
                            points.put(String.valueOf(r.get("mon")), round2(toDouble(r.get("val"))));
                        }
                    });
            months.addAll(points.keySet());
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("label", t[0]);
            s.put("points", points);
            series.add(s);
        }
        List<String> monthList = new ArrayList<>(months);
        for (Map<String, Object> s : series) {
            @SuppressWarnings("unchecked")
            Map<String, Double> points = (Map<String, Double>) s.remove("points");
            Double[] values = new Double[monthList.size()];
            for (int i = 0; i < monthList.size(); i++) {
                values[i] = points.get(monthList.get(i));
            }
            s.put("values", values);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("months", monthList);
        out.put("series", series);
        return out;
    }

    private Map<String, Object> freshness(Map<Long, String> stateNames) {
        Map<Long, Long> totals = new LinkedHashMap<>();
        jdbc.queryForList("select state_id, count(*) n from district group by state_id")
                .forEach(r -> totals.put(toLong(r.get("state_id")), toLong(r.get("n"))));
        Map<Long, Map<String, Object>> reported = new LinkedHashMap<>();
        jdbc.queryForList("select state_id, count(*) n, max(last_update_on_date)::date last_upd from district_mis_data_entry group by state_id")
                .forEach(r -> reported.put(toLong(r.get("state_id")), r));
        Map<String, Object> recent = jdbc.queryForMap("select"
                + " count(*) filter (where last_update_on_date >= current_date - 30) d30,"
                + " count(*) filter (where last_update_on_date >= current_date - 90) d90 from district_mis_data_entry");

        long totalDistricts = 0;
        long reportedDistricts = 0;
        List<Map<String, Object>> states = new ArrayList<>();
        for (Map.Entry<Long, String> s : stateNames.entrySet()) {
            long total = totals.getOrDefault(s.getKey(), 0L);
            Map<String, Object> rep = reported.get(s.getKey());
            long done = rep == null ? 0 : Math.min(total, toLong(rep.get("n")));
            totalDistricts += total;
            reportedDistricts += done;
            Object last = rep == null ? null : rep.get("last_upd");
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", s.getValue());
            item.put("reported", done);
            item.put("total", total);
            item.put("lastUpdate", last == null ? null : last.toString());
            item.put("daysAgo", last == null ? null : ChronoUnit.DAYS.between(LocalDate.parse(last.toString()), LocalDate.now()));
            states.add(item);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalDistricts", totalDistricts);
        out.put("reportedDistricts", reportedDistricts);
        out.put("updated30", toLong(recent.get("d30")));
        out.put("updated90", toLong(recent.get("d90")));
        out.put("states", states);
        return out;
    }

    private Map<String, Object> modernisation() {
        Map<String, Object> out = new LinkedHashMap<>();
        List<SroModernizationReport> sro = sroModernizationReportService.getGrandTotalFormatted();
        if (!sro.isEmpty()) {
            SroModernizationReport s = sro.get(0);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("total", s.getTotalSros());
            m.put("online", s.getSrosUsingOnlineRegistration());
            m.put("stateFunds", s.getSrosModernisedStateFunds());
            m.put("dilrmpSanctioned", s.getSrosDilrmpSanctioned());
            m.put("dilrmpFunds", s.getSrosModernisedDilrmpFunds());
            out.put("sro", m);
        }
        List<LegacyDigitizationReport> legacy = legacyDigitizationReportService.getGrandTotalFormatted();
        if (!legacy.isEmpty()) {
            LegacyDigitizationReport l = legacy.get(0);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("total", l.getLegacyTotalPages());
            m.put("stateFunds", l.getLegacyDigitisedStateFundsPages());
            m.put("dilrmpSanctioned", l.getLegacyDilrmpSanctionedPages());
            m.put("dilrmpFunds", l.getLegacyDigitisedDilrmpFundsPages());
            m.put("digitised", l.getLegacyTotalDigitisedPages());
            m.putAll(yearRange("legacy_digitised_upto_year"));
            out.put("registered", m);
        }
        List<LegacyRevenueReport> revenue = legacyRevenueReportService.getGrandTotalFormatted();
        if (!revenue.isEmpty()) {
            LegacyRevenueReport r = revenue.get(0);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("total", r.getTotalPages());
            m.put("stateFunds", r.getDigitisedStateFundsPages());
            m.put("dilrmpSanctioned", r.getDilrmpSanctionedPages());
            m.put("dilrmpFunds", r.getDigitisedDilrmpFundsPages());
            m.put("digitised", r.getTotalDigitisedPages());
            m.putAll(yearRange("revenue_legacy_digitised_upto_year"));
            out.put("revenue", m);
        }
        return out;
    }

    private Map<String, Object> yearRange(String column) {
        Map<String, Object> r = jdbc.queryForMap("select min(" + column + ") earliest, max(" + column + ") latest, count(*) districts"
                + " from district_mis_data_entry where " + column + " between 1900 and 2100");
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("yearEarliest", r.get("earliest"));
        out.put("yearLatest", r.get("latest"));
        out.put("yearDistricts", toLong(r.get("districts")));
        return out;
    }

    /** Record linkages and legal safeguards entered in the District MIS and State MIS forms. */
    private Map<String, Object> safeguards() {
        Map<String, Object> mis = jdbc.queryForMap("select sum(total_ror) total_ror, sum(ror_with_cadastral_map) ror_map,"
                + " sum(total_land_owners) owners, sum(land_owners_aadhar) owners_aadhaar,"
                + " sum(land_owners_linked_with_mobile) owners_mobile, sum(land_owners_linked_with_address) owners_address"
                + " from district_mis_data_entry");
        Map<String, Object> clr = jdbc.queryForMap("select districts_with_bank_red_flag_mortgage, bank_branches_with_red_flag_mortgage,"
                + " bank_red_flag_mortgage_in_land_records, case_filing_red_flagged_in_land_records_from_civil_courts, total_district"
                + " from state_clr_report_view where state_id = 999");
        Map<String, Object> sro = jdbc.queryForMap("select fetch_property_details_from_land_records, revenue_court_case_red_flagged"
                + " from state_registration_report_view where state_id = 999");

        List<Map<String, Object>> linkages = new ArrayList<>();
        linkages.add(step("RoRs with cadastral map (District MIS)", "RoRs", mis.get("ror_map"), mis.get("total_ror")));
        linkages.add(step("Land owners linked with Aadhaar, consent-based (District MIS)", "land owners", mis.get("owners_aadhaar"), mis.get("owners")));
        linkages.add(step("Land owners linked with mobile (District MIS)", "land owners", mis.get("owners_mobile"), mis.get("owners")));
        linkages.add(step("Land owners linked with address (District MIS)", "land owners", mis.get("owners_address"), mis.get("owners")));

        List<Map<String, Object>> stateFlags = new ArrayList<>();
        stateFlags.add(flag("Bank mortgages red-flagged in land records", clr.get("bank_red_flag_mortgage_in_land_records")));
        stateFlags.add(flag("Civil court cases red-flagged in land records", clr.get("case_filing_red_flagged_in_land_records_from_civil_courts")));
        stateFlags.add(flag("Revenue court cases red-flagged at registration", sro.get("revenue_court_case_red_flagged")));
        stateFlags.add(flag("Registration fetches property details from land records", sro.get("fetch_property_details_from_land_records")));

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("linkages", linkages);
        out.put("stateFlags", stateFlags);
        out.put("mortgageDistricts", toLong(clr.get("districts_with_bank_red_flag_mortgage")));
        out.put("mortgageBranches", toLong(clr.get("bank_branches_with_red_flag_mortgage")));
        out.put("totalDistricts", toLong(clr.get("total_district")));
        return out;
    }

    /** The national row of a Yes/No column holds the number of States/UTs that answered Yes. */
    private static Map<String, Object> flag(String label, Object count) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("label", label);
        long n = 0;
        try {
            n = count == null ? 0 : Long.parseLong(String.valueOf(count).trim());
        } catch (NumberFormatException ignored) {
            n = 0;
        }
        m.put("states", n);
        return m;
    }

    private Object safe(String section, java.util.function.Supplier<Object> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            log.warn("Home page insights section '{}' failed: {}", section, e.getMessage());
            return null;
        }
    }

    private static Map<String, Object> stage(String label, long villages) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("label", label);
        m.put("villages", villages);
        return m;
    }

    private static Map<String, Object> step(String label, String unit, Object value, Object total) {
        long v = toLong(value);
        long t = toLong(total);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("label", label);
        m.put("unit", unit);
        m.put("value", v);
        m.put("total", t);
        m.put("percent", t > 0 ? round2(Math.min(100.0, v * 100.0 / t)) : 0.0);
        return m;
    }

    private static long toLong(Object o) {
        return o instanceof Number ? ((Number) o).longValue() : 0L;
    }

    private static double toDouble(Object o) {
        return o instanceof Number ? ((Number) o).doubleValue() : 0.0;
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
