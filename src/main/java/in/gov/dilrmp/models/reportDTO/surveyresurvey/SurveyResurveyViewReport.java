package in.gov.dilrmp.models.reportDTO.surveyresurvey;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "survey_resurvey_report_view", schema = "public")
public class SurveyResurveyViewReport {

    @Id
    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "total_district")
    private Integer totalDistrict;

    @Transient
    private String formattingTotalDistrict;

    @Column(name = "total_tehsils")
    private Integer totalTehsils;

    @Transient
    private String formattingTotalTehsils;

    @Column(name = "total_villages")
    private Integer totalVillages;
    @Transient
    private String formattingTotalVillages;

    @Column(name = "total_rural_revenue_area")
    private Double totalRuralRevenueArea;


    @Column(name = "area_sanctioned_for_survey")
    private Double areaSanctionedForSurvey;

    @Column(name = "villages_drone_flying_completed")
    private Integer villagesDroneFlyingCompleted;

    @Transient
    private String formattingvillagesDroneFlyingCompleted;

    @Column(name = "area_drone_flying_completed")
    private Double areaDroneFlyingCompleted;

    @Column(name = "villages_map1_generated")
    private Integer villagesMap1Generated;

    @Transient
    private String formattingvillagesMap1Generated;

    @Column(name = "villages_draft_map_published")
    private Integer villagesDraftMapPublished;

    @Transient
    private String formattingvillagesDraftMapPublished;

    @Column(name = "villages_final_promulgation_done")
    private Integer villagesFinalPromulgationDone;

    @Transient
    private String formattingvillagesFinalPromulgationDone;

    @Column(name = "villages_survey_sanction_not_started")
    private Integer villagesSurveySanctionNotStarted;

    @Transient
    private String formattingvillagesSurveySanctionNotStarted;

    @Column(name = "area_survey_sanction_not_started")
    private Double areaSurveySanctionNotStarted;

    @Column(name = "survey_completed_village_percent")
    private BigDecimal surveyCompletedVillagePercent;

//New Added for survey area format
    @Transient
    private String formatetotalRuralRevenueArea;
    @Transient
    private String fomateareaSanctionedForSurvey;
    @Transient
    private String formateareaDroneFlyingCompleted;

    @Transient
    private String formateareaSurveySanctionNotStarted;

}