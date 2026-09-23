package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.sroModernization.DistrictSroModernizationReport;
import in.gov.dilrmp.models.reportDTO.sroModernization.SroModernizationReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictMrrViewRepository;
import in.gov.dilrmp.repositories.physicalProgressRepositories.StateSroViewRepository;
import in.gov.dilrmp.utils.SroModernizationReportBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SroModernizationReportService {

    @Autowired
    StateSroViewRepository sroViewRepository;
    @Autowired
    DistrictMrrViewRepository districtMrrViewRepository;
    @Autowired
    SroModernizationReportBuilder sroModernizationReportBuilder;

    public Map<String, String> createLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("serialNumber", ReportLabels.SERIAL_NUMBER);
        labels.put("stateUT", ReportLabels.STATE_UT);
        labels.put("districtName", ReportLabels.DISTRICT_NAME);
        labels.put("totalDistricts", ReportLabels.TOTAL_DISTRICTS);
        labels.put("totalTehsils", ReportLabels.TOTAL_TEHSILS);
        labels.put("totalSros", ReportLabels.TOTAL_SROS);
        labels.put("onlineRegistration", ReportLabels.SROS_USING_ONLINE_REGISTRATION_A);
        labels.put("modernisedStateFunds", ReportLabels.SROS_MODERNISED_STATE_FUNDS);
        labels.put("sanctionedDilrmp", ReportLabels.SROS_SANCTIONED_DILRMP);
        labels.put("modernisedDilrmpFunds", ReportLabels.SROS_MODERNISED_DILRMP_FUNDS);
        labels.put("modernisedTotal", ReportLabels.SROS_MODERNISED_TOTAL);
        labels.put("nos", ReportLabels.NOS);
        labels.put("percentage", ReportLabels.PERCENTAGE);
        labels.put("reportName", ReportLabels.SRO_MODERNIZATION_REPORT);
        labels.put("grandTotal", ReportLabels.GRAND_TOTAL);
        return labels;
    }

    public List<SroModernizationReport> getAllFormattedList() {
        List<SroModernizationReport> list = sroViewRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"))
                .stream()
                .filter(sro -> sro.getLgdCode() == null || !sro.getLgdCode().equals(999))
                .map(sroModernizationReportBuilder::fromSroView)
                .collect(Collectors.toList());
        sroModernizationReportBuilder.enrichStateReports(list);
        return list;
    }

    public List<SroModernizationReport> getGrandTotalFormatted() {
        List<SroModernizationReport> list = sroViewRepository.findAllSroByStateId(999).stream()
                .map(sroModernizationReportBuilder::fromSroView)
                .collect(Collectors.toList());
        sroModernizationReportBuilder.enrichStateReports(list);
        return list;
    }

    public List<SroModernizationReport> getGrandTotalRaw() {
        return getGrandTotalFormatted();
    }

    public List<SroModernizationReport> getByStateId(Integer stateId) {
        List<SroModernizationReport> list = sroViewRepository.findAllSroByStateId(stateId).stream()
                .map(sroModernizationReportBuilder::fromSroView)
                .collect(Collectors.toList());
        sroModernizationReportBuilder.enrichStateReports(list);
        return list;
    }

    public List<DistrictSroModernizationReport> getDistrictListByStateId(Long stateId) {
        List<DistrictMrrViewReport> districts = districtMrrViewRepository.findByStateId(stateId);
        List<DistrictSroModernizationReport> list = districts.stream()
                .map(sroModernizationReportBuilder::fromMrrDistrict)
                .sorted(Comparator.comparing(
                        r -> r.getDistrictName() == null ? "" : r.getDistrictName(),
                        String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        sroModernizationReportBuilder.enrichDistrictReports(list, stateId);
        return list;
    }

    public List<SroModernizationReport> getStateTotalsForDistrictPage(Long stateId) {
        List<SroModernizationReport> list = sroViewRepository.findAllSroByStateId(stateId.intValue()).stream()
                .map(sroModernizationReportBuilder::fromSroView)
                .collect(Collectors.toList());
        sroModernizationReportBuilder.enrichStateTotalsForDistrictPage(list, stateId);
        return list;
    }
}
