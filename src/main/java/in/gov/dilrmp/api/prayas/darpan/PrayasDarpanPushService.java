package in.gov.dilrmp.api.prayas.darpan;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.gov.dilrmp.api.prayas.PrayasPortalApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Pushes live MIS PRAYAS indicators to DARPAN (getdate → encrypt → pushdata).
 */
@Service
public class PrayasDarpanPushService {

    private static final Logger logger = LoggerFactory.getLogger(PrayasDarpanPushService.class);
    private static final DateTimeFormatter DARPAN_DATE = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final Gson GSON = new Gson();

    private final PrayasDarpanProperties properties;
    private final PrayasDarpanClient client;
    private final PrayasDarpanCrypto crypto;
    private final PrayasDarpanLogRepository logRepository;
    private final PrayasPortalApiService prayasPortalApiService;

    public PrayasDarpanPushService(PrayasDarpanProperties properties,
                                   PrayasDarpanClient client,
                                   PrayasDarpanCrypto crypto,
                                   PrayasDarpanLogRepository logRepository,
                                   PrayasPortalApiService prayasPortalApiService) {
        this.properties = properties;
        this.client = client;
        this.crypto = crypto;
        this.logRepository = logRepository;
        this.prayasPortalApiService = prayasPortalApiService;
    }

    @Transactional
    public Map<String, Object> pushNow(String triggerSource) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("triggerSource", triggerSource);
        result.put("enabled", properties.isEnabled());

        if (!properties.isEnabled()) {
            result.put("status", "skipped");
            result.put("message", "PRAYAS DARPAN push is disabled (prayas.darpan.enabled=false).");
            return result;
        }

