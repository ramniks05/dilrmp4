package in.gov.dilrmp.models.reportDTO.surveyresurvey;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "district_survey_resurvey_report_view", schema = "public")
public class DistrictSurveyResurveyViewReport {

    @Id
    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "district_lgd_code")
    private Integer districtLgdCode;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "total_tehsils")
    private Long totalTehsils;

    @Column(name = "total_villages")
    private Long totalVillages;

    @Column(name = "total_rural_revenue_area")
    private Double totalRuralRevenueArea;

    @Column(name = "area_sanctioned_for_survey")
    private Double areaSanctionedForSurvey;

    @Column(name = "villages_drone_flying_completed")
    private Long villagesDroneFlyingCompleted;

    @Column(name = "area_drone_flying_completed")
    private Double areaDroneFlyingCompleted;

    @Column(name = "villages_map1_generated")
    private Long villagesMap1Generated;

    @Column(name = "villages_draft_map_published")
    private Long villagesDraftMapPublished;

    @Column(name = "villages_final_promulgation_done")
    private Long villagesFinalPromulgationDone;

    @Column(name = "villages_survey_sanction_not_started")
    private Long villagesSurveySanctionNotStarted;

    @Column(name = "area_survey_sanction_not_started")
    private Double areaSurveySanctionNotStarted;

    @Column(name = "survey_completed_village_percent")
    private Double surveyCompletedVillagePercent;
}
