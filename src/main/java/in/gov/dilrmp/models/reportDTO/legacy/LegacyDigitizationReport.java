package in.gov.dilrmp.models.reportDTO.legacy;

import lombok.Data;

import java.math.BigDecimal;

/** //v5 Legacy Registered Documents Digitization — state-level row. */
@Data
public class LegacyDigitizationReport {

    private Long stateId;
    private String stateName;
    private Integer lgdCode;
    private Integer totalDistrict;
    private Integer totalTehsils;

    private Integer legacyTotalPages;
    private Integer legacyDigitisedStateFundsPages;
    private BigDecimal legacyDigitisedStateFundsPercent;
    private Integer legacyDilrmpSanctionedPages;
    private Integer legacyDigitisedDilrmpFundsPages;
    private BigDecimal legacyDigitisedDilrmpFundsPercent;
    private Integer legacyTotalDigitisedPages;
    private BigDecimal legacyTotalDigitisedPercent;
    private Integer legacyDigitisedUptoYear;

    private String formattingTotalDistrict;
    private String formattingTotalTehsils;
    private String formattingLegacyTotalPages;
    private String formattingLegacyDigitisedStateFundsPages;
    private String formattingLegacyDilrmpSanctionedPages;
    private String formattingLegacyDigitisedDilrmpFundsPages;
    private String formattingLegacyTotalDigitisedPages;
}
