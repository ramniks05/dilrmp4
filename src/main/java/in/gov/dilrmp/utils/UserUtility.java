package in.gov.dilrmp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import in.gov.dilrmp.models.administrativeBoundry.AdministrativeBoundry;
import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.models.user.Role;

public class UserUtility {

    static Function<AdministrativeBoundry, String> generateUsername = aBoundry -> {
        if (aBoundry.getBoundryType().equals(State.ADMINSTRATATIVE_BOUNDRY_TYPE_STATE))
            return aBoundry.getName().substring(0, 3);
        if (aBoundry.getBoundryType().equals(District.ADMINSTRATATIVE_BOUNDRY_TYPE_DISTRICT))
            return aBoundry.getName().substring(0, 4).concat(aBoundry.getLgdCode().toString());
        return null;
        
    };

    public static String generateDefaultPasword() {
       /* CharSequence defaultPassword = "nlrmp123";
        BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
        return bc.encode(defaultPassword);*/
        return SecurityUtils.hashWithSHA256("nlrmp123");
    }

    static BiConsumer<AdministrativeBoundry, User> userRoleSet = (aBoundry, user) -> {
        if (aBoundry.getBoundryType().equals(State.ADMINSTRATATIVE_BOUNDRY_TYPE_STATE))
            user.setRoleId(Role.USER_ROLE_STATE);
        if (aBoundry.getBoundryType().equals(District.ADMINSTRATATIVE_BOUNDRY_TYPE_DISTRICT))
            user.setRoleId(Role.USER_ROLE_DISTRICT);
    };

    public static User createUser(AdministrativeBoundry aBoundry) {
        User user = new User();
        user.setUserName(generateUsername.apply(aBoundry));
        userRoleSet.accept(aBoundry, user);
        user.setPassword(generateDefaultPasword());
        user.setActive(true);
        return user;
    }

    public static List<String> getRoleList() {
        List<String> roleList = new ArrayList<String>();
        roleList.add(Role.USER_ROLE_DOLR);
        roleList.add(Role.USER_ROLE_IGR);
        roleList.add(Role.USER_ROLE_STATE);
        roleList.add(Role.USER_ROLE_DISTRICT);
        return roleList;
    }
    
    
}
