package in.gov.dilrmp.models.reportDTO.linkedaadhaar;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "district_linked_aadhaar_report_view")
public class DistrictLinkedAadhaarViewReport {

    @Id
    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "district_lgd_code")
    private Integer districtLgdCode;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "total_tehsils")
    private Long totalTehsils;

    @Column(name = "total_villages")
    private Long totalVillages;

    @Column(name = "villages_with_ror_linked_aadhaar")
    private Long villagesWithRorLinkedAadhaar;

    @Column(name = "villages_with_ror_linked_aadhaar_percent")
    private Double villagesWithRorLinkedAadhaarPercent;

    @Column(name = "villages_with_100_percent_ror_linked_aadhaar")
    private Integer villagesWith100PercentRorLinkedAadhaar;

    @Column(name = "villages_with_100_percent_ror_linked_aadhaar_percent")
    private Double villagesWith100PercentRorLinkedAadhaarPercent;

    @Column(name = "total_ror")
    private Long totalRor;

    @Column(name = "ror_linked_with_aadhaar")
    private Integer rorLinkedWithAadhaar;

    @Column(name = "ror_linked_with_aadhaar_percent")
    private Double rorLinkedWithAadhaarPercent;

    @Column(name = "ror_linked_with_mobile_number")
    private Integer rorLinkedWithMobileNumber;

    @Column(name = "ror_linked_with_mobile_number_percent")
    private Double rorLinkedWithMobileNumberPercent;

    //v5 Aadhaar: RoR Linked with Address
    @jakarta.persistence.Transient
    private Integer rorLinkedWithAddress;
    @jakarta.persistence.Transient
    private Double rorLinkedWithAddressPercent;

    //v5 Aadhaar: No. of Land owners block
    @jakarta.persistence.Transient
    private Integer totalLandOwners;
    @jakarta.persistence.Transient
    private Integer landOwnersLinkedWithAadhaar;
    @jakarta.persistence.Transient
    private Double landOwnersLinkedWithAadhaarPercent;
    @jakarta.persistence.Transient
    private Integer landOwnersLinkedWithMobile;
    @jakarta.persistence.Transient
    private Double landOwnersLinkedWithMobilePercent;
    @jakarta.persistence.Transient
    private Integer landOwnersLinkedWithAddress;
    @jakarta.persistence.Transient
    private Double landOwnersLinkedWithAddressPercent;
}
