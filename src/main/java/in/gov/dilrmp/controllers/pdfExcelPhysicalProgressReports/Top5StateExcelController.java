package in.gov.dilrmp.controllers.pdfExcelPhysicalProgressReports;

import in.gov.dilrmp.component.ExcelExporterComponent;
import in.gov.dilrmp.models.dashboard.DashBordDTO;
import in.gov.dilrmp.utils.ExcelExporter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("physical/report")
public class Top5StateExcelController {

    private static final Logger logger = LoggerFactory.getLogger(Top5StateExcelController.class);

    @GetMapping("top-5-state-excel")
    public void getTopStateExcel(HttpServletResponse response, HttpSession session) {
        try {
            generateStateExcel(response, session, "top5State", "Top 5 States/UTs Report", "top-5-state-UTs-report.xlsx");
            logger.info("Successfully generated Top 5 States/UTs Excel report");
        } catch (Exception e) {
            logger.error("Error generating Top 5 States/UTs Excel report", e);
        }
    }

    @GetMapping("bottom-5-state-excel")
    public void getBottomStateExcel(HttpServletResponse response, HttpSession session) {
        try {
            generateStateExcel(response, session, "bottom5State", "Bottom 5 States/UTs Report", "bottom-5-state-UTs-report.xlsx");
            logger.info("Successfully generated Bottom 5 States/UTs Excel report");
        } catch (Exception e) {
            logger.error("Error generating Bottom 5 States/UTs Excel report", e);
        }
    }

    private void generateStateExcel(HttpServletResponse response, HttpSession session, String sessionAttribute,
                                    String reportTitle, String fileName) {
        try {
            ExcelExporterComponent eComponent = createExcelComponent(fileName, reportTitle);
            List<DashBordDTO> stateData = (List<DashBordDTO>) session.getAttribute(sessionAttribute);
            List<List<String>> reportDataList = createReportData(stateData);

            eComponent.setReportDataList(reportDataList);

            ExcelExporter.generateExcelFile(response, eComponent);
            logger.info("Excel file '{}' generated successfully", fileName);
        } catch (IOException e) {
            logger.error("IOException occurred while generating Excel file '{}'", fileName, e);
        } catch (Exception e) {
            logger.error("Unexpected error while generating Excel file '{}'", fileName, e);
        }
    }

    private ExcelExporterComponent createExcelComponent(String fileName, String reportTitle) {
        ExcelExporterComponent eComponent = new ExcelExporterComponent();
        eComponent.setReportName(fileName);
        eComponent.setNoOfColumns(14);
        eComponent.setNoOfheaderRows(7);
        eComponent.setHeaderText(generateHeaderText(reportTitle));
        eComponent.setHeaderSpanMerged(generateHeaderSpanMerged());
        eComponent.setHeaderSpanUnMerged(new String[]{});
        return eComponent;
    }

    private String[] generateHeaderText(String reportTitle) {
        return new String[]{
                "Department of Land Resources",
                "Digital India Land Records Modernization Programme (DILRMP)",
                reportTitle, "", "",
                "S.No",
                "State/ UT Name",
                "CLR Completed Villages(%)",
                "Digitized Bs/TippMap-sheets/FMans (No.)(%)",
                "Villages with Cadastral Maps linked With RoR (%)",
                "SRO Computerized %",
                "Completed Modern Record Room(%)", ""
        };
    }

    private String[] generateHeaderSpanMerged() {
        return new String[]{"A1:G1", "A2:G2", "A3:G3", "A4:G4", "A5:G5", "A6:A7",
                "B6:B7", "C6:C7", "D6:D7", "E6:E7", "F6:F7", "G6:G7"};
    }

    private List<List<String>> createReportData(List<DashBordDTO> stateData) {
        List<List<String>> reportDataList = new ArrayList<>();
        AtomicInteger indexHolder = new AtomicInteger();

        if (stateData == null || stateData.isEmpty()) {
            logger.warn("No data found in session attribute for state data.");
            return reportDataList;
        }

        stateData.forEach(b -> {
            List<String> rowData = new ArrayList<>();
            rowData.add(Integer.toString(indexHolder.incrementAndGet()));
            rowData.add(b.getName() != null ? b.getName().toString() : "N/A");
            rowData.add(formatValue(b.getVillagesComputerizationCompletedPercent()));
            rowData.add(formatValue(b.getDigitizedMapsFmbTippansPercent()));
            rowData.add(formatValue(b.getVillagesLinkedWithRoRPercent()));
            rowData.add(formatValue(b.getSroComputerizedPercent()));
            rowData.add(formatValue(b.getMrrCompletedOutOfTotalSanctionedPercent()));

            reportDataList.add(rowData);
        });

        logger.info("Report data generated with {} rows.", reportDataList.size());
        return reportDataList;
    }

    private String formatValue(Object value) {
        try {
            if (value instanceof BigDecimal && ((BigDecimal) value).compareTo(BigDecimal.ZERO) == 0) {
                return "0.0";
            }
            return value != null ? value.toString() : "0.0";
        } catch (Exception e) {
            logger.error("Error formatting value '{}'", value, e);
            return "0.0";
        }
    }
}

