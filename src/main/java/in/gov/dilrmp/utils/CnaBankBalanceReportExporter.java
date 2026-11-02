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
import in.gov.dilrmp.models.cna.CnaBankBalanceReport;
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
import java.util.Date;
import java.util.List;

public final class CnaBankBalanceReportExporter {

    private static final Logger logger = LoggerFactory.getLogger(CnaBankBalanceReportExporter.class);
    private static final String REPORT_TITLE = "CNA Bank Balance Report";
    private static final String[] COLUMNS = {
            ReportLabels.SERIAL_NUMBER,
            "State / UT",
            "CNA Code",
            "CNA Name",
            "Bank Balance (in Crore)"
    };

    private static final Font HEADING_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
    private static final Font DATA_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);

    private CnaBankBalanceReportExporter() {
    }

    public static void writePdf(HttpServletResponse response, List<CnaBankBalanceReport> reports, String balanceHeading)
            throws IOException {
        String fileName = fileName(balanceHeading, "pdf");
        Document document = new Document(PageSize.A4.rotate());
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            PdfExporter.addHeaderToPdf(document);

            for (String heading : new String[]{REPORT_TITLE, balanceHeading}) {
                Paragraph paragraph = new Paragraph(heading, HEADING_FONT);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                document.add(paragraph);
            }
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[]{8f, 28f, 20f, 46f, 22f});
            table.setWidthPercentage(100f);
            table.setHeaderRows(2);
            for (String header : COLUMNS) {
                table.addCell(pdfCell(header, HEADER_FONT));
            }
            for (int i = 1; i <= COLUMNS.length; i++) {
                table.addCell(pdfCell("(" + i + ")", HEADER_FONT));
            }
            int serial = 1;
            for (CnaBankBalanceReport report : reports) {
                table.addCell(pdfCell(String.valueOf(serial++), DATA_FONT));
                PdfPCell state = pdfCell(stateName(report), DATA_FONT);
                state.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(state);
                table.addCell(pdfCell(report.getCnaCode(), DATA_FONT));
                PdfPCell name = pdfCell(report.getCnaName(), DATA_FONT);
                name.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(name);
                table.addCell(pdfCell(balance(report), DATA_FONT));
            }
            if (!reports.isEmpty()) {
                BaseColor totalBg = new BaseColor(255, 244, 230);
                table.addCell(shadedCell("", HEADER_FONT, totalBg, Element.ALIGN_CENTER));
                table.addCell(shadedCell("Total", HEADER_FONT, totalBg, Element.ALIGN_LEFT));
                table.addCell(shadedCell("", HEADER_FONT, totalBg, Element.ALIGN_CENTER));
                table.addCell(shadedCell("", HEADER_FONT, totalBg, Element.ALIGN_CENTER));
                table.addCell(shadedCell(totalBalance(reports), HEADER_FONT, totalBg, Element.ALIGN_CENTER));
            }
            document.add(table);
            PdfExporter.addFotterToPdf(document);
        } catch (Exception e) {
            logger.error("Error generating CNA bank balance PDF", e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to generate PDF.");
            }
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    public static void writeExcel(HttpServletResponse response, List<CnaBankBalanceReport> reports, String balanceHeading)
            throws IOException {
        String fileName = fileName(balanceHeading, "xlsx");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("CNA Bank Balance");
            CellStyle headerStyle = excelStyle(workbook, true, true);
            CellStyle titleStyle = excelStyle(workbook, true, false);
            CellStyle leftStyle = excelStyle(workbook, false, false);
            leftStyle.setAlignment(HorizontalAlignment.LEFT);
            CellStyle totalStyle = excelStyle(workbook, true, false);
            totalStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            CellStyle totalLeftStyle = excelStyle(workbook, true, false);
            totalLeftStyle.setAlignment(HorizontalAlignment.LEFT);
            totalLeftStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            totalLeftStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] titles = {
                    "Department of Land Resources",
                    "Ministry of Rural Development, Government of India",
                    "Digital India Land Records Modernization Programme (DILRMP)",
                    REPORT_TITLE,
                    balanceHeading
            };
            int rowIndex = 0;
            int lastColumn = COLUMNS.length - 1;
            for (String title : titles) {
                Row row = sheet.createRow(rowIndex);
                excelCell(row, 0, title, titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, lastColumn));
                rowIndex++;
            }

            Row header = sheet.createRow(rowIndex++);
            for (int i = 0; i < COLUMNS.length; i++) {
                excelCell(header, i, COLUMNS[i], headerStyle);
            }
            int headerRow = rowIndex - 1;

            int serial = 1;
            for (CnaBankBalanceReport report : reports) {
                Row row = sheet.createRow(rowIndex++);
                excelCell(row, 0, String.valueOf(serial++), null);
                excelCell(row, 1, stateName(report), leftStyle);
                excelCell(row, 2, report.getCnaCode(), null);
                excelCell(row, 3, report.getCnaName(), null);
                excelCell(row, 4, balance(report), null);
            }
            if (!reports.isEmpty()) {
                Row total = sheet.createRow(rowIndex++);
                excelCell(total, 0, "", totalStyle);
                excelCell(total, 1, "Total", totalLeftStyle);
                excelCell(total, 2, "", totalStyle);
                excelCell(total, 3, "", totalStyle);
                excelCell(total, 4, totalBalance(reports), totalStyle);
            }

            Row source = sheet.createRow(rowIndex++);
            excelCell(source, 1, "Source: http://dilrmp.gov.in", titleStyle);
            Row dated = sheet.createRow(rowIndex);
            excelCell(dated, 1, "Dated: " + new SimpleDateFormat("dd/MMM/yyyy  hh:mm:ss").format(new Date()), titleStyle);

            int[] widths = {8, 28, 18, 55, 24};
            for (int i = 0; i < widths.length; i++) {
                sheet.setColumnWidth(i, widths[i] * 256);
            }
            sheet.createFreezePane(0, headerRow + 1);

            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        }
    }

    private static String fileName(String balanceHeading, String extension) {
        String safe = balanceHeading == null ? "CNA_Bank_Balance" : balanceHeading.replaceAll("[^A-Za-z0-9]+", "_");
        if (safe.length() > 80) {
            safe = safe.substring(0, 80);
        }
        return safe + "." + extension;
    }

    private static String stateName(CnaBankBalanceReport report) {
        return report.getStateName() != null ? report.getStateName() : "Not linked";
    }

    private static String balance(CnaBankBalanceReport report) {
        return report.getBankBalance() != null ? report.getBankBalance().toPlainString() : "-";
    }

    private static String totalBalance(List<CnaBankBalanceReport> reports) {
        BigDecimal total = BigDecimal.ZERO;
        for (CnaBankBalanceReport report : reports) {
            if (report.getBankBalance() != null) {
                total = total.add(report.getBankBalance());
            }
        }
        return total.toPlainString();
    }

    private static PdfPCell shadedCell(String text, Font font, BaseColor background, int alignment) {
        PdfPCell cell = pdfCell(text, font);
        cell.setBackgroundColor(background);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private static PdfPCell pdfCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(BaseColor.WHITE);
        return cell;
    }

    private static CellStyle excelStyle(XSSFWorkbook workbook, boolean bold, boolean header) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        XSSFFont font = workbook.createFont();
        font.setBold(bold);
        style.setFont(font);
        if (header) {
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
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
