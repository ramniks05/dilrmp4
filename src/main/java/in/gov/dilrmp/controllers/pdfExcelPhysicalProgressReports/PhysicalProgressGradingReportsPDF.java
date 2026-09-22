package in.gov.dilrmp.controllers.pdfExcelPhysicalProgressReports;
import in.gov.dilrmp.component.PdfExporterComponent;
import in.gov.dilrmp.models.dashboard.DashBordDTO;
import in.gov.dilrmp.models.naksha.CapacityBuilding;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntry;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntrySolrAndVender;
import in.gov.dilrmp.models.oomf.OOMFReport;
import in.gov.dilrmp.models.reportDTO.ComparisonReportDTO.ComparisonReportDTO;
import in.gov.dilrmp.utils.PdfExporter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("physcial/report/pdf")
public class PhysicalProgressGradingReportsPDF {


    @GetMapping("/grading")
    public void getGradingPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {"Grading of States/UTs in Platinum, Gold and Silver categories",
                "Criteria: Platinum (P): Progress >=99% | Gold (G): Progress 95-99% | Silver (S): Progress 90-95%"};
        pComponent.setReportHeading(head);
        pComponent.setReportName("Grading of States/UTs");
        float col_width[] = {45f, 130f, 130f, 130f, 130f, 130f, 110f, 110f, 90f, 90f, 90f, 90f};
        pComponent.setCol_width(col_width);
        String col_head[] = {"S.No", "State/UT Name", "CLR Completed Villages(%)", "MapSheets Digitized(%)",
                "Villages with Cadastral Maps linked to RoR (%)", "SRO Computerized (%)",
                "SRO Integrated With Land Records(%)", "Completed Modern Record Room(%)",
                "Platinum", "Gold", "Silver", "Grade"};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        pComponent.setColumnnumber(colsBreak);
        List<List<String>> reportDataList = new ArrayList<>();
        List<DashBordDTO> list = (List<DashBordDTO>) session.getAttribute("gradingOfState");
        List<String> strings = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        list.stream().forEachOrdered(gList -> {
            strings.add(Integer.toString(indexHolder.incrementAndGet()));
            strings.add(gList.getName());
            strings.add(gList.getClrpercent1() != null || gList.getClrpercent1() == "" ? gList.getClrpercent1() : "0.0");
            strings.add(gList.getDigitizedmapspercent1() != null ? gList.getDigitizedmapspercent1() : "0.0");
            strings.add(gList.getCadastralmapslinkedtororpercent1() != null ? gList.getCadastralmapslinkedtororpercent1() : "0.0");
            strings.add(gList.getSrocomputrizedpercent1() != null ? gList.getSrocomputrizedpercent1() : "0.0");
            strings.add(gList.getSroinreragllrpercent1() != null ? gList.getSroinreragllrpercent1() : "0.0");
            strings.add(gList.getMrrcompletedpercent1() != null ? gList.getMrrcompletedpercent1() : "0.0");
            strings.add(Integer.toString(gList.getPlatinum()));
            strings.add(Integer.toString(gList.getGold()));
            strings.add(Integer.toString(gList.getSilver()));
            strings.add(gList.getCategory());
        });
        reportDataList.add(strings);
        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);
    }

    @GetMapping("/grading-district")
    public void getGradingOfDistrictPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String head[] = {"Grading of Districts in Platinum, Gold and Silver categories",
                "Criteria: Platinum (P): Progress >=99% | Gold (G): Progress 95-99% | Silver (S): Progress 90-95%"};
        pComponent.setReportHeading(head);
        pComponent.setReportName("Grading of Districts");
        float col_width[] = {45f, 130f, 130f, 130f, 130f, 130f, 110f, 110f, 90f, 90f, 90f};
        pComponent.setCol_width(col_width);
        String col_head[] = {"S.No", "State/UT Name", "District Name", "CLR Completed Villages(%)", "MapSheets Digitized(%)",
                "Villages with Cadastral Maps linked to RoR (%)", "Completed Modern Record Room(%)", "Platinum", "Gold", "Silver", "Grade"};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        pComponent.setColumnnumber(colsBreak);
        List<List<String>> reportDataList = new ArrayList<>();
        List<DashBordDTO> list = (List<DashBordDTO>) session.getAttribute("gradingOfDistrict");
        List<String> strings = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        list.stream().forEachOrdered(gList -> {
            strings.add(Integer.toString(indexHolder.incrementAndGet()));
            strings.add(gList.getName());
            strings.add(gList.getDistrict_name());
            strings.add(gList.getClrpercent1() != null || gList.getClrpercent1() == "" ? gList.getClrpercent1() : "0.0");
            strings.add(gList.getDigitizedmapspercent1() != null ? gList.getDigitizedmapspercent1() : "0.0");
            strings.add(gList.getCadastralmapslinkedtororpercent1() != null ? gList.getCadastralmapslinkedtororpercent1() : "0.0");
            strings.add(gList.getMrrcompletedpercent1() != null ? gList.getMrrcompletedpercent1() : "0.0");
            strings.add(Integer.toString(gList.getPlatinum()));
            strings.add(Integer.toString(gList.getGold()));
            strings.add(Integer.toString(gList.getSilver()));
            strings.add(gList.getCategory());
        });
        reportDataList.add(strings);
        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);
    }


    @GetMapping("/date-wise")
    public void getDateWisePdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String date1 = (String) session.getAttribute("fromdate");
        String head[] = {"Physical Progress Report (Data available from October 18, 2024)",
                "Date Wise Physical Progress Report As on (" + date1 + ")"};
        pComponent.setReportHeading(head);
        pComponent.setReportName("Date Wise Physical Progress Report");
        float col_width[] = {45f, 130f, 130f, 130f, 130f, 130f, 110f, 110f, 90f, 90f, 90f, 90f, 90f};
        pComponent.setCol_width(col_width);
        String col_head[] = {"S.No", "State/UT Name", "Total Tehsil", "Total villages)",
                "Total SROs", "CLR Completed Villages",
                "Total Mapsheets/FMBs/Tippans (No.)", "Digitized Mapsheets/FMBs/Tippans (No.)",
                "Villages with Cadastral Maps linked With RoR", "SRO Computerized", "Sanctioned Modern Record Room", "Completed Modern Record Room", "Survey/Resurvey Completed (village)"};
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        pComponent.setColumnnumber(colsBreak);
        List<List<String>> reportDataList = new ArrayList<>();
        List<ComparisonReportDTO> list = (List<ComparisonReportDTO>) session.getAttribute("comparisonData");
        List<String> strings = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        list.stream().forEachOrdered(gList -> {
            strings.add(Integer.toString(indexHolder.incrementAndGet()));
            strings.add(gList.getStateName());
            strings.add(gList.getTotalTehsilsDiff());
            strings.add(gList.getTotalVillagesDiff());
            strings.add(gList.getNoOfSroInStateDiff());
            strings.add(gList.getVillagesClrCompletedDiff());
            strings.add(gList.getTotalMapsFmbTippansDiff());
            strings.add(gList.getTotalDigitizedMapsFmbTippansDiff());
            strings.add(gList.getVillagesLinkedWithRorDiff());
            strings.add(gList.getNoOfSroUsingOnlineRegistrationDiff());
            strings.add(gList.getMrrSanctionedDiff());
            strings.add(gList.getMrrCompletedDiff());
            strings.add(gList.getVillagesFinalPromulgationDoneDiff());
            reportDataList.add(new ArrayList<>(strings));
            strings.clear();
        });
        ComparisonReportDTO grandTotal = (ComparisonReportDTO) session.getAttribute("grandTotal");
        if (grandTotal != null) {
            List<String> grandTotalRow = new ArrayList<>();
            grandTotalRow.add(Integer.toString(indexHolder.incrementAndGet()));
            grandTotalRow.add(grandTotal.getStateName());
            grandTotalRow.add(grandTotal.getTotalTehsilsDiff());
            grandTotalRow.add(grandTotal.getTotalVillagesDiff());
            grandTotalRow.add(grandTotal.getNoOfSroInStateDiff());
            grandTotalRow.add(grandTotal.getVillagesClrCompletedDiff());
            grandTotalRow.add(grandTotal.getTotalMapsFmbTippansDiff());
            grandTotalRow.add(grandTotal.getTotalDigitizedMapsFmbTippansDiff());
            grandTotalRow.add(grandTotal.getVillagesLinkedWithRorDiff());
            grandTotalRow.add(grandTotal.getNoOfSroUsingOnlineRegistrationDiff());
            grandTotalRow.add(grandTotal.getMrrSanctionedDiff());
            grandTotalRow.add(grandTotal.getMrrCompletedDiff());
            grandTotalRow.add(grandTotal.getVillagesFinalPromulgationDoneDiff());
            reportDataList.add(grandTotalRow);
        }
        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);
    }

    @GetMapping("/date-wise-diffrence")
    public void getDateWisediffrencePdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String date1 = (String) session.getAttribute("fromdate");
        String date2 = (String) session.getAttribute("todate");
        String head[] = {
                "Date-wise Physical Progress Difference Report: Comparison Across All Components (Data available from October 18, 2024)",
                "Physical Progress Differences for All Components (" + date1 + " to " + date2 + ")"};
        pComponent.setReportHeading(head);
        pComponent.setReportName("Date-wise Comparison Report for All Components");
        float col_width[] = {45f, 130f, 130f, 130f, 130f, 130f, 130f, 130f, 110f, 110f, 110f, 110f, 110f,110f,110f, 110f, 110f, 110f, 90f, 90f, 110f, 110f, 110f,110f,110f};
        pComponent.setCol_width(col_width);
        String col_head[] = {
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
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {0, 1, 2, 3};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,24,25};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 21)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        pComponent.setColumnnumber(colsBreak);
        List<List<String>> reportDataList = new ArrayList<>();
        List<ComparisonReportDTO> list = (List<ComparisonReportDTO>) session.getAttribute("comparisonData");
        List<String> strings = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        list.stream().forEachOrdered(gList -> {
            strings.add(Integer.toString(indexHolder.incrementAndGet()));
            strings.add(gList.getStateName());
            strings.add(gList.getTotalTehsilsDiff());
            strings.add(gList.getTotalVillagesDiff());
            strings.add(gList.getTotalRorDiff());
            strings.add(gList.getRorComputerizedDiff());
            strings.add(gList.getRorLinkedWithAadhaarDiff());
            strings.add(gList.getDistrictsWithGenderBasedOwnershipDiff());
            strings.add(gList.getVillagesClrCompletedDiff());
            strings.add(gList.getTotalCadastralMapsDiff());
            strings.add(gList.getDigitizedCadastralMapsDiff());
            strings.add(gList.getTotalMapsFmbTippansDiff());
            strings.add(gList.getTotalDigitizedMapsFmbTippansDiff());
            strings.add(gList.getTotalDigitizedFmbTippansDiff());
            strings.add(gList.getVillagesLinkedWithRorDiff());
            strings.add(gList.getVillagesGeoreferencedDiff());
            strings.add(gList.getTotalLandParcelsDiff());
            strings.add(gList.getLandParcelsWithUlipnDiff());
            strings.add(gList.getGeoreferencedLandParcelsDiff());
            strings.add(gList.getNoOfSroInStateDiff());
            strings.add(gList.getNoOfSroUsingOnlineRegistrationDiff());
            strings.add(gList.getMrrSanctionedDiff());
            strings.add(gList.getMrrCompletedDiff());
            strings.add(gList.getVillagesFinalPromulgationDoneDiff());
            strings.add(gList.getRevenueCourtsComputerizedDiff());
            reportDataList.add(new ArrayList<>(strings));
            strings.clear();
        });
        ComparisonReportDTO grandTotal = (ComparisonReportDTO) session.getAttribute("grandTotal");
        if (grandTotal != null) {
            List<String> grandTotalRow = new ArrayList<>();
            grandTotalRow.add(Integer.toString(indexHolder.incrementAndGet()));
            grandTotalRow.add(grandTotal.getStateName());
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
        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);
    }

    @GetMapping("/oomf-reports")
    public void getOmfPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String year = (String) session.getAttribute("selectedyear");
        String quater = (String) session.getAttribute("selectedquater");
        if (year == null) {
            year = "Not Selected";
        }
        if (quater == null) {
            quater = "Not Selected";
        }
        String[] head = {
                "OOMF Status",
                "OOMF Status of Physical Progress for the financial year: " + year + " and Quarter: " + quater
        };
        pComponent.setReportHeading(head);
        pComponent.setReportName("OOMF Status");

        // Define column widths and headers
        float col_width[] = {45f, 130f, 130f, 130f, 130f, 130f, 110f, 110f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f, 90f};
        pComponent.setCol_width(col_width);

        String[] col_head = {
                "S.No", "Output", "Outcome", "1. Computerization of Land Records across Districts of the Country (except NE States having Community rights issue)",
                "2.1 No. of Computerization of Registration across Districts of the Country", "3.1 No. of Integration of Land Records (RoRs) with registration in Districts ",
                "4.1 Integrated Districts enabled for auto credit to farmer", "5.1 Integrated Villages ready for Aadhar based authentication",
                "1.Ease of living of citizens.", "1.1.Record of Rights (RoRs) computerized (No. of villages).", "1.2. No. of Maps / FMBs / Tippans digitized.", "1.3. RoRs integrated with cadastral Maps / FMBs / Tippans (No. of villages).",
                "1.4.No. of villages having geo-referenced cadastral maps/FMBs.", "1.1.Percentage of villages where RoRs are made accessible through CSC, Kiosk, Online, etc (%)",
                "1.2.Percentage of villages where digitally signed RoRs are issued (%)", "1.3.Percentage of villages in which land records (RoRs) have been computerized (%)",
                "Target", "Achievement", "Achievement (%)", "Target", "Achievement", "Achievement (%)", "Target", "Achievement", "Achievement (%)",
                "Target", "Achievement", "Achievement (%)", "Target", "Achievement", "Achievement (%)", "Target", "Achievement", "Achievement (%)",
                "Target", "Achievement", "Achievement (%)", "Target", "Achievement", "Achievement (%)", "Target", "Achievement", "Achievement (%)",
                "Target", "Achievement", "Achievement (%)", "Target", "Achievement", "Achievement (%)"
        };
        pComponent.setCol_head(col_head);

        Integer[] rwspn2col = {3, 8};
        Integer[] rwspn3col = {4, 5, 6, 7};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {0};
        Integer[] simplecell = {16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);

        // Define column spans
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        colspanval.add(new ArrayList<Integer>(Arrays.asList(1, 24)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(2, 9)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(3, 12)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(6, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(7, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(8, 9)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(9, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(10, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(11, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(12, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(13, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(14, 3)));
        colspanval.add(new ArrayList<Integer>(Arrays.asList(15, 3)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        pComponent.setColumnnumber(colsBreak);
        List<List<String>> reportDataList = new ArrayList<>();
        List<OOMFReport> list = (List<OOMFReport>) session.getAttribute("oomfReport");

        if (list == null || list.isEmpty()) {
            List<String> emptyRow = new ArrayList<>();
            emptyRow.add("");
            for (int i = 1; i < col_head.length; i++) {
                emptyRow.add("");
            }
            reportDataList.add(emptyRow);
        } else {
            AtomicInteger indexHolder = new AtomicInteger();
            list.forEach(gList -> {
                List<String> strings = new ArrayList<>();
                strings.add(Integer.toString(indexHolder.incrementAndGet()));
                strings.add(gList.getTarget1() != null ? gList.getTarget1().toString() : "0");
                strings.add("0");
                strings.add("0.00");
                strings.add(gList.getTarget2() != null ? gList.getTarget2().toString() : "0");
                strings.add("0");
                strings.add("0.00");
                strings.add(gList.getTarget3() != null ? gList.getTarget3().toString() : "0");
                strings.add("0");
                strings.add("0.00");
                strings.add(gList.getTarget4() != null ? gList.getTarget4().toString() : "0");
                strings.add("0");
                strings.add("0.00");
                strings.add(gList.getTarget5() != null ? gList.getTarget5().toString() : "0");
                strings.add("0");
                strings.add("0.00");
                strings.add(gList.getTarget6() != null ? gList.getTarget6().toString() : "0");
                strings.add("0");
                strings.add("0.00");
                strings.add(gList.getTarget7() != null ? gList.getTarget7().toString() : "0");
                strings.add("0");
                strings.add("0.00");
                strings.add(gList.getTarget8() != null ? gList.getTarget8().toString() : "0");
                strings.add("0");
                strings.add("0.00");
                strings.add(gList.getTarget9() != null ? gList.getTarget9().toString() : "0");
                strings.add("0");
                strings.add("0.00");
                strings.add(gList.getTarget10() != null ? gList.getTarget10().toString() : "0");
                strings.add("0");
                strings.add("0.00");
                strings.add(gList.getTarget11() != null ? gList.getTarget11().toString() : "0");
                strings.add("0");
                strings.add("0.00");
                reportDataList.add(strings);
            });
        }

        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);
    }


    @GetMapping("/naksha")
    public void getNakshaPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String[] head = {
                "Naksha For State/UT (before publication of Map1) Report"
        };
        pComponent.setReportHeading(head);
        pComponent.setReportName("Naksha Report");
        float col_width[] = {50f, 120f, 150f, 60f, 150f, 150f, 150f, 100f, 100f, 100f, 100f, 100f, 80f, 100f, 250f,100f,100f,100f,250f, 100f,100f,100f,100f,100f,100f,250f,100f};
        pComponent.setCol_width(col_width);
        String[] col_head = {
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
                "Ground Truthing ",
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
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,17,19,20,21,22};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {23,24,25,26,27};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        colspanval.add(new ArrayList<Integer>(Arrays.asList(18, 5)));
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        pComponent.setColumnnumber(colsBreak);
        List<List<String>> reportDataList = new ArrayList<>();
        List<NakshaMISDataEntry> list = (List<NakshaMISDataEntry>) session.getAttribute("nakshaMISDataEntries");
        List<NakshaMISDataEntry> grandTotal = (List<NakshaMISDataEntry>) session.getAttribute("grandTotal");
        List<String> strings = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        list.stream().forEachOrdered(gList -> {
            strings.add(Integer.toString(indexHolder.incrementAndGet()));
            strings.add(gList.getState().getName() != null ? gList.getState().getName() : "N/A");
            strings.add(gList.getNodalDepartmentName() != null ? gList.getNodalDepartmentName() : "N/A");
            strings.add("Yes".equalsIgnoreCase(gList.getNodalOfficerAppointed()) ? "Yes" : "No");
            strings.add(gList.getNodalOfficerName() != null ? gList.getNodalOfficerName() : "N/A");
            strings.add(gList.getPhonenumber() != null ? gList.getPhonenumber() : "N/A");
            strings.add(gList.getEmail() != null ? gList.getEmail() : "N/A");
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
            reportDataList.add(new ArrayList<>(strings));
            strings.clear();
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
        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);
    }

    @GetMapping("/capacity-building")
    public void getCapacitybuildingPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String[] head = {
                "Naksha for State/UT Capacity Building Report"
        };
        pComponent.setReportHeading(head);
        pComponent.setReportName("CapacityBuildingReport");
        float col_width[] = {50f, 150f, 150f, 100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f};
        pComponent.setCol_width(col_width);
        String[] col_head = {
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
        pComponent.setCol_head(col_head);
        Integer[] rwspn2col = {};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);
        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
        pComponent.setColumnspan(colspanval);
        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
        pComponent.setColumnnumber(colsBreak);
        List<List<String>> reportDataList = new ArrayList<>();
        List<CapacityBuilding> list = (List<CapacityBuilding>) session.getAttribute("capacityBuilding");
        List<CapacityBuilding> grandTotal = (List<CapacityBuilding>) session.getAttribute("grandTotal");
        List<String> strings = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();
        list.stream().forEachOrdered(gList -> {
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
            reportDataList.add(new ArrayList<>(strings));
            strings.clear();
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
        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);
    }

    @GetMapping("/naksha-vender")
    public void getNakshaVenderPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();

        // Report heading and name
        String[] head = {"Format 2: Naksha For SoI & Vendor (MAP 1) Report"};
        pComponent.setReportHeading(head);
        pComponent.setReportName("NakshaVendorReport");
        // Column widths
        float[] col_width = {40f, 100f, 100f, 80f, 80f, 80f, 80f, 100f, 80f, 60f, 80f, 120f, 100f, 100f, 100f, 100f, 100f, 100f, 80f, 80f, 80f, 100f, 100f,100f, 100f, 100f, 100f,100f, 100f, 100f};
        pComponent.setCol_width(col_width);
        // Column headers
        String[] col_head = {
                "S.No", "Name of the Geospatial Directorate", "Email of the Geospatial Directorate", "Zone", "State",
                "District Name", "Name of ULB/Town", "Third Party Agency", "Technology", "GD/Wing", "Package",
                "DoLR Sanctioned Area", "Buffer Area by states for Data Acquisition (Area in sq.km.)", "Last Updated Time/ Date by GD",

                "Grid Completion", "Aerial Data Acquisition",
                "Total No. of 1:2k Grid", "Grids Completed Last Week", "Total Grids Completed",

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
                "Completion Status"};
        pComponent.setCol_head(col_head);
        // Rowspan and colspan
        pComponent.setRowspn5(new Integer[]{});
        pComponent.setRowspn4(new Integer[]{});
        pComponent.setRowspn3(new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13});
        pComponent.setRowspn2(new Integer[]{16, 17,18,20});
        pComponent.setRowspan1(new Integer[]{21,22,23,24,25,26,27,28,29,30,31,32});
        List<List<Integer>> colspanval = new ArrayList<>();
        colspanval.add(Arrays.asList(14, 3));
        colspanval.add(Arrays.asList(15, 13));
        colspanval.add(Arrays.asList(19, 12));
        pComponent.setColumnspan(colspanval);
        pComponent.setColumnnumber(new ArrayList<>());
        // Prepare report data
        List<List<String>> reportDataList = new ArrayList<>();
        List<NakshaMISDataEntrySolrAndVender> list = (List<NakshaMISDataEntrySolrAndVender>) session.getAttribute("nakshaMISDataEntries");
        List<NakshaMISDataEntrySolrAndVender> grandTotal = (List<NakshaMISDataEntrySolrAndVender>) session.getAttribute("grandTotal");
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

        // Add Grand Total row
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
            while (totalRow.size() < col_head.length) {
                totalRow.add("");
            }

            reportDataList.add(totalRow);
        }

        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);
    }

    @GetMapping("/naksha-vender-data-processing")
    public void getNakshaVenderDataProcessingPdf(HttpServletResponse response, HttpSession session) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        DecimalFormat df = new DecimalFormat("0.00");
        // Report heading and name
        String[] head = {"Format 2: Map 1 (SoI) – Data Processing, QA/QC Details, and Feature Extraction"};
        pComponent.setReportHeading(head);
        pComponent.setReportName("NakshaSOIDataProcessingReport");
        // Column widths
        float[] col_width = {40f, 100f, 100f, 80f,  80f,  80f,  80f, 100f, 80f, 60f,
                             80f, 120f, 100f, 100f, 100f, 100f, 100f, 100f, 80f, 80f,
                             80f, 120f, 100f, 100f, 100f, 80f,  80f, 80f, 80f, 80f,
                             80f, 80f,  80f,  80f,  80f,  80f,  80f};
        pComponent.setCol_width(col_width);
        // Column headers
        String[] col_head = {
                "S.No", "Name of the Geospatial Directorate", "Email of the Geospatial Directorate", "Zone", "State",
                "District Name", "Name of ULB/Town",
                "Data Processing", "QA/QC Details", "Feature Extraction",
                // Data Processing
                "ORI Submitted by Vendor for QA/QC – Cumulative Area Completed",
                "ORI Submitted by Vendor for QA/QC – Cumulative Percentage Completed",
                "DEM Submitted by Vendor for QA/QC – Cumulative Area Completed",
                "DEM Submitted by Vendor for QA/QC – Percentage Completed",
                "DSM Submitted by Vendor for QA/QC – Cumulative Area Completed",
                "DSM Submitted by Vendor for QA/QC – Percentage Completed",
                "DTM Submitted by Vendor for QA/QC – Cumulative Area Completed",
                "DTM Submitted by Vendor for QA/QC – Percentage Completed",
                "3D Mesh Model Submitted by Vendor for QA/QC – Cumulative Completed",
                "3D Mesh Model Submitted by Vendor for QA/QC – Percentage Completed",
                // QA/QC Details
                "QA/QC of ORI – Cumulative Area Completed",
                "QA/QC of ORI – Percentage Completed",
                "QA/QC of DEM – Cumulative Area Completed",
                "QA/QC of DEM – Percentage Completed",
                "QA/QC of DSM – Cumulative Area Completed",
                "QA/QC of DSM – Percentage Completed",
                "QA/QC of DTM – Cumulative Area Completed",
                "QA/QC of DTM – Percentage Completed",
                "QA/QC of 3D Mesh – Cumulative Area Completed",
                "QA/QC of 3D Mesh – Percentage Completed",
                // Feature Extraction
                "2D Feature Extraction on ORI – Cumulative Area Completed",
                "2D Feature Extraction on ORI – Percentage Completed",
                "QA/QC of 2D Feature Extraction – Cumulative Area Completed",
                "QA/QC of 2D Feature Extraction – Percentage Completed",
                "3D Feature Extraction Submitted for QA/QC – Cumulative Area Completed",
                "3D Feature Extraction Submitted for QA/QC – Percentage Completed",
                "QA/QC of 3D Feature Extraction – Cumulative Area Completed",
                "QA/QC of 3D Feature Extraction – Percentage Completed",

                "Fixing Ground Control Points & City Boundaries",
                "ORI Received Back from State (Yes/No)",
                "ORI Submitted to state after Rectification (Yes/No)"
        };
        pComponent.setCol_head(col_head);

        // Rowspan and colspan
        pComponent.setRowspn5(new Integer[]{});
        pComponent.setRowspn4(new Integer[]{});
        pComponent.setRowspn3(new Integer[]{});
        pComponent.setRowspn2(new Integer[]{0, 1, 2, 3, 4, 5, 6,10, 11, 12});
        pComponent.setRowspan1(new Integer[]{
                13, 14, 15, 16, 17, 18, 19, 20,
                21, 22, 23, 24, 25, 26, 27, 28,
                29, 30, 31, 32, 33, 34, 35, 36,
                37, 38, 39, 40
        });
        List<List<Integer>> colspanval = new ArrayList<>();
        colspanval.add(Arrays.asList(7, 10));
        colspanval.add(Arrays.asList(8, 10));
        colspanval.add(Arrays.asList(9, 8));
        pComponent.setColumnspan(colspanval);
        pComponent.setColumnnumber(new ArrayList<>());
        // Prepare report data
        List<List<String>> reportDataList = new ArrayList<>();
        List<NakshaMISDataEntrySolrAndVender> list = (List<NakshaMISDataEntrySolrAndVender>) session.getAttribute("nakshaMISDataEntries");
        List<NakshaMISDataEntrySolrAndVender> grandTotal = (List<NakshaMISDataEntrySolrAndVender>) session.getAttribute("grandTotal");
        AtomicInteger indexHolder = new AtomicInteger();
        list.forEach(entry -> {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(indexHolder.incrementAndGet()));
            row.add(nonNull(entry.getOfficerName()));
            row.add(nonNull(entry.getGdEmail()));
            row.add(nonNull(entry.getZoneName()));
            row.add(nonNull(entry.getStateName()));
            row.add(nonNull(entry.getDistrict_Name()));
            row.add(nonNull(entry.getUlbName()));

            // Data Processing
            row.add(safeFormat(entry.getSeparateOriDataProcessingCumulative(), df));
            row.add(safeFormatPercentage(entry.getSeparateOriDataProcessingPercentage(), df));

            row.add(safeFormat(entry.getDemDataProcessingCumulative(), df));
            row.add(safeFormatPercentage(entry.getDemDataProcessingPercentage(), df));

            row.add(safeFormat(entry.getDsmDataProcessingCumulative(), df));
            row.add(safeFormatPercentage(entry.getDsmDataProcessingPercentage(), df));

            row.add(safeFormat(entry.getDtmDataProcessingCumulative(), df));
            row.add(safeFormatPercentage(entry.getDtmDataProcessingPercentage(), df));

            row.add(safeFormat(entry.getMeshDataProcessingCumulative(), df));
            row.add(safeFormatPercentage(entry.getMeshDataProcessingPercentage(), df));

            // QA/QC Details
            row.add(safeFormat(entry.getSeparateOriQaqcDataProcessingCumulative(), df));
            row.add(safeFormatPercentage(entry.getSeparateOriQaqcDataProcessingPercentage(), df));

            row.add(safeFormat(entry.getDemDataProcessingQaqcCumulative(), df));
            row.add(safeFormatPercentage(entry.getDemDataProcessingQaqcPercentage(), df));

            row.add(safeFormat(entry.getDsmDataProcessingQaqcCumulative(), df));
            row.add(safeFormatPercentage(entry.getDsmDataProcessingQaqcPercentage(), df));

            row.add(safeFormat(entry.getDtmDataProcessingQaqcCumulative(), df));
            row.add(safeFormatPercentage(entry.getDtmDataProcessingQaqcPercentage(), df));

            row.add(safeFormat(entry.getMeshDataProcessingQaqcCumulative(), df));
            row.add(safeFormatPercentage(entry.getMeshDataProcessingQaqcPercentage(), df));

            // Feature Extraction
            row.add(safeFormat(entry.getFeatureExtractionOriCumulativeCompleted(), df));
            row.add(safeFormatPercentage(entry.getFeatureExtractionOriPercentageCompleted(), df));

            row.add(safeFormat(entry.getFeatureExtractionOriQaqcCumulativeCompleted(), df));
            row.add(safeFormatPercentage(entry.getFeatureExtractionOriQaqcPercentageCompleted(), df));

            row.add(safeFormat(entry.getMeshFeatureExtractionCumulativeCompleted(), df));
            row.add(safeFormatPercentage(entry.getMeshFeatureExtractionPercentageCompleted(), df));

            row.add(safeFormat(entry.getMeshFeatureExtractionQaqcCumulativeCompleted(), df));
            row.add(safeFormatPercentage(entry.getMeshFeatureExtractionQaqcPercentageCompleted(), df));

            row.add(nonNull(entry.getTotalGC()));
            row.add("Yes".equalsIgnoreCase(entry.getOriReceivedBack()) ? "Yes" : "No");
            row.add("Yes".equalsIgnoreCase(entry.getOriSubmittedToState()) ? "Yes" : "No");
            reportDataList.add(row);
        });

        // Add Grand Total row
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
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            totalRow.add(String.valueOf(total.getGrandTotalTotalGC()));
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            totalRow.add("");
            while (totalRow.size() < col_head.length) {
                totalRow.add("");
            }

            reportDataList.add(totalRow);
        }

        pComponent.setReportDataList(reportDataList);
        PdfExporter.createPdf(pComponent, response);
    }



