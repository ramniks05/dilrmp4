package in.gov.dilrmp.configs;


import in.gov.dilrmp.controllers.Login.HomePageController;
import in.gov.dilrmp.utils.SecurityUtils;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // Extract the CAPTCHA value from the request
        String captcha = obtainCaptcha(request);



        // Validate the CAPTCHA (implement this logic)
        if (!validateCaptcha(captcha, request)) {
            // CAPTCHA validation failed
            try {
                unsuccessfulAuthentication(request, response, new CustomBadCredentialsException("Invalid CAPTCHA"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        // Continue with the authentication process
        return super.attemptAuthentication(request, response);
    }

    private String obtainCaptcha(HttpServletRequest request) {
        return request.getParameter("captcha");
    }
    private boolean validateCaptcha(String captcha, HttpServletRequest request) {
        // Retrieve the stored CAPTCHA text from the session
        String storedCaptchaText1 = (String) request.getSession().getAttribute("captchaText");
        String token = HomePageController.getOneTimeToken();
        String combinedValue = SecurityUtils.hashWithSHA256(storedCaptchaText1)+ token;
//        String combinedValue = storedCaptchaText1;

        String storedCaptchaText = SecurityUtils.hashWithSHA256(combinedValue);

        // Check if both CAPTCHA and user input are not null
        if (storedCaptchaText != null && captcha != null) {
            // Perform a case-sensitive comparison
            return storedCaptchaText.equals(captcha);
        }

        return false; // If either CAPTCHA or user input is null, return false
    }

   
    




}
