package in.gov.dilrmp.services.physicalProgressServices;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.repositories.physicalProgressRepositories.StateRcmsViewRepository;
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
public class StateRCMSService {

    @Autowired
    StateRcmsViewRepository stateRcmsViewRepository;


    private Logger logger = LoggerFactory.getLogger(StateRCMSService.class);


    public Map<String, String> createLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("serialNumber", ReportLabels.SERIAL_NUMBER);
        labels.put("stateUT", ReportLabels.STATE_UT);
        labels.put("totalState", ReportLabels.TOTAL_STATE_UT);
        labels.put("numberOfStateWhereAvailable", ReportLabels.NUMBER_OF_STATE_WHERE);
        labels.put("districtName", ReportLabels.DISTRICT_NAME);
        labels.put("totalDistricts", ReportLabels.TOTAL_DISTRICTS);
        labels.put("totalTehsils", ReportLabels.TOTAL_TEHSILS);
        labels.put("total", ReportLabels.TOTAL);
        labels.put("computerized", ReportLabels.COMPUTERIZED_ONLINE);
        labels.put("noOfrevenueCourts", ReportLabels.NUMBER_OF_REVENUE_COURTS);
        labels.put("intergratedWithlandRecords", ReportLabels.INTEGRATED_WITH_LAND_RECORDS);
        labels.put("intergratedWithsro", ReportLabels.INTEGRATED_WITH_SRO);
        labels.put("CourtManagementSystem", ReportLabels.COURT_MANAGEMENT_SYSTEM);
        labels.put("eReveCourtManagementSystem", ReportLabels.E_REVENUE_COURT_MANAGEMENT_SYSTEM);
        labels.put("onlineProcessOfRevenueCourts", ReportLabels.ONLINE_PROCESS_OF_REVENUE_COURT_PROCESSES_AVAILABLE);
        labels.put("attachedDocuments", ReportLabels.ATTACHED_DOCUMENTS);
        labels.put("generationOfCauseList", ReportLabels.CAUSE_LIST_GENERATION);
        labels.put("noticeToDefendants", ReportLabels.DEFENDANT_NOTIFICATION);
        labels.put("filingOfCounterAffidavits", ReportLabels.COUNTER_AFFIDAVIT_FILING);
        labels.put("proceedingsTypingSystem", ReportLabels.PROCEEDINGS_TYPING_SYSTEM);
        labels.put("uploadingRevenueCourtOrders", ReportLabels.UPLOADING_COURT_ORDERS_NOTIFICATION);
        labels.put("whetherProceed",ReportLabels.WHETHER_PROCEEDS);
        labels.put("ProceedComplete",ReportLabels.PROCEEDINGCOMPLETE_PROCEEDS);
        labels.put("whetherLawRecordOnline",ReportLabels.WHETHER_LAW_RECORD_ONLINE_COURT);
        labels.put("LandRecordCHeckedOnline",ReportLabels.LAND_RECORD_CHECKED_ONLINE_COURT);
        labels.put("whetherFilingDirectly",ReportLabels.WHETHER_FILING_DIRECTLY_SYSTEM);
        labels.put("FilingDirectly",ReportLabels.FILING_DIRECTLY_SYSTEM);
        labels.put("landRecordSystemIntegration", ReportLabels.LAND_RECORD_INTEGRATION);
        labels.put("landRecordsCheckedOnline", ReportLabels.ONLINE_LAND_RECORD_CHECK);
        labels.put("number", ReportLabels.NO);
        labels.put("yesNo", ReportLabels.YES_NO);
        labels.put("percentage", ReportLabels.PERCENTAGE);
        labels.put("reportName", ReportLabels.RCMS_REPORT);
        labels.put("grandTotal", ReportLabels.GRAND_TOTAL);
        return labels;
    }

    public List<RcmsReportDTO> getRcmsStateViwe(){

        List<RcmsReportDTO> rcmsList =  stateRcmsViewRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"));
        rcmsList = rcmsList.stream()
                .filter(rcms -> !rcms.getLgdCode().equals(999))
                .collect(Collectors.toList());
        return rcmsList;
    }

    public List<RcmsReportDTO> filterAndSortStateSurveyReport(String parameter, String ascDesc) {
        List<RcmsReportDTO> rcmsList = stateRcmsViewRepository.findAll();
        Comparator<RcmsReportDTO> comparator = Comparator.comparing(RcmsReportDTO::getStateName);
        if ("computerized".equals(parameter)) {
            comparator = Comparator.comparing(RcmsReportDTO::getRevenueCourtsComputerizedPercent);
        }
        if ("DESC".equals(ascDesc)) {
            comparator = comparator.reversed();
        }
        return rcmsList.stream()
                .filter(rcms -> !rcms.getLgdCode().equals(999))
                .sorted(comparator)
                .collect(Collectors.toList());
    }


    public List<RcmsReportDTO> getStateRcmsReportsGrandToatal(){

        return stateRcmsViewRepository.findAllRcmsByStateId(999);
    }

    public List<RcmsReportDTO> getrcmsStateDataByStateId(Integer stateId) {

        return stateRcmsViewRepository.findAllRcmsByStateId(stateId);
    }
}
