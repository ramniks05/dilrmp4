package in.gov.dilrmp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    /**
     * District login: the district name with the first letter uppercase and the
     * rest lowercase, then the LGD code. "ABC" and 123 become Abc123.
     */
    public static String generateDistrictUserName(String districtName, Integer lgdCode) {
        if (districtName == null || lgdCode == null) {
            return null;
        }
        String letters = districtName.replaceAll("[^A-Za-z]", "");
        if (letters.length() < 3) {
            return null;
        }
        String shaped = letters.substring(0, 1).toUpperCase(Locale.ENGLISH)
                + letters.substring(1).toLowerCase(Locale.ENGLISH);
        return shaped + lgdCode;
    }

    /**
     * Default district password: the generated username plus {@code @dolr}.
     * "ABC" and 123 become Abc123@dolr.
     */
    public static String generateDistrictPassword(String userName) {
        if (userName == null || userName.isBlank()) {
            return null;
        }
        return userName + "@dolr";
    }

    /** Fixed default for DOLR and ADMIN accounts. */
    public static final String ADMIN_DEFAULT_PASSWORD = "Dilrmp4@dol";

    /**
     * Default password used when an administrator resets a login.
     * ROLE_STATE: first three letters of the username (first uppercase) + LGD code + {@code @dolr}.
     * BIH and 10 become Bih10@dolr.
     * ROLE_DISTRICT: username with the first character uppercase and the rest lowercase, then {@code @dolr}.
     * AZA123 becomes Aza123@dolr.
     * ROLE_DOLR and ROLE_ADMIN: {@code Dilrmp4@dol}.
     * ROLE_IGR: username in lowercase, then {@code @dolr}. TELIGR becomes teligr@dolr.
     */
    public static String generateResetPassword(String userName, String roleId, Integer lgdCode) {
        if (userName == null || userName.isBlank() || roleId == null || roleId.isBlank()) {
            return null;
        }
        String name = userName.trim();
        String role = roleId.trim();

        if (Role.USER_ROLE_STATE.equals(role)) {
            if (lgdCode == null || name.length() < 3) {
                return null;
            }
            String prefix = name.substring(0, 1).toUpperCase(Locale.ENGLISH)
                    + name.substring(1, 3).toLowerCase(Locale.ENGLISH);
            return prefix + lgdCode + "@dolr";
        }
        if (Role.USER_ROLE_DISTRICT.equals(role)) {
            String shaped = name.substring(0, 1).toUpperCase(Locale.ENGLISH)
                    + name.substring(1).toLowerCase(Locale.ENGLISH);
            return shaped + "@dolr";
        }
        if (Role.USER_ROLE_DOLR.equals(role) || Role.USER_ROLE_ADMIN.equals(role)) {
            return ADMIN_DEFAULT_PASSWORD;
        }
        if (Role.USER_ROLE_IGR.equals(role)) {
            return name.toLowerCase(Locale.ENGLISH) + "@dolr";
        }
        return null;
    }

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
