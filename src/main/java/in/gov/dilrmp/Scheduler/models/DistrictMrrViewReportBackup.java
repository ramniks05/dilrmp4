package in.gov.dilrmp.Scheduler.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Data
@Entity

public class DistrictMrrViewReportBackup {
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
    private Long mrrSanctioned;
    private Long mrrCompleted;
    private Double mrrSanctionedPercent;
    private Double mrrCompletedOutOfSanctionedPercent;
    private Double mrrCompletedOutOfTotalTehsilsPercent;
}
