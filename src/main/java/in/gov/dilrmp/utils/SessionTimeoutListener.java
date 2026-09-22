package in.gov.dilrmp.utils;
import in.gov.dilrmp.repositories.user.UserRepositry;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
public class SessionTimeoutListener implements HttpSessionListener{

    @Autowired
    UserRepositry userRepositry;



    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // Get the username from the session if it's stored there
        String username = (String) se.getSession().getAttribute("user");

        if (username != null) {
            userRepositry.updateUserActive(username, false);
        }
    }
}
