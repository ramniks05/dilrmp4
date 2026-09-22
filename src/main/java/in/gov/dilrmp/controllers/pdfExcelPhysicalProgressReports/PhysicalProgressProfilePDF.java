package in.gov.dilrmp.controllers.pdfExcelPhysicalProgressReports;

import in.gov.dilrmp.component.PdfExporterComponent;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.services.physicalProgressServices.*;
import in.gov.dilrmp.utils.PdfExporter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("physcial/report/pdf")
public class PhysicalProgressProfilePDF {

    @Autowired
    StateMAPService stateMAPService;
    @Autowired
    StateCLRService stateCLRService;
    @Autowired
    MapDigitizationReportService mapDigitizationReportService;
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
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    @GetMapping("/nationalProfile")
    public void getnationalPdf(HttpServletResponse response, HttpSession session) {
        List<PdfExporterComponent> pdfexporterList = new ArrayList<PdfExporterComponent>();
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String[] head = {ReportLabels.NATIONAL_PROFILE, " ", " ", "1. " + ReportLabels.CLR_REPORT};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.NATIONAL_PROFILE);
        float[] col_width = {20f, 30f, 50f, 50f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponent.setCol_width(col_width);
        String[] col_head = {ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS,
                ReportLabels.TOTAL_VILLAGES, ReportLabels.RoR, ReportLabels.NUMBER_OF_VILLAGES_WHERE_CLR_COMPLETED,
                ReportLabels.TOTAL_NO_OF_LAND_OWNERS, ReportLabels.AVAILABILITY_OF_GENDER_BASED_LAND_OWNERSHIP,ReportLabels.NUMBER_OF_STATE_WHERE,
                ReportLabels.ROR_AVAILABLE_ONLINE,ReportLabels.DIGITALLY_SIGNED_ROR_AVAILABLE_ONLINE,
                ReportLabels.DIGITALLY_SIGNED_ROR_LEGALLY_VALID_IN_STATE,ReportLabels.MUTATION_APPLICATION_SUBMITTED_ONLINE,
                ReportLabels.AUTO_TRIGGERED_MUTATION_FACILITY_AVAILABLE,ReportLabels.BANKS_AUTHORIZED_TO_CREATE_CLEAR_MORTGAGE_CHARGE_IN_ROR,
                ReportLabels.LAND_RECORDS_BE_CHECKED_ONLINE_BY_SRO,ReportLabels.LAND_RECORDS_BE_CHECKED_ONLINE_BY_REVENUE_COURTS,
                ReportLabels.LAND_RECORDS_BE_CHECKED_ONLINE_BY_CIVIL_COURTS_THROUGH_E_COURTS_SYSTEM,
                ReportLabels.TOTAL,ReportLabels.COMPUTERIZED,ReportLabels.PERCENTAGE,ReportLabels.NO,ReportLabels.PERCENTAGE,
                ReportLabels.NUMBER_OF_DISTRICTS_WHERE_AVAILABLE,ReportLabels.MALE,ReportLabels.FEMALE,ReportLabels.TOTAL,ReportLabels.NO,
                ReportLabels.NO,ReportLabels.NO,ReportLabels.NO,ReportLabels.NO,ReportLabels.NO,
                ReportLabels.NUMBER_OF_DISTRICTS_AUTHORIZED,ReportLabels.NUMBER_OF_BANK_BRANCHES_AUTHORIZED,ReportLabels.NO,ReportLabels.NO,
                ReportLabels.NO
        };
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {5,6,8};
        Integer[] rwspn3col = {0,1,2,3,4,7};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,31,32,33,34,35,36,37};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanvalNational = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanvalNational.add(new ArrayList<Integer>(Arrays.asList(5, 3)));
        colspanvalNational.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        colspanvalNational.add(new ArrayList<Integer>(Arrays.asList(8, 4)));
        colspanvalNational.add(new ArrayList<Integer>(Arrays.asList(9, 11)));
        colspanvalNational.add(new ArrayList<Integer>(Arrays.asList(10, 1)));
        colspanvalNational.add(new ArrayList<Integer>(Arrays.asList(11, 1)));
        colspanvalNational.add(new ArrayList<Integer>(Arrays.asList(12, 1)));
        colspanvalNational.add(new ArrayList<Integer>(Arrays.asList(13, 1)));
        colspanvalNational.add(new ArrayList<Integer>(Arrays.asList(15, 3)));
        colspanvalNational.add(new ArrayList<Integer>(Arrays.asList(16, 1)));
        colspanvalNational.add(new ArrayList<Integer>(Arrays.asList(17, 1)));
        colspanvalNational.add(new ArrayList<Integer>(Arrays.asList(18, 1)));
        pComponent.setColumnspan(colspanvalNational);
        List<List<Integer>> colsBreakNational = new ArrayList<List<Integer>>();
        colsBreakNational.add(new ArrayList<Integer>(Arrays.asList(5, 3)));
        colsBreakNational.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        colsBreakNational.add(new ArrayList<Integer>(Arrays.asList(8, 4)));
        colsBreakNational.add(new ArrayList<Integer>(Arrays.asList(14, 3)));
        pComponent.setColumnnumber(colsBreakNational);
        pComponent.setColumnBreakCountNo(8);
        //List<Object[]> clrList = (List<Object[]>) session.getAttribute("clrdata");
        List<StateClrReportView> clrList = stateCLRService.getStateClrReportsGrandToatal();
        List<List<String>> reportDataList = new ArrayList<>(); // Initialize reportDataList

        if (clrList != null && !clrList.isEmpty()) {
            int serialNo = 1;
            for (StateClrReportView obj : clrList) {
                List<String> rowData = new ArrayList<>();
                rowData.add(String.valueOf(serialNo++));
                rowData.add("36");
                rowData.add(obj.getTotalDistrict() != null ? String.valueOf(obj.getTotalDistrict()) : "0");
                rowData.add(obj.getTotalTehsils() != null ? obj.getTotalTehsils().toString() : "0");
                rowData.add(obj.getTotalVillages() != null ? obj.getTotalVillages().toString() : "0");
                rowData.add(obj.getTotalRor() != null ? obj.getTotalRor().toString() : "0");
                rowData.add(obj.getRorComputerized() != null ? obj.getRorComputerized().toString() : "0");
                rowData.add(obj.getRorComputerizedPercent() != null ? obj.getRorComputerizedPercent().toString() : "0.0");
                rowData.add(obj.getVillagesClrCompleted() != null ? obj.getVillagesClrCompleted().toString() : "0");
                rowData.add(obj.getClrCompletionPercent() != null ? obj.getClrCompletionPercent().toString() : "0.0");
                rowData.add(obj.getTotalLandOwners() != null ? obj.getTotalLandOwners().toString() : "0");
                rowData.add(obj.getDistrictsWithGenderBasedOwnership() != null ? obj.getDistrictsWithGenderBasedOwnership().toString() : "0");
                rowData.add(obj.getTotalMaleLandOwners() != null ? obj.getTotalMaleLandOwners().toString() : "0");
                rowData.add(obj.getTotalFemaleLandOwners() != null ? obj.getTotalFemaleLandOwners().toString() : "0");
                rowData.add(obj.getTotalOwners() != null ? obj.getTotalOwners().toString() : "0");

                // Add boolean flags for online/digital availability, using "No" as default
                rowData.add(obj.getRorAvailableOnline() != null ? obj.getRorAvailableOnline() : "0");
                rowData.add(obj.getDigitallySignedRorAvailable() != null ? obj.getDigitallySignedRorAvailable() : "0");
                rowData.add(obj.getDigitallySignedRorLegallyValid() != null ? obj.getDigitallySignedRorLegallyValid() : "0");
                rowData.add(obj.getOnlineMutationFacility() != null ? obj.getOnlineMutationFacility() : "0");
                rowData.add(obj.getAutoTriggerMutation() != null ? obj.getAutoTriggerMutation() : "0");
                rowData.add(obj.getBankRedFlagMortgageInLandRecords() != null ? obj.getBankRedFlagMortgageInLandRecords() : "0");
                rowData.add(obj.getDistrictsWithBankRedFlagMortgage() != null ? obj.getDistrictsWithBankRedFlagMortgage().toString() : "0");
                rowData.add(obj.getBankBranchesWithRedFlagMortgage() != null ? obj.getBankBranchesWithRedFlagMortgage().toString() : "0");
                rowData.add(obj.getLandRecordsOnlineFromRegistrationSystem() != null ? obj.getLandRecordsOnlineFromRegistrationSystem() : "0");
                rowData.add(obj.getRevenueCourtProceedingsPaperless() != null ? obj.getRevenueCourtProceedingsPaperless() : "0");
                rowData.add(obj.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() != null ? obj.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() : "0");
                reportDataList.add(rowData);
            }
            pComponent.setReportDataList(reportDataList);
        } else {
            System.out.println("No CLR data found.");
        }


