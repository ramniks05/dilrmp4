package in.gov.dilrmp.models.reportDTO.clr;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "state_clr_report_view")
public class StateClrReportView {

    @Id
    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "lgd_code")
    private Integer lgdCode;

    @Column(name = "state_website_url")
    private String stateWebsiteUrl;

    @Column(name = "mobile_app_available")
    private String mobileAppAvailable;

    @Column(name = "mobile_app_name")
    private String mobileAppName;

    @Column(name = "cadastral_maps_portal_url")
    private String cadastralMapsPortalUrl;

    @Column(name = "ror_available_online")
    private String rorAvailableOnline;

    @Column(name = "digitally_signed_ror_available")
    private String digitallySignedRorAvailable;

    @Column(name = "digitally_signed_ror_legally_valid")
    private String digitallySignedRorLegallyValid;

    @Column(name = "online_mutation_facility")
    private String onlineMutationFacility;

    @Column(name = "land_records_online_from_registration_system")
    private String landRecordsOnlineFromRegistrationSystem;

    @Column(name = "auto_trigger_mutation")
    private String autoTriggerMutation;

    //v5 CLR: Whether Auto-Mutation Facility Available [11]
    @Transient
    private String autoMutationFacility;

    @Column(name = "ercms_available")
    private String ercmsAvailable;

    @Column(name = "ercms_name_url")
    private String ercmsNameUrl;

    @Column(name = "total_revenue_courts")
    private Integer totalRevenueCourts;

    @Column(name = "computerized_revenue_courts")
    private Integer computerizedRevenueCourts;

    @Column(name = "affidavit_filing_online")
    private String affidavitFilingOnline;

    @Column(name = "cause_list_generation_online")
    private String causeListGenerationOnline;

    @Column(name = "notice_to_defendants_online")
    private String noticeToDefendantsOnline;

    @Column(name = "counter_affidavit_filing_online")
    private String counterAffidavitFilingOnline;

    @Column(name = "proceedings_typing_directly_online")
    private String proceedingsTypingDirectlyOnline;

    @Column(name = "uploading_revenue_court_orders_online")
    private String uploadingRevenueCourtOrdersOnline;

    @Column(name = "land_records_online_from_revenue_court_system")
    private String landRecordsOnlineFromRevenueCourtSystem;

    @Column(name = "revenue_court_proceedings_paperless")
    private String revenueCourtProceedingsPaperless;

    @Column(name = "land_records_online_for_civil_courts")
    private String landRecordsOnlineForCivilCourts;

    @Column(name = "case_filing_red_flagged_in_land_records_from_civil_courts")
    private String caseFilingRedFlaggedInLandRecordsFromCivilCourts;

    @Column(name = "bank_red_flag_mortgage_in_land_records")
    private String bankRedFlagMortgageInLandRecords;

    @Column(name = "districts_with_bank_red_flag_mortgage")
    private Long districtsWithBankRedFlagMortgage;

    @Column(name = "bank_branches_with_red_flag_mortgage")
    private Integer bankBranchesWithRedFlagMortgage;

    @Column(name = "last_update_on_date")
    private LocalDate lastUpdateOnDate;

    @Column(name = "version")
    private Integer version;

    @Column(name = "created_on_date")
    private LocalDate createdOnDate;

    @Column(name = "total_district")
    private Integer totalDistrict;

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

    //v5 CLR: RoR with Cadastral Map [6.1]
    @Transient
    private Integer rorWithCadastralMap;
    @Transient
    private String formattingRorWithCadastralMap;
    @Transient
    private BigDecimal rorWithCadastralMapPercent;

    @Column(name = "villages_clr_completed")
    private Integer villagesClrCompleted;

    @Column(name = "clr_completion_percent")
    private BigDecimal clrCompletionPercent;

    @Column(name = "total_land_owners")
    private Integer totalLandOwners;

    @Column(name = "districts_with_gender_based_ownership")
    private Integer districtsWithGenderBasedOwnership;

    @Column(name = "total_male_land_owners")
    private Integer totalMaleLandOwners;

    @Column(name = "total_female_land_owners")
    private Integer totalFemaleLandOwners;

    @Column(name = "total_owners")
    private Integer totalOwners;



    //For comma make Transient

    @Transient
    private String formattingTotalDistrict;

    @Transient
    private String formattingTotalTehsils;

    @Transient
    private String formattingTotalVillages;

    @Transient
    private String formattingTotalRor;

    @Transient
    private String formattingTotalLandOwners;


    @Transient
    private String formattingRorComputerized;

    @Transient
    private String formattingvillagesClrCompleted;

    @Transient
    private String formattingTotalMaleLandOwners;

    @Transient
    private String formattingTotalFemaleLandOwners;

    @Transient
    private String formattingTotalOwners;

    @Transient
    private String formattingDistrictsWithBankRedFlagMortgage;

    @Transient
    private String formattingBankBranchesWithRedFlagMortgage;

}