package in.gov.dilrmp.services.administrativeBoundry;

import in.gov.dilrmp.models.administrativeBoundry.UserActivity;
import in.gov.dilrmp.repositories.administrativeBoundry.UserActivityRepositry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActivityService {

    private static final Logger logger = LoggerFactory.getLogger(UserActivityService.class);

    @Autowired
    private UserActivityRepositry userActivityRepositry;

    public String save(UserActivity userActivity) {
        String message = "fail";
        try {
            userActivity.setActiveTime(LocalDateTime.now());
            userActivityRepositry.save(userActivity);
            message = "success";
            logger.info("User activity saved successfully: {}", userActivity);
        } catch (Exception e) {
            logger.error("Failed to save user activity: {}", userActivity, e);
        }
        return message;
    }

    public List<UserActivity> findAllLoginHistoryByID(Long id) {
        logger.info("Fetching all login history for user ID: {}", id);
        List<UserActivity> userActivities = userActivityRepositry.findAllLoginHistoryByID(id);
        if (userActivities.isEmpty()) {
            logger.warn("No login history found for user ID: {}", id);
        } else {
            logger.info("Found {} login activities for user ID: {}", userActivities.size(), id);
        }
        return userActivities;
    }
}