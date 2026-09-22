package in.gov.dilrmp.models.reportDTO.linkedaadhaar;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "linked_aadhaar_report_view")
public class LinkedAadharViewReport {

    @Id
    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "lgd_code")
    private Integer lgdCode;

    @Column(name = "total_district")
    private Integer totalDistrict;

    @Transient
    private String formattingTotalDistrict;

    @Transient
    private String formattingTotalTehsils;

    @Column(name = "total_tehsils")
    private Integer totalTehsils;

    @Column(name = "total_villages")
    private Integer totalVillages;

    @Transient
    private String formattingTotalVillages;

    @Column(name = "villages_with_ror_linked_aadhaar")
    private Integer villagesWithRorLinkedAadhaar;

    @Transient
    private String formattingvillagesWithRorLinkedAadhaar;

    @Column(name = "villages_with_ror_linked_aadhaar_percent")
    private Double villagesWithRorLinkedAadhaarPercent;

    @Column(name = "villages_with_100_percent_ror_linked_aadhaar")
    private Integer villagesWith100PercentRorLinkedAadhaar;

    @Transient
    private String formattingvillagesWith100PercentRorLinkedAadhaar;

    @Column(name = "villages_with_100_percent_ror_linked_aadhaar_percent")
    private BigDecimal villagesWith100PercentRorLinkedAadhaarPercent;

    @Column(name = "total_ror")
    private Integer totalRor;

    @Transient
    private String formattingtotalRor;

    @Column(name = "ror_linked_with_aadhaar")
    private Integer rorLinkedWithAadhaar;

    @Transient
    private String formattingrorLinkedWithAadhaar;

    @Column(name = "ror_linked_with_aadhaar_percent")
    private BigDecimal rorLinkedWithAadhaarPercent;

    @Column(name = "ror_linked_with_mobile_number")
    private Integer rorLinkedWithMobileNumber;

    @Transient
    private String formattingrorLinkedWithMobileNumber;

    @Column(name = "ror_linked_with_mobile_number_percent")
    private BigDecimal rorLinkedWithMobileNumberPercent;

}
