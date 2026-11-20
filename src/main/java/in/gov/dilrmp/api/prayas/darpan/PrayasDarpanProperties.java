package in.gov.dilrmp.api.prayas.darpan;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DARPAN / PRAYAS push settings (same codes as legacy dilrmpwebservice).
 */
@Component
@ConfigurationProperties(prefix = "prayas.darpan")
public class PrayasDarpanProperties {

    /** When false, scheduler and manual push are no-ops. */
    private boolean enabled = true;

    private String getdateUrl = "http://prayasapi.darpan.nic.in/getdate";
    private String pushdataUrl = "http://prayasapi.darpan.nic.in/pushdata";

    /** Classpath location of AES key file from DARPAN. */
    private String keyClasspath = "darpan/Key_1_10086_20200709.key";

    private int instanceCode = 1;
    private int projectCode = 10086;
    private int ministryCode = 40;
    private int deptCode = 46;
    private int secCode = 25;
    private int groupId = 1;
    /** 1 = Monthly (legacy). */
    private int frequencyId = 1;
    private int atmpt = 0;

    /**
     * LGD footprint for LValue — keep aligned with DARPAN project registration.
     * Legacy DILRMP push always sent 3 parts: 91,stateLgd,0 (Central–State + placeholder district).
     */
    private int lvalueLevels = 3;

    /** Expected HTTP status from DARPAN APIs (legacy sample used 201). */
    private int successHttpStatus = 201;

    /** Connect timeout to DARPAN host (seconds). */
    private int connectTimeoutSeconds = 20;

    /** Full request timeout for getdate/pushdata (seconds). */
    private int requestTimeoutSeconds = 90;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getGetdateUrl() {
        return getdateUrl;
    }

    public void setGetdateUrl(String getdateUrl) {
        this.getdateUrl = getdateUrl;
    }

    public String getPushdataUrl() {
        return pushdataUrl;
    }

    public void setPushdataUrl(String pushdataUrl) {
        this.pushdataUrl = pushdataUrl;
    }

    public String getKeyClasspath() {
        return keyClasspath;
    }

    public void setKeyClasspath(String keyClasspath) {
        this.keyClasspath = keyClasspath;
    }

    public int getInstanceCode() {
        return instanceCode;
    }

    public void setInstanceCode(int instanceCode) {
        this.instanceCode = instanceCode;
    }

    public int getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(int projectCode) {
        this.projectCode = projectCode;
    }

    public int getMinistryCode() {
        return ministryCode;
    }

    public void setMinistryCode(int ministryCode) {
        this.ministryCode = ministryCode;
    }

    public int getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(int deptCode) {
        this.deptCode = deptCode;
    }

    public int getSecCode() {
        return secCode;
    }

    public void setSecCode(int secCode) {
        this.secCode = secCode;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getFrequencyId() {
        return frequencyId;
    }

    public void setFrequencyId(int frequencyId) {
        this.frequencyId = frequencyId;
    }

    public int getAtmpt() {
        return atmpt;
    }

    public void setAtmpt(int atmpt) {
        this.atmpt = atmpt;
    }

    public int getLvalueLevels() {
        return lvalueLevels;
    }

    public void setLvalueLevels(int lvalueLevels) {
        this.lvalueLevels = lvalueLevels;
    }

    public int getSuccessHttpStatus() {
        return successHttpStatus;
    }

    public void setSuccessHttpStatus(int successHttpStatus) {
        this.successHttpStatus = successHttpStatus;
    }

    public int getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public void setConnectTimeoutSeconds(int connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }

    public int getRequestTimeoutSeconds() {
        return requestTimeoutSeconds;
    }

    public void setRequestTimeoutSeconds(int requestTimeoutSeconds) {
        this.requestTimeoutSeconds = requestTimeoutSeconds;
    }
}
