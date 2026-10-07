package in.gov.dilrmp.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.unspentBalance.UnspentBalanceRow;
import in.gov.dilrmp.services.unspentBalance.UnspentBalanceService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class UnspentBalanceReportExporter {

    private static final Logger logger = LoggerFactory.getLogger(UnspentBalanceReportExporter.class);
    private static final int MAX_SHEET_NAME_LENGTH = 31;

    private static final Font HEADING_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
    private static final Font DATA_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
    private static final Font BOLD_DATA_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
    private static final Font GRAND_TOTAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final BaseColor HIGHLIGHT_BG = new BaseColor(235, 235, 235);

    private UnspentBalanceReportExporter() {
    }

    private static String reportName(String stateName) {
        String safeState = stateName == null ? "State" : stateName.replaceAll("[^A-Za-z0-9]+", "_");
        String name = "Unspent_Balance_" + safeState;
        return name.length() > MAX_SHEET_NAME_LENGTH ? name.substring(0, MAX_SHEET_NAME_LENGTH) : name;
    }

    private static String stateLine(String stateName) {
        return "State / UT: " + stateName;
    }

    private static boolean isBoldRow(UnspentBalanceRow row) {
        return row.isGroup() || row.isTotal();
    }

    private static boolean isHighlightedRow(UnspentBalanceRow row) {
        return row.isGroup() || UnspentBalanceRow.TYPE_SUBTOTAL.equals(row.getRowType());
    }

    public static void writePdf(HttpServletResponse response, String stateName, String amountHeader,
                                List<UnspentBalanceRow> rows) {
        Document document = new Document(PageSize.A4);
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + reportName(stateName) + ".pdf");
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            PdfExporter.addHeaderToPdf(document);

            for (String heading : new String[]{UnspentBalanceService.REPORT_TITLE, stateLine(stateName)}) {
                Paragraph paragraph = new Paragraph(heading, HEADING_FONT);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                document.add(paragraph);
            }
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[]{12f, 60f, 28f});
            table.setWidthPercentage(100f);
            table.setHeaderRows(2);
            String[] headers = {ReportLabels.SERIAL_NUMBER, "Component", amountHeader};
            for (String header : headers) {
                table.addCell(pdfCell(header, HEADING_FONT, null));
            }
            for (int i = 1; i <= headers.length; i++) {
                table.addCell(pdfCell("(" + i + ")", HEADING_FONT, null));
            }

            for (UnspentBalanceRow row : rows) {
                Font font = UnspentBalanceRow.TYPE_TOTAL.equals(row.getRowType()) ? GRAND_TOTAL_FONT
                        : isBoldRow(row) ? BOLD_DATA_FONT : DATA_FONT;
                BaseColor background = isHighlightedRow(row) ? HIGHLIGHT_BG : null;
                table.addCell(pdfCell(row.getSerialNo(), font, background));
                PdfPCell label = pdfCell(row.getLabel(), font, background);
                label.setHorizontalAlignment(Element.ALIGN_LEFT);
                label.setPaddingLeft(UnspentBalanceRow.TYPE_SUB_ITEM.equals(row.getRowType()) ? 14f : 4f);
                table.addCell(label);
                table.addCell(pdfCell(row.getAmountText(), font, background));
            }
            document.add(table);
            PdfExporter.addFotterToPdf(document);
        } catch (Exception e) {
            logger.error("Error generating unspent balance PDF", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private static PdfPCell pdfCell(String text, Font font, BaseColor background) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        if (background != null) {
            cell.setBackgroundColor(background);
        }
        return cell;
    }

    public static void writeExcel(HttpServletResponse response, String stateName, String amountHeader,
                                  List<UnspentBalanceRow> rows) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + reportName(stateName) + ".xlsx");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet(reportName(stateName));
            CellStyle headerStyle = excelStyle(workbook, true, false);
            CellStyle boldStyle = excelStyle(workbook, true, false);
            CellStyle highlightStyle = excelStyle(workbook, true, true);
            CellStyle labelStyle = leftAligned(excelStyle(workbook, false, false), (short) 0);
            CellStyle subItemLabelStyle = leftAligned(excelStyle(workbook, false, false), (short) 2);
            CellStyle boldLabelStyle = leftAligned(excelStyle(workbook, true, false), (short) 0);
            CellStyle highlightLabelStyle = leftAligned(excelStyle(workbook, true, true), (short) 0);

            String[] titles = {
                    "Department of Land Resources",
                    "Ministry of Rural Development, Government of India",
                    "Digital India Land Records Modernization Programme (DILRMP)",
                    UnspentBalanceService.REPORT_TITLE,
                    stateLine(stateName)
            };
            int rowIndex = 0;
            for (String title : titles) {
                Row row = sheet.createRow(rowIndex);
                excelCell(row, 0, title, headerStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 2));
                rowIndex++;
            }

            Row header = sheet.createRow(rowIndex++);
            excelCell(header, 0, ReportLabels.SERIAL_NUMBER, headerStyle);
            excelCell(header, 1, "Component", headerStyle);
            excelCell(header, 2, amountHeader, headerStyle);

            for (UnspentBalanceRow data : rows) {
                Row row = sheet.createRow(rowIndex++);
                CellStyle style = isHighlightedRow(data) ? highlightStyle : isBoldRow(data) ? boldStyle : null;
                CellStyle labelCellStyle = isHighlightedRow(data) ? highlightLabelStyle
                        : isBoldRow(data) ? boldLabelStyle
                        : UnspentBalanceRow.TYPE_SUB_ITEM.equals(data.getRowType()) ? subItemLabelStyle : labelStyle;
                excelCell(row, 0, data.getSerialNo(), style);
                excelCell(row, 1, data.getLabel(), labelCellStyle);
                excelCell(row, 2, data.getAmountText(), style);
            }

            Row source = sheet.createRow(rowIndex++);
            excelCell(source, 1, "Source: http://dilrmp.gov.in", headerStyle);
            Row dated = sheet.createRow(rowIndex);
            excelCell(dated, 1, "Dated: " + new SimpleDateFormat("dd/MMM/yyyy  hh:mm:ss").format(new Date()), headerStyle);

            sheet.setColumnWidth(0, 10 * 256);
            sheet.setColumnWidth(1, 80 * 256);
            sheet.setColumnWidth(2, 36 * 256);

            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        }
    }

    public static void writeAllStatesExcel(HttpServletResponse response, String amountHeader,
                                           Map<String, List<UnspentBalanceRow>> rowsByState) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=Unspent_Balance_All_States.xlsx");

        List<String> stateNames = new ArrayList<>(rowsByState.keySet());
        List<UnspentBalanceRow> structure = stateNames.isEmpty() ? new ArrayList<>() : rowsByState.get(stateNames.get(0));
        int totalColumn = stateNames.size() + 2;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Unspent_Balance_All_States");
            short amountFormat = workbook.createDataFormat().getFormat("0.00");
            CellStyle headerStyle = excelStyle(workbook, true, false);
            CellStyle labelStyle = leftAligned(excelStyle(workbook, false, false), (short) 0);
            CellStyle subItemLabelStyle = leftAligned(excelStyle(workbook, false, false), (short) 2);
            CellStyle boldLabelStyle = leftAligned(excelStyle(workbook, true, true), (short) 0);
            CellStyle amountStyle = excelStyle(workbook, false, false);
            amountStyle.setDataFormat(amountFormat);
            CellStyle boldAmountStyle = excelStyle(workbook, true, true);
            boldAmountStyle.setDataFormat(amountFormat);

            String[] titles = {
                    "Department of Land Resources",
                    "Ministry of Rural Development, Government of India",
                    "Digital India Land Records Modernization Programme (DILRMP)",
                    UnspentBalanceService.REPORT_TITLE + " \u2013 All States / UTs",
                    amountHeader
            };
            int rowIndex = 0;
            for (String title : titles) {
                Row row = sheet.createRow(rowIndex);
                excelCell(row, 0, title, headerStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, totalColumn));
                rowIndex++;
            }

            Row header = sheet.createRow(rowIndex++);
            excelCell(header, 0, ReportLabels.SERIAL_NUMBER, headerStyle);
            excelCell(header, 1, "Component", headerStyle);
            for (int s = 0; s < stateNames.size(); s++) {
                excelCell(header, s + 2, stateNames.get(s), headerStyle);
            }
            excelCell(header, totalColumn, "Total", headerStyle);
            int headerRowIndex = rowIndex - 1;

            for (int r = 0; r < structure.size(); r++) {
                UnspentBalanceRow template = structure.get(r);
                boolean bold = isBoldRow(template);
                Row row = sheet.createRow(rowIndex++);
                excelCell(row, 0, template.getSerialNo(), bold ? boldAmountStyle : null);
                excelCell(row, 1, template.getLabel(), bold ? boldLabelStyle
                        : UnspentBalanceRow.TYPE_SUB_ITEM.equals(template.getRowType()) ? subItemLabelStyle : labelStyle);
                BigDecimal rowTotal = null;
                for (int s = 0; s < stateNames.size(); s++) {
                    BigDecimal amount = rowsByState.get(stateNames.get(s)).get(r).getAmount();
                    amountCell(row, s + 2, amount, bold ? boldAmountStyle : amountStyle);
                    if (amount != null) {
                        rowTotal = rowTotal == null ? amount : rowTotal.add(amount);
                    }
                }
                amountCell(row, totalColumn, rowTotal, boldAmountStyle);
            }

            Row source = sheet.createRow(rowIndex++);
            excelCell(source, 1, "Source: http://dilrmp.gov.in", headerStyle);
            Row dated = sheet.createRow(rowIndex);
            excelCell(dated, 1, "Dated: " + new SimpleDateFormat("dd/MMM/yyyy  hh:mm:ss").format(new Date()), headerStyle);

            sheet.setColumnWidth(0, 8 * 256);
            sheet.setColumnWidth(1, 60 * 256);
            for (int c = 2; c <= totalColumn; c++) {
                sheet.setColumnWidth(c, 16 * 256);
            }
            sheet.createFreezePane(2, headerRowIndex + 1);

            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        }
    }

    private static void amountCell(Row row, int column, BigDecimal amount, CellStyle style) {
        Cell cell = row.createCell(column);
        if (amount != null) {
            cell.setCellValue(amount.doubleValue());
        }
        cell.setCellStyle(style);
    }

    private static CellStyle excelStyle(XSSFWorkbook workbook, boolean bold, boolean highlight) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        XSSFFont font = workbook.createFont();
        font.setBold(bold);
        style.setFont(font);
        if (highlight) {
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }

    private static CellStyle leftAligned(CellStyle style, short indent) {
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setIndention(indent);
        return style;
    }

    private static void excelCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(ExcelExporter.sanitizeCsvCell(value == null ? "" : value));
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
}
