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
import in.gov.dilrmp.models.user.AdminManagedUserRow;
import in.gov.dilrmp.models.user.Role;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class AdminUserListPdfExporter {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    private static final Font SUB_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD, BaseColor.WHITE);
    private static final Font DATA_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
    private static final DateTimeFormatter GENERATED_ON =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private AdminUserListPdfExporter() {
    }

    public static void write(HttpServletResponse response,
                             List<AdminManagedUserRow> rows,
                             String role,
                             String stateName) throws IOException {
        String fileName = fileName(role, stateName);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        byte[] pdf = toBytes(rows, role, stateName);
        response.getOutputStream().write(pdf);
    }

    public static byte[] toBytes(List<AdminManagedUserRow> rows, String role, String stateName) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(output, rows, role, stateName);
        return output.toByteArray();
    }

    public static String fileName(String role, String stateName) {
        String safeRole = role == null ? "users" : role.replaceAll("[^A-Za-z0-9_-]", "");
        if (stateName == null || stateName.isBlank()) {
            return "User_List_" + safeRole + ".pdf";
        }
        String safeState = stateName.replaceAll("[^A-Za-z0-9_-]", "_");
        return "User_List_" + safeRole + "_" + safeState + ".pdf";
    }

    private static void write(OutputStream output,
                              List<AdminManagedUserRow> rows,
                              String role,
                              String stateName) throws IOException {
        Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
        try {
            PdfWriter.getInstance(document, output);
            document.open();
            PdfExporter.addHeaderToPdf(document);

            Paragraph title = new Paragraph("User List", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(8f);
            document.add(title);

            String generatedOn = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).format(GENERATED_ON);
            Paragraph meta = new Paragraph(userLabel(role)
                    + (stateName == null || stateName.isBlank() ? "" : "    |    State/UT: " + stateName)
                    + "    |    Generated on: " + generatedOn, SUB_FONT);
            meta.setAlignment(Element.ALIGN_CENTER);
            meta.setSpacingBefore(4f);
            meta.setSpacingAfter(10f);
            document.add(meta);

            boolean showDistrict = Role.USER_ROLE_DISTRICT.equals(role);
            PdfPTable table = new PdfPTable(showDistrict
                    ? new float[]{8f, 24f, 26f, 26f, 14f, 16f}
                    : new float[]{8f, 28f, 32f, 16f, 16f});
            table.setWidthPercentage(100f);
            table.setHeaderRows(1);
            header(table, "S.No.");
            header(table, "Username");
            header(table, "State/UT");
            if (showDistrict) {
                header(table, "District Name");
            }
            header(table, "LGD Code");
            header(table, "Account Status");

            int serial = 1;
            for (AdminManagedUserRow row : rows) {
                cell(table, String.valueOf(serial++), Element.ALIGN_CENTER);
                cell(table, row.getUsername(), Element.ALIGN_LEFT);
                cell(table, row.getStateName(), Element.ALIGN_LEFT);
                if (showDistrict) {
                    cell(table, row.getDistrictName(), Element.ALIGN_LEFT);
                }
                cell(table, row.getLgdCode(), Element.ALIGN_CENTER);
                cell(table, row.getAccountStatus(), Element.ALIGN_CENTER);
            }
            if (rows.isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("No users found for the selected filters.", DATA_FONT));
                empty.setColspan(showDistrict ? 6 : 5);
                empty.setHorizontalAlignment(Element.ALIGN_CENTER);
                empty.setPadding(8f);
                table.addCell(empty);
            }
            document.add(table);
        } catch (Exception e) {
            throw new IOException("Could not create the user list PDF", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private static String userLabel(String role) {
        if (Role.USER_ROLE_STATE.equals(role)) {
            return "User : STATE";
        }
        if (Role.USER_ROLE_DISTRICT.equals(role)) {
            return "User : DISTRICT";
        }
        if (Role.USER_ROLE_IGR.equals(role)) {
            return "User : IGR";
        }
        return "User : " + role;
    }

    private static void header(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(new BaseColor(23, 162, 184));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6f);
        table.addCell(cell);
    }

    private static void cell(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "-" : text, DATA_FONT));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4f);
        table.addCell(cell);
    }
}
