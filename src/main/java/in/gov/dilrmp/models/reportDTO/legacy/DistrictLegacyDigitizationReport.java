package in.gov.dilrmp.models.reportDTO.legacy;

import lombok.Data;

import java.math.BigDecimal;

/** //v5 Legacy Registered Documents Digitization — district-level row. */
@Data
public class DistrictLegacyDigitizationReport {

    private Long districtId;
    private String districtName;
    private Long stateId;
    private String stateName;
    private Long totalTehsils;

    private Integer legacyTotalPages;
    private Integer legacyDigitisedStateFundsPages;
    private BigDecimal legacyDigitisedStateFundsPercent;
    private Integer legacyDilrmpSanctionedPages;
    private Integer legacyDigitisedDilrmpFundsPages;
    private BigDecimal legacyDigitisedDilrmpFundsPercent;
    private Integer legacyTotalDigitisedPages;
    private BigDecimal legacyTotalDigitisedPercent;
    private Integer legacyDigitisedUptoYear;
}
