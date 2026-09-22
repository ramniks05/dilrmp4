package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.models.reportDTO.ComparisonReportDTO.ComparisonReportDTO;

import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictAadhaarLinkingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComparisonDateByMisReportService {
    @Autowired
    private DistrictAadhaarLinkingRepository comparisonRepo;

    public List<ComparisonReportDTO> getDataDifferences(LocalDate date1, LocalDate date2) {
        List<Object[]> objects = new ArrayList<>();

        if (date1 != null && date2 == null) {
            // Only date1 is provided, call findComparisonReportData
            objects = comparisonRepo.findComparisonReportData(date1);
        } else if (date1 != null && date2 != null) {
            // Both date1 and date2 are provided, call findDataDifferences
            objects = comparisonRepo.findDataDifferences(date1, date2);
        } else {
            // If date1 is null, handle this as an error or return an empty list
            throw new IllegalArgumentException("date1 cannot be null");
        }

        // Initialize a list to store the ComparisonReportDTOs
        List<ComparisonReportDTO> comparisonReportDTOList = new ArrayList<>();

        // Iterate over the result set (Object[]) and map to ComparisonReportDTO
        for (Object[] object : objects) {
            ComparisonReportDTO dto = new ComparisonReportDTO();
            dto.setStateName((String) object[0]);

            // Create a DecimalFormat instance for Indian numbering system
            DecimalFormat df = new DecimalFormat("#,##,###");

            // Convert Integer values to formatted strings using the Indian numbering format
            Integer totalDigitizedMapsFmbTippans = (Integer) object[6];
            Integer digitizedCadastralMaps = (Integer) object[17];
            Integer totalDigitizedFmbTippans = (totalDigitizedMapsFmbTippans != null ? totalDigitizedMapsFmbTippans : 0)
                    - (digitizedCadastralMaps != null ? digitizedCadastralMaps : 0);

            dto.setTotalTehsilsDiff(formatNumber((Integer) object[1], df));
            dto.setTotalVillagesDiff(formatNumber((Integer) object[2], df));
            dto.setTotalRorDiff(formatNumber((Integer) object[3], df));
            dto.setVillagesClrCompletedDiff(formatNumber((Integer) object[4], df));
            dto.setTotalMapsFmbTippansDiff(formatNumber((Integer) object[5], df));
            dto.setTotalDigitizedMapsFmbTippansDiff(formatNumber(totalDigitizedMapsFmbTippans, df));
            dto.setTotalDigitizedFmbTippansDiff(formatNumber(totalDigitizedFmbTippans, df));
            dto.setVillagesLinkedWithRorDiff(formatNumber((Integer) object[7], df));
            dto.setMrrSanctionedDiff(formatNumber((Integer) object[8], df));
            dto.setMrrCompletedDiff(formatNumber((Integer) object[9], df));
            dto.setVillagesFinalPromulgationDoneDiff(formatNumber((Integer) object[10], df));
            dto.setNoOfSroInStateDiff(formatNumber((Integer) object[11], df));
            dto.setNoOfSroUsingOnlineRegistrationDiff(formatNumber((Integer) object[12], df));
            dto.setRorComputerizedDiff(formatNumber((Integer) object[13], df));
            dto.setRorLinkedWithAadhaarDiff(formatNumber((Integer) object[14], df));
            dto.setDistrictsWithGenderBasedOwnershipDiff(formatNumber((Integer) object[15], df));
            dto.setTotalCadastralMapsDiff(formatNumber((Integer) object[16], df));
            dto.setDigitizedCadastralMapsDiff(formatNumber(digitizedCadastralMaps, df));
            dto.setVillagesGeoreferencedDiff(formatNumber((Integer) object[18], df));
            dto.setTotalLandParcelsDiff(formatNumber((Integer) object[19], df));
            dto.setLandParcelsWithUlipnDiff(formatNumber((Integer) object[20], df));
            dto.setGeoreferencedLandParcelsDiff(formatNumber((Integer) object[21], df));
            dto.setRevenueCourtsComputerizedDiff(formatNumber((Integer) object[22], df));

            comparisonReportDTOList.add(dto);
        }

        return comparisonReportDTOList;
    }

    // Helper method to format Integer values
    private String formatNumber(Integer number, DecimalFormat df) {
        return number != null ? df.format(number) : "0";
    }


}
