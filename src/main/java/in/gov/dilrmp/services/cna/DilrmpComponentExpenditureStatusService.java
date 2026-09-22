package in.gov.dilrmp.services.cna;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.cna.ComponentRowInput;
import in.gov.dilrmp.models.cna.DilrmpComponentExpenditureStatus;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DilrmpComponentExpenditureStatusService {

    public static final String DEFAULT_PHASE = "Phase 3.0";
    public static final String DEFAULT_FINANCIAL_YEAR = "2025-26";
    public static final String DEFAULT_STATE_NAME = "Assam";

    public static final List<String> DILRMP_PHASES = List.of("Phase 2.0", "Phase 3.0");

    public static final List<String> FINANCIAL_YEARS = List.of(
            "2019-20", "2020-21", "2021-22", "2022-23", "2023-24", "2024-25", "2025-26", "2026-27"
    );

    public static final List<String> COMPONENT_NAMES = List.of(
            "Digitization, Georefencing and assigning Unique Land Parcel Identification Number",
            "Modern Record Room (Tehsil)",
            "Consent-based integration of Aadhaar number, seeding of Mobile number and Address in Land Record database",
            "Computerization of Revenue Courts and their integration with Land Record",
            "Core GIS, Landstack Software Development",
            "NAKSHA Pilot – Urban Land Record",
            "Computerization of Registration Process",
            "Registration Repository",
            "Modernization of Sub Registrar Offices (SROs)",
            "Evaluation Studies, IEC and Training",
            "Programming Management Unit (PMU)"
    );

    @Autowired
    private DilrmpComponentExpenditureStatusRepository repository;

    @Autowired
    private StateService stateService;

    public List<State> getStates() {
        return stateService.findAllOrderByStateName();
    }

    public Optional<State> findDefaultState() {
        return stateService.findAllOrderByStateName().stream()
                .filter(state -> DEFAULT_STATE_NAME.equalsIgnoreCase(state.getName()))
                .findFirst();
    }

    public List<Map<String, Object>> getComponentRows(String dilrmpPhase, Long stateId, String financialYear) {
        State state = resolveState(stateId);
        List<DilrmpComponentExpenditureStatus> savedRows = repository
                .findByDilrmpPhaseAndStateIdAndFinancialYearOrderBySerialNoAsc(
                        dilrmpPhase, state.getId(), financialYear);

        Map<Integer, DilrmpComponentExpenditureStatus> savedBySerial = new LinkedHashMap<>();
        for (DilrmpComponentExpenditureStatus row : savedRows) {
            savedBySerial.put(row.getSerialNo(), row);
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = 0; i < COMPONENT_NAMES.size(); i++) {
            int serialNo = i + 1;
            DilrmpComponentExpenditureStatus saved = savedBySerial.get(serialNo);
            rows.add(toRowMap(serialNo, COMPONENT_NAMES.get(i), saved));
        }
        return rows;
    }

    @Transactional
    public void saveComponentRows(String dilrmpPhase, Long stateId, String financialYear,
                                  List<ComponentRowInput> inputs) {
        State state = resolveState(stateId);
        repository.deleteByDilrmpPhaseAndStateIdAndFinancialYear(dilrmpPhase, state.getId(), financialYear);

        LocalDateTime now = LocalDateTime.now();
        List<DilrmpComponentExpenditureStatus> entities = new ArrayList<>();
        for (int i = 0; i < COMPONENT_NAMES.size(); i++) {
            int serialNo = i + 1;
            ComponentRowInput input = findInput(inputs, serialNo);

            DilrmpComponentExpenditureStatus entity = new DilrmpComponentExpenditureStatus();
            entity.setDilrmpPhase(dilrmpPhase);
            entity.setStateId(state.getId());
            entity.setStateName(state.getName());
            entity.setFinancialYear(financialYear);
            entity.setSerialNo(serialNo);
            entity.setComponentName(COMPONENT_NAMES.get(i));
            entity.setSanctionIssued(parseAmount(input != null ? input.getSanctionIssued() : null));
            entity.setSanctionCancelled(parseAmount(input != null ? input.getSanctionCancelled() : null));
            entity.setFundReleased(parseAmount(input != null ? input.getFundReleased() : null));
            entity.setFundReleasedDate(parseDate(input != null ? input.getFundReleasedDate() : null));
            entity.setFundBalance(parseAmount(input != null ? input.getFundBalance() : null));
            entity.setUpdatedOn(now);
            entities.add(entity);
        }
        repository.saveAll(entities);
    }

    private ComponentRowInput findInput(List<ComponentRowInput> inputs, int serialNo) {
        if (inputs == null) {
            return null;
        }
        return inputs.stream()
                .filter(input -> input.getSerialNo() != null && input.getSerialNo() == serialNo)
                .findFirst()
                .orElse(null);
    }

    private State resolveState(Long stateId) {
        if (stateId == null) {
            throw new IllegalArgumentException("Please select State / UT.");
        }
        return stateService.findAllOrderByStateName().stream()
                .filter(state -> state.getId().equals(stateId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid State / UT selected."));
    }

    private Map<String, Object> toRowMap(int serialNo, String componentName, DilrmpComponentExpenditureStatus saved) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("serialNo", serialNo);
        row.put("componentName", componentName);
        row.put("sanctionIssued", formatAmount(saved != null ? saved.getSanctionIssued() : null));
        row.put("sanctionCancelled", formatAmount(saved != null ? saved.getSanctionCancelled() : null));
        row.put("revisedSanction", formatAmount(computeRevisedSanction(saved)));
        row.put("fundReleased", formatAmount(saved != null ? saved.getFundReleased() : null));
        row.put("fundReleasedDate", saved != null && saved.getFundReleasedDate() != null
                ? saved.getFundReleasedDate().toString() : "");
        row.put("fundBalance", formatAmount(saved != null ? saved.getFundBalance() : null));
        return row;
    }

    private BigDecimal computeRevisedSanction(DilrmpComponentExpenditureStatus saved) {
        if (saved == null) {
            return null;
        }
        BigDecimal issued = safeAmount(saved.getSanctionIssued());
        BigDecimal cancelled = safeAmount(saved.getSanctionCancelled());
        return issued.subtract(cancelled);
    }

    private BigDecimal parseAmount(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new BigDecimal(value.trim()).setScale(2, RoundingMode.HALF_UP);
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value.trim());
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String formatAmount(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

}
