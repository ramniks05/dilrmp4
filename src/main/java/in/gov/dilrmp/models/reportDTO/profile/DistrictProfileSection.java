package in.gov.dilrmp.models.reportDTO.profile;

import java.util.List;

/** One component (CLR, MAP, MRR, ...) of the District Profile report. */
public class DistrictProfileSection {
    private final String title;
    private final List<DistrictProfileItem> items;

    public DistrictProfileSection(String title, List<DistrictProfileItem> items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public List<DistrictProfileItem> getItems() {
        return items;
    }

    public boolean isHasData() {
        return items != null && !items.isEmpty();
    }
}
