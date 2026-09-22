package in.gov.dilrmp.services.cna;

import in.gov.dilrmp.models.cna.DilrmpExpenditureStatusReport;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DilrmpExpenditureStatusReportService {

    private static final Logger logger = LoggerFactory.getLogger(DilrmpExpenditureStatusReportService.class);
    private static final int MAX_COLUMNS_TO_SCAN = 20;
    private static final int MAX_HEADER_ROWS_TO_SCAN = 25;

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };

    private static final Pattern AS_ON_DATE_PATTERN =
            Pattern.compile("as\\s+on\\s+(\\d{1,2}[./\\-]\\d{1,2}[./\\-]\\d{2,4})", Pattern.CASE_INSENSITIVE);
    private static final Pattern FY_PATTERN =
            Pattern.compile("fy[-\\s]*(\\d{4}[-/]\\d{2,4})", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATE_IN_TEXT_PATTERN =
            Pattern.compile("(\\d{1,2}[./\\-]\\d{1,2}[./\\-]\\d{2,4})");

    private static final List<String> MONTH_NAMES = List.of(
            "january", "february", "march", "april", "may", "june",
            "july", "august", "september", "october", "november", "december"
    );

    @Autowired
    private DilrmpExpenditureStatusReportRepository repository;

    public List<DilrmpExpenditureStatusReport> getLatestUploadReport() {
        LocalDateTime latestUpload = repository.findLatestUploadOn();
        if (latestUpload == null) {
            return Collections.emptyList();
        }
        return findByUploadDay(latestUpload.toLocalDate());
    }

    public List<DilrmpExpenditureStatusReport> getReportAsOnDate(LocalDate asOnDate) {
        if (asOnDate == null) {
            throw new IllegalArgumentException("Please select the report as on date.");
        }
        return repository.findByReportAsOnDateOrderBySerialNoAscStateNameAsc(asOnDate);
    }

    public List<LocalDate> getDistinctReportAsOnDates() {
        return repository.findDistinctReportAsOnDates();
    }

    @Transactional
    public int deleteLatestUpload() {
        LocalDateTime latestUpload = repository.findLatestUploadOn();
        if (latestUpload == null) {
            throw new IllegalArgumentException("No uploaded report found to delete.");
        }
        return deleteByUploadDay(latestUpload.toLocalDate());
    }

    @Transactional
    public int deleteByReportAsOnDate(LocalDate reportAsOnDate) {
        if (reportAsOnDate == null) {
            throw new IllegalArgumentException("Please select the report as on date to delete.");
        }
        long count = repository.countByReportAsOnDate(reportAsOnDate);
        if (count == 0) {
            throw new IllegalArgumentException(
                    "No report found for DILRMP Expenditure Status as on "
                            + reportAsOnDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ".");
        }
        repository.deleteByReportAsOnDate(reportAsOnDate);
        repository.flush();
        logger.info("Deleted {} DILRMP expenditure status record(s) for report as on {}", count, reportAsOnDate);
        return (int) count;
    }

    private List<DilrmpExpenditureStatusReport> findByUploadDay(LocalDate uploadDay) {
        return repository.findByUploadedOnBetween(
                uploadDay.atStartOfDay(),
                uploadDay.plusDays(1).atStartOfDay());
    }

    private int deleteByUploadDay(LocalDate uploadDay) {
        LocalDateTime startOfDay = uploadDay.atStartOfDay();
        LocalDateTime endOfDay = uploadDay.plusDays(1).atStartOfDay();
        long count = repository.countByUploadDateRange(startOfDay, endOfDay);
        repository.deleteByUploadedOnGreaterThanEqualAndUploadedOnLessThan(startOfDay, endOfDay);
        repository.flush();
        logger.info("Deleted {} DILRMP expenditure status record(s) uploaded on {}", count, uploadDay);
        return (int) count;
    }

    @Transactional
    public int uploadFromExcel(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an Excel file to upload.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Only .xlsx Excel files are supported.");
        }

        List<DilrmpExpenditureStatusReport> records = parseExcel(file);
        if (records.isEmpty()) {
            throw new IllegalArgumentException("No valid data rows found in the Excel file.");
        }

        LocalDate reportAsOnDate = records.get(0).getReportAsOnDate();
        if (reportAsOnDate == null) {
            throw new IllegalArgumentException(
                    "Could not detect report date from Excel. Ensure the file title contains "
                            + "'DILRMP Expenditure Status as on DD.MM.YYYY'.");
        }
        if (repository.countByReportAsOnDate(reportAsOnDate) > 0) {
            throw new IllegalArgumentException(
                    "Report already exists for DILRMP Expenditure Status as on "
                            + reportAsOnDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                            + ". Delete the existing report first before uploading again.");
        }

        LocalDateTime uploadedOn = LocalDateTime.now();
        for (DilrmpExpenditureStatusReport record : records) {
            record.setUploadedOn(uploadedOn);
        }

        List<DilrmpExpenditureStatusReport> saved = repository.saveAll(records);
        repository.flush();
        logger.info("DILRMP expenditure status upload saved {} record(s)", saved.size());
        return saved.size();
    }

    private static class ColumnMapping {
        private int headerRowIndex;
        private int serialNoCol = -1;
        private int stateNameCol = -1;
        private int releaseByCenterCol = -1;
        private int cnaOpeningBalanceCol = -1;
        private int expenditureAprilCol = -1;
        private int expenditureMayCol = -1;
        private int expenditureJuneCol = -1;
        private int totalExpenditureCol = -1;
        private int cnaBalanceCol = -1;
        private String financialYear;
        private LocalDate reportAsOnDate;
        private String expenditureHeader1;
        private String expenditureHeader2;
        private String expenditureHeader3;
        private String openingBalanceHeader;
        private String totalExpenditureHeader;
        private String cnaBalanceHeader;

        private boolean isComplete() {
            return stateNameCol >= 0 && releaseByCenterCol >= 0 && cnaOpeningBalanceCol >= 0
                    && expenditureAprilCol >= 0 && expenditureMayCol >= 0 && expenditureJuneCol >= 0
                    && totalExpenditureCol >= 0 && cnaBalanceCol >= 0;
        }

        private boolean hasAnyMapping() {
            return serialNoCol >= 0 || stateNameCol >= 0 || releaseByCenterCol >= 0
                    || cnaOpeningBalanceCol >= 0 || expenditureAprilCol >= 0 || expenditureMayCol >= 0
                    || expenditureJuneCol >= 0 || totalExpenditureCol >= 0 || cnaBalanceCol >= 0;
        }

        private int requiredMappedCount() {
            int count = 0;
            if (stateNameCol >= 0) count++;
            if (releaseByCenterCol >= 0) count++;
            if (cnaOpeningBalanceCol >= 0) count++;
            if (expenditureAprilCol >= 0) count++;
            if (expenditureMayCol >= 0) count++;
            if (expenditureJuneCol >= 0) count++;
            if (totalExpenditureCol >= 0) count++;
            if (cnaBalanceCol >= 0) count++;
            return count;
        }

        private List<String> missingRequiredColumns() {
            List<String> missing = new ArrayList<>();
            if (stateNameCol < 0) missing.add("State Name");
            if (releaseByCenterCol < 0) missing.add("Release by Center");
            if (cnaOpeningBalanceCol < 0) missing.add("CNA Opening Balance");
            if (expenditureAprilCol < 0) missing.add("Expenditure in April");
            if (expenditureMayCol < 0) missing.add("Expenditure in May");
            if (expenditureJuneCol < 0) missing.add("Expenditure in June");
            if (totalExpenditureCol < 0) missing.add("Total Expenditure till");
            if (cnaBalanceCol < 0) missing.add("CNA Balance");
            return missing;
        }
    }

    private List<DilrmpExpenditureStatusReport> parseExcel(MultipartFile file) throws IOException {
        List<DilrmpExpenditureStatusReport> records = new ArrayList<>();
        LocalDateTime uploadedOn = LocalDateTime.now();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return records;
            }

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            DataFormatter dataFormatter = new DataFormatter();
            ColumnMapping mapping = findColumnMapping(sheet, evaluator, dataFormatter);

            for (int rowIndex = mapping.headerRowIndex + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isSkippableRow(sheet, row, mapping, evaluator, dataFormatter)) {
                    continue;
                }

                String stateName = getDataCellValue(sheet, row, mapping.stateNameCol, evaluator, dataFormatter);
                if (stateName.isBlank() || isTotalRow(stateName)) {
                    continue;
                }

                DilrmpExpenditureStatusReport record = new DilrmpExpenditureStatusReport();
                record.setSerialNo(parseSerialNo(
                        getDataCellValue(sheet, row, mapping.serialNoCol, evaluator, dataFormatter), rowIndex));
                record.setStateName(stateName.trim());
                record.setReleaseByCenter(getNumericValue(sheet, row, mapping.releaseByCenterCol, evaluator, dataFormatter));
                record.setCnaOpeningBalance(getNumericValue(sheet, row, mapping.cnaOpeningBalanceCol, evaluator, dataFormatter));
                record.setExpenditureApril(getNumericValue(sheet, row, mapping.expenditureAprilCol, evaluator, dataFormatter));
                record.setExpenditureMay(getNumericValue(sheet, row, mapping.expenditureMayCol, evaluator, dataFormatter));
                record.setExpenditureJune(getNumericValue(sheet, row, mapping.expenditureJuneCol, evaluator, dataFormatter));
                record.setTotalExpenditure(getNumericValue(sheet, row, mapping.totalExpenditureCol, evaluator, dataFormatter));
                record.setCnaBalance(getNumericValue(sheet, row, mapping.cnaBalanceCol, evaluator, dataFormatter));
                record.setFinancialYear(mapping.financialYear);
                record.setReportAsOnDate(mapping.reportAsOnDate);
                record.setExpenditureLabel1(mapping.expenditureHeader1);
                record.setExpenditureLabel2(mapping.expenditureHeader2);
                record.setExpenditureLabel3(mapping.expenditureHeader3);
                record.setOpeningBalanceHeader(mapping.openingBalanceHeader);
                record.setTotalExpenditureHeader(mapping.totalExpenditureHeader);
                record.setCnaBalanceHeader(mapping.cnaBalanceHeader);
                record.setUploadedOn(uploadedOn);
                records.add(record);
            }
        }

        logger.info("DILRMP expenditure status parse found {} data row(s)", records.size());
        return records;
    }

    private ColumnMapping findColumnMapping(Sheet sheet, FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        int lastRowToScan = Math.min(sheet.getLastRowNum(), MAX_HEADER_ROWS_TO_SCAN);
        ColumnMapping mapping = new ColumnMapping();
        int lastHeaderRowFound = sheet.getFirstRowNum();

        LocalDate titleAsOnDate = extractAsOnDateFromTitle(sheet, evaluator, dataFormatter);
        if (titleAsOnDate != null) {
            mapping.reportAsOnDate = titleAsOnDate;
        }

        for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= lastRowToScan; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null || isTitleRow(sheet, rowIndex, evaluator, dataFormatter)) {
                continue;
            }

            ColumnMapping rowMapping = mapColumnsFromRow(sheet, rowIndex, evaluator, dataFormatter);
            ColumnMapping combinedMapping = mapColumnsFromCombinedRows(
                    sheet, rowIndex, Math.min(rowIndex + 2, lastRowToScan), evaluator, dataFormatter);

            if (rowMapping.hasAnyMapping()) {
                mergeMapping(mapping, rowMapping);
                lastHeaderRowFound = rowIndex;
            }
            if (combinedMapping.hasAnyMapping()) {
                mergeMapping(mapping, combinedMapping);
                lastHeaderRowFound = rowIndex;
            }

            applyPositionalFallback(mapping);

            if (mapping.isComplete()) {
                mapping.headerRowIndex = findLastHeaderRowIndex(
                        sheet, mapping, lastHeaderRowFound, evaluator, dataFormatter);
                if (mapping.reportAsOnDate == null) {
                    mapping.reportAsOnDate = extractAsOnDateFromHeaders(
                            sheet, mapping.headerRowIndex, mapping, evaluator, dataFormatter);
                }
                populateDisplayHeaders(sheet, mapping, evaluator, dataFormatter);
                logger.info("DILRMP Excel columns mapped at row {}: state={}, release={}, opening={}, apr={}, may={}, jun={}, total={}, balance={}",
                        lastHeaderRowFound + 1, mapping.stateNameCol, mapping.releaseByCenterCol,
                        mapping.cnaOpeningBalanceCol, mapping.expenditureAprilCol, mapping.expenditureMayCol,
                        mapping.expenditureJuneCol, mapping.totalExpenditureCol, mapping.cnaBalanceCol);
                return mapping;
            }
        }

        applyPositionalFallback(mapping);
        if (mapping.isComplete()) {
            mapping.headerRowIndex = findLastHeaderRowIndex(
                    sheet, mapping, lastHeaderRowFound, evaluator, dataFormatter);
            populateDisplayHeaders(sheet, mapping, evaluator, dataFormatter);
            return mapping;
        }

        throw new IllegalArgumentException(buildHeaderValidationMessage(mapping));
    }

    private int findLastHeaderRowIndex(Sheet sheet, ColumnMapping mapping, int startRow,
                                       FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        int lastHeaderRow = startRow;
        int scanLimit = Math.min(startRow + 4, sheet.getLastRowNum());
        for (int rowIndex = startRow; rowIndex <= scanLimit; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                break;
            }

            String stateName = getDataCellValue(sheet, row, mapping.stateNameCol, evaluator, dataFormatter);
            if (!stateName.isBlank()) {
                String normalizedState = normalizeHeader(stateName);
                if (!matchesStateNameHeader(normalizedState) && !isTotalRow(stateName)) {
                    return Math.max(startRow, rowIndex - 1);
                }
            }
            lastHeaderRow = rowIndex;
        }
        return lastHeaderRow;
    }

    private void populateDisplayHeaders(Sheet sheet, ColumnMapping mapping, FormulaEvaluator evaluator,
                                        DataFormatter dataFormatter) {
        mapping.expenditureHeader1 = extractRawHeaderText(
                sheet, mapping.headerRowIndex, mapping.expenditureAprilCol, evaluator, dataFormatter);
        mapping.expenditureHeader2 = extractRawHeaderText(
                sheet, mapping.headerRowIndex, mapping.expenditureMayCol, evaluator, dataFormatter);
        mapping.expenditureHeader3 = extractRawHeaderText(
                sheet, mapping.headerRowIndex, mapping.expenditureJuneCol, evaluator, dataFormatter);
        mapping.openingBalanceHeader = extractRawHeaderText(
                sheet, mapping.headerRowIndex, mapping.cnaOpeningBalanceCol, evaluator, dataFormatter);
        mapping.totalExpenditureHeader = extractRawHeaderText(
                sheet, mapping.headerRowIndex, mapping.totalExpenditureCol, evaluator, dataFormatter);
        mapping.cnaBalanceHeader = extractRawHeaderText(
                sheet, mapping.headerRowIndex, mapping.cnaBalanceCol, evaluator, dataFormatter);

        if (mapping.expenditureHeader1 == null || mapping.expenditureHeader1.isBlank()) {
            mapping.expenditureHeader1 = buildDefaultExpenditureHeader(mapping.expenditureAprilCol, 1);
        }
        if (mapping.expenditureHeader2 == null || mapping.expenditureHeader2.isBlank()) {
            mapping.expenditureHeader2 = buildDefaultExpenditureHeader(mapping.expenditureMayCol, 2);
        }
        if (mapping.expenditureHeader3 == null || mapping.expenditureHeader3.isBlank()) {
            mapping.expenditureHeader3 = buildDefaultExpenditureHeader(mapping.expenditureJuneCol, 3);
        }
        if (mapping.openingBalanceHeader == null || mapping.openingBalanceHeader.isBlank()) {
            mapping.openingBalanceHeader = "CNA Opening Balance";
        }
        if (mapping.totalExpenditureHeader == null || mapping.totalExpenditureHeader.isBlank()) {
            mapping.totalExpenditureHeader = mapping.reportAsOnDate != null
                    ? "Total Expenditure till (" + mapping.reportAsOnDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ")"
                    : "Total Expenditure till";
        }
        if (mapping.cnaBalanceHeader == null || mapping.cnaBalanceHeader.isBlank()) {
            mapping.cnaBalanceHeader = mapping.reportAsOnDate != null
                    ? "CNA Balance (" + mapping.reportAsOnDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ")"
                    : "CNA Balance";
        }
    }

    private String buildDefaultExpenditureHeader(int colIndex, int position) {
        if (colIndex < 0) {
            return "Expenditure";
        }
        String[] defaults = {"Expenditure in April", "Expenditure in May", "Expenditure in June"};
        if (position >= 1 && position <= defaults.length) {
            return defaults[position - 1];
        }
        return "Expenditure";
    }

    private String extractRawHeaderText(Sheet sheet, int headerRowIndex, int colIndex,
                                        FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        if (colIndex < 0) {
            return "";
        }
        StringBuilder combined = new StringBuilder();
        int startRow = Math.max(sheet.getFirstRowNum(), headerRowIndex - 2);
        for (int rowIndex = startRow; rowIndex <= headerRowIndex; rowIndex++) {
            String cellText = getHeaderCellValue(sheet, rowIndex, colIndex, evaluator, dataFormatter);
            if (cellText.isBlank() || isTitleLikeHeader(normalizeHeader(cellText))) {
                continue;
            }
            if (combined.length() > 0) {
                combined.append(' ');
            }
            combined.append(cellText.trim());
        }
        return combined.toString().replaceAll("\\s+", " ").trim();
    }

    private void applyPositionalFallback(ColumnMapping mapping) {
        if (mapping.stateNameCol < 0) {
            return;
        }
        int base = mapping.stateNameCol;
        if (mapping.serialNoCol < 0 && base > 0) {
            mapping.serialNoCol = base - 1;
        }
        if (mapping.releaseByCenterCol < 0) {
            mapping.releaseByCenterCol = base + 1;
        }
        if (mapping.cnaOpeningBalanceCol < 0) {
            mapping.cnaOpeningBalanceCol = base + 2;
        }
        if (mapping.expenditureAprilCol < 0) {
            mapping.expenditureAprilCol = base + 3;
        }
        if (mapping.expenditureMayCol < 0) {
            mapping.expenditureMayCol = base + 4;
        }
        if (mapping.expenditureJuneCol < 0) {
            mapping.expenditureJuneCol = base + 5;
        }
        if (mapping.totalExpenditureCol < 0) {
            mapping.totalExpenditureCol = base + 6;
        }
        if (mapping.cnaBalanceCol < 0) {
            mapping.cnaBalanceCol = base + 7;
        }
    }

    private boolean isTitleRow(Sheet sheet, int rowIndex, FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return false;
        }
        StringBuilder rowText = new StringBuilder();
        for (int colIndex = 0; colIndex < MAX_COLUMNS_TO_SCAN; colIndex++) {
            String cellText = getHeaderCellValue(sheet, rowIndex, colIndex, evaluator, dataFormatter);
            if (!cellText.isBlank()) {
                if (rowText.length() > 0) {
                    rowText.append(' ');
                }
                rowText.append(cellText);
            }
        }
        String normalized = normalizeHeader(rowText.toString());
        if (normalized.isBlank()) {
            return false;
        }
        return (normalized.contains("dilrmp expenditure status") || normalized.contains("expenditure status as on"))
                && !normalized.contains("state name");
    }

    private ColumnMapping mapColumnsFromCombinedRows(Sheet sheet, int startRow, int endRow,
                                                     FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        ColumnMapping mapping = new ColumnMapping();
        for (int colIndex = 0; colIndex < MAX_COLUMNS_TO_SCAN; colIndex++) {
            StringBuilder combined = new StringBuilder();
            for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
                String cellText = getHeaderCellValue(sheet, rowIndex, colIndex, evaluator, dataFormatter);
                if (!cellText.isBlank()) {
                    if (combined.length() > 0) {
                        combined.append(' ');
                    }
                    combined.append(cellText);
                }
            }
            applyHeaderToMapping(mapping, normalizeHeader(combined.toString()), colIndex);
        }
        return mapping;
    }

    private void mergeMapping(ColumnMapping target, ColumnMapping source) {
        if (source.serialNoCol >= 0) target.serialNoCol = source.serialNoCol;
        if (source.stateNameCol >= 0) target.stateNameCol = source.stateNameCol;
        if (source.releaseByCenterCol >= 0) target.releaseByCenterCol = source.releaseByCenterCol;
        if (source.cnaOpeningBalanceCol >= 0) target.cnaOpeningBalanceCol = source.cnaOpeningBalanceCol;
        if (source.expenditureAprilCol >= 0) target.expenditureAprilCol = source.expenditureAprilCol;
        if (source.expenditureMayCol >= 0) target.expenditureMayCol = source.expenditureMayCol;
        if (source.expenditureJuneCol >= 0) target.expenditureJuneCol = source.expenditureJuneCol;
        if (source.totalExpenditureCol >= 0) target.totalExpenditureCol = source.totalExpenditureCol;
        if (source.cnaBalanceCol >= 0) target.cnaBalanceCol = source.cnaBalanceCol;
        if (source.financialYear != null) target.financialYear = source.financialYear;
        if (source.reportAsOnDate != null) target.reportAsOnDate = source.reportAsOnDate;
        if (source.expenditureHeader1 != null) target.expenditureHeader1 = source.expenditureHeader1;
        if (source.expenditureHeader2 != null) target.expenditureHeader2 = source.expenditureHeader2;
        if (source.expenditureHeader3 != null) target.expenditureHeader3 = source.expenditureHeader3;
        if (source.openingBalanceHeader != null) target.openingBalanceHeader = source.openingBalanceHeader;
        if (source.totalExpenditureHeader != null) target.totalExpenditureHeader = source.totalExpenditureHeader;
        if (source.cnaBalanceHeader != null) target.cnaBalanceHeader = source.cnaBalanceHeader;
    }

    private String buildHeaderValidationMessage(ColumnMapping mapping) {
        List<String> missing = mapping.missingRequiredColumns();
        if (mapping.requiredMappedCount() == 0) {
            return "Invalid Excel format. Could not find expected header columns. "
                    + "Required: State Name, Release by Center, CNA Opening Balance, "
                    + "Expenditure in April/May/June, Total Expenditure till, CNA Balance.";
        }
        return "Invalid Excel format. Found " + mapping.requiredMappedCount()
                + " of 8 required columns. Missing: " + String.join(", ", missing) + ".";
    }

    private ColumnMapping mapColumnsFromRow(Sheet sheet, int rowIndex, FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        ColumnMapping mapping = new ColumnMapping();
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return mapping;
        }

        for (int colIndex = 0; colIndex < MAX_COLUMNS_TO_SCAN; colIndex++) {
            String header = normalizeHeader(getHeaderCellValue(sheet, rowIndex, colIndex, evaluator, dataFormatter));
            if (header.isBlank()) {
                continue;
            }
            applyHeaderToMapping(mapping, header, colIndex);
        }
        return mapping;
    }

    private void applyHeaderToMapping(ColumnMapping mapping, String header, int colIndex) {
        if (header.isBlank() || isTitleLikeHeader(header)) {
            return;
        }

        if (mapping.serialNoCol < 0 && matchesSerialNoHeader(header)) {
            mapping.serialNoCol = colIndex;
            return;
        }
        if (mapping.stateNameCol < 0 && matchesStateNameHeader(header)) {
            mapping.stateNameCol = colIndex;
            return;
        }
        if (mapping.releaseByCenterCol < 0 && matchesReleaseByCenterHeader(header)) {
            mapping.releaseByCenterCol = colIndex;
            mapping.financialYear = extractFinancialYear(header);
            return;
        }
        if (mapping.cnaOpeningBalanceCol < 0 && matchesCnaOpeningBalanceHeader(header)) {
            mapping.cnaOpeningBalanceCol = colIndex;
            return;
        }
        if (matchesExpenditureMonthHeader(header)) {
            assignExpenditureColumn(mapping, colIndex);
            return;
        }
        if (mapping.totalExpenditureCol < 0 && matchesTotalExpenditureHeader(header)) {
            mapping.totalExpenditureCol = colIndex;
            if (mapping.reportAsOnDate == null) {
                mapping.reportAsOnDate = parseDateFromText(header);
            }
            return;
        }
        if (mapping.cnaBalanceCol < 0 && matchesCnaBalanceHeader(header)) {
            mapping.cnaBalanceCol = colIndex;
            if (mapping.reportAsOnDate == null) {
                mapping.reportAsOnDate = parseDateFromText(header);
            }
        }
    }

    private boolean isTitleLikeHeader(String header) {
        return header.contains("dilrmp expenditure status")
                || header.contains("expenditure status as on");
    }

    private LocalDate extractAsOnDateFromTitle(Sheet sheet, FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        int lastRow = Math.min(sheet.getLastRowNum(), 5);
        for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= lastRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            for (int colIndex = 0; colIndex < MAX_COLUMNS_TO_SCAN; colIndex++) {
                LocalDate date = parseAsOnDateFromText(getHeaderCellValue(sheet, rowIndex, colIndex, evaluator, dataFormatter));
                if (date != null) {
                    return date;
                }
            }
        }
        return null;
    }

    private LocalDate extractAsOnDateFromHeaders(Sheet sheet, int headerRowIndex, ColumnMapping mapping,
                                                 FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        Row row = sheet.getRow(headerRowIndex);
        if (row == null) {
            return null;
        }
        for (int colIndex : new int[]{mapping.totalExpenditureCol, mapping.cnaBalanceCol, mapping.cnaOpeningBalanceCol}) {
            if (colIndex < 0) {
                continue;
            }
            LocalDate date = parseDateFromText(getHeaderCellValue(sheet, headerRowIndex, colIndex, evaluator, dataFormatter));
            if (date != null) {
                return date;
            }
        }
        return null;
    }

    private LocalDate parseAsOnDateFromText(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        Matcher matcher = AS_ON_DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            return parseDateToken(matcher.group(1));
        }
        return null;
    }

    private LocalDate parseDateFromText(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        Matcher matcher = DATE_IN_TEXT_PATTERN.matcher(text);
        LocalDate lastDate = null;
        while (matcher.find()) {
            LocalDate parsed = parseDateToken(matcher.group(1));
            if (parsed != null) {
                lastDate = parsed;
            }
        }
        return lastDate;
    }

    private String extractFinancialYear(String header) {
        Matcher matcher = FY_PATTERN.matcher(header);
        if (matcher.find()) {
            return "FY-" + matcher.group(1).replace('/', '-');
        }
        return null;
    }

    private LocalDate parseDateToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(token.trim(), formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    private boolean matchesSerialNoHeader(String header) {
        return header.equals("s no") || header.equals("sno") || header.equals("serial no")
                || header.startsWith("s no ") || header.contains("serial number") || header.equals("sl no");
    }

    private boolean matchesStateNameHeader(String header) {
        return header.contains("state name") || header.equals("state") || header.contains("name of state");
    }

    private boolean matchesReleaseByCenterHeader(String header) {
        return header.contains("release by center") || header.contains("release by centre")
                || header.contains("release by the center") || header.contains("release by the centre")
                || (header.contains("release") && header.contains("center"))
                || (header.contains("release") && header.contains("centre"))
                || (header.contains("release") && header.contains("fy"));
    }

    private boolean matchesCnaOpeningBalanceHeader(String header) {
        return header.contains("cna opening balance")
                || header.contains("opening balance")
                || header.contains("cna opening");
    }

    private void assignExpenditureColumn(ColumnMapping mapping, int colIndex) {
        if (mapping.expenditureAprilCol < 0) {
            mapping.expenditureAprilCol = colIndex;
        } else if (mapping.expenditureMayCol < 0) {
            mapping.expenditureMayCol = colIndex;
        } else if (mapping.expenditureJuneCol < 0) {
            mapping.expenditureJuneCol = colIndex;
        }
    }

    private boolean matchesExpenditureMonthHeader(String header) {
        if (header.contains("total") && header.contains("expenditure")) {
            return false;
        }
        if (header.contains("expenditure in ") || header.contains("expenditure ")) {
            return true;
        }
        for (String month : MONTH_NAMES) {
            if (header.equals(month)
                    || header.endsWith(" " + month)
                    || header.startsWith(month + " ")
                    || (header.contains(month) && header.contains("exp"))) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesExpenditureAprilHeader(String header) {
        return matchesExpenditureMonthHeader(header) && header.contains("april");
    }

    private boolean matchesExpenditureMayHeader(String header) {
        return matchesExpenditureMonthHeader(header) && header.contains("may");
    }

    private boolean matchesExpenditureJuneHeader(String header) {
        return matchesExpenditureMonthHeader(header) && header.contains("june");
    }

    private boolean matchesTotalExpenditureHeader(String header) {
        return header.contains("total expenditure")
                || header.contains("total exp")
                || (header.contains("total") && header.contains("expenditure"));
    }

    private boolean matchesCnaBalanceHeader(String header) {
        return (header.contains("cna balance") && !header.contains("opening"))
                || (header.contains("balance") && !header.contains("opening")
                && !header.contains("expenditure") && !header.contains("total")
                && DATE_IN_TEXT_PATTERN.matcher(header).find());
    }

    private boolean isSkippableRow(Sheet sheet, Row row, ColumnMapping mapping,
                                   FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        String stateName = getDataCellValue(sheet, row, mapping.stateNameCol, evaluator, dataFormatter);
        if (stateName.isBlank()) {
            return true;
        }
        String normalized = normalizeHeader(stateName);
        return matchesStateNameHeader(normalized) || matchesReleaseByCenterHeader(normalized)
                || matchesTotalExpenditureHeader(normalized) || matchesCnaBalanceHeader(normalized)
                || isTotalRow(stateName);
    }

    private boolean isTotalRow(String stateName) {
        return "total".equalsIgnoreCase(stateName.trim());
    }

    private Integer parseSerialNo(String value, int rowIndex) {
        if (value == null || value.isBlank()) {
            return rowIndex;
        }
        try {
            return (int) Double.parseDouble(value.replace(",", "").trim());
        } catch (NumberFormatException ex) {
            return rowIndex;
        }
    }

    private BigDecimal getNumericValue(Sheet sheet, Row row, int colIndex,
                                       FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        BigDecimal value = getDataCellValueAsBigDecimal(sheet, row, colIndex, evaluator, dataFormatter);
        return value != null ? value : BigDecimal.ZERO;
    }

    private String normalizeHeader(String value) {
        return value.toLowerCase()
                .replace('\u00a0', ' ')
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String getHeaderCellValue(Sheet sheet, int rowIndex, int colIndex,
                                      FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return "";
        }

        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null) {
            String directValue = formatCellValue(cell, evaluator, dataFormatter);
            if (!directValue.isBlank()) {
                return directValue;
            }
        }

        if (cell != null) {
            Cell mergedCell = resolveMergedCell(sheet, cell);
            String mergedValue = formatCellValue(mergedCell, evaluator, dataFormatter);
            if (!mergedValue.isBlank() && !isTitleLikeHeader(normalizeHeader(mergedValue))) {
                return mergedValue;
            }
        }
        return "";
    }

    private String getDataCellValue(Sheet sheet, Row row, int colIndex,
                                    FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        Cell cell = getOrCreateCell(row, colIndex);
        if (cell == null) {
            return "";
        }
        cell = resolveMergedCell(sheet, cell);
        return formatCellValue(cell, evaluator, dataFormatter);
    }

    private Cell getOrCreateCell(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell != null ? cell : row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

    private String formatCellValue(Cell cell, FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        if (cell == null) {
            return "";
        }
        String value = dataFormatter.formatCellValue(cell, evaluator);
        return value == null ? "" : value.replace('\n', ' ').replace('\r', ' ').trim();
    }

    private Cell resolveMergedCell(Sheet sheet, Cell cell) {
        int rowIndex = cell.getRowIndex();
        int colIndex = cell.getColumnIndex();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.isInRange(rowIndex, colIndex)) {
                Row firstRow = sheet.getRow(region.getFirstRow());
                if (firstRow != null) {
                    Cell firstCell = firstRow.getCell(region.getFirstColumn());
                    if (firstCell != null) {
                        return firstCell;
                    }
                }
            }
        }
        return cell;
    }

    private BigDecimal getDataCellValueAsBigDecimal(Sheet sheet, Row row, int colIndex,
                                                    FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        Cell cell = getOrCreateCell(row, colIndex);
        if (cell == null) {
            return null;
        }
        cell = resolveMergedCell(sheet, cell);
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return null;
        }
        return parseBigDecimal(formatCellValue(cell, evaluator, dataFormatter));
    }

    private BigDecimal parseBigDecimal(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        String lowerValue = rawValue.trim().toLowerCase();
        if ("-".equals(rawValue.trim()) || "na".equals(lowerValue) || "n/a".equals(lowerValue) || "nil".equals(lowerValue)) {
            return null;
        }
        String numeric = rawValue.replace(",", "").replaceAll("[^0-9.\\-]", "").trim();
        if (numeric.isBlank() || "-".equals(numeric) || ".".equals(numeric)) {
            return null;
        }
        try {
            return new BigDecimal(numeric);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
