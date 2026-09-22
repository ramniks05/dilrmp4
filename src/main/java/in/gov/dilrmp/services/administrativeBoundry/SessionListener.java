package in.gov.dilrmp.services.administrativeBoundry;

import in.gov.dilrmp.repositories.administrativeBoundry.UserLoginHistoryRepository;
import in.gov.dilrmp.repositories.user.UserRepositry;import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;


@Configuration
public class SessionListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionListener.class);

    @Autowired
    private UserRepositry userRepositry;

    @Autowired
    private UserLoginHistoryRepository userLoginHistoryRepository;

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        SecurityContext securityContext = (SecurityContext) event.getSession()
                .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        if (securityContext != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null) {
                String username = authentication.getName();
                logger.info("Session destroyed for user: {}", username);

                // Update the user status to inactive
                try {
                    userRepositry.updateUserActive(username, false);
                    logger.info("User status updated to inactive for user: {}", username);
                } catch (Exception e) {
                    logger.error("Failed to update user status for user: {}", username, e);
                }
            }
        } else {
            logger.warn("SecurityContext is null for the destroyed session");
        }
    }
}