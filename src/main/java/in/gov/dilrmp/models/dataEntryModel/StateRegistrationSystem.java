package in.gov.dilrmp.models.dataEntryModel;

import java.time.LocalDate;
import java.util.Date;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "state_registration_system",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"state_id"})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StateRegistrationSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "number_of_sros_in_state", columnDefinition = "int default 0")
    private Integer numberOfSROsInState = 0;

    @Column(name = "number_of_sros_using_online_registration", columnDefinition = "int default 0")
    private Integer numberOfSROsUsingOnlineRegistration = 0;

    //v5 Modernization of SRO — State/UT funds / PPP (B); IGR form
    @Column(name = "sros_modernised_state_funds", columnDefinition = "int default 0")
    private Integer srosModernisedStateFunds = 0;

    /** DoLR-entered later — shown in report only; no IGR form question. */
    @Column(name = "sros_dilrmp_sanctioned", columnDefinition = "int default 0")
    private Integer srosDilrmpSanctioned = 0;

    //v5 Modernization of SRO — DILRMP funds (D); IGR form
    @Column(name = "sros_modernised_dilrmp_funds", columnDefinition = "int default 0")
    private Integer srosModernisedDilrmpFunds = 0;

    @Column(name = "legacy_records_available_from_year_sro")
    private Date legacyRecordsAvailableFromYearSRO;

    @Column(name = "legacy_records_available_from_year_public")
    private Date legacyRecordsAvailableFromYearPublic;

    @Column(name = "standard_deed_templates_available", columnDefinition = "boolean default false" )
    private Boolean standardDeedTemplatesAvailable = false;

    @Column(name = "circle_rates_visible_to_public",  columnDefinition = "boolean default false")
    private Boolean circleRatesVisibleToPublic = false;

    @Column(name = "automatic_fee_calculation", columnDefinition = "boolean default false" )
    private Boolean automaticFeeCalculation = false;

    @Column(name = "integration_with_e_stamp_system",  columnDefinition = "boolean default false")
    private Boolean integrationWithEStampSystem = false;

    @Column(name = "refund_option_available", columnDefinition = "boolean default false" )
    private Boolean refundOptionAvailable = false;

    @Column(name = "upload_supporting_documents_option",  columnDefinition = "boolean default false")
    private Boolean uploadSupportingDocumentsOption = false;

    @Column(name = "online_verification_of_documents",  columnDefinition = "boolean default false")
    private Boolean onlineVerificationOfDocuments = false;

    @Column(name = "signatures_obtained_digitally", columnDefinition = "boolean default false" )
    private Boolean signaturesObtainedDigitally = false;

    @Column(name = "digital_signature_enabled_for_sro", columnDefinition = "boolean default false" )
    private Boolean digitalSignatureEnabledForSRO = false;

    @Column(name = "e_kyc_option_available", columnDefinition = "boolean default false" )
    private Boolean eKYCOptionAvailable = false;

    @Column(name = "video_conferencing_option_available", columnDefinition = "boolean default false" )
    private Boolean videoConferencingOptionAvailable = false;

    @Column(name = "home_visit_module_available",  columnDefinition = "boolean default false")
    private Boolean homeVisitModuleAvailable = false;

    @Column(name = "automatic_email_notifications",  columnDefinition = "boolean default false")
    private Boolean automaticEmailNotifications = false;

    @Column(name = "digital_format_registered_documents", columnDefinition = "boolean default false" )
    private Boolean digitalFormatRegisteredDocuments = false;

    @Column(name = "online_grievance_redressal_system",  columnDefinition = "boolean default false")
    private Boolean onlineGrievanceRedressalSystem = false;

    @Column(name = "fetch_property_details_from_land_records", columnDefinition = "boolean default false" )
    private Boolean fetchPropertyDetailsFromLandRecords = false;

    @Column(name = "auto_trigger_mutation_available",  columnDefinition = "boolean default false")
    private Boolean autoTriggerMutationAvailable = false;

    @Column(name = "revenue_court_case_red_flagged", columnDefinition = "boolean default false" )
    private Boolean revenueCourtCaseRedFlagged = false;

    @Column(name = "masking_sensitive_details_in_registered_deeds", columnDefinition = "boolean default false" )
    private Boolean maskingSensitiveDetailsInRegisteredDeeds = false;

    @Column(name = "store_documents_in_digilocker",  columnDefinition = "boolean default false")
    private Boolean storeDocumentsInDigilocker = false;

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
    @Column(name = "version")
    private Integer version;

    @Column(name = "created_on_date")
    private LocalDate createdOnDate;

    @Transient
    private String  legacyRecordsAvailableFromYearSRODate;
    @Transient
    private String  legacyRecordsAvailableFromYearPublicDate;

}
