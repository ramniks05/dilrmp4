package in.gov.dilrmp.Scheduler.models;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class StateMISDataEntryBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;
    private String stateWebsiteUrl;
    private Boolean mobileAppAvailable;
    private String mobileAppName;
    private String cadastralMapsPortalUrl;
    private Boolean rorAvailableOnline;
    private Boolean digitallySignedRorAvailable;
    private Boolean digitallySignedRorLegallyValid;
    private Boolean onlineMutationFacility;
    private Boolean landRecordsOnlineFromRegistrationSystem;
    private Boolean autoTriggerMutation;
    private Boolean eRCMSAvailable = false;
    private String eRCMSNameUrl;
    private Integer totalRevenueCourts;
    private Integer computerizedRevenueCourts;
    private Boolean affidavitFilingOnline;
    private Boolean causeListGenerationOnline;
    private Boolean noticeToDefendantsOnline;
    private Boolean counterAffidavitFilingOnline;
    private Boolean proceedingsTypingDirectlyOnline;
    private Boolean uploadingRevenueCourtOrdersOnline;
    private Boolean caseFilingRedFlaggedInLandRecords;
    private Boolean landRecordsOnlineFromRevenueCourtSystem;
    private Boolean revenueCourtProceedingsPaperless;
    private Boolean landRecordsOnlineForCivilCourts;
    private Boolean caseFilingRedFlaggedInLandRecordsFromCivilCourts;
    private Boolean bankRedFlagMortgageInLandRecords;
    private Integer districtsWithBankRedFlagMortgage;
    private Integer bankBranchesWithRedFlagMortgage ;

    // Additional fields

    private LocalDate updateOnDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //Insert or Update Medium DE Forms:-1 and API -2

    private Integer dataSourceType;
    //Count How many times update increment value by 1 on each update initial value 1
    private Integer version;
    private LocalDate createdOnDate;
    private LocalDate backupDate;

}
