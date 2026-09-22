package in.gov.dilrmp.Scheduler.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name="state_registration_report_view_backup")
public class SroViewReportBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate backupDate;
    private Long stateId;
    private String stateName;
    private Integer lgdCode;
    private Integer totalDistricts;
    private Integer numberOfSROsInState;
    private Integer numberOfSROsUsingOnlineRegistration;
    private String legacyRecordsAvailableFromYearSRO;
    private String legacyRecordsAvailableFromYearPublic;
    private String standardDeedTemplatesAvailable;
    private String circleRatesVisibleToPublic;
    private String automaticFeeCalculation;
    private String integrationWithEStampSystem;
    private String refundOptionAvailable;
    private String uploadSupportingDocumentsOption;
    private String onlineVerificationOfDocuments;
    private String signaturesObtainedDigitally;
    private String digitalSignatureEnabledForSRO;
    private String ekycOptionAvailable;
    private String videoConferencingOptionAvailable;
    private String homeVisitModuleAvailable;
    private String automaticEmailNotifications;
    private String digitalFormatRegisteredDocuments;
    private String onlineGrievanceRedressalSystem;
    private String fetchPropertyDetailsFromLandRecords;
    private String autoTriggerMutationAvailable;
    private String revenueCourtCaseRedFlagged;
    private String maskingSensitiveDetailsInRegisteredDeeds;
    private String storeDocumentsInDigilocker;
    private BigDecimal onlineRegistrationPercent;
}
