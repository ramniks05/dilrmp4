package in.gov.dilrmp.controllers.pdfExcelPhysicalProgressReports;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import in.gov.dilrmp.models.naksha.CapacityBuilding;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntry;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntrySolrAndVender;
import in.gov.dilrmp.models.reportDTO.ComparisonReportDTO.ComparisonReportDTO;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.DistrictMapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.DistrictClrReportView;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.legacy.DistrictLegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.legacy.LegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.DistrictLegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.LegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.DistrictLinkedAadhaarViewReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.DistrictRcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.sroModernization.SroModernizationReport;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.DistrictSurveyResurveyViewReport;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.services.DataEntryForm.StateMISDataEntryService;
import in.gov.dilrmp.services.physicalProgressServices.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import in.gov.dilrmp.component.ExcelExporterComponent;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.utils.AadhaarReportExportV5Util;
import in.gov.dilrmp.utils.ExcelExporter;
import in.gov.dilrmp.utils.LegacyDigitizationExportUtil;
import in.gov.dilrmp.utils.LegacyRevenueExportUtil;
import in.gov.dilrmp.utils.MapDigitizationReportExportV5Util;
import in.gov.dilrmp.utils.SroModernizationExportUtil;

@Controller
@RequestMapping("physcial/report/excel")
public class PhyscialProgressReportControllerExcel {

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
    public void exportCLRExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();

