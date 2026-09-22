package in.gov.dilrmp.Scheduler.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "state_clr_report_view_backup")
public class StateClrReportViewBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long stateId;
    private String stateName;
    private Integer lgdCode;
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
    private Boolean ercmsAvailable;
    private String ercmsNameUrl;
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
    private Long districtsWithBankRedFlagMortgage;
    private Integer bankBranchesWithRedFlagMortgage;
    private LocalDate lastUpdateOnDate;
    private Integer version;
    private LocalDate createdOnDate;
    private Integer totalDistrict;
    private Integer totalTehsils;
    private Integer totalVillages;
    private Integer totalRor;
    private Integer rorComputerized;
    private BigDecimal rorComputerizedPercent;
    private Integer villagesClrCompleted;
    private BigDecimal clrCompletionPercent;
    private Integer totalLandOwners;
    private Integer districtsWithGenderBasedOwnership;
    private Integer totalMaleLandOwners;
    private Integer totalFemaleLandOwners;
    private Integer totalOwners;
    private LocalDate backupDate;
}
