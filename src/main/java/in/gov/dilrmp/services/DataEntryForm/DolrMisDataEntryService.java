package in.gov.dilrmp.services.DataEntryForm;

import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.dataEntryModel.DolrDistrictMisDataEntry;
import in.gov.dilrmp.models.dataEntryModel.DolrMisDataEntry;
import in.gov.dilrmp.models.dataEntryModel.StateRegistrationSystem;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DolrDistrictMisDataEntryRepository;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DolrMisDataEntryRepository;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.IgrMISDataEntryRepository;
import in.gov.dilrmp.repositories.administrativeBoundry.DistrictRepositry;
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
    DolrDistrictMisDataEntryRepository dolrDistrictMisDataEntryRepository;
    @Autowired
    IgrMISDataEntryRepository igrMISDataEntryRepository;
    @Autowired
    StateRepositry stateRepositry;
    @Autowired
    DistrictRepositry districtRepositry;
    @Autowired
    LoogedInUserUtility loogedInUserUtility;

    public List<State> getStates() {
        return stateRepositry.findAllOrderByStateName();
    }

    public List<District> getDistricts(Long stateId) {
        if (stateId == null) {
            return List.of();
        }
        return districtRepositry.findAllDistrictByStateID(stateId);
    }

    public boolean districtBelongsToState(Long stateId, Long districtId) {
        if (stateId == null || districtId == null) {
            return false;
        }
        return districtRepositry.findById(districtId)
                .map(district -> district.getState() != null && stateId.equals(district.getState().getId()))
                .orElse(false);
    }

    public DolrDistrictMisDataEntry getByDistrictId(Long districtId) {
        if (districtId == null) {
            return blankDistrictEntry();
        }
        return dolrDistrictMisDataEntryRepository.findByDistrict_Id(districtId).orElseGet(this::blankDistrictEntry);
    }

    public DolrMisDataEntry getByStateId(Long stateId) {
        return dolrMisDataEntryRepository.findByState_Id(stateId).orElseGet(() -> {
            DolrMisDataEntry blank = new DolrMisDataEntry();
            blank.setLegacyDilrmpSanctionedPages(0);
            blank.setSrosDilrmpSanctioned(0);
            blank.setRevenueLegacyDilrmpSanctionedPages(0);
            return blank;
        });
    }

    @Transactional
    public String saveSroSanction(Long stateId, Integer srosDilrmpSanctioned) {
        if (stateId == null) {
            throw new IllegalArgumentException("State/UT is required.");
        }
        State state = stateRepositry.findById(stateId)
                .orElseThrow(() -> new IllegalArgumentException("State not found for ID: " + stateId));
        int sroSanctioned = nonNegative(srosDilrmpSanctioned);

        Optional<DolrMisDataEntry> existing = dolrMisDataEntryRepository.findByState_Id(stateId);
        DolrMisDataEntry entry = existing.orElseGet(DolrMisDataEntry::new);
        if (entry.getId() == null) {
            entry.setState(state);
            entry.setLegacyDilrmpSanctionedPages(0);
            entry.setRevenueLegacyDilrmpSanctionedPages(0);
            entry.setCreatedOnDate(LocalDate.now());
            entry.setVersion(0);
        } else {
            entry.setVersion(entry.getVersion() == null ? 1 : entry.getVersion() + 1);
        }
        entry.setSrosDilrmpSanctioned(sroSanctioned);
        entry.setUpdateOnDate(LocalDate.now());
        try {
            entry.setUser(loogedInUserUtility.getLoggedinUser().getUser());
        } catch (Exception e) {
            logger.warn("Could not set logged-in user on DolrMisDataEntry: {}", e.getMessage());
        }
        dolrMisDataEntryRepository.save(entry);
        syncSroSanctionToRegistrationSystem(state, sroSanctioned);
        return "success";
    }

    @Transactional
    public String saveDistrictSanctions(Long stateId, Long districtId, Integer legacyDilrmpSanctionedPages,
                                        Integer revenueLegacyDilrmpSanctionedPages) {
        if (stateId == null) {
            throw new IllegalArgumentException("State/UT is required.");
        }
        if (districtId == null) {
            throw new IllegalArgumentException("District is required.");
        }
        District district = districtRepositry.findById(districtId)
                .orElseThrow(() -> new IllegalArgumentException("District not found."));
        if (district.getState() == null || !stateId.equals(district.getState().getId())) {
            throw new IllegalArgumentException("Selected district does not belong to the selected State/UT.");
        }

        Optional<DolrDistrictMisDataEntry> existing = dolrDistrictMisDataEntryRepository.findByDistrict_Id(districtId);
        DolrDistrictMisDataEntry entry = existing.orElseGet(DolrDistrictMisDataEntry::new);
        if (entry.getId() == null) {
            entry.setDistrict(district);
            entry.setCreatedOnDate(LocalDate.now());
            entry.setVersion(0);
        } else {
            entry.setVersion(entry.getVersion() == null ? 1 : entry.getVersion() + 1);
        }
        entry.setState(district.getState());
        entry.setLegacyDilrmpSanctionedPages(nonNegative(legacyDilrmpSanctionedPages));
        entry.setRevenueLegacyDilrmpSanctionedPages(nonNegative(revenueLegacyDilrmpSanctionedPages));
        entry.setUpdateOnDate(LocalDate.now());
        try {
            entry.setUser(loogedInUserUtility.getLoggedinUser().getUser());
        } catch (Exception e) {
            logger.warn("Could not set logged-in user on DolrDistrictMisDataEntry: {}", e.getMessage());
        }
        dolrDistrictMisDataEntryRepository.save(entry);
        return "success";
    }

    private DolrDistrictMisDataEntry blankDistrictEntry() {
        DolrDistrictMisDataEntry blank = new DolrDistrictMisDataEntry();
        blank.setLegacyDilrmpSanctionedPages(0);
        blank.setRevenueLegacyDilrmpSanctionedPages(0);
        return blank;
    }

    private static int nonNegative(Integer value) {
        return value == null ? 0 : Math.max(0, value);
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
