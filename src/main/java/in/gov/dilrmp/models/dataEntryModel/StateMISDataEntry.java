package in.gov.dilrmp.models.dataEntryModel;

import java.time.LocalDate;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "state_mis_data_entry",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"state_id"})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StateMISDataEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "state_website_url")
    private String stateWebsiteUrl;

    @Column(name = "mobile_app_available", columnDefinition = "boolean default false")
    private Boolean mobileAppAvailable = false;

    @Column(name = "mobile_app_name")
    private String mobileAppName;

    @Column(name = "cadastral_maps_portal_url")
    private String cadastralMapsPortalUrl;

    @Column(name = "ror_available_online", columnDefinition = "boolean default false")
    private Boolean rorAvailableOnline=false;

    @Column(name = "digitally_signed_ror_available", columnDefinition = "boolean default false")
    private Boolean digitallySignedRorAvailable =false;

    @Column(name = "digitally_signed_ror_legally_valid", columnDefinition = "boolean default false")
    private Boolean digitallySignedRorLegallyValid = false;

    @Column(name = "online_mutation_facility",columnDefinition = "boolean default false")
    private Boolean onlineMutationFacility = false;

    @Column(name = "land_records_online_from_registration_system", columnDefinition = "boolean default false")
    private Boolean landRecordsOnlineFromRegistrationSystem = false;

    @Column(name = "auto_trigger_mutation", columnDefinition = "boolean default false")
    private Boolean autoTriggerMutation = false;

    @Column(name = "e_rcms_available", columnDefinition = "boolean default false")
    private Boolean eRCMSAvailable = false;

    @Column(name = "e_rcms_name_url")
    private String eRCMSNameUrl;

    @Column(name = "total_revenue_courts", columnDefinition = "int default 0")
    private Integer totalRevenueCourts = 0;

    @Column(name = "computerized_revenue_courts", columnDefinition = "int default 0")
    private Integer computerizedRevenueCourts = 0;

    @Column(name = "affidavit_filing_online", columnDefinition = "boolean default false")
    private Boolean affidavitFilingOnline = false;

    @Column(name = "cause_list_generation_online", columnDefinition = "boolean default false")
    private Boolean causeListGenerationOnline = false;

    @Column(name = "notice_to_defendants_online", columnDefinition = "boolean default false")
    private Boolean noticeToDefendantsOnline = false;

    @Column(name = "counter_affidavit_filing_online", columnDefinition = "boolean default false")
    private Boolean counterAffidavitFilingOnline = false;

    @Column(name = "proceedings_typing_directly_online", columnDefinition = "boolean default false")
    private Boolean proceedingsTypingDirectlyOnline = false;

    @Column(name = "uploading_revenue_court_orders_online",columnDefinition = "boolean default false")
    private Boolean uploadingRevenueCourtOrdersOnline = false;

    @Column(name = "case_filing_red_flagged_in_land_records", columnDefinition = "boolean default false")
    private Boolean caseFilingRedFlaggedInLandRecords = false;

    @Column(name = "land_records_online_from_revenue_court_system", columnDefinition = "boolean default false")
    private Boolean landRecordsOnlineFromRevenueCourtSystem = false;

    @Column(name = "revenue_court_proceedings_paperless", columnDefinition = "boolean default false")
    private Boolean revenueCourtProceedingsPaperless = false;

    @Column(name = "land_records_online_for_civil_courts",columnDefinition = "boolean default false")
    private Boolean landRecordsOnlineForCivilCourts = false;

    @Column(name = "case_filing_red_flagged_in_land_records_from_civil_courts",columnDefinition = "boolean default false")
    private Boolean caseFilingRedFlaggedInLandRecordsFromCivilCourts = false;

    @Column(name = "bank_red_flag_mortgage_in_land_records", columnDefinition = "boolean default false")
    private Boolean bankRedFlagMortgageInLandRecords = false;

    @Column(name = "districts_with_bank_red_flag_mortgage",columnDefinition = "int default 0")
    private Integer districtsWithBankRedFlagMortgage = 0;

    @Column(name = "bank_branches_with_red_flag_mortgage",columnDefinition = "int default 0")
    private Integer bankBranchesWithRedFlagMortgage = 0;

    // Additional fields
    @Column(name = "last_update_on_date")
    private LocalDate updateOnDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //Insert or Update Medium DE Forms:-1 and API -2
    @Column(name = "data_source_type")
    private Integer dataSourceType;

    //Count How many times update increment value by 1 on each update initial value 1
    @Column(name = "version", columnDefinition = "int default 0")
    private Integer version = 0;

    @Column(name = "created_on_date")
    private LocalDate createdOnDate;


    @Transient
    private String created_date;





}

