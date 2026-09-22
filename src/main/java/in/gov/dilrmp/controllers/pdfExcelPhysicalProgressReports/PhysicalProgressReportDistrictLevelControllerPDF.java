package in.gov.dilrmp.controllers.pdfExcelPhysicalProgressReports;


import in.gov.dilrmp.component.PdfExporterComponent;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.DistrictMapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.DistrictClrReportView;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.DistrictLinkedAadhaarViewReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.DistrictRcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.DistrictSurveyResurveyViewReport;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.utils.AadhaarReportExportV5Util;
import in.gov.dilrmp.utils.MapDigitizationReportExportV5Util;
import in.gov.dilrmp.utils.PdfExporter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("physcial/report/district/pdf")
public class PhysicalProgressReportDistrictLevelControllerPDF {

    DecimalFormat df = new DecimalFormat("0.00");

    @GetMapping("/district_clr")
    public void getDistrictClrPDF(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        String woner = (String) session.getAttribute("Wonership");
        String head[] = {ReportLabels.CLR_REPORT,ReportLabels.STATE_UT +" - "+stateName,ReportLabels.GENDER_OWNERSHIP_HEADING + " " + woner};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.CLR_REPORT);
        float col_width[] = {45f, 90f, 50f, 50f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponent.setCol_width(col_width);
        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.DISTRICT_NAME, ReportLabels.TOTAL_TEHSILS,
                ReportLabels.TOTAL_VILLAGES, ReportLabels.RoR, ReportLabels.NUMBER_OF_VILLAGES_WHERE_CLR_COMPLETED,
                ReportLabels.TOTAL_NO_OF_LAND_OWNERS, ReportLabels.AVAILABILITY_OF_GENDER_BASED_LAND_OWNERSHIP,
                ReportLabels.WHETHER_ROR_AVAILABLE_ONLINE,ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_AVAILABLE_ONLINE,
                ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_LEGALLY_VALID_IN_STATE,ReportLabels.WHETHER_MUTATION_APPLICATION_SUBMITTED_ONLINE,
                ReportLabels.WHETHER_AUTO_TRIGGERED_MUTATION_FACILITY_AVAILABLE,ReportLabels.WHETHER_AUTO_MUTATION_FACILITY_AVAILABLE,
                ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_SRO,ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_REVENUE_COURTS,
                ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_CIVIL_COURTS_THROUGH_E_COURTS_SYSTEM,
                //v5 RoR leaf includes with Cadastral Map
                ReportLabels.TOTAL,ReportLabels.COMPUTERIZED,ReportLabels.PERCENTAGE,ReportLabels.ROR_WITH_CADASTRAL_MAP,ReportLabels.PERCENTAGE,ReportLabels.NO,ReportLabels.PERCENTAGE,
                ReportLabels.NUMBER_OF_DISTRICTS_WHERE_AVAILABLE,ReportLabels.MALE,ReportLabels.FEMALE,ReportLabels.TOTAL,ReportLabels.YES_NO,
                ReportLabels.YES_NO,ReportLabels.YES_NO,ReportLabels.YES_NO,ReportLabels.YES_NO,ReportLabels.YES_NO,ReportLabels.YES_NO,
                ReportLabels.YES_NO,ReportLabels.YES_NO
        };
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {0, 1, 2, 3,6};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 5))); //v5 RoR 3->5
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(9, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(11, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(12, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(13, 1))); //v5 Auto-Mutation
        colspanval.add(new ArrayList<Integer>(Arrays.asList(14, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(15, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(16, 1)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 5))); //v5
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(7, 4)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(8); //v5: 25-17=8 (was 6; +2 RoR leaf)
        List<DistrictClrReportView> districtClrList = (List<DistrictClrReportView>) session.getAttribute("districtClrData");
        List<StateClrReportView> grandTotal = (List<StateClrReportView>) session.getAttribute("stateClrData");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();

        if (districtClrList != null && !districtClrList.isEmpty()) {
            districtClrList.forEach(map -> {
                List<String> strings = new ArrayList<>();
                strings.add(Integer.toString(indexHolder.incrementAndGet()));
                strings.add(map.getDistrictName() != null ? map.getDistrictName().toUpperCase() : "N/A");
                strings.add(map.getTotalTehsils() != null ? map.getTotalTehsils().toString() : "0");
                strings.add(map.getTotalVillages() != null ? map.getTotalVillages().toString() : "0");
                strings.add(map.getTotalRor() != null ? map.getTotalRor().toString() : "0");
                strings.add(map.getRorComputerized() != null ? map.getRorComputerized().toString() : "0");
                strings.add(map.getRorComputerizedPercent() != null && map.getRorComputerizedPercent().compareTo(BigDecimal.ZERO) != 0 ? map.getRorComputerizedPercent().toString() : "0.0");
                //v5
                strings.add(map.getRorWithCadastralMap() != null ? map.getRorWithCadastralMap().toString() : "0");
                strings.add(map.getRorWithCadastralMapPercent() != null && map.getRorWithCadastralMapPercent().compareTo(BigDecimal.ZERO) != 0 ? map.getRorWithCadastralMapPercent().toString() : "0.0");
                strings.add(map.getVillagesClrCompleted() != null ? map.getVillagesClrCompleted().toString() : "0");
                strings.add(map.getClrCompletionPercent() != null && map.getClrCompletionPercent().compareTo(BigDecimal.ZERO) != 0 ? map.getClrCompletionPercent().toString() : "0.0");
                strings.add(map.getTotalLandOwners() != null ? map.getTotalLandOwners().toString() : "0");
                strings.add(map.getDistrictsWithGenderBasedOwnership() != null ? map.getDistrictsWithGenderBasedOwnership().toString() : "0");
                strings.add(map.getTotalMaleLandOwners() != null ? map.getTotalMaleLandOwners().toString() : "0");
                strings.add(map.getTotalFemaleLandOwners() != null ? map.getTotalFemaleLandOwners().toString() : "0");
                strings.add(map.getTotalOwners() != null ? map.getTotalOwners().toString() : "0");

                // Convert boolean values to "YES" or "NO"
                strings.add(map.getRorAvailableOnline() != null ? map.getRorAvailableOnline() : "NO");
                strings.add(map.getDigitallySignedRorAvailable() != null ? map.getDigitallySignedRorAvailable() : "NO");
                strings.add(map.getDigitallySignedRorLegallyValid() != null ? map.getDigitallySignedRorLegallyValid() : "NO");
                strings.add(map.getOnlineMutationFacility() != null ? map.getOnlineMutationFacility() : "NO");
                strings.add(map.getAutoTriggerMutation() != null ? map.getAutoTriggerMutation() : "NO");
                //v5
                strings.add(map.getAutoMutationFacility() != null ? map.getAutoMutationFacility() : "NO");
                strings.add(map.getLandRecordsOnlineFromRegistrationSystem() != null ? map.getLandRecordsOnlineFromRegistrationSystem() : "NO");
                strings.add(map.getRevenueCourtProceedingsPaperless() != null ? map.getRevenueCourtProceedingsPaperless() : "NO");
                strings.add(map.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() != null ? map.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() : "NO");

                reportDataList.add(strings);
            });
        }

        List<List<String>> grandTotalData = new ArrayList<>();

        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add("Grand Total");
                grandTotalList.add(gt.getTotalTehsils() != null ? gt.getTotalTehsils().toString() : "0");
                grandTotalList.add(gt.getTotalVillages() != null ? gt.getTotalVillages().toString() : "0");
                grandTotalList.add(gt.getTotalRor() != null ? String.valueOf(gt.getTotalRor()) : "0");
                grandTotalList.add(gt.getRorComputerized() != null ? String.valueOf(gt.getRorComputerized()) : "0");
                grandTotalList.add(gt.getRorComputerizedPercent() != null && gt.getRorComputerizedPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getRorComputerizedPercent()) : "0.0");
                //v5
                grandTotalList.add(gt.getRorWithCadastralMap() != null ? String.valueOf(gt.getRorWithCadastralMap()) : "0");
                grandTotalList.add(gt.getRorWithCadastralMapPercent() != null && gt.getRorWithCadastralMapPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getRorWithCadastralMapPercent()) : "0.0");
                grandTotalList.add(gt.getVillagesClrCompleted() != null ? String.valueOf(gt.getVillagesClrCompleted()) : "0");
                grandTotalList.add(gt.getClrCompletionPercent() != null && gt.getClrCompletionPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getClrCompletionPercent()) : "0.0");
                grandTotalList.add(gt.getTotalLandOwners() != null ? String.valueOf(gt.getTotalLandOwners()) : "0");
                grandTotalList.add(gt.getDistrictsWithGenderBasedOwnership() != null ? String.valueOf(gt.getDistrictsWithGenderBasedOwnership()) : "0");
                grandTotalList.add(gt.getTotalMaleLandOwners() != null ? String.valueOf(gt.getTotalMaleLandOwners()) : "0");
                grandTotalList.add(gt.getTotalFemaleLandOwners() != null ? String.valueOf(gt.getTotalFemaleLandOwners()) : "0");
                grandTotalList.add(gt.getTotalOwners() != null ? String.valueOf(gt.getTotalOwners()) : "0");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalData.add(grandTotalList);
            });
        }

        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);

    }

