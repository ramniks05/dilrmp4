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
import java.util.List;
import java.util.Map;

@Service
public class UnspentBalanceService {

    public static final String UNIT_LABEL = "Rs. in crore";
    public static final String REPORT_TITLE = "Component-wise unspent balance under DILRMP & Survey-Resurvey";
    public static final String LETTER_REFERENCE = "DoLR letter File No. 16014/01/2025-LRD (e-3014910) dated 28.09.2026";
    public static final String COMPONENT_NOTE = "Consolidated components of DILRMP 2.0 & 3.0. Item 4 is the sum of 4.1 to 4.3.";

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999999999999.99");

    public static final class Component {
        private final String code;
        private final String groupCode;
        private final String serialNo;
        private final String name;
        private final String parentCode;
        private final boolean parent;

        Component(String code, String groupCode, String serialNo, String name, String parentCode, boolean parent) {
            this.code = code;
            this.groupCode = groupCode;
            this.serialNo = serialNo;
            this.name = name;
            this.parentCode = parentCode;
            this.parent = parent;
        }

        public String getCode() { return code; }
        public String getGroupCode() { return groupCode; }
        public String getSerialNo() { return serialNo; }
        public String getName() { return name; }
        public String getParentCode() { return parentCode; }
        public boolean isParent() { return parent; }
    }

    public static final Map<String, String> GROUPS = Map.of(
            "A", "Land Records",
            "B", "Registration",
            "C", "Programme Management & Capacity Building"
    );

    public static final List<Component> COMPONENTS = List.of(
            item("01", "A", "1", "Modern Record Room (Tehsil/Taluka/Circle/Block Level)"),
            item("02", "A", "2", "Survey/Re-Survey and Innovative Initiatives"),
            item("03", "A", "3", "Digitization, Georeferencing and Unique Land Parcel Identification Number (ULPIN)"),
            new Component("04", "A", "4", "Computerization of Land Records", null, true),
            subItem("04.1", "4.1", "Data Entry / Re-entry", "04"),
            subItem("04.2", "4.2", "Digitization of Cadastral Maps / FMBs / Tippans", "04"),
            subItem("04.3", "4.3", "State Level Data Centre", "04"),
            item("05", "A", "5", "Consent-based Integration of Aadhaar, Seeding of Mobile Number and Address in Land Records Database"),
            item("06", "A", "6", "Computerization of Revenue Courts and Integration with Land Records"),
            item("07", "A", "7", "Core GIS / LandStack / Software Applications"),
            item("08", "A", "8", "NAKSHA \u2013 Urban Land Records / NAKSHA Pilot"),
            item("09", "B", "9", "Computerization of Registration Process"),
            item("10", "B", "10", "Registration Repository"),
            item("11", "B", "11", "Modernization of Sub-Registrar Offices (SROs)"),
            item("12", "C", "12", "DILRMP Cell"),
            item("13", "C", "13", "Programming Management Unit (PMU)"),
            item("14", "C", "14", "Evaluation Studies, IEC and Training")
    );

    @Value("${unspent-balance.as-on-date:2026-09-28}")
    private String asOnDateValue;

    @Value("${unspent-balance.last-date:2026-09-29}")
    private String lastDateValue;

    @Autowired
    private UnspentBalanceRepository repository;

    @Autowired
    private StateService stateService;

    private static Component item(String code, String groupCode, String serialNo, String name) {
        return new Component(code, groupCode, serialNo, name, null, false);
    }

    private static Component subItem(String code, String serialNo, String name, String parentCode) {
        return new Component(code, "A", serialNo, name, parentCode, false);
    }

    public static String groupLabel(String groupCode) {
        return groupCode + ". " + GROUPS.get(groupCode);
    }

    public LocalDate getAsOnDate() {
        return LocalDate.parse(asOnDateValue.trim());
    }

    public LocalDate getLastDate() {
        return lastDateValue == null || lastDateValue.isBlank() ? null : LocalDate.parse(lastDateValue.trim());
    }

    public String getAsOnDateText() {
        return getAsOnDate().format(DISPLAY_DATE);
    }

    public String getLastDateText() {
        LocalDate lastDate = getLastDate();
        return lastDate == null ? "" : lastDate.format(DISPLAY_DATE);
    }

    public String getAmountHeader() {
        return "Unspent Balance as on " + getAsOnDateText() + " (" + UNIT_LABEL + ")";
    }

    public boolean isEntryOpen() {
        LocalDate lastDate = getLastDate();
        return lastDate == null || !LocalDate.now().isAfter(lastDate);
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

    public LocalDateTime getLastUpdatedOn(Long stateId) {
        LocalDateTime latest = null;
        for (UnspentBalance saved : findSaved(stateId)) {
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
        if (!isEntryOpen()) {
            throw new IllegalArgumentException("Data entry closed on " + getLastDateText() + ".");
        }
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
            if (component.isParent()) {
                continue;
            }
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
        BigDecimal groupTotal = BigDecimal.ZERO;
        String currentGroup = null;
        for (Component component : COMPONENTS) {
            if (!component.getGroupCode().equals(currentGroup)) {
                if (currentGroup != null) {
                    rows.add(subtotalRow(currentGroup, groupTotal));
                }
                currentGroup = component.getGroupCode();
                groupTotal = BigDecimal.ZERO;
                rows.add(new UnspentBalanceRow(UnspentBalanceRow.TYPE_GROUP, null, currentGroup, null,
                        currentGroup, GROUPS.get(currentGroup), null));
            }
            if (component.isParent()) {
                rows.add(new UnspentBalanceRow(UnspentBalanceRow.TYPE_PARENT, component.getCode(), currentGroup, null,
                        component.getSerialNo(), component.getName(), sumChildren(component.getCode(), amounts)));
                continue;
            }
            BigDecimal amount = amounts.get(component.getCode());
            if (amount != null) {
                total = total.add(amount);
                groupTotal = groupTotal.add(amount);
            }
            String type = component.getParentCode() != null ? UnspentBalanceRow.TYPE_SUB_ITEM : UnspentBalanceRow.TYPE_ITEM;
            rows.add(new UnspentBalanceRow(type, component.getCode(), currentGroup, component.getParentCode(),
                    component.getSerialNo(), component.getName(), amount));
        }
        if (currentGroup != null) {
            rows.add(subtotalRow(currentGroup, groupTotal));
        }
        rows.add(new UnspentBalanceRow(UnspentBalanceRow.TYPE_TOTAL, "TOTAL", null, null, "",
                "Grand Total (A + B + C)", total));
        return rows;
    }

    private UnspentBalanceRow subtotalRow(String groupCode, BigDecimal amount) {
        return new UnspentBalanceRow(UnspentBalanceRow.TYPE_SUBTOTAL, "SUBTOTAL-" + groupCode, groupCode, null, "",
                "Sub-total " + groupLabel(groupCode), amount);
    }

    private BigDecimal sumChildren(String parentCode, Map<String, BigDecimal> amounts) {
        BigDecimal sum = null;
        for (Component component : COMPONENTS) {
            if (parentCode.equals(component.getParentCode())) {
                BigDecimal amount = amounts.get(component.getCode());
                if (amount != null) {
                    sum = (sum == null ? BigDecimal.ZERO : sum).add(amount);
                }
            }
        }
        return sum;
    }
}
