package in.gov.dilrmp.configs;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "credentials";
        String path = request.getContextPath();

        if (exception instanceof UsernameNotFoundException) {
            errorMessage = "credentials";
        } else if (exception instanceof SessionAuthenticationException) {
            errorMessage = "concurrent";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = exception.getMessage();
        }

        response.sendRedirect(path + "/loginPage?error=" + errorMessage);
    }
}