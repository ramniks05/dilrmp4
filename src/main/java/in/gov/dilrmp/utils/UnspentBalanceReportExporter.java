package in.gov.dilrmp.utils;

import in.gov.dilrmp.component.ExcelExporterComponent;
import in.gov.dilrmp.component.PdfExporterComponent;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.unspentBalance.UnspentBalanceRow;
import in.gov.dilrmp.services.unspentBalance.UnspentBalanceService;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class UnspentBalanceReportExporter {

    private static final DateTimeFormatter UPDATED_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
    private static final int MAX_SHEET_NAME_LENGTH = 31;

    private UnspentBalanceReportExporter() {
    }

    public static String lastUpdatedText(LocalDateTime lastUpdatedOn) {
        return lastUpdatedOn == null ? "Not yet submitted" : lastUpdatedOn.format(UPDATED_FORMAT);
    }

    private static String reportName(String stateName) {
        String safeState = stateName == null ? "State" : stateName.replaceAll("[^A-Za-z0-9]+", "_");
        String name = "Unspent_Balance_" + safeState;
        return name.length() > MAX_SHEET_NAME_LENGTH ? name.substring(0, MAX_SHEET_NAME_LENGTH) : name;
    }

    private static String stateLine(String stateName, LocalDateTime lastUpdatedOn) {
        return "State / UT: " + stateName + "      Last updated by State: " + lastUpdatedText(lastUpdatedOn);
    }

    private static String referenceLine() {
        return "Ref: " + UnspentBalanceService.LETTER_REFERENCE + ". " + UnspentBalanceService.COMPONENT_NOTE;
    }

    public static void writePdf(HttpServletResponse response, String stateName, String amountHeader,
                                LocalDateTime lastUpdatedOn, List<UnspentBalanceRow> rows) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        pComponent.setReportHeading(new String[]{
                UnspentBalanceService.REPORT_TITLE,
                stateLine(stateName, lastUpdatedOn),
                referenceLine()
        });
        pComponent.setReportName(reportName(stateName));
        pComponent.setCol_width(new float[]{12f, 60f, 28f});
        pComponent.setCol_head(new String[]{ReportLabels.SERIAL_NUMBER, "Component", amountHeader});
        pComponent.setRowspan1(new Integer[]{});
        pComponent.setRowspn2(new Integer[]{});
        pComponent.setRowspn3(new Integer[]{});
        pComponent.setRowspn4(new Integer[]{});
        pComponent.setRowspn5(new Integer[]{});
        pComponent.setColumnspan(new ArrayList<>());
        pComponent.setColumnnumber(new ArrayList<>());
        pComponent.setColumnBreakCountNo(0);

        List<List<String>> reportDataList = new ArrayList<>();
        List<List<String>> grandTotal = new ArrayList<>();
        for (UnspentBalanceRow row : rows) {
            if (UnspentBalanceRow.TYPE_TOTAL.equals(row.getRowType())) {
                grandTotal.add(toCells(row));
            } else {
                reportDataList.add(toCells(row));
            }
        }
        pComponent.setReportDataList(reportDataList);
        pComponent.setGrandTotal(grandTotal);
        PdfExporter.createPdf(pComponent, response);
    }

    public static void writeExcel(HttpServletResponse response, String stateName, String amountHeader,
                                  LocalDateTime lastUpdatedOn, List<UnspentBalanceRow> rows) throws IOException {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        eComponent.setHeaderText(new String[]{
                "Department of Land Resources",
                "Ministry of Rural Development, Government of India",
                "Digital India Land Records Modernization Programme (DILRMP)",
                UnspentBalanceService.REPORT_TITLE,
                stateLine(stateName, lastUpdatedOn),
                referenceLine(),
                ReportLabels.SERIAL_NUMBER,
                "Component",
                amountHeader
        });
        eComponent.setHeaderSpanMerged(new String[]{"A1:C1", "A2:C2", "A3:C3", "A4:C4", "A5:C5", "A6:C6"});
        eComponent.setHeaderSpanUnMerged(new String[]{"A7", "B7", "C7"});
        eComponent.setReportName(reportName(stateName));
        eComponent.setNoOfColumns(3);
        eComponent.setNoOfheaderRows(7);

        List<List<String>> reportDataList = new ArrayList<>();
        for (UnspentBalanceRow row : rows) {
            reportDataList.add(toCells(row));
        }
        eComponent.setReportDataList(reportDataList);
        ExcelExporter.generateExcelFile(response, eComponent);
    }

    private static List<String> toCells(UnspentBalanceRow row) {
        List<String> cells = new ArrayList<>();
        cells.add(row.getSerialNo() != null ? row.getSerialNo() : "");
        cells.add(row.getLabel());
        cells.add(row.getAmountText());
        return cells;
    }
}
