package in.gov.dilrmp.utils;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.gov.dilrmp.component.PdfExporterComponent;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfExporter {

        final static Logger logger = LoggerFactory.getLogger(PdfExporter.class);
        static Font webLinkFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD, BaseColor.BLUE);
        static Font smallFont = new Font(new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD));
        static Font mediumFont = new Font(Font.FontFamily.TIMES_ROMAN, 8,
                Font.BOLD);
        static Font mediumFontForData = new Font(Font.FontFamily.TIMES_ROMAN, 8,
                Font.NORMAL);
        static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                Font.BOLD);
        static Font fontlarge = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);

        public static void createPdf(PdfExporterComponent pComponent, HttpServletResponse response) {

                createPdf(pComponent, response, mediumFontForData);
        }

        public static void createPdf(List<PdfExporterComponent> pComponentList, HttpServletResponse response) {

                createPdf(pComponentList, response, mediumFontForData);
        }

        // for multiple data
        public static void createPdf(List<PdfExporterComponent> pComponentList, HttpServletResponse response,
                                     Font theFont) {
                Document document = new Document();

                try {
                        PdfExporterComponent pComponent = null;
                        if (pComponentList != null && pComponentList.size() > 0) {
                                pComponent = pComponentList.get(0);
                        }
                        response.setContentType("application/pdf");
                        String headerKey = "Content-Disposition";
                        if (pComponent != null) {
                                String headerValue = "attachment; filename=" + pComponent.getReportName() + ".pdf";
                                response.setHeader(headerKey, headerValue);
                        }

                        if (pComponent.getCol_width().length < 10) {
                                document = new Document(PageSize.A4);
                        } else {
                                document = new Document(PageSize.A4.rotate());
                        }

                        PdfWriter.getInstance(document, response.getOutputStream());
                        document.open();
                        addHeaderToPdf(document);
                        addDataToPdf(document, pComponentList, theFont);
                        addFotterToPdf(document);
                        //document.close();
                } catch (Exception e) {
                        logger.error("Error generating PDF", e);
                }
                finally{
                        document.close();
                }

        }

        public static void createPdf(PdfExporterComponent pComponent, HttpServletResponse response, Font theFont) {
                Document document = new Document();
                try {
                        response.setContentType("application/pdf");
                        String headerKey = "Content-Disposition";
                        String headerValue = "attachment; filename=" + pComponent.getReportName() + ".pdf";
                        response.setHeader(headerKey, headerValue);

                        if (pComponent.getCol_width().length < 10) {
                                document = new Document(PageSize.A4);
                        } else {
                                document = new Document(PageSize.A4.rotate());
                        }
                        PdfWriter.getInstance(document, response.getOutputStream());
                        document.open();
                        addHeaderToPdf(document);
                        addDataToPdf(document, pComponent, theFont);
                        addFotterToPdf(document);
                        //document.close();
                } catch (Exception e) {
                        logger.error("Error occurred while creating PDF: ", e);
                }
                finally{
                        document.close();
                }

        }

        public static void addDataToPdf(Document document, PdfExporterComponent pComponent, Font theFont)
                throws DocumentException {
                // write report name
                for (int i = 0; i < pComponent.getReportHeading().length; i++) {
                        if (pComponent.getReportHeading()[i].contains("Funds")) {
                                document.add(new Paragraph(" "));
                                document.add(new Paragraph(pComponent.getReportHeading()[i], mediumFont));
                        } else {
                                Paragraph paragraph = new Paragraph(pComponent.getReportHeading()[i], mediumFont);
                                paragraph.setAlignment(1);
                                document.add(paragraph);

                        }
                        if (i == pComponent.getReportHeading().length - 1) {
                                document.add(new Paragraph(" "));
                        }
                        if (pComponent.getReportHeading()[i].contains("Master Details-Month Wise")) {
                                Paragraph note = new Paragraph();
                                note.setSpacingBefore(1f);
                                note.add(new Paragraph(
                                        "Updated:- Data Updated by the Administrative Unit during the selected period",
                                        new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD)));
                                document.add(note);
                                Paragraph note1 = new Paragraph();
                                note1.setSpacingBefore(1f);
                                note1.add(new Paragraph("Blank Cell- No updation done by the Administrative Unit",
                                        new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD)));
                                document.add(note1);
                                Paragraph note2 = new Paragraph();
                                note2.setSpacingBefore(1f);
                                note2.add(new Paragraph(
                                        "Saturation-  Data Updation completed by all Administrative Units during the selected period",
                                        new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD)));
                                document.add(note2);
                                Paragraph note3 = new Paragraph();
                                note3.setSpacingBefore(1f);
                                note3.add(new Paragraph("NDSFE-  No Data Entered So Far",
                                        new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD)));
                                document.add(new Paragraph(" "));
                                document.add(note3);

                        }
                }
                // generate table for Data header and data
                PdfPTable table = new PdfPTable(pComponent.getCol_width());

                table.setWidthPercentage(100f);
                if (pComponent.getRowspn5().length > 0) {
                        table.setHeaderRows(6);
                } else if (pComponent.getRowspn4().length > 0) {
                        table.setHeaderRows(5);

                } else if (pComponent.getRowspn3().length > 0) {
                        table.setHeaderRows(4);
                } else if (pComponent.getRowspn2().length > 0) {
                        table.setHeaderRows(3);

                } else if (pComponent.getRowspan1().length > 0) {
                        table.setHeaderRows(2);

                } else {
                        table.setHeaderRows(1);
                }
                for (int i = 0; i < pComponent.getCol_head().length; i++) {
                        PdfPCell cell = new PdfPCell(new Phrase(pComponent.getCol_head()[i], mediumFont));
                        if (pComponent.getColumnspan().size() > 0) {
                                for (List<Integer> lt : pComponent.getColumnspan()) {
                                        if (lt.get(0) == i) {
                                                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                cell.setColspan(lt.get(1));

                                        }
                                }
                        }
                        if (pComponent.getRowspn5().length > 0) {
                                for (Integer lt : pComponent.getRowspn5()) {
                                        if (lt == i) {
                                                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                cell.setRowspan(5);

                                        }
                                }
                        }
                        if (pComponent.getRowspn4().length > 0) {
                                for (Integer lt : pComponent.getRowspn4()) {
                                        if (lt == i) {
                                                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                cell.setRowspan(4);

                                        }
                                }
                        }
                        if (pComponent.getRowspn3().length > 0) {
                                for (Integer lt : pComponent.getRowspn3()) {
                                        if (lt == i) {

                                                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                cell.setRowspan(3);

                                        }
                                }
                        }
                        if (pComponent.getRowspn2().length > 0) {

                                for (Integer lt : pComponent.getRowspn2()) {
                                        if (lt == i) {
                                                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                cell.setRowspan(2);

                                        }
                                }
                        }
                        if (pComponent.getRowspan1().length > 0) {

                                for (Integer lt : pComponent.getRowspan1()) {
                                        if (lt == i) {
                                                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                                        }
                                }
                        }
                        table.addCell(cell);

                }
                for (int i = 0; i < (pComponent.getCol_width().length - pComponent.getColumnBreakCountNo()); i++) {
                        PdfPCell cell = null;
                        PdfPCell cell1 = null;
                        PdfPCell cell2 = null;
                        PdfPCell cell3 = null;
                        PdfPCell cell4 = null;
                        PdfPCell cell5 = null;
                        PdfPCell cell6 = null;
                        PdfPCell cell7 = null;
                        PdfPCell cell8 = null;
                        PdfPCell cell9 = null;

                        boolean cellAdded = false;
                        for (List<Integer> lt : pComponent.getColumnnumber()) {
                                if (lt.get(0) == i) {
                                        if (lt.get(1) == 2) {
                                                cell = new PdfPCell(new Phrase("(" + (i + 1) + "a)", mediumFont));
                                                cell1 = new PdfPCell(new Phrase("(" + (i + 1) + "b)", mediumFont));
                                                cellAdded = true;
                                        }
                                        if (lt.get(1) == 3) {
                                                cell2 = new PdfPCell(new Phrase("(" + (i + 1) + "a)", mediumFont));
                                                cell3 = new PdfPCell(new Phrase("(" + (i + 1) + "b)", mediumFont));
                                                cell4 = new PdfPCell(new Phrase("(" + (i + 1) + "c)", mediumFont));
                                                cellAdded = true;
                                        }
                                        if (lt.get(1) == 4) {
                                                cell2 = new PdfPCell(new Phrase("(" + (i + 1) + "a)", mediumFont));
                                                cell3 = new PdfPCell(new Phrase("(" + (i + 1) + "b)", mediumFont));
                                                cell4 = new PdfPCell(new Phrase("(" + (i + 1) + "c)", mediumFont));
                                                cell5 = new PdfPCell(new Phrase("(" + (i + 1) + "d)", mediumFont));
                                                cellAdded = true;
                                        }
                                        if (lt.get(1) == 5) {
                                                cell2 = new PdfPCell(new Phrase("(" + (i + 1) + "a)", mediumFont));
                                                cell3 = new PdfPCell(new Phrase("(" + (i + 1) + "b)", mediumFont));
                                                cell4 = new PdfPCell(new Phrase("(" + (i + 1) + "c)", mediumFont));
                                                cell5 = new PdfPCell(new Phrase("(" + (i + 1) + "d)", mediumFont));
                                                cell6 = new PdfPCell(new Phrase("(" + (i + 1) + "e)", mediumFont));
                                                cellAdded = true;
                                        }
                                        if (lt.get(1) == 8) {
                                                cell2 = new PdfPCell(new Phrase("(" + (i + 1) + "a)", mediumFont));
                                                cell3 = new PdfPCell(new Phrase("(" + (i + 1) + "b)", mediumFont));
                                                cell4 = new PdfPCell(new Phrase("(" + (i + 1) + "c)", mediumFont));
                                                cell5 = new PdfPCell(new Phrase("(" + (i + 1) + "d)", mediumFont));
                                                cell6 = new PdfPCell(new Phrase("(" + (i + 1) + "e)", mediumFont));
                                                cell7 = new PdfPCell(new Phrase("(" + (i + 1) + "f)", mediumFont));
                                                cell8 = new PdfPCell(new Phrase("(" + (i + 1) + "g)", mediumFont));
                                                cell9 = new PdfPCell(new Phrase("(" + (i + 1) + "h)", mediumFont));
                                                cellAdded = true;
                                        }
                                }

                        }
                        if (!cellAdded) {
                                cell = new PdfPCell(
                                        new Phrase("(" + Integer.toString(i + 1) + ")", mediumFont));
                        }
                        if (cell != null) {
                                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                table.addCell(cell);
                        }
                        if (cell1 != null) {
                                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                                table.addCell(cell1);
                        }
                        if (cell2 != null) {
                                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                                table.addCell(cell2);
                        }
                        if (cell3 != null) {
                                cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                                table.addCell(cell3);
                        }
                        if (cell4 != null) {
                                cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
                                table.addCell(cell4);
                        }
                        if (cell5 != null) {
                                cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
                                table.addCell(cell5);
                        }
                        if (cell6 != null) {
                                cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
                                table.addCell(cell6);
                        }
                        if (cell7 != null) {
                                cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
                                table.addCell(cell7);
                        }
                        if (cell8 != null) {
                                cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
                                table.addCell(cell8);
                        }
                        if (cell9 != null) {
                                cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
                                table.addCell(cell9);
                        }

                }

                // fetch and data to table
                List<List<String>> reportDataList = pComponent.getReportDataList();
                reportDataList.stream().forEachOrdered(e -> e.stream().forEachOrdered(lt -> {
                        PdfPCell cell = new PdfPCell(new Phrase(lt, theFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                }));
                // Add grand total rows if any
                List<List<String>> grandTotal = pComponent.getGrandTotal();
                if (grandTotal != null && !grandTotal.isEmpty()) {
                        Font totalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
                        for (List<String> totalRow : grandTotal) {
                                for (String totalCell : totalRow) {
                                        PdfPCell cell = new PdfPCell(new Phrase(totalCell, totalFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table.addCell(cell);
                                }
                        }
                }

                document.add(table);
        }

        // for multiple table
        public static void addDataToPdf(Document document, List<PdfExporterComponent> pComponentList, Font theFont)
                throws DocumentException {
                for (PdfExporterComponent pComponent : pComponentList) {
                        // write report name
                        for (int i = 0; i < pComponent.getReportHeading().length; i++) {
                                if (pComponent.getReportHeading()[i].contains("Funds")) {
                                        document.add(new Paragraph(" "));
                                        document.add(new Paragraph(pComponent.getReportHeading()[i], mediumFont));
                                } else {
                                        Paragraph paragraph = new Paragraph(pComponent.getReportHeading()[i],
                                                mediumFont);
                                        paragraph.setAlignment(1);
                                        paragraph.setSpacingAfter(20f);
                                        paragraph.setSpacingAfter(10f);
                                        document.add(paragraph);

                                }

                        }
                        // generate table for Data header and data
                        PdfPTable table = new PdfPTable(pComponent.getCol_width());
                        table.setWidthPercentage(100f);
                        for (int i = 0; i < pComponent.getCol_head().length; i++) {
                                PdfPCell cell = new PdfPCell(new Phrase(pComponent.getCol_head()[i], mediumFont));
                                if (pComponent.getColumnspan().size() > 0) {
                                        for (List<Integer> lt : pComponent.getColumnspan()) {
                                                if (lt.get(0) == i) {
                                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        cell.setColspan(lt.get(1));

                                                }
                                        }
                                }
                                if (pComponent.getRowspn5().length > 0) {
                                        for (Integer lt : pComponent.getRowspn5()) {
                                                if (lt == i) {
                                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        cell.setRowspan(5);

                                                }
                                        }
                                }
                                if (pComponent.getRowspn4().length > 0) {
                                        for (Integer lt : pComponent.getRowspn4()) {
                                                if (lt == i) {
                                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        cell.setRowspan(4);

                                                }
                                        }
                                }
                                if (pComponent.getRowspn3().length > 0) {
                                        for (Integer lt : pComponent.getRowspn3()) {
                                                if (lt == i) {

                                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        cell.setRowspan(3);

                                                }
                                        }
                                }
                                if (pComponent.getRowspn2().length > 0) {

                                        for (Integer lt : pComponent.getRowspn2()) {
                                                if (lt == i) {
                                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        cell.setRowspan(2);

                                                }
                                        }
                                }
                                if (pComponent.getRowspan1().length > 0) {

                                        for (Integer lt : pComponent.getRowspan1()) {
                                                if (lt == i) {
                                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                                                }
                                        }
                                }
                                table.addCell(cell);

                        }
                        for (int i = 0; i < (pComponent.getCol_width().length
                                - pComponent.getColumnBreakCountNo()); i++) {
                                PdfPCell cell = null;
                                PdfPCell cell1 = null;
                                PdfPCell cell2 = null;
                                PdfPCell cell3 = null;
                                PdfPCell cell4 = null;
                                PdfPCell cell5 = null;
                                PdfPCell cell6 = null;
                                PdfPCell cell7 = null;
                                PdfPCell cell8 = null;
                                PdfPCell cell9 = null;
                                boolean cellAdded = false;
                                for (List<Integer> lt : pComponent.getColumnnumber()) {
                                        if (lt.get(0) == i) {
                                                if (lt.get(1) == 2) {
                                                        cell = new PdfPCell(
                                                                new Phrase("(" + (i + 1) + "a)", mediumFont));
                                                        cell1 = new PdfPCell(
                                                                new Phrase("(" + (i + 1) + "b)", mediumFont));
                                                        cellAdded = true;
                                                }
                                                if (lt.get(1) == 3) {
                                                        cell2 = new PdfPCell(new Phrase("(" + (i + 1) + "a)", mediumFont));
                                                        cell3 = new PdfPCell(new Phrase("(" + (i + 1) + "b)", mediumFont));
                                                        cell4 = new PdfPCell(new Phrase("(" + (i + 1) + "c)", mediumFont));
                                                        cellAdded = true;
                                                }
                                                if (lt.get(1) == 4) {
                                                        cell2 = new PdfPCell(new Phrase("(" + (i + 1) + "a)", mediumFont));
                                                        cell3 = new PdfPCell(new Phrase("(" + (i + 1) + "b)", mediumFont));
                                                        cell4 = new PdfPCell(new Phrase("(" + (i + 1) + "c)", mediumFont));
                                                        cell5 = new PdfPCell(new Phrase("(" + (i + 1) + "d)", mediumFont));
                                                        cellAdded = true;
                                                }
                                                if (lt.get(1) == 5) {
                                                        cell2 = new PdfPCell(new Phrase("(" + (i + 1) + "a)", mediumFont));
                                                        cell3 = new PdfPCell(new Phrase("(" + (i + 1) + "b)", mediumFont));
                                                        cell4 = new PdfPCell(new Phrase("(" + (i + 1) + "c)", mediumFont));
                                                        cell5 = new PdfPCell(new Phrase("(" + (i + 1) + "d)", mediumFont));
                                                        cell6 = new PdfPCell(new Phrase("(" + (i + 1) + "e)", mediumFont));
                                                        cellAdded = true;
                                                }
                                                if (lt.get(1) == 8) {
                                                        cell2 = new PdfPCell(new Phrase("(" + (i + 1) + "a)", mediumFont));
                                                        cell3 = new PdfPCell(new Phrase("(" + (i + 1) + "b)", mediumFont));
                                                        cell4 = new PdfPCell(new Phrase("(" + (i + 1) + "c)", mediumFont));
                                                        cell5 = new PdfPCell(new Phrase("(" + (i + 1) + "d)", mediumFont));
                                                        cell6 = new PdfPCell(new Phrase("(" + (i + 1) + "e)", mediumFont));
                                                        cell7 = new PdfPCell(new Phrase("(" + (i + 1) + "f)", mediumFont));
                                                        cell8 = new PdfPCell(new Phrase("(" + (i + 1) + "g)", mediumFont));
                                                        cell9 = new PdfPCell(new Phrase("(" + (i + 1) + "h)", mediumFont));
                                                        cellAdded = true;
                                                }
                                        }

                                }
                                if (!cellAdded) {
                                        cell = new PdfPCell(
                                                new Phrase("(" + Integer.toString(i + 1) + ")", mediumFont));
                                }
                                if (cell != null) {
                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table.addCell(cell);
                                }
                                if (cell1 != null) {
                                        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table.addCell(cell1);
                                }
                                if (cell2 != null) {
                                        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table.addCell(cell2);
                                }
                                if (cell3 != null) {
                                        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table.addCell(cell3);
                                }
                                if (cell4 != null) {
                                        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table.addCell(cell4);
                                }
                                if (cell5 != null) {
                                        cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table.addCell(cell5);
                                }
                                if (cell6 != null) {
                                        cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table.addCell(cell6);
                                }
                                if (cell7 != null) {
                                        cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table.addCell(cell7);
                                }
                                if (cell8 != null) {
                                        cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table.addCell(cell8);
                                }
                                if (cell9 != null) {
                                        cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table.addCell(cell9);
                                }

                        }

                        // fetch and data to table
                        List<List<String>> reportDataList = pComponent.getReportDataList();
                        reportDataList.stream().forEachOrdered(e -> e.stream().forEachOrdered(lt -> {
                                if (lt.equals("")) {
                                        System.err.println(lt);
                                }
                                System.err.println(lt);
                                PdfPCell cell = new PdfPCell(new Phrase(lt, theFont));
                                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                table.addCell(cell);
                        }));

                        document.add(table);
                }
        }

        public static void addHeaderToPdf(Document document)
                throws DocumentException, MalformedURLException, IOException {
                InputStream input = null;
                byte[] bytes = null;
                try {
                        // Create and configure the table for the date
                        PdfPTable tableTop = new PdfPTable(1);
                        PdfPCell cellbottom = null;
                        Date d = new Date();
                        SimpleDateFormat s = new SimpleDateFormat("dd/MMM/yyyy  hh:mm:ss a");
                        String sd = s.format(d);
                        cellbottom = new PdfPCell(
                                new Paragraph(new Chunk("Dated : " + sd, smallFont)));
                        cellbottom.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cellbottom.setBorder(PdfPCell.NO_BORDER);
                        tableTop.addCell(cellbottom);
                        document.add(tableTop);

                        // Add the first image
                        Resource resource = new ClassPathResource("static/images/emb1.png");
                        input = resource.getInputStream();
                        bytes = IOUtils.toByteArray(input);
                        Image img = Image.getInstance(bytes);
                        int indentation = 0;
                        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                                - document.rightMargin() - indentation) / img.getWidth()) * 6;
                        img.scalePercent(scaler);
                        img.setAbsolutePosition(document.leftMargin() + 50, document.top() - img.getScaledHeight() - 20);
                        document.add(img);

                        // Add the second image
                        Resource resource1 = new ClassPathResource("static/images/nat-digindia.png");
                        input = resource1.getInputStream();
                        bytes = IOUtils.toByteArray(input);
                        img = Image.getInstance(bytes);
                        indentation = 0;
                        scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                                - document.rightMargin() - indentation) / img.getWidth()) * 16;
                        img.scalePercent(scaler);
                        img.setAbsolutePosition(document.getPageSize().getWidth() - img.getScaledWidth() - document.rightMargin() - 50, document.top() - img.getScaledHeight() - 20);
                        document.add(img);

                        // Add text paragraphs
                        Paragraph paragraph = new Paragraph("Department of Land Resources", mediumFont);
                        paragraph.setAlignment(Element.ALIGN_CENTER);
                        document.add(paragraph);
                        paragraph = new Paragraph("Ministry of Rural Development, Government of India", mediumFont);
                        paragraph.setAlignment(Element.ALIGN_CENTER);
                        document.add(paragraph);
                        paragraph = new Paragraph("Digital India Land Records Modernization Programme (DILRMP)", mediumFont);
                        paragraph.setAlignment(Element.ALIGN_CENTER);
                        document.add(paragraph);
                        document.add(new Paragraph("    "));

                } catch (IOException e) {
                        // Handle IO exceptions
                        logger.error("Error occurred while add header to Pdf: ", e);
                } catch (DocumentException e) {
                        // Handle Document exceptions
                        logger.error("Error occurred while add header to Pdf: ", e);

                } catch (Exception e) {
                        // Handle any other exceptions
                        logger.error("Error occurred while add header to Pdf: ", e);
                        throw new RuntimeException("Unexpected error occurred");

                } finally {
                        // Clean up resources
                        if (input != null) {
                                try {
                                        input.close();
                                } catch (IOException e) {
                                        logger.error("Error occurred while add header to Pdf: ", e); // Handle closing exceptions if necessary
                                }
                        }
                }
        }


        public static void addFotterToPdf(Document document) throws DocumentException {

                PdfPTable tablebottom = new PdfPTable(2);

                Paragraph note = new Paragraph();
                note.setSpacingBefore(1f);

                Anchor dilrpmLink = new Anchor("DILRMP-MIS", webLinkFont);
                dilrpmLink.setReference("http://dilrmp.gov.in/");
                Anchor nicLink = new Anchor("National Informatics Centre", webLinkFont);
                nicLink.setReference("http://www.nic.in");
                Anchor dolrLink = new Anchor("Department of Land Resources", webLinkFont);
                dolrLink.setReference("https://dolr.gov.in/");
                Paragraph para1 = new Paragraph(10);
                para1.setSpacingAfter(15f);
                para1.setSpacingBefore(15f);
                para1.add(new Phrase("        "));
                para1.add(new Phrase("The ", smallFont));
                para1.add(dilrpmLink);
                para1.add(new Phrase(" is designed and hosted by ", mediumFont));
                para1.add(nicLink);
                para1.add(new Phrase(
                        " Ministry of Electronics and Information Technolgy, Government of India.Content provided and owned by the ",
                        mediumFont));
                para1.add(dolrLink);
                para1.add(new Phrase(
                        " Ministry of Rural Developmennt, Government of India.Data entered and updated by different users of concerned State Government / UT Administration.",
                        mediumFont));
                para1.add(new Phrase("        "));
                para1.setAlignment(1);
                document.add(note);
                document.add(para1);
                float[] columnWidthtablebottom = new float[] { 30f, 30f };
                tablebottom.setWidths(columnWidthtablebottom);
                tablebottom.setWidthPercentage(100);
                PdfPCell cellbottom = null;
                cellbottom = new PdfPCell(new Paragraph(
                        new Chunk("Source: http://dilrmp.gov.in ", mediumFont)));
                cellbottom.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellbottom.setBorder(PdfPCell.NO_BORDER);
                Date d = new Date();
                SimpleDateFormat s = new SimpleDateFormat("dd/MMM/yyyy  hh:mm:ss");
                String sd = s.format(d);
                cellbottom = new PdfPCell(
                        new Paragraph(new Chunk("Dated : " + sd, mediumFont)));
                cellbottom.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellbottom.setBorder(PdfPCell.NO_BORDER);
                tablebottom.addCell(cellbottom);

        }

}
