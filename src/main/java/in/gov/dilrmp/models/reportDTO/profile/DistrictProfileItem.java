package in.gov.dilrmp.models.reportDTO.profile;

/** One indicator / value line of a District Profile section. */
public class DistrictProfileItem {
    private final String label;
    private final String value;

    public DistrictProfileItem(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
