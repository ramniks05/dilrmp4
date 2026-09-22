package in.gov.dilrmp.models.naksha;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "naksha_venderEntry", uniqueConstraints = {@UniqueConstraint(columnNames = {"ulb_master_id"})})
@Data
public class NakshaMISDataEntrySolrAndVender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ulb_master_id")
    private Long ulbMasterId;

    @Column(name = "ulb_name")
    private String ulbName;

    @Column(name = "gd_id")
    private Integer gdID;

    //Entry Point

    @Column(name = "gd_name")
    private String gdName;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "district_name")
    private String district_Name;

    @Column(name = "officer_name")
    private String officerName;

    @Column(name = "gd_email")
    private String gdEmail;

    @Column(name = "zones_name")
    private String zoneName;

    @Column(name = "contractor")
    private String contractor;


    @Column(name = "technology")
    private String technology;


    @Column(name = "gd_wing")
    private String gdwing;


    @Column(name = "soi_package ")
    private String soipackage;


    @Column(name = "sanctioned_area")
    private Double sanctionedArea;


    @Column(name = "buffer_area_data_acquisition")
    private Double bufferAreaDataAcquisition;

    @Column(name = "update_on_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateOnDate;




    // -------------------- CUMULATIVE COMPLETED --------------------

    @Column(name = "tech1_cumulative")
    private Double tech1Cumulative;


    @Column(name = "tech2_cumulative")
    private Double tech2Cumulative;

    @Column(name = "tech3_cumulative")
    private Double tech3Cumulative;


    // -------------------- PERCENTAGE COMPLETED --------------------

    @Column(name = "tech1_percentage")
    private Double tech1Percentage;

    @Column(name = "tech2_percentage")
    private Double tech2Percentage;

    @Column(name = "tech3_percentage")
    private Double tech3Percentage;



    // -------------------- Grid Completion --------------------
    @Column(name = "total_grids")
    private String totalGrids;

    @Column(name = "grids_completed_last_week")
    private String gridsCompletedLastWeek;

    @Column(name = "total_grids_completed")
    private String totalGridsCompleted;


    // -------------------- Separate Data Processing (ORI) --------------------
    @Column(name = "separate_ori_data_processing_last_week")
    private String separateOriDataProcessingLastWeek;

    @Column(name = "separate_ori_data_processing_cumulative")
    private String separateOriDataProcessingCumulative;

    @Column(name = "separate_ori_data_processing_percentage")
    private String separateOriDataProcessingPercentage;


    // -------------------- Separate QA/QC Data Processing (ORI) --------------------
    @Column(name = "separate_ori_qaqc_data_processing_last_week")
    private String separateOriQaqcDataProcessingLastWeek;

    @Column(name = "separate_ori_qaqc_data_processing_cumulative")
    private String separateOriQaqcDataProcessingCumulative;

    @Column(name = "separate_ori_qaqc_data_processing_percentage")
    private String separateOriQaqcDataProcessingPercentage;



    // -------------------- New added columns FEATURE EXTRACTION (ORI) --------------------
    @Column(name = "feature_extraction_ori_completed_last_week")
    private Double featureExtractionOriCompletedLastWeek;

    @Column(name = "feature_extraction_ori_cumulative_completed")
    private Double featureExtractionOriCumulativeCompleted;

    @Column(name = "feature_extraction_ori_percentage_completed")
    private Double featureExtractionOriPercentageCompleted;



    // -----------------FEATURE EXTRACTION (ORI) (QA/QC) fields--------------------------

    @Column(name = "feature_extraction_ori_qaqc_completed_last_week")
    private Double featureExtractionOriQaqcCompletedLastWeek;
    @Column(name = "feature_extraction_ori_qaqc_cumulative_completed")
    private Double featureExtractionOriQaqcCumulativeCompleted;
    @Column(name = "feature_extraction_ori_qaqc_percentage_completed")
    private Double featureExtractionOriQaqcPercentageCompleted;

    //----------------seprate data for DEM------------------
    @Column(name = "dem_data_processing_last_week")
    private String demDataProcessingLastWeek;
    @Column(name = "dem_data_processing_cumulative")
    private String demDataProcessingCumulative;
    @Column(name = "dem_data_processing_percentage")
    private String demDataProcessingPercentage;


    // -------------------QA/QC fields for Data Acquisition (DEM)-----------------
    @Column(name = "dem_data_processing_qaqc_last_week")
    private String demDataProcessingQaqcLastWeek;
    @Column(name = "dem_data_processing_qaqc_cumulative")
    private String demDataProcessingQaqcCumulative;
    @Column(name = "dem_data_processing_qaqc_percentage")
    private String demDataProcessingQaqcPercentage;


    // -----------------------------DSM Data Acquisition fields--------------
    @Column(name = "dsm_data_processing_last_week")
    private String dsmDataProcessingLastWeek;
    @Column(name = "dsm_data_processing_cumulative")
    private String dsmDataProcessingCumulative;
    @Column(name = "dsm_data_processing_percentage")
    private String dsmDataProcessingPercentage;

    // ------------------------------------QA/QC fields for DSM---------------------
    @Column(name = "dsm_data_processing_qaqc_last_week")
    private String dsmDataProcessingQaqcLastWeek;
    @Column(name = "dsm_data_processing_qaqc_cumulative")
    private String dsmDataProcessingQaqcCumulative;
    @Column(name = "dsm_data_processing_qaqc_percentage")
    private String dsmDataProcessingQaqcPercentage;

    // ------------------------------------DTM Data Acquisition fields------------------
    @Column(name = "dtm_data_processing_last_week")
    private String dtmDataProcessingLastWeek;
    @Column(name = "dtm_data_processing_cumulative")
    private String dtmDataProcessingCumulative;
    @Column(name = "dtm_data_processing_percentage")
    private String dtmDataProcessingPercentage;

    // ----------------------------------------QA/QC fields for DTM-------------------------------
    @Column(name = "dtm_data_processing_qaqc_last_week")
    private String dtmDataProcessingQaqcLastWeek;
    @Column(name = "dtm_data_processing_qaqc_cumulative")
    private String dtmDataProcessingQaqcCumulative;
    @Column(name = "dtm_data_processing_qaqc_percentage")
    private String dtmDataProcessingQaqcPercentage;


    // -------------------- Data Acquisition (3D Mesh Model) --------------------
    @Column(name = "mesh_data_processing_last_week")
    private String meshDataProcessingLastWeek;
    @Column(name = "mesh_data_processing_cumulative")
    private String meshDataProcessingCumulative;
    @Column(name = "mesh_data_processing_percentage")
    private String meshDataProcessingPercentage;

    // ----------------------------------QA/QC fields for Mesh Data Acquisition-----------------
    @Column(name = "mesh_data_processing_qaqc_last_week")
    private String meshDataProcessingQaqcLastWeek;
    @Column(name = "mesh_data_processing_qaqc_cumulative")
    private String meshDataProcessingQaqcCumulative;

    @Column(name = "mesh_data_processing_qaqc_percentage")
    private String meshDataProcessingQaqcPercentage;

    // -----------------------------------FEATURE EXTRACTION (3D Mesh Model) fields---------------------------------
    @Column(name = "mesh_feature_extraction_completed_last_week")
    private Double meshFeatureExtractionCompletedLastWeek;
    @Column(name = "mesh_feature_extraction_cumulative_completed")
    private Double meshFeatureExtractionCumulativeCompleted;
    @Column(name = "mesh_feature_extraction_percentage_completed")
    private Double meshFeatureExtractionPercentageCompleted;

    // ------------------------------FEATURE EXTRACTION (3D Mesh Model) QA/QC fields--------------------------------------
    @Column(name = "mesh_feature_extraction_qaqc_completed_last_week")
    private Double meshFeatureExtractionQaqcCompletedLastWeek;
    @Column(name = "mesh_feature_extraction_qaqc_cumulative_completed")
    private Double meshFeatureExtractionQaqcCumulativeCompleted;
    @Column(name = "mesh_feature_extraction_qaqc_percentage_completed")
    private Double meshFeatureExtractionQaqcPercentageCompleted;

    @Column(name = "total_gc")
    private String totalGC; // nubmer
    @Column(name = "ori_received_back")
    private String oriReceivedBack; // Yes/No
    @Column(name = "ori_submitted_to_state")
    private String oriSubmittedToState; // Yes/No


    @Column(name = "naksha_uploaded_webportal")
    private String nakshaUploadedWebportal; // Yes/No

    @Column(name = "ori_data_uploaded")
    private String oriDataUploaded; // Yes/No

    @Column(name = "two_d_fe_data_uploaded")
    private String twoDFeDataUploaded; // Yes/No

    @Column(name = "survey_unit_uploaded_by_gd")
    private String surveyUnitUploadedByGd; // Yes/No

    @Column(name = "remark")
    private String remark;



    //Transient for Grand Total

    @Transient
    private Double grandTotalSanctionedArea;
    @Transient
    private Double grandTotalBufferAreaDataAcquisition;
    @Transient
    private Double grandTotalTech1Cumulative;
    @Transient
    private Double grandTotalTech2Cumulative;
    @Transient
    private Double grandTotalTech3Cumulative;
    @Transient
    private Integer grandTotalTotalGrids;
    @Transient
    private Integer grandTotalGridsCompletedLastWeek;
    @Transient
    private Integer grandTotalTotalGridsCompleted;
    @Transient
    private Integer grandTotalTotalGC;

    // --- Grand Total for Data Processing (Cumulative Only) ---
    @Transient
    private Double grandTotalSeparateOriDataProcessingCumulative;
    @Transient
    private Double grandTotalDemDataProcessingCumulative;
    @Transient
    private Double grandTotalDsmDataProcessingCumulative;
    @Transient
    private Double grandTotalDtmDataProcessingCumulative;
    @Transient
    private Double grandTotalMeshDataProcessingCumulative;

    // --- Grand Total for QA/QC (Cumulative Only) ---
    @Transient
    private Double grandTotalSeparateOriQaqcDataProcessingCumulative;
    @Transient
    private Double grandTotalDemDataProcessingQaqcCumulative;
    @Transient
    private Double grandTotalDsmDataProcessingQaqcCumulative;
    @Transient
    private Double grandTotalDtmDataProcessingQaqcCumulative;
    @Transient
    private Double grandTotalMeshDataProcessingQaqcCumulative;

    // --- Grand Total for Feature Extraction (Cumulative Only) ---
    @Transient
    private Double grandTotalFeatureExtractionOriCumulativeCompleted;
    @Transient
    private Double grandTotalFeatureExtractionOriQaqcCumulativeCompleted;
    @Transient
    private Double grandTotalMeshFeatureExtractionCumulativeCompleted;
    @Transient
    private Double grandTotalMeshFeatureExtractionQaqcCumulativeCompleted;

    @Transient
    private Double grandTotaltech3LidarSensor;

    @Transient
    private Double grandTotaltwoDFeatextrStereomodeCumulative;

    @Transient
    private Double grandTotaltwoDFeatextrStereomodeCumulativeQaQc;


    @Transient
    private Double grandTotalthreeDFeatextrCumulative;

    @Transient
    private Double grandTotalthreeDFeatextrCumulativeQaQc;



    @Column(name = "tech3_LidarSensor")
    private Double tech3LidarSensor;

    @Column(name = "tech3_LidarSensor_percentage")
    private Double tech3LidarSensorPercentage;

    @Column(name = "tech_status")
    private Integer techStatus;

    @Column(name = "tech2_oblique_status")
    private Integer tech2ObliqueStatus;

    @Column(name = "tech3_oblique_status")
    private Integer tech3ObliqueStatus;

    @Column(name = "tech3_lidar_sensor_status")
    private Integer tech3LidarSensorStatus;


    @Column(name = "ori_cumulative_status")
    private Integer oriCumulativestatus;

    @Column(name = "ori_cumulative_qaqc_status")
    private Integer oriCumulativeQaQcStatus;

    @Column(name = "dsm_submission_status")
    private Integer dsmSubmissionStatus;

    @Column(name = "dsm_submission_qaqc_status")
    private Integer dsmSubmissionQaQcStatus;


    @Column(name = "dtm_submission_status")
    private Integer dtmSubmissionStatus;

    @Column(name = "dtm_submission_qaqc_status")
    private Integer dtmSubmissionQaQcStatus;

    @Column(name = "twoD_featureextraction_status")
    private Integer twoDfeatureextractionStatus;

    @Column(name = "twoD_featureextraction_qaqc_status")
    private Integer twoDfeatureextractionQaQcStatus;



    @Column(name = "twoD_featextr_stereomode_lastweek")
    private Double twoDFeatextrStereomodeLastWeek;

    @Column(name = "twoD_featextr_stereomode_cumulative")
    private Double twoDFeatextrStereomodeCumulative;

    @Column(name = "twoD_featextr_stereomode_percentage")
    private Double twoDFeatextrStereomodePercentage;

    @Column(name = "twoD_featextr_stereomode_status")
    private Integer twoDFeatextrStereomodeStatus;

//Qa/Qc
    @Column(name = "twoD_featextr_stereomode_qaqc_lastweek")
    private Double twoDFeatextrStereomodeLastWeekQaQc;

    @Column(name = "twoD_featextr_stereomode_qaqc_cumulative")
    private Double twoDFeatextrStereomodeCumulativeQaQc;

    @Column(name = "twoD_featextr_stereomode_qaqc_percentage")
    private Double twoDFeatextrStereomodePercentageQaQc;

    @Column(name = "twoD_featextr_stereomode_qaqc_status")
    private Integer twoDFeatextrStereomodeQaQcStatus;





//3d feature exctr

    @Column(name = "threeD_featextr_lastweek")
    private Double threeDFeatextrLastWeek;

    @Column(name = "threeD_milestone_lastweek")
    private Double threeDMilestoneLastWeek;

    @Column(name = "threeD_featextr_cumulative")
    private Double threeDFeatextrCumulative;

    @Column(name = "threeD_milestone_cumulative")
    private Double threeDmilestoneCumulative;

    @Column(name = "threeD_featextr_percentage")
    private Double threeDFeatextrPercentage;

    @Column(name = "threeD_milestone_percentage")
    private Double threeDmilestonePercentage;

    @Column(name = "threeD_featextr_status")
    private Integer threeDFeatextrStatus;

    @Column(name = "threeD_milestone_status")
    private Integer threeDmilestoneStatus;

    //Qa/Qc
    @Column(name = "threeD_featextr_qaqc_lastweek")
    private Double threeDFeatextrLastWeekQaQc;

    @Column(name = "threeD_milestone_qaqc_lastweek")
    private Double threeDmilestoneLastWeekQaQc;

    @Column(name = "threeD_featextr_qaqc_cumulative")
    private Double threeDFeatextrCumulativeQaQc;

    @Column(name = "threeD_milestone_qaqc_cumulative")
    private Double threeDmilestoneCumulativeQaQc;

    @Column(name = "threeD_featextr_qaqc_percentage")
    private Double threeDDFeatextrPercentageQaQc;

    @Column(name = "threeD_milestone_qaqc_percentage")
    private Double threeDmilestonePercentageQaQc;

    @Column(name = "threeD_featextr_qaqc_status")
    private Integer threeDFeatextrQaQcStatus;

    @Column(name = "threeD_milestone_qaqc_status")
    private Integer threeDmilestoneQaQcStatus;


    @Column(name = "threeD_Meshmodel_status")
    private Integer threedMeshModelStatus;

    @Column(name = "threeD_Meshmodel_qaqc_status")
    private Integer threedMeshModelQaQcStatus;

    @Column(name = "aerial_acquisirion_remark")
    private String aerialAcquisirionRemark;




    //Status By date

    private LocalDate completionDate;

    private LocalDate tentativeDate;

    @Column(name = "date_Of_Commecement")
    private LocalDate dateOfCommecement;


    @Column(name = "ori_completion_date")
    private LocalDate oriCompletionDate;

    @Column(name = "ori_tentative_date")
    private LocalDate oriTentativeDate;

    @Column(name = "ori_qa_qc_completion_date")
    private LocalDate oriqaQcCompletionDate;

    @Column(name = "ori_qa_qc_tentative_date")
    private LocalDate oriqaQcTentativeDate;


    @Column(name = "feature_extraction2D_completion_date")
    private LocalDate featureExtraction2DCompletionDate;

    @Column(name = "feature_extraction2D_tentative_date")
    private LocalDate featureExtraction2DTentativeDate;


    @Column(name = "qa_qc_2d_completion_date")
    private LocalDate qaQc2DCompletionDate;

    @Column(name = "qa_qc_2d_tentative_date")
    private LocalDate qaQc2DTentativeDate;

    @Column(name = "stereo_2d_completion_date")
    private LocalDate stereo2DCompletionDate;

    @Column(name = "stereo_2d_tentative_date")
    private LocalDate stereo2DTentativeDate;

    @Column(name = "qa_qc_stereo_2d_completion_date")
    private LocalDate qaQcStereo2DCompletionDate;

    @Column(name = "qa_qc_stereo_2d_tentative_date")
    private LocalDate qaQcStereo2DTentativeDate;



    @Column(name = "threed_feat_ext_completion_date")
    private LocalDate threeDFeatExtCompletionDate;
    @Column(name = "threed_feat_ext_tentative_date")
    private LocalDate threeDFeatExtTentativeDate;

    @Column(name = "threed_feat_ext_qaqc_completion_date")
    private LocalDate threeDFeatExtQaQcCompletionDate;
    @Column(name = "threed_feat_ext_qaqc_tentative_date")
    private LocalDate threeDFeatExtQaQcTentativeDate;



    @Column(name = "threed_milestone_completion_date")
    private LocalDate threeDMilestoneCompletionDate;

    @Column(name = "threed_milestone_tentative_date")
    private LocalDate threeDMilestoneTentativeDate;

    @Column(name = "threed_milestone_qaqc_completion_date")
    private LocalDate threeDMilestoneQaQcCompletionDate;

    @Column(name = "threed_milestone_qaqc_tentative_date")
    private LocalDate threeDMilestoneQaQcTentativeDate;

    @Column(name = "milestone2_tentative_date")
    private LocalDate milestone2TentativeDate;
    @Column(name = "milestone2_completion_date")
    private LocalDate milestone2CompletionDate;

    @Column(name = "qa_qc_milestone2_tentative_date")
    private LocalDate qaQcmilestone2TentativeDate;
    @Column(name = "qa_qc_milestone2_completion_date")
    private LocalDate qaQcmilestone2CompletionDate;




}
