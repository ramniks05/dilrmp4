package in.gov.dilrmp.services.DataEntryForm;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.dataEntryModel.DolrMisDataEntry;
import in.gov.dilrmp.models.dataEntryModel.StateRegistrationSystem;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DolrMisDataEntryRepository;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.IgrMISDataEntryRepository;
import in.gov.dilrmp.repositories.administrativeBoundry.StateRepositry;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DolrMisDataEntryService {

    private static final Logger logger = LoggerFactory.getLogger(DolrMisDataEntryService.class);

    @Autowired
    DolrMisDataEntryRepository dolrMisDataEntryRepository;
    @Autowired
    IgrMISDataEntryRepository igrMISDataEntryRepository;
    @Autowired
    StateRepositry stateRepositry;
    @Autowired
    LoogedInUserUtility loogedInUserUtility;

    public List<State> getStates() {
        return stateRepositry.findAllOrderByStateName();
    }

    public DolrMisDataEntry getByStateId(Long stateId) {
        return dolrMisDataEntryRepository.findByState_Id(stateId).orElseGet(() -> {
            DolrMisDataEntry blank = new DolrMisDataEntry();
            blank.setLegacyDilrmpSanctionedPages(0);
            blank.setSrosDilrmpSanctioned(0);
            return blank;
        });
    }

    @Transactional
    public String save(Long stateId, Integer legacyDilrmpSanctionedPages, Integer srosDilrmpSanctioned) {
        if (stateId == null) {
            throw new IllegalArgumentException("State/UT is required.");
        }
        State state = stateRepositry.findById(stateId)
                .orElseThrow(() -> new IllegalArgumentException("State not found for ID: " + stateId));

        int legacySanctioned = legacyDilrmpSanctionedPages == null ? 0 : Math.max(0, legacyDilrmpSanctionedPages);
        int sroSanctioned = srosDilrmpSanctioned == null ? 0 : Math.max(0, srosDilrmpSanctioned);

        Optional<DolrMisDataEntry> existing = dolrMisDataEntryRepository.findByState_Id(stateId);
        DolrMisDataEntry entry = existing.orElseGet(DolrMisDataEntry::new);
        if (entry.getId() == null) {
            entry.setState(state);
            entry.setCreatedOnDate(LocalDate.now());
            entry.setVersion(0);
        } else {
            entry.setVersion(entry.getVersion() == null ? 1 : entry.getVersion() + 1);
        }
        entry.setLegacyDilrmpSanctionedPages(legacySanctioned);
        entry.setSrosDilrmpSanctioned(sroSanctioned);
        entry.setUpdateOnDate(LocalDate.now());
        try {
            entry.setUser(loogedInUserUtility.getLoggedinUser().getUser());
        } catch (Exception e) {
            logger.warn("Could not set logged-in user on DolrMisDataEntry: {}", e.getMessage());
        }
        dolrMisDataEntryRepository.save(entry);

        // Keep IGR table C in sync so existing SRO modernization aggregates stay consistent
        syncSroSanctionToRegistrationSystem(state, sroSanctioned);

        return "success";
    }

    private void syncSroSanctionToRegistrationSystem(State state, int sroSanctioned) {
        Optional<StateRegistrationSystem> opt = igrMISDataEntryRepository.findByStateId(state.getId());
        if (opt.isEmpty()) {
            StateRegistrationSystem byPath = igrMISDataEntryRepository.findByState_Id(state.getId());
            if (byPath != null) {
                opt = Optional.of(byPath);
            }
        }
        StateRegistrationSystem reg;
        if (opt.isPresent()) {
            reg = opt.get();
        } else {
            reg = new StateRegistrationSystem();
            reg.setState(state);
            reg.setNumberOfSROsInState(0);
            reg.setNumberOfSROsUsingOnlineRegistration(0);
            reg.setSrosModernisedStateFunds(0);
            reg.setSrosModernisedDilrmpFunds(0);
            reg.setDataSourceType(1);
            reg.setVersion(0);
            reg.setCreatedOnDate(LocalDate.now());
        }
        reg.setSrosDilrmpSanctioned(sroSanctioned);
        reg.setUpdateOnDate(LocalDate.now());
        if (reg.getId() != null) {
            reg.setVersion(reg.getVersion() == null ? 1 : reg.getVersion() + 1);
        }
        igrMISDataEntryRepository.save(reg);
    }
}
