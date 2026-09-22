package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.igr.Igr;
import in.gov.dilrmp.repositories.igr.IgrRepositry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import in.gov.dilrmp.models.administrativeBoundry.AdministrativeBoundry;
import in.gov.dilrmp.models.user.Role;
import in.gov.dilrmp.repositories.user.UserRepositry;
import in.gov.dilrmp.services.administrativeBoundry.DistrictService;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import in.gov.dilrmp.services.user.UserService;

@Component
public class LoogedInUserUtility {
    @Autowired
    public StateService sService;
    @Autowired
    public DistrictService dService;
    @Autowired
    public UserRepositry uRepositry;
    @Autowired
    public IgrRepositry iRepositry;

    public UserService getLoggedinUser() {
        AbstractAuthenticationToken auth = (AbstractAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        UserService details = (UserService) auth.getPrincipal();
        AdministrativeBoundry aBoundry = null;
        Igr igr = null;
        if (details.getRole().equals(Role.USER_ROLE_STATE)) {
            aBoundry = sService.findStateByUserID(details.getUserID());
        } else if (details.getRole().equals(Role.USER_ROLE_DISTRICT)) {
            aBoundry = dService.findBoundryByUserID(details.getUserID());
        } else if (details.getRole().equals(Role.USER_ROLE_ADC)) {
            aBoundry = dService.findBoundryByAdcUserID(details.getUserID());
        } else if (details.getRole().equals(Role.USER_ROLE_IGR)) {
            igr = iRepositry.findIgrByUserId(details.getUserID());
        } else {
            aBoundry = null;
        }
        details.setBoundry(aBoundry);
        details.setIgr(igr);
        details.setUser(uRepositry.getUserById(details.getUserID()));
        return details;
    }

    public boolean hasRole(String ROLE_USER) {
        boolean hasUserRole = false;
        if (getLoggedinUser() != null)  {
            hasUserRole = getLoggedinUser().getRole().equals(ROLE_USER);
        }
        return hasUserRole;
    }
}
    


