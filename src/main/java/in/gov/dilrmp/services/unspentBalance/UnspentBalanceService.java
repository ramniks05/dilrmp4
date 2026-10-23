package in.gov.dilrmp.services.unspentBalance;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.unspentBalance.UnspentBalance;
import in.gov.dilrmp.models.unspentBalance.UnspentBalanceRow;
import in.gov.dilrmp.repositories.unspentBalance.UnspentBalanceRepository;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UnspentBalanceService {

    public static final String UNIT_LABEL = "Rs. in crore";
    public static final String REPORT_TITLE = "Component-wise unspent balance under DILRMP";
    public static final String COMPONENT_NOTE = "Components marked (DILRMP 2.0) belong to DILRMP 2.0.";

    private static final String DILRMP_20_MARK = " (DILRMP 2.0)";
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999999999999.99");

    public static final class Component {
        private final String code;
        private final String serialNo;
        private final String name;
        private final String parentCode;

        Component(String code, String serialNo, String name, String parentCode) {
            this.code = code;
            this.serialNo = serialNo;
            this.name = name;
            this.parentCode = parentCode;
        }

        public String getCode() { return code; }
        public String getSerialNo() { return serialNo; }
        public String getName() { return name; }
        public String getParentCode() { return parentCode; }
    }

    public static final List<Component> COMPONENTS = List.of(
            item("01", "1", "Modern Record Room (Tehsil/Taluka/Circle/Block Level)", true),
            item("02", "2", "Survey/Re-survey and Innovative Initiatives", true),
            item("04", "3", "Computerization of Land Records", true),
            subItem("04.1", "3(a)", "Data Entry/Re-entry", "04", true),
            subItem("04.2", "3(b)", "Digitization of Cadastral Maps/FMBs/Tippans", "04", false),
            subItem("04.3", "3(c)", "State Level Data Centre", "04", true),
            subItem("07", "3(d)", "Core GIS/Software Applications", "04", true),
            item("09", "4", "Computerization of Registration Process", true),
            item("12", "5", "DILRMP Cell", true),
            item("13", "6", "PMU", false),
            item("14", "7", "Evaluation Studies, IEC and Training", false),
            item("05", "8", "Consent-based Integration of Aadhaar Number with the Land Record Database", false),
            item("06", "9", "Computerization of Revenue Courts and their Integration with Land Records", false),
            item("08", "10", "NAKSHA", false),
            item("16", "11", "Digitization of Legacy Revenue Records", false),
            item("17", "12", "Digitization of Legacy Registration Deeds/Documents", false),
            item("18", "13", "Land Stack Software Development", false),
            item("15", "14", "Accrued Interest", false)
    );

    @Value("${unspent-balance.as-on-date:2026-09-28}")
    private String asOnDateValue;

    @Autowired
    private UnspentBalanceRepository repository;

    @Autowired
    private StateService stateService;

    private static Component item(String code, String serialNo, String name, boolean dilrmp20) {
        return new Component(code, serialNo, dilrmp20 ? name + DILRMP_20_MARK : name, null);
    }

    private static Component subItem(String code, String serialNo, String name, String parentCode, boolean dilrmp20) {
        return new Component(code, serialNo, dilrmp20 ? name + DILRMP_20_MARK : name, parentCode);
    }

    private static boolean isCurrentComponent(String code) {
        return COMPONENTS.stream().anyMatch(component -> component.getCode().equals(code));
    }

    public LocalDate getAsOnDate() {
        return LocalDate.parse(asOnDateValue.trim());
    }

    public String getAsOnDateText() {
        return getAsOnDate().format(DISPLAY_DATE);
    }

    public String getAmountHeader() {
        return "Unspent Balance as on " + getAsOnDateText() + " (" + UNIT_LABEL + ")";
    }

    public List<State> getStates() {
        return stateService.findAllOrderByStateName();
    }

    public State resolveState(Long stateId) {
        if (stateId == null) {
            throw new IllegalArgumentException("Please select State / UT.");
        }
        return stateService.findAllOrderByStateName().stream()
                .filter(state -> state.getId().equals(stateId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid State / UT selected."));
    }

    public List<UnspentBalanceRow> getRows(Long stateId) {
        Map<String, BigDecimal> amounts = new HashMap<>();
        for (UnspentBalance saved : findSaved(stateId)) {
            amounts.put(saved.getComponentCode(), saved.getUnspentBalance());
        }
        return buildRows(amounts);
    }

    public Map<String, List<UnspentBalanceRow>> getRowsForAllStates() {
        Map<Long, Map<String, BigDecimal>> amountsByState = new HashMap<>();
        for (UnspentBalance saved : repository.findByAsOnDate(getAsOnDate())) {
            amountsByState.computeIfAbsent(saved.getStateId(), id -> new HashMap<>())
                    .put(saved.getComponentCode(), saved.getUnspentBalance());
        }
        Map<String, List<UnspentBalanceRow>> rowsByState = new LinkedHashMap<>();
        for (State state : getStates()) {
            rowsByState.put(state.getName(), buildRows(amountsByState.getOrDefault(state.getId(), new HashMap<>())));
        }
        return rowsByState;
    }

    public LocalDateTime getLastUpdatedOn(Long stateId) {
        LocalDateTime latest = null;
        for (UnspentBalance saved : findSaved(stateId)) {
            if (!isCurrentComponent(saved.getComponentCode())) {
                continue;
            }
            if (saved.getUpdatedOn() != null && (latest == null || saved.getUpdatedOn().isAfter(latest))) {
                latest = saved.getUpdatedOn();
            }
        }
        return latest;
    }

    private List<UnspentBalance> findSaved(Long stateId) {
        return repository.findByStateIdAndAsOnDateOrderByDisplayOrderAsc(stateId, getAsOnDate());
    }

    @Transactional
    public void save(Long stateId, Map<String, String> amountsByCode, String updatedBy) {
        State state = resolveState(stateId);
        LocalDate asOnDate = getAsOnDate();

        Map<String, UnspentBalance> existing = new HashMap<>();
        for (UnspentBalance saved : findSaved(stateId)) {
            existing.put(saved.getComponentCode(), saved);
        }

        LocalDateTime now = LocalDateTime.now();
        List<UnspentBalance> toSave = new ArrayList<>();
        for (int i = 0; i < COMPONENTS.size(); i++) {
            Component component = COMPONENTS.get(i);
            BigDecimal amount = parseAmount(component, amountsByCode != null ? amountsByCode.get(component.getCode()) : null);

            UnspentBalance entity = existing.remove(component.getCode());
            if (entity == null) {
                entity = new UnspentBalance();
                entity.setStateId(state.getId());
                entity.setAsOnDate(asOnDate);
                entity.setComponentCode(component.getCode());
            }
            entity.setStateName(state.getName());
            entity.setDisplayOrder(i + 1);
            entity.setSerialNo(component.getSerialNo());
            entity.setComponentName(component.getName());
            entity.setUnspentBalance(amount);
            entity.setUpdatedBy(updatedBy);
            entity.setUpdatedOn(now);
            toSave.add(entity);
        }
        if (!existing.isEmpty()) {
            repository.deleteAll(existing.values());
            repository.flush();
        }
        repository.saveAll(toSave);
    }

    private BigDecimal parseAmount(Component component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(value.trim()).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount for " + label(component) + ".");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Unspent balance cannot be negative for " + label(component) + ".");
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException("Amount is too large for " + label(component) + ".");
        }
        return amount;
    }

    private String label(Component component) {
        return "[" + component.getSerialNo() + "] " + component.getName();
    }

    private List<UnspentBalanceRow> buildRows(Map<String, BigDecimal> amounts) {
        List<UnspentBalanceRow> rows = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Component component : COMPONENTS) {
            BigDecimal amount = amounts.get(component.getCode());
            if (amount != null) {
                total = total.add(amount);
            }
            String type = component.getParentCode() != null ? UnspentBalanceRow.TYPE_SUB_ITEM : UnspentBalanceRow.TYPE_ITEM;
            rows.add(new UnspentBalanceRow(type, component.getCode(), null, component.getParentCode(),
                    component.getSerialNo(), component.getName(), amount));
        }
        rows.add(new UnspentBalanceRow(UnspentBalanceRow.TYPE_TOTAL, "TOTAL", null, null, "", "Total", total));
        return rows;
    }
}