//    @GetMapping("/naksha-timeline")
//    public void getNakshaTimlineReportPdf(HttpServletResponse response, HttpSession session) {
//        PdfExporterComponent pComponent = new PdfExporterComponent();
//        String[] head = {
//                "Format 2: Map 1 (SoI) – Data Processing ,Timelines of Various Components"
//        };
//        pComponent.setReportHeading(head);
//        pComponent.setReportName("DataProcessing_TimelinesVariousComponents");
//        float col_width[] = {50f, 100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f,100f};
//        pComponent.setCol_width(col_width);
//        String[] col_head = {
//                "S.No",
//                "State/UT",
//                "ULB/Town",
//                "Date of Aerail Data Acquisition (Completion/ Tentative) ",
//                "Submission Date of  Milestone 2   (Completion / Tentative) ",
//                "QA/QC Date of of Milestone 2 ((Completion / Tentative)",
//                "Submission Date of 2D  FE on ORI  (Milestone 3A) ((Completion / Tentative)",
//                "QA/QC Date  of 2D FE on ORI (Milestone 3A) (Completion / Tentative)",
//                "Submission Date of 2D FE on Stereo  (Milestone 3A) (Completion / Tentative)",
//                "QA/QC Date of 2D FE on Stereo  (Milestone 3A) (Completion / Tentative)",
//                "Submission Date  of 3D Feature Extraction for Field Survey (Milestone 3A) (Completion / Tentative)",
//                "QA/QC Date of 3D Feature Extraction for Field Survey (Milestone 3A) ((Completion / Tentative)",
//                "Submission Date of Final 3D Feature Extraction (Milestone 3B) (Completion / Tentative)",
//                "QA/QC Date of Final 3D Feature Extraction (Milestone 3B) (Completion / Tentative)",
//
//        };
//        pComponent.setCol_head(col_head);
//        Integer[] rwspn2col = {};
//        Integer[] rwspn3col = {};
//        Integer[] rwspn4col = {};
//        Integer[] rwspn5col = {};
//        Integer[] simplecell = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
//        pComponent.setRowspn5(rwspn5col);
//        pComponent.setRowspn4(rwspn4col);
//        pComponent.setRowspn3(rwspn3col);
//        pComponent.setRowspn2(rwspn2col);
//        pComponent.setRowspan1(simplecell);
//        List<List<Integer>> colspanval = new ArrayList<List<Integer>>();
//        pComponent.setColumnspan(colspanval);
//        List<List<Integer>> colsBreak = new ArrayList<List<Integer>>();
//        pComponent.setColumnnumber(colsBreak);
//        List<List<String>> reportDataList = new ArrayList<>();
//        List<NakshaMISDataEntry> list = (List<NakshaMISDataEntry>) session.getAttribute("nakshaMISDataEntries");
//        List<NakshaMISDataEntry> grandTotal = (List<NakshaMISDataEntry>) session.getAttribute("grandTotal");
//        List<String> strings = new ArrayList<>();
//        AtomicInteger indexHolder = new AtomicInteger();
//        list.stream().forEachOrdered(gList -> {
//            strings.add(Integer.toString(indexHolder.incrementAndGet()));
//            strings.add(gList.getState().getName() != null ? gList.getState().getName() : "N/A");
//            strings.clear();
//        });
//        pComponent.setReportDataList(reportDataList);
//        PdfExporter.createPdf(pComponent, response);
//    }




    // ✅ Utility Methods for Safe Formatting

    private String safeFormat(Object value, DecimalFormat df) {
        try {
            if (value instanceof Number) {
                return df.format(value);
            }
        } catch (Exception e) {
            // log if necessary
        }
        return "0.00";
    }

    private String safeFormatPercentage(Object value, DecimalFormat df) {
        try {
            if (value instanceof Number) {
                return df.format(value) + "%";
            }
        } catch (Exception e) {
            // log if necessary
        }
        return "0.00%";
    }

    private String nonNull(String value) {
        return value != null ? value : "NA";
    }

}

