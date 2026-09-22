package in.gov.dilrmp.services.administrativeBoundry;
import in.gov.dilrmp.models.administrativeBoundry.UserLoginHistory;
import in.gov.dilrmp.repositories.administrativeBoundry.UserLoginHistoryRepository;
import in.gov.dilrmp.repositories.user.UserRepositry;
import in.gov.dilrmp.utils.IPAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationSuccessEventListener.class);

    @Autowired
    private UserLoginHistoryRepository userLoginHistoryRepository;

    @Autowired
    private UserRepositry userRepositry;

    @Autowired
    private HttpServletRequest request;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        HttpSession session = request.getSession();
        String username = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();
        String ipAddress = IPAddressUtil.getClientIP(request);

        logger.info("Authentication success for user: {} from IP address: {}", username, ipAddress);

        UserLoginHistory loginHistory = new UserLoginHistory();
        loginHistory.setUser(userRepositry.getUserByUsername(username));
        loginHistory.setLoginTime(LocalDateTime.now());
        loginHistory.setIpAddress(ipAddress);
        loginHistory.setLoginStatus(true);

        try {
            UserLoginHistory userLoginHistory = userLoginHistoryRepository.save(loginHistory);
            Long userLoginHistoryId = userLoginHistory.getId();
            session.setAttribute("userLoginHistoryId", userLoginHistoryId);

            logger.info("User login history saved with ID: {}", userLoginHistoryId);
        } catch (Exception e) {
            logger.error("Failed to save user login history for user: {}", username, e);
        }
    }
}
