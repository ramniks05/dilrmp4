package in.gov.dilrmp.services.administrativeBoundry;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.repositories.administrativeBoundry.StateRepositry;


@Service
public class StateService {

    private static final Logger logger = LoggerFactory.getLogger(StateService.class);

    @Autowired
    private StateRepositry stateRepositry;

    public State findStateByUserID(Long userID) {
        logger.info("Finding state for userID: {}", userID);
        State state = stateRepositry.findStateByUserID(userID);
        if (state != null) {
            logger.info("State found for userID: {}", userID);
        } else {
            logger.warn("No state found for userID: {}", userID);
        }
        return state;
    }

    public List<State> findAllOrderByStateName() {
        return stateRepositry.findAllOrderByStateName();
    }

    public Optional<State> findStateByLgdCode(Integer lgdCode) {
        return stateRepositry.findStateByLgdCode(lgdCode);
    }

    public State findStateByAdcUserID(Long userID) {
        logger.info("Finding state for userID: {}", userID);
        State state = stateRepositry.findStateByAdcUserID(userID);
        if (state != null) {
            logger.info("State found for userID: {}", userID);
        } else {
            logger.warn("No state found for userID: {}", userID);
        }
        return state;
    }
}
