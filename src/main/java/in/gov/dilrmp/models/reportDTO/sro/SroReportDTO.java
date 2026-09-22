package in.gov.dilrmp.models.reportDTO.sro;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="state_registration_report_view")
public class SroReportDTO {

    @Id
    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "lgd_code")
    private Integer lgdCode;

    @Column(name = "total_districts")
    private Integer totalDistricts;

    @Column(name = "number_of_sros_in_state")
    private Integer numberOfSROsInState;

    @Column(name = "number_of_sros_using_online_registration")
    private Integer numberOfSROsUsingOnlineRegistration;

    @Column(name = "legacy_records_available_from_year_sro")
    private Date legacyRecordsAvailableFromYearSRO;

    @Column(name = "legacy_records_available_from_year_public")
    private Date legacyRecordsAvailableFromYearPublic;

    @Column(name = "standard_deed_templates_available")
    private String standardDeedTemplatesAvailable;

    @Column(name = "circle_rates_visible_to_public")
    private String circleRatesVisibleToPublic;

    @Column(name = "automatic_fee_calculation")
    private String automaticFeeCalculation;

    @Column(name = "integration_with_e_stamp_system")
    private String integrationWithEStampSystem;

    @Column(name = "refund_option_available")
    private String refundOptionAvailable;

    @Column(name = "upload_supporting_documents_option")
    private String uploadSupportingDocumentsOption;

    @Column(name = "online_verification_of_documents")
    private String onlineVerificationOfDocuments;

    @Column(name = "signatures_obtained_digitally")
    private String signaturesObtainedDigitally;

    @Column(name = "digital_signature_enabled_for_sro")
    private String digitalSignatureEnabledForSRO;

    @Column(name = "ekyc_option_available")
    private String ekycOptionAvailable;

    @Column(name = "video_conferencing_option_available")
    private String videoConferencingOptionAvailable;

    @Column(name = "home_visit_module_available")
    private String homeVisitModuleAvailable;

    @Column(name = "automatic_email_notifications")
    private String automaticEmailNotifications;

    @Column(name = "digital_format_registered_documents")
    private String digitalFormatRegisteredDocuments;

    @Column(name = "online_grievance_redressal_system")
    private String onlineGrievanceRedressalSystem;

    @Column(name = "fetch_property_details_from_land_records")
    private String fetchPropertyDetailsFromLandRecords;

    @Column(name = "auto_trigger_mutation_available")
    private String autoTriggerMutationAvailable;

    @Column(name = "revenue_court_case_red_flagged")
    private String revenueCourtCaseRedFlagged;

    @Column(name = "masking_sensitive_details_in_registered_deeds")
    private String maskingSensitiveDetailsInRegisteredDeeds;

    @Column(name = "store_documents_in_digilocker")
    private String storeDocumentsInDigilocker;

    @Column(name = "online_registration_percent")
    private BigDecimal onlineRegistrationPercent;

}

