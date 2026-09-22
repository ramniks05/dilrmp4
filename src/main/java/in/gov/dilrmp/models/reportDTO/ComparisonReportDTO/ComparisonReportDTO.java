package in.gov.dilrmp.models.reportDTO.ComparisonReportDTO;

import lombok.Data;

import java.util.Date;

@Data
public class ComparisonReportDTO {
    private String stateName;

    private String totalTehsilsDiff;

    private String totalVillagesDiff;

    private String totalRorDiff;

    private String rorComputerizedDiff;

    private String villagesClrCompletedDiff;

    private String totalMapsFmbTippansDiff;

    private String totalDigitizedMapsFmbTippansDiff;

    private String totalDigitizedFmbTippansDiff;

    private String villagesLinkedWithRorDiff;

    private String mrrSanctionedDiff;

    private String mrrCompletedDiff;

    private String villagesFinalPromulgationDoneDiff;

    private String noOfSroInStateDiff;
    private String noOfSroUsingOnlineRegistrationDiff;
    private String rorLinkedWithAadhaarDiff;

    private String districtsWithGenderBasedOwnershipDiff;
    private String totalCadastralMapsDiff;
    private String digitizedCadastralMapsDiff;
    private String villagesGeoreferencedDiff;
    private String totalLandParcelsDiff;
    private String georeferencedLandParcelsDiff;

    private String landParcelsWithUlipnDiff;
    private String revenueCourtsComputerizedDiff;

    private String grandTotal;

    private Date backupDate;
}
