package in.gov.dilrmp.configs;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sends a short code to the login page, which shows the matching message. Exception text is never put
 * in the URL, so the page cannot be made to display arbitrary text.
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Pattern MINUTES_LEFT = Pattern.compile("(\\d+)\\s+minute");

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String path = request.getContextPath();
        String message = exception.getMessage() == null ? "" : exception.getMessage();
        String query;

        // The texts matched here are thrown by CustomAuthenticationProvider.
        if (exception instanceof SessionAuthenticationException || message.startsWith("You are already logged in")) {
            query = "error=active";
        } else if (message.startsWith("Account is blocked")) {
            Matcher m = MINUTES_LEFT.matcher(message);
            query = "error=blocked" + (m.find() ? "&minutes=" + m.group(1) : "");
        } else if (message.startsWith("Account locked")) {
            query = "error=locked";
        } else if (message.startsWith("First failed attempt")) {
            query = "error=attempt1";
        } else if (message.startsWith("Second failed attempt")) {
            query = "error=attempt2";
        } else {
            query = "error=invalid";
        }

        response.sendRedirect(path + "/loginPage?" + query);
    }
}
