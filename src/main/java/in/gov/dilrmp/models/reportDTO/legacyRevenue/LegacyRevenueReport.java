package in.gov.dilrmp.models.reportDTO.legacyRevenue;

import lombok.Data;

import java.math.BigDecimal;

/** Legacy Revenue Records Digitisation — state-level row. */
@Data
public class LegacyRevenueReport {

    private Long stateId;
    private String stateName;
    private Integer lgdCode;
    private Integer totalDistrict;
    private Integer totalTehsils;

    private Integer totalPages;
    private Integer digitisedStateFundsPages;
    private BigDecimal digitisedStateFundsPercent;
    private Integer dilrmpSanctionedPages;
    private Integer digitisedDilrmpFundsPages;
    private BigDecimal digitisedDilrmpFundsPercent;
    private Integer totalDigitisedPages;
    private BigDecimal totalDigitisedPercent;
    private Integer digitisedUptoYear;

    private String formattingTotalDistrict;
    private String formattingTotalTehsils;
    private String formattingTotalPages;
    private String formattingDigitisedStateFundsPages;
    private String formattingDilrmpSanctionedPages;
    private String formattingDigitisedDilrmpFundsPages;
    private String formattingTotalDigitisedPages;
}
