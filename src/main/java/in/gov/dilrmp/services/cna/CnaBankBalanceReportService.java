package in.gov.dilrmp.services.cna;

import in.gov.dilrmp.models.cna.CnaBankBalanceReport;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CnaBankBalanceReportService {

    private static final Logger logger = LoggerFactory.getLogger(CnaBankBalanceReportService.class);

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd MMM yyyy"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy")
    };

    @Autowired
    private CnaBankBalanceReportRepository repository;

    public List<CnaBankBalanceReport> findAll() {
        return repository.findAllByOrderByCnaNameAsc();
    }

    public List<CnaBankBalanceReport> getLatestUploadReport() {
        LocalDateTime latestUpload = repository.findLatestUploadOn();
        if (latestUpload == null) {
            return Collections.emptyList();
        }
        return findByUploadDay(latestUpload.toLocalDate());
    }

    public List<LocalDate> getDistinctUploadDates() {
        return repository.findDistinctUploadedOn().stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public List<CnaBankBalanceReport> getReportAsOnDate(LocalDate asOnDate) {
        if (asOnDate == null) {
            throw new IllegalArgumentException("Please select a date for the As On Date report.");
        }
        LocalDateTime startOfDay = asOnDate.atStartOfDay();
        LocalDateTime endOfDay = asOnDate.plusDays(1).atStartOfDay();
        if (repository.countByUploadDateRange(startOfDay, endOfDay) == 0) {
            return Collections.emptyList();
        }
        return findByUploadDay(asOnDate);
    }

    private List<CnaBankBalanceReport> findByUploadDay(LocalDate uploadDay) {
        return repository.findByUploadedOnBetween(
                uploadDay.atStartOfDay(),
                uploadDay.plusDays(1).atStartOfDay());
    }

    public List<CnaBankBalanceReport> getReportBetweenDates(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Please select both From Date and To Date.");
        }
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("From Date cannot be after To Date.");
        }
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay();
        return repository.findByUploadedOnBetween(fromDateTime, toDateTime);
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

        LocalDate uploadDate = LocalDate.now();
        LocalDateTime startOfDay = uploadDate.atStartOfDay();
        LocalDateTime endOfDay = uploadDate.plusDays(1).atStartOfDay();
        if (repository.countByUploadDateRange(startOfDay, endOfDay) > 0) {
            throw new IllegalArgumentException(
                    "Data has already been uploaded for today ("
                            + uploadDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                            + "). Only one upload per day is allowed.");
        }

        List<CnaBankBalanceReport> records = parseExcel(file);
        if (records.isEmpty()) {
            throw new IllegalArgumentException("No valid data rows found in the Excel file.");
        }

        List<CnaBankBalanceReport> saved = repository.saveAll(records);
        repository.flush();
        logger.info("CNA bank balance upload saved {} record(s)", saved.size());
        return saved.size();
    }

    private static class ColumnMapping {
        private int headerRowIndex;
        private int cnaCodeCol = -1;
        private int cnaNameCol = -1;
        private int bankBalanceCol = -1;
        private int reportDateCol = -1;

        private boolean isComplete() {
            return cnaCodeCol >= 0 && cnaNameCol >= 0 && bankBalanceCol >= 0 && reportDateCol >= 0;
        }

        private boolean hasAnyMapping() {
            return cnaCodeCol >= 0 || cnaNameCol >= 0 || bankBalanceCol >= 0 || reportDateCol >= 0;
        }

        private int mappedCount() {
            int count = 0;
            if (cnaCodeCol >= 0) count++;
            if (cnaNameCol >= 0) count++;
            if (bankBalanceCol >= 0) count++;
            if (reportDateCol >= 0) count++;
            return count;
        }
    }

    private static final int MAX_COLUMNS_TO_SCAN = 30;

    private List<CnaBankBalanceReport> parseExcel(MultipartFile file) throws IOException {
        List<CnaBankBalanceReport> records = new ArrayList<>();
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

                String cnaCode = getDataCellValue(sheet, row, mapping.cnaCodeCol, evaluator, dataFormatter);
                String cnaName = getDataCellValue(sheet, row, mapping.cnaNameCol, evaluator, dataFormatter);
                BigDecimal bankBalance = getDataCellValueAsBigDecimal(sheet, row, mapping.bankBalanceCol, evaluator, dataFormatter);
                LocalDate reportDate = getDataCellValueAsDate(sheet, row, mapping.reportDateCol, evaluator, dataFormatter);

                if (cnaCode.isBlank() && cnaName.isBlank()) {
                    continue;
                }

                if (cnaCode.isBlank() || cnaName.isBlank()) {
                    throw new IllegalArgumentException(
                            "Invalid data at row " + (rowIndex + 1) + ". CNA Code and CNA Name are required.");
                }

                if (bankBalance == null) {
                    String balanceRaw = getDataCellValue(sheet, row, mapping.bankBalanceCol, evaluator, dataFormatter);
                    if (isNullOrDash(balanceRaw)) {
                        bankBalance = BigDecimal.ZERO;
                    } else {
                        throw new IllegalArgumentException(
                                "Invalid bank balance at row " + (rowIndex + 1) + ". Value '" + balanceRaw +
                                        "' is not a valid number.");
                    }
                }

                CnaBankBalanceReport record = new CnaBankBalanceReport();
                record.setCnaCode(cnaCode.trim());
                record.setCnaName(cnaName.trim());
                record.setBankBalance(bankBalance);
                record.setReportDate(reportDate);
                record.setUploadedOn(uploadedOn);
                records.add(record);
            }
        }

        logger.info("CNA bank balance parse found {} data row(s)", records.size());
        return records;
    }

    private ColumnMapping findColumnMapping(Sheet sheet, FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        int lastRowToScan = Math.min(sheet.getLastRowNum(), 20);
        ColumnMapping mapping = new ColumnMapping();
        int lastHeaderRowFound = sheet.getFirstRowNum();

        for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= lastRowToScan; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }

            ColumnMapping rowMapping = mapColumnsFromRow(sheet, rowIndex, evaluator, dataFormatter);
            if (rowMapping.hasAnyMapping()) {
                mergeMapping(mapping, rowMapping);
                lastHeaderRowFound = rowIndex;
            }

            if (mapping.isComplete()) {
                mapping.headerRowIndex = lastHeaderRowFound;
                logger.info("CNA Excel columns mapped at row {}: code={}, name={}, balance={}, date={}",
                        lastHeaderRowFound + 1, mapping.cnaCodeCol, mapping.cnaNameCol,
                        mapping.bankBalanceCol, mapping.reportDateCol);
                return mapping;
            }
        }

        throw new IllegalArgumentException(buildHeaderValidationMessage(mapping));
    }

    private void mergeMapping(ColumnMapping target, ColumnMapping source) {
        if (source.cnaCodeCol >= 0) {
            target.cnaCodeCol = source.cnaCodeCol;
        }
        if (source.cnaNameCol >= 0) {
            target.cnaNameCol = source.cnaNameCol;
        }
        if (source.bankBalanceCol >= 0) {
            target.bankBalanceCol = source.bankBalanceCol;
        }
        if (source.reportDateCol >= 0) {
            target.reportDateCol = source.reportDateCol;
        }
    }

    private String buildHeaderValidationMessage(ColumnMapping mapping) {
        StringBuilder found = new StringBuilder();
        if (mapping.cnaCodeCol >= 0) {
            found.append("Unique Code of IAs/CNA");
        }
        if (mapping.cnaNameCol >= 0) {
            appendFoundColumn(found, "Name of IAs/CNA");
        }
        if (mapping.bankBalanceCol >= 0) {
            appendFoundColumn(found, "CNA Bank Balance");
        }
        if (mapping.reportDateCol >= 0) {
            appendFoundColumn(found, "Bank Balance Last Shared/Updated On");
        }

        if (found.length() == 0) {
            return "Invalid Excel format. Could not find any expected header columns. " +
                    "Required: 'Unique Code of IAs/SNA', 'Name of IAs/SNA', 'CNA Bank Balance', 'Bank Balance Last Shared On'.";
        }

        return "Invalid Excel format. Found " + mapping.mappedCount() + " of 4 required columns (" + found + "). " +
                "Please ensure all four header columns are present in the Excel file.";
    }

    private void appendFoundColumn(StringBuilder found, String columnName) {
        if (found.length() > 0) {
            found.append(", ");
        }
        found.append(columnName);
    }

    private ColumnMapping mapColumnsFromRow(Sheet sheet, int rowIndex, FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        ColumnMapping mapping = new ColumnMapping();
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return mapping;
        }

        for (int colIndex = 0; colIndex < MAX_COLUMNS_TO_SCAN; colIndex++) {
            String header = normalizeHeader(getHeaderCellValue(sheet, row, colIndex, evaluator, dataFormatter));
            if (header.isBlank()) {
                continue;
            }
            if (mapping.cnaCodeCol < 0 && matchesCnaCodeHeader(header)) {
                mapping.cnaCodeCol = colIndex;
            } else if (mapping.cnaNameCol < 0 && matchesCnaNameHeader(header)) {
                mapping.cnaNameCol = colIndex;
            } else if (mapping.bankBalanceCol < 0 && matchesBankBalanceHeader(header)) {
                mapping.bankBalanceCol = colIndex;
            } else if (mapping.reportDateCol < 0 && matchesReportDateHeader(header)) {
                mapping.reportDateCol = colIndex;
            }
        }
        return mapping;
    }

    private boolean matchesCnaCodeHeader(String header) {
        return header.contains("unique code of ia")
                || header.contains("unique code of cna")
                || header.contains("unique code of sna");
    }

    private boolean matchesCnaNameHeader(String header) {
        return header.contains("name of ia")
                || header.contains("name of cna")
                || header.contains("name of sna");
    }

    private boolean matchesBankBalanceHeader(String header) {
        return header.contains("cna bank balance")
                || (header.contains("bank balance") && !header.contains("shared")
                && !header.contains("updated") && !header.contains("last"));
    }

    private boolean matchesReportDateHeader(String header) {
        return header.contains("shared on")
                || header.contains("last shared")
                || header.contains("last updated")
                || header.contains("updated on")
                || (header.contains("bank balance") && (header.contains("shared") || header.contains("updated")));
    }

    private boolean isSkippableRow(Sheet sheet, Row row, ColumnMapping mapping,
                                   FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        String cnaCode = getDataCellValue(sheet, row, mapping.cnaCodeCol, evaluator, dataFormatter);
        String cnaName = getDataCellValue(sheet, row, mapping.cnaNameCol, evaluator, dataFormatter);
        String balanceText = getDataCellValue(sheet, row, mapping.bankBalanceCol, evaluator, dataFormatter);
        String dateText = getDataCellValue(sheet, row, mapping.reportDateCol, evaluator, dataFormatter);

        if (cnaCode.isBlank() && cnaName.isBlank() && balanceText.isBlank() && dateText.isBlank()) {
            return true;
        }

        String normalizedCode = normalizeHeader(cnaCode);
        String normalizedName = normalizeHeader(cnaName);

        if (matchesCnaCodeHeader(normalizedCode) || matchesCnaNameHeader(normalizedName)
                || matchesBankBalanceHeader(normalizedCode) || matchesReportDateHeader(normalizedName)) {
            return true;
        }

        return isSerialOrMetaRow(cnaCode, cnaName, balanceText, dateText);
    }

    private boolean isSerialOrMetaRow(String cnaCode, String cnaName, String balanceText, String dateText) {
        if (isSmallInteger(cnaCode) && isSmallInteger(cnaName)) {
            return true;
        }
        if (isSmallInteger(balanceText) && isSmallInteger(dateText)) {
            return true;
        }
        if (cnaCode.isBlank() && cnaName.isBlank()
                && (isSmallInteger(balanceText) || isSmallInteger(dateText))) {
            return true;
        }
        return false;
    }

    private boolean isNullOrDash(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }
        String trimmed = value.trim().toLowerCase();
        return "-".equals(trimmed) || "na".equals(trimmed) || "n/a".equals(trimmed) || "nil".equals(trimmed);
    }

    private boolean isSmallInteger(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            double number = Double.parseDouble(value.replace(",", "").trim());
            return number == Math.floor(number) && number >= 0 && number < 100;
        } catch (NumberFormatException ex) {
            return false;
        }
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

    private String getHeaderCellValue(Sheet sheet, Row row, int colIndex,
                                      FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            return "";
        }
        cell = resolveMergedCell(sheet, cell);
        return formatCellValue(cell, evaluator, dataFormatter);
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
        if (cell != null) {
            return cell;
        }
        return row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
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
        if (rawValue == null) {
            return null;
        }

        String value = rawValue.trim();
        if (value.isBlank()) {
            return null;
        }

        String lowerValue = value.toLowerCase();
        if ("-".equals(value) || "na".equals(lowerValue) || "n/a".equals(lowerValue) || "nil".equals(lowerValue)) {
            return null;
        }

        String numeric = value
                .replace(",", "")
                .replaceAll("[^0-9.\\-]", "")
                .trim();

        if (numeric.isBlank() || "-".equals(numeric) || ".".equals(numeric)) {
            return null;
        }

        try {
            return new BigDecimal(numeric);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private LocalDate getDataCellValueAsDate(Sheet sheet, Row row, int colIndex,
                                             FormulaEvaluator evaluator, DataFormatter dataFormatter) {
        Cell cell = getOrCreateCell(row, colIndex);
        if (cell == null) {
            return null;
        }
        cell = resolveMergedCell(sheet, cell);

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        String dateText = formatCellValue(cell, evaluator, dataFormatter);
        if (dateText.isBlank() || isSmallInteger(dateText)) {
            return null;
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateText, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        if (dateText.contains("/") || dateText.contains("-")) {
            throw new IllegalArgumentException("Unable to parse date: " + dateText);
        }

        return null;
    }
}
