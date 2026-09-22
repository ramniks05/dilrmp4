package in.gov.dilrmp.Scheduler.models.clr;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@Entity
public class DistrictClrReportViewBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private LocalDate backupDate;
    private Long districtId;
    private Integer districtLgdCode;
    private String districtName;
    private Long stateId;
    private String stateName;
    private Integer totalTehsils;
    private Integer totalVillages;
    private Integer totalRor;
    private Integer rorComputerized;
    private BigDecimal rorComputerizedPercent;
    private Integer villagesClrCompleted;
    private BigDecimal clrCompletionPercent;
    private Integer totalLandOwners;
    private String districtsWithGenderBasedOwnership;
    private Integer totalMaleLandOwners;
    private Integer totalFemaleLandOwners;
    private Integer totalOwners;
}
