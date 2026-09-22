package in.gov.dilrmp.Scheduler.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "linked_aadhaar_report_view_backup")
public class LinkedAadharViewReportBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long stateId;
    private String stateName;
    private Integer lgdCode;
    private Integer totalDistrict;
    private Integer totalTehsils;
    private Integer totalVillages;
    private Integer villagesWithRorLinkedAadhaar;
    private Double villagesWithRorLinkedAadhaarPercent;
    private Integer villagesWith100PercentRorLinkedAadhaar;
    private BigDecimal villagesWith100PercentRorLinkedAadhaarPercent;
    private Integer totalRor;
    private Integer rorLinkedWithAadhaar;
    private BigDecimal rorLinkedWithAadhaarPercent;
    private Integer rorLinkedWithMobileNumber;
    private BigDecimal rorLinkedWithMobileNumberPercent;
    private LocalDate backupDate;
}
