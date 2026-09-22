package in.gov.dilrmp.controllers.pdfExcelPhysicalProgressReports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import in.gov.dilrmp.component.PdfExporterComponent;
import in.gov.dilrmp.models.dashboard.DashBordDTO;
import in.gov.dilrmp.utils.PdfExporter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Controller
@RequestMapping("physical/report")
public class Top5StatePdfController {
    private static final Logger logger = LoggerFactory.getLogger(Top5StatePdfController.class);
    @GetMapping("top-5-state")
    public void getTopStatePdf(HttpServletResponse response, HttpSession session) {
        try {
            List<DashBordDTO> top5State = (List<DashBordDTO>) session.getAttribute("top5State");
            generateStatePdfReport(response, "Top 5 States/UTs Report", "Top5States/UTs", top5State);
            logger.info("Top 5 States/UTs PDF report generated successfully.");
        } catch (Exception e) {
            logger.error("Error generating Top 5 States/UTs PDF report.", e);
        }
    }

    @GetMapping("bottom-5-state")
    public void getBottomStatePdf(HttpServletResponse response, HttpSession session) {
        try {
            List<DashBordDTO> bottom5State = (List<DashBordDTO>) session.getAttribute("bottom5State");
            generateStatePdfReport(response, "Bottom 5 States/UTs Report", "Bottom5States/UTsReport", bottom5State);
            logger.info("Bottom 5 States/UTs PDF report generated successfully.");
        } catch (Exception e) {
            logger.error("Error generating Bottom 5 States/UTs PDF report.", e);
        }
    }

    private void generateStatePdfReport(HttpServletResponse response, String reportHeading, String reportName, List<DashBordDTO> stateDataList) {
        try {
            PdfExporterComponent pComponent = setupPdfComponent(reportHeading, reportName);
            List<List<String>> reportDataList = populateReportData(stateDataList);
            pComponent.setReportDataList(reportDataList);
            PdfExporter.createPdf(pComponent, response);
            logger.info("PDF file '{}' generated successfully.", reportName);
        } catch (Exception e) {
            logger.error("Error generating PDF file '{}'.", reportName, e);
        }
    }

    private PdfExporterComponent setupPdfComponent(String reportHeading, String reportName) {
        PdfExporterComponent pComponent = new PdfExporterComponent();
        pComponent.setReportHeading(new String[]{reportHeading});
        pComponent.setReportName(reportName);

        float[] colWidth = {15f, 30f, 30f, 30f, 30f, 30f, 30f};
        pComponent.setCol_width(colWidth);

        String[] colHead = {"S.No", "State/ UT Name", "CLR Completed Villages(%)",
                "Digitized Bs/TippMap-sheets/FMans (No.)(%)",
                "Villages with Cadastral Maps linked With RoR (%)",
                "SRO Computerized %", "Completed Modern Record Room(%)"};
        pComponent.setCol_head(colHead);

        pComponent.setRowspn5(new Integer[]{});
        pComponent.setRowspn4(new Integer[]{});
        pComponent.setRowspn3(new Integer[]{});
        pComponent.setRowspn2(new Integer[]{});
        pComponent.setRowspan1(new Integer[]{0, 1, 2, 3, 4, 5, 6});

        pComponent.setColumnspan(new ArrayList<>());
        pComponent.setColumnnumber(new ArrayList<>());

        return pComponent;
    }

    private List<List<String>> populateReportData(List<DashBordDTO> stateDataList) {
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();

        if (stateDataList == null || stateDataList.isEmpty()) {
            logger.warn("No data found in session attribute for state data.");
            return reportDataList;
        }

        stateDataList.forEach(b -> {
            List<String> strings = new ArrayList<>();
            try {
                strings.add(Integer.toString(indexHolder.incrementAndGet()));
                strings.add(convertToString(b.getName()));
                strings.add(convertToString(b.getVillagesComputerizationCompletedPercent()));
                strings.add(convertToString(b.getDigitizedMapsFmbTippansPercent()));
                strings.add(convertToString(b.getVillagesLinkedWithRoRPercent()));
                strings.add(convertToString(b.getSroComputerizedPercent()));
                strings.add(convertToString(b.getMrrCompletedOutOfTotalSanctionedPercent()));
            } catch (Exception e) {
                logger.error("Error populating report data for state '{}'.", b.getName(), e);
            }
            reportDataList.add(strings);
        });

        logger.info("Report data populated with {} rows.", reportDataList.size());
        return reportDataList;
    }

    private String convertToString(Object value) {
        try {
            if (value instanceof BigDecimal) {
                BigDecimal decimalValue = (BigDecimal) value;
                return decimalValue.compareTo(BigDecimal.ZERO) == 0 ? "0.0" : decimalValue.toString();
            } else if (value instanceof Integer) {
                Integer intValue = (Integer) value;
                return intValue == 0 ? "0.0" : intValue.toString();
            } else if (value != null) {
                return value.toString();
            }
        } catch (Exception e) {
            logger.error("Error converting value '{}'", value, e);
        }
        return "0.0";
    }

}
