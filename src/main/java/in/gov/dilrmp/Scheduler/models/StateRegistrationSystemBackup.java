package in.gov.dilrmp.Scheduler.models;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
@Data
@Entity
public class StateRegistrationSystemBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;
    private Integer numberOfSROsInState;
    private Integer numberOfSROsUsingOnlineRegistration;
    //v5 Modernization of SRO
    private Integer srosModernisedStateFunds;
    private Integer srosDilrmpSanctioned;
    private Integer srosModernisedDilrmpFunds;
    private Date legacyRecordsAvailableFromYearSRO;
    private Date legacyRecordsAvailableFromYearPublic;
    private Boolean standardDeedTemplatesAvailable;
    private Boolean circleRatesVisibleToPublic;
    private Boolean automaticFeeCalculation;
    private Boolean integrationWithEStampSystem;
    private Boolean refundOptionAvailable;
    private Boolean uploadSupportingDocumentsOption;
    private Boolean onlineVerificationOfDocuments;
    private Boolean signaturesObtainedDigitally;
    private Boolean digitalSignatureEnabledForSRO;
    private Boolean eKYCOptionAvailable;
    private Boolean videoConferencingOptionAvailable;
    private Boolean homeVisitModuleAvailable;
    private Boolean automaticEmailNotifications;
    private Boolean digitalFormatRegisteredDocuments;
    private Boolean onlineGrievanceRedressalSystem;
    private Boolean fetchPropertyDetailsFromLandRecords;
    private Boolean autoTriggerMutationAvailable;
    private Boolean revenueCourtCaseRedFlagged;
    private Boolean maskingSensitiveDetailsInRegisteredDeeds;
    private Boolean storeDocumentsInDigilocker;
    private LocalDate updateOnDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private Integer dataSourceType;
    private Integer version;
    private LocalDate createdOnDate;
    private LocalDate backupDate;
}