        PdfExporterComponent pComponentMapDigitization = new PdfExporterComponent();
        String[] head1 = {" ", " ", "", "", "", " ", " ", "", "", "", "2. " + ReportLabels.MAP_DIGITIZATION_REPORT};
        pComponentMapDigitization.setReportHeading(head1);
        pComponentMapDigitization.setReportName(ReportLabels.MAP_DIGITIZATION_REPORT);
        float[] col_width1 = {30f, 90f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f,40f,40f, 40f, 50f, 50f, 50f, 40f, 50f, 40f, 40f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f,50f};
        pComponentMapDigitization.setCol_width(col_width1);
        String[] col_head1 = {ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.NO_OF_CADASTRAL_MAPS_FMBS_TIPPANS, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS
                , ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.NO_OF_LAND_PARCELS,ReportLabels.NUMBER_OF_DISTRICT_WHERE_CADMAP_FMB_TIPPAN_SHOWING_CURRENT_OWNERSHIP,
                ReportLabels.CADASTRAL_MAPS, ReportLabels.FMBs, ReportLabels.TIPPANS, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.GEO_REFERENCED, ReportLabels.TOTAL, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_LINKED_TO_ROR, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_GEO_REFERENCED, ReportLabels.ULPIN_ASSIGNED, ReportLabels.TOTAL, ReportLabels.GEO_REFERENCED, ReportLabels.ULPIN_ASSIGNED,
                ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO,ReportLabels.PERCENTAGE,ReportLabels.NO,ReportLabels.PERCENTAGE};
        pComponentMapDigitization.setCol_head(col_head1);
        Integer[] rwspn2col1 = {8};
        Integer[] rwspn3col1 = {0, 1, 2, 3};
        Integer[] rwspn4col1 = {};
        Integer[] rwspn5col1 = {};
        Integer[] simplecell1 = {22,23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46,47,48,49,50};
        pComponentMapDigitization.setRowspn5(rwspn5col1);
        pComponentMapDigitization.setRowspn4(rwspn4col1);
        pComponentMapDigitization.setRowspn3(rwspn3col1);
        pComponentMapDigitization.setRowspn2(rwspn2col1);
        pComponentMapDigitization.setRowspan1(simplecell1);
        List<List<Integer>> colspanval1 = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(4, 9)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(5, 5)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(6, 7)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(7, 5)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(9, 3)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(10, 3)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(11, 3)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(12, 1)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(13, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(14, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(15, 1)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(16, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(17, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(18, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(19, 1)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(20, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(21, 2)));
        pComponentMapDigitization.setColumnspan(colspanval1);
        List<List<Integer>> colsBreak1 = new ArrayList<List<Integer>>();
        // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(5, 3)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(6, 3)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(9, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(11, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(12, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(13, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(15, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(16, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(17, 2)));
        pComponentMapDigitization.setColumnnumber(colsBreak1);
        pComponentMapDigitization.setColumnBreakCountNo(14);
        List<List<String>> reportDataList1 = new ArrayList<>();
        //List<MapDigitizationReport> mapList = (List<MapDigitizationReport>) session.getAttribute("reportData");
        List<MapDigitizationReport> mapResultList = mapDigitizationReportService.getStateMapReportsGrandToatal();
        if (mapResultList != null && !mapResultList.isEmpty()) {
            for (MapDigitizationReport obj : mapResultList) {
                List<String> rowData = new ArrayList<>();
                rowData.add("1");
                if (obj != null) {
                    rowData.add("36");
                    rowData.add(obj.getTotalDistrict() != null ? String.valueOf(obj.getTotalDistrict()) : "0");
                    rowData.add(obj.getTotalTehsils() != null ? obj.getTotalTehsils().toString() : "0");
                    rowData.add(obj.getTotalCadastralMaps() != null ? obj.getTotalCadastralMaps().toString() : "0");
                    rowData.add(obj.getDigitizedCadastralMaps() != null ? obj.getDigitizedCadastralMaps().toString() : "0");
                    rowData.add(obj.getDigitizedCadastralMapsPercent() != null ? obj.getDigitizedCadastralMapsPercent().toString() : "0.0");
                    rowData.add(obj.getTotalFmbs() != null ? obj.getTotalFmbs().toString() : "0");
                    rowData.add(obj.getDigitizedFmbs() != null ? obj.getDigitizedFmbs().toString() : "0");
                    rowData.add(obj.getDigitizedFmbsPercent() != null ? obj.getDigitizedFmbsPercent().toString() : "0.0");
                    rowData.add(obj.getTotalTippans() != null ? obj.getTotalTippans().toString() : "0");
                    rowData.add(obj.getDigitizedTippans() != null ? obj.getDigitizedTippans().toString() : "0");
                    rowData.add(obj.getDigitizedTippansPercent() != null ? obj.getDigitizedTippansPercent().toString() : "0.0");
                    rowData.add(obj.getTotalMapsFmbTippans() != null ? obj.getTotalMapsFmbTippans().toString() : "0");
                    rowData.add(obj.getTotalDigitizedMapsFmbTippans() != null ? obj.getTotalDigitizedMapsFmbTippans().toString() : "0");
                    rowData.add(obj.getDigitizedMapsFmbTippansPercent() != null ? obj.getDigitizedMapsFmbTippansPercent().toString() : "0.0");
                    rowData.add(obj.getGeoreferencedMaps() != null ? obj.getGeoreferencedMaps().toString() : "0");
                    rowData.add(obj.getGeoreferencedMapsPercent() != null ? obj.getGeoreferencedMapsPercent().toString() : "0.0");
                    rowData.add(obj.getTotalVillages() != null ? obj.getTotalVillages().toString() : "0");
                    rowData.add(obj.getVillagesLinkedWithRor() != null ? obj.getVillagesLinkedWithRor().toString() : "0");
                    rowData.add(obj.getVillagesLinkedWithRorPercent() != null ? obj.getVillagesLinkedWithRorPercent().toString() : "0.0");
                    rowData.add(obj.getVillagesGeoreferenced() != null ? obj.getVillagesGeoreferenced().toString() : "0");
                    rowData.add(obj.getVillagesGeoreferencedPercent() != null ? obj.getVillagesGeoreferencedPercent().toString() : "0.0");
                    rowData.add(obj.getVillagesWithUlipn() != null ? obj.getVillagesWithUlipn().toString() : "0");
                    rowData.add(obj.getVillagesWithUlipnPercent() != null ? obj.getVillagesWithUlipnPercent().toString() : "0.0");
                    rowData.add(obj.getTotalLandParcels() != null ? obj.getTotalLandParcels().toString() : "0");
                    rowData.add(obj.getGeoreferencedLandParcels() != null ? obj.getGeoreferencedLandParcels().toString() : "0");
                    rowData.add(obj.getGeoreferencedLandParcelsPercent() != null ? obj.getGeoreferencedLandParcelsPercent().toString() : "0.0");
                    rowData.add(obj.getLandParcelsWithUlipn() != null ? obj.getLandParcelsWithUlipn().toString() : "0");
                    rowData.add(obj.getLandParcelsWithUlipnPercent() != null ? obj.getLandParcelsWithUlipnPercent().toString() : "0.0");
                    rowData.add(obj.getMapsUpdatedBasedOnMutation() != null ? obj.getMapsUpdatedBasedOnMutation() : "0");
                    rowData.add(obj.getMapsUpdatedBasedOnMutationPercentage() != null ? obj.getMapsUpdatedBasedOnMutationPercentage() : "0");

                }
                reportDataList1.add(rowData);
            }

            pComponentMapDigitization.setReportDataList(reportDataList1);

        }

        // SRO details
        PdfExporterComponent pComponentRoR = new PdfExporterComponent();
        String[] head5 = {"", "3. " + ReportLabels.SRO_REPORT};
        pComponentRoR.setReportHeading(head5);
        pComponentRoR.setReportName(ReportLabels.SRO_REPORT);
        float[] col_width5 = {30f, 75f, 55f, 55f, 55f, 60f, 60f, 75f, 60f, 60f, 60f, 60f, 60f, 60f,60f,60f,60f,60f,60f,60f,60f,60f,60f,60f,60f};
        pComponentRoR.setCol_width(col_width5);
        String[] col_head5 = {
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.TOTAL_STATE_UT,
                ReportLabels.TOTAL_DISTRICTS,
                ReportLabels.NUMBER_OF_SRO_IN_THE_STATE,
                ReportLabels.NUMBER_SRO_USING_ONLINE_REGISTRATION,
                ReportLabels.NUMBER_OF_STATE_WHERE,
                ReportLabels.REGISTRATION_SYSTEM_WITH_OPTION,
                ReportLabels.RATES_PUBLIC_REGISTRATION_SYSTEM,
                ReportLabels.FREE_CALCULATION_DONE_AUTOMATICALLY_REGISTRATION_SYSTEM,
                ReportLabels.REGISTRATION_SYSTEM_INTEGRATED_E_STAMP,
                ReportLabels.OPTION_AVAILABLE_REFUND_PAID_FEE,
                ReportLabels.OPTION_AVAILABLE_REGISTRATION_SYSTEM,
                ReportLabels.FACILITY_ONLINE_VERIFICATION_DOCUMENT,
                ReportLabels.SIGN_OBTAINED_FROM_PARTIES,
                ReportLabels.DIGITAL_SIGNATURE_ENABLED_REGISTRATION,
                ReportLabels.OPTION_OF_EKYC_AVAILABLE_REGISTRATION_SYSTEM,
                ReportLabels.OPTION_AVAILABLE_REGISTRATION_SYSTEM_VEDIO,
                ReportLabels.HOME_VISIT_MODULE_AVAILABLE_REGISTRATION_SYSTEM,
                ReportLabels.EMAIL_AUTOMATICALLY_PARTIES_IMPORTANT,
                ReportLabels.REGI_SENT_AUTOMATICALLY_DIGITAL,
                ReportLabels.PROVISION_ONLINE_GRIEVANCE_REDRESSAL,
                ReportLabels.OPTION_AVAILABLE_REGISTRATION_SYSTEM_FETCH,
                ReportLabels.REGIS_SYSTEM_FACILITY_AUTO_TRIGGER,
                ReportLabels.PENDENCY_OF_REVENUE_COURT_CASE,
                ReportLabels.AADHA_PAN_FINGERPRINT_DETAILS_MASKED,
                ReportLabels.FACIL_TO_STORE_REGISTERED_DOCUMENT
        };
        pComponentRoR.setCol_head(col_head5);
        Integer[] rwspn2col5 = {0, 1, 2,3,4};
        Integer[] rwspn3col5 = {};
        Integer[] rwspn4col5 = {};
        Integer[] rwspn5col5 = {};
        Integer[] simplecell5 = {6,7,8,9,10,11,12,13,14,15,16,18,19,20,21,22,23,24,25};
        pComponentRoR.setRowspn5(rwspn5col5);
        pComponentRoR.setRowspn4(rwspn4col5);
        pComponentRoR.setRowspn3(rwspn3col5);
        pComponentRoR.setRowspn2(rwspn2col5);
        pComponentRoR.setRowspan1(simplecell5);
        List<List<Integer>> colspanval5 = new ArrayList<List<Integer>>();
        colspanval5.add(new ArrayList<Integer>(Arrays.asList(5, 20)));
        pComponentRoR.setColumnspan(colspanval5);
        List<List<Integer>> colsBreak5 = new ArrayList<List<Integer>>();
        pComponentRoR.setColumnnumber(colsBreak5);
        pComponentRoR.setColumnBreakCountNo(0);
        List<List<String>> reportDataList5 = new ArrayList<>();
        List<SroReportDTO> rorList = stateSROService.getStateSroReportsGrandToatal();
        if (rorList != null && !rorList.isEmpty()) {
            for (SroReportDTO obj : rorList) {
                List<String> rowData = new ArrayList<>();
                rowData.add("1");
                if (obj != null) {
                    rowData.add("36");
                    rowData.add(obj.getTotalDistricts() != null ? String.valueOf(obj.getTotalDistricts()) : "0");
                    rowData.add(obj.getNumberOfSROsInState() != null ? obj.getNumberOfSROsInState().toString() : "0");
                    rowData.add(obj.getNumberOfSROsUsingOnlineRegistration() != null ? obj.getNumberOfSROsUsingOnlineRegistration().toString() : "0");
                    rowData.add(obj.getStandardDeedTemplatesAvailable() != null ? obj.getStandardDeedTemplatesAvailable() : "0");
                    rowData.add(obj.getCircleRatesVisibleToPublic() != null ? obj.getCircleRatesVisibleToPublic() : "0");
                    rowData.add(obj.getAutomaticFeeCalculation() != null ? obj.getAutomaticFeeCalculation() : "0");
                    rowData.add(obj.getIntegrationWithEStampSystem() != null ? obj.getIntegrationWithEStampSystem() : "0");
                    rowData.add(obj.getRefundOptionAvailable() != null ? obj.getRefundOptionAvailable() : "0");
                    rowData.add(obj.getUploadSupportingDocumentsOption() != null ? obj.getUploadSupportingDocumentsOption() : "0");
                    rowData.add(obj.getOnlineVerificationOfDocuments() != null ? obj.getOnlineVerificationOfDocuments() : "0");
                    rowData.add(obj.getSignaturesObtainedDigitally() != null ? obj.getSignaturesObtainedDigitally() : "0");
                    rowData.add(obj.getDigitalSignatureEnabledForSRO() != null ? obj.getDigitalSignatureEnabledForSRO() : "0");
                    rowData.add(obj.getEkycOptionAvailable() != null ? obj.getEkycOptionAvailable() : "0");
                    rowData.add(obj.getVideoConferencingOptionAvailable() != null ? obj.getVideoConferencingOptionAvailable() : "0");
                    rowData.add(obj.getHomeVisitModuleAvailable() != null ? obj.getHomeVisitModuleAvailable() : "0");
                    rowData.add(obj.getAutomaticEmailNotifications() != null ? obj.getAutomaticEmailNotifications() : "0");
                    rowData.add(obj.getDigitalFormatRegisteredDocuments() != null ? obj.getDigitalFormatRegisteredDocuments() : "0");
                    rowData.add(obj.getOnlineGrievanceRedressalSystem() != null ? obj.getOnlineGrievanceRedressalSystem() : "0");
                    rowData.add(obj.getFetchPropertyDetailsFromLandRecords() != null ? obj.getFetchPropertyDetailsFromLandRecords() : "0");
                    rowData.add(obj.getAutoTriggerMutationAvailable() != null ? obj.getAutoTriggerMutationAvailable() : "0");
                    rowData.add(obj.getRevenueCourtCaseRedFlagged() != null ? obj.getRevenueCourtCaseRedFlagged() : "0");
                    rowData.add(obj.getMaskingSensitiveDetailsInRegisteredDeeds() != null ? obj.getMaskingSensitiveDetailsInRegisteredDeeds() : "No");
                    rowData.add(obj.getStoreDocumentsInDigilocker() != null ? obj.getStoreDocumentsInDigilocker() : "0");

                }
                reportDataList5.add(rowData);

            }
            pComponentRoR.setReportDataList(reportDataList5);
        }

        // Status of Cadastral Survey pdf
        PdfExporterComponent pComponentSurveyResurvey = new PdfExporterComponent();
        String[] head4 = {"","","", " ", " ", "", "", "4. " + ReportLabels.SURVEY_REPORT};
        pComponentSurveyResurvey.setReportHeading(head4);
        pComponentSurveyResurvey.setReportName(ReportLabels.SURVEY_REPORT);
        float[] col_width4 = {20f, 65f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponentSurveyResurvey.setCol_width(col_width4);
        String[] col_head4 = {ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS,ReportLabels.TOTAL_VILLAGES, ReportLabels.TOTAL_RURAL_REVENUE_AREA_SQ_KM, ReportLabels.AREA_SANCTIONED_FOR_SURVEY_RE_SURVEY_SQ_KM,ReportLabels.DRON_FLYING_SURVEY,ReportLabels.NUMBER_OF_VILLAGES,ReportLabels.AREA_OF_VILLAGES_WHERE_SURVEY_SANCTIONED_NOT_STARTED,
                ReportLabels.NUMBER_OF_VILLAGES,ReportLabels.AREA_OF_VILLAGES_SQ_KM,ReportLabels.MAP_1_IS_GENERATED,ReportLabels.DRAFT_MAP_PUBLISHED,ReportLabels.FINAL_PROMULGATION_DONE,ReportLabels.SURVEY_SANCTIONED_NOT_STARTED,};
        pComponentSurveyResurvey.setCol_head(col_head4);
        Integer[] rwspn2col4 = {0, 1, 2, 3, 4, 5,6,9};
        Integer[] rwspn3col4 = {};
        Integer[] rwspn4col4 = {};
        Integer[] rwspn5col4 = {};
        Integer[] simplecell4 = {10, 11, 12, 13, 14, 15};
        pComponentSurveyResurvey.setRowspn5(rwspn5col4);
        pComponentSurveyResurvey.setRowspn5(rwspn5col4);
        pComponentSurveyResurvey.setRowspn4(rwspn4col4);
        pComponentSurveyResurvey.setRowspn3(rwspn3col4);
        pComponentSurveyResurvey.setRowspn2(rwspn2col4);
        pComponentSurveyResurvey.setRowspan1(simplecell4);
        List<List<Integer>> colspanval4 = new ArrayList<List<Integer>>();
        colspanval4.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colspanval4.add(new ArrayList<Integer>(Arrays.asList(8, 4)));
        pComponentSurveyResurvey.setColumnspan(colspanval4);
        List<List<Integer>> colsBreak4 = new ArrayList<List<Integer>>();
        colsBreak4.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colsBreak4.add(new ArrayList<Integer>(Arrays.asList(8, 4)));
        pComponentSurveyResurvey.setColumnnumber(colsBreak4);
        pComponentSurveyResurvey.setColumnBreakCountNo(4);
        List<List<String>> reportDataList4 = new ArrayList<>();
        List<SurveyResurveyViewReport> surveyList = stateSurveyService.getStateSurveyReportsGrandToatal();

        if (surveyList != null && !surveyList.isEmpty()) {
            int serialNo = 1;
            for (SurveyResurveyViewReport obj : surveyList) {
                List<String> rowData = new ArrayList<>();
                rowData.add(String.valueOf(serialNo++));
                rowData.add("36");
                rowData.add(obj.getTotalDistrict() != null ? obj.getTotalDistrict().toString() : "0");
                rowData.add(obj.getTotalTehsils() != null ? obj.getTotalTehsils().toString() : "0");
                rowData.add(obj.getTotalVillages() != null ? obj.getTotalVillages().toString() : "0");
                rowData.add(obj.getFormatetotalRuralRevenueArea() != null ? obj.getFormatetotalRuralRevenueArea().toString() : "0.0");
                rowData.add(obj.getFomateareaSanctionedForSurvey() != null ? obj.getFomateareaSanctionedForSurvey().toString() : "0.0");
                rowData.add(obj.getVillagesDroneFlyingCompleted() != null ? obj.getVillagesDroneFlyingCompleted().toString() : "0.0");
                rowData.add(obj.getFormateareaDroneFlyingCompleted() != null ? obj.getFormateareaDroneFlyingCompleted().toString() : "0.0");
                rowData.add(obj.getVillagesMap1Generated() != null ? obj.getVillagesMap1Generated().toString() : "0");
                rowData.add(obj.getVillagesDraftMapPublished() != null ? obj.getVillagesDraftMapPublished().toString() : "0");
                rowData.add(obj.getVillagesFinalPromulgationDone() != null ? obj.getVillagesFinalPromulgationDone().toString() : "0");
                rowData.add(obj.getVillagesSurveySanctionNotStarted() != null ? obj.getVillagesSurveySanctionNotStarted().toString() : "0");
                rowData.add(obj.getFormateareaSurveySanctionNotStarted() != null ? obj.getFormateareaSurveySanctionNotStarted().toString() : "0.0");

                reportDataList4.add(rowData);
            }
            pComponentSurveyResurvey.setReportDataList(reportDataList4);
        } else {
            System.out.println("No survey data found.");
        }
        //RCMS Reports pdf
        PdfExporterComponent pComponentRCMS = new PdfExporterComponent();
        String[] head3 = {"", "5. " + ReportLabels.RCMS_REPORT};
        pComponentRCMS.setReportHeading(head3);
        pComponentRCMS.setReportName(ReportLabels.RCMS_REPORT);
        float[] col_width3 = {20f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 70f, 70f, 70f,70f,70f,70f,70f,70f,70f,70f,70f};
        pComponentRCMS.setCol_width(col_width3);
        String[] col_head3 = {
                ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.NUMBER_OF_REVENUE_COURTS,ReportLabels.NUMBER_OF_STATE_WHERE,
                ReportLabels.TOTAL, ReportLabels.COMPUTERIZED_ONLINE,ReportLabels.COURT_MANAGEMENT_SYSTEM,ReportLabels.ONLINE_PROCESS_OF_REVENUE_COURT_PROCESSES_AVAILABLE,ReportLabels.PROCEEDINGCOMPLETE_PROCEEDS,ReportLabels.LAND_RECORD_CHECKED_ONLINE_COURT, ReportLabels.FILING_DIRECTLY_SYSTEM,
                ReportLabels.NO,ReportLabels.NO,ReportLabels.PERCENTAGE,ReportLabels.ATTACHED_DOCUMENTS,ReportLabels.CAUSE_LIST_GENERATION, ReportLabels.DEFENDANT_NOTIFICATION, ReportLabels.COUNTER_AFFIDAVIT_FILING, ReportLabels.PROCEEDINGS_TYPING_SYSTEM,ReportLabels.UPLOADING_COURT_ORDERS_NOTIFICATION, ReportLabels.LAND_RECORD_INTEGRATION, ReportLabels.ONLINE_LAND_RECORD_CHECK
        };
        pComponentRCMS.setCol_head(col_head3);
        Integer[] rwspn2col3 = {8,10,11,12};
        Integer[] rwspn3col3 = {0,1,2,3};
        Integer[] rwspn4col3 = {};
        Integer[] rwspn5col3 = {};
        Integer[] simplecell3 = {13, 14, 15,16,17,18,19,20,21,22,23};
        pComponentRCMS.setRowspn5(rwspn5col3);
        pComponentRCMS.setRowspn4(rwspn4col3);
        pComponentRCMS.setRowspn3(rwspn3col3);
        pComponentRCMS.setRowspn2(rwspn2col3);
        pComponentRCMS.setRowspan1(simplecell3);
        List<List<Integer>> colspanval3 = new ArrayList<List<Integer>>();
        colspanval3.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
        colspanval3.add(new ArrayList<Integer>(Arrays.asList(5, 12)));
        colspanval3.add(new ArrayList<Integer>(Arrays.asList(6, 1)));
        colspanval3.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colspanval3.add(new ArrayList<Integer>(Arrays.asList(9, 8)));
        pComponentRCMS.setColumnspan(colspanval3);
        List<List<Integer>> colsBreak3 = new ArrayList<List<Integer>>();
        colsBreak3.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
        colsBreak3.add(new ArrayList<Integer>(Arrays.asList(6, 8)));
        pComponentRCMS.setColumnnumber(colsBreak3);
        pComponentRCMS.setColumnBreakCountNo(9);
        List<List<String>> reportDataList3 = new ArrayList<>();
        List<RcmsReportDTO> rcmsList = stateRCMSService.getStateRcmsReportsGrandToatal();
        if (rcmsList != null && !rcmsList.isEmpty()) {
            for (RcmsReportDTO obj : rcmsList) {
                List<String> rowData = new ArrayList<>();
                rowData.add("1");
                if (obj != null) {
                    rowData.add("36");
                    rowData.add(obj.getTotalDistrict() != null ? String.valueOf(obj.getTotalDistrict()) : "0");
                    rowData.add(obj.getTotalTehsils() != null ? obj.getTotalTehsils().toString() : "0");
                    rowData.add(obj.getTotalRevenueCourts() != null ? obj.getTotalRevenueCourts().toString() : "0");
                    rowData.add(obj.getRevenueCourtsComputerized() != null ? obj.getRevenueCourtsComputerized().toString() : "0");
                    rowData.add(obj.getRevenueCourtsComputerizedPercent() != null ? obj.getRevenueCourtsComputerizedPercent().toString() : "0.0");
                    rowData.add(obj.getERcmsAvailable() != null ? obj.getERcmsAvailable() : "Not Available");
                    rowData.add(obj.getAffidavitFilingOnline() != null ? obj.getAffidavitFilingOnline() : "Not Available");
                    rowData.add(obj.getCauseListGenerationOnline() != null ? obj.getCauseListGenerationOnline() : "Not Available");
                    rowData.add(obj.getNoticeToDefendantsOnline() != null ? obj.getNoticeToDefendantsOnline() : "Not Available");
                    rowData.add(obj.getCounterAffidavitFilingOnline() != null ? obj.getCounterAffidavitFilingOnline() : "Not Available");
                    rowData.add(obj.getProceedingsTypingDirectlyOnline() != null ? obj.getProceedingsTypingDirectlyOnline() : "Not Available");
                    rowData.add(obj.getUploadingRevenueCourtOrdersOnline() != null ? obj.getUploadingRevenueCourtOrdersOnline() : "Not Available");
                   
                    rowData.add(obj.getLandRecordsOnlineFromRevenueCourtSystem() != null ? obj.getLandRecordsOnlineFromRevenueCourtSystem() : "Not Available");
                    rowData.add(obj.getRevenueCourtProceedingsPaperless() != null ? obj.getRevenueCourtProceedingsPaperless() : "Not Available");
                    rowData.add(obj.getLandRecordsOnlineForCivilCourts() != null ? obj.getLandRecordsOnlineForCivilCourts() : "Not Available");
                    rowData.add(obj.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() != null ? obj.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() : "Not Available");
                    rowData.add(obj.getCaseFilingRedFlaggedInLandRecords() != null ? obj.getCaseFilingRedFlaggedInLandRecords() :"Not Available");
                }
                reportDataList3.add(rowData);
            }
            pComponentRCMS.setReportDataList(reportDataList3);
        }
        //Aadhar link Reports pdf
        PdfExporterComponent pComponentAadhar = new PdfExporterComponent();
        String[] head7 = {"", "6. " + ReportLabels.AADHAR_REPORT};
        pComponentAadhar.setReportHeading(head7);
        pComponentAadhar.setReportName(ReportLabels.AADHAR_REPORT);
        float[] col_width7 = {20f, 65f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponentAadhar.setCol_width(col_width7);
        String[] col_head7 =  {ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.RoR,
                ReportLabels.TOTAL,ReportLabels.WHERE_AT_LEAST_ONE_RoR_LINKED_WITH_AADHAAR,ReportLabels.WHERE_100_PERCENT_RoR_LINKED_WITH_AADHAAR,ReportLabels.TOTAL,ReportLabels.LINKED_WITH_AADHAAR,ReportLabels.LINKED_WITH_MOBILE_NUMBER,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO,ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO, ReportLabels.PERCENTAGE};
        pComponentAadhar.setCol_head(col_head7);
        Integer[] rwspn2col7 = {6,7,8, 9,10,11};
        Integer[] rwspn3col7 = {};
        Integer[] rwspn4col7 = {0, 1, 2, 3};
        Integer[] rwspn5col7 = {};
        Integer[] simplecell7 = {12,13,14,15,16, 17, 18, 19, 20, 21};
        pComponentAadhar.setRowspn5(rwspn5col7);
        pComponentAadhar.setRowspn4(rwspn4col7);
        pComponentAadhar.setRowspn3(rwspn3col7);
        pComponentAadhar.setRowspn2(rwspn2col7);
        pComponentAadhar.setRowspan1(simplecell7);
        List<List<Integer>> colspanval7 = new ArrayList<List<Integer>>();
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(4, 5)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(5, 5)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(6, 1)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(9, 1)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(10, 2)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(11, 2)));
        pComponentAadhar.setColumnspan(colspanval7);
        List<List<Integer>> colsBreak7 = new ArrayList<List<Integer>>();
        colsBreak7.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak7.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        colsBreak7.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        colsBreak7.add(new ArrayList<Integer>(Arrays.asList(9, 2)));
        pComponentAadhar.setColumnnumber(colsBreak7);
        pComponentAadhar.setColumnBreakCountNo(4);
        List<List<String>> reportDataListAadhar = new ArrayList<>();
        List<LinkedAadharViewReport> aadharResultList = stateAadharService.getStateLinkedAadhaarReportsGrandToatal();
        if (aadharResultList != null && !aadharResultList.isEmpty()) {
            for (LinkedAadharViewReport obj : aadharResultList) {
                List<String> rowData = new ArrayList<>();
                rowData.add("1");
                if (obj != null) {
                    rowData.add("36");
                    rowData.add(obj.getTotalDistrict() != null ? String.valueOf(obj.getTotalDistrict()) : "0");
                    rowData.add(obj.getTotalTehsils() != null ? obj.getTotalTehsils().toString() : "0");
                    rowData.add(obj.getTotalVillages() != null ? obj.getTotalVillages().toString() : "0");
                    rowData.add(obj.getVillagesWithRorLinkedAadhaar() != null ? obj.getVillagesWithRorLinkedAadhaar().toString() : "0");
                    rowData.add(obj.getVillagesWithRorLinkedAadhaarPercent() != null ? obj.getVillagesWithRorLinkedAadhaarPercent().toString() : "0.0");
                    rowData.add(obj.getVillagesWith100PercentRorLinkedAadhaar() != null ? obj.getVillagesWith100PercentRorLinkedAadhaar().toString() : "0");
                    rowData.add(obj.getVillagesWith100PercentRorLinkedAadhaarPercent() != null ? obj.getVillagesWith100PercentRorLinkedAadhaarPercent().toString() : "0.0");
                    rowData.add(obj.getTotalRor() != null ? obj.getTotalRor().toString() : "0");
                    rowData.add(obj.getRorLinkedWithAadhaar() != null ? obj.getRorLinkedWithAadhaar().toString() : "0");
                    rowData.add(obj.getRorLinkedWithAadhaarPercent() != null ? obj.getRorLinkedWithAadhaarPercent().toString() : "0.0");
                    rowData.add(obj.getRorLinkedWithMobileNumber() != null ? obj.getRorLinkedWithMobileNumber().toString() : "0");
                    rowData.add(obj.getRorLinkedWithMobileNumberPercent() != null ? obj.getRorLinkedWithMobileNumberPercent().toString() : "0.0");
                }
                reportDataListAadhar.add(rowData);
            }
            pComponentAadhar.setReportDataList(reportDataListAadhar);

        }
        // MRR report pdf
        PdfExporterComponent pComponentMrr = new PdfExporterComponent();
        String[] head2 = {"", "7. " + ReportLabels.MRR_REPORT};
        pComponentMrr.setReportHeading(head2);
        pComponentMrr.setReportName(ReportLabels.MRR_REPORT);
        float[] col_width2 = {20f, 65f, 50f, 50f, 90f, 50f, 50f, 50f, 50f,50f};
        pComponentMrr.setCol_width(col_width2);
        String[] col_head2 = {ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_STATE_UT, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.MRR_SANCTIONED, ReportLabels.MRR_COMPLETED_OUT_OF_TOTAL, ReportLabels.MRR_COMPLETED_OUT_OF_SANCTIONED
                , ReportLabels.NO,ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE};
        pComponentMrr.setCol_head(col_head2);
        Integer[] rwspn2col2 = {0, 1, 2, 3};
        Integer[] rwspn3col2 = {};
        Integer[] rwspn4col2 = {};
        Integer[] rwspn5col2 = {};
        Integer[] simplecell2 = {7, 8, 9, 10, 11,12};
        pComponentMrr.setRowspn5(rwspn5col2);
        pComponentMrr.setRowspn4(rwspn4col2);
        pComponentMrr.setRowspn3(rwspn3col2);
        pComponentMrr.setRowspn2(rwspn2col2);
        pComponentMrr.setRowspan1(simplecell2);
        List<List<Integer>> colspanval2 = new ArrayList<List<Integer>>();
        colspanval2.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colspanval2.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colspanval2.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        pComponentMrr.setColumnspan(colspanval2);
        List<List<Integer>> colsBreak2 = new ArrayList<List<Integer>>();
        colsBreak2.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak2.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak2.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        pComponentMrr.setColumnnumber(colsBreak2);
        pComponentMrr.setColumnBreakCountNo(3);
        List<List<String>> reportDataList2 = new ArrayList<>();
        List<MrrViewReport> mrrResultList = stateMRRService.getStateMrrReportsGrandToatal();

        if (mrrResultList != null && !mrrResultList.isEmpty()) {
            int serialNo = 1;
            for (MrrViewReport obj : mrrResultList) {
                List<String> rowData = new ArrayList<>();
                rowData.add(String.valueOf(serialNo++));
                rowData.add("36");
                rowData.add(obj.getTotalDistrict() != null ? obj.getTotalDistrict().toString() : "0");
                rowData.add(obj.getTotalTehsils() != null ? obj.getTotalTehsils().toString() : "0");
                rowData.add(obj.getMrrSanctioned() != null ? obj.getMrrSanctioned().toString() : "0");
                rowData.add(obj.getMrrSanctionedPercent() != null ? obj.getMrrSanctionedPercent().toString() : "0.0");
                rowData.add(obj.getMrrCompleted() != null ? obj.getMrrCompleted().toString() : "0");
                rowData.add(obj.getMrrCompletedOutOfTotalTehsilsPercent() != null ? obj.getMrrCompletedOutOfTotalTehsilsPercent().toString() : "0.0");
                rowData.add(obj.getMrrCompleted() != null ? obj.getMrrCompleted().toString() : "0");
                rowData.add(obj.getMrrCompletedOutOfSanctionedPercent() != null ? obj.getMrrCompletedOutOfSanctionedPercent().toString() : "0.0");
                reportDataList2.add(rowData);
            }
            pComponentMrr.setReportDataList(reportDataList2);
        } else {
            System.out.println("No MRR data found.");
        }


        //All adding PDF in pdfexporterList
        pdfexporterList.add(pComponent);
        pdfexporterList.add(pComponentMapDigitization);
        pdfexporterList.add(pComponentRoR);
        pdfexporterList.add(pComponentSurveyResurvey);
        pdfexporterList.add(pComponentRCMS);
        pdfexporterList.add(pComponentAadhar);
        pdfexporterList.add(pComponentMrr);
        PdfExporter.createPdf(pdfexporterList, response);
    }




    //StateProfile PDF function:
    @GetMapping("/stateProfile")
    public void getStateProfilePdf(HttpServletResponse response, HttpSession session) {
        List<PdfExporterComponent> pdfexporterList = new ArrayList<PdfExporterComponent>();
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String stateName = (String) session.getAttribute("stateName");
        String[] head = {ReportLabels.STATE_PROFILE, stateName, " ", " ", "1. " + ReportLabels.CLR_REPORT};
        pComponent.setReportHeading(head);
        pComponent.setReportName(ReportLabels.STATE_PROFILE);
        float[] col_width = {45f, 90f, 50f, 50f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponent.setCol_width(col_width);
        String[] col_head = {ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS,
                ReportLabels.TOTAL_VILLAGES, ReportLabels.RoR, ReportLabels.NUMBER_OF_VILLAGES_WHERE_CLR_COMPLETED,
                ReportLabels.TOTAL_NO_OF_LAND_OWNERS, ReportLabels.AVAILABILITY_OF_GENDER_BASED_LAND_OWNERSHIP,
                ReportLabels.WHETHER_ROR_AVAILABLE_ONLINE,ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_AVAILABLE_ONLINE,
                ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_LEGALLY_VALID_IN_STATE,ReportLabels.WHETHER_MUTATION_APPLICATION_SUBMITTED_ONLINE,
                ReportLabels.WHETHER_AUTO_TRIGGERED_MUTATION_FACILITY_AVAILABLE,ReportLabels.WHETHER_BANKS_AUTHORIZED_TO_CREATE_CLEAR_MORTGAGE_CHARGE_IN_ROR,
                ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_SRO,ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_REVENUE_COURTS,
                ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_CIVIL_COURTS_THROUGH_E_COURTS_SYSTEM,
                ReportLabels.TOTAL,ReportLabels.COMPUTERIZED,ReportLabels.PERCENTAGE,ReportLabels.NO,ReportLabels.PERCENTAGE,
                ReportLabels.NUMBER_OF_DISTRICTS_WHERE_AVAILABLE,ReportLabels.MALE,ReportLabels.FEMALE,ReportLabels.TOTAL,ReportLabels.YES_NO,
                ReportLabels.YES_NO,ReportLabels.YES_NO,ReportLabels.YES_NO,ReportLabels.YES_NO,ReportLabels.YES_NO,
                ReportLabels.NUMBER_OF_DISTRICTS_AUTHORIZED,ReportLabels.NUMBER_OF_BANK_BRANCHES_AUTHORIZED,ReportLabels.YES_NO,ReportLabels.YES_NO,
                ReportLabels.YES_NO
        };
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {0, 1, 2, 3,6};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {17,18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,31,32,33,34,35,36};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(9, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(11, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(12, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(13, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(14, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(15, 1)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(16, 1)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(7, 4)));
        colsBreak.add(new ArrayList<Integer>(Arrays.asList(13, 3)));
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(8);
        List<StateClrReportView> clrList = (List<StateClrReportView>) session.getAttribute("clrData");
        List<List<String>> reportDataList = new ArrayList<>();
        if (clrList != null && !clrList.isEmpty()) {
            int serialNo = 1;
            for (StateClrReportView obj : clrList) {
                List<String> rowData = new ArrayList<>();
                rowData.add(String.valueOf(serialNo++));
                rowData.add(obj.getTotalDistrict() != null ? String.valueOf(obj.getTotalDistrict()) : "0");
                rowData.add(obj.getTotalTehsils() != null ? obj.getTotalTehsils().toString() : "0");
                rowData.add(obj.getTotalVillages() != null ? obj.getTotalVillages().toString() : "0");
                rowData.add(obj.getTotalRor() != null ? obj.getTotalRor().toString() : "0");
                rowData.add(obj.getRorComputerized() != null ? obj.getRorComputerized().toString() : "0");
                rowData.add(obj.getRorComputerizedPercent() != null ? obj.getRorComputerizedPercent().toString() : "0.0");
                rowData.add(obj.getVillagesClrCompleted() != null ? obj.getVillagesClrCompleted().toString() : "0");
                rowData.add(obj.getClrCompletionPercent() != null ? obj.getClrCompletionPercent().toString() : "0.0");
                rowData.add(obj.getTotalLandOwners() != null ? obj.getTotalLandOwners().toString() : "0");
                rowData.add(obj.getDistrictsWithGenderBasedOwnership() != null ? obj.getDistrictsWithGenderBasedOwnership().toString() : "0");
                rowData.add(obj.getTotalMaleLandOwners() != null ? obj.getTotalMaleLandOwners().toString() : "0");
                rowData.add(obj.getTotalFemaleLandOwners() != null ? obj.getTotalFemaleLandOwners().toString() : "0");
                rowData.add(obj.getTotalOwners() != null ? obj.getTotalOwners().toString() : "0");
                // Add boolean flags for online/digital availability, using "No" as default
                rowData.add(obj.getRorAvailableOnline() != null ? obj.getRorAvailableOnline() : "0");
                rowData.add(obj.getDigitallySignedRorAvailable() != null ? obj.getDigitallySignedRorAvailable() : "0");
                rowData.add(obj.getDigitallySignedRorLegallyValid() != null ? obj.getDigitallySignedRorLegallyValid() : "0");
                rowData.add(obj.getOnlineMutationFacility() != null ? obj.getOnlineMutationFacility() : "0");
                rowData.add(obj.getAutoTriggerMutation() != null ? obj.getAutoTriggerMutation() : "0");
                rowData.add(obj.getBankRedFlagMortgageInLandRecords() != null ? obj.getBankRedFlagMortgageInLandRecords() : "0");
                rowData.add(obj.getDistrictsWithBankRedFlagMortgage() != null ? obj.getDistrictsWithBankRedFlagMortgage().toString() : "0");
                rowData.add(obj.getBankBranchesWithRedFlagMortgage() != null ? obj.getBankBranchesWithRedFlagMortgage().toString() : "0");
                rowData.add(obj.getLandRecordsOnlineFromRegistrationSystem() != null ? obj.getLandRecordsOnlineFromRegistrationSystem() : "0");
                rowData.add(obj.getRevenueCourtProceedingsPaperless() != null ? obj.getRevenueCourtProceedingsPaperless() : "0");
                rowData.add(obj.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() != null ? obj.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() : "0");
                reportDataList.add(rowData);
            }
            pComponent.setReportDataList(reportDataList);
        } else {
            System.out.println("No CLR data found.");
        }
        PdfExporterComponent pComponentMapDigitization = new PdfExporterComponent();
        String[] head1 = {" ", " ", "", "", "", " ", " ", "", "", "", "2. " + ReportLabels.MAP_DIGITIZATION_REPORT};
        pComponentMapDigitization.setReportHeading(head1);
        pComponentMapDigitization.setReportName(ReportLabels.MAP_DIGITIZATION_REPORT);
        float[] col_width1 = {30f, 90f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 40f, 40f, 50f, 50f, 50f, 40f, 50f, 40f, 40f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f,50f};
        pComponentMapDigitization.setCol_width(col_width1);
        String[] col_head1 = {ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.NO_OF_CADASTRAL_MAPS_FMBS_TIPPANS, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS
                , ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.NO_OF_LAND_PARCELS,ReportLabels.NUMBER_OF_DISTRICT_WHERE_CADMAP_FMB_TIPPAN_SHOWING_CURRENT_OWNERSHIP,
                ReportLabels.CADASTRAL_MAPS, ReportLabels.FMBs, ReportLabels.TIPPANS, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.GEO_REFERENCED, ReportLabels.TOTAL, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_LINKED_TO_ROR, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_GEO_REFERENCED, ReportLabels.ULPIN_ASSIGNED, ReportLabels.TOTAL, ReportLabels.GEO_REFERENCED, ReportLabels.ULPIN_ASSIGNED,
                ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.TOTAL, ReportLabels.DIGITIZED, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO,ReportLabels.PERCENTAGE,ReportLabels.NO,ReportLabels.PERCENTAGE};
        pComponentMapDigitization.setCol_head(col_head1);
        Integer[] rwspn2col1 = {7};
        Integer[] rwspn3col1 = {0, 1, 2};
        Integer[] rwspn4col1 = {};
        Integer[] rwspn5col1 = {};
        Integer[] simplecell1 = { 21,22,23,24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46,47};
        pComponentMapDigitization.setRowspn5(rwspn5col1);
        pComponentMapDigitization.setRowspn4(rwspn4col1);
        pComponentMapDigitization.setRowspn3(rwspn3col1);
        pComponentMapDigitization.setRowspn2(rwspn2col1);
        pComponentMapDigitization.setRowspan1(simplecell1);
        List<List<Integer>> colspanval1 = new ArrayList<List<Integer>>();
        // Example: colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(3, 9)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(4, 5)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(5, 7)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(6, 5)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(8, 3)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(9, 3)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(10, 3)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(11, 1)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(12, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(13, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(14, 1)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(15, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(16, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(17, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(18, 1)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(19, 2)));
        colspanval1.add(new ArrayList<Integer>(Arrays.asList(20, 2)));
        pComponentMapDigitization.setColumnspan(colspanval1);
        List<List<Integer>> colsBreak1 = new ArrayList<List<Integer>>();
        // Example: colsBreak.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(3, 3)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(5, 3)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(10, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(11, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(12, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(14, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(15, 2)));
        colsBreak1.add(new ArrayList<Integer>(Arrays.asList(16, 2)));
        pComponentMapDigitization.setColumnnumber(colsBreak1);
        pComponentMapDigitization.setColumnBreakCountNo(14);
        List<List<String>> reportDataList1 = new ArrayList<>();
        //List<MapDigitizationReport> mapList = (List<MapDigitizationReport>) session.getAttribute("reportData");
        List<MapDigitizationReport> mapResultList = (List<MapDigitizationReport>) session.getAttribute("mapData");
        if (mapResultList != null && !mapResultList.isEmpty()) {
            for (MapDigitizationReport obj : mapResultList) {
                List<String> rowData = new ArrayList<>();
                rowData.add("1");
                if (obj != null) {
                    rowData.add(obj.getTotalDistrict() != null ? String.valueOf(obj.getTotalDistrict()) : "0");
                    rowData.add(obj.getTotalTehsils() != null ? obj.getTotalTehsils().toString() : "0");
                    rowData.add(obj.getTotalCadastralMaps() != null ? obj.getTotalCadastralMaps().toString() : "0");
                    rowData.add(obj.getDigitizedCadastralMaps() != null ? obj.getDigitizedCadastralMaps().toString() : "0");
                    rowData.add(obj.getDigitizedCadastralMapsPercent() != null ? obj.getDigitizedCadastralMapsPercent().toString() : "0.0");
                    rowData.add(obj.getTotalFmbs() != null ? obj.getTotalFmbs().toString() : "0");
                    rowData.add(obj.getDigitizedFmbs() != null ? obj.getDigitizedFmbs().toString() : "0");
                    rowData.add(obj.getDigitizedFmbsPercent() != null ? obj.getDigitizedFmbsPercent().toString() : "0.0");
                    rowData.add(obj.getTotalTippans() != null ? obj.getTotalTippans().toString() : "0");
                    rowData.add(obj.getDigitizedTippans() != null ? obj.getDigitizedTippans().toString() : "0");
                    rowData.add(obj.getDigitizedTippansPercent() != null ? obj.getDigitizedTippansPercent().toString() : "0.0");
                    rowData.add(obj.getTotalMapsFmbTippans() != null ? obj.getTotalMapsFmbTippans().toString() : "0");
                    rowData.add(obj.getTotalDigitizedMapsFmbTippans() != null ? obj.getTotalDigitizedMapsFmbTippans().toString() : "0");
                    rowData.add(obj.getDigitizedMapsFmbTippansPercent() != null ? obj.getDigitizedMapsFmbTippansPercent().toString() : "0.0");
                    rowData.add(obj.getGeoreferencedMaps() != null ? obj.getGeoreferencedMaps().toString() : "0");
                    rowData.add(obj.getGeoreferencedMapsPercent() != null ? obj.getGeoreferencedMapsPercent().toString() : "0.0");
                    rowData.add(obj.getTotalVillages() != null ? obj.getTotalVillages().toString() : "0");
                    rowData.add(obj.getVillagesLinkedWithRor() != null ? obj.getVillagesLinkedWithRor().toString() : "0");
                    rowData.add(obj.getVillagesLinkedWithRorPercent() != null ? obj.getVillagesLinkedWithRorPercent().toString() : "0.0");
                    rowData.add(obj.getVillagesGeoreferenced() != null ? obj.getVillagesGeoreferenced().toString() : "0");
                    rowData.add(obj.getVillagesGeoreferencedPercent() != null ? obj.getVillagesGeoreferencedPercent().toString() : "0.0");
                    rowData.add(obj.getVillagesWithUlipn() != null ? obj.getVillagesWithUlipn().toString() : "0");
                    rowData.add(obj.getVillagesWithUlipnPercent() != null ? obj.getVillagesWithUlipnPercent().toString() : "0.0");
                    rowData.add(obj.getTotalLandParcels() != null ? obj.getTotalLandParcels().toString() : "0");
                    rowData.add(obj.getGeoreferencedLandParcels() != null ? obj.getGeoreferencedLandParcels().toString() : "0");
                    rowData.add(obj.getGeoreferencedLandParcelsPercent() != null ? obj.getGeoreferencedLandParcelsPercent().toString() : "0.0");
                    rowData.add(obj.getLandParcelsWithUlipn() != null ? obj.getLandParcelsWithUlipn().toString() : "0");
                    rowData.add(obj.getLandParcelsWithUlipnPercent() != null ? obj.getLandParcelsWithUlipnPercent().toString() : "0.0");
                    rowData.add(obj.getMapsUpdatedBasedOnMutation() != null ? obj.getMapsUpdatedBasedOnMutation().toString() : "0");
                    rowData.add(obj.getMapsUpdatedBasedOnMutationPercentage() != null ? obj.getMapsUpdatedBasedOnMutationPercentage().toString() : "0.0");
                }
                reportDataList1.add(rowData);
            }
            pComponentMapDigitization.setReportDataList(reportDataList1);
        }
        // SRO details
        PdfExporterComponent pComponentRoR = new PdfExporterComponent();
        String[] head5 = {"", "3. " + ReportLabels.SRO_REPORT};
        pComponentRoR.setReportHeading(head5);
        pComponentRoR.setReportName(ReportLabels.SRO_REPORT);
        float[] col_width5 = {30f, 75f, 55f, 55f, 55f, 60f, 60f, 75f, 60f, 60f, 60f, 60f, 60f, 60f,60f,60f,60f,60f,60f,60f,60f,60f,60f,60f,60f,60f};
        pComponentRoR.setCol_width(col_width5);
        String[] col_head5 = {
                ReportLabels.SERIAL_NUMBER,
                ReportLabels.TOTAL_DISTRICTS,
                ReportLabels.NUMBER_OF_SRO_IN_THE_STATE,
                ReportLabels.NUMBER_SRO_USING_ONLINE_REGISTRATION,
                ReportLabels.YEAR_FROM_LEGACY_RECORD_DIGITIZED_SRO,
                ReportLabels.YEAR_LEGACY_RECORD_DIGITIZED_PUBLIC,
                ReportLabels.REGISTRATION_SYSTEM_WITH_OPTION + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.RATES_PUBLIC_REGISTRATION_SYSTEM + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.FREE_CALCULATION_DONE_AUTOMATICALLY_REGISTRATION_SYSTEM + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.REGISTRATION_SYSTEM_INTEGRATED_E_STAMP + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.OPTION_AVAILABLE_REFUND_PAID_FEE + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.OPTION_AVAILABLE_REGISTRATION_SYSTEM + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.FACILITY_ONLINE_VERIFICATION_DOCUMENT + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.SIGN_OBTAINED_FROM_PARTIES + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.DIGITAL_SIGNATURE_ENABLED_REGISTRATION + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.OPTION_OF_EKYC_AVAILABLE_REGISTRATION_SYSTEM + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.OPTION_AVAILABLE_REGISTRATION_SYSTEM_VEDIO + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.HOME_VISIT_MODULE_AVAILABLE_REGISTRATION_SYSTEM + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.EMAIL_AUTOMATICALLY_PARTIES_IMPORTANT + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.REGI_SENT_AUTOMATICALLY_DIGITAL + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.PROVISION_ONLINE_GRIEVANCE_REDRESSAL + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.OPTION_AVAILABLE_REGISTRATION_SYSTEM_FETCH + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.REGIS_SYSTEM_FACILITY_AUTO_TRIGGER + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.PENDENCY_OF_REVENUE_COURT_CASE + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.AADHA_PAN_FINGERPRINT_DETAILS_MASKED + " (" + ReportLabels.YES_NO + " )",
                ReportLabels.FACIL_TO_STORE_REGISTERED_DOCUMENT + " (" + ReportLabels.YES_NO + " )"
        };
        pComponentRoR.setCol_head(col_head5);
        Integer[] rwspn2col5 = {};
        Integer[] rwspn3col5 = {};
        Integer[] rwspn4col5 = {};
        Integer[] rwspn5col5 = {};
        Integer[] simplecell5 = {0, 1, 2,4,5,6,7,8,9,10,11,12,13,14,15,16,18,19,20,21,22,23,24,25,26};
        pComponentRoR.setRowspn5(rwspn5col5);
        pComponentRoR.setRowspn4(rwspn4col5);
        pComponentRoR.setRowspn3(rwspn3col5);
        pComponentRoR.setRowspn2(rwspn2col5);
        pComponentRoR.setRowspan1(simplecell5);
        List<List<Integer>> colspanval5 = new ArrayList<List<Integer>>();
        pComponentRoR.setColumnspan(colspanval5);
        List<List<Integer>> colsBreak5 = new ArrayList<List<Integer>>();
        pComponentRoR.setColumnnumber(colsBreak5);
        pComponentRoR.setColumnBreakCountNo(0);
        List<List<String>> reportDataList5 = new ArrayList<>();
        List<SroReportDTO> rorList = (List<SroReportDTO>) session.getAttribute("sroData");
        if (rorList != null && !rorList.isEmpty()) {
            for (SroReportDTO obj : rorList) {
                List<String> rowData = new ArrayList<>();
                rowData.add("1");
                if (obj != null) {
                    rowData.add(obj.getTotalDistricts() != null ? String.valueOf(obj.getTotalDistricts()) : "0");
                    rowData.add(obj.getNumberOfSROsInState() != null ? obj.getNumberOfSROsInState().toString() : "0");
                    rowData.add(obj.getNumberOfSROsUsingOnlineRegistration() != null ? obj.getNumberOfSROsUsingOnlineRegistration().toString() : "0");
                    rowData.add(obj.getLegacyRecordsAvailableFromYearSRO() != null ? formatter.format(obj.getLegacyRecordsAvailableFromYearSRO()) : "");
                    rowData.add(obj.getLegacyRecordsAvailableFromYearPublic() != null ? formatter.format(obj.getLegacyRecordsAvailableFromYearPublic()) : "");
                    rowData.add(obj.getStandardDeedTemplatesAvailable() != null ? obj.getStandardDeedTemplatesAvailable() : "0");
                    rowData.add(obj.getCircleRatesVisibleToPublic() != null ? obj.getCircleRatesVisibleToPublic() : "0");
                    rowData.add(obj.getAutomaticFeeCalculation() != null ? obj.getAutomaticFeeCalculation() : "0");
                    rowData.add(obj.getIntegrationWithEStampSystem() != null ? obj.getIntegrationWithEStampSystem() : "0");
                    rowData.add(obj.getRefundOptionAvailable() != null ? obj.getRefundOptionAvailable() : "0");
                    rowData.add(obj.getUploadSupportingDocumentsOption() != null ? obj.getUploadSupportingDocumentsOption() : "0");
                    rowData.add(obj.getOnlineVerificationOfDocuments() != null ? obj.getOnlineVerificationOfDocuments() : "0");
                    rowData.add(obj.getSignaturesObtainedDigitally() != null ? obj.getSignaturesObtainedDigitally() : "0");
                    rowData.add(obj.getDigitalSignatureEnabledForSRO() != null ? obj.getDigitalSignatureEnabledForSRO() : "0");
                    rowData.add(obj.getEkycOptionAvailable() != null ? obj.getEkycOptionAvailable() : "0");
                    rowData.add(obj.getVideoConferencingOptionAvailable() != null ? obj.getVideoConferencingOptionAvailable() : "0");
                    rowData.add(obj.getHomeVisitModuleAvailable() != null ? obj.getHomeVisitModuleAvailable() : "0");
                    rowData.add(obj.getAutomaticEmailNotifications() != null ? obj.getAutomaticEmailNotifications() : "0");
                    rowData.add(obj.getDigitalFormatRegisteredDocuments() != null ? obj.getDigitalFormatRegisteredDocuments() : "0");
                    rowData.add(obj.getOnlineGrievanceRedressalSystem() != null ? obj.getOnlineGrievanceRedressalSystem() : "0");
                    rowData.add(obj.getFetchPropertyDetailsFromLandRecords() != null ? obj.getFetchPropertyDetailsFromLandRecords() : "0");
                    rowData.add(obj.getAutoTriggerMutationAvailable() != null ? obj.getAutoTriggerMutationAvailable() : "0");
                    rowData.add(obj.getRevenueCourtCaseRedFlagged() != null ? obj.getRevenueCourtCaseRedFlagged() : "0");
                    rowData.add(obj.getMaskingSensitiveDetailsInRegisteredDeeds() != null ? obj.getMaskingSensitiveDetailsInRegisteredDeeds() : "No");
                    rowData.add(obj.getStoreDocumentsInDigilocker() != null ? obj.getStoreDocumentsInDigilocker() : "0");
                }
                reportDataList5.add(rowData);
            }
            pComponentRoR.setReportDataList(reportDataList5);
        }
        // Status of Cadastral Survey pdf
        PdfExporterComponent pComponentSurveyResurvey = new PdfExporterComponent();
        String[] head4 = {" ", " ", "", "","","","", " ", " ", "", "", "4. " + ReportLabels.SURVEY_REPORT};
        pComponentSurveyResurvey.setReportHeading(head4);
        pComponentSurveyResurvey.setReportName(ReportLabels.SURVEY_REPORT);
        float[] col_width4 = {20f, 65f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponentSurveyResurvey.setCol_width(col_width4);
        String[] col_head4 = {ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS,ReportLabels.TOTAL_VILLAGES, ReportLabels.TOTAL_RURAL_REVENUE_AREA_SQ_KM, ReportLabels.AREA_SANCTIONED_FOR_SURVEY_RE_SURVEY_SQ_KM,ReportLabels.DRON_FLYING_SURVEY,ReportLabels.NUMBER_OF_VILLAGES,ReportLabels.AREA_OF_VILLAGES_WHERE_SURVEY_SANCTIONED_NOT_STARTED,
                ReportLabels.NUMBER_OF_VILLAGES,ReportLabels.AREA_OF_VILLAGES_SQ_KM,ReportLabels.MAP_1_IS_GENERATED,ReportLabels.DRAFT_MAP_PUBLISHED,ReportLabels.FINAL_PROMULGATION_DONE,ReportLabels.SURVEY_SANCTIONED_NOT_STARTED,};
        pComponentSurveyResurvey.setCol_head(col_head4);
        Integer[] rwspn2col4 = {0, 1, 2, 3,4, 5,8};
        Integer[] rwspn3col4 = {};
        Integer[] rwspn4col4 = {};
        Integer[] rwspn5col4 = {};
        Integer[] simplecell4 = {9,10, 11, 12, 13, 14};
        pComponentSurveyResurvey.setRowspn5(rwspn5col4);
        pComponentSurveyResurvey.setRowspn5(rwspn5col4);
        pComponentSurveyResurvey.setRowspn4(rwspn4col4);
        pComponentSurveyResurvey.setRowspn3(rwspn3col4);
        pComponentSurveyResurvey.setRowspn2(rwspn2col4);
        pComponentSurveyResurvey.setRowspan1(simplecell4);
        List<List<Integer>> colspanval4 = new ArrayList<List<Integer>>();
        colspanval4.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        colspanval4.add(new ArrayList<Integer>(Arrays.asList(7, 4)));
        pComponentSurveyResurvey.setColumnspan(colspanval4);
        List<List<Integer>> colsBreak4 = new ArrayList<List<Integer>>();
        colsBreak4.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        colsBreak4.add(new ArrayList<Integer>(Arrays.asList(7, 4)));
        pComponentSurveyResurvey.setColumnnumber(colsBreak4);
        pComponentSurveyResurvey.setColumnBreakCountNo(4);
        List<List<String>> reportDataList4 = new ArrayList<>();
        List<SurveyResurveyViewReport> surveyList = (List<SurveyResurveyViewReport>) session.getAttribute("surveyData");
        if (surveyList != null && !surveyList.isEmpty()) {
            int serialNo = 1;
            for (SurveyResurveyViewReport obj : surveyList) {
                List<String> rowData = new ArrayList<>();
                rowData.add(String.valueOf(serialNo++));
                rowData.add(obj.getTotalDistrict() != null ? obj.getTotalDistrict().toString() : "0");
                rowData.add(obj.getTotalTehsils() != null ? obj.getTotalTehsils().toString() : "0");
                rowData.add(obj.getTotalVillages() != null ? obj.getTotalVillages().toString() : "0");
                rowData.add(obj.getFormatetotalRuralRevenueArea() != null ? obj.getFormatetotalRuralRevenueArea().toString() : "0.0");
                rowData.add(obj.getFomateareaSanctionedForSurvey() != null ? obj.getFomateareaSanctionedForSurvey().toString() : "0.0");
                rowData.add(obj.getVillagesDroneFlyingCompleted() != null ? obj.getVillagesDroneFlyingCompleted().toString() : "0.0");
                rowData.add(obj.getFormateareaDroneFlyingCompleted() != null ? obj.getFormateareaDroneFlyingCompleted().toString() : "0.0");
                rowData.add(obj.getVillagesMap1Generated() != null ? obj.getVillagesMap1Generated().toString() : "0");
                rowData.add(obj.getVillagesDraftMapPublished() != null ? obj.getVillagesDraftMapPublished().toString() : "0");
                rowData.add(obj.getVillagesFinalPromulgationDone() != null ? obj.getVillagesFinalPromulgationDone().toString() : "0");
                rowData.add(obj.getVillagesSurveySanctionNotStarted() != null ? obj.getVillagesSurveySanctionNotStarted().toString() : "0");
                rowData.add(obj.getFormateareaSurveySanctionNotStarted() != null ? obj.getFormateareaSurveySanctionNotStarted().toString() : "0.0");
                reportDataList4.add(rowData);
            }
            pComponentSurveyResurvey.setReportDataList(reportDataList4);
        } else {
            System.out.println("No survey data found.");
        }
        //RCMS Reports pdf
        PdfExporterComponent pComponentRCMS = new PdfExporterComponent();
        String[] head3 = {"", "5. " + ReportLabels.RCMS_REPORT};
        pComponentRCMS.setReportHeading(head3);
        pComponentRCMS.setReportName(ReportLabels.RCMS_REPORT);
        float[] col_width3 = {20f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 70f, 70f, 70f,70f,70f,70f,70f,70f,70f,70f};
        pComponentRCMS.setCol_width(col_width3);
        String[] col_head3 = {
                ReportLabels.SERIAL_NUMBER,ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.NUMBER_OF_REVENUE_COURTS,ReportLabels.E_REVENUE_COURT_MANAGEMENT_SYSTEM + " (" + ReportLabels.YES_NO + ")",ReportLabels.ONLINE_PROCESS_OF_REVENUE_COURT_PROCESSES_AVAILABLE,ReportLabels.WHETHER_PROCEEDS + " (" + ReportLabels.YES_NO + ")",ReportLabels.WHETHER_LAW_RECORD_ONLINE_COURT + " (" + ReportLabels.YES_NO + ")", ReportLabels.WHETHER_FILING_DIRECTLY_SYSTEM + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.TOTAL, ReportLabels.COMPUTERIZED_ONLINE,ReportLabels.ATTACHED_DOCUMENTS + " (" + ReportLabels.YES_NO + ")",ReportLabels.CAUSE_LIST_GENERATION + " (" + ReportLabels.YES_NO + ")", ReportLabels.DEFENDANT_NOTIFICATION + " (" + ReportLabels.YES_NO + ")", ReportLabels.COUNTER_AFFIDAVIT_FILING + " (" + ReportLabels.YES_NO + ")", ReportLabels.PROCEEDINGS_TYPING_SYSTEM + " (" + ReportLabels.YES_NO + ")",ReportLabels.UPLOADING_COURT_ORDERS_NOTIFICATION + " (" + ReportLabels.YES_NO + ")", ReportLabels.LAND_RECORD_INTEGRATION + " (" + ReportLabels.YES_NO + ")", ReportLabels.ONLINE_LAND_RECORD_CHECK + " (" + ReportLabels.YES_NO + ")",
                ReportLabels.NO,ReportLabels.NO,ReportLabels.PERCENTAGE,
        };
        pComponentRCMS.setCol_head(col_head3);
        Integer[] rwspn2col3 = {11,12,13,14,15,16,17,18};
        Integer[] rwspn3col3 = {0,1,2,4,6,7,8};
        Integer[] rwspn4col3 = {};
        Integer[] rwspn5col3 = {};
        Integer[] simplecell3 = {19,20,21};
        pComponentRCMS.setRowspn5(rwspn5col3);
        pComponentRCMS.setRowspn4(rwspn4col3);
        pComponentRCMS.setRowspn3(rwspn3col3);
        pComponentRCMS.setRowspn2(rwspn2col3);
        pComponentRCMS.setRowspan1(simplecell3);
        List<List<Integer>> colspanval3 = new ArrayList<List<Integer>>();
        colspanval3.add(new ArrayList<Integer>(Arrays.asList(3, 3)));
        colspanval3.add(new ArrayList<Integer>(Arrays.asList(5, 8)));
        colspanval3.add(new ArrayList<Integer>(Arrays.asList(9, 1)));
        colspanval3.add(new ArrayList<Integer>(Arrays.asList(10, 2)));
        pComponentRCMS.setColumnspan(colspanval3);
        List<List<Integer>> colsBreak3 = new ArrayList<List<Integer>>();
        colsBreak3.add(new ArrayList<Integer>(Arrays.asList(3, 3)));
        colsBreak3.add(new ArrayList<Integer>(Arrays.asList(5, 8)));
        pComponentRCMS.setColumnnumber(colsBreak3);
        pComponentRCMS.setColumnBreakCountNo(9);
        List<List<String>> reportDataList3 = new ArrayList<>();
        List<RcmsReportDTO> rcmsList = (List<RcmsReportDTO>) session.getAttribute("rcmsData");
        if (rcmsList != null && !rcmsList.isEmpty()) {
            for (RcmsReportDTO obj : rcmsList) {
                List<String> rowData = new ArrayList<>();
                rowData.add("1");
                if (obj != null) {
                    rowData.add(obj.getTotalDistrict() != null ? String.valueOf(obj.getTotalDistrict()) : "0");
                    rowData.add(obj.getTotalTehsils() != null ? obj.getTotalTehsils().toString() : "0");
                    rowData.add(obj.getTotalRevenueCourts() != null ? obj.getTotalRevenueCourts().toString() : "0");
                    rowData.add(obj.getRevenueCourtsComputerized() != null ? obj.getRevenueCourtsComputerized().toString() : "0");
                    rowData.add(obj.getRevenueCourtsComputerizedPercent() != null ? obj.getRevenueCourtsComputerizedPercent().toString() : "0.0");
                    rowData.add(obj.getERcmsAvailable() != null ? obj.getERcmsAvailable() : "Not Available");
                    rowData.add(obj.getAffidavitFilingOnline() != null ? obj.getAffidavitFilingOnline() : "Not Available");
                    rowData.add(obj.getCauseListGenerationOnline() != null ? obj.getCauseListGenerationOnline() : "Not Available");
                    rowData.add(obj.getNoticeToDefendantsOnline() != null ? obj.getNoticeToDefendantsOnline() : "Not Available");
                    rowData.add(obj.getCounterAffidavitFilingOnline() != null ? obj.getCounterAffidavitFilingOnline() : "Not Available");
                    rowData.add(obj.getProceedingsTypingDirectlyOnline() != null ? obj.getProceedingsTypingDirectlyOnline() : "Not Available");
                    rowData.add(obj.getUploadingRevenueCourtOrdersOnline() != null ? obj.getUploadingRevenueCourtOrdersOnline() : "Not Available");
                    rowData.add(obj.getLandRecordsOnlineFromRevenueCourtSystem() != null ? obj.getLandRecordsOnlineFromRevenueCourtSystem() : "Not Available");
                    rowData.add(obj.getRevenueCourtProceedingsPaperless() != null ? obj.getRevenueCourtProceedingsPaperless() : "Not Available");
                    rowData.add(obj.getLandRecordsOnlineForCivilCourts() != null ? obj.getLandRecordsOnlineForCivilCourts() : "Not Available");
                    rowData.add(obj.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() != null ? obj.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts() : "Not Available");
                    rowData.add(obj.getCaseFilingRedFlaggedInLandRecords() != null ? obj.getCaseFilingRedFlaggedInLandRecords() : "Not Available");
                }
                reportDataList3.add(rowData);
            }
            pComponentRCMS.setReportDataList(reportDataList3);
        }
        //Aadhar link Reports pdf
        PdfExporterComponent pComponentAadhar = new PdfExporterComponent();
        String[] head7 = {"", "6. " + ReportLabels.AADHAR_REPORT};
        pComponentAadhar.setReportHeading(head7);
        pComponentAadhar.setReportName(ReportLabels.AADHAR_REPORT);
        float[] col_width7 = {20f, 65f, 50f, 50f, 90f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f};
        pComponentAadhar.setCol_width(col_width7);
        String[] col_head7 =  {ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.RoR,
                ReportLabels.TOTAL,ReportLabels.WHERE_AT_LEAST_ONE_RoR_LINKED_WITH_AADHAAR,ReportLabels.WHERE_100_PERCENT_RoR_LINKED_WITH_AADHAAR,ReportLabels.TOTAL,ReportLabels.LINKED_WITH_AADHAAR,ReportLabels.LINKED_WITH_MOBILE_NUMBER,
                ReportLabels.NO, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO,ReportLabels.NO, ReportLabels.PERCENTAGE,ReportLabels.NO, ReportLabels.PERCENTAGE};
        pComponentAadhar.setCol_head(col_head7);
        Integer[] rwspn2col7 = {5,6,7,8, 9,10};
        Integer[] rwspn3col7 = {};
        Integer[] rwspn4col7 = {0, 1, 2};
        Integer[] rwspn5col7 = {};
        Integer[] simplecell7 = {11,12,13,14,15,16, 17, 18, 19, 20};
        pComponentAadhar.setRowspn5(rwspn5col7);
        pComponentAadhar.setRowspn4(rwspn4col7);
        pComponentAadhar.setRowspn3(rwspn3col7);
        pComponentAadhar.setRowspn2(rwspn2col7);
        pComponentAadhar.setRowspan1(simplecell7);
        List<List<Integer>> colspanval7 = new ArrayList<List<Integer>>();
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(3, 5)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(4, 5)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(5, 1)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(6, 2)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(8, 1)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(9, 2)));
        colspanval7.add(new ArrayList<Integer>(Arrays.asList(10, 2)));
        pComponentAadhar.setColumnspan(colspanval7);
        List<List<Integer>> colsBreak7 = new ArrayList<List<Integer>>();
        colsBreak7.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak7.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        colsBreak7.add(new ArrayList<Integer>(Arrays.asList(7, 2)));
        colsBreak7.add(new ArrayList<Integer>(Arrays.asList(8, 2)));
        pComponentAadhar.setColumnnumber(colsBreak7);
        pComponentAadhar.setColumnBreakCountNo(4);
        List<List<String>> reportDataListAadhar = new ArrayList<>();
        List<LinkedAadharViewReport> aadharResultList = (List<LinkedAadharViewReport>) session.getAttribute("aadhaarData");
        if (aadharResultList != null && !aadharResultList.isEmpty()) {
            for (LinkedAadharViewReport obj : aadharResultList) {
                List<String> rowData = new ArrayList<>();
                rowData.add("1");
                if (obj != null) {
                    rowData.add(obj.getTotalDistrict() != null ? String.valueOf(obj.getTotalDistrict()) : "0");
                    rowData.add(obj.getTotalTehsils() != null ? obj.getTotalTehsils().toString() : "0");
                    rowData.add(obj.getTotalVillages() != null ? obj.getTotalVillages().toString() : "0");
                    rowData.add(obj.getVillagesWithRorLinkedAadhaar() != null ? obj.getVillagesWithRorLinkedAadhaar().toString() : "0");
                    rowData.add(obj.getVillagesWithRorLinkedAadhaarPercent() != null ? obj.getVillagesWithRorLinkedAadhaarPercent().toString() : "0.0");
                    rowData.add(obj.getVillagesWith100PercentRorLinkedAadhaar() != null ? obj.getVillagesWith100PercentRorLinkedAadhaar().toString() : "0");
                    rowData.add(obj.getVillagesWith100PercentRorLinkedAadhaarPercent() != null ? obj.getVillagesWith100PercentRorLinkedAadhaarPercent().toString() : "0.0");
                    rowData.add(obj.getTotalRor() != null ? obj.getTotalRor().toString() : "0");
                    rowData.add(obj.getRorLinkedWithAadhaar() != null ? obj.getRorLinkedWithAadhaar().toString() : "0");
                    rowData.add(obj.getRorLinkedWithAadhaarPercent() != null ? obj.getRorLinkedWithAadhaarPercent().toString() : "0.0");
                    rowData.add(obj.getRorLinkedWithMobileNumber() != null ? obj.getRorLinkedWithMobileNumber().toString() : "0");
                    rowData.add(obj.getRorLinkedWithMobileNumberPercent() != null ? obj.getRorLinkedWithMobileNumberPercent().toString() : "0.0");
                }
                reportDataListAadhar.add(rowData);
            }
            pComponentAadhar.setReportDataList(reportDataListAadhar);
        }
        // MRR report pdf
        PdfExporterComponent pComponentMrr = new PdfExporterComponent();
        String[] head2 = {"", "7. " + ReportLabels.MRR_REPORT};
        pComponentMrr.setReportHeading(head2);
        pComponentMrr.setReportName(ReportLabels.MRR_REPORT);
        float[] col_width2 = {20f, 65f, 50f, 50f, 90f, 50f, 50f, 50f, 50f};
        pComponentMrr.setCol_width(col_width2);
        String[] col_head2 = {ReportLabels.SERIAL_NUMBER, ReportLabels.TOTAL_DISTRICTS, ReportLabels.TOTAL_TEHSILS, ReportLabels.MRR_SANCTIONED, ReportLabels.MRR_COMPLETED_OUT_OF_TOTAL, ReportLabels.MRR_COMPLETED_OUT_OF_SANCTIONED
                , ReportLabels.NO,ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE, ReportLabels.NO, ReportLabels.PERCENTAGE};
        pComponentMrr.setCol_head(col_head2);
        Integer[] rwspn2col2 = {0, 1, 2};
        Integer[] rwspn3col2 = {};
        Integer[] rwspn4col2 = {};
        Integer[] rwspn5col2 = {};
        Integer[] simplecell2 = {6, 7, 8, 9, 10, 11};
        pComponentMrr.setRowspn5(rwspn5col2);
        pComponentMrr.setRowspn4(rwspn4col2);
        pComponentMrr.setRowspn3(rwspn3col2);
        pComponentMrr.setRowspn2(rwspn2col2);
        pComponentMrr.setRowspan1(simplecell2);
        List<List<Integer>> colspanval2 = new ArrayList<List<Integer>>();
        colspanval2.add(new ArrayList<Integer>(Arrays.asList(3, 2)));
        colspanval2.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colspanval2.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        pComponentMrr.setColumnspan(colspanval2);
        List<List<Integer>> colsBreak2 = new ArrayList<List<Integer>>();
        colsBreak2.add(new ArrayList<Integer>(Arrays.asList(3, 2)));
        colsBreak2.add(new ArrayList<Integer>(Arrays.asList(4, 2)));
        colsBreak2.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
        pComponentMrr.setColumnnumber(colsBreak2);
        pComponentMrr.setColumnBreakCountNo(3);
        List<List<String>> reportDataList2 = new ArrayList<>();
        List<MrrViewReport> mrrResultList = (List<MrrViewReport>) session.getAttribute("mrrData");
        if (mrrResultList != null && !mrrResultList.isEmpty()) {
            int serialNo = 1;
            for (MrrViewReport obj : mrrResultList) {
                List<String> rowData = new ArrayList<>();
                rowData.add(String.valueOf(serialNo++));
                rowData.add(obj.getTotalDistrict() != null ? obj.getTotalDistrict().toString() : "0");
                rowData.add(obj.getTotalTehsils() != null ? obj.getTotalTehsils().toString() : "0");
                rowData.add(obj.getMrrSanctioned() != null ? obj.getMrrSanctioned().toString() : "0");
                rowData.add(obj.getMrrSanctionedPercent() != null ? obj.getMrrSanctionedPercent().toString() : "0.0");
                rowData.add(obj.getMrrCompleted() != null ? obj.getMrrCompleted().toString() : "0");
                rowData.add(obj.getMrrCompletedOutOfTotalTehsilsPercent() != null ? obj.getMrrCompletedOutOfTotalTehsilsPercent().toString() : "0.0");
                rowData.add(obj.getMrrCompleted() != null ? obj.getMrrCompleted().toString() : "0");
                rowData.add(obj.getMrrCompletedOutOfSanctionedPercent() != null ? obj.getMrrCompletedOutOfSanctionedPercent().toString() : "0.0");
                reportDataList2.add(rowData);
            }
            pComponentMrr.setReportDataList(reportDataList2);
        } else {
            System.out.println("No MRR data found.");
        }
        //All adding PDF in pdfexporterList
        pdfexporterList.add(pComponent);
        pdfexporterList.add(pComponentMapDigitization);
        pdfexporterList.add(pComponentRoR);
        pdfexporterList.add(pComponentSurveyResurvey);
        pdfexporterList.add(pComponentRCMS);
        pdfexporterList.add(pComponentAadhar);
        pdfexporterList.add(pComponentMrr);
        PdfExporter.createPdf(pdfexporterList, response);
    }


    @GetMapping("oomfreport")
    public void getOmmfReportPdf(HttpSession session, HttpServletResponse response) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String[] head = {"OOMF Report"};
        pComponent.setReportHeading(head);
        pComponent.setReportName("oomf");
        float[] col_width = {30f, 90f, 60f, 90f, 90f, 90f, 90f, 90f, 60f, 60f, 60f, 60f, 60f, 60f, 60f};
        pComponent.setCol_width(col_width);
        String[] col_head = {"S.No.", "Financial Year", "Quarter", "Status Date", "Output", "Outcome", "1.Computerization of Land Records across Districts of the Country (except NE States having Community rights issue)", "2.Computerization of Registration across Districts of the Country", "3.Integration of Land Records (RoRs) with registration in Districts",
                "4.Districts enabled for auto credit to farmer ", "5.Villages ready for Aadhar based authentication ", "1.Ease of living of citizens.", "1.1.Record of Rights (RoRs) computerized (No of villages).", "1.2. No. of Maps/FMBs digitized.", "1.3. RoRs integrated with cadastral maps/FMBs (No. of villages).", "1.4.No. of villages having geo-referenced cadastral maps/FMBs.", "1.1.Percentage of villages where RoRs are made accessible through CSC, Kiosk, Online, etc (%)", "1.2.Percentage of villages where digitally signed RoRs are issued (%)", "1.3.Percentage of villages in which land records (RoRs) have been computerized (%)"};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {7, 8, 9, 10};
        Integer[] rwspn3col = {0, 1, 2, 3};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {13, 14, 15, 16, 17, 18, 19, 20, 21};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 8)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 4)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(11, 3)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        pComponent.setColumnnumber(colsBreak);
        pComponent.setColumnBreakCountNo(0);
        // List<OomfModel> oomfList = (List<OomfModel>) session.getAttribute("oomfList");
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
//            oomfList.forEach(oomf -> {
//                List<String> rowData = new ArrayList<>();
//
//                // Add data to rowData list
//                rowData.add(Integer.toString(indexHolder.incrementAndGet()));
//                rowData.add(oomf.getFinancialYear());
//                rowData.add(oomf.getQuarter1());
//                rowData.add(oomf.getStatusDate().toString());
//                rowData.add(oomf.getFormattingclr1_1() == null ? "0" : (oomf.getFormattingclr1_1()));
//                rowData.add(oomf.getFormattingclr1_2() == null ? "0" :(oomf.getFormattingclr1_2()));
//                rowData.add(oomf.getFormattingclr1_3() == null ? "0" : (oomf.getFormattingclr1_3()));
//                rowData.add(oomf.getFormattingclr1_4() == null ? "0" : (oomf.getFormattingclr1_4()));
//                rowData.add(oomf.getFormattingcrc2_1() == null ? "0" : (oomf.getFormattingcrc2_1()));
//                rowData.add(oomf.getFormattingIlr3_1() == null ? "0" : (oomf.getFormattingIlr3_1()));
//                rowData.add(oomf.getDofrf4_1() == null ? "0" : Double.toString(oomf.getDofrf4_1()));
//                rowData.add(oomf.getFormattingVoA5_1() == null ? "0" : (oomf.getFormattingVoA5_1()));
//                rowData.add(oomf.getClr1_1_1() == null ? "0" : Double.toString(oomf.getClr1_1_1()));
//                rowData.add(oomf.getClr1_1_2() == null ? "0" : Double.toString(oomf.getClr1_1_2()));
//                rowData.add(oomf.getClr1_1_3() == null ? "0" : Double.toString(oomf.getClr1_1_3()));
//                reportDataList.add(rowData);
//            });

        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);
    }


}
