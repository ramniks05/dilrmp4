package in.gov.dilrmp.api.prayas.darpan;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class PrayasDarpanClient {

    private static final Logger logger = LoggerFactory.getLogger(PrayasDarpanClient.class);
    private static final DateTimeFormatter DARPAN_DATE = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final PrayasDarpanProperties properties;
    private final HttpClient httpClient;

    public PrayasDarpanClient(PrayasDarpanProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(Math.max(5, properties.getConnectTimeoutSeconds())))
                .build();
    }

    public GetDateResult getDate() throws Exception {
        try {
            JsonObject body = identityJson();
            HttpResponse<String> response = postJson(properties.getGetdateUrl(), body.toString());
            int status = response.statusCode();
            String raw = response.body() == null ? "" : response.body();
            logger.info("DARPAN getdate HTTP {} body length {}", status, raw.length());

            if (status != properties.getSuccessHttpStatus() && status != 200) {
                return GetDateResult.httpError(status, raw);
            }

            JsonObject json = JsonParser.parseString(raw).getAsJsonObject();
            if (json.has("Status")) {
                int code = parseInt(json.get("Status"));
                String message = json.has("Message") ? json.get("Message").getAsString() : "";
                return GetDateResult.status(code, message, raw);
            }

            List<LocalDate> dates = new ArrayList<>();
            Set<Integer> groups = new LinkedHashSet<>();
            groups.add(properties.getGroupId());

            if (json.has("RetDMDashCaption") && json.get("RetDMDashCaption").isJsonArray()) {
                JsonArray arr = json.getAsJsonArray("RetDMDashCaption");
                for (JsonElement el : arr) {
                    JsonObject row = el.getAsJsonObject();
                    if (row.has("datadate")) {
                        dates.add(LocalDate.parse(row.get("datadate").getAsString().trim(), DARPAN_DATE));
                    }
                    if (row.has("Group_Id")) {
                        String groupRaw = row.get("Group_Id").getAsString();
                        for (String part : groupRaw.split("#")) {
                            String trimmed = part.trim();
                            if (!trimmed.isEmpty()) {
                                groups.add(Integer.parseInt(trimmed));
                            }
                        }
                    }
                }
            }
            return GetDateResult.dates(dates, new ArrayList<>(groups), raw);
        } catch (HttpTimeoutException ex) {
            throw new IllegalStateException(
                    "Timeout contacting DARPAN getdate (" + properties.getGetdateUrl()
                            + "). Server could not reach prayasapi.darpan.nic.in in time — check firewall/proxy or ask DARPAN if API is up.",
                    ex);
        } catch (java.io.IOException ex) {
            throw new IllegalStateException(
                    "Network error contacting DARPAN getdate (" + properties.getGetdateUrl() + "): " + ex.getMessage(),
                    ex);
        }
    }

    public PushResult pushData(String encryptedPayloadJson) throws Exception {
        try {
            HttpResponse<String> response = postJson(properties.getPushdataUrl(), encryptedPayloadJson);
            int status = response.statusCode();
            String raw = response.body() == null ? "" : response.body();
            logger.info("DARPAN pushdata HTTP {} body length {}", status, raw.length());

            if (status != properties.getSuccessHttpStatus() && status != 200) {
                return PushResult.httpError(status, raw);
            }

            JsonObject json = JsonParser.parseString(raw).getAsJsonObject();
            if (json.has("Status")) {
                int code = parseInt(json.get("Status"));
                String message = json.has("Message") ? json.get("Message").getAsString() : "";
                return PushResult.status(code, message, raw);
            }
            return PushResult.httpError(status, raw);
        } catch (HttpTimeoutException ex) {
            throw new IllegalStateException(
                    "Timeout contacting DARPAN pushdata (" + properties.getPushdataUrl()
                            + "). Check firewall/proxy or DARPAN API availability.",
                    ex);
        } catch (java.io.IOException ex) {
            throw new IllegalStateException(
                    "Network error contacting DARPAN pushdata (" + properties.getPushdataUrl() + "): " + ex.getMessage(),
                    ex);
        }
    }

    private JsonObject identityJson() {
        JsonObject json = new JsonObject();
        json.addProperty("Instance_Code", properties.getInstanceCode());
        json.addProperty("Sec_Code", properties.getSecCode());
        json.addProperty("Ministry_Code", properties.getMinistryCode());
        json.addProperty("Dept_Code", properties.getDeptCode());
        json.addProperty("Project_Code", properties.getProjectCode());
        return json;
    }

    private HttpResponse<String> postJson(String url, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(Math.max(15, properties.getRequestTimeoutSeconds())))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static int parseInt(JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return 0;
        }
        if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber()) {
            return el.getAsInt();
        }
        return Integer.parseInt(el.getAsString().trim());
    }

    public static final class GetDateResult {
        private final boolean ok;
        private final int statusCode;
        private final String message;
        private final List<LocalDate> dates;
        private final List<Integer> groupIds;
        private final String raw;

        private GetDateResult(boolean ok, int statusCode, String message,
                              List<LocalDate> dates, List<Integer> groupIds, String raw) {
            this.ok = ok;
            this.statusCode = statusCode;
            this.message = message;
            this.dates = dates;
            this.groupIds = groupIds;
            this.raw = raw;
        }

        static GetDateResult dates(List<LocalDate> dates, List<Integer> groupIds, String raw) {
            return new GetDateResult(true, 1, "OK", dates, groupIds, raw);
        }

        static GetDateResult status(int code, String message, String raw) {
            // Status 4 = already ported / no missing dates — treat as non-fatal
            boolean ok = code == 1 || code == 4;
            return new GetDateResult(ok, code, message, List.of(), List.of(), raw);
        }

        static GetDateResult httpError(int http, String raw) {
            return new GetDateResult(false, http, "HTTP error " + http, List.of(), List.of(), raw);
        }

        public boolean isOk() {
            return ok;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }

        public List<LocalDate> getDates() {
            return dates;
        }

        public List<Integer> getGroupIds() {
            return groupIds;
        }

        public String getRaw() {
            return raw;
        }
    }

    public static final class PushResult {
        private final boolean ok;
        private final int statusCode;
        private final String message;
        private final String raw;

        private PushResult(boolean ok, int statusCode, String message, String raw) {
            this.ok = ok;
            this.statusCode = statusCode;
            this.message = message;
            this.raw = raw;
        }

        static PushResult status(int code, String message, String raw) {
            return new PushResult(code == 1, code, message, raw);
        }

        static PushResult httpError(int http, String raw) {
            return new PushResult(false, http, "HTTP error " + http, raw);
        }

        public boolean isOk() {
            return ok;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }

        public String getRaw() {
            return raw;
        }
    }
}
