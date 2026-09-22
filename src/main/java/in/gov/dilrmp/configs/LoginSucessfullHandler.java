package in.gov.dilrmp.configs;

import java.io.IOException;


import in.gov.dilrmp.repositories.user.UserRepositry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import in.gov.dilrmp.models.user.Role;
import in.gov.dilrmp.services.user.UserService;

@Component
public class LoginSucessfullHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private UserRepositry userRepositry;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserService userDetails = (UserService) authentication.getPrincipal();

        session.setAttribute("user", userDetails.getUser().getUserName());
        session.setAttribute("userid",userDetails.getUser().getId());
        String redirectURL = request.getContextPath();
        /*userRepositry.updateUserActive(userDetails.getUser().getUserName(),true);

        LocalDateTime lastPasswordChangeDate = userDetails.getUser().getLastPasswordChangeDate();
        LocalDateTime currentDate = LocalDateTime.now();
        long daysSincePasswordChange = ChronoUnit.DAYS.between(lastPasswordChangeDate, currentDate);
        if (daysSincePasswordChange > 90 ) {
               redirectURL = "daysSincePasswordChange";

        }
        else{*/
        if (userDetails.getRole().equals(Role.USER_ROLE_STATE)  ) {
            redirectURL = "state";
        }
        else if (userDetails.getRole().equals(Role.USER_ROLE_ULB)) {
            redirectURL = "ulb";
            // check for commit4
        }
        else if (userDetails.getRole().equals(Role.USER_ROLE_STATEULB)) {
            redirectURL = "ulb/naksha-home";
            // check for commit4
        }

        else if (userDetails.getRole().equals(Role.USER_ROLE_GD)) {
            redirectURL = "ulb/naksha-mis-venderdata/form";
            // check for commit4
        }
        else if (userDetails.getRole().equals(Role.USER_ROLE_DISTRICT)) {
            redirectURL = "district";
            // check for commit4
        } else if (userDetails.getRole().equals(Role.USER_ROLE_ADC)) {
            redirectURL = "adc";
            // check for commit4
        }
        else if (userDetails.getRole().equals(Role.USER_ROLE_DOLR)) {
            redirectURL = "dolr";
        }else if (userDetails.getRole().equals(Role.USER_ROLE_ADMIN)) {
            redirectURL = "dolr";
        }
        else if (userDetails.getRole().equals(Role.USER_ROLE_IGR)) {
            redirectURL = "igr";
        }
        else if (userDetails.getRole().equals(Role.USER_ROLE_SGO)) {
            redirectURL = "sgo/monitor";

        }
       else {
            redirectURL = "/success";
        }
        //}

        session.setAttribute("redirectURL", redirectURL);
        response.sendRedirect(redirectURL);

    }
}


