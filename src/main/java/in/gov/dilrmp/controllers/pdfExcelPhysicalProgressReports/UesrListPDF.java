package in.gov.dilrmp.controllers.pdfExcelPhysicalProgressReports;

import in.gov.dilrmp.component.PdfExporterComponent;
import in.gov.dilrmp.utils.PdfExporter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("/user/report/pdf")
public class UesrListPDF{

    private static final Logger logger = LoggerFactory.getLogger(UesrListPDF.class);

    @GetMapping("/user-list-report")
    public void getMasterStatePdf(HttpServletResponse response, HttpSession session) {
        logger.info("Generating user list report PDF");
        String  stateName = (String) session.getAttribute("stateName");
        PdfExporterComponent pComponent = new PdfExporterComponent();
        String[] head = {"User List","State/UT: " + stateName};
        pComponent.setReportHeading(head);
        pComponent.setReportName("user list report");

        float[] col_width = {30f, 60f, 60f, 60f, 60f, 60f};
        pComponent.setCol_width(col_width);

        String[] col_head = {"S.No.", "Role", "User ID", "Name", "Officer Name", "Officer Email ID"};
        pComponent.setCol_head(col_head);

        Integer[] rwspn2col = {};
        Integer[] rwspn3col = {};
        Integer[] rwspn4col = {};
        Integer[] rwspn5col = {};
        Integer[] simplecell = {0, 1, 2, 3, 5};

        pComponent.setRowspn5(rwspn5col);
        pComponent.setRowspn4(rwspn4col);
        pComponent.setRowspn3(rwspn3col);
        pComponent.setRowspn2(rwspn2col);
        pComponent.setRowspan1(simplecell);

        List<List<Integer>> colspanval = new ArrayList<>();
        pComponent.setColumnspan(colspanval);

        List<List<Integer>> colsBreak = new ArrayList<>();
        pComponent.setColumnnumber(colsBreak);

        pComponent.setColumnBreakCountNo(0);

        @SuppressWarnings("unchecked")
        List<Object> userlist = (List<Object>) session.getAttribute("userlist");
        if (userlist == null) {
            logger.error("User list is not found in the session");
            return;
        }

        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();

        userlist.forEach(item -> {
            if (item instanceof Object[]) {
                Object[] list = (Object[]) item;
                List<String> strings = new ArrayList<>();
                strings.add(Integer.toString(indexHolder.incrementAndGet())); // Index
                strings.add(list.length > 2 && list[2] != null ? list[2].toString() : "");
                strings.add(list.length > 3 && list[3] != null ? list[3].toString() : "");
                strings.add(list.length > 4 && list[4] != null ? list[4].toString() : "");
                strings.add(list.length > 5 && list[5] != null ? list[5].toString() : "");
                strings.add(list.length > 6 && list[6] != null ? list[6].toString() : "");
                reportDataList.add(strings);
            } else {
                logger.warn("Unexpected type: {}", item.getClass().getName());
            }
        });

        pComponent.setReportDataList(reportDataList);

        try {
            PdfExporter.createPdf(pComponent, response);
            logger.info("PDF report generated successfully");
        } catch (Exception e) {
            logger.error("Error generating PDF report", e);
        }
    }
}
