package in.gov.dilrmp.Scheduler.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Data
@Entity
public class DistrictSurveyResurveyViewReportBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private LocalDate backupDate;
    private Long districtId;
    private Integer districtLgdCode;
    private String districtName;
    private Long stateId;
    private String stateName;
    private Long totalTehsils;
    private Long totalVillages;
    private Double totalRuralRevenueArea;
    private Double areaSanctionedForSurvey;

    private Long villagesDroneFlyingCompleted;
    private Double areaDroneFlyingCompleted;
    private Long villagesMap1Generated;
    private Long villagesDraftMapPublished;
    private Long villagesFinalPromulgationDone;
    private Long villagesSurveySanctionNotStarted;
    private Double areaSurveySanctionNotStarted;
    private Double surveyCompletedVillagePercent;
}
