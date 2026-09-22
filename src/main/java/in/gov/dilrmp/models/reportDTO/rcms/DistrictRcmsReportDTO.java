package in.gov.dilrmp.models.reportDTO.rcms;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "district_rcms_report_view")
public class DistrictRcmsReportDTO {

    @Id
    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "district_lgd_code")
    private Integer districtLgdCode;

    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "total_revenue_courts")
    private Integer totalRevenueCourts;

    @Column(name = "revenue_courts_computerized")
    private Integer revenueCourtsComputerized;

    @Column(name = "revenue_courts_computerized_percent")
    private Double revenueCourtsComputerizedPercent;

    @Column(name = "total_tehsils")
    private Long totalTehsils;

    @Transient
    private String eRcmsAvailable;

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
    private String caseFilingRedFlaggedInLandRecords;

}
