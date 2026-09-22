package in.gov.dilrmp.models.naksha;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Data
@Table(name = "naksha_timeline_backup")
public class TimelineBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timelineBackupID;

    @Column(name = "gd_name")
    private String gdName;

    @Column(name = "state_name")
    private String stateName;
    @Column(name = "ulb_name")
    private String ulbName;

    @Column(name = "soi_package ")
    private String soipackage;

    @Column(name = "technology")
    private String technology;


    @Column(name = "contractor")
    private String contractor;

    @Column(name = "date_Of_Commecement")
    private LocalDate dateOfCommecement;

    @Column(name = "buffer_area_data_acquisition")
    private Double bufferAreaDataAcquisition;

    private LocalDate completionDate;

    private LocalDate tentativeDate;


    @Column(name = "ori_tentative_date")
    private LocalDate oriTentativeDate;
    @Column(name = "ori_completion_date")
    private LocalDate oriCompletionDate;


    @Column(name = "ori_qa_qc_tentative_date")
    private LocalDate oriqaQcTentativeDate;
    @Column(name = "ori_qa_qc_completion_date")
    private LocalDate oriqaQcCompletionDate;


    @Column(name = "milestone2_completion_date")
    private LocalDate milestone2CompletionDate;
    @Column(name = "milestone2_tentative_date")
    private LocalDate milestone2TentativeDate;

    @Column(name = "qa_qc_milestone2_completion_date")
    private LocalDate qaQcmilestone2CompletionDate;
    @Column(name = "qa_qc_milestone2_tentative_date")
    private LocalDate qaQcmilestone2TentativeDate;


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


    @Column(name = "update_on_date")
    private LocalDate updateOnDate;


    @Column(name = "timeline_meeting_date")
    private LocalDate timelineMeetingDate;

    @Column(name = "backup_date")
    private LocalDate backupDate;


}
