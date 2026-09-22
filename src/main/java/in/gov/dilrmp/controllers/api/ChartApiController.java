package in.gov.dilrmp.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.services.physicalProgressServices.MapDigitizationReportService;
import in.gov.dilrmp.services.physicalProgressServices.StateAadharService;
import in.gov.dilrmp.services.physicalProgressServices.StateCLRService;
import in.gov.dilrmp.services.physicalProgressServices.StateMAPService;
import in.gov.dilrmp.services.physicalProgressServices.StateMRRService;
import in.gov.dilrmp.services.physicalProgressServices.StateRCMSService;
import in.gov.dilrmp.services.physicalProgressServices.StateSROService;
import in.gov.dilrmp.services.physicalProgressServices.StateSurveyService;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

@RestController
@RequestMapping("/api/chart")
public class ChartApiController {

    @Autowired
    StateCLRService stateCLRService;

    @Autowired
    MapDigitizationReportService mDigitizationReportService;

    @Autowired
    StateMRRService stateMRRService;

    @Autowired
    StateSROService stateSROService;

    @Autowired
    StateSurveyService stateSurveyService;

    @Autowired
    StateRCMSService stateRCMSService;

    @Autowired
    StateAadharService stateAadharService;

    // Utility method for calculating percentage
    private String calculatePercentage(int completed, int total) {
        if (total == 0) {
            return "0";
        }
        double percentTotal = ((double) completed * 100) / total;
        return String.format("%.2f", percentTotal);
    }

    // Utility method for safe integer value conversion
    private String safeValue(Integer value) {
        return value == null ? "0" : String.valueOf(value);
    }

