package in.gov.dilrmp.models.profile;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** DoLR-entered part of a State Profile; MIS figures are not stored, they are read again when the profile is opened. */
@Entity
@Table(name = "state_profile_word_entry",
        uniqueConstraints = @UniqueConstraint(columnNames = {"state_id", "as_on_date"}))
@Getter
@Setter
@NoArgsConstructor
public class StateProfileWordEntry {

    public static final String DRAFT = "DRAFT";
    public static final String FINAL = "FINAL";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "state_id", nullable = false)
    private Long stateId;

    @Column(name = "as_on_date", nullable = false)
    private LocalDate asOnDate;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "fund_released1") private String fundReleased1;
    @Column(name = "fund_released2") private String fundReleased2;
    @Column(name = "fund_released3") private String fundReleased3;
    @Column(name = "fund_spent1") private String fundSpent1;
    @Column(name = "fund_spent2") private String fundSpent2;
    @Column(name = "fund_spent3") private String fundSpent3;
    @Column(name = "unspent_funds") private String unspentFunds;
    @Column(name = "unspent_as_per_pfms_date") private String unspentAsPerPfmsDate;
    @Column(name = "pending_uc") private String pendingUc;

    /** One reason per physical progress row, newline separated. */
    @Column(name = "reasons", columnDefinition = "TEXT") private String reasonsText;

    @Column(name = "ngdrs_status", columnDefinition = "TEXT") private String ngdrsStatus;
    @Column(name = "rccms_status", columnDefinition = "TEXT") private String rccmsStatus;
    @Column(name = "gender_column_status", columnDefinition = "TEXT") private String genderColumnStatus;
    @Column(name = "land_stack_status", columnDefinition = "TEXT") private String landStackStatus;

    @Column(name = "naksha_ror_tagging", columnDefinition = "TEXT") private String nakshaRorTagging;
    @Column(name = "naksha_amount_utilized") private String nakshaAmountUtilized;
    @Column(name = "naksha_sasci", columnDefinition = "TEXT") private String nakshaSasci;
    @Column(name = "naksha_other_points", columnDefinition = "TEXT") private String nakshaPhysicalStatus;

    /** One line per ULB: name, RoR %, draft UrPro timeline, final UrPro publication, tab separated. */
    @Column(name = "ulb_details", columnDefinition = "TEXT") private String ulbDetailsText;

    @Column(name = "similar_scheme", columnDefinition = "TEXT") private String similarScheme;
    @Column(name = "issues_by_state", columnDefinition = "TEXT") private String issuesByState;
    @Column(name = "issues_by_dolr_financial", columnDefinition = "TEXT") private String issuesByDolrFinancial;
    @Column(name = "issues_by_dolr_physical", columnDefinition = "TEXT") private String issuesByDolrPhysical;
    @Column(name = "issues_by_dolr_naksha", columnDefinition = "TEXT") private String issuesByDolrNaksha;

    @Column(name = "updated_by") private String updatedBy;
    @Column(name = "updated_on") private LocalDateTime updatedOn;
    @Column(name = "final_saved_by") private String finalSavedBy;
    @Column(name = "final_saved_on") private LocalDateTime finalSavedOn;
}
