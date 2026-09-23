package in.gov.dilrmp.controllers.pdfExcelPhysicalProgressReports;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


import in.gov.dilrmp.models.dashboard.DashBordDTO;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.legacy.LegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.LegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.sroModernization.SroModernizationReport;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.services.DataEntryForm.StateMISDataEntryService;
import in.gov.dilrmp.services.physicalProgressServices.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import in.gov.dilrmp.component.PdfExporterComponent;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.utils.AadhaarReportExportV5Util;
import in.gov.dilrmp.utils.LegacyDigitizationExportUtil;
import in.gov.dilrmp.utils.LegacyRevenueExportUtil;
import in.gov.dilrmp.utils.MapDigitizationReportExportV5Util;
import in.gov.dilrmp.utils.PdfExporter;
import in.gov.dilrmp.utils.SroModernizationExportUtil;

@Controller
@RequestMapping("physcial/report/pdf")
public class PhyscialProgressReportControllerPdf {
    @Autowired
    StateMAPService stateMAPService;
    @Autowired
    StateCLRService stateCLRService;

    @Autowired
    StateMRRService stateMRRService;
    @Autowired
    StateSurveyService stateSurveyService;
    @Autowired
    StateSROService stateSROService;
    @Autowired
    StateRCMSService stateRCMSService;
    @Autowired
    StateAadharService stateAadharService;
    @Autowired
    StateMISDataEntryService stateMISDataEntryService;
    @Autowired
    LegacyRevenueReportService legacyRevenueReportService;