//    @GetMapping("/district_clr")
//    public void getDistrictClrPDF(HttpServletResponse response, HttpSession session) {
//        PdfExporterComponent pComponent = new PdfExporterComponent();
//        String stateName = (String) session.getAttribute("stateName");
//        String head[] = {ReportLabels.CLR_REPORT,ReportLabels.STATE_UT +" - "+stateName};
//        pComponent.setReportHeading(head);
//        pComponent.setReportName(ReportLabels.CLR_REPORT);
//        float col_width[] = {45f, 90f, 50f, 50f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
//        pComponent.setCol_width(col_width);
//        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.DISTRICT_NAME, ReportLabels.TOTAL_TEHSILS,
//                ReportLabels.TOTAL_VILLAGES, ReportLabels.RoR, ReportLabels.NUMBER_OF_VILLAGES_WHERE_CLR_COMPLETED,
//                ReportLabels.TOTAL_NO_OF_LAND_OWNERS, ReportLabels.AVAILABILITY_OF_GENDER_BASED_LAND_OWNERSHIP,
//                ReportLabels.TOTAL,ReportLabels.COMPUTERIZED,ReportLabels.PERCENTAGE,ReportLabels.NO,ReportLabels.PERCENTAGE,
//                ReportLabels.NUMBER_OF_DISTRICTS_WHERE_AVAILABLE,ReportLabels.MALE,ReportLabels.FEMALE,ReportLabels.TOTAL
//        };
//        pComponent.setCol_head(col_head);
//        Integer[] rwspn2col = {0, 1, 2, 3,6};
//        Integer[] rwspn3col = {};
//        Integer[] rwspn4col = {};
//        Integer[] rwspn5col = {};
//        Integer[] simplecell = {8,9,10,11,12,13,14,15,16};
//        pComponent.setRowspn5(rwspn5col);
//        pComponent.setRowspn4(rwspn4col);
//        pComponent.setRowspn3(rwspn3col);
//        pComponent.setRowspn2(rwspn2col);
//        pComponent.setRowspan1(simplecell);
//        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
//        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
//        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
//        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
//        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 4)));
//        pComponent.setColumnspan(colspanval);
//        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
//        colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
//        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
//        colsBreak.add(new ArrayList<Integer>(Arrays.asList(7, 4)));
//        pComponent.setColumnnumber(colsBreak);
//        pComponent.setColumnBreakCountNo(6);
//        List<DistrictClrReportView> districtClrList = (List<DistrictClrReportView>) session.getAttribute("districtClrData");
//        List<StateClrReportView> grandTotal = (List<StateClrReportView>) session.getAttribute("stateClrData");
//        List<List<String>> reportDataList = new ArrayList<>();
//        AtomicInteger indexHolder = new AtomicInteger();
//
//        if (districtClrList != null && !districtClrList.isEmpty()) {
//            districtClrList.forEach(map -> {
//                List<String> strings = new ArrayList<>();
//                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
//                    strings.add(map.getStateName() != null ? map.getDistrictName() : "N/A");
//                    strings.add(map.getTotalTehsils() != null ? map.getTotalTehsils().toString() : "0");
//                    strings.add(map.getTotalVillages() != null ? map.getTotalVillages().toString() : "0");
//                    strings.add(map.getTotalRor() != null ? map.getTotalRor().toString() : "0");
//                    strings.add(map.getRorComputerized() != null ? map.getRorComputerized().toString() : "0");
//                    strings.add(map.getRorComputerizedPercent() != null && map.getRorComputerizedPercent().compareTo(BigDecimal.ZERO) != 0 ? map.getRorComputerizedPercent().toString() : "0.0");
//                    strings.add(map.getVillagesClrCompleted() != null ? map.getVillagesClrCompleted().toString() : "0");
//                    strings.add(map.getClrCompletionPercent() != null && map.getClrCompletionPercent().compareTo(BigDecimal.ZERO) != 0 ? map.getClrCompletionPercent().toString() : "0.0");
//                    strings.add(map.getTotalLandOwners() != null ? map.getTotalLandOwners().toString() : "0");
//                    strings.add(map.getDistrictsWithGenderBasedOwnership() != null ? map.getDistrictsWithGenderBasedOwnership().toString() : "0");
//                    strings.add(map.getTotalMaleLandOwners() != null ? map.getTotalMaleLandOwners().toString() : "0");
//                    strings.add(map.getTotalFemaleLandOwners() != null ? map.getTotalFemaleLandOwners().toString() : "0");
//                    strings.add(map.getTotalOwners() != null ? map.getTotalOwners().toString() : "0");
//
//                    reportDataList.add(strings);
//            });
//        }
//
//        List<List<String>> grandTotalData = new ArrayList<>();
//
//        if (grandTotal != null && !grandTotal.isEmpty()) {
//            grandTotal.forEach(gt -> {
//                List<String> grandTotalList = new ArrayList<>();
//                grandTotalList.add("");
//                grandTotalList.add("Grand Total");
//                grandTotalList.add(gt.getTotalTehsils() != null ? gt.getTotalTehsils().toString() : "0");
//                grandTotalList.add(gt.getTotalVillages() != null ? gt.getTotalVillages().toString() : "0");
//                grandTotalList.add(gt.getTotalRor() != null ? String.valueOf(gt.getTotalRor()) : "0");
//                grandTotalList.add(gt.getRorComputerized() != null ? String.valueOf(gt.getRorComputerized()) : "0");
//                grandTotalList.add(gt.getRorComputerizedPercent() != null && gt.getRorComputerizedPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getRorComputerizedPercent()) : "0.0");
//                grandTotalList.add(gt.getVillagesClrCompleted() != null ? String.valueOf(gt.getVillagesClrCompleted()) : "0");
//                grandTotalList.add(gt.getClrCompletionPercent() != null && gt.getClrCompletionPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getClrCompletionPercent()) : "0.0");
//                grandTotalList.add(gt.getTotalLandOwners() != null ? String.valueOf(gt.getTotalLandOwners()) : "0");
//                grandTotalList.add(gt.getDistrictsWithGenderBasedOwnership() != null ? String.valueOf(gt.getDistrictsWithGenderBasedOwnership()) : "0");
//                grandTotalList.add(gt.getTotalMaleLandOwners() != null ? String.valueOf(gt.getTotalMaleLandOwners()) : "0");
//                grandTotalList.add(gt.getTotalFemaleLandOwners() != null ? String.valueOf(gt.getTotalFemaleLandOwners()) : "0");
//                grandTotalList.add(gt.getTotalOwners() != null ? String.valueOf(gt.getTotalOwners()) : "0");
//
//                grandTotalData.add(grandTotalList);
//            });
//        }
//
//        pComponent.setReportDataList(reportDataList);
//        pComponent.setGrandTotal(grandTotalData);
//        PdfExporter.createPdf(pComponent, response);
//
//    }

    @GetMapping("/district_map")
    public void getMAPNewPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        String head[] = {ReportLabels.MAP_DIGITIZATION_REPORT,ReportLabels.STATE_UT +" - "+stateName};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.MAP_DIGITIZATION_REPORT);
        //v5: +3 cadastral columns
        float col_width[] = {30f, 90f, 50f, 50f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 40f, 40f, 50f, 50f, 50f, 50f, 40f, 40f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponent.setCol_width(col_width);
        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.DISTRICT_NAME, ReportLabels.TOTAL_TEHSILS, ReportLabels.NO_OF_CADASTRAL_MAPS_FMBS_TIPPANS, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS
                , ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.NO_OF_LAND_PARCELS,ReportLabels.SHOWING_CURRENT_OWNERSHIP,
                ReportLabels.CADASTRAL_MAPS, ReportLabels.FMBs, ReportLabels.TIPPANS, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.GEO_REFERENCED, ReportLabels.TOTAL, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_LINKED_TO_ROR, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_GEO_REFERENCED, ReportLabels.ULPIN_ASSIGNED, ReportLabels.TOTAL, ReportLabels.GEO_REFERENCED, ReportLabels.ULPIN_ASSIGNED,ReportLabels.YES_NO,ReportLabels.IF_NO_YEAR_UPTO_WHICH_ARE_UPDATED,
                //v5 leaf headers for Cadastral Maps
                ReportLabels.TOTAL, ReportLabels.TOTAL_DAMAGED_MISSING_MAPS, ReportLabels.MAP_IN_GOOD_CONDITION, ReportLabels.DIGITIZED, ReportLabels.DIGITIZED_PERCENT_OF_TOTAL_CADASTRAL_MAPS, ReportLabels.DIGITIZED_PERCENT_OF_GOOD_CONDITION_MAPS,
                ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO,ReportLabels.PERCENTAGE};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {21,22};
        Integer[] rwspn3col = {0, 1, 2};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {23,24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46,47,48,49,50,51};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        colspanval.add(new ArrayList<Integer>(Arrays.asList(3, 12))); //v5 9->12
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 5)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 7)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 5)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 6))); //v5 3->6
        colspanval.add(new ArrayList<Integer>(Arrays.asList(9, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(11, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(12, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(13, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(14, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(15, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(16, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(17, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(18, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(19, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(20, 2)));

        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(3, 6))); //v5 3->6
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 3)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(10, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(11, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(12, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(14, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(15, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(16, 2)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(17); //v5: was 14; +3 extras for 6 cadastral leaf cols
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<DistrictMapDigitizationReport> districtMapList = (List<DistrictMapDigitizationReport>) session.getAttribute("reportData");
        List<MapDigitizationReport> grandTotal = (List<MapDigitizationReport>) session.getAttribute("stateMapData");
        AtomicInteger indexHolder = new AtomicInteger();

        if (districtMapList != null && !districtMapList.isEmpty()) {
            districtMapList.forEach(map -> {
                List<String> strings = new ArrayList<>();
                strings.add(Integer.toString(indexHolder.incrementAndGet()));
                strings.add(map.getDistrictName().toUpperCase());
                strings.add(Integer.toString(Math.toIntExact(map.getTotalTehsils())));
                MapDigitizationReportExportV5Util.appendCadastralMapRowV5(strings, map, df); //v5
                strings.add(Integer.toString(map.getTotalFmbs()));
                strings.add(Integer.toString(map.getDigitizedFmbs()));
                strings.add(df.format(map.getDigitizedFmbsPercent()));
                strings.add(Integer.toString(map.getTotalTippans()));
                strings.add(Integer.toString(map.getDigitizedTippans()));
                strings.add(df.format(map.getDigitizedTippansPercent()));
                strings.add(Integer.toString(map.getTotalMapsFmbTippans()));
                strings.add(Integer.toString(map.getTotalDigitizedMapsFmbTippans()));
                strings.add(df.format(map.getDigitizedMapsFmbTippansPercent()));
                strings.add(Integer.toString(map.getGeoreferencedMaps()));
                strings.add(df.format(map.getGeoreferencedMapsPercent()));
                strings.add(Integer.toString(map.getTotalVillages()));
                strings.add(Integer.toString(map.getVillagesLinkedWithRor()));
                strings.add(df.format(map.getVillagesLinkedWithRorPercent()));
                strings.add(Integer.toString(map.getVillagesGeoreferenced()));
                strings.add(df.format(map.getVillagesGeoreferencedPercent()));
                strings.add(Integer.toString(map.getVillagesWithUlipn()));
                strings.add(df.format(map.getVillagesWithUlipnPercent()));
                strings.add(Integer.toString(map.getTotalLandParcels()));
                strings.add(Integer.toString(map.getGeoreferencedLandParcels()));
                strings.add(df.format(map.getGeoreferencedLandParcelsPercent()));
                strings.add(Integer.toString(map.getLandParcelsWithUlipn()));
                strings.add(df.format(map.getLandParcelsWithUlipnPercent()));
                strings.add(map.getMapsUpdatedBasedOnMutation());
                strings.add(map.getMapsUpdatePeriodDate());
                reportDataList.add(strings);
            });
        }

        List<List<String>> grandTotalData = new ArrayList<>();

        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> stringList = new ArrayList<>();
                stringList.add("");
                stringList.add("Grand Total");
                stringList.add(Integer.toString(gt.getTotalTehsils()));
                MapDigitizationReportExportV5Util.appendCadastralMapRowV5Raw(stringList, gt, df); //v5
                stringList.add(Integer.toString(gt.getTotalFmbs()));
                stringList.add(Integer.toString(gt.getDigitizedFmbs()));
                stringList.add(df.format(gt.getDigitizedFmbsPercent()));
                stringList.add(Integer.toString(gt.getTotalTippans()));
                stringList.add(Integer.toString(gt.getDigitizedTippans()));
                stringList.add(df.format(gt.getDigitizedTippansPercent()));
                stringList.add(Integer.toString(gt.getTotalMapsFmbTippans()));
                stringList.add(Integer.toString(gt.getTotalDigitizedMapsFmbTippans()));
                stringList.add(df.format(gt.getDigitizedMapsFmbTippansPercent()));
                stringList.add(Integer.toString(gt.getGeoreferencedMaps()));
                stringList.add(df.format(gt.getGeoreferencedMapsPercent()));
                stringList.add(Integer.toString(gt.getTotalVillages()));
                stringList.add(Integer.toString(gt.getVillagesLinkedWithRor()));
                stringList.add(df.format(gt.getVillagesLinkedWithRorPercent()));
                stringList.add(Integer.toString(gt.getVillagesGeoreferenced()));
                stringList.add(df.format(gt.getVillagesGeoreferencedPercent()));
                stringList.add(Integer.toString(gt.getVillagesWithUlipn()));
                stringList.add(df.format(gt.getVillagesWithUlipnPercent()));
                stringList.add(Integer.toString(gt.getTotalLandParcels()));
                stringList.add(Integer.toString(gt.getGeoreferencedLandParcels()));
                stringList.add(df.format(gt.getGeoreferencedLandParcelsPercent()));
                stringList.add(Integer.toString(gt.getLandParcelsWithUlipn()));
                stringList.add(df.format(gt.getLandParcelsWithUlipnPercent()));
                stringList.add(gt.getMapsUpdatedBasedOnMutation());
                stringList.add(gt.getMapsUpdatePeriodDate());
                grandTotalData.add(stringList);
            });
        }

        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);

    }

    @GetMapping("/district_mrr")
    public void getMRRPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        String head[] = {ReportLabels.MRR_REPORT,ReportLabels.STATE_UT +" - "+stateName};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.MRR_REPORT);
        float col_width[] = {20f, 100f, 30f, 20f, 20f, 20f, 20f, 20f, 20f};
        pComponent.setCol_width(col_width);
        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.DISTRICT_NAME, ReportLabels.TOTAL_TEHSILS, ReportLabels.MRR_SANCTIONED, ReportLabels.MRR_COMPLETED_OUT_OF_TOTAL, ReportLabels.MRR_COMPLETED_OUT_OF_SANCTIONED
                , ReportLabels.NO,ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {0, 1, 2};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {6,7,8,9,10,11};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(3, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(3, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(3);
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<DistrictMrrViewReport> districtMrrList = (List<DistrictMrrViewReport>) session.getAttribute("districtMrrData");
        List<MrrViewReport> grandTotal = (List<MrrViewReport>) session.getAttribute("stateMrrData");
        AtomicInteger indexHolder = new AtomicInteger();

        if (districtMrrList != null && !districtMrrList.isEmpty()) {
            districtMrrList.forEach(map -> {
                List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getDistrictName() != null ? map.getDistrictName().toUpperCase() : "N/A");
                    strings.add(String.valueOf(map.getTotalTehsils() != null ? map.getTotalTehsils() : "0"));
                    strings.add(String.valueOf(map.getMrrSanctioned() != null ? map.getMrrSanctioned() : "0"));
                    strings.add(String.valueOf(map.getMrrSanctionedPercent() != null ? map.getMrrSanctionedPercent() : "0.0"));
                    strings.add(String.valueOf(map.getMrrCompleted() != null ? map.getMrrCompleted() : "0"));
                    strings.add(String.valueOf(map.getMrrCompletedOutOfTotalTehsilsPercent() != null ? map.getMrrCompletedOutOfTotalTehsilsPercent() : "0.0"));
                    strings.add(String.valueOf(map.getMrrCompleted() != null ? map.getMrrCompleted() : "0"));
                    strings.add(String.valueOf(map.getMrrCompletedOutOfSanctionedPercent() != null ? map.getMrrCompletedOutOfSanctionedPercent() : "0.0"));
                    reportDataList.add(strings);
            });
        }

        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add("Grand Total");
                grandTotalList.add(gt.getTotalTehsils() != null ? gt.getTotalTehsils().toString() : "0");
                grandTotalList.add(gt.getMrrSanctioned() != null ? gt.getMrrSanctioned().toString() : "0");
                grandTotalList.add(gt.getMrrSanctionedPercent() != null ? String.valueOf(gt.getMrrSanctionedPercent()) : "0.0");
                grandTotalList.add(gt.getMrrCompleted() != null ? String.valueOf(gt.getMrrCompleted()) : "0");
                grandTotalList.add(gt.getMrrCompletedOutOfTotalTehsilsPercent() != null ? String.valueOf(gt.getMrrCompletedOutOfTotalTehsilsPercent()) : "0.0");
                grandTotalList.add(gt.getMrrCompleted() != null ? String.valueOf(gt.getMrrCompleted()) : "0");
                grandTotalList.add(gt.getMrrCompletedOutOfSanctionedPercent() != null ? String.valueOf(gt.getMrrCompletedOutOfSanctionedPercent()) : "0.0");
                grandTotalData.add(grandTotalList);

            });
        }

        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);
    }


    @GetMapping("/district_aadhaar_linkage")
    public void getAadharpdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        String head[] = {ReportLabels.AADHAR_REPORT,ReportLabels.STATE_UT +" - "+stateName};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.AADHAR_REPORT);
        //v5: +2 RoR Address +7 Land owners
        float col_width[] = {20f, 65f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponent.setCol_width(col_width);
        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.DISTRICT_NAME, ReportLabels.TOTAL_TEHSILS, ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.RoR, ReportLabels.NUMBER_OF_LAND_OWNERS,
                ReportLabels.TOTAL,ReportLabels.WHERE_AT_LEAST_ONE_RoR_LINKED_WITH_AADHAAR,ReportLabels.WHERE_100_PERCENT_RoR_LINKED_WITH_AADHAAR,ReportLabels.TOTAL,ReportLabels.LINKED_WITH_AADHAAR,ReportLabels.LINKED_WITH_MOBILE_NUMBER,ReportLabels.LINKED_WITH_ADDRESS,
                ReportLabels.TOTAL,ReportLabels.LINKED_WITH_AADHAAR,ReportLabels.LINKED_WITH_MOBILE_NUMBER,ReportLabels.LINKED_WITH_ADDRESS,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO,ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {6,7,8,9,10,11,12,13,14,15,16};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {0, 1, 2};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        colspanval.add(new ArrayList<Integer>(Arrays.asList(3, 5)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 7)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 7)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(9, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(11, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(12, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(13, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(14, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(15, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(16, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(9, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(11, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(12, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(13, 2)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(8); //v5
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<DistrictLinkedAadhaarViewReport> districtAadhaarList = (List<DistrictLinkedAadhaarViewReport>) session.getAttribute("districtAadhaarData");
        List<LinkedAadharViewReport> grandTotal = (List<LinkedAadharViewReport>) session.getAttribute("stateAadhaarData");
        AtomicInteger indexHolder = new AtomicInteger();

        if (districtAadhaarList != null && !districtAadhaarList.isEmpty()) {
            districtAadhaarList.forEach(map -> {
                List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(String.valueOf(map.getDistrictName() != null ? map.getDistrictName().toUpperCase() : "N/A"));
                    strings.add(String.valueOf(map.getTotalTehsils() != null ? map.getTotalTehsils() : "0"));
                    strings.add(String.valueOf(map.getTotalVillages() != null ? map.getTotalVillages() : "0"));
                    strings.add(String.valueOf(map.getVillagesWithRorLinkedAadhaar() != null ? map.getVillagesWithRorLinkedAadhaar() : "0"));
                    strings.add(String.valueOf(map.getVillagesWithRorLinkedAadhaarPercent() != null ? map.getVillagesWithRorLinkedAadhaarPercent() : "0.0"));
                    strings.add(String.valueOf(map.getVillagesWith100PercentRorLinkedAadhaar() != null ? map.getVillagesWith100PercentRorLinkedAadhaar() : "0"));
                    strings.add(String.valueOf(map.getVillagesWith100PercentRorLinkedAadhaarPercent() != null ? map.getVillagesWith100PercentRorLinkedAadhaarPercent() : "0.0"));
                    strings.add(String.valueOf(map.getTotalRor() != null ? map.getTotalRor() : "0"));
                    strings.add(String.valueOf(map.getRorLinkedWithAadhaar() != null ? map.getRorLinkedWithAadhaar() : "0"));
                    strings.add(String.valueOf(map.getRorLinkedWithAadhaarPercent() != null ? map.getRorLinkedWithAadhaarPercent() : ".00"));
                    strings.add(String.valueOf(map.getRorLinkedWithMobileNumber() != null ? map.getRorLinkedWithMobileNumber() : "0"));
                    strings.add(String.valueOf(map.getRorLinkedWithMobileNumberPercent() != null ? map.getRorLinkedWithMobileNumberPercent() : "0.0"));
                    AadhaarReportExportV5Util.appendAddressRowDistrict(strings, map); //v5
                    AadhaarReportExportV5Util.appendLandOwnerRowDistrict(strings, map); //v5
                    reportDataList.add(strings);
            });
        }

        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add("Grand Total");
                grandTotalList.add(gt.getTotalTehsils() != null ? gt.getTotalTehsils().toString() : "0");
                grandTotalList.add(gt.getTotalVillages() != null ? gt.getTotalVillages().toString() : "0");
                grandTotalList.add(gt.getVillagesWithRorLinkedAadhaar() != null ? String.valueOf(gt.getVillagesWithRorLinkedAadhaar()) : "0");
                grandTotalList.add(gt.getVillagesWithRorLinkedAadhaarPercent() != null ? String.valueOf(gt.getVillagesWithRorLinkedAadhaarPercent()) : "0.0");
                grandTotalList.add(gt.getVillagesWith100PercentRorLinkedAadhaar() != null ? String.valueOf(gt.getVillagesWith100PercentRorLinkedAadhaar()) : "0");
                grandTotalList.add(gt.getVillagesWith100PercentRorLinkedAadhaarPercent() != null && gt.getVillagesWith100PercentRorLinkedAadhaarPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getVillagesWith100PercentRorLinkedAadhaarPercent()) : "0.0");
                grandTotalList.add(gt.getTotalRor() != null ? String.valueOf(gt.getTotalRor()) : "0");
                grandTotalList.add(gt.getRorLinkedWithAadhaar() != null ? String.valueOf(gt.getRorLinkedWithAadhaar()) : "0");
                grandTotalList.add(gt.getRorLinkedWithAadhaarPercent() != null && gt.getRorLinkedWithAadhaarPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getRorLinkedWithAadhaarPercent()) : "0.0");
                grandTotalList.add(gt.getRorLinkedWithMobileNumber() != null ? String.valueOf(gt.getRorLinkedWithMobileNumber()) : "0");
                grandTotalList.add(gt.getRorLinkedWithMobileNumberPercent() != null && gt.getRorLinkedWithMobileNumberPercent().compareTo(BigDecimal.ZERO) != 0 ? String.valueOf(gt.getRorLinkedWithMobileNumberPercent()) : "0.0");
                AadhaarReportExportV5Util.appendAddressRowRaw(grandTotalList, gt); //v5
                AadhaarReportExportV5Util.appendLandOwnerRowRaw(grandTotalList, gt); //v5
                grandTotalData.add(grandTotalList);
            });
        }

        pComponent.setGrandTotal(grandTotalData);
        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);
    }


    @GetMapping("/district_survey_resurvey")
    public void getSurveyNewpdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        String head[] = {ReportLabels.SURVEY_REPORT,ReportLabels.STATE_UT +" - "+stateName};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.SURVEY_REPORT);
        float col_width[] = {20f, 65f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponent.setCol_width(col_width);
        String col_head[] = {ReportLabels.SERIAL_NUMBER, ReportLabels.DISTRICT_NAME, ReportLabels.TOTAL_TEHSILS,ReportLabels.TOTAL_VILLAGES, ReportLabels.TOTAL_RURAL_REVENUE_AREA_SQ_KM, ReportLabels.AREA_SANCTIONED_FOR_SURVEY_RE_SURVEY_SQ_KM,ReportLabels.DRON_FLYING_SURVEY,ReportLabels.NUMBER_OF_VILLAGES,ReportLabels.AREA_OF_VILLAGES_WHERE_SURVEY_SANCTIONED_NOT_STARTED,
                ReportLabels.NUMBER_OF_VILLAGES,ReportLabels.AREA_OF_VILLAGES_SQ_KM,ReportLabels.MAP_1_IS_GENERATED,ReportLabels.DRAFT_MAP_PUBLISHED,ReportLabels.FINAL_PROMULGATION_DONE,ReportLabels.SURVEY_SANCTIONED_NOT_STARTED,};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {0, 1, 2, 3, 4,5, 8};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {9,10, 11, 12, 13, 14};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 4)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(7, 4)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(4);
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<DistrictSurveyResurveyViewReport> districtSurveyList = (List<DistrictSurveyResurveyViewReport>) session.getAttribute("districtSurveyData");
        List<SurveyResurveyViewReport> grandTotal = (List<SurveyResurveyViewReport>) session.getAttribute("stateSurveyData");
        AtomicInteger indexHolder = new AtomicInteger();

        if (districtSurveyList != null && !districtSurveyList.isEmpty()) {
            districtSurveyList.forEach(map -> {
                List<String> strings = new ArrayList<>();
                    // Populate normal row data
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(String.valueOf(map.getDistrictName() != null ? map.getDistrictName().toUpperCase() : "N/A"));
                    strings.add(String.valueOf(map.getTotalTehsils() != null ? map.getTotalTehsils() : "0"));
                    strings.add(String.valueOf(map.getTotalVillages() != null ? map.getTotalVillages() : "0"));
                    strings.add(String.valueOf(map.getTotalRuralRevenueArea() != null ? map.getTotalRuralRevenueArea() : "0.0"));
                    strings.add(String.valueOf(map.getAreaSanctionedForSurvey() != null ? map.getAreaSanctionedForSurvey() : "0.0"));
                    strings.add(String.valueOf(map.getVillagesDroneFlyingCompleted() != null ? map.getVillagesDroneFlyingCompleted() : "0"));
                    strings.add(String.valueOf(map.getAreaDroneFlyingCompleted() != null ? map.getAreaDroneFlyingCompleted() : "0.0"));
                    strings.add(String.valueOf(map.getVillagesMap1Generated() != null ? map.getVillagesMap1Generated() : "0"));
                    strings.add(String.valueOf(map.getVillagesDraftMapPublished() != null ? map.getVillagesDraftMapPublished() : "0"));
                    strings.add(String.valueOf(map.getVillagesFinalPromulgationDone() != null ? map.getVillagesFinalPromulgationDone() : "0"));
                    strings.add(String.valueOf(map.getVillagesSurveySanctionNotStarted() != null ? map.getVillagesSurveySanctionNotStarted() : "0"));
                    strings.add(String.valueOf(map.getAreaSurveySanctionNotStarted() != null ? map.getAreaSurveySanctionNotStarted() : "0.0"));

                    reportDataList.add(strings);
            });
        }


        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add("Grand Total");
                grandTotalList.add(gt.getTotalTehsils() != null ? gt.getTotalTehsils().toString() : "0");
                grandTotalList.add(gt.getTotalVillages() != null ? gt.getTotalVillages().toString() : "0");
                grandTotalList.add(gt.getTotalRuralRevenueArea() != null ? String.valueOf(gt.getTotalRuralRevenueArea()) : "0.0");
                grandTotalList.add(gt.getAreaSanctionedForSurvey() != null ? String.valueOf(gt.getAreaSanctionedForSurvey()) : "0.0");
                grandTotalList.add(gt.getVillagesDroneFlyingCompleted() != null ? String.valueOf(gt.getVillagesDroneFlyingCompleted()) : "0");
                grandTotalList.add(gt.getAreaDroneFlyingCompleted() != null ? String.valueOf(gt.getAreaDroneFlyingCompleted()) : "0.0");
                grandTotalList.add(gt.getVillagesMap1Generated() != null ? String.valueOf(gt.getVillagesMap1Generated()) : "0");
                grandTotalList.add(gt.getVillagesDraftMapPublished() != null ? String.valueOf(gt.getVillagesDraftMapPublished()) : "0");
                grandTotalList.add(gt.getVillagesFinalPromulgationDone() != null ? String.valueOf(gt.getVillagesFinalPromulgationDone()) : "0");
                grandTotalList.add(gt.getVillagesSurveySanctionNotStarted() != null ? String.valueOf(gt.getVillagesSurveySanctionNotStarted()) : "0");
                grandTotalList.add(gt.getAreaSurveySanctionNotStarted() != null ? String.valueOf(gt.getAreaSurveySanctionNotStarted()) : "0.0");

                grandTotalData.add(grandTotalList);
            });
        }

        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);

    }


    @GetMapping("/district_rcms")
    public void getRCMSpdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        String head[] = {ReportLabels.RCMS_REPORT,ReportLabels.STATE_UT +" - "+stateName};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.RCMS_REPORT);
        float col_width[] = {20f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 70f, 70f, 70f,70f,70f,70f,70f,70f,70f,70f};
        pComponent.setCol_width(col_width);
        String col_head[] = {
                ReportLabels.SERIAL_NUMBER, ReportLabels.DISTRICT_NAME, ReportLabels.TOTAL_TEHSILS, ReportLabels.NUMBER_OF_REVENUE_COURTS,ReportLabels.E_REVENUE_COURT_MANAGEMENT_SYSTEM + " (" + ReportLabels.YES_NO + ")",ReportLabels.ONLINE_PROCESS_OF_REVENUE_COURT_PROCESSES_AVAILABLE,ReportLabels.WHETHER_PROCEEDS + " (" + ReportLabels.YES_NO + ")",ReportLabels.WHETHER_LAW_RECORD_ONLINE_COURT + " (" + ReportLabels.YES_NO + ")", ReportLabels.WHETHER_FILING_DIRECTLY_SYSTEM + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.TOTAL, ReportLabels.COMPUTERIZED_ONLINE,ReportLabels.ATTACHED_DOCUMENTS + " (" + ReportLabels.YES_NO + ")",ReportLabels.CAUSE_LIST_GENERATION + " (" + ReportLabels.YES_NO + ")", ReportLabels.DEFENDANT_NOTIFICATION + " (" + ReportLabels.YES_NO + ")", ReportLabels.COUNTER_AFFIDAVIT_FILING + " (" + ReportLabels.YES_NO + ")", ReportLabels.PROCEEDINGS_TYPING_SYSTEM + " (" + ReportLabels.YES_NO + ")",ReportLabels.UPLOADING_COURT_ORDERS_NOTIFICATION + " (" + ReportLabels.YES_NO + ")", ReportLabels.LAND_RECORD_INTEGRATION + " (" + ReportLabels.YES_NO + ")", ReportLabels.ONLINE_LAND_RECORD_CHECK + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.NO,ReportLabels.NO,ReportLabels.PERCENTAGE,
        };
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {9,10};
        Integer[] rwspn3col = {11,12, 13, 14, 15,16,17,18};
        Integer[] rwspn4col = {0,1,2,4,6,7,8};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {19,20,21};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(3, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 8)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(9, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 2)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(3, 3)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 8)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(9);
        List<List<String>> reportDataList = new ArrayList<List<String>>();
        List<DistrictRcmsReportDTO> districtRcmsList = (List<DistrictRcmsReportDTO>) session.getAttribute("districtRcmsData");
        List<RcmsReportDTO> grandTotal = (List<RcmsReportDTO>) session.getAttribute("stateRcmsData");
        AtomicInteger indexHolder = new AtomicInteger();

        if (districtRcmsList != null && !districtRcmsList.isEmpty()) {
            districtRcmsList.forEach(map -> {
                List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(String.valueOf(map.getDistrictName() != null ? map.getDistrictName().toUpperCase() : "N/A"));
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
            });
        }


        List<List<String>> grandTotalData = new ArrayList<>();
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add("Grand Total");
                grandTotalList.add(gt.getTotalTehsils() != null ? gt.getTotalTehsils().toString() : "0");
                grandTotalList.add(gt.getTotalRevenueCourts() != null ? gt.getTotalRevenueCourts().toString() : "0");
                grandTotalList.add(gt.getRevenueCourtsComputerized() != null ? String.valueOf(gt.getRevenueCourtsComputerized()) : "0");
                grandTotalList.add(gt.getRevenueCourtsComputerizedPercent() != null ? String.valueOf(gt.getRevenueCourtsComputerizedPercent()) : "0.0");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");

                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");
                grandTotalList.add("");

