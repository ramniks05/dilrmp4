package in.gov.dilrmp.utils;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import in.gov.dilrmp.component.ExcelExporterComponent;

public class ExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public ExcelExporter() {
        this.workbook = new XSSFWorkbook();
    }

    int rowNumber;
    Row row;
    CellReference cr;
    Cell cell;
    CellStyle style;
    int i;
    int cellnum;

    private void writeHeader(ExcelExporterComponent eComponent) {
        sheet = workbook.createSheet(eComponent.getReportName());
        style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        this.rowNumber = eComponent.getNoOfheaderRows();

        Boolean p = false;
        // Create rows based on hedarHeight Parameter value
        for (i = 0; i <= eComponent.getNoOfheaderRows(); i++) {
            row = sheet.createRow(i);
        }
        i = 0;
        // Create merged header based on headerSpanMerged parameter value
        for (String span : eComponent.getHeaderSpanMerged()) {
            sheet.addMergedRegion(CellRangeAddress.valueOf(span));
            cr = new CellReference(span.split(":")[0]);
            row = sheet.getRow(cr.getRow());
            cell = row.createCell(cr.getCol());
            if (eComponent.getHeaderText()[i].equals("") && p == false) {
                cell.setCellValue("");
                cell.setCellStyle(style);
                p = true;
            } else {
                cell.setCellValue(sanitizeCsvCell(eComponent.getHeaderText()[i]));
                cell.setCellStyle(style);
            }
            i++;
        }
        // Create unmerged header based on headerSpanUnMerged parameter value
        for (String span : eComponent.getHeaderSpanUnMerged()) {
            cr = new CellReference(span);
            row = sheet.getRow(cr.getRow());
            cell = row.createCell(cr.getCol());
            cell.setCellValue(sanitizeCsvCell(eComponent.getHeaderText()[i]));
            cell.setCellStyle(style);
            i++;
        }
    }

    // fill data in excel
    private void write(ExcelExporterComponent eComponent) {
        boolean isDoubleInt;
                  
        if (eComponent.getReportDataList() != null) {
            for (List<String> s: eComponent.getReportDataList()) {
                row = sheet.createRow(this.rowNumber++);
                cellnum = 0;
                for(String s1:s) {
                    cell = row.createCell(cellnum++);
                    cell.setCellValue(sanitizeCsvCell(s1));
                }
                
               
            }
        }
    }