    @CrossOrigin
    @PostMapping("/category=11")
    public ResponseEntity<List<Map<String, String>>> getAllDataClr() {
        try {
            List<Map<String, String>> resultList = new ArrayList<>();
            List<StateClrReportView> clrReportModelList = stateCLRService.getClrStateViwe();

            for (StateClrReportView clrReportModel : clrReportModelList) {
                Map<String, String> map = new HashMap<>();
                int totalVillages = clrReportModel.getTotalVillages() != null ? clrReportModel.getTotalVillages() : 0;
                int clrCompleted = clrReportModel.getVillagesClrCompleted() != null
                        ? clrReportModel.getVillagesClrCompleted()
                        : 0;

                map.put("total", safeValue(totalVillages));
                map.put("lgd", String.valueOf(clrReportModel.getLgdCode()));
                map.put("completed", safeValue(clrCompleted));
                map.put("percent", calculatePercentage(clrCompleted, totalVillages));

                resultList.add(map);
            }
            return ResponseEntity.ok(resultList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @CrossOrigin
    @PostMapping("/category=111")
    public String getClrData() {
        JSONObject jObj = new JSONObject();
        int grandTotalVillage = 0;
        int grandTotalCLRCompleted = 0;
        int grandTotalROR = 0;

        List<StateClrReportView> clrList = stateCLRService.getClrStateViwe();
        List<Object[]> yearWiseData = new ArrayList<>(); // Placeholder for actual data fetching

        try {
            JSONArray categories = new JSONArray();
            JSONArray colors = new JSONArray();
            JSONArray data = new JSONArray();
            JSONArray yearWiseCLRDataValue = new JSONArray();
            JSONArray yearWiseCLRDataYear = new JSONArray();
            JSONArray grandDataTable = new JSONArray();

            // Creating header for the data table
            JSONArray dataTableHeader = new JSONArray();
            dataTableHeader.put("S No");
            dataTableHeader.put("State/UT");
            dataTableHeader.put("Total RORs");
            dataTableHeader.put("Total Villages");
            dataTableHeader.put("CLR Completed (Villages)");
            dataTableHeader.put("% CLR Completed");
            grandDataTable.put(dataTableHeader);

            int sno = 1;

            for (StateClrReportView clr : clrList) {
                JSONArray dataTable = new JSONArray();
                int totalVillages = clr.getTotalVillages() != null ? clr.getTotalVillages() : 0;
                int completedVillages = clr.getVillagesClrCompleted() != null
                        ? clr.getVillagesClrCompleted()
                        : 0;

                // Calculate percentage, ensuring no division by zero
                float perc = (totalVillages > 0) ? (float) completedVillages * 100 / totalVillages : 0.0f;

                // Increment grand totals
                grandTotalVillage += totalVillages;
                grandTotalCLRCompleted += completedVillages;
                int totalRor = clr.getTotalRor() != null ? clr.getTotalRor() : 0;
                grandTotalROR += totalRor;

                // Add data to table
                categories.put(clr.getStateName());
                dataTable.put(sno++);
                dataTable.put(clr.getStateName());
                dataTable.put(String.format("%,d", totalRor));
                dataTable.put(String.format("%,d", totalVillages));
                dataTable.put(String.format("%,d", completedVillages));
                dataTable.put(String.format("%.02f", perc));

                // Determine color coding based on percentage
                if (perc == 0.0) {
                    colors.put("#FF0000");
                } else if (perc > 0 && perc < 30) {
                    colors.put("#ffe4d9");
                } else if (perc >= 30 && perc < 66) {
                    colors.put("#ebc5dd");
                } else if (perc >= 66 && perc < 81) {
                    colors.put("#fbae90");
                } else if (perc >= 81 && perc <= 100) {
                    colors.put("#c1dfb1");
                }

                // Add row to grand data table
                grandDataTable.put(dataTable);
            }

            // Add total row
            JSONArray totalRow = new JSONArray();
            totalRow.put("Total");
            totalRow.put("");
            totalRow.put(String.format("%,d", grandTotalROR));
            totalRow.put(String.format("%,d", grandTotalVillage));
            totalRow.put(String.format("%,d", grandTotalCLRCompleted));
            float grandPerc = (grandTotalVillage > 0) ? (float) grandTotalCLRCompleted * 100 / grandTotalVillage : 0.0f;
            totalRow.put(String.format("%.02f", grandPerc));
            grandDataTable.put(totalRow);

            // Populate year-wise data
            for (Object[] row : yearWiseData) {
                if (row[0] != null && row[1] != null && row[2] != null) {
                    yearWiseCLRDataYear.put("1-" + row[1].toString() + "-" + row[0].toString());
                    yearWiseCLRDataValue.put(new DecimalFormat("#").format(row[2]));
                }
            }

            // Construct final JSON object
            jObj.put("categories", categories);
            jObj.put("chartvalue", data);
            jObj.put("clrYearWiseData", yearWiseCLRDataValue);
            jObj.put("clrYear", yearWiseCLRDataYear);
            jObj.put("grandperc", String.format("%.02f", grandPerc));
            jObj.put("datatable", grandDataTable);
            jObj.put("colors", colors);
            jObj.put("grandvill", String.format("%,d", grandTotalVillage));
            jObj.put("grandcompletedcr", String.format("%,d", grandTotalCLRCompleted));
            jObj.put("totalror", String.format("%,d", grandTotalROR));

        } catch (Exception e) {
            System.err.println("Error processing CLR data: " + e.getMessage());
            e.printStackTrace();
        }
        return jObj.toString();
    }

    @CrossOrigin
    @PostMapping("/category=301")
    public String getMapDigitizationData() {
        JSONObject jObj = new JSONObject();
        int grandTotalVillage = 0;
        int grandTotalMapDigitized = 0;
        int grandTotalMap = 0;

        List<MapDigitizationReport> digitizationList = mDigitizationReportService.getAllMapDigitizationReport();
        List<Object[]> yearWiseData = new ArrayList<>(); // Placeholder for actual data fetching

        try {
            JSONArray categories = new JSONArray();
            JSONArray colors = new JSONArray();
            JSONArray data = new JSONArray();
            JSONArray yearWiseCLRDataValue = new JSONArray();
            JSONArray yearWiseCLRDataYear = new JSONArray();
            JSONArray grandDataTable = new JSONArray();

            // Creating header for the data table
            JSONArray dataTableHeader = new JSONArray();
            dataTableHeader.put("S No");
            dataTableHeader.put("State/UT");
            dataTableHeader.put("Total Villages");
            dataTableHeader.put("Total Mapsheets/FMB's/Tippans");
            dataTableHeader.put("Total Digitized Mapsheets/FMB's/Tippans");
            dataTableHeader.put("% Digitized Mapsheets/FMB's/Tippans");
            grandDataTable.put(dataTableHeader);

            int sno = 1;

            for (MapDigitizationReport map : digitizationList) {
                JSONArray dataTable = new JSONArray();
                int totalVillages = map.getTotalVillages() != null ? map.getTotalVillages() : 0;
                int totalMaps = map.getTotalMapsFmbTippans() != null ? map.getTotalMapsFmbTippans() : 0;
                int digitizedMaps = map.getTotalDigitizedMapsFmbTippans() != null
                        ? map.getTotalDigitizedMapsFmbTippans()
                        : 0;

                // Calculate percentage, ensuring no division by zero
                float perc = (totalMaps > 0) ? (float) digitizedMaps * 100 / totalMaps : 0.0f;

                // Increment grand totals
                grandTotalVillage += totalVillages;
                grandTotalMap += totalMaps;
                grandTotalMapDigitized += digitizedMaps;

                // Add data to table
                categories.put(map.getStateName());
                dataTable.put(sno++);
                dataTable.put(map.getStateName());
                dataTable.put(String.format("%,d", totalVillages));
                dataTable.put(String.format("%,d", totalMaps));
                dataTable.put(String.format("%,d", digitizedMaps));
                dataTable.put(String.format("%.02f", perc));

                // Determine color coding based on percentage
                if (perc == 0.0) {
                    colors.put("#FF0000");
                } else if (perc > 0 && perc < 30) {
                    colors.put("#ffe4d9");
                } else if (perc >= 30 && perc < 66) {
                    colors.put("#ebc5dd");
                } else if (perc >= 66 && perc < 81) {
                    colors.put("#fbae90");
                } else if (perc >= 81 && perc <= 100) {
                    colors.put("#c1dfb1");
                }

                // Add row to grand data table
                grandDataTable.put(dataTable);
            }

            // Add total row
            JSONArray totalRow = new JSONArray();
            totalRow.put("Total");
            totalRow.put("");
            totalRow.put(String.format("%,d", grandTotalVillage));
            totalRow.put(String.format("%,d", grandTotalMap));
            totalRow.put(String.format("%,d", grandTotalMapDigitized));
            float grandPerc = (grandTotalMap > 0) ? (float) grandTotalMapDigitized * 100 / grandTotalMap : 0.0f;
            totalRow.put(String.format("%.02f", grandPerc));
            grandDataTable.put(totalRow);

            // Populate year-wise data
            for (Object[] row : yearWiseData) {
                if (row[0] != null && row[1] != null && row[2] != null) {
                    yearWiseCLRDataYear.put("1-" + row[1].toString() + "-" + row[0].toString());
                    yearWiseCLRDataValue.put(new DecimalFormat("#").format(row[2]));
                }
            }

            // Construct final JSON object
            jObj.put("categories", categories);
            jObj.put("chartvalue", data);
            jObj.put("grandperc", String.format("%.02f", grandPerc));
            jObj.put("datatable", grandDataTable);
            jObj.put("colors", colors);
            jObj.put("clrYearWiseData", yearWiseCLRDataValue);
            jObj.put("clrYear", yearWiseCLRDataYear);
            jObj.put("grandvill", String.format("%,d", grandTotalVillage));
            jObj.put("grandcompletedcr", String.format("%,d", grandTotalMapDigitized));
            jObj.put("totalror", String.format("%,d", grandTotalMap));

        } catch (Exception e) {
            System.err.println("Error processing map digitization data: " + e.getMessage());
            e.printStackTrace();
        }
        return jObj.toString();
    }

    @CrossOrigin
    @PostMapping("/category=30")
    public ResponseEntity<List<Map<String, String>>> getAllDataMapDigitization() {
        try {
            List<Map<String, String>> resultList = new ArrayList<>();
            List<MapDigitizationReport> digitizationReportModelList = mDigitizationReportService
                    .getAllMapDigitizationReport();

            for (MapDigitizationReport digitizationReportModel : digitizationReportModelList) {
                Map<String, String> map = new HashMap<>();
                int totalMaps = digitizationReportModel.getTotalMapsFmbTippans() != null
                        ? digitizationReportModel.getTotalMapsFmbTippans()
                        : 0;
                int digitizedMaps = digitizationReportModel.getTotalDigitizedMapsFmbTippans() != null
                        ? digitizationReportModel.getTotalDigitizedMapsFmbTippans()
                        : 0;

                map.put("total", safeValue(totalMaps));
                map.put("lgd", String.valueOf(digitizationReportModel.getLgdCode()));
                map.put("completed", safeValue(digitizedMaps));
                map.put("percent", calculatePercentage(digitizedMaps, totalMaps));

                resultList.add(map);
            }
            return ResponseEntity.ok(resultList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @CrossOrigin
    @PostMapping("/category=16")
    public ResponseEntity<List<Map<String, String>>> getAllDataMrr() {
        try {
            List<Map<String, String>> resultList = new ArrayList<>();
            List<MrrViewReport> mrrReportModelList = stateMRRService.getAllMrrList();

            for (MrrViewReport mrrReportModel : mrrReportModelList) {
                Map<String, String> map = new HashMap<>();
                int totalTehsil = mrrReportModel.getTotalTehsils() != null ? mrrReportModel.getTotalTehsils() : 0;
                int completed = mrrReportModel.getMrrCompleted() != null ? mrrReportModel.getMrrCompleted() : 0;

                map.put("total", safeValue(totalTehsil));
                map.put("sanctioned", safeValue(mrrReportModel.getMrrSanctioned()));
                map.put("lgd", String.valueOf(mrrReportModel.getLgdCode()));
                map.put("completed", safeValue(completed));
                map.put("percent", calculatePercentage(completed, totalTehsil));

                resultList.add(map);
            }

            return ResponseEntity.ok(resultList);
        } catch (Exception e) {
            Logger.getLogger(ChartApiController.class.getName()).log(Level.SEVERE, "Error fetching MRR data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @CrossOrigin
    @PostMapping("/category=161")
    public String getDashboardData() {
        Integer grandTotalVillage = 0;
        Integer grandTotalCLRCompleted = 0;
        Integer grandTotalROR = 0;

        List<MrrViewReport> mrrongList = stateMRRService.getAllMrrList();
        // List<Object[]> yearWiseData =
        // monthlyAllKpiStateSummaryRepository.getYearWiseMRRData();
        List<Object[]> yearWiseData = new ArrayList<>();
        JSONObject jObj = new JSONObject();

        try {
            JSONArray categories = new JSONArray();
            JSONArray colors = new JSONArray();
            JSONArray data = new JSONArray();
            JSONArray yearWiseCLRDataValue = new JSONArray();
            JSONArray yearWiseCLRDataYear = new JSONArray();
            JSONArray grandDataTable = new JSONArray();

            // Creating header for the data table
            JSONArray dataTableHeader = new JSONArray();
            dataTableHeader.put("State/Ut");
            dataTableHeader.put("Total Tehsils");
            dataTableHeader.put("Sanctioned MRR");
            dataTableHeader.put("Completed MRR");
            grandDataTable.put(dataTableHeader);

            for (MrrViewReport mrr : mrrongList) {
                JSONArray dataTable = new JSONArray();
                String stateName = mrr.getStateName() != null ? mrr.getStateName() : "";

                if (mrr.getMrrCompleted() != null && mrr.getMrrSanctioned() != null) {
                    // Ensure no division by zero
                    float perc = mrr.getMrrSanctioned() > 0
                            ? (float) mrr.getMrrCompleted() * 100 / mrr.getMrrSanctioned()
                            : 0.0f;
                    categories.put(stateName);

                    dataTable.put(stateName);

                    int totalTehsil = mrr.getTotalTehsils() != null ? mrr.getTotalTehsils() : 0;
                    grandTotalVillage += totalTehsil;
                    dataTable.put(totalTehsil);

                    int sanctioned = mrr.getMrrSanctioned() != null ? mrr.getMrrSanctioned() : 0;
                    grandTotalROR += sanctioned;
                    dataTable.put(sanctioned);

                    data.put(String.format("%.02f", perc));

                    int completed = mrr.getMrrCompleted() != null ? mrr.getMrrCompleted() : 0;
                    grandTotalCLRCompleted += completed;
                    dataTable.put(completed);

                    // Assign colors based on percentage
                    if (perc == 0.0) {
                        colors.put("#FF0000");
                    } else if (perc > 0 && perc < 30) {
                        colors.put("#fcae91");
                    } else if (perc >= 30 && perc < 66) {
                        colors.put("#fee5d9");
                    } else if (perc >= 66 && perc < 81) {
                        colors.put("#bdd7e7");
                    } else if (perc >= 81 && perc <= 100) {
                        colors.put("#6baed6");
                    }
                } else {
                    categories.put(mrr.getLgdCode());
                    data.put(0);
                    dataTable.put(stateName).put(0).put(0).put(0);
                }

                grandDataTable.put(dataTable);
            }

            // Add total row
            JSONArray totalRow = new JSONArray();
            totalRow.put("Total");
            totalRow.put("");
            totalRow.put(String.format("%,d", grandTotalVillage));
            totalRow.put(String.format("%,d", grandTotalROR));
            totalRow.put(String.format("%,d", grandTotalCLRCompleted));
            float grandPerc = grandTotalVillage > 0 ? (float) grandTotalCLRCompleted * 100 / grandTotalVillage : 0.0f;
            totalRow.put(String.format("%.02f", grandPerc));
            grandDataTable.put(totalRow);

            // Populate year-wise data
            for (Object[] row : yearWiseData) {
                if (row[0] != null && row[1] != null && row[2] != null) {
                    yearWiseCLRDataYear.put("1-" + row[1].toString() + "-" + row[0].toString());
                    yearWiseCLRDataValue.put(new DecimalFormat("#").format(row[2]));
                }
            }

            // Construct final JSON object
            jObj.put("categories", categories);
            jObj.put("chartvalue", data);
            jObj.put("grandperc", String.format("%.02f", grandPerc));
            jObj.put("datatable", grandDataTable);
            jObj.put("colors", colors);
            jObj.put("clrYearWiseData", yearWiseCLRDataValue);
            jObj.put("clrYear", yearWiseCLRDataYear);
            jObj.put("grandvill", grandTotalVillage);
            jObj.put("grandcompletedcr", grandTotalCLRCompleted);
            jObj.put("totalror", grandTotalROR);

        } catch (Exception e) {
            Logger.getLogger(ChartApiController.class.getName()).log(Level.SEVERE, "Error processing dashboard data",
                    e);
        }
        return jObj.toString();
    }

    @CrossOrigin
    @PostMapping("/category=50")
    public ResponseEntity<List<Map<String, String>>> getDataSRO() {
        try {
            List<Map<String, String>> resultList = new ArrayList<>();
            List<SroReportDTO> sroReportModelList = stateSROService.getSroStateViwe();

            for (SroReportDTO sroReportModel : sroReportModelList) {
                Map<String, String> map = new HashMap<>();

                // Retrieve values with null checks
                int totalSro = sroReportModel.getNumberOfSROsInState() != null ? sroReportModel.getNumberOfSROsInState()
                        : 0;
                int computerization = sroReportModel.getNumberOfSROsUsingOnlineRegistration() != null
                        ? sroReportModel.getNumberOfSROsUsingOnlineRegistration()
                        : 0;

                // Populate map with values
                map.put("total", safeValue(totalSro));
                map.put("lgd", String.valueOf(sroReportModel.getLgdCode()));
                map.put("completed", safeValue(computerization));
                map.put("percent", calculatePercentage(computerization, totalSro));

                resultList.add(map);
            }

            return ResponseEntity.ok(resultList);
        } catch (Exception e) {
            Logger.getLogger(ChartApiController.class.getName()).log(Level.SEVERE, "Error fetching SRO data: {0}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @CrossOrigin
    @PostMapping("/category=501")
    public String getAllDataSRO() {
        JSONObject jObj = new JSONObject();
        int grandTotalSRO = 0;
        int grandTotalComputerizedSRO = 0;
        float grandPerc = 0.0f;

        try {
            List<SroReportDTO> sroList = stateSROService.getSroStateViwe();
            // List<Object[]> yearWiseData =
            // monthlyAllKpiStateSummaryRepository.getYearWiseSROData();
            List<Object[]> yearWiseData = new ArrayList<>();

            JSONArray categories = new JSONArray();
            JSONArray colors = new JSONArray();
            JSONArray data = new JSONArray();
            JSONArray yearWiseCLRDataValue = new JSONArray();
            JSONArray yearWiseCLRDataYear = new JSONArray();
            JSONArray grandDataTable = new JSONArray();

            // Creating header for the data table
            JSONArray dataTableHeader = new JSONArray();
            dataTableHeader.put("S No");
            dataTableHeader.put("State/UT");
            dataTableHeader.put("Total SRO");
            dataTableHeader.put("Total Computerized SRO");
            dataTableHeader.put("Computerized SRO (%)");
            grandDataTable.put(dataTableHeader);

            int sno = 1;

            for (SroReportDTO sro : sroList) {
                JSONArray dataTable = new JSONArray();

                int totalSRO = sro.getNumberOfSROsInState() != null ? sro.getNumberOfSROsInState() : 0;
                int computerizedSRO = sro.getNumberOfSROsUsingOnlineRegistration() != null
                        ? sro.getNumberOfSROsUsingOnlineRegistration()
                        : 0;

                // Calculate percentage, ensuring no division by zero
                float perc = (totalSRO > 0) ? (float) computerizedSRO * 100 / totalSRO : 0.0f;

                // Add data to table
                categories.put(sro.getStateName());
                dataTable.put(sno++);
                dataTable.put(sro.getStateName());
                dataTable.put(totalSRO);
                dataTable.put(computerizedSRO);
                dataTable.put(String.format("%.02f", perc));
                data.put(String.format("%.02f", perc));

                // Increment grand totals
                grandTotalSRO += totalSRO;
                grandTotalComputerizedSRO += computerizedSRO;

                // Determine color coding based on percentage
                if (perc == 0.0) {
                    colors.put("#FF0000");
                } else if (perc > 0 && perc < 30) {
                    colors.put("#ffe4d9");
                } else if (perc >= 30 && perc < 66) {
                    colors.put("#ebc5dd");
                } else if (perc >= 66 && perc < 81) {
                    colors.put("#fbae90");
                } else if (perc >= 81 && perc <= 100) {
                    colors.put("#c1dfb1");
                }

                // Add row to grand data table
                grandDataTable.put(dataTable);
            }

            // Calculate grand percentage ensuring no division by zero
            grandPerc = (grandTotalSRO > 0) ? (float) grandTotalComputerizedSRO * 100 / grandTotalSRO : 0.0f;

            // Add total row
            JSONArray totalRow = new JSONArray();
            totalRow.put("Total");
            totalRow.put("");
            totalRow.put(String.format("%,d", grandTotalSRO));
            totalRow.put(String.format("%,d", grandTotalComputerizedSRO));
            totalRow.put(String.format("%.02f", grandPerc));
            grandDataTable.put(totalRow);

            // Populate year-wise data
            for (Object[] row : yearWiseData) {
                if (row[0] != null && row[1] != null && row[2] != null) {
                    yearWiseCLRDataYear.put("1-" + row[1].toString() + "-" + row[0].toString());
                    yearWiseCLRDataValue.put(new DecimalFormat("#").format(row[2]));
                }
            }

            // Construct final JSON object
            jObj.put("categories", categories);
            jObj.put("chartvalue", data);
            jObj.put("grandperc", String.format("%.02f", grandPerc));
            jObj.put("datatable", grandDataTable);
            jObj.put("colors", colors);
            jObj.put("clrYearWiseData", yearWiseCLRDataValue);
            jObj.put("clrYear", yearWiseCLRDataYear);
            jObj.put("grandvill", grandTotalSRO);
            jObj.put("grandcompletedcr", grandTotalComputerizedSRO);
            jObj.put("totalror", grandTotalComputerizedSRO);

        } catch (Exception e) {
            Logger.getLogger(ChartApiController.class.getName()).log(Level.SEVERE, "Error processing SRO data: {0}", e);
        }

        return jObj.toString();
    }

    /*
     * @PostMapping("/category=60")
     * public ResponseEntity<List<Map<String, String>>> getDataSRO60() {
     * try {
     * List<Map<String, String>> resultList = new ArrayList<>();
     * List<SroReportDTO> sroReportModelList = stateSROService.getSroStateViwe();
     *
     * for (SroReportDTO data : sroReportModelList) {
     * // Creating map for response
     * Map<String, String> map = new HashMap<>();
     * int totalSro = data.getNumberOfSROsInState() != null ?
     * data.getNumberOfSROsInState() : 0;
     * int sroLr = data. != null
     * ? data.getSroLr()
     * : 0;
     *
     * map.put("total", safeValue(totalSro));
     * map.put("lgd", String.valueOf(data.getLgdCode()));
     * map.put("completed", safeValue(sroLr));
     * map.put("percent", String.format("%.2f", data.getSroLrPercent()));
     *
     * resultList.add(map);
     * }
     *
     * return ResponseEntity.ok(resultList);
     * } catch (Exception e) {
     * Logger.getLogger(ChartApiController.class.getName()).log(Level.SEVERE,
     * "Error fetching SRO data: {0}", e);
     * return
     * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.
     * emptyList());
     * }
     * }
     *
     * @PostMapping("/category=601")
     * public ResponseEntity<Map<String, Object>> getAllDataSRO601() {
     * Map<String, Object> response = new HashMap<>();
     * List<List<Object>> dataList = new ArrayList<>();
     * List<StateSROModel> sroReportDataList =
     * stateSROService.getAllStateSROModels();
     *
     * // Initialize grand totals
     * int grandTotalSRO = 0;
     * int grandTotalIntegrated = 0;
     *
     * try {
     * List<String> categories = new ArrayList<>();
     * List<String> colors = new ArrayList<>();
     * List<String> data = new ArrayList<>();
     *
     * // Adding table header
     * List<Object> header = Arrays.asList("S.No", "State/UT", "Total SRO",
     * "Total SRO Integrated With LR",
     * "SRO Integrated With LR (%)");
     * dataList.add(header);
     *
     * int sno = 1;
     *
     * for (StateSROModel sro : sroReportDataList) {
     * if (sro.getSroComputerized() != null) {
     * float perc = (float) sro.getSroLr() * 100 / sro.getTotalSro();
     * categories.add(sro.getName());
     *
     * List<Object> dataTable = new ArrayList<>();
     * dataTable.add(sno++);
     * dataTable.add(sro.getName());
     * dataTable.add(sro.getTotalSro() != null ? sro.getTotalSro() : 0);
     * dataTable.add(sro.getSroLr() != null ? sro.getSroLr() : 0);
     * dataTable.add(String.format("%.02f", perc));
     *
     * grandTotalSRO += sro.getTotalSro() != null ? sro.getTotalSro() : 0;
     * grandTotalIntegrated += sro.getSroLr() != null ? sro.getSroLr()
     * : 0;
     *
     * dataList.add(dataTable);
     * } else {
     * categories.add(sro.getLgdCode().toString());
     * data.add("0");
     * }
     * }
     *
     * // Calculate grand percentage ensuring no division by zero
     * float grandPerc = (grandTotalSRO > 0) ? (float) grandTotalIntegrated * 100 /
     * grandTotalSRO : 0.0f;
     *
     * // Adding total row
     * List<Object> totalRow = new ArrayList<>();
     * totalRow.add("Total");
     * totalRow.add("");
     * totalRow.add(grandTotalSRO);
     * totalRow.add(grandTotalIntegrated);
     * totalRow.add(String.format("%.02f", grandPerc));
     * dataList.add(totalRow);
     *
     * // Prepare final response
     * response.put("categories", categories);
     * response.put("chartvalue", data);
     * response.put("grandperc", grandPerc);
     * response.put("datatable", dataList);
     * response.put("colors", colors);
     * response.put("grandvill", grandTotalSRO);
     * response.put("grandcompletedcr", grandTotalIntegrated);
     * response.put("totalror", grandTotalIntegrated);
     *
     * return ResponseEntity.ok(response);
     * } catch (Exception e) {
     * Logger.getLogger(ChartApiController.class.getName()).log(Level.SEVERE,
     * "Error processing SRO data: {0}", e);
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
     * }
     * }
     */
    @CrossOrigin
    @PostMapping("/category=40")
    public ResponseEntity<List<Map<String, String>>> getDataSurveyResurvey() {
        try {
            List<Map<String, String>> resultList = new ArrayList<>();
            List<SurveyResurveyViewReport> surveyList = stateSurveyService.getAllSurveyResurveyList();

            for (SurveyResurveyViewReport surveyReport : surveyList) {
                Map<String, String> map = new HashMap<>();

                // Fetching and checking for null values
                int totalVillages = surveyReport.getTotalVillages() != null ? surveyReport.getTotalVillages() : 0;
                int surveyedVillages = surveyReport.getVillagesFinalPromulgationDone() != null
                        ? surveyReport.getVillagesFinalPromulgationDone()
                        : 0;

                // Preparing data for response
                map.put("total", safeValue(totalVillages));
                map.put("lgd", String.valueOf(surveyReport.getStateId()));
                map.put("completed", safeValue(surveyedVillages));
                map.put("percent", calculatePercentage(surveyedVillages, totalVillages));

                resultList.add(map);
            }

            return ResponseEntity.ok(resultList);
        } catch (Exception e) {
            Logger.getLogger(ChartApiController.class.getName()).log(Level.SEVERE,
                    "Error fetching survey resurvey data: {0}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @CrossOrigin
    @PostMapping("/category=401")
    public String getAllDataSurveyResurvey() {
        JSONObject jObj = new JSONObject();
        int grandTotalVillage = 0;
        int grandTotalSurveyed = 0;
        int grandTotalnotStarted = 0;

        try {
            // Fetching data from the repository
            List<SurveyResurveyViewReport> surveyList = stateSurveyService.getAllSurveyResurveyList();
            // List<Object[]> yearWiseData =
            // monthlyAllKpiStateSummaryRepository.getYearWiseSurveyReserveyData();
            List<Object[]> yearWiseData = new ArrayList<>();

            JSONArray categories = new JSONArray();
            JSONArray colors = new JSONArray();
            JSONArray data = new JSONArray();
            JSONArray yearWiseCLRDataValue = new JSONArray();
            JSONArray yearWiseCLRDataYear = new JSONArray();
            JSONArray grandDataTable = new JSONArray();

            // Creating header for the data table
            JSONArray dataTableHeader = new JSONArray();
            dataTableHeader.put("S No");
            dataTableHeader.put("State/UT");
            dataTableHeader.put("Total Villages");
            dataTableHeader.put("Total Surveyed");
            dataTableHeader.put("Survey Not Started");
            dataTableHeader.put("% Surveyed Villages");
            grandDataTable.put(dataTableHeader);

            int sno = 1;

            for (SurveyResurveyViewReport survey : surveyList) {
                JSONArray dataTable = new JSONArray();

                int totalVillages = survey.getTotalVillages() != null ? survey.getTotalVillages() : 0;
                int surveyedVillages = survey.getVillagesFinalPromulgationDone() != null ? survey.getVillagesFinalPromulgationDone()
                        : 0;
                int notStartedVillages = survey.getVillagesSurveySanctionNotStarted() != null
                        ? survey.getVillagesSurveySanctionNotStarted()
                        : 0;

                // Calculate percentage, ensuring no division by zero
                float perc = (totalVillages > 0) ? (float) surveyedVillages * 100 / totalVillages : 0.0f;

                // Add data to table
                categories.put(survey.getStateName());
                dataTable.put(sno++);
                dataTable.put(survey.getStateName());
                dataTable.put(String.format("%,d", totalVillages));
                dataTable.put(String.format("%,d", surveyedVillages));
                dataTable.put(String.format("%,d", notStartedVillages));
                dataTable.put(String.format("%.02f", perc));

                // Increment grand totals
                grandTotalVillage += totalVillages;
                grandTotalSurveyed += surveyedVillages;
                grandTotalnotStarted += notStartedVillages;

                // Determine color coding based on percentage
                if (perc == 0.0) {
                    colors.put("#FF0000");
                } else if (perc > 0 && perc < 30) {
                    colors.put("#ffe4d9");
                } else if (perc >= 30 && perc < 66) {
                    colors.put("#ebc5dd");
                } else if (perc >= 66 && perc < 81) {
                    colors.put("#fbae90");
                } else if (perc >= 81 && perc <= 100) {
                    colors.put("#c1dfb1");
                }

                // Add row to grand data table
                grandDataTable.put(dataTable);
            }

            // Calculate grand percentage ensuring no division by zero
            float grandPerc = (grandTotalVillage > 0) ? (float) grandTotalSurveyed * 100 / grandTotalVillage : 0.0f;

            // Add total row
            JSONArray totalRow = new JSONArray();
            totalRow.put("Total");
            totalRow.put("");
            totalRow.put(String.format("%,d", grandTotalVillage));
            totalRow.put(String.format("%,d", grandTotalSurveyed));
            totalRow.put(String.format("%,d", grandTotalnotStarted));
            totalRow.put(String.format("%.02f", grandPerc));
            grandDataTable.put(totalRow);

            // Populate year-wise data
            for (Object[] row : yearWiseData) {
                if (row[0] != null && row[1] != null && row[2] != null) {
                    yearWiseCLRDataYear.put("1-" + row[1].toString() + "-" + row[0].toString());
                    yearWiseCLRDataValue.put(new DecimalFormat("#").format(row[2]));
                }
            }

            // Construct final JSON object
            jObj.put("categories", categories);
            jObj.put("chartvalue", data);
            jObj.put("grandperc", String.format("%.02f", grandPerc));
            jObj.put("datatable", grandDataTable);
            jObj.put("colors", colors);
            jObj.put("clrYearWiseData", yearWiseCLRDataValue);
            jObj.put("clrYear", yearWiseCLRDataYear);
            jObj.put("grandvill", String.format("%,d", grandTotalVillage));
            jObj.put("grandcompletedcr", String.format("%,d", grandTotalnotStarted));
            jObj.put("totalror", String.format("%,d", grandTotalSurveyed));

        } catch (Exception e) {
            Logger.getLogger(ChartApiController.class.getName()).log(Level.SEVERE, "Error processing survey data: {0}",
                    e);
        }

        return jObj.toString();
    }

    @CrossOrigin
    @PostMapping("/category=80")
    public ResponseEntity<List<Map<String, String>>> getRccmsData() {
        try {
            List<Map<String, String>> resultList = new ArrayList<>();
            List<RcmsReportDTO> rccmsList = stateRCMSService.getRcmsStateViwe();

            for (RcmsReportDTO rccms : rccmsList) {
                Map<String, String> map = new HashMap<>();

                // Fetching and checking for null values
                int totalRevenueCourt = rccms.getTotalRevenueCourts() != null ? rccms.getTotalRevenueCourts() : 0;
                int computerizedrccms = rccms.getRevenueCourtsComputerized() != null
                        ? rccms.getRevenueCourtsComputerized()
                        : 0;

                // Preparing data for response
                map.put("total", safeValue(totalRevenueCourt));
                map.put("lgd", String.valueOf(rccms.getLgdCode()));
                map.put("completed", safeValue(computerizedrccms));
                map.put("percent", calculatePercentage(computerizedrccms, totalRevenueCourt));

                resultList.add(map);
            }

            return ResponseEntity.ok(resultList);
        } catch (Exception e) {
            Logger.getLogger(ChartApiController.class.getName()).log(Level.SEVERE,
                    "Error fetching rccms data: {0}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @CrossOrigin
    @PostMapping("/category=801")
    public ResponseEntity<Map<String, Object>> getRCCMSData() {
        int grandTotalRevenueCourt = 0;
        int grandTotalComputerizedRccms = 0;

        try {
            List<RcmsReportDTO> rccmsList = stateRCMSService.getRcmsStateViwe();
            List<List<Object>> grandDataTable = new ArrayList<>();

            // Header row
            List<Object> dataTableHeader = Arrays.asList(
                    "S.No",
                    "State/UT",
                    "Total Revenue Courts",
                    "Computerized Revenue Courts",
                    "%");
            grandDataTable.add(dataTableHeader);

            int sno = 1;

            for (RcmsReportDTO row : rccmsList) {
                // Ensure all elements in row are not null and can be casted properly

                List<Object> dataTableData = new ArrayList<>();
                // Prepare data row
                dataTableData.add(sno++);
                dataTableData.add(row.getStateName());
                dataTableData.add(String.format("%,d", row.getTotalRevenueCourts()));
                dataTableData.add(String.format("%,d", row.getRevenueCourtsComputerized()));
                dataTableData.add(String.format("%.2f", row.getRevenueCourtsComputerizedPercent()));

                // Increment grand totals
                grandTotalRevenueCourt += row.getTotalRevenueCourts();
                grandTotalComputerizedRccms += row.getRevenueCourtsComputerized();

                // Add row to data table
                grandDataTable.add(dataTableData);

            }

            // Calculate grand total percentage ensuring no division by zero
            float grandPercentage = (grandTotalRevenueCourt > 0)
                    ? (float) grandTotalComputerizedRccms * 100 / grandTotalRevenueCourt
                    : 0.0f;

            // Add total row
            List<Object> totalRow = new ArrayList<>();
            totalRow.add("Total");
            totalRow.add("");
            totalRow.add(String.format("%,d", grandTotalRevenueCourt));
            totalRow.add(String.format("%,d", grandTotalComputerizedRccms));
            totalRow.add(String.format("%.2f%%", grandPercentage));
            grandDataTable.add(totalRow);

            // Prepare final response
            Map<String, Object> response = new HashMap<>();
            response.put("datatable", grandDataTable);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Logger.getLogger(ChartApiController.class.getName()).log(Level.SEVERE,
                    "Error processing rccms data: {0}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @CrossOrigin
    @PostMapping("/category=90")
    public ResponseEntity<List<Map<String, String>>> getAadhaarLinkag() {
        try {
            List<Map<String, String>> resultList = new ArrayList<>();
            List<LinkedAadharViewReport> aadhaarList = stateAadharService.getAllAadhaarList();

            for (LinkedAadharViewReport aadhaar : aadhaarList) {
                Map<String, String> map = new HashMap<>();

                // Fetching and checking for null values
                int totalVillages = aadhaar.getTotalVillages() != null ? aadhaar.getTotalVillages() : 0;
                int linkedWithAadhaarFully = aadhaar.getVillagesWith100PercentRorLinkedAadhaar() != null
                        ? aadhaar.getVillagesWith100PercentRorLinkedAadhaar()
                        : 0;

                // Preparing data for response
                map.put("total", safeValue(totalVillages));
                map.put("lgd", String.valueOf(aadhaar.getLgdCode()));
                map.put("completed", safeValue(linkedWithAadhaarFully));
                map.put("percent", calculatePercentage(linkedWithAadhaarFully, totalVillages));

                resultList.add(map);
            }

            return ResponseEntity.ok(resultList);
        } catch (Exception e) {
            Logger.getLogger(ChartApiController.class.getName()).log(Level.SEVERE,
                    "Error fetching aadhaar linkage data: {0}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @CrossOrigin
    @PostMapping("/category=901")
    public ResponseEntity<Map<String, Object>> getAadhaarLinkageData() {
        int grandTotalVillages = 0;
        int grandTotalaadhaarliked = 0;

        try {
            List<LinkedAadharViewReport> aadhaarList = stateAadharService.getAllAadhaarList();
            List<List<Object>> grandDataTable = new ArrayList<>();

            // Header row
            List<Object> dataTableHeader = Arrays.asList(
                    "S.No",
                    "State/UT",
                    "Total No. of Villages",
                    "Where 100% RoR linked with Aadhaar",
                    "%");
            grandDataTable.add(dataTableHeader);

            int sno = 1;

            for (LinkedAadharViewReport row : aadhaarList) {
                // Ensure all elements in row are not null and can be casted properly

                List<Object> dataTableData = new ArrayList<>();
                // Prepare data row
                dataTableData.add(sno++);
                dataTableData.add(row.getStateName());
                dataTableData.add(String.format("%,d", row.getTotalVillages()));
                dataTableData.add(String.format("%,d", row.getVillagesWith100PercentRorLinkedAadhaar()));
                dataTableData.add(String.format("%.2f", row.getVillagesWith100PercentRorLinkedAadhaarPercent()));

                // Increment grand totals
                grandTotalVillages += row.getTotalVillages();
                grandTotalaadhaarliked += row.getVillagesWith100PercentRorLinkedAadhaar();

                // Add row to data table
                grandDataTable.add(dataTableData);

            }

            // Calculate grand total percentage ensuring no division by zero
            float grandPercentage = (grandTotalVillages > 0)
                    ? (float) grandTotalaadhaarliked * 100 / grandTotalVillages
                    : 0.0f;

            // Add total row
            List<Object> totalRow = new ArrayList<>();
            totalRow.add("Total");
            totalRow.add("");
            totalRow.add(String.format("%,d", grandTotalVillages));
            totalRow.add(String.format("%,d", grandTotalaadhaarliked));
            totalRow.add(String.format("%.2f", grandPercentage));
            grandDataTable.add(totalRow);

            // Prepare final response
            Map<String, Object> response = new HashMap<>();
            response.put("datatable", grandDataTable);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Logger.getLogger(ChartApiController.class.getName()).log(Level.SEVERE,
                    "Error processing Aadhaar linkage data: {0}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
