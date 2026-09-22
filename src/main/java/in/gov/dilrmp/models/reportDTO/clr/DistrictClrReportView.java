package in.gov.dilrmp.models.reportDTO.clr;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table(name = "district_clr_report_view", schema = "public")
public class DistrictClrReportView {

    @Id
    @Column(name = "district_id")
    private Long districtId;

    // from district Model
    @Column(name = "district_lgd_code")
    private Integer districtLgdCode;

    @Column(name = "district_name")
    private String districtName;

    // from state model
    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_name")
    private String stateName;

    // from DistrictMISDataEntry Model
    @Column(name = "total_tehsils")
    private Integer totalTehsils;

    @Column(name = "total_villages")
    private Integer totalVillages;

    @Column(name = "total_ror")
    private Integer totalRor;

    @Column(name = "ror_computerized")
    private Integer rorComputerized;

    @Column(name = "ror_computerized_percent")
    private BigDecimal rorComputerizedPercent;

    @Column(name = "villages_clr_completed")
    private Integer villagesClrCompleted;

    @Column(name = "clr_completion_percent")
    private BigDecimal clrCompletionPercent;

    @Column(name = "total_land_owners")
    private Integer totalLandOwners;

    @Column(name = "districts_with_gender_based_ownership")
    private String districtsWithGenderBasedOwnership;

    @Column(name = "total_male_land_owners")
    private Integer totalMaleLandOwners;

    @Column(name = "total_female_land_owners")
    private Integer totalFemaleLandOwners;

    @Column(name = "total_owners")
    private Integer totalOwners;

    // from StateMISDataEntry Model all field take transiant and data set from state
    // view or StateMISDataEntry
    @Transient
    private String stateWebsiteUrl;

    @Transient
    private String mobileAppAvailable;

    @Transient
    private String mobileAppName;

    @Transient
    private String cadastralMapsPortalUrl;

    @Transient
    private String rorAvailableOnline;

    @Transient
    private String digitallySignedRorAvailable;

    @Transient
    private String digitallySignedRorLegallyValid;

    @Transient
    private String onlineMutationFacility;

    @Transient
    private String landRecordsOnlineFromRegistrationSystem;

    @Transient
    private String autoTriggerMutation;

    @Transient
    private String ercmsAvailable;

    @Transient
    private String ercmsNameUrl;

    @Transient
    private Integer totalRevenueCourts;

    @Transient
    private Integer computerizedRevenueCourts;

    @Transient
    private String affidavitFilingOnline;

    @Transient
    private String causeListGenerationOnline;

    @Transient
    private String noticeToDefendantsOnline;

    @Transient
    private String counterAffidavitFilingOnline;

    @Transient
    private String proceedingsTypingDirectlyOnline;

    @Transient
    private String uploadingRevenueCourtOrdersOnline;

    @Transient
    private String landRecordsOnlineFromRevenueCourtSystem;

    @Transient
    private String revenueCourtProceedingsPaperless;

    @Transient
    private String landRecordsOnlineForCivilCourts;

    @Transient
    private String caseFilingRedFlaggedInLandRecordsFromCivilCourts;

    @Transient
    private String bankRedFlagMortgageInLandRecords;

    @Transient
    private Long districtsWithBankRedFlagMortgage;

    @Transient
    private Integer bankBranchesWithRedFlagMortgage;

}
