package in.gov.dilrmp.services.physicalProgressServices;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.MRRViewReportRepository;
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
public class StateMRRService {

    @Autowired
    MRRViewReportRepository mrrViewReportRepository;

    private Logger logger = LoggerFactory.getLogger(StateMAPService.class);


    public Map<String, String> createLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("serialNumber", ReportLabels.SERIAL_NUMBER);
        labels.put("stateUT", ReportLabels.STATE_UT);
        labels.put("totalState", ReportLabels.TOTAL_STATE_UT);
        labels.put("districtName", ReportLabels.DISTRICT_NAME);
        labels.put("totalDistricts", ReportLabels.TOTAL_DISTRICTS);
        labels.put("totalTehsils", ReportLabels.TOTAL_TEHSILS);
        labels.put("mrrSanctioned", ReportLabels.MRR_SANCTIONED);
        labels.put("mrrCompletedOutOfTotal", ReportLabels.MRR_COMPLETED_OUT_OF_TOTAL);
        labels.put("mrrCompletedOutOfSanctioned", ReportLabels.MRR_COMPLETED_OUT_OF_SANCTIONED);
        labels.put("number", ReportLabels.NO);
        labels.put("percentage", ReportLabels.PERCENTAGE);
        labels.put("reportName", ReportLabels.MRR_REPORT);
        labels.put("grandTotal", ReportLabels.GRAND_TOTAL);
        return labels;
    }


    public List<MrrViewReport> filterAndSortStateMRRReport(String parameter, String ascDesc) {
        List<MrrViewReport> stateClrList = mrrViewReportRepository.findAll();
        Comparator<MrrViewReport> comparator = Comparator.comparing(MrrViewReport::getStateName);
        if ("completedper".equals(parameter)) {
            comparator = Comparator.comparing(MrrViewReport::getMrrCompletedOutOfTotalTehsilsPercent);
        } else if ("sanctioned".equals(parameter)) {
            comparator = Comparator.comparing(MrrViewReport::getMrrCompletedOutOfSanctionedPercent);
        }
        else if ("sanctionedper".equals(parameter)) {
            comparator = Comparator.comparing(MrrViewReport::getMrrSanctionedPercent);
        }
        if ("DESC".equals(ascDesc)) {
            comparator = comparator.reversed();
        }
        return stateClrList.stream()
                .filter(clr -> !clr.getLgdCode().equals(999))
                .map(mrrL -> {
                    mrrL.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(mrrL.getTotalDistrict()));
                    mrrL.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(mrrL.getTotalTehsils()));
                    mrrL.setFormattingMrrCompleted(NumberFormatterUtil.formatWithCommas(mrrL.getMrrCompleted()));
                    mrrL.setFormattingMrrSanctioned(NumberFormatterUtil.formatWithCommas(mrrL.getMrrSanctioned()));
                    return mrrL;
                })
                .sorted(comparator)
                .collect(Collectors.toList());
    }



    public List<MrrViewReport> getStateMrrReportsGrandToatal(){

        return mrrViewReportRepository.findAllMrrByStateId(999);
    }

    public List<MrrViewReport> getStateMrrReportsFormatedGrandToatal(){
        List<MrrViewReport> mrrGrandTotal=mrrViewReportRepository.findAllMrrByStateId(999);
        mrrGrandTotal=mrrGrandTotal.stream()
                .map(mrrL ->{
                    mrrL.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(mrrL.getTotalDistrict()));
                    mrrL.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(mrrL.getTotalTehsils()));
                    mrrL.setFormattingMrrCompleted(NumberFormatterUtil.formatWithCommas(mrrL.getMrrCompleted()));
                    mrrL.setFormattingMrrSanctioned(NumberFormatterUtil.formatWithCommas(mrrL.getMrrSanctioned()));
                    return mrrL;
                })
                .collect(Collectors.toList());
        return mrrGrandTotal;
    }

    public List<MrrViewReport> getAllMrrList(){
        List<MrrViewReport> mrrList = mrrViewReportRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"));
        mrrList = mrrList.stream()
                .filter(mrr -> !mrr.getLgdCode().equals(999))
                .collect(Collectors.toList());
        return mrrList;
    }

    public List<MrrViewReport> getAllMrrFormatedList(){

        List<MrrViewReport> mrrList = mrrViewReportRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"));
        mrrList = mrrList.stream()
                .filter(mrr -> !mrr.getLgdCode().equals(999))
                .map(mrrL-> {
                    mrrL.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(mrrL.getTotalDistrict()));
                    mrrL.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(mrrL.getTotalTehsils()));
                    mrrL.setFormattingMrrCompleted(NumberFormatterUtil.formatWithCommas(mrrL.getMrrCompleted()));
                    mrrL.setFormattingMrrSanctioned(NumberFormatterUtil.formatWithCommas(mrrL.getMrrSanctioned()));
                    return mrrL;
                })
                .collect(Collectors.toList());

        return mrrList;
    }


    public List<MrrViewReport> getMrrStateDataByStateId(Integer stateId) {

        return mrrViewReportRepository.findAllMrrByStateId(stateId);
    }
}
