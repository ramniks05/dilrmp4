package in.gov.dilrmp.services.physicalProgressServices;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.LinkedAadhaarReportRepository;
import in.gov.dilrmp.utils.AadhaarReportV5Enricher;
import in.gov.dilrmp.utils.NumberFormatterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StateAadharService {


    @Autowired
    LinkedAadhaarReportRepository linkedAadhaarReportRepository;
    @Autowired
    AadhaarReportV5Enricher aadhaarReportV5Enricher;

    private Logger logger = LoggerFactory.getLogger(StateAadharService.class);

    public Map<String, String> createLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("serialNumber", ReportLabels.SERIAL_NUMBER);
        labels.put("stateUT", ReportLabels.STATE_UT);
        labels.put("totalState", ReportLabels.TOTAL_STATE_UT);
        labels.put("districtName", ReportLabels.DISTRICT_NAME);
        labels.put("totalDistricts", ReportLabels.TOTAL_DISTRICTS);
        labels.put("totalTehsils", ReportLabels.TOTAL_TEHSILS);
        labels.put("noOfVillages", ReportLabels.NUMBER_OF_VILLAGES);
        labels.put("landowner", ReportLabels.NUMBER_OF_LAND_OWNERS);
        labels.put("ror", ReportLabels.RoR);
        labels.put("total", ReportLabels.TOTAL);
        labels.put("at_least_one_ror_linked_with_aadhaar", ReportLabels.WHERE_AT_LEAST_ONE_RoR_LINKED_WITH_AADHAAR);
        labels.put("100_percent_ror_linked_with_aadhaar", ReportLabels.WHERE_100_PERCENT_RoR_LINKED_WITH_AADHAAR);
        labels.put("aadhaar_linked_with_ror", ReportLabels.WHOSE_AADHAAR_LINKED_WITH_RoR);
        labels.put("linked_with_aadhaar", ReportLabels.LINKED_WITH_AADHAAR);
        labels.put("linked_with_mobile_number", ReportLabels.LINKED_WITH_MOBILE_NUMBER);
        labels.put("linked_with_address", ReportLabels.LINKED_WITH_ADDRESS); //v5
        labels.put("number", ReportLabels.NO);
        labels.put("percentage", ReportLabels.PERCENTAGE);
        labels.put("reportName", ReportLabels.AADHAR_REPORT);
        labels.put("grandTotal", ReportLabels.GRAND_TOTAL);
        return labels;
    }


    public List<LinkedAadharViewReport> getStateLinkedAadhaarReportsGrandToatal(){

        List<LinkedAadharViewReport> list = linkedAadhaarReportRepository.findAllAadhaarByStateId(999);
        aadhaarReportV5Enricher.enrichStateReports(list);
        return list;
    }

    public List<LinkedAadharViewReport> getStateLinkedAadhaarReportsGrandFormateToatal(){
        List<LinkedAadharViewReport> aadharlist=linkedAadhaarReportRepository.findAllAadhaarByStateId(999);
        aadharlist=aadharlist.stream()
                .map(mapL->{
                    mapL.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(mapL.getTotalDistrict()));
                    mapL.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(mapL.getTotalTehsils()));
                    mapL.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(mapL.getTotalVillages()));
                    mapL.setFormattingvillagesWithRorLinkedAadhaar(NumberFormatterUtil.formatWithCommas(mapL.getVillagesWithRorLinkedAadhaar()));
                    mapL.setFormattingvillagesWith100PercentRorLinkedAadhaar(NumberFormatterUtil.formatWithCommas(mapL.getVillagesWith100PercentRorLinkedAadhaar()));
                    mapL.setFormattingtotalRor(NumberFormatterUtil.formatWithCommas(mapL.getTotalRor()));
                    mapL.setFormattingrorLinkedWithAadhaar(NumberFormatterUtil.formatWithCommas(mapL.getRorLinkedWithAadhaar()));
                    mapL.setFormattingrorLinkedWithMobileNumber(NumberFormatterUtil.formatWithCommas(mapL.getRorLinkedWithMobileNumber()));
                    return mapL;
                })
                .collect(Collectors.toList());
        aadhaarReportV5Enricher.enrichStateReports(aadharlist);
        return aadharlist;

    }

  public List<LinkedAadharViewReport> getAllAadhaarList(){

        List<LinkedAadharViewReport> aadhaarList = linkedAadhaarReportRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"));
        aadhaarList = aadhaarList.stream()
                .filter(aadhaar -> !aadhaar.getLgdCode().equals(999))
                .collect(Collectors.toList());
        aadhaarReportV5Enricher.enrichStateReports(aadhaarList);
        return aadhaarList;
    }

    public List<LinkedAadharViewReport> getAllAadhaarFormatList(){

        List<LinkedAadharViewReport> aadhaarList = linkedAadhaarReportRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"));
        aadhaarList = aadhaarList.stream()
                .filter(aadhaar -> !aadhaar.getLgdCode().equals(999))
                .map(mapL->{
                    mapL.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(mapL.getTotalDistrict()));
                    mapL.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(mapL.getTotalTehsils()));
                    mapL.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(mapL.getTotalVillages()));
                    mapL.setFormattingvillagesWithRorLinkedAadhaar(NumberFormatterUtil.formatWithCommas(mapL.getVillagesWithRorLinkedAadhaar()));
                    mapL.setFormattingvillagesWith100PercentRorLinkedAadhaar(NumberFormatterUtil.formatWithCommas(mapL.getVillagesWith100PercentRorLinkedAadhaar()));
                    mapL.setFormattingtotalRor(NumberFormatterUtil.formatWithCommas(mapL.getTotalRor()));
                    mapL.setFormattingrorLinkedWithAadhaar(NumberFormatterUtil.formatWithCommas(mapL.getRorLinkedWithAadhaar()));
                    mapL.setFormattingrorLinkedWithMobileNumber(NumberFormatterUtil.formatWithCommas(mapL.getRorLinkedWithMobileNumber()));
                    return mapL;
                })
                .collect(Collectors.toList());
        aadhaarReportV5Enricher.enrichStateReports(aadhaarList);
        return aadhaarList;
    }


    public List<LinkedAadharViewReport> filterAndSortStateAadharReport(String parameter, String ascDesc) {
        List<LinkedAadharViewReport> aadhaarList = linkedAadhaarReportRepository.findAll();
        aadhaarReportV5Enricher.enrichStateReports(aadhaarList);
        Comparator<LinkedAadharViewReport> comparator = Comparator.comparing(LinkedAadharViewReport::getStateName);
        if ("1link".equals(parameter)) {
            comparator = Comparator.comparing(LinkedAadharViewReport::getVillagesWithRorLinkedAadhaarPercent);
        } else if ("100link".equals(parameter)) {
            comparator = Comparator.comparing(LinkedAadharViewReport::getVillagesWith100PercentRorLinkedAadhaarPercent);
        }
        else if ("aadharlink".equals(parameter)) {
            comparator = Comparator.comparing(LinkedAadharViewReport::getRorLinkedWithAadhaarPercent);
        }
        else if ("mobilelink".equals(parameter)) {
            comparator = Comparator.comparing(LinkedAadharViewReport::getRorLinkedWithMobileNumberPercent);
        }
        else if ("addresslink".equals(parameter)) {
            comparator = Comparator.comparing(LinkedAadharViewReport::getRorLinkedWithAddressPercent,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        }
        if ("DESC".equals(ascDesc)) {
            comparator = comparator.reversed();
        }
        return   aadhaarList.stream()
                .filter(aadhaar -> !aadhaar.getLgdCode().equals(999))
                .map(mapL -> {
                    mapL.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(mapL.getTotalDistrict()));
                    mapL.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(mapL.getTotalTehsils()));
                    mapL.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(mapL.getTotalVillages()));
                    mapL.setFormattingvillagesWithRorLinkedAadhaar(NumberFormatterUtil.formatWithCommas(mapL.getVillagesWithRorLinkedAadhaar()));
                    mapL.setFormattingvillagesWith100PercentRorLinkedAadhaar(NumberFormatterUtil.formatWithCommas(mapL.getVillagesWith100PercentRorLinkedAadhaar()));
                    mapL.setFormattingtotalRor(NumberFormatterUtil.formatWithCommas(mapL.getTotalRor()));
                    mapL.setFormattingrorLinkedWithAadhaar(NumberFormatterUtil.formatWithCommas(mapL.getRorLinkedWithAadhaar()));
                    mapL.setFormattingrorLinkedWithMobileNumber(NumberFormatterUtil.formatWithCommas(mapL.getRorLinkedWithMobileNumber()));
                    return mapL;
                })
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<LinkedAadharViewReport> getAadhaarStateDataByStateId(Integer stateId) {

        List<LinkedAadharViewReport> list = linkedAadhaarReportRepository.findAllAadhaarByStateId(stateId);
        aadhaarReportV5Enricher.enrichStateReports(list);
        return list;
    }
}
