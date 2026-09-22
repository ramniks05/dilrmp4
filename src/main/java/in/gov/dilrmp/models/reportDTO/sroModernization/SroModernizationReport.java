package in.gov.dilrmp.models.reportDTO.sroModernization;

import lombok.Data;

import java.math.BigDecimal;

/** //v5 Modernization of Registration Office (SRO) — state-level row. */
@Data
public class SroModernizationReport {

    private Long stateId;
    private String stateName;
    private Integer lgdCode;
    private Integer totalDistricts;
    private Integer totalSros;
    private Integer srosUsingOnlineRegistration; // A

    private Integer srosModernisedStateFunds; // B
    private BigDecimal srosModernisedStateFundsPercent; // B/A

    private Integer srosDilrmpSanctioned; // C (DoLR)

    private Integer srosModernisedDilrmpFunds; // D
    private BigDecimal srosModernisedDilrmpFundsPercent; // D/C

    private Integer srosModernisedTotal; // B+D
    private BigDecimal srosModernisedTotalPercent; // (B+D)/A

    private String formattingTotalDistricts;
    private String formattingTotalSros;
    private String formattingSrosUsingOnlineRegistration;
    private String formattingSrosModernisedStateFunds;
    private String formattingSrosDilrmpSanctioned;
    private String formattingSrosModernisedDilrmpFunds;
    private String formattingSrosModernisedTotal;
}
