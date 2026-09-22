package in.gov.dilrmp.Scheduler.models.linkedaadhaar;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Data
@Entity
public class DistrictLinkedAadhaarViewReportBackup {
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
    private Long villagesWithRorLinkedAadhaar;
    private Double villagesWithRorLinkedAadhaarPercent;
    private Long villagesWith100PercentRorLinkedAadhaar;
    private Double villagesWith100PercentRorLinkedAadhaarPercent;
    private Long totalRor;
    private Long rorLinkedWithAadhaar;
    private Double rorLinkedWithAadhaarPercent;
    private Long rorLinkedWithMobileNumber;
    private Double rorLinkedWithMobileNumberPercent;
}
