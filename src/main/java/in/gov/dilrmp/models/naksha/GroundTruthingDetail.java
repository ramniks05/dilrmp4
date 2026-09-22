package in.gov.dilrmp.models.naksha;


import in.gov.dilrmp.models.administrativeBoundry.State;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "ground-trothing-details")
@Data
public class GroundTruthingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "ulbMaster_id")
    private ULBMaster ulbMaster;


    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @Column(name = "ulb_Wise_field_survey_teams_formed")
    private Integer ulbWiseFieldSurveyTeamsFormed;
    @Column(name = "survey_unit_name_Option")
    private Integer surveyUnitNameOptions;


    @Column(name = "survey_unit_text")
    private String surveyUnitText;

    @Column(name = "type_of_survey_done")
    private Integer typeOfSurveyDone;

    @Column(name = "field_survey_area")
    private Double fieldSurveyArea;  // Field Survey Area in sq.km.

    @Column(name = "area_field_survey_completed_ulb")
    private Double areaFieldSurveyCompletedULB;

    @Column(name = "area_wise_survey_completion_percent")
    private Double areaWiseSurveyCompletionPercent;

    // Survey Unit tracking fields (Fields 16-19)
    @Column(name = "total_survey_units")
    private Integer totalSurveyUnits;

    @Column(name = "survey_units_surveyed")
    private Integer surveyUnitsSurveyed;

    @Column(name = "survey_units_pending")
    private Integer surveyUnitsPending;

    @Column(name = "survey_unit_completion_percent")
    private Double surveyUnitCompletionPercent;

    @Column(name = "type_of_survey_done_for_survey_unit")
    private Integer typeOfSurveyDoneForSurveyUnit;

    @Column(name = "total_properties_for_field_survey_in_survey_unit")
    private Integer totalPropertiesForFieldSurveyInSurveyUnit;

    @Column(name = "no_of_properties_surveyed_in_survey_unit")
    private Integer noOfPropertiesSurveyedInSurveyUnit;

    @Column(name = "property_wise_field_survey_completion_percent")
    private Double propertyWiseFieldSurveyCompletionPercent;

    @Column(name = "total_area_for_field_survey_in_survey_unit")
    private Double totalAreaForFieldSurveyInSurveyUnit;

    @Column(name = "area_where_field_survey_completed_in_survey_unit")
    private Double areaWhereFieldSurveyCompletedInSurveyUnit;

    @Column(name = "area_wise_field_survey_completion_percent_survey_unit")
    private Double areaWiseFieldSurveyCompletionPercentSurveyUnit;

    @Column(name = "work_validated_by_supervisory_officer")
    private Integer workValidatedBySupervisoryOfficer;

    @Column(name = "no_of_claims_with_boundary_disputes")
    private Integer noOfClaimsWithBoundaryDisputes;

    @Column(name = "no_of_claims_with_ownership_conflicts")
    private Integer noOfClaimsWithOwnershipConflicts;

    @Column(name = "no_of_claims_with_data_errors")
    private Integer noOfClaimsWithDataErrors;

    @Column(name = "no_of_claims_with_administrative_issues")
    private Integer noOfClaimsWithAdministrativeIssues;

    @Column(name = "no_of_any_other_claims")
    private Integer noOfAnyOtherClaims;

    @Column(name = "no_of_claims_objections_resolved")
    private Integer noOfClaimsObjectionsResolved;

    @Column(name = "no_of_claims_objections_pending")
    private Integer noOfClaimsObjectionsPending;

    @Column(name = "percent_of_claims_disputes_resolution")
    private Double percentOfClaimsDisputesResolution;

    @Column(name = "total_plots_to_survey")
    private Integer totalPlotsToSurvey;

    @Column(name = "plots_surveyed_completed")
    private Integer plotsSurveyedCompleted;

    @Column(name = "survey_completion_percent")
    private Double surveyCompletionPercent;

    @Column(name = "total_urpro_card_issued")
    private Integer totalUrProCardIssued;

    @Column(name = "claims_objections_received")
    private Integer claimsObjectionsReceived;

    @Column(name = "claims_objections_resolved")
    private Integer claimsObjectionsResolved;

    @Column(name = "claims_resolution_percentage")
    private Double claimsResolutionPercentage;


    @Column(name = "total_final_ur_pro_card_issued")
    private Integer totalFinalUrProCardIssued;

    @Column(name = "create_on_date")
    private LocalDate createOnDate;

    @Column(name = "last_update_on_date")
    private LocalDate updateOnDate;


    @Column(name = "gt_commencement_date")
    private LocalDate groundTruthingCommencementDate;

    @Column(name = "gt_completion_date")
    private LocalDate groundTruthingCompletionDate;

}


