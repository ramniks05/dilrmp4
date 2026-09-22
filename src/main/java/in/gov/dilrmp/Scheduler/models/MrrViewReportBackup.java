package in.gov.dilrmp.Scheduler.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "mrr_report_view_backup")
public class MrrViewReportBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate backupDate;
    private Long stateId;
    private String stateName;
    private Integer lgdCode;
    private Integer sortOrder;
    private Integer totalDistrict;
    private Integer totalTehsils;
    private Integer mrrSanctioned;
    private Integer mrrCompleted;
    private Double mrrSanctionedPercent;
    private Double mrrCompletedOutOfSanctionedPercent;
    private Double mrrCompletedOutOfTotalTehsilsPercent;

}
