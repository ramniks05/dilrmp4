package in.gov.dilrmp.services.administrativeBoundry;

import in.gov.dilrmp.models.administrativeBoundry.UserLoginHistory;
import in.gov.dilrmp.repositories.administrativeBoundry.UserLoginHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserLoginHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginHistoryService.class);

    @Autowired
    private UserLoginHistoryRepository userLoginHistoryRepository;

    public List<UserLoginHistory> loginHistoryList() {
        logger.info("Fetching all user login history sorted by login time ascending.");
        List<UserLoginHistory> loginHistories = userLoginHistoryRepository.findByOrderByLoginTimeAsc();
        logger.info("Retrieved {} login history records.", loginHistories.size());
        return loginHistories;
    }

    public Optional<UserLoginHistory> findById(Long id) {
        logger.info("Fetching user login history with ID: {}", id);
        Optional<UserLoginHistory> userLoginHistory = userLoginHistoryRepository.findById(id);
        if (userLoginHistory.isPresent()) {
            logger.info("User login history found: {}", userLoginHistory.get());
        } else {
            logger.warn("No user login history found for ID: {}", id);
        }
        return userLoginHistory;
    }

    public void save(UserLoginHistory userLoginHistory) {
        try {
            userLoginHistoryRepository.save(userLoginHistory);
            logger.info("User login history saved successfully: {}", userLoginHistory);
        } catch (Exception e) {
            logger.error("Failed to save user login history: {}", userLoginHistory, e);
        }
    }
}