//                grandTotalList.add(gt.getERcmsAvailable() != null ? gt.getERcmsAvailable() : "NO");
//                grandTotalList.add(gt.getAffidavitFilingOnline() != null ? gt.getAffidavitFilingOnline() : "NO");
//                grandTotalList.add(gt.getCauseListGenerationOnline() != null ? gt.getCauseListGenerationOnline() : "NO");
//                grandTotalList.add(gt.getNoticeToDefendantsOnline() != null ? gt.getNoticeToDefendantsOnline() : "NO");
//                grandTotalList.add(gt.getCounterAffidavitFilingOnline() != null ? gt.getCounterAffidavitFilingOnline() : "NO");
//                grandTotalList.add(gt.getProceedingsTypingDirectlyOnline() != null ? gt.getProceedingsTypingDirectlyOnline() : "NO");
//                grandTotalList.add(gt.getUploadingRevenueCourtOrdersOnline() != null ? gt.getUploadingRevenueCourtOrdersOnline() : "NO");
//
//                grandTotalList.add(gt.getLandRecordsOnlineFromRevenueCourtSystem() != null ? gt.getLandRecordsOnlineFromRevenueCourtSystem() : "NO");
//                grandTotalList.add(gt.getRevenueCourtProceedingsPaperless() != null ? gt.getRevenueCourtProceedingsPaperless() : "NO");
//                grandTotalList.add(gt.getLandRecordsOnlineForCivilCourts() != null ? gt.getLandRecordsOnlineForCivilCourts() : "NO");
//                grandTotalList.add(gt.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() != null ? gt.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() : "NO");
//                grandTotalList.add(gt.getCaseFilingRedFlaggedInLandRecords() != null ? gt.getCaseFilingRedFlaggedInLandRecords() : "NO");
                grandTotalData.add(grandTotalList);
            });
        }

        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotalData);
        PdfExporter.createPdf(pComponent, response);
    }




}
