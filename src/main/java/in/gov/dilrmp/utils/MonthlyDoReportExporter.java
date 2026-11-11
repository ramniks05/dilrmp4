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
import in.gov.dilrmp.services.dolr.MonthlyDoReportService.Report;
import in.gov.dilrmp.services.dolr.MonthlyDoReportService.Row;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

public final class MonthlyDoReportExporter {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
    private static final Font SUB_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
    private static final Font DATA_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);

    private MonthlyDoReportExporter() {
    }

    public static void writePdf(HttpServletResponse response, Report report) throws IOException {
        String fileName = "Monthly_DO_Report_" + report.month + ".pdf";
        Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            PdfExporter.addHeaderToPdf(document);

            Paragraph title = new Paragraph("Information to be provided Monthly", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            Paragraph programme = new Paragraph("Land Records Modernization programme", SUB_FONT);
            programme.setAlignment(Element.ALIGN_CENTER);
            document.add(programme);
            Paragraph month = new Paragraph("Month: " + report.monthLabel, SUB_FONT);
            month.setAlignment(Element.ALIGN_CENTER);
            document.add(month);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[]{8f, 28f, 14f, 14f, 14f, 14f, 14f, 12f});
            table.setWidthPercentage(100f);
            table.setHeaderRows(1);
            addPdfHeader(table, "S. No");
            addPdfHeader(table, "Indicator");
            addPdfHeader(table, "Previous Month\n(as on " + report.previousAsOnLabel + ")");
            addPdfHeader(table, "Current Month\n(as on " + report.currentAsOnLabel + ")");
            addPdfHeader(table, "Current Month\nPrevious FY\n(as on " + report.previousFyAsOnLabel + ")");
            addPdfHeader(table, "Year to Date\n(Current FY)");
            addPdfHeader(table, "Year to Date\n(Previous FY)");
            addPdfHeader(table, "% change in YTD\nfrom previous FY");

            for (Row row : report.rows) {
                table.addCell(pdfCell(row.sno, DATA_FONT, Element.ALIGN_CENTER));
                table.addCell(pdfCell(row.indicator, DATA_FONT, Element.ALIGN_LEFT));
                table.addCell(pdfCell(row.previousMonthFormatted, DATA_FONT, Element.ALIGN_RIGHT));
                table.addCell(pdfCell(row.currentMonthFormatted, DATA_FONT, Element.ALIGN_RIGHT));
                table.addCell(pdfCell(row.currentMonthPreviousFyFormatted, DATA_FONT, Element.ALIGN_RIGHT));
                table.addCell(pdfCell(row.ytdCurrentFyFormatted, DATA_FONT, Element.ALIGN_RIGHT));
                table.addCell(pdfCell(row.ytdPreviousFyFormatted, DATA_FONT, Element.ALIGN_RIGHT));
                table.addCell(pdfCell(row.percentChangeFormatted, DATA_FONT, Element.ALIGN_CENTER));
            }
            document.add(table);

            Paragraph note = new Paragraph(report.note, DATA_FONT);
            note.setSpacingBefore(10f);
            document.add(note);
        } catch (Exception e) {
            throw new IOException("Could not create Monthly DO Report PDF", e);
        } finally {
            document.close();
        }
    }

    public static void writeExcel(HttpServletResponse response, Report report) throws IOException {
        String fileName = "Monthly_DO_Report_" + report.month + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Monthly DO Report");
            CellStyle titleStyle = workbook.createCellStyle();
            XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 12);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 9);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setWrapText(true);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.RIGHT);
            CellStyle leftStyle = workbook.createCellStyle();
            leftStyle.setAlignment(HorizontalAlignment.LEFT);

            org.apache.poi.ss.usermodel.Row r0 = sheet.createRow(0);
            Cell c0 = r0.createCell(0);
            c0.setCellValue("Information to be provided Monthly");
            c0.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            org.apache.poi.ss.usermodel.Row r1 = sheet.createRow(1);
            Cell c1 = r1.createCell(0);
            c1.setCellValue("Land Records Modernization programme — Month: " + report.monthLabel);
            c1.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

            String[] headers = {
                    "S. No",
                    "Indicator",
                    "Previous Month (as on " + report.previousAsOnLabel + ")",
                    "Current Month (as on " + report.currentAsOnLabel + ")",
                    "Current Month Previous FY (as on " + report.previousFyAsOnLabel + ")",
                    "Year to Date (Current FY)",
                    "Year to Date (Previous FY)",
                    "% change in YTD from previous FY"
            };
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(3);
            headerRow.setHeightInPoints(36);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 4;
            for (Row row : report.rows) {
                org.apache.poi.ss.usermodel.Row excelRow = sheet.createRow(rowIdx++);
                excelRow.createCell(0).setCellValue(row.sno);
                Cell indicator = excelRow.createCell(1);
                indicator.setCellValue(row.indicator);
                indicator.setCellStyle(leftStyle);
                setRight(excelRow, 2, row.previousMonthFormatted, dataStyle);
                setRight(excelRow, 3, row.currentMonthFormatted, dataStyle);
                setRight(excelRow, 4, row.currentMonthPreviousFyFormatted, dataStyle);
                setRight(excelRow, 5, row.ytdCurrentFyFormatted, dataStyle);
                setRight(excelRow, 6, row.ytdPreviousFyFormatted, dataStyle);
                setRight(excelRow, 7, row.percentChangeFormatted, dataStyle);
            }

            org.apache.poi.ss.usermodel.Row noteRow = sheet.createRow(rowIdx + 1);
            noteRow.createCell(0).setCellValue(report.note);
            sheet.addMergedRegion(new CellRangeAddress(rowIdx + 1, rowIdx + 1, 0, 7));

            sheet.setColumnWidth(0, 2500);
            sheet.setColumnWidth(1, 11000);
            for (int i = 2; i <= 7; i++) {
                sheet.setColumnWidth(i, 5200);
            }
            workbook.write(response.getOutputStream());
        }
    }

    private static void addPdfHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(new BaseColor(255, 244, 214));
        cell.setPadding(4f);
        table.addCell(cell);
    }

    private static PdfPCell pdfCell(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(3f);
        return cell;
    }

    private static void setRight(org.apache.poi.ss.usermodel.Row row, int index, String value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }
}