//    private void writeHeader(ExcelExporterComponent eComponent) {
//        sheet = workbook.createSheet(eComponent.getReportName());
//
//        // Style for header cells
//        style = workbook.createCellStyle();
//        style.setAlignment(HorizontalAlignment.CENTER);
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//        style.setWrapText(true);
//
//        XSSFFont font = workbook.createFont();
//        font.setBold(true);
//        style.setFont(font);
//
//        this.rowNumber = eComponent.getNoOfheaderRows();
//        Boolean p = false;
//
//        // Create rows for header
//        for (int rowIndex = 0; rowIndex <= eComponent.getNoOfheaderRows(); rowIndex++) {
//            sheet.createRow(rowIndex);
//        }
//
//        int i = 0;
//
//        // Merged headers
//        for (String span : eComponent.getHeaderSpanMerged()) {
//            sheet.addMergedRegion(CellRangeAddress.valueOf(span));
//            CellReference cr = new CellReference(span.split(":")[0]);
//            Row row = sheet.getRow(cr.getRow());
//            Cell cell = row.createCell(cr.getCol());
//
//            String headerText = eComponent.getHeaderText()[i];
//
//            // Insert line breaks for readability (wrap text)
////            headerText = insertLineBreaks(headerText, 20);
//
//            if (i >= 5) {
//                headerText = insertLineBreaks(headerText, 20);
//            }
//
//
//            if (headerText.equals("") && !p) {
//                cell.setCellValue("");
//                p = true;
//            } else {
//                cell.setCellValue(headerText);
//            }
//
//            cell.setCellStyle(style);
//            row.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * headerText.split("\n").length);
//
//            i++;
//        }
//
//        // Unmerged headers
//        for (String span : eComponent.getHeaderSpanUnMerged()) {
//            CellReference cr = new CellReference(span);
//            Row row = sheet.getRow(cr.getRow());
//            Cell cell = row.createCell(cr.getCol());
//
//            String headerText = insertLineBreaks(eComponent.getHeaderText()[i], 20);
//
//            cell.setCellValue(headerText);
//            cell.setCellStyle(style);
//            row.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * headerText.split("\n").length);
//
//            sheet.autoSizeColumn(cr.getCol());
//            i++;
//        }
//    }
//
//    // Helper method to insert line breaks every N characters
//    private String insertLineBreaks(String text, int maxLineLength) {
//        if (text == null) return "";
//        StringBuilder sb = new StringBuilder();
//        int index = 0;
//        while (index < text.length()) {
//            sb.append(text, index, Math.min(index + maxLineLength, text.length()));
//            sb.append("\n");
//            index += maxLineLength;
//        }
//        return sb.toString().trim();
//    }
//
//    // Fill data in excel
//    private void write(ExcelExporterComponent eComponent) {
//        if (eComponent.getReportDataList() != null) {
//
//            // Style for data cells
//            CellStyle dataStyle = workbook.createCellStyle();
//            dataStyle.setWrapText(true);
//            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//            XSSFFont dataFont = workbook.createFont();
//            dataFont.setFontHeightInPoints((short)10); // adjust font size as needed
//            dataStyle.setFont(dataFont);
//
//            for (List<String> s : eComponent.getReportDataList()) {
//                row = sheet.createRow(this.rowNumber++);
//                cellnum = 0;
//                int maxLines = 1;
//
//                for (String s1 : s) {
//                    cell = row.createCell(cellnum++);
//
//                    // Wrap data content every 50 characters
//                    String cellText = insertLineBreaks(sanitizeCsvCell(s1), 50);
//                    cell.setCellValue(cellText);
//                    cell.setCellStyle(dataStyle);
//
//                    int lines = cellText.split("\n").length;
//                    if (lines > maxLines) maxLines = lines;
//                }
//
//                // Adjust row height based on max lines in this row
//                row.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * maxLines);
//            }
//        }
//    }



    private void writeFooter(ExcelExporterComponent eComponent) {
        row = sheet.createRow(this.rowNumber++);
        cell = row.createCell(1);
        cell.setCellValue("Source: http://dilrmp.gov.in");
        cell.setCellStyle(style);
        row = sheet.createRow(rowNumber++);
        cell = row.createCell(1);
        Date timestamp = new Date();
        SimpleDateFormat timestampFormat = new SimpleDateFormat("dd/MMM/yyyy  hh:mm:ss");
        cell.setCellValue("Dated: " + timestampFormat.format(timestamp));
        cell.setCellStyle(style);
        for (i = 0; i < eComponent.getNoOfColumns(); i++) {
            sheet.autoSizeColumn(i, true);
            // sheet.setColumnWidth(i, size);

        }
    }

    public static void generateExcelFile(HttpServletResponse response, ExcelExporterComponent eComponent)
            throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + eComponent.getReportName() + ".xlsx";
        response.setHeader(headerKey, headerValue);
        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.writeHeader(eComponent);
        excelExporter.write(eComponent);
        excelExporter.writeFooter(eComponent);
        ServletOutputStream outputStream = response.getOutputStream();
        excelExporter.workbook.write(outputStream);
        excelExporter.workbook.close();
        outputStream.close();
    }






//    public static String sanitizeCsvCell(String cellValue) {
//        /*System.out.println(" Before sanitizeCsvCell "+cellValue);*/
//        if (cellValue != null) {
//            // Remove potentially harmful characters
//            cellValue = cellValue.replaceAll("[=+\\-@\\t\\r\n*]", "");
//
//            // If the input starts with a single quote, prepend another single quote
//            if (!cellValue.isEmpty() && cellValue.charAt(0) == '\'') {
//                cellValue = "'" + cellValue;
//            }
//        }
//
//        return cellValue;
//    }



    public static String sanitizeCsvCell(String cellValue) {
        /*System.out.println(" Before sanitizeCsvCell "+cellValue);*/
        if (cellValue != null) {
            // Check if the value starts with dangerous formula characters
            // Remove only if they appear at the start (formula injection prevention)
            if (cellValue.length() > 0) {
                char firstChar = cellValue.charAt(0);
                // If it starts with =, +, or @, remove the dangerous character at the start
                if (firstChar == '=' || firstChar == '+' || firstChar == '@') {
                    // Remove the dangerous character at the start
                    cellValue = cellValue.substring(1);
                }
                // Note: We preserve minus signs (-) as they are needed for negative numbers
                // Only remove - if it's followed by =, +, or @ (formula injection pattern)
                else if (firstChar == '-' && cellValue.length() > 1) {
                    char secondChar = cellValue.charAt(1);
                    if (secondChar == '=' || secondChar == '+' || secondChar == '@') {
                        // Remove - followed by dangerous formula character
                        cellValue = cellValue.substring(2);
                    }
                    // Otherwise, preserve the minus sign (it's a negative number)
                }
            }
            
            // Remove other potentially harmful characters (tabs, newlines, asterisks)
            // Preserve minus signs as they are needed for negative numbers
            cellValue = cellValue.replaceAll("[\\t\\r\\n*]", "");

            // If the input starts with a single quote, prepend another single quote
            if (!cellValue.isEmpty() && cellValue.charAt(0) == '\'') {
                cellValue = "'" + cellValue;
            }
        }

        return cellValue;
    }


}
