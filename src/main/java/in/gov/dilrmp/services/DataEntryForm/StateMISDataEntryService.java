package in.gov.dilrmp.services.DataEntryForm;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DistrictMISDataEntryRepository;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.StateMISDataEntryRepository;
import in.gov.dilrmp.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.dataEntryModel.StateMISDataEntry;
import in.gov.dilrmp.repositories.administrativeBoundry.StateRepositry;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class StateMISDataEntryService {

    @Autowired
    StateMISDataEntryRepository stateMISDataEntryRepository;

    @Autowired
    DistrictMISDataEntryRepository districtMISDataEntryRepository;
    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    StateRepositry stateRepositry;


    @Transactional
    public String save(StateMISDataEntry stateMISDataEntry) {
        Logger logger = LoggerFactory.getLogger(StateMISDataEntryService.class);

        try {
            Long stateId = loogedInUserUtility.getLoggedinUser().getBoundry().getId();
            Optional<State> optionalState = stateRepositry.findById(stateId);

            if (!optionalState.isPresent()) {
                throw new IllegalStateException("State not found for ID: " + stateId);
            }

            State state = optionalState.get();
            stateMISDataEntry.setState(state);
            stateMISDataEntry.setUser(loogedInUserUtility.getLoggedinUser().getUser());

            // Check if a record already exists for the same state and user
            Optional<StateMISDataEntry> existingEntry = stateMISDataEntryRepository
                    .findByStateIdAndUserId(state.getId(), loogedInUserUtility.getLoggedinUser().getUser().getId());

            if (existingEntry.isPresent() && stateMISDataEntry.getId() == null) {
                // Duplicate found, throw an exception or handle the update logic
                throw new IllegalStateException("An entry already exists for this state and user.");
            }

            // Handle new or existing records
            if (stateMISDataEntry.getId() == null) {
                stateMISDataEntry.setDataSourceType(1);
                stateMISDataEntry.setVersion(0);
                stateMISDataEntry.setCreatedOnDate(LocalDate.now());
                stateMISDataEntry.setUpdateOnDate(LocalDate.now());
            } else {
                if (stateMISDataEntry.getCreatedOnDate() == null) {
                    stateMISDataEntry.setCreatedOnDate(LocalDate.now());
                }
                if (stateMISDataEntry.getDataSourceType() == null) {
                    stateMISDataEntry.setDataSourceType(1);
                }
                stateMISDataEntry.setVersion(stateMISDataEntry.getVersion() + 1);
                stateMISDataEntry.setUpdateOnDate(LocalDate.now());
            }

            // Save the entity
            stateMISDataEntryRepository.save(stateMISDataEntry);
            logger.info("StateMISDataEntry with ID {} saved successfully.", stateMISDataEntry.getId());

        } catch (DataAccessException e) {
            logger.error("Data access error while saving StateMISDataEntry.", e);
            return "error-message";
        } catch (TransactionSystemException e) {
            logger.error("Transaction system error while saving StateMISDataEntry.", e);
            return "error-message";
        } catch (IllegalStateException e) {
            logger.error("Illegal state encountered while saving StateMISDataEntry: {}", e.getMessage());
            return "error-message";
        } catch (Exception e) {
            logger.error("Unexpected error while saving StateMISDataEntry.", e);
            return "error-message";
        }

        return "success";
    }



    public StateMISDataEntry getStateMISDataEntryByStateId(Long stateID) {
        StateMISDataEntry stateMISDataEntry=stateMISDataEntryRepository.findByState_Id(stateID);
        if(stateMISDataEntry != null && stateMISDataEntry.getCreatedOnDate()!=null){
            stateMISDataEntry.setCreated_date(DateUtils.formatLocalDateToString(stateMISDataEntry.getCreatedOnDate()));
        }
      return stateMISDataEntry;
    }



    
}