    DecimalFormat df = new DecimalFormat("0.00");
    DecimalFormat df1 = new DecimalFormat("#.####");
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");


@GetMapping("/clr")
public void getCLRNEWPdf(HttpServletResponse response, HttpSession session) {
    PdfExporterComponent pComponent = new PdfExporterComponent();
    String head[] = {ReportLabels.CLR_REPORT};
    pComponent.setReportHeading(head);
    pComponent.setReportName(ReportLabels.CLR_REPORT);
    //v5: +2 RoR leaf cols +1 Auto-Mutation Facility col
    float col_width[] = {45f, 90f, 50f, 50f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
    pComponent.setCol_width(col_width);
    String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS,
            ReportLabels.TOTAL_VILLAGES, ReportLabels.RoR, ReportLabels.NUMBER_OF_VILLAGES_WHERE_CLR_COMPLETED,
            ReportLabels.TOTAL_NO_OF_LAND_OWNERS, ReportLabels.AVAILABILITY_OF_GENDER_BASED_LAND_OWNERSHIP,
            ReportLabels.WHETHER_ROR_AVAILABLE_ONLINE,ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_AVAILABLE_ONLINE,
            ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_LEGALLY_VALID_IN_STATE,ReportLabels.WHETHER_MUTATION_APPLICATION_SUBMITTED_ONLINE,
            ReportLabels.WHETHER_AUTO_TRIGGERED_MUTATION_FACILITY_AVAILABLE,ReportLabels.WHETHER_AUTO_MUTATION_FACILITY_AVAILABLE,ReportLabels.WHETHER_BANKS_AUTHORIZED_TO_CREATE_CLEAR_MORTGAGE_CHARGE_IN_ROR,
            ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_SRO,ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_REVENUE_COURTS,
            ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_CIVIL_COURTS_THROUGH_E_COURTS_SYSTEM,
            //v5 RoR leaf: Total, Computerized, %, with Cadastral Map, %
            ReportLabels.TOTAL,ReportLabels.COMPUTERIZED,ReportLabels.PERCENTAGE,ReportLabels.ROR_WITH_CADASTRAL_MAP,ReportLabels.PERCENTAGE,ReportLabels.NO,ReportLabels.PERCENTAGE,
            ReportLabels.NUMBER_OF_DISTRICTS_WHERE_AVAILABLE,ReportLabels.MALE,ReportLabels.FEMALE,ReportLabels.TOTAL,ReportLabels.YES_NO,
            ReportLabels.YES_NO,ReportLabels.YES_NO,ReportLabels.YES_NO,ReportLabels.YES_NO,ReportLabels.YES_NO,ReportLabels.YES_NO,
            ReportLabels.NUMBER_OF_DISTRICTS_AUTHORIZED,ReportLabels.NUMBER_OF_BANK_BRANCHES_AUTHORIZED,ReportLabels.YES_NO,ReportLabels.YES_NO,
            ReportLabels.YES_NO
    };
    pComponent.setCol_head(col_head);
    Integer[] rwspn2col = {0, 1, 2, 3, 4,7};
    Integer[] rwspn3col = {};
    Integer[] rwspn4col = {};
    Integer[] rwspn5col = {};
    Integer[] simplecell = {19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40};
    pComponent.setRowspn5(rwspn5col);
    pComponent.setRowspn4(rwspn4col);
    pComponent.setRowspn3(rwspn3col);
    pComponent.setRowspn2(rwspn2col);
    pComponent.setRowspan1(simplecell);
    List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
    // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
    colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 5))); //v5 RoR 3->5
    colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
    colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 4)));
    colspanval.add(new ArrayList<Integer>(Arrays.asList(9, 1)));
    colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 1)));
    colspanval.add(new ArrayList<Integer>(Arrays.asList(11, 1)));
    colspanval.add(new ArrayList<Integer>(Arrays.asList(12, 1)));
    colspanval.add(new ArrayList<Integer>(Arrays.asList(13, 1)));
    colspanval.add(new ArrayList<Integer>(Arrays.asList(14, 1))); //v5 Auto-Mutation Facility
    colspanval.add(new ArrayList<Integer>(Arrays.asList(15, 3)));
    colspanval.add(new ArrayList<Integer>(Arrays.asList(16, 1)));
    colspanval.add(new ArrayList<Integer>(Arrays.asList(17, 1)));
    colspanval.add(new ArrayList<Integer>(Arrays.asList(18, 1)));
    pComponent.setColumnspan(colspanval);
    List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
    // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
    colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 5))); //v5 RoR leaf 3->5
    colsBreak.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
    colsBreak.add(new ArrayList<Integer>(Arrays.asList(8, 4)));
    colsBreak.add(new ArrayList<Integer>(Arrays.asList(15, 3))); //v5 banks shifted after Auto-Mutation
    pComponent.setColumnnumber(colsBreak);
    pComponent.setColumnBreakCountNo(10); //v5: first-level 19, data cols 29 → 29-19=10
    List<StateClrReportView> clrList = (List<StateClrReportView>) session.getAttribute("stateClrList");
    List<StateClrReportView> grandTotal = (List<StateClrReportView>) session.getAttribute("clrGrandTotal");
    List<List<String>> reportDataList = new ArrayList<>();
    AtomicInteger indexHolder = new AtomicInteger();
    if (clrList != null && !clrList.isEmpty()) {
        clrList.forEach(map -> {
            List<String> strings = new ArrayList<>();

            if (map.getStateId() != 999) { // Process only for non-grand total states
                strings.add(Integer.toString(indexHolder.incrementAndGet()));
                strings.add(map.getStateName() != null ? map.getStateName() : "N/A");
                strings.add(map.getFormattingTotalDistrict() != null ? map.getFormattingTotalDistrict().toString() : "N/A");
                strings.add(map.getFormattingTotalTehsils() != null ? map.getFormattingTotalTehsils().toString() : "0");
                strings.add(map.getFormattingTotalVillages() != null ? map.getFormattingTotalVillages().toString() : "0");
                strings.add(map.getFormattingTotalRor() != null ? map.getFormattingTotalRor().toString() : "0");
                strings.add(map.getFormattingRorComputerized() != null ? map.getFormattingRorComputerized().toString() : "0");
                strings.add(map.getRorComputerizedPercent() != null && map.getRorComputerizedPercent().compareTo(BigDecimal.ZERO) != 0 ? map.getRorComputerizedPercent().toString() : "0.0");
                //v5
                strings.add(map.getFormattingRorWithCadastralMap() != null ? map.getFormattingRorWithCadastralMap() : "0");
                strings.add(map.getRorWithCadastralMapPercent() != null && map.getRorWithCadastralMapPercent().compareTo(BigDecimal.ZERO) != 0 ? map.getRorWithCadastralMapPercent().toString() : "0.0");
                strings.add(map.getFormattingvillagesClrCompleted() != null ? map.getFormattingvillagesClrCompleted().toString() : "0");
                strings.add(map.getClrCompletionPercent() != null && map.getClrCompletionPercent().compareTo(BigDecimal.ZERO) != 0 ? map.getClrCompletionPercent().toString() : "0.0");
                strings.add(map.getFormattingTotalLandOwners() != null ? map.getFormattingTotalLandOwners().toString() : "0");
                strings.add(map.getDistrictsWithGenderBasedOwnership() != null ? map.getDistrictsWithGenderBasedOwnership().toString() : "0");
                strings.add(map.getFormattingTotalMaleLandOwners() != null ? map.getFormattingTotalMaleLandOwners().toString() : "0");
                strings.add(map.getFormattingTotalFemaleLandOwners() != null ? map.getFormattingTotalFemaleLandOwners().toString() : "0");
                strings.add(map.getFormattingTotalOwners() != null ? map.getFormattingTotalOwners().toString() : "0");
                // Convert boolean values to "YES" or "NO"
                strings.add(map.getRorAvailableOnline() != null ? map.getRorAvailableOnline() : "NO");
                strings.add(map.getDigitallySignedRorAvailable() != null ? map.getDigitallySignedRorAvailable() : "NO");
                strings.add(map.getDigitallySignedRorLegallyValid() != null ? map.getDigitallySignedRorLegallyValid() : "NO");
                strings.add(map.getOnlineMutationFacility() != null ? map.getOnlineMutationFacility() : "NO");
                strings.add(map.getAutoTriggerMutation() != null ? map.getAutoTriggerMutation() : "NO");
                //v5
                strings.add(map.getAutoMutationFacility() != null ? map.getAutoMutationFacility() : "NO");
                strings.add(map.getBankRedFlagMortgageInLandRecords() != null ? map.getBankRedFlagMortgageInLandRecords() : "NO");
                strings.add(map.getFormattingDistrictsWithBankRedFlagMortgage() != null ? map.getFormattingDistrictsWithBankRedFlagMortgage().toString() : "0");
                strings.add(map.getFormattingBankBranchesWithRedFlagMortgage() != null ? map.getFormattingBankBranchesWithRedFlagMortgage().toString() : "0");
                strings.add(map.getLandRecordsOnlineFromRegistrationSystem() != null ? map.getLandRecordsOnlineFromRegistrationSystem() : "NO");
                strings.add(map.getRevenueCourtProceedingsPaperless() != null ? map.getRevenueCourtProceedingsPaperless() : "NO");
                strings.add(map.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() != null ? map.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() : "NO");
                reportDataList.add(strings);
            }
        });
    }
    List<List<String>> grandTotalData = new ArrayList<>();
    if (grandTotal != null && !grandTotal.isEmpty()) {
        grandTotal.forEach(gt -> {
            List<String> grandTotalList = new ArrayList<>();
            grandTotalList.add("");  // No index for grand total
            grandTotalList.add(gt.getStateName() != null ? "Grand Total" : "N/A");
            grandTotalList.add(gt.getFormattingTotalDistrict() != null ? gt.getFormattingTotalDistrict().toString() : "N/A");
            grandTotalList.add(gt.getFormattingTotalTehsils() != null ? gt.getFormattingTotalTehsils().toString() : "0");
            grandTotalList.add(gt.getFormattingTotalVillages() != null ? gt.getFormattingTotalVillages().toString() : "0");
            grandTotalList.add(gt.getFormattingTotalRor() != null ? gt.getFormattingTotalRor() : "0");
            grandTotalList.add(gt.getFormattingRorComputerized() != null ? gt.getFormattingRorComputerized() : "0");
            grandTotalList.add(gt.getRorComputerizedPercent() != null && gt.getRorComputerizedPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getRorComputerizedPercent()) : "0.0");
            //v5
            grandTotalList.add(gt.getFormattingRorWithCadastralMap() != null ? gt.getFormattingRorWithCadastralMap() : "0");
            grandTotalList.add(gt.getRorWithCadastralMapPercent() != null && gt.getRorWithCadastralMapPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getRorWithCadastralMapPercent()) : "0.0");
            grandTotalList.add(gt.getFormattingvillagesClrCompleted() != null ? gt.getFormattingvillagesClrCompleted() : "0");
            grandTotalList.add(gt.getClrCompletionPercent() != null && gt.getClrCompletionPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getClrCompletionPercent()) : "0.0");
            grandTotalList.add(gt.getFormattingTotalLandOwners() != null ? gt.getFormattingTotalLandOwners() : "0");
            grandTotalList.add(gt.getDistrictsWithGenderBasedOwnership() != null ? String.valueOf(gt.getDistrictsWithGenderBasedOwnership()) : "0");
            grandTotalList.add(gt.getFormattingTotalMaleLandOwners() != null ? gt.getFormattingTotalMaleLandOwners() : "0");
            grandTotalList.add(gt.getFormattingTotalFemaleLandOwners() != null ? gt.getFormattingTotalFemaleLandOwners() : "0");
            grandTotalList.add(gt.getFormattingTotalOwners() != null ? gt.getFormattingTotalOwners() : "0");
            grandTotalList.add(gt.getRorAvailableOnline() != null ? gt.getRorAvailableOnline() : "0");
            grandTotalList.add(gt.getDigitallySignedRorAvailable() != null ? gt.getDigitallySignedRorAvailable() : "0");
            grandTotalList.add(gt.getDigitallySignedRorLegallyValid() != null ? gt.getDigitallySignedRorLegallyValid() : "0");
            grandTotalList.add(gt.getOnlineMutationFacility() != null ? gt.getOnlineMutationFacility() : "0");
            grandTotalList.add(gt.getAutoTriggerMutation() != null ? gt.getAutoTriggerMutation() : "0");
            //v5
            grandTotalList.add(gt.getAutoMutationFacility() != null ? gt.getAutoMutationFacility() : "0");
            grandTotalList.add(gt.getBankRedFlagMortgageInLandRecords() != null ? gt.getBankRedFlagMortgageInLandRecords() : "0");
            grandTotalList.add(gt.getFormattingDistrictsWithBankRedFlagMortgage() != null ? gt.getFormattingDistrictsWithBankRedFlagMortgage() : "0");
            grandTotalList.add(gt.getFormattingBankBranchesWithRedFlagMortgage() != null ? gt.getFormattingBankBranchesWithRedFlagMortgage() : "0");
            grandTotalList.add(gt.getLandRecordsOnlineFromRegistrationSystem() != null ? gt.getLandRecordsOnlineFromRegistrationSystem() : "0");
            grandTotalList.add(gt.getRevenueCourtProceedingsPaperless() != null ? gt.getRevenueCourtProceedingsPaperless() : "0");
            grandTotalList.add(gt.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() != null ? gt.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() : "0");
            grandTotalData.add(grandTotalList);
        });
    }
    pComponent.setReportDataList(reportDataList);
    pComponent.setGrandTotal(grandTotalData);
    PdfExporter.createPdf(pComponent, response);

}

    @GetMapping("/map")
    public void getMAPNewPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {ReportLabels.MAP_DIGITIZATION_REPORT};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.MAP_DIGITIZATION_REPORT);
        //v5: +3 cadastral columns (damaged/missing, good condition, second %)
        float col_width[] = {30f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 40f, 40f, 40f, 50f, 50f, 50f, 40f, 50f, 40f, 40f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 90f};
        pComponent.setCol_width(col_width);
        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.NO_OF_CADASTRAL_MAPS_FMBS_TIPPANS, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS
                , ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.NO_OF_LAND_PARCELS, ReportLabels.NUMBER_OF_DISTRICT_WHERE_CADMAP_FMB_TIPPAN_SHOWING_CURRENT_OWNERSHIP,
                ReportLabels.CADASTRAL_MAPS, ReportLabels.FMBs, ReportLabels.TIPPANS, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.GEO_REFERENCED, ReportLabels.TOTAL, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_LINKED_TO_ROR, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_GEO_REFERENCED, ReportLabels.ULPIN_ASSIGNED, ReportLabels.TOTAL, ReportLabels.GEO_REFERENCED, ReportLabels.ULPIN_ASSIGNED,
                //v5 leaf headers for Cadastral Maps
                ReportLabels.TOTAL, ReportLabels.TOTAL_DAMAGED_MISSING_MAPS, ReportLabels.MAP_IN_GOOD_CONDITION, ReportLabels.DIGITIZED, ReportLabels.DIGITIZED_PERCENT_OF_TOTAL_CADASTRAL_MAPS, ReportLabels.DIGITIZED_PERCENT_OF_GOOD_CONDITION_MAPS,
                ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {8};
        Integer[] rwspn3col = {0, 1, 2, 3};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 12))); //v5 NO_OF_CADASTRAL 9->12
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 5)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 7)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 5)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(9, 6))); //v5 Cadastral Maps 3->6
        colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(11, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(12, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(13, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(14, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(15, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(16, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(17, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(18, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(19, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(20, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(21, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 6))); //v5 cadastral leaf 3->6
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 3)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(6, 3)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(9, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(11, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(12, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(13, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(15, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(16, 2)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(16); //v5: was 13; +3 extras for 6 cadastral leaf cols
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<MapDigitizationReport> mapList = (List<MapDigitizationReport>) session.getAttribute("reportData");
        List<MapDigitizationReport> grandTotal = (List<MapDigitizationReport>) session.getAttribute("mapGrandTotal");
        AtomicInteger indexHolder = new AtomicInteger();

        if (mapList != null && !mapList.isEmpty()) {
            mapList.forEach(map -> {
                List<String> strings = new ArrayList<>();

                if (map.getLgdCode() != 999) {
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getStateName());
                    strings.add(map.getFormattingTotalDistrict());
                    strings.add(map.getFormattingTotalTehsils());
                    MapDigitizationReportExportV5Util.appendCadastralMapRowV5(strings, map, df); //v5
                    strings.add(map.getFormattingtotalFmbs());
                    strings.add(map.getFormattingdigitizedFmbs());
                    strings.add(df.format(map.getDigitizedFmbsPercent()));
                    strings.add(map.getFormattingtotalTippans());
                    strings.add(map.getFormattingdigitizedTippans());
                    strings.add(df.format(map.getDigitizedTippansPercent()));
                    strings.add(map.getFormattingtotalMapsFmbTippans());
                    strings.add(map.getFormattingtotalDigitizedMapsFmbTippans());
                    strings.add(df.format(map.getDigitizedMapsFmbTippansPercent()));
                    strings.add(map.getFormattinggeoreferencedMaps());
                    strings.add(df.format(map.getGeoreferencedMapsPercent()));
                    strings.add(Integer.toString(map.getTotalVillages()));
                    strings.add(map.getFormattingvillagesLinkedWithRor());
                    strings.add(df.format(map.getVillagesLinkedWithRorPercent()));
                    strings.add(map.getFormattingvillagesGeoreferenced());
                    strings.add(df.format(map.getVillagesGeoreferencedPercent()));
                    strings.add(map.getFormattingvillagesWithUlipn());
                    strings.add(df.format(map.getVillagesWithUlipnPercent()));
                    strings.add(map.getFormattingtotalLandParcels());
                    strings.add(map.getFormattinggeoreferencedLandParcels());
                    strings.add(df.format(map.getGeoreferencedLandParcelsPercent()));
                    strings.add(map.getFormattinglandParcelsWithUlipn());
                    strings.add(df.format(map.getLandParcelsWithUlipnPercent()));
                    strings.add(map.getMapsUpdatedBasedOnMutation());
                    reportDataList.add(strings);
                }
            });
        }

        List<List<String>> grandTotalData = new ArrayList<>();

        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(map -> {
                List<String> strings = new ArrayList<>();
                strings.add("");
                strings.add(map.getStateName());
                strings.add(map.getFormattingTotalDistrict());
                strings.add(map.getFormattingTotalTehsils());
                MapDigitizationReportExportV5Util.appendCadastralMapRowV5(strings, map, df); //v5
                strings.add(map.getFormattingtotalFmbs());
                strings.add(map.getFormattingdigitizedFmbs());
                strings.add(df.format(map.getDigitizedFmbsPercent()));
                strings.add(map.getFormattingtotalTippans());
                strings.add(map.getFormattingdigitizedTippans());
                strings.add(df.format(map.getDigitizedTippansPercent()));
                strings.add(map.getFormattingtotalMapsFmbTippans());
                strings.add(map.getFormattingtotalDigitizedMapsFmbTippans());
                strings.add(df.format(map.getDigitizedMapsFmbTippansPercent()));
                strings.add(map.getFormattinggeoreferencedMaps());
                strings.add(df.format(map.getGeoreferencedMapsPercent()));
                strings.add(Integer.toString(map.getTotalVillages()));
                strings.add(map.getFormattingvillagesLinkedWithRor());
                strings.add(df.format(map.getVillagesLinkedWithRorPercent()));
                strings.add(map.getFormattingvillagesGeoreferenced());
                strings.add(df.format(map.getVillagesGeoreferencedPercent()));
                strings.add(map.getFormattingvillagesWithUlipn());
                strings.add(df.format(map.getVillagesWithUlipnPercent()));
                strings.add(map.getFormattingtotalLandParcels());
                strings.add(map.getFormattinggeoreferencedLandParcels());
                strings.add(df.format(map.getGeoreferencedLandParcelsPercent()));
                strings.add(map.getFormattinglandParcelsWithUlipn());
                strings.add(df.format(map.getLandParcelsWithUlipnPercent()));
                strings.add(map.getMapsUpdatedBasedOnMutation());
                grandTotalData.add(strings);
            });
        }

        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);

    }

    @GetMapping("/map-dolr")
    public void getMAPDoLRPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {ReportLabels.MAP_DIGITIZATION_REPORT_MAPFMBTIPPAN};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.MAP_DIGITIZATION_REPORT);
        float col_width[] = {30f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f,40f,40f, 40f};
        pComponent.setCol_width(col_width);
        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.NO_OF_CADASTRAL_MAPS_FMBS_TIPPANS, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS,
                ReportLabels.CADASTRAL_MAPS, ReportLabels.FMBs, ReportLabels.TIPPANS, ReportLabels.TOTAL, ReportLabels.DIGITIZED,
                ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {};
        Integer[] rwspn3col = {0, 1, 2, 3};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {11,12,13,14,15,16,17,18,19,20,21,22};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 9)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(9, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 3)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(6, 3)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(7);
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<MapDigitizationReport> mapList = (List<MapDigitizationReport>) session.getAttribute("reportData");
        List<MapDigitizationReport> grandTotal = (List<MapDigitizationReport>) session.getAttribute("mapGrandTotal");
        AtomicInteger indexHolder = new AtomicInteger();
        if (mapList != null && !mapList.isEmpty()) {
            mapList.forEach(map -> {
                List<String> strings = new ArrayList<>();
                if (map.getLgdCode() != 999) {
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getStateName());
                    strings.add(map.getFormattingTotalDistrict());
                    strings.add(map.getFormattingTotalTehsils());
                    strings.add(map.getFormattingtotalCadastralMaps());
                    strings.add(map.getFormattingdigitizedCadastralMaps());
                    strings.add(df.format(map.getDigitizedCadastralMapsPercent()));
                    strings.add(map.getFormattingtotalFmbs());
                    strings.add(map.getFormattingdigitizedFmbs());
                    strings.add(df.format(map.getDigitizedFmbsPercent()));
                    strings.add(map.getFormattingtotalTippans());
                    strings.add(map.getFormattingdigitizedTippans());
                    strings.add(df.format(map.getDigitizedTippansPercent()));
                    strings.add(map.getFormattingtotalMapsFmbTippans());
                    strings.add(map.getFormattingtotalDigitizedMapsFmbTippans());
                    strings.add(df.format(map.getDigitizedMapsFmbTippansPercent()));
                    reportDataList.add(strings);
                }
            });
        }
        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(map -> {
                List<String> strings = new ArrayList<>();
                strings.add("");
                strings.add(map.getStateName());
                strings.add(map.getFormattingTotalDistrict());
                strings.add(map.getFormattingTotalTehsils());
                strings.add(map.getFormattingtotalCadastralMaps());
                strings.add(map.getFormattingdigitizedCadastralMaps());
                strings.add(df.format(map.getDigitizedCadastralMapsPercent()));
                strings.add(map.getFormattingtotalFmbs());
                strings.add(map.getFormattingdigitizedFmbs());
                strings.add(df.format(map.getDigitizedFmbsPercent()));
                strings.add(map.getFormattingtotalTippans());
                strings.add(map.getFormattingdigitizedTippans());
                strings.add(df.format(map.getDigitizedTippansPercent()));
                strings.add(map.getFormattingtotalMapsFmbTippans());
                strings.add(map.getFormattingtotalDigitizedMapsFmbTippans());
                strings.add(df.format(map.getDigitizedMapsFmbTippansPercent()));
                grandTotalData.add(strings);
            });
        }
        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);

    }
    @GetMapping("/mrr")
    public void getMRRPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {ReportLabels.MRR_REPORT};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.MRR_REPORT);
        float col_width[] = {20f, 65f, 50f, 50f, 90f, 50f, 50f, 50f, 50f,50f};
        pComponent.setCol_width(col_width);
        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.MRR_SANCTIONED, ReportLabels.MRR_COMPLETED_OUT_OF_TOTAL, ReportLabels.MRR_COMPLETED_OUT_OF_SANCTIONED
                , ReportLabels.NO,ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {0, 1, 2, 3};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {7, 8, 9, 10, 11,12};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(3);
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<MrrViewReport> mrrList = (List<MrrViewReport>) session.getAttribute("mrrViewReportList");
        List<MrrViewReport> grandTotal = (List<MrrViewReport>) session.getAttribute("mrrGrandTotal");
        AtomicInteger indexHolder = new AtomicInteger();

        if (mrrList != null && !mrrList.isEmpty()) {
            mrrList.forEach(map -> {
                List<String> strings = new ArrayList<>();
            if (map.getStateId() != 999) {
                // Populate normal row data
                strings.add(Integer.toString(indexHolder.incrementAndGet()));
                strings.add(map.getStateName() != null ? map.getStateName() : "N/A");
                strings.add(map.getFormattingTotalDistrict() != null ? map.getFormattingTotalDistrict() : "0");
                strings.add(map.getFormattingTotalTehsils() != null ? map.getFormattingTotalTehsils() : "0");
                strings.add(map.getFormattingMrrSanctioned() != null ? map.getFormattingMrrSanctioned() : "0");
                strings.add(String.valueOf(map.getMrrSanctionedPercent() != null ? map.getMrrSanctionedPercent() : "0.0"));
                strings.add(map.getFormattingMrrCompleted() != null ? map.getFormattingMrrCompleted() : "0");
                strings.add(String.valueOf(map.getMrrCompletedOutOfTotalTehsilsPercent() != null ? map.getMrrCompletedOutOfTotalTehsilsPercent() : "0.0"));
                strings.add(map.getFormattingMrrCompleted() != null ? map.getFormattingMrrCompleted() : "0");
                strings.add(String.valueOf(map.getMrrCompletedOutOfSanctionedPercent() != null ? map.getMrrCompletedOutOfSanctionedPercent() : "0.0"));
                reportDataList.add(strings);
            }
            });
        }
        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                        grandTotalList.add("");
                        grandTotalList.add(gt.getStateName() != null ? gt.getStateName() : "Grand Total");
                        grandTotalList.add(gt.getFormattingTotalDistrict() != null ? gt.getFormattingTotalDistrict() : "N/A");
                        grandTotalList.add(gt.getFormattingTotalTehsils() != null ? gt.getFormattingTotalTehsils() : "0");
                        grandTotalList.add(gt.getFormattingMrrSanctioned() != null ? gt.getFormattingMrrSanctioned() : "0");
                        grandTotalList.add(gt.getMrrSanctionedPercent() != null ? String.valueOf(gt.getMrrSanctionedPercent()) : "0.0");
                        grandTotalList.add(gt.getFormattingMrrCompleted() != null ? gt.getFormattingMrrCompleted() : "0");
                        grandTotalList.add(gt.getMrrCompletedOutOfTotalTehsilsPercent() != null ? String.valueOf(gt.getMrrCompletedOutOfTotalTehsilsPercent()) : "0.0");
                        grandTotalList.add(gt.getFormattingMrrCompleted() != null ? gt.getFormattingMrrCompleted() : "0");
                        grandTotalList.add(gt.getMrrCompletedOutOfSanctionedPercent() != null ? String.valueOf(gt.getMrrCompletedOutOfSanctionedPercent()) : "0.0");
                        grandTotalData.add(grandTotalList);
                    });
        }
        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);
    }


    @GetMapping("/survey")
    public void getSurveyNewpdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {ReportLabels.SURVEY_REPORT};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.SURVEY_REPORT);
        float col_width[] = {20f, 65f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponent.setCol_width(col_width);
        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS,ReportLabels.TOTAL_VILLAGES, ReportLabels.TOTAL_RURAL_REVENUE_AREA_SQ_KM, ReportLabels.AREA_SANCTIONED_FOR_SURVEY_RE_SURVEY_SQ_KM,ReportLabels.DRON_FLYING_SURVEY,ReportLabels.NUMBER_OF_VILLAGES,ReportLabels.AREA_OF_VILLAGES_WHERE_SURVEY_SANCTIONED_NOT_STARTED,
                ReportLabels.NUMBER_OF_VILLAGES,ReportLabels.AREA_OF_VILLAGES_SQ_KM,ReportLabels.MAP_1_IS_GENERATED,ReportLabels.DRAFT_MAP_PUBLISHED,ReportLabels.FINAL_PROMULGATION_DONE,ReportLabels.SURVEY_SANCTIONED_NOT_STARTED,};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {0, 1, 2, 3, 4, 5,6,9};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {10, 11, 12, 13, 14, 15};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 4)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(8, 4)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(4);
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<SurveyResurveyViewReport> surveyList = (List<SurveyResurveyViewReport>) session.getAttribute("surveyResurveyViewReportList");
        List<SurveyResurveyViewReport> grandTotal = (List<SurveyResurveyViewReport>) session.getAttribute("surveyResurveyGrandTotal");
        AtomicInteger indexHolder = new AtomicInteger();

        if (surveyList != null && !surveyList.isEmpty()) {
            surveyList.forEach(map -> {
                List<String> strings = new ArrayList<>();
                if (map.getStateId() != 999) {
                    // Populate normal row data
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getStateName() != null ? map.getStateName() : "N/A");
                    strings.add(map.getFormattingTotalDistrict() != null ? map.getFormattingTotalDistrict() : "0");
                    strings.add(map.getFormattingTotalTehsils() != null ? map.getFormattingTotalTehsils() : "0");
                    strings.add(map.getFormattingTotalVillages() != null ? map.getFormattingTotalVillages() : "0");
                    strings.add(map.getFormatetotalRuralRevenueArea() != null ? map.getFormatetotalRuralRevenueArea() : "0.00");
                    strings.add(map.getFomateareaSanctionedForSurvey() != null ? map.getFomateareaSanctionedForSurvey() : "0.00");
                    strings.add(map.getFormattingvillagesDroneFlyingCompleted() != null ? map.getFormattingvillagesDroneFlyingCompleted() : "0");
                    strings.add(map.getFormateareaDroneFlyingCompleted() != null ? map.getFormateareaDroneFlyingCompleted() :"0.00");
                    strings.add(map.getFormattingvillagesMap1Generated() != null ? map.getFormattingvillagesMap1Generated() : "0");
                    strings.add(map.getFormattingvillagesDraftMapPublished() != null ? map.getFormattingvillagesDraftMapPublished() : "0");
                    strings.add(map.getFormattingvillagesFinalPromulgationDone() != null ? map.getFormattingvillagesFinalPromulgationDone() : "0");
                    strings.add(map.getFormattingvillagesSurveySanctionNotStarted() != null ? map.getFormattingvillagesSurveySanctionNotStarted() : "0");
                    strings.add(map.getFormateareaSurveySanctionNotStarted() != null ? map.getFormateareaSurveySanctionNotStarted()  : "0.00");
                    reportDataList.add(strings);
                }
            });
        }
        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add(gt.getStateName() != null ? gt.getStateName() : "Grand Total");
                grandTotalList.add(gt.getFormattingTotalDistrict() != null ? gt.getFormattingTotalDistrict().toString() : "N/A");
                grandTotalList.add(gt.getFormattingTotalTehsils() != null ? gt.getFormattingTotalTehsils().toString() : "0");
                grandTotalList.add(gt.getFormattingTotalVillages() != null ? gt.getFormattingTotalVillages().toString() : "0");
                grandTotalList.add(gt.getFormatetotalRuralRevenueArea() != null ? gt.getFormatetotalRuralRevenueArea() :"0.00");
                grandTotalList.add(gt.getFomateareaSanctionedForSurvey() != null ? gt.getFomateareaSanctionedForSurvey() : "0.00");
                grandTotalList.add(gt.getFormattingvillagesDroneFlyingCompleted() != null ? gt.getFormattingvillagesDroneFlyingCompleted() : "0");
                grandTotalList.add(gt.getFormateareaDroneFlyingCompleted() != null ? gt.getFormateareaDroneFlyingCompleted() : "0.00");
                grandTotalList.add(gt.getFormattingvillagesMap1Generated() != null ? gt.getFormattingvillagesMap1Generated() : "0");
                grandTotalList.add(gt.getFormattingvillagesDraftMapPublished() != null ? gt.getFormattingvillagesDraftMapPublished() : "0");
                grandTotalList.add(gt.getFormattingvillagesFinalPromulgationDone() != null ? gt.getFormattingvillagesFinalPromulgationDone() : "0");
                grandTotalList.add(gt.getFormattingvillagesSurveySanctionNotStarted() != null ? gt.getFormattingvillagesSurveySanctionNotStarted() : "0");
                grandTotalList.add(gt.getFormateareaSurveySanctionNotStarted() != null ? gt.getFormateareaSurveySanctionNotStarted() :"0.00");
                grandTotalData.add(grandTotalList);
            });
        }
        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);

    }


    @GetMapping("/sro")
    public void getSROpdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {ReportLabels.SRO_REPORT};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.SRO_REPORT);
        float col_width[] = {30f, 75f, 55f, 55f, 55f, 60f, 60f, 75f, 60f, 60f, 60f, 60f, 60f, 60f,60f,60f,60f,60f,60f,60f,60f,60f,60f,60f,60f,60f,60f};
        pComponent.setCol_width(col_width);
        String col_head[] = {
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.STATE_UT,
                ReportLabels.TOTAL_DISTRICTS,
                ReportLabels.NUMBER_OF_SRO_IN_THE_STATE,
                ReportLabels.NUMBER_SRO_USING_ONLINE_REGISTRATION,
                ReportLabels.YEAR_FROM_LEGACY_RECORD_DIGITIZED_SRO,
                ReportLabels.YEAR_LEGACY_RECORD_DIGITIZED_PUBLIC,
                ReportLabels.REGISTRATION_SYSTEM_WITH_OPTION + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.CIRCLE_RATES_PUBLIC_REGISTRATION_SYSTEM + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.CALCULATION_DONE_AUTOMATICALLY_REGISTRATION_SYSTEM + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.REGISTRATION_SYSTEM_INTEGRATED_E_STAMP + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.OPTION_AVAILABLE_REFUND_PAID_FEE + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.OPTION_AVAILABLE_REGISTRATION_SYSTEM + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.FACILITY_ONLINE_VERIFICATION_DOCUMENT + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.SIGNATURE_OBTAINED_FROM_PARTIES + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.DIGITAL_SIGNATURE_ENABLED_REGISTRATION + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.OPTION_OF_EKYC_AVAILABLE_REGISTRATION_SYSTEM + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.OPTION_AVAILABLE_REGISTRATION_SYSTEM_VEDIO + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.HOME_VISIT_MODULE_AVAILABLE_REGISTRATION_SYSTEM + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.EMAIL_AUTOMATICALLY_PARTIES_IMPORTANT + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.REGISTERED_SENT_AUTOMATICALLY_DIGITAL + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.THERE_PROVISION_ONLINE_GRIEVANCE_REDRESSAL + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.OPTION_AVAILABLE_REGISTRATION_SYSTEM_FETCH + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.REGISTRATION_SYSTEM_FACILITY_AUTO_TRIGGER + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.IS_PENDENCY_OF_REVENUE_COURT_CASE + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.AADHAAR_PAN_FINGERPRINT_DETAILS_MASKED + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.FACILITY_TO_STORE_REGISTERED_DOCUMENT + " (" + ReportLabels.YES_NO + " )"
        };
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {0, 1, 2,4,5,6,7,8,9,10,11,12,13,14,15,16,18,19,20,21,22,23,24,25,26,27};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();

        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(0);
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<SroReportDTO> sroList = (List<SroReportDTO>) session.getAttribute("sroStateList");
        List<SroReportDTO> grandTotal = (List<SroReportDTO>) session.getAttribute("sroGrandTotal");
        AtomicInteger indexHolder = new AtomicInteger();

           if (sroList != null && !sroList.isEmpty()) {
               sroList.forEach(map -> {
                   List<String> strings = new ArrayList<>();
                   if (map.getStateId() != 999) {
                       // Populate normal row data
                       strings.add(Integer.toString(indexHolder.incrementAndGet()));
                       strings.add(String.valueOf(map.getStateName() != null ? map.getStateName() : "N/A"));
                       strings.add(String.valueOf(map.getTotalDistricts() != null ? map.getTotalDistricts() : "N/A"));
                       strings.add(String.valueOf(map.getNumberOfSROsInState() != null ? map.getNumberOfSROsInState() : "0"));
                       strings.add(String.valueOf(map.getNumberOfSROsUsingOnlineRegistration() != null ? map.getNumberOfSROsUsingOnlineRegistration() : "0"));
                       strings.add(map.getLegacyRecordsAvailableFromYearSRO() != null ? formatter.format(map.getLegacyRecordsAvailableFromYearSRO()) : "");
                       strings.add(map.getLegacyRecordsAvailableFromYearPublic() != null ? formatter.format(map.getLegacyRecordsAvailableFromYearPublic()) : "");
                       strings.add(String.valueOf(map.getStandardDeedTemplatesAvailable() != null ? map.getStandardDeedTemplatesAvailable() : "NO"));
                       strings.add(String.valueOf(map.getCircleRatesVisibleToPublic() != null ? map.getCircleRatesVisibleToPublic() : "NO"));
                       strings.add(String.valueOf(map.getAutomaticFeeCalculation() != null ? map.getAutomaticFeeCalculation() : "NO"));
                       strings.add(String.valueOf(map.getIntegrationWithEStampSystem() != null ? map.getIntegrationWithEStampSystem() : "NO"));
                       strings.add(String.valueOf(map.getRefundOptionAvailable() != null ? map.getRefundOptionAvailable() : "NO"));
                       strings.add(String.valueOf(map.getUploadSupportingDocumentsOption() != null ? map.getUploadSupportingDocumentsOption() : "NO"));
                       strings.add(String.valueOf(map.getOnlineVerificationOfDocuments() != null ? map.getOnlineVerificationOfDocuments() : "NO"));
                       strings.add(String.valueOf(map.getSignaturesObtainedDigitally() != null ? map.getSignaturesObtainedDigitally() : "NO"));
                       strings.add(String.valueOf(map.getDigitalSignatureEnabledForSRO() != null ? map.getDigitalSignatureEnabledForSRO() : "NO"));
                       strings.add(String.valueOf(map.getEkycOptionAvailable() != null ? map.getEkycOptionAvailable() : "NO"));
                       strings.add(String.valueOf(map.getVideoConferencingOptionAvailable() != null ? map.getVideoConferencingOptionAvailable() : "NO"));
                       strings.add(String.valueOf(map.getHomeVisitModuleAvailable() != null ? map.getHomeVisitModuleAvailable() : "NO"));
                       strings.add(String.valueOf(map.getAutomaticEmailNotifications() != null ? map.getAutomaticEmailNotifications() : "NO"));
                       strings.add(String.valueOf(map.getDigitalFormatRegisteredDocuments() != null ? map.getDigitalFormatRegisteredDocuments() : "NO"));
                       strings.add(String.valueOf(map.getOnlineGrievanceRedressalSystem() != null ? map.getOnlineGrievanceRedressalSystem() : "NO"));
                       strings.add(String.valueOf(map.getFetchPropertyDetailsFromLandRecords() != null ? map.getFetchPropertyDetailsFromLandRecords() : "NO"));
                       strings.add(String.valueOf(map.getAutoTriggerMutationAvailable() != null ? map.getAutoTriggerMutationAvailable() : "NO"));
                       strings.add(String.valueOf(map.getRevenueCourtCaseRedFlagged() != null ? map.getRevenueCourtCaseRedFlagged() : "NO"));
                       strings.add(String.valueOf(map.getMaskingSensitiveDetailsInRegisteredDeeds() != null ? map.getMaskingSensitiveDetailsInRegisteredDeeds() : "NO"));
                       strings.add(String.valueOf(map.getStoreDocumentsInDigilocker() != null ? map.getStoreDocumentsInDigilocker() : "NO"));

                       reportDataList.add(strings);
                   }
               });
           }

        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add(gt.getStateName() != null ? gt.getStateName() : "Grand Total");
                grandTotalList.add(gt.getTotalDistricts() != null ? gt.getTotalDistricts().toString() : "N/A");
                grandTotalList.add(gt.getNumberOfSROsInState() != null ? gt.getNumberOfSROsInState().toString() : "0");
                grandTotalList.add(gt.getNumberOfSROsUsingOnlineRegistration() != null ? gt.getNumberOfSROsUsingOnlineRegistration().toString() : "0");
                grandTotalList.add(gt.getLegacyRecordsAvailableFromYearSRO() != null ? formatter.format(gt.getLegacyRecordsAvailableFromYearSRO()) : "");
                grandTotalList.add(gt.getLegacyRecordsAvailableFromYearPublic() != null ? formatter.format(gt.getLegacyRecordsAvailableFromYearPublic()) : "");
                grandTotalList.add(gt.getStandardDeedTemplatesAvailable() != null ? gt.getStandardDeedTemplatesAvailable() : "NO");
                grandTotalList.add(gt.getCircleRatesVisibleToPublic() != null ? gt.getCircleRatesVisibleToPublic() : "NO");
                grandTotalList.add(gt.getAutomaticFeeCalculation() != null ? gt.getAutomaticFeeCalculation() : "NO");
                grandTotalList.add(gt.getIntegrationWithEStampSystem() != null ? gt.getIntegrationWithEStampSystem() : "NO");
                grandTotalList.add(gt.getRefundOptionAvailable() != null ? gt.getRefundOptionAvailable() : "NO");
                grandTotalList.add(gt.getUploadSupportingDocumentsOption() != null ? gt.getUploadSupportingDocumentsOption() : "NO");
                grandTotalList.add(gt.getOnlineVerificationOfDocuments() != null ? gt.getOnlineVerificationOfDocuments() : "NO");
                grandTotalList.add(gt.getSignaturesObtainedDigitally() != null ? gt.getSignaturesObtainedDigitally() : "NO");
                grandTotalList.add(gt.getDigitalSignatureEnabledForSRO() != null ? gt.getDigitalSignatureEnabledForSRO() : "NO");
                grandTotalList.add(gt.getEkycOptionAvailable() != null ? gt.getEkycOptionAvailable() : "NO");
                grandTotalList.add(gt.getVideoConferencingOptionAvailable() != null ? gt.getVideoConferencingOptionAvailable() : "NO");
                grandTotalList.add(gt.getHomeVisitModuleAvailable() != null ? gt.getHomeVisitModuleAvailable() : "NO");
                grandTotalList.add(gt.getAutomaticEmailNotifications() != null ? gt.getAutomaticEmailNotifications() : "NO");
                grandTotalList.add(gt.getDigitalFormatRegisteredDocuments() != null ? gt.getDigitalFormatRegisteredDocuments() : "NO");
                grandTotalList.add(gt.getOnlineGrievanceRedressalSystem() != null ? gt.getOnlineGrievanceRedressalSystem() : "NO");
                grandTotalList.add(gt.getFetchPropertyDetailsFromLandRecords() != null ? gt.getFetchPropertyDetailsFromLandRecords() : "NO");
                grandTotalList.add(gt.getAutoTriggerMutationAvailable() != null ? gt.getAutoTriggerMutationAvailable() : "NO");
                grandTotalList.add(gt.getRevenueCourtCaseRedFlagged() != null ? gt.getRevenueCourtCaseRedFlagged() : "NO");
                grandTotalList.add(gt.getMaskingSensitiveDetailsInRegisteredDeeds() != null ? gt.getMaskingSensitiveDetailsInRegisteredDeeds() : "NO");
                grandTotalList.add(gt.getStoreDocumentsInDigilocker() != null ? gt.getStoreDocumentsInDigilocker() : "NO");

                grandTotalData.add(grandTotalList);
            });
        }

        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);
    }


    @GetMapping("/rcms")
    public void getRCMSpdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {ReportLabels.RCMS_REPORT};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.RCMS_REPORT);
        float col_width[] = {20f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 70f, 70f, 70f,70f,70f,70f,70f,70f,70f,70f,70f};
        pComponent.setCol_width(col_width);
        String col_head[] = {
                ReportLabels.SERIAL_NUMBER, ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.NUMBER_OF_REVENUE_COURTS,ReportLabels.E_REVENUE_COURT_MANAGEMENT_SYSTEM + " (" + ReportLabels.YES_NO + ")",ReportLabels.ONLINE_PROCESS_OF_REVENUE_COURT_PROCESSES_AVAILABLE,ReportLabels.WHETHER_PROCEEDS + " (" + ReportLabels.YES_NO + ")",ReportLabels.WHETHER_LAW_RECORD_ONLINE_COURT + " (" + ReportLabels.YES_NO + ")", ReportLabels.WHETHER_FILING_DIRECTLY_SYSTEM + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.TOTAL, ReportLabels.COMPUTERIZED_ONLINE,ReportLabels.ATTACHED_DOCUMENTS + " (" + ReportLabels.YES_NO + ")",ReportLabels.CAUSE_LIST_GENERATION + " (" + ReportLabels.YES_NO + ")", ReportLabels.DEFENDANT_NOTIFICATION + " (" + ReportLabels.YES_NO + ")", ReportLabels.COUNTER_AFFIDAVIT_FILING + " (" + ReportLabels.YES_NO + ")", ReportLabels.PROCEEDINGS_TYPING_SYSTEM + " (" + ReportLabels.YES_NO + ")",ReportLabels.UPLOADING_COURT_ORDERS_NOTIFICATION + " (" + ReportLabels.YES_NO + ")", ReportLabels.LAND_RECORD_INTEGRATION + " (" + ReportLabels.YES_NO + ")", ReportLabels.ONLINE_LAND_RECORD_CHECK + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.NO,ReportLabels.NO,ReportLabels.PERCENTAGE,
        };
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {12, 13, 14, 15,16,17,18,19};
        Integer[] rwspn3col = {0,1,2,3,5,7,8,9};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {20,21,22};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 8)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(11, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(6, 8)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(9);
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<RcmsReportDTO> rcmsList = (List<RcmsReportDTO>) session.getAttribute("rcmsReportList");
        List<RcmsReportDTO> grandTotal = (List<RcmsReportDTO>) session.getAttribute("rcmsGrandTotal");
        AtomicInteger indexHolder = new AtomicInteger();

        if (rcmsList != null && !rcmsList.isEmpty()) {
            rcmsList.forEach(map -> {
                List<String> strings = new ArrayList<>();
                if (map.getStateId() != 999) {
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(String.valueOf(map.getStateName() != null ? map.getStateName() : "N/A"));
                    strings.add(String.valueOf(map.getTotalDistrict() != null ? map.getTotalDistrict() : "N/A"));
                    strings.add(String.valueOf(map.getTotalTehsils() != null ? map.getTotalTehsils() : "0"));
                    strings.add(String.valueOf(map.getTotalRevenueCourts() != null ? map.getTotalRevenueCourts() : "0"));
                    strings.add(String.valueOf(map.getRevenueCourtsComputerized() != null ? map.getRevenueCourtsComputerized() : "N/A"));
                    strings.add(String.valueOf(map.getRevenueCourtsComputerizedPercent() != null ? map.getRevenueCourtsComputerizedPercent() : "N/A"));
                    strings.add(String.valueOf(map.getERcmsAvailable() != null ? map.getERcmsAvailable() : "NO"));
                    strings.add(String.valueOf(map.getAffidavitFilingOnline() != null ? map.getAffidavitFilingOnline() : "NO"));
                    strings.add(String.valueOf(map.getCauseListGenerationOnline() != null ? map.getCauseListGenerationOnline() : "NO"));
                    strings.add(String.valueOf(map.getNoticeToDefendantsOnline() != null ? map.getNoticeToDefendantsOnline() : "NO"));
                    strings.add(String.valueOf(map.getCounterAffidavitFilingOnline() != null ? map.getCounterAffidavitFilingOnline() : "NO"));
                    strings.add(String.valueOf(map.getProceedingsTypingDirectlyOnline() != null ? map.getProceedingsTypingDirectlyOnline() : "NO"));
                    strings.add(String.valueOf(map.getUploadingRevenueCourtOrdersOnline() != null ? map.getUploadingRevenueCourtOrdersOnline() : "NO"));

                    strings.add(String.valueOf(map.getLandRecordsOnlineFromRevenueCourtSystem() != null ? map.getLandRecordsOnlineFromRevenueCourtSystem() : "NO"));
                    strings.add(String.valueOf(map.getRevenueCourtProceedingsPaperless() != null ? map.getRevenueCourtProceedingsPaperless() : "NO"));
                    strings.add(String.valueOf(map.getLandRecordsOnlineForCivilCourts() != null ? map.getLandRecordsOnlineForCivilCourts() : "NO"));
                    strings.add(String.valueOf(map.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() != null ? map.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() : "NO"));
                    strings.add(String.valueOf(map.getCaseFilingRedFlaggedInLandRecords() != null ? map.getCaseFilingRedFlaggedInLandRecords() : "NO"));
                    reportDataList.add(strings);
                }
            });
        }


        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add(gt.getStateName() != null ? gt.getStateName() : "Grand Total");
                grandTotalList.add(gt.getTotalDistrict() != null ? gt.getTotalDistrict().toString() : "N/A");
                grandTotalList.add(gt.getTotalTehsils() != null ? gt.getTotalTehsils().toString() : "0");
                grandTotalList.add(gt.getTotalRevenueCourts() != null ? gt.getTotalRevenueCourts().toString() : "0");
                grandTotalList.add(gt.getRevenueCourtsComputerized() != null ? String.valueOf(gt.getRevenueCourtsComputerized()) : "N/A");
                grandTotalList.add(gt.getRevenueCourtsComputerizedPercent() != null ? String.valueOf(gt.getRevenueCourtsComputerizedPercent()) : "N/A");
                grandTotalList.add(gt.getERcmsAvailable() != null ? gt.getERcmsAvailable() : "NO");
                grandTotalList.add(gt.getAffidavitFilingOnline() != null ? gt.getAffidavitFilingOnline() : "NO");
                grandTotalList.add(gt.getCauseListGenerationOnline() != null ? gt.getCauseListGenerationOnline() : "NO");
                grandTotalList.add(gt.getNoticeToDefendantsOnline() != null ? gt.getNoticeToDefendantsOnline() : "NO");
                grandTotalList.add(gt.getCounterAffidavitFilingOnline() != null ? gt.getCounterAffidavitFilingOnline() : "NO");
                grandTotalList.add(gt.getProceedingsTypingDirectlyOnline() != null ? gt.getProceedingsTypingDirectlyOnline() : "NO");
                grandTotalList.add(gt.getUploadingRevenueCourtOrdersOnline() != null ? gt.getUploadingRevenueCourtOrdersOnline() : "NO");
                
                grandTotalList.add(gt.getLandRecordsOnlineFromRevenueCourtSystem() != null ? gt.getLandRecordsOnlineFromRevenueCourtSystem() : "NO");
                grandTotalList.add(gt.getRevenueCourtProceedingsPaperless() != null ? gt.getRevenueCourtProceedingsPaperless() : "NO");
                grandTotalList.add(gt.getLandRecordsOnlineForCivilCourts() != null ? gt.getLandRecordsOnlineForCivilCourts() : "NO");
                grandTotalList.add(gt.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() != null ? gt.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() : "NO");
                grandTotalList.add(gt.getCaseFilingRedFlaggedInLandRecords() != null ? gt.getCaseFilingRedFlaggedInLandRecords() : "NO");
                grandTotalData.add(grandTotalList);
            });
        }

        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);
    }


    @GetMapping("/aadhar-linkage")
    public void getAadharpdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {ReportLabels.AADHAR_REPORT};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.AADHAR_REPORT);
        //v5: +2 RoR Address +7 Land owners
        float col_width[] = {20f, 65f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponent.setCol_width(col_width);
        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.RoR, ReportLabels.NUMBER_OF_LAND_OWNERS,
                ReportLabels.TOTAL,ReportLabels.WHERE_AT_LEAST_ONE_RoR_LINKED_WITH_AADHAAR,ReportLabels.WHERE_100_PERCENT_RoR_LINKED_WITH_AADHAAR,ReportLabels.TOTAL,ReportLabels.LINKED_WITH_AADHAAR,ReportLabels.LINKED_WITH_MOBILE_NUMBER,ReportLabels.LINKED_WITH_ADDRESS,
                ReportLabels.TOTAL,ReportLabels.LINKED_WITH_AADHAAR,ReportLabels.LINKED_WITH_MOBILE_NUMBER,ReportLabels.LINKED_WITH_ADDRESS,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO,ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {7,8,9,10,11,12,13,14,15,16,17};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {0, 1, 2, 3};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 5)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 7))); //v5 RoR
        colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 7))); //v5 Land owners
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(9, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(11, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(12, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(13, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(14, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(15, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(16, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(17, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(9, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(10, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(12, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(13, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(14, 2)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(8); //v5
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<LinkedAadharViewReport> aadharList = (List<LinkedAadharViewReport>) session.getAttribute("linkedAadharViewReportList");
        List<LinkedAadharViewReport> grandTotal = (List<LinkedAadharViewReport>) session.getAttribute("linkedAadharGrandTotal");
        AtomicInteger indexHolder = new AtomicInteger();

        if (aadharList != null && !aadharList.isEmpty()) {
            aadharList.forEach(map -> {
                List<String> strings = new ArrayList<>();
                if (map.getStateId() != 999) {
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getStateName() != null ? map.getStateName() : "N/A");
                    strings.add(map.getFormattingTotalDistrict() != null ? map.getFormattingTotalDistrict() : "0");
                    strings.add(map.getFormattingTotalTehsils() != null ? map.getFormattingTotalTehsils() : "0");
                    strings.add(map.getFormattingTotalVillages() != null ? map.getFormattingTotalVillages() : "0");
                    strings.add(map.getFormattingvillagesWithRorLinkedAadhaar() != null ? map.getFormattingvillagesWithRorLinkedAadhaar() : "0");
                    strings.add(String.valueOf(map.getVillagesWithRorLinkedAadhaarPercent() != null ? map.getVillagesWithRorLinkedAadhaarPercent() : "0.0"));
                    strings.add(map.getFormattingvillagesWith100PercentRorLinkedAadhaar() != null ? map.getFormattingvillagesWith100PercentRorLinkedAadhaar() : "0");
                    strings.add(map.getVillagesWith100PercentRorLinkedAadhaarPercent() != null && map.getVillagesWith100PercentRorLinkedAadhaarPercent().compareTo(BigDecimal.ZERO) != 0 ? map.getVillagesWith100PercentRorLinkedAadhaarPercent().toString() : "0.0");
                    strings.add(map.getFormattingtotalRor() != null ? map.getFormattingtotalRor() : "0");
                    strings.add(map.getFormattingrorLinkedWithAadhaar() != null ? map.getFormattingrorLinkedWithAadhaar() : "0");
                    strings.add(map.getRorLinkedWithAadhaarPercent() != null && map.getRorLinkedWithAadhaarPercent().compareTo(BigDecimal.ZERO) != 0 ? map.getRorLinkedWithAadhaarPercent().toString() : "0.0");
                    strings.add(map.getFormattingrorLinkedWithMobileNumber() != null ? map.getFormattingrorLinkedWithMobileNumber() : "0");
                    strings.add(map.getRorLinkedWithMobileNumberPercent() != null && map.getRorLinkedWithMobileNumberPercent().compareTo(BigDecimal.ZERO) != 0 ? map.getRorLinkedWithMobileNumberPercent().toString() : "0.0");
                    AadhaarReportExportV5Util.appendAddressRowFormatted(strings, map); //v5
                    AadhaarReportExportV5Util.appendLandOwnerRowFormatted(strings, map); //v5
                    reportDataList.add(strings);
                }
            });
        }
        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add(gt.getStateName() != null ? gt.getStateName() : "Grand Total");
                grandTotalList.add(gt.getFormattingTotalDistrict() != null ? gt.getFormattingTotalDistrict() : "N/A");
                grandTotalList.add(gt.getFormattingTotalTehsils() != null ? gt.getFormattingTotalTehsils() : "0");
                grandTotalList.add(gt.getFormattingTotalVillages() != null ? gt.getFormattingTotalVillages() : "0");
                grandTotalList.add(gt.getFormattingvillagesWithRorLinkedAadhaar() != null ? gt.getFormattingvillagesWithRorLinkedAadhaar() : "0");
                grandTotalList.add(gt.getVillagesWithRorLinkedAadhaarPercent() != null ? String.valueOf(gt.getVillagesWithRorLinkedAadhaarPercent()) : "0.0");
                grandTotalList.add(gt.getFormattingvillagesWith100PercentRorLinkedAadhaar() != null ? gt.getFormattingvillagesWith100PercentRorLinkedAadhaar() : "0");
                grandTotalList.add(gt.getVillagesWith100PercentRorLinkedAadhaarPercent() != null && gt.getVillagesWith100PercentRorLinkedAadhaarPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getVillagesWith100PercentRorLinkedAadhaarPercent()) : "0.0");
                grandTotalList.add(gt.getFormattingtotalRor() != null ? gt.getFormattingtotalRor() : "0");
                grandTotalList.add(gt.getFormattingrorLinkedWithAadhaar() != null ? gt.getFormattingrorLinkedWithAadhaar() : "0");
                grandTotalList.add(gt.getRorLinkedWithAadhaarPercent() != null && gt.getRorLinkedWithAadhaarPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getRorLinkedWithAadhaarPercent()) : "0.0");
                grandTotalList.add(gt.getFormattingrorLinkedWithMobileNumber() != null ? gt.getFormattingrorLinkedWithMobileNumber() : "0");
                grandTotalList.add(gt.getRorLinkedWithMobileNumberPercent() != null && gt.getRorLinkedWithMobileNumberPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getRorLinkedWithMobileNumberPercent()) : "0.0");
                AadhaarReportExportV5Util.appendAddressRowFormatted(grandTotalList, gt); //v5
                AadhaarReportExportV5Util.appendLandOwnerRowFormatted(grandTotalList, gt); //v5
                grandTotalData.add(grandTotalList);
            });
        }

        pComponent.setGrandTotal(grandTotalData);
        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);
    }
    @GetMapping("/all-kpi")
    public void getAllKPIpdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {ReportLabels.STATE_INDICATOR};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.STATE_INDICATOR);
        float col_width[] = {20f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f,50f,50f,50f};
        pComponent.setCol_width(col_width);
        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.VILLAGES_WHERE_CLR_COMPLETED,ReportLabels.NO_OF_CADASTRAL_MAPS_FMBS_TIPPANS,ReportLabels.VILLAGE_WHERE_CADASTRAL_MAPS_LINKED_TO_ROR,ReportLabels.NO_OF_SROS
                ,ReportLabels.TOTAL,ReportLabels.COMPUTERIZED_SROS,ReportLabels.SROS_INTEGRATED_WITH_LAND_RECORDS
                ,ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.TOTAL, ReportLabels.DIGITIZED,ReportLabels.PERCENTAGE,ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO, ReportLabels.PERCENTAGE
        };
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {5,6,7,9};
        Integer[] rwspn3col = {0, 1, 2, 3,4};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {12,13,14,15,16,17,18,19,20,21,22};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 5)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(11, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(6, 3)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(8, 5)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(8);
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<DashBordDTO> kpiList = (List<DashBordDTO>) session.getAttribute("reportData");
        List<String> strings = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        kpiList.stream().forEachOrdered(map -> {
            strings.add(Integer.toString(indexHolder.incrementAndGet()));
            strings.add(map.getName());
            strings.add(String.valueOf(map.getTotalDistrict()));
            strings.add(String.valueOf(map.getTotalTehsil()));
            strings.add(String.valueOf(map.getTotalVillage()));
            strings.add(String.valueOf(map.getVillagesComputerizationCompleted()));
            strings.add(String.valueOf(map.getVillagesComputerizationCompletedPercent()));
            strings.add(String.valueOf(map.getTotalMapsFmbTippans()));
            strings.add(String.valueOf(map.getTotalDigitizedMapsFmbTippans()));
            strings.add(String.valueOf(map.getDigitizedMapsFmbTippansPercent()));
            strings.add(String.valueOf(map.getVillagesLinkedWithRor()));
            strings.add(String.valueOf(map.getVillagesLinkedWithRoRPercent()));
            strings.add(String.valueOf(map.getTotalSro()));
            strings.add(String.valueOf(map.getSroComputerized()));
            strings.add(String.valueOf(map.getSroComputerizedPercent()));
            strings.add(String.valueOf(map.getSroIntegration()));
            strings.add(String.valueOf(map.getSroIntegrationPercent()));
        });
        reportDataList.add(strings);
        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);

    }


    @GetMapping("/all-kpi-district")
    public void getAllKPIDistrictpdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {ReportLabels.DISTRICT_INDICATOR};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.DISTRICT_INDICATOR);
        float col_width[] = {20f, 65f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponent.setCol_width(col_width);
        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.DISTRICT_NAME, ReportLabels.TOTAL_TEHSILS, ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.NO_OF_CADASTRAL_MAPS_FMBS_TIPPANS,ReportLabels.DIGITIZED_CADASTRAL_MAPS_FMBS_TIPPANS,ReportLabels.SROS,
                ReportLabels.TOTAL,ReportLabels.CLR_COMPLETED, ReportLabels.COMPUTERIZED,ReportLabels.SROS_LINKED_WITH_LAND_RECORDS_ROR,
                ReportLabels.NO,ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO, ReportLabels.PERCENTAGE
        };
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {7,8,9,10};
        Integer[] rwspn3col = {4,5};
        Integer[] rwspn4col = {0, 1, 2};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {11,12,13,16,17,18,19,20,21};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(3, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(9, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(5);
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<DashBordDTO> kpiList = (List<DashBordDTO>) session.getAttribute("reportDataDistrict");
        List<String> strings = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        kpiList.stream().forEachOrdered(map -> {
            strings.add(Integer.toString(indexHolder.incrementAndGet()));
            strings.add(map.getDistrict_name().toUpperCase());
            strings.add(String.valueOf(map.getTotalTehsil()));
            strings.add(String.valueOf(map.getTotalVillage()));
            strings.add(String.valueOf(map.getVillagesComputerizationCompleted()));
            strings.add(String.valueOf(map.getVillagesComputerizationCompletedPercent()));
            strings.add(String.valueOf(map.getVillagesLinkedWithRor()));
            strings.add(String.valueOf(map.getVillagesLinkedWithRoRPercent()));
            strings.add(String.valueOf(map.getTotalDigitizedMapsFmbTippans()));
            strings.add(String.valueOf(map.getDigitizedMapsFmbTippansPercent()));
            strings.add(String.valueOf(map.getSroComputerized()));
            strings.add(String.valueOf(map.getSroComputerizedPercent()));
            strings.add(String.valueOf(map.getSroIntegration()));
            strings.add(String.valueOf(map.getSroIntegrationPercent()));
        });
        reportDataList.add(strings);
        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);

    }

    //v5 Legacy Registered Documents Digitization — state PDF
    @GetMapping("/legacy-digitization")
    public void getLegacyDigitizationPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {ReportLabels.LEGACY_DIGITIZATION_REPORT};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.LEGACY_DIGITIZATION_REPORT);
        float col_width[] = {20f, 65f, 40f, 40f, 50f, 50f, 40f, 50f, 40f, 50f, 40f, 50f, 40f};
        pComponent.setCol_width(col_width);
        String col_head[] = {
                ReportLabels.SERIAL_NUMBER, ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS,
                ReportLabels.TOTAL_LEGACY_REGISTERED_DOCUMENTS, ReportLabels.LEGACY_DIGITIZED_FROM_STATE_FUNDS,
                ReportLabels.LEGACY_SANCTIONED_UNDER_DILRMP, ReportLabels.LEGACY_COMPLETED_FROM_DILRMP_FUNDS,
                ReportLabels.TOTAL_LEGACY_DIGITIZED, ReportLabels.LEGACY_DIGITIZED_UPTO_YEAR,
                ReportLabels.NO_OF_PAGES, ReportLabels.PERCENTAGE, ReportLabels.NO_OF_PAGES, ReportLabels.PERCENTAGE,
                ReportLabels.NO_OF_PAGES, ReportLabels.PERCENTAGE
        };
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {0, 1, 2, 3, 4, 6, 9};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {10, 11, 12, 13, 14, 15};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(3);
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<LegacyDigitizationReport> legacyList =
                (List<LegacyDigitizationReport>) session.getAttribute("legacyList");
        List<LegacyDigitizationReport> grandTotal =
                (List<LegacyDigitizationReport>) session.getAttribute("legacyGrandTotal");
        AtomicInteger indexHolder = new AtomicInteger();
        if (legacyList != null && !legacyList.isEmpty()) {
            legacyList.forEach(map -> {
                if (map.getStateId() == null || map.getStateId() != 999) {
                    List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getStateName() != null ? map.getStateName() : "N/A");
                    strings.add(map.getFormattingTotalDistrict() != null ? map.getFormattingTotalDistrict() : "0");
                    strings.add(map.getFormattingTotalTehsils() != null ? map.getFormattingTotalTehsils() : "0");
                    LegacyDigitizationExportUtil.appendStateFormatted(strings, map);
                    reportDataList.add(strings);
                }
            });
        }
        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add(gt.getStateName() != null ? gt.getStateName() : "Grand Total");
                grandTotalList.add(gt.getFormattingTotalDistrict() != null ? gt.getFormattingTotalDistrict() : "0");
                grandTotalList.add(gt.getFormattingTotalTehsils() != null ? gt.getFormattingTotalTehsils() : "0");
                LegacyDigitizationExportUtil.appendStateFormatted(grandTotalList, gt);
                grandTotalData.add(grandTotalList);
            });
        }
        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);
    }

    //v5 Legacy Revenue Records Digitisation — state PDF
    @GetMapping("/legacy-revenue")
    public void getLegacyRevenuePdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {ReportLabels.LEGACY_REVENUE_REPORT};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.LEGACY_REVENUE_REPORT);
        float col_width[] = {20f, 65f, 40f, 40f, 50f, 50f, 40f, 50f, 40f, 50f, 40f, 50f, 40f};
        pComponent.setCol_width(col_width);
        String col_head[] = {
                ReportLabels.SERIAL_NUMBER, ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS,
                ReportLabels.TOTAL_LEGACY_REVENUE_RECORDS, ReportLabels.LEGACY_REVENUE_DIGITISED_STATE_FUNDS,
                ReportLabels.LEGACY_REVENUE_SANCTIONED_DILRMP, ReportLabels.LEGACY_REVENUE_COMPLETED_DILRMP,
                ReportLabels.TOTAL_LEGACY_REVENUE_DIGITISED, ReportLabels.LEGACY_REVENUE_UPTO_YEAR,
                ReportLabels.NO_OF_PAGES, ReportLabels.NO_OF_PAGES, ReportLabels.PERCENTAGE,
                ReportLabels.NO_OF_PAGES, ReportLabels.NO_OF_PAGES, ReportLabels.PERCENTAGE,
                ReportLabels.NO_OF_PAGES, ReportLabels.PERCENTAGE, ReportLabels.YEAR
        };
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {0, 1, 2, 3};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {4, 6, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(3);
        List<LegacyRevenueReport> legacyList = legacyRevenueReportService.getAllFormattedList();
        List<LegacyRevenueReport> grandTotal = legacyRevenueReportService.getGrandTotalFormatted();
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        AtomicInteger indexHolder = new AtomicInteger();
        if (legacyList != null && !legacyList.isEmpty()) {
            legacyList.forEach(map -> {
                if (map.getStateId() == null || map.getStateId() != 999) {
                    List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getStateName() != null ? map.getStateName() : "N/A");
                    strings.add(map.getFormattingTotalDistrict() != null ? map.getFormattingTotalDistrict() : "0");
                    strings.add(map.getFormattingTotalTehsils() != null ? map.getFormattingTotalTehsils() : "0");
                    LegacyRevenueExportUtil.appendStateFormatted(strings, map);
                    reportDataList.add(strings);
                }
            });
        }
        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add(gt.getStateName() != null ? gt.getStateName() : "Grand Total");
                grandTotalList.add(gt.getFormattingTotalDistrict() != null ? gt.getFormattingTotalDistrict() : "0");
                grandTotalList.add(gt.getFormattingTotalTehsils() != null ? gt.getFormattingTotalTehsils() : "0");
                LegacyRevenueExportUtil.appendStateFormatted(grandTotalList, gt);
                grandTotalData.add(grandTotalList);
            });
        }
        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);
    }

    //v5 Modernization of Registration Office (SRO) — state PDF
    @GetMapping("/sro-modernization")
    public void getSroModernizationPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {ReportLabels.SRO_MODERNIZATION_REPORT};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.SRO_MODERNIZATION_REPORT);
        float col_width[] = {18f, 58f, 36f, 36f, 52f, 42f, 36f, 48f, 42f, 36f, 42f, 42f};
        pComponent.setCol_width(col_width);
        // Row1 titles (indices 0-8) + Row2 sub-heads (9-14): Nos. / %
        String col_head[] = {
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.STATE_UT,
                ReportLabels.TOTAL_DISTRICTS,
                ReportLabels.TOTAL_SROS,
                ReportLabels.SROS_USING_ONLINE_REGISTRATION_A,
                ReportLabels.SROS_MODERNISED_STATE_FUNDS,
                ReportLabels.SROS_SANCTIONED_DILRMP,
                ReportLabels.SROS_MODERNISED_DILRMP_FUNDS,
                ReportLabels.SROS_MODERNISED_TOTAL,
                ReportLabels.NOS,
                ReportLabels.PERCENTAGE,
                ReportLabels.NOS,
                ReportLabels.PERCENTAGE,
                ReportLabels.NOS,
                ReportLabels.PERCENTAGE
        };
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {0, 1, 2, 3, 4, 6};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {9, 10, 11, 12, 13, 14};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(3);
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<SroModernizationReport> list =
                (List<SroModernizationReport>) session.getAttribute("sroModernizationList");
        List<SroModernizationReport> grandTotal =
                (List<SroModernizationReport>) session.getAttribute("sroModernizationGrandTotal");
        AtomicInteger indexHolder = new AtomicInteger();
        if (list != null && !list.isEmpty()) {
            list.forEach(map -> {
                if (map.getStateId() == null || map.getStateId() != 999) {
                    List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getStateName() != null ? map.getStateName() : "N/A");
                    strings.add(map.getFormattingTotalDistricts() != null ? map.getFormattingTotalDistricts() : "0");
                    strings.add(map.getFormattingTotalSros() != null ? map.getFormattingTotalSros() : "0");
                    SroModernizationExportUtil.appendStateFormatted(strings, map);
                    reportDataList.add(strings);
                }
            });
        }
        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add(gt.getStateName() != null ? gt.getStateName() : "Grand Total");
                grandTotalList.add(gt.getFormattingTotalDistricts() != null ? gt.getFormattingTotalDistricts() : "0");
                grandTotalList.add(gt.getFormattingTotalSros() != null ? gt.getFormattingTotalSros() : "0");
                SroModernizationExportUtil.appendStateFormatted(grandTotalList, gt);
                grandTotalData.add(grandTotalList);
            });
        }
        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);
    }

}
