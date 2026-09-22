package in.gov.dilrmp.services.DataEntryForm;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.dataEntryModel.StateRegistrationSystem;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.IgrMISDataEntryRepository;
import in.gov.dilrmp.repositories.administrativeBoundry.StateRepositry;
import in.gov.dilrmp.utils.DateUtils;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import java.time.LocalDate;
import java.util.Optional;


@Service
public class IgrMISDataEntryService {

    @Autowired
    IgrMISDataEntryRepository igrMISDataEntryRepository;
    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    StateRepositry stateRepositry;


    @Transactional
    public String save(StateRegistrationSystem stateRegistrationSystem) {
        Logger logger = LoggerFactory.getLogger(StateMISDataEntryService.class);

        try {
            Long stateId = null;
            // Check if the user is an IGR user or a State user
            if (loogedInUserUtility.getLoggedinUser().getBoundry() != null) {
                // State login
                stateId = loogedInUserUtility.getLoggedinUser().getBoundry().getId();
            } else if (loogedInUserUtility.getLoggedinUser().getIgr() != null) {
                // IGR login
                stateId = loogedInUserUtility.getLoggedinUser().getIgr().getState().getId();
            }

            // If no stateId is found, throw an exception
            if (stateId == null) {
                throw new IllegalStateException("No state ID found for the logged-in user.");
            }

            Long finalStateId = stateId;
            State state = stateRepositry.findById(stateId)
                    .orElseThrow(() -> new IllegalStateException("State not found for ID: " + finalStateId));

            // Check if a record already exists for this state
            Optional<StateRegistrationSystem> existingRecord = igrMISDataEntryRepository.findByStateId(stateId);
            if (existingRecord.isPresent() && stateRegistrationSystem.getId() == null) {
                throw new IllegalStateException("Duplicate entry: StateRegistrationSystem already exists for state ID: " + stateId);
            }

            stateRegistrationSystem.setState(state);
            stateRegistrationSystem.setUser(loogedInUserUtility.getLoggedinUser().getUser());

            // Handling new and existing stateRegistrationSystem records
            if (stateRegistrationSystem.getId() == null) {
                stateRegistrationSystem.setDataSourceType(1);
                stateRegistrationSystem.setVersion(0);
                stateRegistrationSystem.setCreatedOnDate(LocalDate.now());
                stateRegistrationSystem.setUpdateOnDate(LocalDate.now());
            } else {
                if (stateRegistrationSystem.getCreatedOnDate() == null) {
                    stateRegistrationSystem.setCreatedOnDate(LocalDate.now());
                }
                if (stateRegistrationSystem.getDataSourceType() == null) {
                    stateRegistrationSystem.setDataSourceType(1);
                }
                stateRegistrationSystem.setVersion(stateRegistrationSystem.getVersion() + 1);
                stateRegistrationSystem.setUpdateOnDate(LocalDate.now());
            }

            // Handle date parsing for legacy records
            if (stateRegistrationSystem.getLegacyRecordsAvailableFromYearSRODate() != null) {
                stateRegistrationSystem.setLegacyRecordsAvailableFromYearSRO(
                        DateUtils.parseDate(stateRegistrationSystem.getLegacyRecordsAvailableFromYearSRODate()));
            }
            if (stateRegistrationSystem.getLegacyRecordsAvailableFromYearPublicDate() != null) {
                stateRegistrationSystem.setLegacyRecordsAvailableFromYearPublic(
                        DateUtils.parseDate(stateRegistrationSystem.getLegacyRecordsAvailableFromYearPublicDate()));
            }

            // Save the entity
            igrMISDataEntryRepository.save(stateRegistrationSystem);
            logger.info("StateRegistrationSystem with ID {} saved successfully.", stateRegistrationSystem.getId());

        } catch (DataAccessException e) {
            logger.error("Data access error while saving StateRegistrationSystem.", e);
            return "error-message";
        } catch (TransactionSystemException e) {
            logger.error("Transaction system error while saving StateRegistrationSystem.", e);
            return "error-message";
        } catch (IllegalStateException e) {
            logger.error("Illegal state encountered while saving StateRegistrationSystem: {}", e.getMessage());
            return "error-message";
        } catch (Exception e) {
            logger.error("Unexpected error while saving StateRegistrationSystem.", e);
            return "error-message";
        }
        return "success";
    }

    public StateRegistrationSystem getStateRegistrationSystemByStateId(Long stateID) {
        StateRegistrationSystem stateRegistrationSystem = igrMISDataEntryRepository.findByState_Id(stateID);

        // If no record is found, create a new instance to avoid NullPointerException
        if (stateRegistrationSystem == null) {
            stateRegistrationSystem = new StateRegistrationSystem();
        }

        // Format legacy records dates if they are not null
        if (stateRegistrationSystem.getLegacyRecordsAvailableFromYearSRO() != null) {
            stateRegistrationSystem.setLegacyRecordsAvailableFromYearSRODate(
                    DateUtils.formatDate(stateRegistrationSystem.getLegacyRecordsAvailableFromYearSRO())
            );
        }

        if (stateRegistrationSystem.getLegacyRecordsAvailableFromYearPublic() != null) {
            stateRegistrationSystem.setLegacyRecordsAvailableFromYearPublicDate(
                    DateUtils.formatDate(stateRegistrationSystem.getLegacyRecordsAvailableFromYearPublic())
            );
        }

        return stateRegistrationSystem;
    }
}
