package in.gov.dilrmp.services.administrativeBoundry;

import in.gov.dilrmp.models.administrativeBoundry.UserLoginHistory;
import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.repositories.administrativeBoundry.UserLoginHistoryRepository;
import in.gov.dilrmp.repositories.user.UserRepositry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// LogoutEventListener.java
@Component
public class LogoutEventListener implements ApplicationListener<LogoutSuccessEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LogoutEventListener.class);

    @Autowired
    private UserLoginHistoryRepository userLoginHistoryRepository;

    @Autowired
    private UserRepositry userRepositry;

    @Override
    public void onApplicationEvent(LogoutSuccessEvent event) {
        String username = event.getAuthentication().getName();
        logger.info("Logout successful for user: {}", username);

        UserLoginHistory logoutHistory = findLatestLoginHistory(username);
        if (logoutHistory != null) {
            logoutHistory.setLogoutTime(LocalDateTime.now());
            logoutHistory.setLoginStatus(false);

            try {
                userLoginHistoryRepository.save(logoutHistory);
                logger.info("Updated logout history for user: {} with ID: {}", username, logoutHistory.getId());
            } catch (Exception e) {
                logger.error("Failed to update logout history for user: {}", username, e);
            }
        } else {
            logger.warn("No login history found for user: {}", username);
        }
    }

    private UserLoginHistory findLatestLoginHistory(String username) {
        User user = userRepositry.getUserByUsername(username);
        return userLoginHistoryRepository.findTopByUserOrderByLoginTimeDesc(user);
    }
}