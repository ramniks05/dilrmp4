package in.gov.dilrmp.models.naksha;
import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "naksha_entry",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"state_id"})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NakshaMISDataEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "ulbMaster_id")
    private ULBMaster ulbMaster;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;
    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "muser_id")
    private User muser;


    @Column(name = "nodal_department_name")
    private String nodalDepartmentName;

    @Column(name = "contectDetails")
    private String contectDetails;

    @Column(name = "phone_Number")
    private String phonenumber;

    @Column(name = "email")
    private String email;

    @Column(name = "nodalOfficerName")
    private String nodalOfficerName;

    @Column(name = "nodal_officer_appointed")
    private String nodalOfficerAppointed;

    @Column(name = "state_level_monitoring_committee")
    private String stateLevelMonitoringCommittee;

    @Column(name = "total_spmu_positions_sanctioned")
    private Integer totalSPMUPositionsSanctioned;

    @Column(name = "spmu_recruitment_completed")
    private String spmuRecruitmentCompleted;

    @Column(name = "total_professionals_recruited")
    private Integer totalProfessionalsRecruited;

    @Column(name = "teams_Formed_For_sanctioned")
    private Integer teamsFormedForsanctioned;

    @Column(name = "teams_formed_for_field_survey")
    private Integer teamsFormedForFieldSurvey;

    @Column(name = "rovers_Sanctioned")
    private Integer roversSanctioned;

    @Column(name = "rovers_procured_for_field_survey")
    private Integer roversProcuredForFieldSurvey;

    @Column(name = "property_tax_data_obtained")
    private String propertyTaxDataObtained;

    @Column(name = "property_tax_data_digitized")
    private String propertyTaxDataDigitized;

    @Column(name = "slc_Status")
    private String slcStatus;

    @Column(name = "legal_framework_urban_survey")
    private String legal_framework_urban_survey;

    @Column(name = "cbAwareness_plans")
    private String cbAwarenessPlans;

    @Column(name = "update_on_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateOnDate;




//Newly Added fields:


    @Column(name = "survey_unit_name")
    private String surveyUnitName;

    @Column(name = "survey_unit_name_Option")
    private Integer surveyUnitNameOptions;

    @Column(name = "total_plots_to_survey")
    private Integer totalPlotsToSurvey;

    @Column(name = "plots_surveyed_completed")
    private Integer plotsSurveyedCompleted;

    @Column(name = "survey_completion_percent")
    private Double surveyCompletionPercent;



    @Column(name = "ulbs_ground_truthing_completed")
    private Integer ulbsGroundTruthingCompleted;

    @Column(name = "data_uploaded_on_naksha_webgis_portal")
    private String dataUploadedOnNakshaWebGISPortal;  // Expected values: "Yes"/"No"

    @Column(name = "total_urpro_card_issued")
    private Integer totalUrProCardIssued;

    @Column(name = "iec_activities_remarks", columnDefinition = "TEXT")
    private String iecActivitiesRemarks;


    //New added column 18-8-25
    @Column(name = "rover_procurement_status")
    private Integer roverProcurementStatus;

    @Column(name = "legal_framework_urban_survey_status")
    private Integer legalFrameworkUrbanSurveyStatus;

    @Column(name = "legal_framework_amendment_status")
    private Integer legalFrameworkAmendmentStatus;

    @Column(name = "slc_meeting_conducted")
    private Integer slcMeetingConducted;

    @Column(name = "slc_meeting_date")
    private LocalDate slcMeetingDate;
    @Column(name = "ulb_Wise_field_survey_teams_formed")
    private Integer ulbWiseFieldSurveyTeamsFormed;

    @Column(name = "claims_objections_received")
    private Integer claimsObjectionsReceived;

    @Column(name = "total_final_ur_pro_card_issued")
    private Integer totalFinalUrProCardIssued;


    @Column(name = "ward_level_shapefile_status")
    private Integer wardLevelShapefileStatus; // Expected values: "Shared"/"Not Shared"

    @Transient
    private Integer grandTotalSPMUPositionsSanctioned;

    @Transient
    private Integer grandtotalProfessionalsRecruited;

    @Transient
    private Integer grandtotalteamsFormedForsanctioned;

    @Transient private Integer grandtotalroversSanctioned;

    @Transient
    private Integer grandtotalteamsFormedForFieldSurvey;

    @Transient private Integer grandtotalroversProcuredForFieldSurvey;

    //Newly Added fields:

    @Transient private Integer grandtotalulbsGroundTruthingCompleted;


    @Transient private Integer grandtotaltotalPlotsToSurvey;

    @Transient private Integer grandtotalplotsSurveyedCompleted;

    @Transient private Integer grandtotalUrProCardIssued;
}