        LocalDateTime start = LocalDateTime.now();
        try {
            PrayasDarpanClient.GetDateResult getDate = client.getDate();
            saveLog(start, LocalDateTime.now(), getDate.getMessage(), null, null,
                    getDate.isOk() ? 1 : 0, getDate.getStatusCode(), triggerSource + "-getdate");

            if (!getDate.isOk()) {
                result.put("status", "error");
                result.put("message", "getdate failed: " + getDate.getMessage());
                result.put("darpanStatus", getDate.getStatusCode());
                return result;
            }

            if (getDate.getDates() == null || getDate.getDates().isEmpty()) {
                result.put("status", "success");
                result.put("message", getDate.getMessage() == null || getDate.getMessage().isBlank()
                        ? "No missing data dates from DARPAN (already up to date)."
                        : getDate.getMessage());
                result.put("darpanStatus", getDate.getStatusCode());
                result.put("dates", List.of());
                return result;
            }

            List<Integer> groups = getDate.getGroupIds() == null || getDate.getGroupIds().isEmpty()
                    ? List.of(properties.getGroupId())
                    : getDate.getGroupIds();

            List<Map<String, Object>> pushResults = new ArrayList<>();
            int okCount = 0;
            for (LocalDate dataDate : getDate.getDates()) {
                for (Integer groupId : groups) {
                    LocalDateTime pushStart = LocalDateTime.now();
                    Map<String, Object> one = new LinkedHashMap<>();
                    one.put("datadate", dataDate.toString());
                    one.put("groupId", groupId);
                    try {
                        String payload = buildEncryptedPayload(dataDate, groupId);
                        PrayasDarpanClient.PushResult push = client.pushData(payload);
                        saveLog(pushStart, LocalDateTime.now(), push.getMessage(), dataDate, dataDate,
                                push.isOk() ? 1 : 0, push.getStatusCode(), triggerSource + "-push");
                        one.put("ok", push.isOk());
                        one.put("darpanStatus", push.getStatusCode());
                        one.put("message", push.getMessage());
                        if (push.isOk()) {
                            okCount++;
                        }
                    } catch (Exception ex) {
                        logger.error("DARPAN push failed for {} group {}", dataDate, groupId, ex);
                        saveLog(pushStart, LocalDateTime.now(), truncate(ex.getMessage()), dataDate, dataDate,
                                0, 999, triggerSource + "-push");
                        one.put("ok", false);
                        one.put("message", ex.getMessage());
                    }
                    pushResults.add(one);
                }
            }

            result.put("status", okCount == pushResults.size() ? "success" : "partial");
            result.put("message", "Pushed " + okCount + " of " + pushResults.size() + " date/group payload(s).");
            result.put("dates", getDate.getDates().stream().map(LocalDate::toString).collect(Collectors.toList()));
            result.put("results", pushResults);
            return result;
        } catch (Exception ex) {
            logger.error("DARPAN push job failed", ex);
            saveLog(start, LocalDateTime.now(), truncate(ex.getMessage()), null, null, 0, 999, triggerSource);
            result.put("status", "error");
            result.put("message", ex.getMessage());
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    private String buildEncryptedPayload(LocalDate dataDate, int groupId) throws Exception {
        Map<String, Object> live = prayasPortalApiService.allStates();
        List<Map<String, Object>> states = (List<Map<String, Object>>) live.get("states");
        if (states == null || states.isEmpty()) {
            throw new IllegalStateException("No live State/UT rows available to push to DARPAN.");
        }

        List<Map<String, Object>> kpiRows = new ArrayList<>();
        String datadate = dataDate.format(DARPAN_DATE);
        for (Map<String, Object> state : states) {
            Object lgdObj = state.get("lgdCode");
            if (lgdObj == null) {
                continue;
            }
            int lgd = ((Number) lgdObj).intValue();
            if (lgd <= 0 || lgd == 999) {
                continue;
            }

            // Same footprint as legacy PMDarpanUtility:
            // LValue = lvl1,lvl2,lvl3   |  KValue = kpi1,kpi2,kpi3,kpi4,kpi5
            Map<String, Object> kpi = new LinkedHashMap<>();
            kpi.put("Group_Id", groupId);
            kpi.put("datadate", datadate);
            kpi.put("LValue", lvalue(lgd));
            kpi.put("KValue",
                    intVal(state.get("villagesClrCompleted")) + ","
                            + intVal(state.get("totalDigitizedMapsFmbTippans")) + ","
                            + intVal(state.get("villagesLinkedWithRor")) + ","
                            + intVal(state.get("numberOfSROsUsingOnlineRegistration")) + ","
                            + intVal(state.get("numberOfSROsIntegratedWithLandRecords")));
            kpiRows.add(kpi);
        }
        if (kpiRows.isEmpty()) {
            throw new IllegalStateException("No State/UT LGD rows to include in DARPAN KValue payload.");
        }

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("Instance_Code", properties.getInstanceCode());
        model.put("Sec_Code", properties.getSecCode());
        model.put("Ministry_Code", properties.getMinistryCode());
        model.put("Dept_Code", properties.getDeptCode());
        model.put("Project_Code", properties.getProjectCode());
        model.put("Frequency_Id", properties.getFrequencyId());
        model.put("atmpt", properties.getAtmpt());
        model.put("ListKpidata", kpiRows);

        List<Map<String, Object>> wrapper = List.of(model);
        String jsonString = GSON.toJson(wrapper);
        String compressed = crypto.compress(jsonString);
        String encrypted = crypto.encrypt(compressed);

        JsonArray array = new JsonArray();
        JsonObject item = new JsonObject();
        JsonObject ip = new JsonObject();
        ip.addProperty("Instance_Code", properties.getInstanceCode());
        ip.addProperty("Sec_Code", properties.getSecCode());
        ip.addProperty("Ministry_Code", properties.getMinistryCode());
        ip.addProperty("Dept_Code", properties.getDeptCode());
        ip.addProperty("Project_Code", properties.getProjectCode());
        item.add("IP", ip);
        item.addProperty("EncyptedData", encrypted);
        array.add(item);
        return array.toString();
    }

    /** Match old dilrmpwebservice LValue footprint (no DARPAN-side change). */
    private String lvalue(int stateLgd) {
        // Legacy always: lvl1=91 (India), lvl2=state LGD, lvl3=0 for state row
        if (properties.getLvalueLevels() >= 3) {
            return "91," + stateLgd + ",0";
        }
        if (properties.getLvalueLevels() <= 1) {
            return "91";
        }
        return "91," + stateLgd;
    }

    private static int intVal(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return (int) Double.parseDouble(value.toString().trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void saveLog(LocalDateTime start, LocalDateTime end, String msg,
                         LocalDate from, LocalDate to, int status, int errorCode, String source) {
        try {
            PrayasDarpanLog log = new PrayasDarpanLog();
            log.setInstanceCode(properties.getInstanceCode());
            log.setSecCode(properties.getSecCode());
            log.setMinistryCode(properties.getMinistryCode());
            log.setDeptCode(properties.getDeptCode());
            log.setProjectCode(properties.getProjectCode());
            log.setGroupId(properties.getGroupId());
            log.setReqStartDt(start);
            log.setReqEndDt(end);
            log.setMsg(truncate(msg));
            log.setDatadtFrom(from);
            log.setDatadtTo(to);
            log.setStatus(status);
            log.setErrorCode(errorCode);
            log.setTriggerSource(source);
            logRepository.save(log);
        } catch (Exception ex) {
            logger.warn("Could not write pmdarpan_log: {}", ex.getMessage());
        }
    }

    private static String truncate(String msg) {
        if (msg == null) {
            return "";
        }
        return msg.length() <= 1000 ? msg : msg.substring(0, 1000);
    }
}
