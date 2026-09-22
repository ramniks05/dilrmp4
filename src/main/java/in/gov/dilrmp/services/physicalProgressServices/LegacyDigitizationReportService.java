package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.legacy.DistrictLegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.legacy.LegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictMrrViewRepository;
import in.gov.dilrmp.repositories.physicalProgressRepositories.MRRViewReportRepository;
import in.gov.dilrmp.utils.LegacyDigitizationReportBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LegacyDigitizationReportService {

    @Autowired
    MRRViewReportRepository mrrViewReportRepository;
    @Autowired
    DistrictMrrViewRepository districtMrrViewRepository;
    @Autowired
    LegacyDigitizationReportBuilder legacyDigitizationReportBuilder;

    public Map<String, String> createLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("serialNumber", ReportLabels.SERIAL_NUMBER);
        labels.put("stateUT", ReportLabels.STATE_UT);
        labels.put("totalState", ReportLabels.TOTAL_STATE_UT);
        labels.put("districtName", ReportLabels.DISTRICT_NAME);
        labels.put("totalDistricts", ReportLabels.TOTAL_DISTRICTS);
        labels.put("totalTehsils", ReportLabels.TOTAL_TEHSILS);
        labels.put("reportName", ReportLabels.LEGACY_DIGITIZATION_REPORT);
        labels.put("grandTotal", ReportLabels.GRAND_TOTAL);
        labels.put("number", ReportLabels.NO);
        labels.put("percentage", ReportLabels.PERCENTAGE);
        labels.put("noOfPages", ReportLabels.NO_OF_PAGES);
        labels.put("totalLegacy", ReportLabels.TOTAL_LEGACY_REGISTERED_DOCUMENTS);
        labels.put("digitisedStateFunds", ReportLabels.LEGACY_DIGITIZED_FROM_STATE_FUNDS);
        labels.put("sanctionedDilrmp", ReportLabels.LEGACY_SANCTIONED_UNDER_DILRMP);
        labels.put("completedDilrmp", ReportLabels.LEGACY_COMPLETED_FROM_DILRMP_FUNDS);
        labels.put("totalDigitised", ReportLabels.TOTAL_LEGACY_DIGITIZED);
        labels.put("uptoYear", ReportLabels.LEGACY_DIGITIZED_UPTO_YEAR);
        return labels;
    }

    public List<LegacyDigitizationReport> getAllFormattedList() {
        List<LegacyDigitizationReport> list = mrrViewReportRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"))
                .stream()
                .filter(mrr -> mrr.getLgdCode() == null || !mrr.getLgdCode().equals(999))
                .map(legacyDigitizationReportBuilder::fromMrrState)
                .collect(Collectors.toList());
        legacyDigitizationReportBuilder.enrichStateReports(list);
        return list;
    }

    public List<LegacyDigitizationReport> getGrandTotalFormatted() {
        List<LegacyDigitizationReport> list = mrrViewReportRepository.findAllMrrByStateId(999).stream()
                .map(legacyDigitizationReportBuilder::fromMrrState)
                .collect(Collectors.toList());
        legacyDigitizationReportBuilder.enrichStateReports(list);
        return list;
    }

    public List<LegacyDigitizationReport> getGrandTotalRaw() {
        return getGrandTotalFormatted();
    }

    public List<LegacyDigitizationReport> getByStateId(Integer stateId) {
        List<LegacyDigitizationReport> list = mrrViewReportRepository.findAllMrrByStateId(stateId).stream()
                .map(legacyDigitizationReportBuilder::fromMrrState)
                .collect(Collectors.toList());
        legacyDigitizationReportBuilder.enrichStateReports(list);
        return list;
    }

    public List<LegacyDigitizationReport> filterAndSort(String parameter, String ascDesc) {
        List<LegacyDigitizationReport> list = getAllFormattedList();
        Comparator<LegacyDigitizationReport> comparator = Comparator.comparing(LegacyDigitizationReport::getStateName);
        if ("stateFundsPct".equals(parameter)) {
            comparator = Comparator.comparing(LegacyDigitizationReport::getLegacyDigitisedStateFundsPercent,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        } else if ("dilrmpPct".equals(parameter)) {
            comparator = Comparator.comparing(LegacyDigitizationReport::getLegacyDigitisedDilrmpFundsPercent,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        } else if ("totalPct".equals(parameter)) {
            comparator = Comparator.comparing(LegacyDigitizationReport::getLegacyTotalDigitisedPercent,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        }
        if ("DESC".equals(ascDesc)) {
            comparator = comparator.reversed();
        }
        return list.stream().sorted(comparator).collect(Collectors.toList());
    }

    public List<DistrictLegacyDigitizationReport> getDistrictListByStateId(Long stateId) {
        List<DistrictLegacyDigitizationReport> list = districtMrrViewRepository.findByStateId(stateId).stream()
                .map(legacyDigitizationReportBuilder::fromMrrDistrict)
                .collect(Collectors.toList());
        legacyDigitizationReportBuilder.enrichDistrictReports(list, stateId);
        return list;
    }

    public List<LegacyDigitizationReport> getStateListForDistrictPage(Long stateId) {
        List<LegacyDigitizationReport> list = mrrViewReportRepository.findAllMrrByStateId(stateId.intValue()).stream()
                .map(legacyDigitizationReportBuilder::fromMrrState)
                .collect(Collectors.toList());
        legacyDigitizationReportBuilder.enrichStateTotalsForDistrictPage(list, stateId);
        return list;
    }
}
