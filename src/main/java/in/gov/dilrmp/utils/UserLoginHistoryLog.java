package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.administrativeBoundry.UserLoginHistory;
import in.gov.dilrmp.services.administrativeBoundry.UserActivityService;
import in.gov.dilrmp.services.administrativeBoundry.UserLoginHistoryService;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class UserLoginHistoryLog {
    @Autowired
    UserLoginHistoryService loginHistoryService;
    @Autowired
    HttpServletRequest request;

    @Autowired
    UserActivityService UserActivity;
    public  UserLoginHistory userLoginHistory(){
        HttpSession session = request.getSession();
        Long userLoginHistoryId = (Long) session.getAttribute("userLoginHistoryId");
        UserLoginHistory loginHistory = loginHistoryService.findById(userLoginHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userLoginHistoryId));
        return loginHistory;
    }
}
