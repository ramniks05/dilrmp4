package in.gov.dilrmp.Scheduler.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Data
@Table(name = "survey_resurvey_report_view_backup")
public class SurveyResurveyViewReportBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate backupDate;
    private Long stateId;
    private String stateName;
    private Integer sortOrder;
    private Integer totalDistrict;
    private Integer totalTehsils;
    private Integer totalVillages;
    private Double totalRuralRevenueArea;
    private Double areaSanctionedForSurvey;
    private Integer villagesDroneFlyingCompleted;
    private Double areaDroneFlyingCompleted;
    private Integer villagesMap1Generated;
    private Integer villagesDraftMapPublished;
    private Integer villagesFinalPromulgationDone;
    private Integer villagesSurveySanctionNotStarted;
    private Double areaSurveySanctionNotStarted;

    private BigDecimal surveyCompletedVillagePercent;
}
