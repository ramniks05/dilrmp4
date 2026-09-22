package in.gov.dilrmp.Scheduler.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Data
@Entity
@Table(name = "rcms_report_view_backup")
public class RcmsReportViewReportBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate backupDate;
    private Long stateId;
    private String stateName;
    private Integer lgdCode;
    private Integer totalDistrict;
    private String eRcmsAvailable;
    private String affidavitFilingOnline;
    private String causeListGenerationOnline;
    private String noticeToDefendantsOnline;
    private String counterAffidavitFilingOnline;
    private String proceedingsTypingDirectlyOnline;
    private String uploadingRevenueCourtOrdersOnline;
    private String landRecordsOnlineFromRevenueCourtSystem;
    private String revenueCourtProceedingsPaperless;
    private String landRecordsOnlineForCivilCourts;
    private String caseFilingRedFlaggedInLandRecordsFromCivilCourts;

    private String caseFilingRedFlaggedInLandRecords;
    private Integer totalRevenueCourts;
    private Integer revenueCourtsComputerized;
    private Double revenueCourtsComputerizedPercent;
    private Integer totalTehsils;

}
