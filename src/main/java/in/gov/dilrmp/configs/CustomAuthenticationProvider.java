package in.gov.dilrmp.configs;

import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.repositories.user.UserRepositry;
import in.gov.dilrmp.services.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Component
public class CustomAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private UserDetailsService userDetailsService;
    @Autowired
    UserRepositry userRepositry;
    @Autowired
    UserService userService;
    private final HttpSession session;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, HttpSession session) {
        this.userDetailsService = userDetailsService;
        this.session = session;
    }


    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        User user = userRepositry.getUserByUsername(userDetails.getUsername());

        if (user != null && user.isBlock()) {
            LocalDateTime now = LocalDateTime.now();
            LocalTime blockUntilTime = user.getBlockTime().toLocalTime();
            if (now.toLocalTime().isBefore(blockUntilTime.plusMinutes(3))) {
                long minutesLeft = Duration.between(now.toLocalTime(), blockUntilTime.plusMinutes(3)).toMinutes();
                throw new BadCredentialsException("Account is blocked. Try again in " + minutesLeft + " minutes.");
            } else {
                user.setBlock(false);
                user.setBlockTime(null);
                userService.saveUserforBlock(user);
                session.setAttribute("failedAttempts", 0);
            }
        }

        // Fetch the session attributes for failed attempts
        String sessionUsername = (String) session.getAttribute("username");
        Integer failedAttempts = (Integer) session.getAttribute("failedAttempts");

        // Initialize session attributes if they are not present
        if (sessionUsername == null) {
            session.setAttribute("username", userDetails.getUsername());
            sessionUsername = userDetails.getUsername();
        }
        if (failedAttempts == null) {
            session.setAttribute("failedAttempts", 0);
            failedAttempts = 0;
        }

        // Check if the session username matches the current username
        if (!sessionUsername.equals(userDetails.getUsername())) {
            session.setAttribute("username", userDetails.getUsername());
            session.setAttribute("failedAttempts", 0);
            failedAttempts = 0;
        }

        if (!authentication.getCredentials().toString().equals(userDetails.getPassword())) {
            failedAttempts++;
            session.setAttribute("failedAttempts", failedAttempts);

            if (failedAttempts >= 3) {
                if(user!=null) {
                    user.setId(user.getId());
                    user.setBlock(true);
                    LocalDateTime now = LocalDateTime.now();
                    LocalTime blockUntilTime = now.plusMinutes(0).toLocalTime();
                    Time blockTime = Time.valueOf(blockUntilTime);
                    user.setBlockTime(blockTime);
                    userService.saveUserforBlock(user);
                }
                throw new BadCredentialsException("Account locked due to multiple failed login attempts");
            }
            switch (failedAttempts) {
                case 1:
                    throw new BadCredentialsException("First failed attempt, 2 more before account lock.");
                case 2:
                    throw new BadCredentialsException("Second failed attempt, 1 more before account lock.");
                default:
                    throw new BadCredentialsException("Invalid password.");
            }
        } else {
            session.setAttribute("failedAttempts", 0);
            Optional<User> existingSession = userRepositry.findByUserNameAndLoginStatus(userDetails.getUsername(), true);
            if (existingSession.isPresent()) {
                throw new BadCredentialsException("You are already logged in another browser");
            }
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        return userDetailsService.loadUserByUsername(username);
    }
}
