package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.DistrictLegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.LegacyRevenueReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictMrrViewRepository;
import in.gov.dilrmp.repositories.physicalProgressRepositories.MRRViewReportRepository;
import in.gov.dilrmp.utils.LegacyRevenueReportBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LegacyRevenueReportService {

    @Autowired
    MRRViewReportRepository mrrViewReportRepository;
    @Autowired
    DistrictMrrViewRepository districtMrrViewRepository;
    @Autowired
    LegacyRevenueReportBuilder legacyRevenueReportBuilder;

    public Map<String, String> createLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("serialNumber", ReportLabels.SERIAL_NUMBER);
        labels.put("stateUT", ReportLabels.STATE_UT);
        labels.put("districtName", ReportLabels.DISTRICT_NAME);
        labels.put("totalDistricts", ReportLabels.TOTAL_DISTRICTS);
        labels.put("totalTehsils", ReportLabels.TOTAL_TEHSILS);
        labels.put("reportName", ReportLabels.LEGACY_REVENUE_REPORT);
        labels.put("percentage", ReportLabels.PERCENTAGE);
        labels.put("noOfPages", ReportLabels.NO_OF_PAGES);
        labels.put("year", ReportLabels.YEAR);
        labels.put("totalLegacy", ReportLabels.TOTAL_LEGACY_REVENUE_RECORDS);
        labels.put("digitisedStateFunds", ReportLabels.LEGACY_REVENUE_DIGITISED_STATE_FUNDS);
        labels.put("sanctionedDilrmp", ReportLabels.LEGACY_REVENUE_SANCTIONED_DILRMP);
        labels.put("completedDilrmp", ReportLabels.LEGACY_REVENUE_COMPLETED_DILRMP);
        labels.put("totalDigitised", ReportLabels.TOTAL_LEGACY_REVENUE_DIGITISED);
        labels.put("uptoYear", ReportLabels.LEGACY_REVENUE_UPTO_YEAR);
        return labels;
    }

    public List<LegacyRevenueReport> getAllFormattedList() {
        List<LegacyRevenueReport> list = mrrViewReportRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"))
                .stream()
                .filter(mrr -> mrr.getLgdCode() == null || !mrr.getLgdCode().equals(999))
                .map(legacyRevenueReportBuilder::fromMrrState)
                .collect(Collectors.toList());
        legacyRevenueReportBuilder.enrichStateReports(list);
        return list;
    }

    public List<LegacyRevenueReport> getGrandTotalFormatted() {
        List<LegacyRevenueReport> list = mrrViewReportRepository.findAllMrrByStateId(999).stream()
                .map(legacyRevenueReportBuilder::fromMrrState)
                .collect(Collectors.toList());
        legacyRevenueReportBuilder.enrichStateReports(list);
        return list;
    }

    public List<LegacyRevenueReport> filterAndSort(String parameter, String ascDesc) {
        List<LegacyRevenueReport> list = getAllFormattedList();
        Comparator<LegacyRevenueReport> comparator = Comparator.comparing(LegacyRevenueReport::getStateName,
                Comparator.nullsLast(String::compareToIgnoreCase));
        if ("stateFundsPct".equals(parameter)) {
            comparator = Comparator.comparing(LegacyRevenueReport::getDigitisedStateFundsPercent,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        } else if ("dilrmpPct".equals(parameter)) {
            comparator = Comparator.comparing(LegacyRevenueReport::getDigitisedDilrmpFundsPercent,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        } else if ("totalPct".equals(parameter)) {
            comparator = Comparator.comparing(LegacyRevenueReport::getTotalDigitisedPercent,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        }
        if ("DESC".equals(ascDesc)) {
            comparator = comparator.reversed();
        }
        return list.stream().sorted(comparator).collect(Collectors.toList());
    }

    public List<DistrictLegacyRevenueReport> getDistrictListByStateId(Long stateId) {
        List<DistrictLegacyRevenueReport> list = districtMrrViewRepository.findByStateId(stateId).stream()
                .map(legacyRevenueReportBuilder::fromMrrDistrict)
                .collect(Collectors.toList());
        legacyRevenueReportBuilder.enrichDistrictReports(list, stateId);
        return list;
    }

    public List<LegacyRevenueReport> getStateListForDistrictPage(Long stateId) {
        List<LegacyRevenueReport> list = mrrViewReportRepository.findAllMrrByStateId(stateId.intValue()).stream()
                .map(legacyRevenueReportBuilder::fromMrrState)
                .collect(Collectors.toList());
        legacyRevenueReportBuilder.enrichStateTotalsForDistrictPage(list, stateId);
        return list;
    }
}
