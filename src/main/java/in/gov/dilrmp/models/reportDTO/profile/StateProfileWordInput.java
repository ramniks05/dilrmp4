package in.gov.dilrmp.models.reportDTO.profile;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Values entered by DoLR for the State Profile Word report. MIS figures are not part of this form;
 * fields left empty stay blank in the Word file so the officer can fill them later.
 */
@Data
public class StateProfileWordInput {
    private Long stateId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate asOnDate;

    /** ₹ in crore, per DILRMP period: 2008-09 to 2013-14, 2014-15 to 2020-21, 2021-22 to 2025-26. */
    private String fundReleased1;
    private String fundReleased2;
    private String fundReleased3;
    private String fundSpent1;
    private String fundSpent2;
    private String fundSpent3;
    private String unspentFunds;
    private String unspentAsPerPfmsDate;
    private String pendingUc;

    /** Reason for incompletion, one per physical progress row in the order of the MIS table. */
    private List<String> reasons = new ArrayList<>();

    private String ngdrsStatus;
    private String rccmsStatus;
    private String genderColumnStatus;
    private String landStackStatus;

    private String nakshaRorTagging;
    private String nakshaAmountUtilized;
    private String nakshaSasci;
    private String nakshaPhysicalStatus;

    /** Per NAKSHA ULB, in the order of the MIS ULB list. */
    private List<String> ulbNames = new ArrayList<>();
    private List<String> ulbRor = new ArrayList<>();
    private List<String> ulbDraftTimeline = new ArrayList<>();
    private List<String> ulbFinalPublication = new ArrayList<>();

    private String similarScheme;
    private String issuesByState;
    private String issuesByDolrFinancial;
    private String issuesByDolrPhysical;
    private String issuesByDolrNaksha;

    /** ULB name to {RoR %, draft UrPro timeline, final UrPro publication}. */
    public Map<String, String[]> ulbDetailsByName() {
        Map<String, String[]> map = new LinkedHashMap<>();
        for (int i = 0; i < ulbNames.size(); i++) {
            String name = ulbNames.get(i);
            if (name == null || name.trim().isEmpty()) {
                continue;
            }
            map.put(name.trim(), new String[]{at(ulbRor, i), at(ulbDraftTimeline, i), at(ulbFinalPublication, i)});
        }
        return map;
    }

    public String reasonAt(int index) {
        return at(reasons, index);
    }

    private static String at(List<String> list, int index) {
        return list != null && index < list.size() && list.get(index) != null ? list.get(index) : "";
    }
}
