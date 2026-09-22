package in.gov.dilrmp.models.reportDTO.rcms;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "rcms_report_view", schema = "public")
public class RcmsReportDTO {

    @Id
    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "lgd_code")
    private Integer lgdCode;

    @Column(name = "total_district")
    private Integer totalDistrict;

    @Column(name = "e_rcms_available")
    private String eRcmsAvailable;

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

    @Column(name = "case_filing_red_flagged_in_land_records")
    private String caseFilingRedFlaggedInLandRecords;

    @Column(name = "total_revenue_courts")
    private Integer totalRevenueCourts;

    @Column(name = "revenue_courts_computerized")
    private Integer revenueCourtsComputerized;

    @Column(name = "revenue_courts_computerized_percent")
    private Double revenueCourtsComputerizedPercent;


    @Column(name = "total_tehsils")
    private Integer totalTehsils;


}