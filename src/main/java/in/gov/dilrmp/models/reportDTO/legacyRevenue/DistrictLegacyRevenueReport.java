package in.gov.dilrmp.models.reportDTO.legacyRevenue;

import lombok.Data;

import java.math.BigDecimal;

/** Legacy Revenue Records Digitisation — district-level row. */
@Data
public class DistrictLegacyRevenueReport {

    private Long districtId;
    private String districtName;
    private Long stateId;
    private String stateName;
    private Long totalTehsils;

    private Integer totalPages;
    private Integer digitisedStateFundsPages;
    private BigDecimal digitisedStateFundsPercent;
    private Integer dilrmpSanctionedPages;
    private Integer digitisedDilrmpFundsPages;
    private BigDecimal digitisedDilrmpFundsPercent;
    private Integer totalDigitisedPages;
    private BigDecimal totalDigitisedPercent;
    private Integer digitisedUptoYear;
}