        // Header text array — v5: RoR 5 leaf cols + Auto-Mutation Facility after Auto-Triggered
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                ReportLabels.CLR_REPORT, "",
                ReportLabels.SERIAL_NUMBER, ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS,
                ReportLabels.TOTAL_TEHSILS, ReportLabels.TOTAL_VILLAGES,
                ReportLabels.RoR, ReportLabels.NUMBER_OF_VILLAGES_WHERE_CLR_COMPLETED,
                ReportLabels.TOTAL_NO_OF_LAND_OWNERS,
                ReportLabels.TOTAL, ReportLabels.COMPUTERIZED, ReportLabels.PERCENTAGE,
                ReportLabels.ROR_WITH_CADASTRAL_MAP, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.AVAILABILITY_OF_GENDER_BASED_LAND_OWNERSHIP,
                ReportLabels.NUMBER_OF_DISTRICTS_WHERE_AVAILABLE, ReportLabels.MALE, ReportLabels.FEMALE, ReportLabels.TOTAL,
                ReportLabels.WHETHER_ROR_AVAILABLE_ONLINE,
                ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_AVAILABLE_ONLINE,
                ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_LEGALLY_VALID_IN_STATE,
                ReportLabels.WHETHER_MUTATION_APPLICATION_SUBMITTED_ONLINE,
                ReportLabels.WHETHER_AUTO_TRIGGERED_MUTATION_FACILITY_AVAILABLE,
                ReportLabels.WHETHER_AUTO_MUTATION_FACILITY_AVAILABLE,
                ReportLabels.WHETHER_BANKS_AUTHORIZED_TO_CREATE_CLEAR_MORTGAGE_CHARGE_IN_ROR,
                ReportLabels.YES_NO, ReportLabels.NUMBER_OF_DISTRICTS_AUTHORIZED, ReportLabels.NUMBER_OF_BANK_BRANCHES_AUTHORIZED,
                ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_SRO,
                ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_REVENUE_COURTS,
                ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_CIVIL_COURTS_THROUGH_E_COURTS_SYSTEM,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,

        };

        // Header spans: F-J = 5 RoR; V = Auto-Triggered; W = Auto-Mutation (v5); X-Z = Banks
        String[] headerSpanMerged = {
                "A1:AC1", "A2:AC2", "A3:AC3", "A4:AC4", "A5:AC5", "A6:A8", "B6:B8", "C6:C8", "D6:D8", "E6:E8",
                "F6:J6", "K6:L6", "M6:M8",
                "F7:F8", "G7:G8", "H7:H8", "I7:I8", "J7:J8", "K7:K8", "L7:L8",
                "N6:Q6", "N7:N8", "O7:O8", "P7:P8", "Q7:Q8",
                "R6:R7", "S6:S7", "T6:T7", "U6:U7", "V6:V7", "W6:W7",
                "X6:Z6", "X7:X8", "Y7:Y8", "Z7:Z8",
                "AA6:AA7", "AB6:AB7", "AC6:AC7"
        };
        String[] headerSpanUnmerged = {"R8", "S8", "T8", "U8", "V8", "W8", "AA8", "AB8", "AC8"};
        eComponent.setReportName(ReportLabels.CLR_REPORT);
        eComponent.setNoOfColumns(29);
        eComponent.setNoOfheaderRows(8);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);

        // Get the CLR report data from session
        List<StateClrReportView> clrList = (List<StateClrReportView>) session.getAttribute("stateClrList");
        List<StateClrReportView> grandTotal = (List<StateClrReportView>) session.getAttribute("clrGrandTotal");

        List<List<String>> reportDataList = new ArrayList<>();

        AtomicInteger indexHolder = new AtomicInteger();

        // Process clrList to create report data
        if (clrList != null && !clrList.isEmpty()) {
            clrList.forEach(map -> {
                if (map.getStateId() != 999) { // Exclude grand total states
                    List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));  // Serial Number
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
                reportDataList.add(grandTotalList);
            });
        }

        // Set the combined data (state data + grand total) to the exporter component
        eComponent.setReportDataList(reportDataList);

        // Generate the Excel file
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/clr-district")
    public void exportCLRDistrictExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        String woner = (String) session.getAttribute("Wonership");

        // Header text array — v5: RoR 5 leaf cols + Auto-Mutation Facility after Auto-Triggered
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                ReportLabels.CLR_REPORT, ReportLabels.STATE_UT + " - " + stateName, ReportLabels.GENDER_OWNERSHIP_HEADING + " : " + woner,
                ReportLabels.SERIAL_NUMBER, ReportLabels.DISTRICT_NAME,
                ReportLabels.TOTAL_TEHSILS, ReportLabels.TOTAL_VILLAGES,
                ReportLabels.RoR, ReportLabels.NUMBER_OF_VILLAGES_WHERE_CLR_COMPLETED,
                ReportLabels.TOTAL, ReportLabels.COMPUTERIZED, ReportLabels.PERCENTAGE,
                ReportLabels.ROR_WITH_CADASTRAL_MAP, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.TOTAL_NO_OF_LAND_OWNERS,
                ReportLabels.AVAILABILITY_OF_GENDER_BASED_LAND_OWNERSHIP,
                ReportLabels.NUMBER_OF_DISTRICTS_WHERE_AVAILABLE, ReportLabels.MALE, ReportLabels.FEMALE, ReportLabels.TOTAL,
                ReportLabels.WHETHER_ROR_AVAILABLE_ONLINE,
                ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_AVAILABLE_ONLINE,
                ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_LEGALLY_VALID_IN_STATE,
                ReportLabels.WHETHER_MUTATION_APPLICATION_SUBMITTED_ONLINE,
                ReportLabels.WHETHER_AUTO_TRIGGERED_MUTATION_FACILITY_AVAILABLE,
                ReportLabels.WHETHER_AUTO_MUTATION_FACILITY_AVAILABLE,
                ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_SRO,
                ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_REVENUE_COURTS,
                ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_CIVIL_COURTS_THROUGH_E_COURTS_SYSTEM,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO,
                ReportLabels.YES_NO


        };

        // Header spans: E-I = 5 RoR; U = Auto-Triggered; V = Auto-Mutation (v5)
        String[] headerSpanMerged = {
                "A1:Y1", "A2:Y2", "A3:Y3", "A4:Y4", "A5:Y5", "A6:Y6",
                "A7:A9", "B7:B9", "C7:C9", "D7:D9",
                "E7:I7", "J7:K7",
                "E8:E9", "F8:F9", "G8:G9", "H8:H9", "I8:I9", "J8:J9", "K8:K9",
                "L7:L9",
                "M7:P7", "M8:M9", "N8:N9", "O8:O9", "P8:P9",
                "Q7:Q8", "R7:R8", "S7:S8", "T7:T8", "U7:U8", "V7:V8", "W7:W8", "X7:X8", "Y7:Y8"
        };
        String[] headerSpanUnmerged = {"Q9", "R9", "S9", "T9", "U9", "V9", "W9", "X9", "Y9"};
        eComponent.setReportName(ReportLabels.CLR_REPORT);
        eComponent.setNoOfColumns(25);
        eComponent.setNoOfheaderRows(9);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);

        // Get the CLR report data from session
        List<DistrictClrReportView> districtClrList = (List<DistrictClrReportView>) session.getAttribute("districtClrData");
        List<StateClrReportView> grandTotal = (List<StateClrReportView>) session.getAttribute("stateClrData");

        List<List<String>> reportDataList = new ArrayList<>();

        AtomicInteger indexHolder = new AtomicInteger();

        // Process clrList to create report data
        if (districtClrList != null && !districtClrList.isEmpty()) {
            districtClrList.forEach(map -> {
                if (map.getStateId() != 999) { // Exclude grand total states
                    List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));  // Serial Number
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
                }
            });
        }

        // Add Grand Total to the data
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
                ;

                reportDataList.add(grandTotalList);
            });
        }

        // Set the combined data (state data + grand total) to the exporter component
        eComponent.setReportDataList(reportDataList);

        // Generate the Excel file
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/map")
    public void exportMAPExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();

        // Header text array — v5: Cadastral Maps has 6 leaf columns
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)", ReportLabels.MAP_DIGITIZATION_REPORT, "",
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.STATE_UT,
                ReportLabels.TOTAL_DISTRICTS,
                ReportLabels.TOTAL_TEHSILS,
                ReportLabels.NO_OF_CADASTRAL_MAPS_FMBS_TIPPANS,
                ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS,
                ReportLabels.NUMBER_OF_VILLAGES,
                ReportLabels.NO_OF_LAND_PARCELS,
                ReportLabels.NUMBER_OF_DISTRICT_WHERE_CADMAP_FMB_TIPPAN_SHOWING_CURRENT_OWNERSHIP,
                ReportLabels.CADASTRAL_MAPS, ReportLabels.FMBs, ReportLabels.TIPPANS, ReportLabels.DIGITIZED, ReportLabels.GEO_REFERENCED, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_LINKED_TO_ROR, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_GEO_REFERENCED, ReportLabels.ULPIN_ASSIGNED, ReportLabels.GEO_REFERENCED, ReportLabels.ULPIN_ASSIGNED, ReportLabels.TOTAL, ReportLabels.TOTAL, ReportLabels.TOTAL
                , ReportLabels.TOTAL, ReportLabels.TOTAL_DAMAGED_MISSING_MAPS, ReportLabels.MAP_IN_GOOD_CONDITION, ReportLabels.DIGITIZED, ReportLabels.DIGITIZED_PERCENT_OF_TOTAL_CADASTRAL_MAPS, ReportLabels.DIGITIZED_PERCENT_OF_GOOD_CONDITION_MAPS,
                ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO

        };

        // Header spans: E-P = 12 cadastral group; E-J = 6 cadastral maps sub-group
        String[] headerSpanMerged = {
                "A1:AH1", "A2:AH2", "A3:AH3", "A4:AH4", "A5:AH5", "A6:A8", "B6:B8", "C6:C8", "D6:D8",
                "E6:P6", "Q6:U6", "V6:AB6", "AC6:AG6", "AH6:AH7",
                "E7:J7", "K7:M7", "N7:P7", "R7:S7", "T7:U7"
                , "W7:X7", "Y7:Z7", "AA7:AB7",
                "AD7:AE7"
                , "AF7:AG7"
        };
        String[] headerSpanUnmerged = {"Q7", "V7", "AC7",
                "E8", "F8", "G8", "H8", "I8", "J8", "K8", "L8", "M8", "N8",
                "O8", "P8", "Q8", "R8", "S8", "T8", "U8", "V8", "W8", "X8",
                "Y8", "Z8", "AA8", "AB8", "AC8", "AD8", "AE8", "AF8", "AG8", "AH8"};
        eComponent.setReportName(ReportLabels.MAP_DIGITIZATION_REPORT);
        eComponent.setNoOfColumns(34);
        eComponent.setNoOfheaderRows(9);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<MapDigitizationReport> mapList = (List<MapDigitizationReport>) session.getAttribute("reportData");
        List<MapDigitizationReport> grandTotal = (List<MapDigitizationReport>) session.getAttribute("mapGrandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        if (mapList != null && !mapList.isEmpty()) {
            mapList.forEach(map -> {
                if (map.getLgdCode() != 999) {
                    List<String> strings = new ArrayList<>();
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
                reportDataList.add(strings);
            });
        }

        eComponent.setReportDataList(reportDataList);

        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/map-dolr")
    public void exportMAPDoLRExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)", ReportLabels.MAP_DIGITIZATION_REPORT_MAPFMBTIPPAN, "",
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.STATE_UT,
                ReportLabels.TOTAL_DISTRICTS,
                ReportLabels.TOTAL_TEHSILS,
                ReportLabels.NO_OF_CADASTRAL_MAPS_FMBS_TIPPANS,
                ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS,
                ReportLabels.CADASTRAL_MAPS, ReportLabels.FMBs, ReportLabels.TIPPANS,
                ReportLabels.DIGITIZED, ReportLabels.TOTAL
                , ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE,
                ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE,
                ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE,
        };
        String[] headerSpanMerged = {
                "A1:P1", "A2:P2", "A3:P3", "A4:P4", "A5:P5",
                "A6:A8", "B6:B8", "C6:C8", "D6:D8",
                "E6:M6", "N6:P6",
                "E7:G7", "H7:J7", "K7:M7", "O7:P7"};
        String[] headerSpanUnmerged = {"N7",
                "E8", "F8", "G8", "H8", "I8", "J8", "K8", "L8", "M8", "N8",
                "O8", "P8"};
        eComponent.setReportName(ReportLabels.MAP_DIGITIZATION_REPORT);
        eComponent.setNoOfColumns(16);
        eComponent.setNoOfheaderRows(9);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<MapDigitizationReport> mapList = (List<MapDigitizationReport>) session.getAttribute("reportData");
        List<MapDigitizationReport> grandTotal = (List<MapDigitizationReport>) session.getAttribute("mapGrandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        if (mapList != null && !mapList.isEmpty()) {
            mapList.forEach(map -> {
                if (map.getLgdCode() != 999) {
                    List<String> strings = new ArrayList<>();
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
                reportDataList.add(strings);
            });
        }
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/map-district")
    public void exportMAPDistrictExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        // Header text array — v5: Cadastral Maps has 6 leaf columns
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)", ReportLabels.MAP_DIGITIZATION_REPORT, ReportLabels.STATE_UT + " : " + stateName, "",
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.DISTRICT_NAME,
                ReportLabels.TOTAL_TEHSILS,
                ReportLabels.NO_OF_CADASTRAL_MAPS_FMBS_TIPPANS,
                ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS,
                ReportLabels.NUMBER_OF_VILLAGES,
                ReportLabels.NO_OF_LAND_PARCELS,
                ReportLabels.NUMBER_OF_DISTRICT_WHERE_CADMAP_FMB_TIPPAN_SHOWING_CURRENT_OWNERSHIP,
                ReportLabels.CADASTRAL_MAPS, ReportLabels.FMBs, ReportLabels.TIPPANS,
                ReportLabels.DIGITIZED, ReportLabels.GEO_REFERENCED,
                ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_LINKED_TO_ROR, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_GEO_REFERENCED,
                ReportLabels.ULPIN_ASSIGNED, ReportLabels.GEO_REFERENCED, ReportLabels.ULPIN_ASSIGNED, ReportLabels.YES_NO, ReportLabels.IF_NO_YEAR_UPTO_WHICH_ARE_UPDATED,
                ReportLabels.TOTAL,
                ReportLabels.TOTAL, ReportLabels.TOTAL
                , ReportLabels.TOTAL, ReportLabels.TOTAL_DAMAGED_MISSING_MAPS, ReportLabels.MAP_IN_GOOD_CONDITION, ReportLabels.DIGITIZED, ReportLabels.DIGITIZED_PERCENT_OF_TOTAL_CADASTRAL_MAPS, ReportLabels.DIGITIZED_PERCENT_OF_GOOD_CONDITION_MAPS,
                ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE,
                ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO,
                ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,

        };

        // D-O = 12 cadastral group; D-I = 6 cadastral maps
        String[] headerSpanMerged = {
                "A1:AH1", "A2:AH2", "A3:AH3", "A4:AH4", "A5:AH5", "A6:AH6",
                "A7:A9", "B7:B8", "C7:C9",
                "D7:O7", "P7:T7", "U7:AA7", "AB7:AF7", "AG7:AH7",
                "D8:I8", "J8:L8", "M8:O8",
                "Q8:R8", "S8:T8"
                , "V8:W8", "X8:Y8", "Z8:AA8",
                "AC8:AD8"
                , "AE8:AF8", "AG8:AG9", "AH8:AH9"
        };
        String[] headerSpanUnmerged = {"P8", "U8", "AB8", "D9", "E9", "F9", "G9", "H9", "I9", "J9", "K9", "L9", "M9", "N9", "O9", "P9", "Q9", "R9", "S9", "T9", "U9", "V9", "W9", "X9", "Y9", "Z9", "AA9", "AB9", "AC9", "AD9", "AE9", "AF9"};
        eComponent.setReportName(ReportLabels.MAP_DIGITIZATION_REPORT);
        eComponent.setNoOfColumns(34);
        eComponent.setNoOfheaderRows(9);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<DistrictMapDigitizationReport> districtMapList = (List<DistrictMapDigitizationReport>) session.getAttribute("reportData");
        List<MapDigitizationReport> grandTotal = (List<MapDigitizationReport>) session.getAttribute("stateMapData");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        if (districtMapList != null && !districtMapList.isEmpty()) {
            districtMapList.forEach(map -> {
                if (map.getStateId() != 999) {
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
                }
            });
        }

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

                reportDataList.add(stringList);
            });
        }

        eComponent.setReportDataList(reportDataList);

        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/mrr")
    public void exportMRRExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();

        // Header text array
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)", ReportLabels.MRR_REPORT, "",
                ReportLabels.SERIAL_NUMBER, ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS,
                ReportLabels.MRR_SANCTIONED, ReportLabels.MRR_COMPLETED_OUT_OF_TOTAL, ReportLabels.MRR_COMPLETED_OUT_OF_SANCTIONED
                , ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE};

        // Header spans for merged and unmerged cells
        String[] headerSpanMerged = {
                "A1:J1", "A2:J2", "A3:J3", "A4:J4", "A5:J5", "A6:A7", "B6:B7", "C6:C7", "D6:D7",
                "E6:F6", "G6:H6", "I6:J6"
        };
        String[] headerSpanUnmerged = {
                "E7", "F7", "G7", "H7", "I7", "J7"};
        eComponent.setReportName(ReportLabels.MRR_REPORT);
        eComponent.setNoOfColumns(10);
        eComponent.setNoOfheaderRows(7);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        // Get the CLR report data from session
        List<MrrViewReport> mrrList = (List<MrrViewReport>) session.getAttribute("mrrViewReportList");
        List<MrrViewReport> grandTotal = (List<MrrViewReport>) session.getAttribute("mrrGrandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        // Process clrList to create report data
        if (mrrList != null && !mrrList.isEmpty()) {
            mrrList.forEach(map -> {
                if (map.getStateId() != 999) {
                    List<String> strings = new ArrayList<>();
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

        // Add Grand Total to the data
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
                reportDataList.add(grandTotalList);
            });
        }

        // Set the combined data (state data + grand total) to the exporter component
        eComponent.setReportDataList(reportDataList);

        // Generate the Excel file
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/mrr-district")
    public void exportMRRDistrictExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)", ReportLabels.MRR_REPORT, ReportLabels.STATE_UT + " : " + stateName, "",
                ReportLabels.SERIAL_NUMBER, ReportLabels.DISTRICT_NAME, ReportLabels.TOTAL_TEHSILS,
                ReportLabels.MRR_SANCTIONED, ReportLabels.MRR_COMPLETED_OUT_OF_TOTAL, ReportLabels.MRR_COMPLETED_OUT_OF_SANCTIONED
                , ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE};
        String[] headerSpanMerged = {
                "A1:I1", "A2:I2", "A3:I3", "A4:I4", "A5:I5", "A6:I6",
                "A7:A8", "B7:B8", "C7:C8",
                "D7:E7", "F7:G7", "H7:I7"
        };
        String[] headerSpanUnmerged = {
                "D8", "E8", "F8", "G8", "H8", "I8"};
        eComponent.setReportName(ReportLabels.MRR_REPORT);
        eComponent.setNoOfColumns(9);
        eComponent.setNoOfheaderRows(8);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        // Get the CLR report data from session
        List<DistrictMrrViewReport> districtMrrList = (List<DistrictMrrViewReport>) session.getAttribute("districtMrrData");
        List<MrrViewReport> grandTotal = (List<MrrViewReport>) session.getAttribute("stateMrrData");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        // Process clrList to create report data
        if (districtMrrList != null && !districtMrrList.isEmpty()) {
            districtMrrList.forEach(map -> {
                if (map.getStateId() != 999) {
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
                }
            });
        }

        // Add Grand Total to the data
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
                reportDataList.add(grandTotalList);
            });
        }

        // Set the combined data (state data + grand total) to the exporter component
        eComponent.setReportDataList(reportDataList);

        // Generate the Excel file
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/survey")
    public void exportSURVEYExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();

        // Header text array
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)", "Survey / Re-Survey under NLRMP / DILRMP", "",
                ReportLabels.SERIAL_NUMBER, ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS,
                ReportLabels.TOTAL_TEHSILS, ReportLabels.TOTAL_VILLAGES, ReportLabels.TOTAL_RURAL_REVENUE_AREA_SQ_KM,
                ReportLabels.AREA_SANCTIONED_FOR_SURVEY_RE_SURVEY_SQ_KM,
                ReportLabels.DRON_FLYING_SURVEY, ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.AREA_OF_VILLAGES_WHERE_SURVEY_SANCTIONED_NOT_STARTED,
                ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.AREA_OF_VILLAGES_SQ_KM,
                ReportLabels.MAP_1_IS_GENERATED, ReportLabels.DRAFT_MAP_PUBLISHED,
                ReportLabels.FINAL_PROMULGATION_DONE, ReportLabels.SURVEY_SANCTIONED_NOT_STARTED
        };

        // Header spans for merged and unmerged cells
        String[] headerSpanMerged = {
                "A1:N1", "A2:N2", "A3:N3", "A4:N4", "A5:N5",
                "A6:A7", "B6:B7", "C6:C7", "D6:D7", "E6:E7", "F6:F7", "G6:G7",
                "H6:I6", "J6:M6", "N6:N7"
        };
        String[] headerSpanUnmerged = {"H7", "I7", "J7", "K7", "L7", "M7"};
        eComponent.setReportName(ReportLabels.SURVEY_REPORT_NMAE);
        eComponent.setNoOfColumns(14);
        eComponent.setNoOfheaderRows(7);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        // Get the CLR report data from session
        List<SurveyResurveyViewReport> surveyList = (List<SurveyResurveyViewReport>) session.getAttribute("surveyResurveyViewReportList");
        List<SurveyResurveyViewReport> grandTotal = (List<SurveyResurveyViewReport>) session.getAttribute("surveyResurveyGrandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        // Process clrList to create report data
        if (surveyList != null && !surveyList.isEmpty()) {
            surveyList.forEach(map -> {
                if (map.getStateId() != 999) {
                    List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getStateName() != null ? map.getStateName() : "N/A");
                    strings.add(map.getFormattingTotalDistrict() != null ? map.getFormattingTotalDistrict() : "0");
                    strings.add(map.getFormattingTotalTehsils() != null ? map.getFormattingTotalTehsils() : "0");
                    strings.add(map.getFormattingTotalVillages() != null ? map.getFormattingTotalVillages() : "0");
                    strings.add(map.getFormatetotalRuralRevenueArea() != null ? map.getFormatetotalRuralRevenueArea() : "0.00");
                    strings.add(map.getFomateareaSanctionedForSurvey() != null ? map.getFomateareaSanctionedForSurvey() : "0.00");
                    strings.add(map.getFormattingvillagesDroneFlyingCompleted() != null ? map.getFormattingvillagesDroneFlyingCompleted() : "0");
                    strings.add(map.getFormateareaDroneFlyingCompleted() != null ? map.getFormateareaDroneFlyingCompleted() : "0.00");
                    strings.add(map.getFormattingvillagesMap1Generated() != null ? map.getFormattingvillagesMap1Generated() : "0");
                    strings.add(map.getFormattingvillagesDraftMapPublished() != null ? map.getFormattingvillagesDraftMapPublished() : "0");
                    strings.add(map.getFormattingvillagesFinalPromulgationDone() != null ? map.getFormattingvillagesFinalPromulgationDone() : "0");
                    strings.add(map.getFormattingvillagesSurveySanctionNotStarted() != null ? map.getFormattingvillagesSurveySanctionNotStarted() : "0");
                    strings.add(map.getFormateareaSurveySanctionNotStarted() != null ? map.getFormateareaSurveySanctionNotStarted() : "0.00");
                    reportDataList.add(strings);
                }
            });
        }
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add(gt.getStateName() != null ? gt.getStateName() : "Grand Total");
                grandTotalList.add(gt.getFormattingTotalDistrict() != null ? gt.getFormattingTotalDistrict().toString() : "N/A");
                grandTotalList.add(gt.getFormattingTotalTehsils() != null ? gt.getFormattingTotalTehsils().toString() : "0");
                grandTotalList.add(gt.getFormattingTotalVillages() != null ? gt.getFormattingTotalVillages().toString() : "0");
                grandTotalList.add(gt.getFormatetotalRuralRevenueArea() != null ? gt.getFormatetotalRuralRevenueArea() : "0.00");
                grandTotalList.add(gt.getFomateareaSanctionedForSurvey() != null ? gt.getFomateareaSanctionedForSurvey() : "0.00");
                grandTotalList.add(gt.getFormattingvillagesDroneFlyingCompleted() != null ? gt.getFormattingvillagesDroneFlyingCompleted() : "0");
                grandTotalList.add(gt.getFormateareaDroneFlyingCompleted() != null ? gt.getFormateareaDroneFlyingCompleted() : "0.00");
                grandTotalList.add(gt.getFormattingvillagesMap1Generated() != null ? gt.getFormattingvillagesMap1Generated() : "0");
                grandTotalList.add(gt.getFormattingvillagesDraftMapPublished() != null ? gt.getFormattingvillagesDraftMapPublished() : "0");
                grandTotalList.add(gt.getFormattingvillagesFinalPromulgationDone() != null ? gt.getFormattingvillagesFinalPromulgationDone() : "0");
                grandTotalList.add(gt.getFormattingvillagesSurveySanctionNotStarted() != null ? gt.getFormattingvillagesSurveySanctionNotStarted() : "0");
                grandTotalList.add(gt.getFormateareaSurveySanctionNotStarted() != null ? gt.getFormateareaSurveySanctionNotStarted() : "0.00");
                reportDataList.add(grandTotalList);
            });
        }
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/survey-district")
    public void exportSURVEYDistrictExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)", "Survey / Re-Survey under NLRMP / DILRMP", ReportLabels.STATE_UT + " : " + stateName, "",
                ReportLabels.SERIAL_NUMBER, ReportLabels.DISTRICT_NAME,
                ReportLabels.TOTAL_TEHSILS, ReportLabels.TOTAL_VILLAGES, ReportLabels.TOTAL_RURAL_REVENUE_AREA_SQ_KM,
                ReportLabels.AREA_SANCTIONED_FOR_SURVEY_RE_SURVEY_SQ_KM,
                ReportLabels.DRON_FLYING_SURVEY, ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.AREA_OF_VILLAGES_WHERE_SURVEY_SANCTIONED_NOT_STARTED,
                ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.AREA_OF_VILLAGES_SQ_KM,
                ReportLabels.MAP_1_IS_GENERATED, ReportLabels.DRAFT_MAP_PUBLISHED,
                ReportLabels.FINAL_PROMULGATION_DONE, ReportLabels.SURVEY_SANCTIONED_NOT_STARTED
        };
        String[] headerSpanMerged = {
                "A1:M1", "A2:M2", "A3:M3", "A4:M4", "A5:M5", "A6:M6",
                "A7:A8", "B7:B8", "C7:C8", "D7:D8", "E7:E8", "F7:F8",
                "G7:H7", "I7:L7", "M7:M8"
        };
        String[] headerSpanUnmerged = {"G8", "H8", "I8", "J8", "K8", "L8"};
        eComponent.setReportName(ReportLabels.SURVEY_REPORT_NMAE);
        eComponent.setNoOfColumns(13);
        eComponent.setNoOfheaderRows(8);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        // Get the CLR report data from session
        List<DistrictSurveyResurveyViewReport> districtSurveyList = (List<DistrictSurveyResurveyViewReport>) session.getAttribute("districtSurveyData");
        List<SurveyResurveyViewReport> grandTotal = (List<SurveyResurveyViewReport>) session.getAttribute("stateSurveyData");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        // Process clrList to create report data
        if (districtSurveyList != null && !districtSurveyList.isEmpty()) {
            districtSurveyList.forEach(map -> {
                if (map.getStateId() != 999) {
                    List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getDistrictName() != null ? map.getDistrictName().toUpperCase() : "N/A");
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
                }
            });
        }

        // Add Grand Total to the data
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
                reportDataList.add(grandTotalList);
            });
        }

        // Set the combined data (state data + grand total) to the exporter component
        eComponent.setReportDataList(reportDataList);

        // Generate the Excel file
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/sro")
    public void exportSROExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();

        // Header text array
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)", ReportLabels.SRO_REPORT, "",
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

        // Header spans for merged and unmerged cells
        String[] headerSpanMerged = {
                "A1:N1", "A2:N2", "A3:N3", "A4:N4", "A5:N5",
                "A6:A7", "B6:B7", "C6:C7", "D6:D7", "E6:E7", "F6:F7", "G6:G7", "H6:H7", "I6:I7", "J6:J7", "K6:K7", "L6:L7", "M6:M7", "N6:N7", "O6:O7", "P6:P7", "Q6:Q7", "R6:R7", "S6:S7", "T6:T7", "U6:U7", "V6:V7", "W6:W7", "X6:X7", "Y6:Y7", "Z6:Z7", "AA6:AA7"

        };
        String[] headerSpanUnmerged = {};
        eComponent.setReportName(ReportLabels.SRO_REPORT);
        eComponent.setNoOfColumns(27);
        eComponent.setNoOfheaderRows(7);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        // Get the CLR report data from session
        List<SroReportDTO> sroList = (List<SroReportDTO>) session.getAttribute("sroStateList");
        List<SroReportDTO> grandTotal = (List<SroReportDTO>) session.getAttribute("sroGrandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        // Process clrList to create report data
        if (sroList != null && !sroList.isEmpty()) {
            sroList.forEach(map -> {
                if (map.getStateId() != 999) {
                    List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getStateName() != null ? map.getStateName() : "N/A");
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

        // Add Grand Total to the data
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
                reportDataList.add(grandTotalList);
            });
        }

        // Set the combined data (state data + grand total) to the exporter component
        eComponent.setReportDataList(reportDataList);

        // Generate the Excel file
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/rcms")
    public void exportRCMSExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();

        // Header text array
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                ReportLabels.RCMS_REPORT, "",
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.STATE_UT,
                ReportLabels.TOTAL_DISTRICTS,
                ReportLabels.TOTAL_TEHSILS,
                ReportLabels.NUMBER_OF_REVENUE_COURTS,
                ReportLabels.E_REVENUE_COURT_MANAGEMENT_SYSTEM + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.ONLINE_PROCESS_OF_REVENUE_COURT_PROCESSES_AVAILABLE,
                ReportLabels.WHETHER_PROCEEDS + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.WHETHER_LAW_RECORD_ONLINE_COURT + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.WHETHER_FILING_DIRECTLY_SYSTEM + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.COMPUTERIZED_ONLINE, ReportLabels.ATTACHED_DOCUMENTS + " (" + ReportLabels.YES_NO + ")", ReportLabels.CAUSE_LIST_GENERATION + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.DEFENDANT_NOTIFICATION + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.COUNTER_AFFIDAVIT_FILING + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.PROCEEDINGS_TYPING_SYSTEM + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.UPLOADING_COURT_ORDERS_NOTIFICATION + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.LAND_RECORD_INTEGRATION + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.ONLINE_LAND_RECORD_CHECK + " (" + ReportLabels.YES_NO + ")", ReportLabels.TOTAL,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE

        };

        // Header spans for merged and unmerged cells
        String[] headerSpanMerged = {
                "A1:X1", "A2:X2", "A3:X3", "A4:X4", "A5:X5",
                "A6:A8", "B6:B8", "C6:C8", "D6:D8",
                "E6:G6", "H6:H8", "I6:P6", "Q6:Q8", "R6:R8", "S6:S8", "F7:G7", "I7:I8", "J7:J8", "K7:K8", "L7:L8", "M7:M8", "N7:N8", "O7:O8", "P7:P8"


        };

        String[] headerSpanUnmerged = {"E7", "E8", "F8", "G8"};
        eComponent.setReportName(ReportLabels.RCMS_REPORT);
        eComponent.setNoOfColumns(20);
        eComponent.setNoOfheaderRows(9);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<RcmsReportDTO> rcmsList = (List<RcmsReportDTO>) session.getAttribute("rcmsReportList");
        List<RcmsReportDTO> grandTotal = (List<RcmsReportDTO>) session.getAttribute("rcmsGrandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        if (rcmsList != null && !rcmsList.isEmpty()) {
            rcmsList.forEach(map -> {
                if (map.getStateId() != 999) {
                    List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getStateName() != null ? map.getStateName() : "N/A");
                    strings.add(String.valueOf(map.getTotalDistrict() != null ? map.getTotalDistrict() : "N/A"));
                    strings.add(String.valueOf(map.getTotalTehsils() != null ? map.getTotalTehsils() : "0"));
                    strings.add(String.valueOf(map.getTotalRevenueCourts() != null ? map.getTotalRevenueCourts() : "0"));
                    strings.add(String.valueOf(map.getRevenueCourtsComputerized() != null ? map.getRevenueCourtsComputerized() : "N/A"));
                    strings.add(String.valueOf(map.getRevenueCourtsComputerizedPercent() != null ? map.getRevenueCourtsComputerizedPercent() : "N/A"));
                    strings.add(map.getERcmsAvailable() != null ? map.getERcmsAvailable() : "NO");
                    strings.add(map.getAffidavitFilingOnline() != null ? map.getAffidavitFilingOnline() : "NO");
                    strings.add(map.getCauseListGenerationOnline() != null ? map.getCauseListGenerationOnline() : "NO");
                    strings.add(map.getNoticeToDefendantsOnline() != null ? map.getNoticeToDefendantsOnline() : "NO");
                    strings.add(map.getCounterAffidavitFilingOnline() != null ? map.getCounterAffidavitFilingOnline() : "NO");
                    strings.add(map.getProceedingsTypingDirectlyOnline() != null ? map.getProceedingsTypingDirectlyOnline() : "NO");
                    strings.add(map.getUploadingRevenueCourtOrdersOnline() != null ? map.getUploadingRevenueCourtOrdersOnline() : "NO");
                    strings.add(map.getLandRecordsOnlineFromRevenueCourtSystem() != null ? map.getLandRecordsOnlineFromRevenueCourtSystem() : "NO");
                    strings.add(map.getRevenueCourtProceedingsPaperless() != null ? map.getRevenueCourtProceedingsPaperless() : "NO");
                    strings.add(map.getLandRecordsOnlineForCivilCourts() != null ? map.getLandRecordsOnlineForCivilCourts() : "NO");
                    strings.add(map.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() != null ? map.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() : "NO");
                    strings.add(map.getCaseFilingRedFlaggedInLandRecords() != null ? map.getCaseFilingRedFlaggedInLandRecords() : "NO");
                    reportDataList.add(strings);
                }
            });
        }
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
                reportDataList.add(grandTotalList);
            });
        }
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/rcms-district")
    public void exportRCMSDistrictExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                ReportLabels.RCMS_REPORT, ReportLabels.STATE_UT + " : " + stateName, "",
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.DISTRICT_NAME,
                ReportLabels.TOTAL_TEHSILS,
                ReportLabels.NUMBER_OF_REVENUE_COURTS,
                ReportLabels.E_REVENUE_COURT_MANAGEMENT_SYSTEM + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.ONLINE_PROCESS_OF_REVENUE_COURT_PROCESSES_AVAILABLE,
                ReportLabels.WHETHER_PROCEEDS + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.WHETHER_LAW_RECORD_ONLINE_COURT + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.WHETHER_FILING_DIRECTLY_SYSTEM + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.COMPUTERIZED_ONLINE, ReportLabels.ATTACHED_DOCUMENTS + " (" + ReportLabels.YES_NO + ")", ReportLabels.CAUSE_LIST_GENERATION + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.DEFENDANT_NOTIFICATION + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.COUNTER_AFFIDAVIT_FILING + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.PROCEEDINGS_TYPING_SYSTEM + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.UPLOADING_COURT_ORDERS_NOTIFICATION + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.LAND_RECORD_INTEGRATION + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.ONLINE_LAND_RECORD_CHECK + " (" + ReportLabels.YES_NO + ")", ReportLabels.TOTAL,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE

        };
        String[] headerSpanMerged = {
                "A1:R1", "A2:R2", "A3:R3", "A4:R4", "A5:R5", "A6:R6",
                "A7:A9", "B7:B9", "C7:C9", "D7:F7", "G7:G9", "H7:O7", "P7:P9", "Q7:Q9", "R7:R9", "E8:F8", "H8:H9", "I8:I9", "J8:J9", "K8:K9", "L8:L9", "M8:M9", "N8:N9", "O8:O9"
        };
        String[] headerSpanUnmerged = {"D8", "D9", "E9", "F9"};
        eComponent.setReportName(ReportLabels.RCMS_REPORT);
        eComponent.setNoOfColumns(18);
        eComponent.setNoOfheaderRows(9);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<DistrictRcmsReportDTO> districtRcmsList = (List<DistrictRcmsReportDTO>) session.getAttribute("districtRcmsData");
        List<RcmsReportDTO> grandTotal = (List<RcmsReportDTO>) session.getAttribute("stateRcmsData");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        if (districtRcmsList != null && !districtRcmsList.isEmpty()) {
            districtRcmsList.forEach(map -> {
                if (map.getStateId() != 999) {
                    List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getDistrictName() != null ? map.getDistrictName().toUpperCase() : "N/A");
                    strings.add(String.valueOf(map.getTotalTehsils() != null ? map.getTotalTehsils() : "0"));
                    strings.add(String.valueOf(map.getTotalRevenueCourts() != null ? map.getTotalRevenueCourts() : "0"));
                    strings.add(String.valueOf(map.getRevenueCourtsComputerized() != null ? map.getRevenueCourtsComputerized() : "N/A"));
                    strings.add(String.valueOf(map.getRevenueCourtsComputerizedPercent() != null ? map.getRevenueCourtsComputerizedPercent() : "N/A"));
                    strings.add(map.getERcmsAvailable() != null ? map.getERcmsAvailable() : "NO");
                    strings.add(map.getAffidavitFilingOnline() != null ? map.getAffidavitFilingOnline() : "NO");
                    strings.add(map.getCauseListGenerationOnline() != null ? map.getCauseListGenerationOnline() : "NO");
                    strings.add(map.getNoticeToDefendantsOnline() != null ? map.getNoticeToDefendantsOnline() : "NO");
                    strings.add(map.getCounterAffidavitFilingOnline() != null ? map.getCounterAffidavitFilingOnline() : "NO");
                    strings.add(map.getProceedingsTypingDirectlyOnline() != null ? map.getProceedingsTypingDirectlyOnline() : "NO");
                    strings.add(map.getUploadingRevenueCourtOrdersOnline() != null ? map.getUploadingRevenueCourtOrdersOnline() : "NO");
                    strings.add(map.getLandRecordsOnlineFromRevenueCourtSystem() != null ? map.getLandRecordsOnlineFromRevenueCourtSystem() : "NO");
                    strings.add(map.getRevenueCourtProceedingsPaperless() != null ? map.getRevenueCourtProceedingsPaperless() : "NO");
                    strings.add(map.getLandRecordsOnlineForCivilCourts() != null ? map.getLandRecordsOnlineForCivilCourts() : "NO");
                    strings.add(map.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() != null ? map.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() : "NO");
                    strings.add(map.getCaseFilingRedFlaggedInLandRecords() != null ? map.getCaseFilingRedFlaggedInLandRecords() : "NO");
                    reportDataList.add(strings);
                }
            });
        }
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
                reportDataList.add(grandTotalList);
            });
        }
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/aadhar")
    public void exportAadharExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();

        // Header text array //v5: + RoR Address + Land owners (16->23 cols A-W)
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                ReportLabels.AADHAR_REPORT, "",
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS,
                ReportLabels.TOTAL_TEHSILS, ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.RoR, ReportLabels.NUMBER_OF_LAND_OWNERS,
                ReportLabels.WHERE_AT_LEAST_ONE_RoR_LINKED_WITH_AADHAAR,
                ReportLabels.WHERE_100_PERCENT_RoR_LINKED_WITH_AADHAAR,
                ReportLabels.LINKED_WITH_AADHAAR, ReportLabels.LINKED_WITH_MOBILE_NUMBER, ReportLabels.LINKED_WITH_ADDRESS,
                ReportLabels.LINKED_WITH_AADHAAR, ReportLabels.LINKED_WITH_MOBILE_NUMBER, ReportLabels.LINKED_WITH_ADDRESS,
                ReportLabels.TOTAL, ReportLabels.TOTAL, ReportLabels.TOTAL,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.PERCENTAGE};

        // Header spans //v5: villages E-I, RoR J-P, Land owners Q-W
        String[] headerSpanMerged = {
                "A1:W1", "A2:W2", "A3:W3", "A4:W4", "A5:W5",
                "A6:A8", "B6:B8", "C6:C8", "D6:D8",
                "E6:I6", "J6:P6", "Q6:W6",
                "F7:G7", "H7:I7", "K7:L7", "M7:N7", "O7:P7",
                "R7:S7", "T7:U7", "V7:W7"
        };
        eComponent.setHeaderSpanUnMerged(new String[]{});
        String[] headerSpanUnmerged = {"E7", "J7", "Q7",
                "E8", "F8", "G8", "H8", "I8", "J8", "K8", "L8", "M8", "N8", "O8", "P8",
                "Q8", "R8", "S8", "T8", "U8", "V8", "W8"};
        eComponent.setReportName(ReportLabels.AADHAR_REPORT);
        eComponent.setNoOfColumns(23);
        eComponent.setNoOfheaderRows(8);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<LinkedAadharViewReport> aadharList = (List<LinkedAadharViewReport>) session.getAttribute("linkedAadharViewReportList");
        List<LinkedAadharViewReport> grandTotal = (List<LinkedAadharViewReport>) session.getAttribute("linkedAadharGrandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        if (aadharList != null && !aadharList.isEmpty()) {
            aadharList.forEach(map -> {
                if (map.getStateId() != 999) {
                    List<String> strings = new ArrayList<>();
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
                reportDataList.add(grandTotalList);
            });
        }

        eComponent.setReportDataList(reportDataList);

        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/aadhar-district")
    public void exportAadharDistrictExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        //v5: + RoR Address + Land owners (15->22 cols A-V)
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                ReportLabels.AADHAR_REPORT, ReportLabels.STATE_UT + " : " + stateName, "",
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.DISTRICT_NAME,
                ReportLabels.TOTAL_TEHSILS,
                ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.RoR, ReportLabels.NUMBER_OF_LAND_OWNERS,
                ReportLabels.WHERE_AT_LEAST_ONE_RoR_LINKED_WITH_AADHAAR,
                ReportLabels.WHERE_100_PERCENT_RoR_LINKED_WITH_AADHAAR,
                ReportLabels.LINKED_WITH_AADHAAR, ReportLabels.LINKED_WITH_MOBILE_NUMBER, ReportLabels.LINKED_WITH_ADDRESS,
                ReportLabels.LINKED_WITH_AADHAAR, ReportLabels.LINKED_WITH_MOBILE_NUMBER, ReportLabels.LINKED_WITH_ADDRESS,
                ReportLabels.TOTAL, ReportLabels.TOTAL, ReportLabels.TOTAL,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,
                ReportLabels.NO, ReportLabels.PERCENTAGE
        };
        String[] headerSpanMerged = {
                "A1:V1", "A2:V2", "A3:V3", "A4:V4", "A5:V5", "A6:V6",
                "A7:A9", "B7:B9", "C7:C9",
                "D7:H7", "I7:O7", "P7:V7",
                "E8:F8", "G8:H8", "J8:K8", "L8:M8", "N8:O8",
                "Q8:R8", "S8:T8", "U8:V8"
        };
        String[] headerSpanUnmerged = {"D8", "I8", "P8",
                "D9", "E9", "F9", "G9", "H9", "I9", "J9", "K9", "L9", "M9", "N9", "O9",
                "P9", "Q9", "R9", "S9", "T9", "U9", "V9"};
        eComponent.setReportName(ReportLabels.AADHAR_REPORT);
        eComponent.setNoOfColumns(22);
        eComponent.setNoOfheaderRows(9);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<DistrictLinkedAadhaarViewReport> districtAadhaarList = (List<DistrictLinkedAadhaarViewReport>) session.getAttribute("districtAadhaarData");
        List<LinkedAadharViewReport> grandTotal = (List<LinkedAadharViewReport>) session.getAttribute("stateAadhaarData");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        if (districtAadhaarList != null && !districtAadhaarList.isEmpty()) {
            districtAadhaarList.forEach(map -> {
                if (map.getStateId() != 999) {
                    List<String> strings = new ArrayList<>();
                    strings.add(Integer.toString(indexHolder.incrementAndGet()));
                    strings.add(map.getDistrictName() != null ? map.getDistrictName().toUpperCase() : "N/A");
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
                }
            });
        }
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
                reportDataList.add(grandTotalList);
            });
        }
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/naksha")
    public void exportNakshaExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)", "", "Naksha For State/UT (before publication of Map1) Report",
                "S.No",
                "State/UT",
                "Nodal Department Name",
                "Nodal office appointed (Yes/No)",
                "Nodal Officer Name",
                "Phone No. of Nodal Officer",
                "Email of Nodal Officer",
                "Total No. of SPMU Positions Sanctioned",
                "SPMU Recruitment Completed (Yes / No / Initiated / Under Process / Pending for Approval)",
                "Total No. of Professionals Recruited for SPMU",
                "No. of Teams to be Formed as Sanctioned",
                "No. of Teams Formed for Field Survey",
                "No. of Rovers Sanctioned",
                "No. of Rovers Procured for Field Survey",
                "Legal Framework for Urban Survey (Exists/ To be Ammended)",
                "Property Tax Data Obtained for all ULBs (Yes/No)",
                "Property Tax Data Digitized for all ULBs (Yes/No)",
                "SLC Status (Initiated / Under Process/ Pending for Approval)",
                "Ground Truthing",
                "Data uploaded on NAKSHA Web GIS Portal (Yes/ No)",
                "Total No. of UrPro Card Issued",
                "IEC Activities  awareness, documentation and monitoring plans. (Remarks)",
                "Last Updated Date",
                "Name of Survey Unit (Ward, Block, Colony,Sector, Revenue Village,Tile, Others) ",
                "No. of ULBs where Ground Truthing has been Completed",
                "Total No. of Plots to be Surveyed ",
                "No. of Plots Survey Completed",
                "Percentage Completed"
        };
        String[] headerSpanMerged = {
                "A1:AA1", "A2:AA2", "A3:AA3", "A4:AA4", "A5:AA5",
                "A6:A7", "B6:B7", "C6:C7", "D6:D7", "E6:E7", "F6:F7", "G6:G7", "H6:H7", "I6:I7", "J6:J7",
                "K6:K7", "L6:L7", "M6:M7", "N6:N7", "O6:O7", "P6:P7", "Q6:Q7", "R6:R7", "S6:W6",
                "X6:X7","Y6:Y7","Z6:Z7","AA6:AA7"};
        String[] headerSpanUnmerged = {"S7", "T7", "U7", "V7", "W7"};
        eComponent.setReportName("Naksha_Report");
        eComponent.setNoOfColumns(27);
        eComponent.setNoOfheaderRows(7);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<NakshaMISDataEntry> list = (List<NakshaMISDataEntry>) session.getAttribute("nakshaMISDataEntries");
        List<NakshaMISDataEntry> grandTotal = (List<NakshaMISDataEntry>) session.getAttribute("grandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        list.forEach(gList -> {
            List<String> strings = new ArrayList<>();
            strings.add(Integer.toString(indexHolder.incrementAndGet()));
            strings.add(gList.getState().getName() != null ? gList.getState().getName() : "N/A");
            strings.add(gList.getNodalDepartmentName() != null ? gList.getNodalDepartmentName() : "N/A");
            strings.add("Yes".equalsIgnoreCase(gList.getNodalOfficerAppointed()) ? "Yes" : "No");
            strings.add(gList.getNodalOfficerName() != null ? gList.getNodalOfficerName() : "N/A");
            strings.add(gList.getPhonenumber() != null ? gList.getPhonenumber() : "N/A");
            strings.add(gList.getEmail() != null ? gList.getEmail() : "N/A");
            //strings.add(gList.getContectDetails() != null ? gList.getContectDetails() : "N/A");
            strings.add(gList.getTotalSPMUPositionsSanctioned() != null ? String.valueOf(gList.getTotalSPMUPositionsSanctioned()) : "0");
            strings.add("Yes".equalsIgnoreCase(gList.getSpmuRecruitmentCompleted()) ? "Yes" : "No");
            strings.add(gList.getTotalProfessionalsRecruited() != null ? String.valueOf(gList.getTotalProfessionalsRecruited()) : "0");
            strings.add(gList.getTeamsFormedForsanctioned() != null ? String.valueOf(gList.getTeamsFormedForsanctioned()) : "0");
            strings.add(gList.getTeamsFormedForFieldSurvey() != null ? String.valueOf(gList.getTeamsFormedForFieldSurvey()) : "0");
            strings.add(gList.getRoversSanctioned() != null ? String.valueOf(gList.getRoversSanctioned()) : "0");
            strings.add(gList.getRoversProcuredForFieldSurvey() != null ? String.valueOf(gList.getRoversProcuredForFieldSurvey()) : "0");
            strings.add(gList.getLegal_framework_urban_survey() != null ? gList.getLegal_framework_urban_survey() : "N/A");
            strings.add("Yes".equalsIgnoreCase(gList.getPropertyTaxDataObtained()) ? "Yes" : "No");
            strings.add("Yes".equalsIgnoreCase(gList.getPropertyTaxDataDigitized()) ? "Yes" : "No");
            strings.add(gList.getSlcStatus() != null ? gList.getSlcStatus() : "N/A");
            strings.add(gList.getSurveyUnitName() != null ? gList.getSurveyUnitName() : "N/A");
            strings.add(gList.getUlbsGroundTruthingCompleted() != null ? String.valueOf(gList.getUlbsGroundTruthingCompleted()) : "0");
            strings.add(gList.getTotalPlotsToSurvey() != null ? String.valueOf(gList.getTotalPlotsToSurvey()) : "0");
            strings.add(gList.getPlotsSurveyedCompleted() != null ? String.valueOf(gList.getPlotsSurveyedCompleted()) : "0");
            strings.add(gList.getSurveyCompletionPercent() != null ? String.valueOf(gList.getSurveyCompletionPercent()) : "0.00");
            strings.add(gList.getDataUploadedOnNakshaWebGISPortal() != null ? gList.getDataUploadedOnNakshaWebGISPortal() : "N/A");
            strings.add(gList.getTotalUrProCardIssued() != null ? String.valueOf(gList.getTotalUrProCardIssued()) : "0");
            strings.add(gList.getIecActivitiesRemarks() != null ? gList.getIecActivitiesRemarks() : "N/A");
            strings.add(gList.getUpdateOnDate() != null ? new SimpleDateFormat("MMMM dd, yyyy").format(gList.getUpdateOnDate()) : "NA");
            reportDataList.add(strings);
        });
        List<String> grandTotalRow = new ArrayList<>();
        grandTotalRow.add("");
        grandTotalRow.add("Grand Total");
        grandTotalRow.add("");
        grandTotalRow.add("");
        grandTotalRow.add("");
        grandTotalRow.add("");
        grandTotalRow.add("");
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandTotalSPMUPositionsSanctioned()));
        grandTotalRow.add("");
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandtotalProfessionalsRecruited()));
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandtotalteamsFormedForsanctioned()));
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandtotalteamsFormedForFieldSurvey()));
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandtotalroversSanctioned()));
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandtotalroversProcuredForFieldSurvey()));
        grandTotalRow.add("");
        grandTotalRow.add("");
        grandTotalRow.add("");
        grandTotalRow.add("");
        grandTotalRow.add("");
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandtotalulbsGroundTruthingCompleted()));
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandtotaltotalPlotsToSurvey()));
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandtotalplotsSurveyedCompleted()));
        grandTotalRow.add("");
        grandTotalRow.add("");
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandtotalUrProCardIssued()));
        grandTotalRow.add("");
        grandTotalRow.add("");
        reportDataList.add(grandTotalRow);
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/capacity-building")
    public void getCapacitybuildingExcel(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)", "", "Naksha for State/UT Capacity Building Report",
                "S.No",
                "State/UT",
                "Name of CoE",
                "A) NIGST master trainers",
                "B) CoE master trainers",
                "C) Total master trainers",
                "D) No. of field teams (Sanctioned by DoLR)",
                "E) No. of  members need to be trained to form field teams (Field teams x 3)",
                "F) Total no. of field team members trained till date.",
                "G)  Total No. of Field Teams Trained Formula :(Column F/3)",
                "H) Percentage of Field Team trained till date (in %)  (Column G/ D) *100"
        };
        String[] headerSpanMerged = {
                "A1:K1", "A2:K2", "A3:K3", "A4:K4", "A5:K5",
                "A6:A7", "B6:B7", "C6:C7", "D6:D7", "E6:E7", "F6:F7", "G6:G7", "H6:H7", "I6:I7", "J6:J7",
                "K6:K7"};
        String[] headerSpanUnmerged = {};
        eComponent.setReportName("CapacityBuildingReport");
        eComponent.setNoOfColumns(11);
        eComponent.setNoOfheaderRows(7);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<CapacityBuilding> list = (List<CapacityBuilding>) session.getAttribute("capacityBuilding");
        List<CapacityBuilding> grandTotal = (List<CapacityBuilding>) session.getAttribute("grandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        list.forEach(gList -> {
            List<String> strings = new ArrayList<>();
            strings.add(Integer.toString(indexHolder.incrementAndGet()));
            strings.add(gList.getState().getName() != null ? gList.getState().getName() : "N/A");
            strings.add(gList.getNameOfCoE() != null ? gList.getNameOfCoE() : "N/A");
            strings.add(gList.getNigstMasterTrainers() != null ? String.valueOf(gList.getNigstMasterTrainers()) : "0");
            strings.add(gList.getCoeMasterTrainers() != null ? String.valueOf(gList.getCoeMasterTrainers()) : "0");
            strings.add(gList.getTotalMasterTrainers() != null ? String.valueOf(gList.getTotalMasterTrainers()) : "0");
            strings.add(gList.getFieldTeamsSanctioned() != null ? String.valueOf(gList.getFieldTeamsSanctioned()) : "0");
            strings.add(gList.getMembersTrained() != null ? String.valueOf(gList.getMembersTrained()) : "0");
            strings.add(gList.getMembersToBeTrained() != null ? String.valueOf(gList.getMembersToBeTrained()) : "0");
            strings.add(gList.getFormatedfieldTeamsTrained() != null ? gList.getFormatedfieldTeamsTrained() : "0.0");
            strings.add(gList.getFormatedpercentageTrained() != null ? gList.getFormatedpercentageTrained() : "0.0");
            reportDataList.add(strings);
        });
        List<String> grandTotalRow = new ArrayList<>();
        grandTotalRow.add("");
        grandTotalRow.add("Grand Total");
        grandTotalRow.add("");
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandTotalNigstMasterTrainers()));
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandTotalCoeMasterTrainers()));
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandTotalTotalMasterTrainers()));
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandTotalFieldTeamsSanctioned()));
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandTotalMembersTrained()));
        grandTotalRow.add(String.valueOf(grandTotal.get(0).getGrandTotalMembersToBeTrained()));
        grandTotalRow.add("");
        grandTotalRow.add("");
        reportDataList.add(grandTotalRow);
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/naksha-vender")
    public void exportNakshaVenderExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();

        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                "",
                "Format 2: Naksha For SoI & Vendor (MAP 1) Report",
                "",
                "S.No",
                "Name of the Geospatial Directorate",
                "Email of the Geospatial Directorate",
                "Zone",
                "State",
                "District Name",
                "Name of ULB/Town",
                "Third Party Agency",
                "Technology",
                "GD/Wing",
                "Package",
                "DoLR Sanctioned Area",
                "Buffer Area by states for Data Acquisition (Area in sq.km.)",
                "Last Updated Time/ Date by GD",
                "Grid Completion",
                "Aerial Data Acquisition",
                "Total No. of 1:2k Grid",
                "Grids Completed Last Week",
                "Total Grids Completed",

                "Cumulative Completed (Area in sq.km. / Completed Percentage / Completion Status: Completed / Under Process / Not Started)"
                ,"Remarks",
                "Tech 1 Nadir (Area in sq.km.)",
                "Tech 1 Nadir (%)",
                "Completion Status",

                "Tech 2 Oblique (Area in sq.km.)",
                "Tech 2 Oblique (%)",
                "Completion Status",

                "Tech 3 Oblique  (Area in sq.km.)",
                "Tech 3 Oblique (%)",
                "Completion Status",

                "Tech 3 Lidar Sensor (Area in sq.km)",
                "Tech 3  Lidar Sensor (%)",
                "Completion Status"
        };
        String[] headerSpanMerged = {
                "A1:AD1", "A2:AD2", "A3:AD3", "A4:AD4", "A5:AD5", "A6:AD6",
                "A7:A9", "B7:B9", "C7:C9", "D7:D9", "E7:E9", "F7:F9", "G7:G9", "H7:H9", "I7:I9", "J7:J9", "K7:K9", "L7:L9", "M7:M9", "N7:N9",
                "O7:Q7", "R7:AD7", "O8:O9", "P8:P9", "Q8:Q9", "R8:AC8","AD8:AD9"};
        String[] headerSpanUnmerged = {"R9", "S9", "T9", "U9", "V9", "W9","X9","Y9","Z9","AA9","AB9","AC9"};
        eComponent.setReportName("NakshaVendorReport");
        eComponent.setNoOfColumns(30);
        eComponent.setNoOfheaderRows(9);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<NakshaMISDataEntrySolrAndVender> list = (List<NakshaMISDataEntrySolrAndVender>) session.getAttribute("nakshaMISDataEntries");
        List<NakshaMISDataEntrySolrAndVender> grandTotal = (List<NakshaMISDataEntrySolrAndVender>) session.getAttribute("grandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        list.forEach(entry -> {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(indexHolder.incrementAndGet()));
            row.add(entry.getOfficerName() != null ? entry.getOfficerName() : "NA");
            row.add(entry.getGdEmail() != null ? entry.getGdEmail() : "NA");
            row.add(entry.getZoneName() != null ? entry.getZoneName() : "NA");
            row.add(entry.getStateName() != null ? entry.getStateName() : "NA");
            row.add(entry.getDistrict_Name() != null ? entry.getDistrict_Name() : "NA");
            row.add(entry.getUlbName() != null ? entry.getUlbName() : "NA");
            row.add(entry.getContractor() != null ? entry.getContractor() : "NA");
            row.add(entry.getTechnology() != null ? entry.getTechnology() : "NA");
            row.add(entry.getGdwing() != null ? entry.getGdwing() : "NA");
            row.add(entry.getSoipackage() != null ? entry.getSoipackage() : "NA");
            row.add(entry.getSanctionedArea() != null ? String.format("%.2f", entry.getSanctionedArea()) : "0.00");
            row.add(entry.getBufferAreaDataAcquisition() != null ? String.format("%.2f", entry.getBufferAreaDataAcquisition()) : "0.00");
            row.add(entry.getUpdateOnDate() != null ? new SimpleDateFormat("HH:mm/yyyy-MM-dd").format(entry.getUpdateOnDate()) : "NA");
            row.add(entry.getTotalGrids() != null ? entry.getTotalGrids() : "0");
            row.add(entry.getGridsCompletedLastWeek() != null ? entry.getGridsCompletedLastWeek() : "0");
            row.add(entry.getTotalGridsCompleted() != null ? entry.getTotalGridsCompleted() : "0");
            row.add(entry.getTech1Cumulative() != null ? String.format("%.2f", entry.getTech1Cumulative()) : "0.00");
            row.add(entry.getTech1Percentage() != null ? String.valueOf(entry.getTech1Percentage()) : "0.00%");
            row.add(entry.getTechStatus() != null ? (entry.getTechStatus() == 1 ? "Not Started" : (entry.getTechStatus() == 2 ? "Under Process" : (entry.getTechStatus() == 3 ? "Completed" : ""))) : " ");
            row.add(entry.getTech2Cumulative() != null ? String.format("%.2f", entry.getTech2Cumulative()) : "0.00");
            row.add(entry.getTech2Percentage() != null ? String.valueOf(entry.getTech2Percentage()) : "0.00%");
            row.add(entry.getTech2ObliqueStatus() != null ? (entry.getTech2ObliqueStatus() == 1 ? "Not Started" : (entry.getTech2ObliqueStatus() == 2 ? "Under Process" : (entry.getTech2ObliqueStatus() == 3 ? "Completed" : ""))) : " ");
            row.add(entry.getTech3Cumulative() != null ? String.format("%.2f", entry.getTech3Cumulative()) : "0.00");
            row.add(entry.getTech3Percentage() != null ? String.valueOf(entry.getTech3Percentage()) : "0.00%");
            row.add(entry.getTech3ObliqueStatus() != null ? (entry.getTech3ObliqueStatus() == 1 ? "Not Started" : (entry.getTech3ObliqueStatus() == 2 ? "Under Process" : (entry.getTech3ObliqueStatus() == 3 ? "Completed" : ""))) : " ");
            row.add(entry.getTech3LidarSensor() != null ? String.format("%.2f", entry.getTech3LidarSensor()) : "0.00");
            row.add(entry.getTech3LidarSensorPercentage() != null ? String.format("%.2f", entry.getTech3LidarSensorPercentage()) : "0.00");
            row.add(entry.getTech3LidarSensorStatus() != null ? (entry.getTech3LidarSensorStatus() == 1 ? "Not Started" : (entry.getTech3LidarSensorStatus() == 2 ? "Under Process" : (entry.getTech3LidarSensorStatus() == 3 ? "Completed" : ""))) : " ");
            row.add(entry.getAerialAcquisirionRemark() != null ? entry.getAerialAcquisirionRemark() : " ");
            reportDataList.add(row);
        });

        // ✅ Add Grand Total row
        if (grandTotal != null && !grandTotal.isEmpty()) {
            NakshaMISDataEntrySolrAndVender total = grandTotal.get(0);
            List<String> totalRow = new ArrayList<>();
            totalRow.add("");  // S.No
            totalRow.add("Grand Total");
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            totalRow.add(String.format("%.2f", total.getGrandTotalSanctionedArea()));
            totalRow.add(String.format("%.2f", total.getGrandTotalBufferAreaDataAcquisition()));
            totalRow.add("");
            totalRow.add(String.valueOf(total.getGrandTotalTotalGrids()));
            totalRow.add(String.valueOf(total.getGrandTotalGridsCompletedLastWeek()));
            totalRow.add(String.valueOf(total.getGrandTotalTotalGridsCompleted()));
            totalRow.add(String.format("%.2f", total.getGrandTotalTech1Cumulative()));
            totalRow.add("");
            totalRow.add("");
            totalRow.add(String.format("%.2f", total.getGrandTotalTech2Cumulative()));
            totalRow.add("");
            totalRow.add("");
            totalRow.add(String.format("%.2f", total.getGrandTotalTech3Cumulative()));
            totalRow.add("");
            totalRow.add("");
            totalRow.add(String.format("%.2f", total.getGrandTotaltech3LidarSensor()));
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            while (totalRow.size() < headerText.length) {
                totalRow.add("");
            }

            reportDataList.add(totalRow);
        }

        eComponent.setReportDataList(reportDataList);

        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/naksha-vender-data-processing")
    public void exportNakshaVenderDataProcessingExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        DecimalFormat df = new DecimalFormat("0.00");

        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                "",
                "Format 2: Map 1 (SoI) – Data Processing, QA/QC Details, and Feature Extraction",
                "",
                "S.No", "Name of the Geospatial Directorate", "Email of the Geospatial Directorate", "Zone", "State",
                "District Name", "Name of ULB/Town",

                "Data Processing (Vendor ORI Submission & GD QA/QC Review)", "Data Processing (Vendor DSM Submission & GD QA/QC Review)",
                "Data Processing (Vendor DTM Submission & GD QA/QC Review)","Data Processing (Vendor Submission & GD QA/QC of 3D Mesh Models for Tech 2 & 3)",
                "2D Feature Extraction (Vendor Submission & GD QA/QC Review)","3D Feature Extraction (Vendor Submission & GD QA/QC Review)",

                "Fixing Ground Control Points & City Boundaries",
                "ORI Received Back from State (Yes/No)",
                "ORI Submitted to state after Rectification (Yes/No)",
                "Last Updated Time/ Date by GD",
                "Remarks",

                "ORI Submitted by vendor for QA/QC) Cumulative Area Completed",
                "ORI Submitted by Vendor for QA/QC – Cumulative Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",
                "QA/QC of ORI – Cumulative Area Completed",
                "QA/QC of ORI – Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",

                "DSM Submitted by Vendor for QA/QC – Cumulative Area Completed",
                "DSM Submitted by Vendor for QA/QC – Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",
                "QA/QC of DSM – Cumulative Area Completed",
                "QA/QC of DSM – Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",


                "DTM Submitted by Vendor for QA/QC – Cumulative Area Completed",
                "DTM Submitted by Vendor for QA/QC – Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",
                "QA/QC of DTM – Cumulative Area Completed",
                "QA/QC of DTM – Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",


                "3D Mesh Model Submitted by Vendor for QA/QC – Cumulative Completed",
                "3D Mesh Model Submitted by Vendor for QA/QC – Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",
                "QA/QC of 3D Mesh – Cumulative Area Completed",
                "QA/QC of 3D Mesh – Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",

                "2D Feature Extraction on ORI – Cumulative Area Completed",
                "2D Feature Extraction on ORI – Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",
                "QA/QC of 2D Feature Extraction – Cumulative Area Completed",
                "QA/QC of 2D Feature Extraction – Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",

                "2D Feature Extraction in Stereo Mode - Cumulative Area Completed",
                "2D Feature Extraction in Stereo Mode - Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",
                "QA/QC of 2D Feature Extraction in Stereo Mode - Cumulative Area Completed",
                "QA/QC of 2D Feature Extraction in Stereo Mode - Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",

                "3D Feature Extraction Submitted for QA/QC Cumulative Area Completed",
                "3D Feature Extraction Submitted for QA/QC Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",
                "QA/QC of 3D Feature Extraction Cumulative Area Completed",
                "QA/QC of 3D Feature Extraction Percentage Completed",
                "Completion Status (Completed / Under Process / Not Started)",
        };
        String[] headerSpanMerged = {
                "A1:BB1", "A2:BB2", "A3:BB3", "A4:BB4", "A5:BB5", "A6:BB6",
                "A7:A9", "B7:B9", "C7:C9", "D7:D9", "E7:E9", "F7:F9", "G7:G9",
                "H7:M7", "N7:S7", "T7:Y7","Z7:AE7","AF7:AQ7","AR7:AW7",
                "AX7:AX9", "AY7:AY9", "AZ7:AZ9","BA7:BA9","BB7:BB9",
                "H8:H9", "I8:I9", "J8:J9", "K8:K9", "L8:L9", "M8:M9", "N8:N9", "O8:O9", "P8:P9", "Q8:Q9", "R8:R9", "S8:S9", "T8:T9", "U8:U9", "V8:V9", "W8:W9", "X8:X9", "Y8:Y9", "Z8:Z9",
                "AA8:AA9", "AB8:AB9", "AC8:AC9", "AD8:AD9", "AE8:AE9", "AF8:AF9", "AG8:AG9", "AH8:AH9", "AI8:AI9", "AJ8:AJ9", "AK8:AK9", "AL8:AL9", "AM8:AM9", "AN8:AN9", "AO8:AO9", "AP8:AP9", "AQ8:AQ9", "AR8:AR9", "AS8:AS9", "AT8:AT9", "AU8:AU9", "AV8:AV9", "AW8:AW9"
        };
        String[] headerSpanUnmerged = {};
        eComponent.setReportName("NakshaSOIDataProcessingReport");
        eComponent.setNoOfColumns(54);
        eComponent.setNoOfheaderRows(9);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<NakshaMISDataEntrySolrAndVender> list = (List<NakshaMISDataEntrySolrAndVender>) session.getAttribute("nakshaMISDataEntries");
        List<NakshaMISDataEntrySolrAndVender> grandTotal = (List<NakshaMISDataEntrySolrAndVender>) session.getAttribute("grandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        list.forEach(entry -> {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(indexHolder.incrementAndGet()));
            row.add(entry.getOfficerName() != null ? entry.getOfficerName() : "NA");
            row.add(entry.getGdEmail() != null ? entry.getGdEmail() : "NA");
            row.add(entry.getZoneName() != null ? entry.getZoneName() : "NA");
            row.add(entry.getStateName() != null ? entry.getStateName() : "NA");
            row.add(entry.getDistrict_Name() != null ? entry.getDistrict_Name() : "NA");
            row.add(entry.getUlbName() != null ? entry.getUlbName() : "NA");

            row.add(entry.getSeparateOriDataProcessingCumulative() != null ? entry.getSeparateOriDataProcessingCumulative() : "0.00");
            row.add(entry.getSeparateOriDataProcessingPercentage() != null ? entry.getSeparateOriDataProcessingPercentage() : "0.00");
            row.add(entry.getOriCumulativestatus() != null ? (entry.getOriCumulativestatus() == 1 ? "Not Started" : (entry.getOriCumulativestatus() == 2 ? "Under Process" : (entry.getOriCumulativestatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getSeparateOriQaqcDataProcessingCumulative() != null ? entry.getSeparateOriQaqcDataProcessingCumulative() : "0.00");
            row.add(entry.getSeparateOriQaqcDataProcessingPercentage() != null ? entry.getSeparateOriQaqcDataProcessingPercentage() : "0.00");
            row.add(entry.getOriCumulativeQaQcStatus() != null ? (entry.getOriCumulativeQaQcStatus() == 1 ? "Not Started" : (entry.getOriCumulativeQaQcStatus() == 2 ? "Under Process" : (entry.getOriCumulativeQaQcStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getDsmDataProcessingCumulative() != null ? entry.getDsmDataProcessingCumulative() : "0.00");
            row.add(entry.getDsmDataProcessingPercentage() != null ? entry.getDsmDataProcessingPercentage() : "0.00%");
            row.add(entry.getDsmSubmissionStatus() != null ? (entry.getDsmSubmissionStatus() == 1 ? "Not Started" : (entry.getDsmSubmissionStatus() == 2 ? "Under Process" : (entry.getDsmSubmissionStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getDsmDataProcessingQaqcCumulative() != null ? entry.getDsmDataProcessingQaqcCumulative() : "0.00");
            row.add(entry.getDsmDataProcessingQaqcPercentage() != null ? entry.getDsmDataProcessingQaqcPercentage() : "0.00");
            row.add(entry.getDsmSubmissionQaQcStatus() != null ? (entry.getDsmSubmissionQaQcStatus() == 1 ? "Not Started" : (entry.getDsmSubmissionQaQcStatus() == 2 ? "Under Process" : (entry.getDsmSubmissionQaQcStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getDtmDataProcessingCumulative() != null ?  entry.getDtmDataProcessingCumulative() : "0.00");
            row.add(entry.getDtmDataProcessingPercentage() != null ? entry.getDtmDataProcessingPercentage() : "0.00%");
            row.add(entry.getDtmSubmissionStatus() != null ? (entry.getDtmSubmissionStatus() == 1 ? "Not Started" : (entry.getDtmSubmissionStatus() == 2 ? "Under Process" : (entry.getDtmSubmissionStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getDtmDataProcessingQaqcCumulative() != null ? entry.getDtmDataProcessingQaqcCumulative() : "0.00");
            row.add(entry.getDtmDataProcessingQaqcPercentage() != null ? entry.getDtmDataProcessingQaqcPercentage() : "0.00%");
            row.add(entry.getDtmSubmissionQaQcStatus() != null ? (entry.getDtmSubmissionQaQcStatus() == 1 ? "Not Started" : (entry.getDtmSubmissionQaQcStatus() == 2 ? "Under Process" : (entry.getDtmSubmissionQaQcStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getMeshDataProcessingCumulative() != null ?  entry.getMeshDataProcessingCumulative() : "0.00");
            row.add(entry.getMeshDataProcessingPercentage() != null ? entry.getMeshDataProcessingPercentage().toString() : "0.00%");
            row.add(entry.getThreedMeshModelStatus() != null ? (entry.getThreedMeshModelStatus() == 1 ? "Not Started" : (entry.getThreedMeshModelStatus() == 2 ? "Under Process" : (entry.getThreedMeshModelStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getMeshDataProcessingQaqcCumulative() != null ? entry.getMeshDataProcessingQaqcCumulative() : "0.00");
            row.add(entry.getMeshDataProcessingQaqcPercentage() != null ? entry.getMeshDataProcessingQaqcPercentage() : "0.00%");
            row.add(entry.getThreedMeshModelQaQcStatus() != null ? (entry.getThreedMeshModelQaQcStatus() == 1 ? "Not Started" : (entry.getThreedMeshModelQaQcStatus() == 2 ? "Under Process" : (entry.getThreedMeshModelQaQcStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getFeatureExtractionOriCumulativeCompleted() != null ? String.format("%.2f", entry.getFeatureExtractionOriCumulativeCompleted()) : "0.00");
            row.add(entry.getFeatureExtractionOriPercentageCompleted() != null ? String.format("%.2f%%", Double.parseDouble(entry.getFeatureExtractionOriPercentageCompleted().toString())) : "0.00%");
            row.add(entry.getTwoDfeatureextractionStatus() != null ? (entry.getTwoDfeatureextractionStatus() == 1 ? "Not Started" : (entry.getTwoDfeatureextractionStatus() == 2 ? "Under Process" : (entry.getTwoDfeatureextractionStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getFeatureExtractionOriQaqcCumulativeCompleted() != null ? String.format("%.2f", entry.getFeatureExtractionOriQaqcCumulativeCompleted()) : "0.00");
            row.add(entry.getFeatureExtractionOriQaqcPercentageCompleted() != null ? String.format("%.2f%%", Double.parseDouble(entry.getFeatureExtractionOriQaqcPercentageCompleted().toString())) : "0.00%");
            row.add(entry.getTwoDfeatureextractionQaQcStatus() != null ? (entry.getTwoDfeatureextractionQaQcStatus() == 1 ? "Not Started" : (entry.getTwoDfeatureextractionQaQcStatus() == 2 ? "Under Process" : (entry.getTwoDfeatureextractionQaQcStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getTwoDFeatextrStereomodeCumulative() != null ? String.format("%.2f", entry.getTwoDFeatextrStereomodeCumulative()) : "0.00");
            row.add(entry.getTwoDFeatextrStereomodePercentage() != null ? String.format("%.2f%%", Double.parseDouble(entry.getTwoDFeatextrStereomodePercentage().toString())) : "0.00%");
            row.add(entry.getTwoDFeatextrStereomodeStatus() != null ? (entry.getTwoDFeatextrStereomodeStatus() == 1 ? "Not Started" : (entry.getTwoDFeatextrStereomodeStatus() == 2 ? "Under Process" : (entry.getTwoDFeatextrStereomodeStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getTwoDFeatextrStereomodeCumulativeQaQc() != null ? String.format("%.2f", entry.getTwoDFeatextrStereomodeCumulativeQaQc()) : "0.00");
            row.add(entry.getTwoDFeatextrStereomodePercentageQaQc() != null ? String.format("%.2f%%", Double.parseDouble(entry.getTwoDFeatextrStereomodePercentageQaQc().toString())) : "0.00%");
            row.add(entry.getTwoDFeatextrStereomodeQaQcStatus() != null ? (entry.getTwoDFeatextrStereomodeQaQcStatus() == 1 ? "Not Started" : (entry.getTwoDFeatextrStereomodeQaQcStatus() == 2 ? "Under Process" : (entry.getTwoDFeatextrStereomodeQaQcStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getThreeDFeatextrCumulative() != null ? String.format("%.2f", entry.getThreeDFeatextrCumulative()) : "0.00");
            row.add(entry.getThreeDFeatextrPercentage() != null ? String.format("%.2f%%", Double.parseDouble(entry.getThreeDFeatextrPercentage().toString())) : "0.00%");
            row.add(entry.getThreeDFeatextrStatus() != null ? (entry.getThreeDFeatextrStatus() == 1 ? "Not Started" : (entry.getThreeDFeatextrStatus() == 2 ? "Under Process" : (entry.getThreeDFeatextrStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getThreeDFeatextrCumulativeQaQc() != null ? String.format("%.2f", entry.getThreeDFeatextrCumulativeQaQc()) : "0.00");
            row.add(entry.getThreeDDFeatextrPercentageQaQc() != null ? String.format("%.2f%%", Double.parseDouble(entry.getThreeDDFeatextrPercentageQaQc().toString())) : "0.00%");
            row.add(entry.getThreeDFeatextrQaQcStatus() != null ? (entry.getThreeDFeatextrQaQcStatus() == 1 ? "Not Started" : (entry.getThreeDFeatextrQaQcStatus() == 2 ? "Under Process" : (entry.getThreeDFeatextrQaQcStatus() == 3 ? "Completed" : ""))) : " ");

            row.add(entry.getTotalGC() != null ? entry.getTotalGC() : "NA");
            row.add("Yes".equalsIgnoreCase(entry.getOriReceivedBack()) ? "Yes" : "No");
            row.add("Yes".equalsIgnoreCase(entry.getOriSubmittedToState()) ? "Yes" : "No");
            row.add(entry.getUpdateOnDate() != null ? new SimpleDateFormat("HH:mm/yyyy-MM-dd").format(entry.getUpdateOnDate()) : "NA");
            row.add(entry.getRemark() != null && !entry.getRemark().isEmpty() ? entry.getRemark() : "");
            reportDataList.add(row);
        });
        if (grandTotal != null && !grandTotal.isEmpty()) {
            NakshaMISDataEntrySolrAndVender total = grandTotal.get(0);
            List<String> totalRow = new ArrayList<>();
            totalRow.add(""); // S.No
            totalRow.add("Grand Total"); // Label
            totalRow.add(""); // Email
            totalRow.add(""); // Zone
            totalRow.add(""); // State
            totalRow.add(""); // District
            totalRow.add(""); // ULB

// ORI Data Processing Cumulative
            totalRow.add(total.getGrandTotalSeparateOriDataProcessingCumulative() != null ? String.format("%.2f", total.getGrandTotalSeparateOriDataProcessingCumulative()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// QA/QC ORI Data Processing Cumulative
            totalRow.add(total.getGrandTotalSeparateOriQaqcDataProcessingCumulative() != null ? String.format("%.2f", total.getGrandTotalSeparateOriQaqcDataProcessingCumulative()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// DSM Data Processing Cumulative
            totalRow.add(total.getGrandTotalDsmDataProcessingCumulative() != null ? String.format("%.2f", total.getGrandTotalDsmDataProcessingCumulative()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// QA/QC DSM Data Processing Cumulative
            totalRow.add(total.getGrandTotalDsmDataProcessingQaqcCumulative() != null ? String.format("%.2f", total.getGrandTotalDsmDataProcessingQaqcCumulative()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// DTM Data Processing Cumulative
            totalRow.add(total.getGrandTotalDtmDataProcessingCumulative() != null ? String.format("%.2f", total.getGrandTotalDtmDataProcessingCumulative()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// QA/QC DTM Data Processing Cumulative
            totalRow.add(total.getGrandTotalDtmDataProcessingQaqcCumulative() != null ? String.format("%.2f", total.getGrandTotalDtmDataProcessingQaqcCumulative()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// 3D Mesh Data Processing Cumulative
            totalRow.add(total.getGrandTotalMeshDataProcessingCumulative() != null ? String.format("%.2f", total.getGrandTotalMeshDataProcessingCumulative()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// QA/QC 3D Mesh Data Processing Cumulative
            totalRow.add(total.getGrandTotalMeshDataProcessingQaqcCumulative() != null ? String.format("%.2f", total.getGrandTotalMeshDataProcessingQaqcCumulative()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// Feature Extraction ORI Cumulative Completed
            totalRow.add(total.getGrandTotalFeatureExtractionOriCumulativeCompleted() != null ? String.format("%.2f", total.getGrandTotalFeatureExtractionOriCumulativeCompleted()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// Feature Extraction QA/QC ORI Cumulative Completed
            totalRow.add(total.getGrandTotalFeatureExtractionOriQaqcCumulativeCompleted() != null ? String.format("%.2f", total.getGrandTotalFeatureExtractionOriQaqcCumulativeCompleted()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// Feature Extraction Stereo Mode Cumulative
            totalRow.add(total.getGrandTotaltwoDFeatextrStereomodeCumulative() != null ? String.format("%.2f", total.getGrandTotaltwoDFeatextrStereomodeCumulative()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// Feature Extraction Stereo Mode QA/QC Cumulative
            totalRow.add(total.getGrandTotaltwoDFeatextrStereomodeCumulativeQaQc() != null ? String.format("%.2f", total.getGrandTotaltwoDFeatextrStereomodeCumulativeQaQc()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// 3D Feature Extraction Cumulative
            totalRow.add(total.getGrandTotalthreeDFeatextrCumulative() != null ? String.format("%.2f", total.getGrandTotalthreeDFeatextrCumulative()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// 3D Feature Extraction QA/QC Cumulative
            totalRow.add(total.getGrandTotalthreeDFeatextrCumulativeQaQc() != null ? String.format("%.2f", total.getGrandTotalthreeDFeatextrCumulativeQaQc()) : "0.00");
            totalRow.add("");
            totalRow.add("");

// Total GC (only one value cell, then 3 empty cells)
            totalRow.add(total.getGrandTotalTotalGC() != null ? String.valueOf(total.getGrandTotalTotalGC()) : "0");
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");

            while (totalRow.size() < headerText.length) {
                totalRow.add("");
            }
            reportDataList.add(totalRow);
        }
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/date-wise-diffrence")
    public void getDateWisediffrenceExcel(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String date1 = (String) session.getAttribute("fromdate");
        String date2 = (String) session.getAttribute("todate");
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)", "",
                "Date-wise Physical Progress Difference Report: Comparison Across All Components (Data available from October 18, 2024)",
                "Physical Progress Differences for All Components (" + date1 + " to " + date2 + ")", "",
                "S.No.",
                "State/UT Name",
                "Total Tehsils",
                "Total Villages",
                "Difference",
                "Total No. of RoRs",
                "No. of RoRs Computerized",
                "RoRs Linked with Aadhaar",
                "Integration of Land Records (RoRs) with Banks (No. of Districts)",
                "CLR Completed Villages",
                "Total No. of Cadastral Maps",
                "No. of Cadastral Maps Digitized",
                "Total Mapsheets / FMBs / Tippans (No.)",
                "Digitized Mapsheets / FMBs / Tippans (No.)",
                "Digitized FMBs + Tippans (No.)",
                "Villages with Cadastral Maps Linked with RoRs",
                "Villages Having Cadastral Maps / FMBs / Tippans Geo-Referenced",
                "Total No. of Land Parcels",
                "No. of Land Parcels Assigned ULPIN",
                "No. of Geo-Referenced Land Parcel",
                "Total SROs",
                "SROs Computerized",
                "Sanctioned Modern Record Rooms",
                "Completed Modern Record Rooms",
                "Survey / Resurvey Completed (Villages)",
                "No. of Revenue courts computerized"
        };
        // Header spans for merged and unmerged cells
        String[] headerSpanMerged = {
                "A1:Y1", "A2:Y2", "A3:Y3", "A4:Y4", "A5:Y5", "A6:Y6", "A7:Y7",
                "A8:A9", "B8:B9", "C8:C9", "D8:D9",
                "E8:V8"
        };

        String[] headerSpanUnmerged = {
                "E9", "F9", "G9", "H9", "I9", "J9", "K9", "L9", "M9", "N9", "O9", "P9", "Q9", "R9", "S9", "T9", "U9", "V9","W9","X9","Y9"
        };
        eComponent.setReportName("Date-wise Comparison Report for All Components");
        eComponent.setNoOfColumns(25);
        eComponent.setNoOfheaderRows(9);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<ComparisonReportDTO> list = (List<ComparisonReportDTO>) session.getAttribute("comparisonData");
        ComparisonReportDTO grandTotal = (ComparisonReportDTO) session.getAttribute("grandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        if (list != null && !list.isEmpty()) {
            list.forEach(gList -> {
                if (!"Grand Total".equals(gList.getStateName())) {
                    List<String> row = new ArrayList<>();
                    row.add(Integer.toString(indexHolder.incrementAndGet()));
                    row.add(gList.getStateName());
                    row.add(gList.getTotalTehsilsDiff());
                    row.add(gList.getTotalVillagesDiff());
                    row.add(gList.getTotalRorDiff());
                    row.add(gList.getRorComputerizedDiff());
                    row.add(gList.getRorLinkedWithAadhaarDiff());
                    row.add(gList.getDistrictsWithGenderBasedOwnershipDiff());
                    row.add(gList.getVillagesClrCompletedDiff());
                    row.add(gList.getTotalCadastralMapsDiff());
                    row.add(gList.getDigitizedCadastralMapsDiff());
                    row.add(gList.getTotalMapsFmbTippansDiff());
                    row.add(gList.getTotalDigitizedMapsFmbTippansDiff());
                    row.add(gList.getTotalDigitizedFmbTippansDiff());
                    row.add(gList.getVillagesLinkedWithRorDiff());
                    row.add(gList.getVillagesGeoreferencedDiff());
                    row.add(gList.getTotalLandParcelsDiff());
                    row.add(gList.getLandParcelsWithUlipnDiff());
                    row.add(gList.getGeoreferencedLandParcelsDiff());
                    row.add(gList.getNoOfSroInStateDiff());
                    row.add(gList.getNoOfSroUsingOnlineRegistrationDiff());
                    row.add(gList.getMrrSanctionedDiff());
                    row.add(gList.getMrrCompletedDiff());
                    row.add(gList.getVillagesFinalPromulgationDoneDiff());
                    row.add(gList.getRevenueCourtsComputerizedDiff());
                    reportDataList.add(row);
                }
            });
        }
        if (grandTotal != null) {
            List<String> grandTotalRow = new ArrayList<>();
            grandTotalRow.add(" "); // Serial number for Grand Total
            grandTotalRow.add("Grand Total");
            grandTotalRow.add(grandTotal.getTotalTehsilsDiff());
            grandTotalRow.add(grandTotal.getTotalVillagesDiff());
            grandTotalRow.add(grandTotal.getTotalRorDiff());
            grandTotalRow.add(grandTotal.getRorComputerizedDiff());
            grandTotalRow.add(grandTotal.getRorLinkedWithAadhaarDiff());
            grandTotalRow.add(grandTotal.getDistrictsWithGenderBasedOwnershipDiff());
            grandTotalRow.add(grandTotal.getVillagesClrCompletedDiff());
            grandTotalRow.add(grandTotal.getTotalCadastralMapsDiff());
            grandTotalRow.add(grandTotal.getDigitizedCadastralMapsDiff());
            grandTotalRow.add(grandTotal.getTotalMapsFmbTippansDiff());
            grandTotalRow.add(grandTotal.getTotalDigitizedMapsFmbTippansDiff());
            grandTotalRow.add(grandTotal.getTotalDigitizedFmbTippansDiff());
            grandTotalRow.add(grandTotal.getVillagesLinkedWithRorDiff());
            grandTotalRow.add(grandTotal.getVillagesGeoreferencedDiff());
            grandTotalRow.add(grandTotal.getTotalLandParcelsDiff());
            grandTotalRow.add(grandTotal.getLandParcelsWithUlipnDiff());
            grandTotalRow.add(grandTotal.getGeoreferencedLandParcelsDiff());
            grandTotalRow.add(grandTotal.getNoOfSroInStateDiff());
            grandTotalRow.add(grandTotal.getNoOfSroUsingOnlineRegistrationDiff());
            grandTotalRow.add(grandTotal.getMrrSanctionedDiff());
            grandTotalRow.add(grandTotal.getMrrCompletedDiff());
            grandTotalRow.add(grandTotal.getVillagesFinalPromulgationDoneDiff());
            grandTotalRow.add(grandTotal.getRevenueCourtsComputerizedDiff());
            reportDataList.add(grandTotalRow);
        }
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //v5 Legacy Digitization — state Excel
    @GetMapping("/legacy-digitization")
    public void exportLegacyDigitizationExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                ReportLabels.LEGACY_DIGITIZATION_REPORT, "",
                ReportLabels.SERIAL_NUMBER, ReportLabels.STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS,
                ReportLabels.TOTAL_LEGACY_REGISTERED_DOCUMENTS, ReportLabels.LEGACY_DIGITIZED_FROM_STATE_FUNDS,
                ReportLabels.LEGACY_SANCTIONED_UNDER_DILRMP, ReportLabels.LEGACY_COMPLETED_FROM_DILRMP_FUNDS,
                ReportLabels.TOTAL_LEGACY_DIGITIZED, ReportLabels.LEGACY_DIGITIZED_UPTO_YEAR,
                ReportLabels.NO_OF_PAGES, ReportLabels.NO_OF_PAGES, ReportLabels.PERCENTAGE,
                ReportLabels.NO_OF_PAGES, ReportLabels.NO_OF_PAGES, ReportLabels.PERCENTAGE,
                ReportLabels.NO_OF_PAGES, ReportLabels.PERCENTAGE
        };
        String[] headerSpanMerged = {
                "A1:M1", "A2:M2", "A3:M3", "A4:M4", "A5:M5",
                "A6:A7", "B6:B7", "C6:C7", "D6:D7", "M6:M7",
                "F6:G6", "I6:J6", "K6:L6"
        };
        String[] headerSpanUnmerged = {"E7", "F7", "G7", "H7", "I7", "J7", "K7", "L7"};
        eComponent.setReportName(ReportLabels.LEGACY_DIGITIZATION_REPORT);
        eComponent.setNoOfColumns(13);
        eComponent.setNoOfheaderRows(7);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<LegacyDigitizationReport> legacyList =
                (List<LegacyDigitizationReport>) session.getAttribute("legacyList");
        List<LegacyDigitizationReport> grandTotal =
                (List<LegacyDigitizationReport>) session.getAttribute("legacyGrandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
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
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add(gt.getStateName() != null ? gt.getStateName() : "Grand Total");
                grandTotalList.add(gt.getFormattingTotalDistrict() != null ? gt.getFormattingTotalDistrict() : "0");
                grandTotalList.add(gt.getFormattingTotalTehsils() != null ? gt.getFormattingTotalTehsils() : "0");
                LegacyDigitizationExportUtil.appendStateFormatted(grandTotalList, gt);
                reportDataList.add(grandTotalList);
            });
        }
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //v5 Legacy Digitization — district Excel
    @GetMapping("/legacy-digitization-district")
    public void exportLegacyDigitizationDistrictExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                ReportLabels.LEGACY_DIGITIZATION_REPORT, ReportLabels.STATE_UT + " : " + stateName, "",
                ReportLabels.SERIAL_NUMBER, ReportLabels.DISTRICT_NAME, ReportLabels.TOTAL_TEHSILS,
                ReportLabels.TOTAL_LEGACY_REGISTERED_DOCUMENTS, ReportLabels.LEGACY_DIGITIZED_FROM_STATE_FUNDS,
                ReportLabels.LEGACY_SANCTIONED_UNDER_DILRMP, ReportLabels.LEGACY_COMPLETED_FROM_DILRMP_FUNDS,
                ReportLabels.TOTAL_LEGACY_DIGITIZED, ReportLabels.LEGACY_DIGITIZED_UPTO_YEAR,
                ReportLabels.NO_OF_PAGES, ReportLabels.NO_OF_PAGES, ReportLabels.PERCENTAGE,
                ReportLabels.NO_OF_PAGES, ReportLabels.NO_OF_PAGES, ReportLabels.PERCENTAGE,
                ReportLabels.NO_OF_PAGES, ReportLabels.PERCENTAGE
        };
        String[] headerSpanMerged = {
                "A1:L1", "A2:L2", "A3:L3", "A4:L4", "A5:L5", "A6:L6",
                "A7:A8", "B7:B8", "C7:C8", "L7:L8",
                "E7:F7", "H7:I7", "J7:K7"
        };
        String[] headerSpanUnmerged = {"D8", "E8", "F8", "G8", "H8", "I8", "J8", "K8"};
        eComponent.setReportName(ReportLabels.LEGACY_DIGITIZATION_REPORT);
        eComponent.setNoOfColumns(12);
        eComponent.setNoOfheaderRows(8);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<DistrictLegacyDigitizationReport> districtList =
                (List<DistrictLegacyDigitizationReport>) session.getAttribute("districtLegacyData");
        List<LegacyDigitizationReport> grandTotal =
                (List<LegacyDigitizationReport>) session.getAttribute("stateLegacyData");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        if (districtList != null && !districtList.isEmpty()) {
            districtList.forEach(map -> {
                List<String> strings = new ArrayList<>();
                strings.add(Integer.toString(indexHolder.incrementAndGet()));
                strings.add(map.getDistrictName() != null ? map.getDistrictName().toUpperCase() : "N/A");
                strings.add(map.getTotalTehsils() != null ? String.valueOf(map.getTotalTehsils()) : "0");
                LegacyDigitizationExportUtil.appendDistrict(strings, map);
                reportDataList.add(strings);
            });
        }
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add("Grand Total");
                grandTotalList.add(gt.getTotalTehsils() != null ? String.valueOf(gt.getTotalTehsils()) : "0");
                LegacyDigitizationExportUtil.appendStateRaw(grandTotalList, gt);
                reportDataList.add(grandTotalList);
            });
        }
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //v5 Legacy Revenue Records Digitisation — state Excel
    @GetMapping("/legacy-revenue")
    public void exportLegacyRevenueExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                ReportLabels.LEGACY_REVENUE_REPORT, "",
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.STATE_UT,
                ReportLabels.TOTAL_DISTRICTS,
                ReportLabels.TOTAL_TEHSILS,
                ReportLabels.LEGACY_REVENUE_DIGITISED_STATE_FUNDS,
                ReportLabels.LEGACY_REVENUE_COMPLETED_DILRMP,
                ReportLabels.TOTAL_LEGACY_REVENUE_DIGITISED,
                ReportLabels.TOTAL_LEGACY_REVENUE_RECORDS,
                ReportLabels.LEGACY_REVENUE_SANCTIONED_DILRMP,
                ReportLabels.LEGACY_REVENUE_UPTO_YEAR,
                ReportLabels.NO_OF_PAGES,
                ReportLabels.NO_OF_PAGES,
                ReportLabels.PERCENTAGE,
                ReportLabels.NO_OF_PAGES,
                ReportLabels.NO_OF_PAGES,
                ReportLabels.PERCENTAGE,
                ReportLabels.NO_OF_PAGES,
                ReportLabels.PERCENTAGE,
                ReportLabels.YEAR
        };
        String[] headerSpanMerged = {
                "A1:M1", "A2:M2", "A3:M3", "A4:M4", "A5:M5",
                "A6:A7", "B6:B7", "C6:C7", "D6:D7",
                "F6:G6", "I6:J6", "K6:L6"
        };
        String[] headerSpanUnmerged = {
                "E6", "H6", "M6",
                "E7", "F7", "G7", "H7", "I7", "J7", "K7", "L7", "M7"
        };
        eComponent.setReportName("Legacy Revenue Records");
        eComponent.setNoOfColumns(13);
        eComponent.setNoOfheaderRows(7);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<LegacyRevenueReport> legacyList = legacyRevenueReportService.getAllFormattedList();
        List<LegacyRevenueReport> grandTotal = legacyRevenueReportService.getGrandTotalFormatted();
        List<List<String>> reportDataList = new ArrayList<>();
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
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add(gt.getStateName() != null ? gt.getStateName() : "Grand Total");
                grandTotalList.add(gt.getFormattingTotalDistrict() != null ? gt.getFormattingTotalDistrict() : "0");
                grandTotalList.add(gt.getFormattingTotalTehsils() != null ? gt.getFormattingTotalTehsils() : "0");
                LegacyRevenueExportUtil.appendStateFormatted(grandTotalList, gt);
                reportDataList.add(grandTotalList);
            });
        }
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //v5 Legacy Revenue Records Digitisation — district Excel
    @GetMapping("/legacy-revenue-district")
    public void exportLegacyRevenueDistrictExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        Long stateId = (Long) session.getAttribute("legacyRevenueStateId");
        List<DistrictLegacyRevenueReport> districtList;
        List<LegacyRevenueReport> grandTotal;
        if (stateId != null) {
            districtList = legacyRevenueReportService.getDistrictListByStateId(stateId);
            grandTotal = legacyRevenueReportService.getStateListForDistrictPage(stateId);
        } else {
            districtList = (List<DistrictLegacyRevenueReport>) session.getAttribute("districtLegacyRevenueData");
            grandTotal = (List<LegacyRevenueReport>) session.getAttribute("stateLegacyRevenueData");
        }
        String stateName = (String) session.getAttribute("legacyRevenueStateName");
        if ((stateName == null || stateName.isBlank()) && districtList != null && !districtList.isEmpty()
                && districtList.get(0).getStateName() != null) {
            stateName = districtList.get(0).getStateName();
        }
        if ((stateName == null || stateName.isBlank()) && grandTotal != null && !grandTotal.isEmpty()
                && grandTotal.get(0).getStateName() != null) {
            stateName = grandTotal.get(0).getStateName();
        }
        if (stateName == null) {
            stateName = "";
        }
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                ReportLabels.LEGACY_REVENUE_REPORT,
                ReportLabels.STATE_UT + " : " + (stateName == null ? "" : stateName),
                "",
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.DISTRICT_NAME,
                ReportLabels.TOTAL_TEHSILS,
                ReportLabels.LEGACY_REVENUE_DIGITISED_STATE_FUNDS,
                ReportLabels.LEGACY_REVENUE_COMPLETED_DILRMP,
                ReportLabels.TOTAL_LEGACY_REVENUE_DIGITISED,
                ReportLabels.TOTAL_LEGACY_REVENUE_RECORDS,
                ReportLabels.LEGACY_REVENUE_SANCTIONED_DILRMP,
                ReportLabels.LEGACY_REVENUE_UPTO_YEAR,
                ReportLabels.NO_OF_PAGES,
                ReportLabels.NO_OF_PAGES,
                ReportLabels.PERCENTAGE,
                ReportLabels.NO_OF_PAGES,
                ReportLabels.NO_OF_PAGES,
                ReportLabels.PERCENTAGE,
                ReportLabels.NO_OF_PAGES,
                ReportLabels.PERCENTAGE,
                ReportLabels.YEAR
        };
        String[] headerSpanMerged = {
                "A1:L1", "A2:L2", "A3:L3", "A4:L4", "A5:L5", "A6:L6",
                "A7:A8", "B7:B8", "C7:C8",
                "E7:F7", "H7:I7", "J7:K7"
        };
        String[] headerSpanUnmerged = {
                "D7", "G7", "L7",
                "D8", "E8", "F8", "G8", "H8", "I8", "J8", "K8", "L8"
        };
        eComponent.setReportName("Legacy Revenue District");
        eComponent.setNoOfColumns(12);
        eComponent.setNoOfheaderRows(8);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        if (districtList != null && !districtList.isEmpty()) {
            districtList.forEach(map -> {
                List<String> strings = new ArrayList<>();
                strings.add(Integer.toString(indexHolder.incrementAndGet()));
                strings.add(map.getDistrictName() != null ? map.getDistrictName().toUpperCase() : "N/A");
                strings.add(map.getTotalTehsils() != null ? String.valueOf(map.getTotalTehsils()) : "0");
                LegacyRevenueExportUtil.appendDistrict(strings, map);
                reportDataList.add(strings);
            });
        }
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add("Grand Total");
                grandTotalList.add(gt.getTotalTehsils() != null ? String.valueOf(gt.getTotalTehsils()) : "0");
                LegacyRevenueExportUtil.appendStateRaw(grandTotalList, gt);
                reportDataList.add(grandTotalList);
            });
        }
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //v5 Modernization of Registration Office (SRO) — state Excel
    @GetMapping("/sro-modernization")
    public void exportSroModernizationExcelFile(HttpServletResponse response, HttpSession session) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        // Merged texts (order = headerSpanMerged): titles + row6 headers
        // Unmerged texts (order = headerSpanUnmerged): row7 Nos. / %
        String[] headerText = {
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                ReportLabels.SRO_MODERNIZATION_REPORT, "",
                // Merged order must match headerSpanMerged: A,B,C,D,E,H then F:G, I:J, K:L
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.STATE_UT,
                ReportLabels.TOTAL_DISTRICTS,
                ReportLabels.TOTAL_SROS,
                ReportLabels.SROS_USING_ONLINE_REGISTRATION_A,
                ReportLabels.SROS_SANCTIONED_DILRMP,
                ReportLabels.SROS_MODERNISED_STATE_FUNDS,
                ReportLabels.SROS_MODERNISED_DILRMP_FUNDS,
                ReportLabels.SROS_MODERNISED_TOTAL,
                ReportLabels.NOS,
                ReportLabels.PERCENTAGE,
                ReportLabels.NOS,
                ReportLabels.PERCENTAGE,
                ReportLabels.NOS,
                ReportLabels.PERCENTAGE
        };
        String[] headerSpanMerged = {
                "A1:L1", "A2:L2", "A3:L3", "A4:L4", "A5:L5",
                "A6:A7", "B6:B7", "C6:C7", "D6:D7", "E6:E7", "H6:H7",
                "F6:G6", "I6:J6", "K6:L6"
        };
        String[] headerSpanUnmerged = {"F7", "G7", "I7", "J7", "K7", "L7"};
        eComponent.setReportName(ReportLabels.SRO_MODERNIZATION_REPORT);
        eComponent.setNoOfColumns(12);
        eComponent.setNoOfheaderRows(7);
        eComponent.setHeaderText(headerText);
        eComponent.setHeaderSpanMerged(headerSpanMerged);
        eComponent.setHeaderSpanUnMerged(headerSpanUnmerged);
        List<SroModernizationReport> list =
                (List<SroModernizationReport>) session.getAttribute("sroModernizationList");
        List<SroModernizationReport> grandTotal =
                (List<SroModernizationReport>) session.getAttribute("sroModernizationGrandTotal");
        List<List<String>> reportDataList = new ArrayList<>();
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
        if (grandTotal != null && !grandTotal.isEmpty()) {
            grandTotal.forEach(gt -> {
                List<String> grandTotalList = new ArrayList<>();
                grandTotalList.add("");
                grandTotalList.add(gt.getStateName() != null ? gt.getStateName() : "Grand Total");
                grandTotalList.add(gt.getFormattingTotalDistricts() != null ? gt.getFormattingTotalDistricts() : "0");
                grandTotalList.add(gt.getFormattingTotalSros() != null ? gt.getFormattingTotalSros() : "0");
                SroModernizationExportUtil.appendStateFormatted(grandTotalList, gt);
                reportDataList.add(grandTotalList);
            });
        }
        eComponent.setReportDataList(reportDataList);
        try {
            ExcelExporter.generateExcelFile(response, eComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
