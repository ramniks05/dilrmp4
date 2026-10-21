package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.models.profile.StateProfileWordEntry;
import in.gov.dilrmp.models.reportDTO.profile.StateProfileWordInput;
import in.gov.dilrmp.repositories.profile.StateProfileWordEntryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Draft / final storage of the DoLR-entered part of the State Profile, one record per state and date. */
@Service
public class StateProfileWordEntryService {

    private static final String[] NOT_COPIED = {"id", "stateId", "asOnDate", "status"};

    @Autowired
    StateProfileWordEntryRepository repository;

    public Optional<StateProfileWordEntry> find(long stateId, LocalDate asOnDate) {
        return repository.findByStateIdAndAsOnDate(stateId, asOnDate);
    }

    /** The state's most recent profile for any date, used to pre-fill a new date. */
    public Optional<StateProfileWordEntry> latest(long stateId) {
        return repository.findFirstByStateIdOrderByAsOnDateDescUpdatedOnDesc(stateId);
    }

    public StateProfileWordInput toInput(StateProfileWordEntry entry, long stateId, LocalDate asOnDate) {
        StateProfileWordInput in = new StateProfileWordInput();
        if (entry != null) {
            BeanUtils.copyProperties(entry, in, NOT_COPIED);
            in.setReasons(lines(entry.getReasonsText()));
            for (String line : lines(entry.getUlbDetailsText())) {
                String[] parts = (line + "\t\t\t").split("\t", -1);
                in.getUlbNames().add(parts[0]);
                in.getUlbRor().add(parts[1]);
                in.getUlbDraftTimeline().add(parts[2]);
                in.getUlbFinalPublication().add(parts[3]);
            }
        }
        in.setStateId(stateId);
        in.setAsOnDate(asOnDate);
        return in;
    }

    /** @throws IllegalStateException when the profile for this state and date is already final */
    @Transactional
    public StateProfileWordEntry save(StateProfileWordInput in, boolean finalSave, String user) {
        StateProfileWordEntry entry = find(in.getStateId(), in.getAsOnDate()).orElseGet(StateProfileWordEntry::new);
        if (StateProfileWordEntry.FINAL.equals(entry.getStatus())) {
            throw new IllegalStateException("This profile is already final and cannot be changed.");
        }
        BeanUtils.copyProperties(in, entry, NOT_COPIED);
        entry.setStateId(in.getStateId());
        entry.setAsOnDate(in.getAsOnDate());
        entry.setReasonsText(joinLines(in.getReasons()));
        entry.setUlbDetailsText(ulbDetailsText(in));
        LocalDateTime now = LocalDateTime.now();
        entry.setUpdatedBy(user);
        entry.setUpdatedOn(now);
        if (finalSave) {
            entry.setStatus(StateProfileWordEntry.FINAL);
            entry.setFinalSavedBy(user);
            entry.setFinalSavedOn(now);
        } else {
            entry.setStatus(StateProfileWordEntry.DRAFT);
        }
        return repository.save(entry);
    }

    private static String ulbDetailsText(StateProfileWordInput in) {
        List<String> rows = new ArrayList<>();
        for (Map.Entry<String, String[]> e : in.ulbDetailsByName().entrySet()) {
            String[] d = e.getValue();
            rows.add(clean(e.getKey()) + "\t" + clean(d[0]) + "\t" + clean(d[1]) + "\t" + clean(d[2]));
        }
        return String.join("\n", rows);
    }

    private static String joinLines(List<String> values) {
        List<String> cleaned = new ArrayList<>();
        if (values != null) {
            for (String v : values) {
                cleaned.add(clean(v));
            }
        }
        return String.join("\n", cleaned);
    }

    /** Single-line values only: tabs and line breaks are the storage separators. */
    private static String clean(String value) {
        return value == null ? "" : value.replaceAll("[\\t\\r\\n]+", " ").trim();
    }

    private static List<String> lines(String text) {
        List<String> list = new ArrayList<>();
        if (text != null && !text.isEmpty()) {
            for (String line : text.split("\n", -1)) {
                list.add(line);
            }
        }
        return list;
    }
}
