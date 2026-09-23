package in.gov.dilrmp.models.reportDTO.sroModernization;

import lombok.Data;

import java.math.BigDecimal;

/** District-level Modernization of Registration Office (SRO) row. */
@Data
public class DistrictSroModernizationReport {

    private Long districtId;
    private String districtName;
    private Long stateId;
    private String stateName;
    private Long totalTehsils;

    private Integer totalSros;
    private Integer srosUsingOnlineRegistration; // A

    private Integer srosModernisedStateFunds; // B
    private BigDecimal srosModernisedStateFundsPercent; // B/A

    private Integer srosDilrmpSanctioned; // C

    private Integer srosModernisedDilrmpFunds; // D
    private BigDecimal srosModernisedDilrmpFundsPercent; // D/C

    private Integer srosModernisedTotal; // B+D
    private BigDecimal srosModernisedTotalPercent; // (B+D)/A
}
