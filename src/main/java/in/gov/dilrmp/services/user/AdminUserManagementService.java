package in.gov.dilrmp.services.user;

import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.igr.Igr;
import in.gov.dilrmp.models.user.AdminManagedUserRow;
import in.gov.dilrmp.models.user.Role;
import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.repositories.administrativeBoundry.DistrictRepositry;
import in.gov.dilrmp.repositories.administrativeBoundry.StateRepositry;
import in.gov.dilrmp.repositories.igr.IgrRepositry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminUserManagementService {

    public static final Set<String> ROLES = Set.of(
            Role.USER_ROLE_STATE,
            Role.USER_ROLE_DISTRICT,
            Role.USER_ROLE_IGR);

    private final StateRepositry stateRepositry;
    private final DistrictRepositry districtRepositry;
    private final IgrRepositry igrRepositry;

    public AdminUserManagementService(StateRepositry stateRepositry,
                                      DistrictRepositry districtRepositry,
                                      IgrRepositry igrRepositry) {
        this.stateRepositry = stateRepositry;
        this.districtRepositry = districtRepositry;
        this.igrRepositry = igrRepositry;
    }

    public String normalizeRole(String role) {
        if (role != null && ROLES.contains(role)) {
            return role;
        }
        return Role.USER_ROLE_STATE;
    }

    public boolean requiresState(String role) {
        return Role.USER_ROLE_DISTRICT.equals(role) || Role.USER_ROLE_IGR.equals(role);
    }

    public List<State> states() {
        return stateRepositry.findAllOrderByStateName();
    }

    public String stateName(Long stateId) {
        if (stateId == null) {
            return "";
        }
        String name = stateRepositry.findStateNameById(stateId);
        return name == null ? "" : name;
    }

    @Transactional(readOnly = true)
    public List<AdminManagedUserRow> users(String role, Long stateId) {
        String selectedRole = normalizeRole(role);
        if (Role.USER_ROLE_STATE.equals(selectedRole)) {
            return stateRepositry.findStatesWithUserByRole(selectedRole).stream()
                    .map(this::fromState)
                    .collect(Collectors.toList());
        }
        if (stateId == null) {
            return List.of();
        }
        if (Role.USER_ROLE_DISTRICT.equals(selectedRole)) {
            return districtRepositry.findDistrictUsersByStateAndRole(selectedRole, stateId).stream()
                    .map(this::fromDistrict)
                    .collect(Collectors.toList());
        }
        return igrRepositry.findIgrUsersByStateAndRole(selectedRole, stateId).stream()
                .map(this::fromIgr)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserPdfPart> allParts() {
        List<UserPdfPart> parts = new ArrayList<UserPdfPart>();
        parts.add(new UserPdfPart("STATE", Role.USER_ROLE_STATE, "", users(Role.USER_ROLE_STATE, null)));

        Map<String, List<AdminManagedUserRow>> districts = new LinkedHashMap<String, List<AdminManagedUserRow>>();
        for (District district : districtRepositry.findDistrictUsersByRole(Role.USER_ROLE_DISTRICT)) {
            String name = district.getState() == null ? "Unknown" : district.getState().getName();
            List<AdminManagedUserRow> rows = districts.get(name);
            if (rows == null) {
                rows = new ArrayList<AdminManagedUserRow>();
                districts.put(name, rows);
            }
            rows.add(fromDistrict(district));
        }
        for (Map.Entry<String, List<AdminManagedUserRow>> entry : districts.entrySet()) {
            parts.add(new UserPdfPart("DISTRICT", Role.USER_ROLE_DISTRICT, entry.getKey(), entry.getValue()));
        }

        Map<String, List<AdminManagedUserRow>> igrs = new LinkedHashMap<String, List<AdminManagedUserRow>>();
        for (Igr igr : igrRepositry.findIgrUsersByRole(Role.USER_ROLE_IGR)) {
            String name = igr.getState() == null ? "Unknown" : igr.getState().getName();
            List<AdminManagedUserRow> rows = igrs.get(name);
            if (rows == null) {
                rows = new ArrayList<AdminManagedUserRow>();
                igrs.put(name, rows);
            }
            rows.add(fromIgr(igr));
        }
        for (Map.Entry<String, List<AdminManagedUserRow>> entry : igrs.entrySet()) {
            parts.add(new UserPdfPart("IGR", Role.USER_ROLE_IGR, entry.getKey(), entry.getValue()));
        }
        return parts;
    }

    public static final class UserPdfPart {
        private final String folder;
        private final String role;
        private final String stateName;
        private final List<AdminManagedUserRow> rows;

        public UserPdfPart(String folder, String role, String stateName, List<AdminManagedUserRow> rows) {
            this.folder = folder;
            this.role = role;
            this.stateName = stateName;
            this.rows = rows;
        }

        public String getFolder() {
            return folder;
        }

        public String getRole() {
            return role;
        }

        public String getStateName() {
            return stateName;
        }

        public List<AdminManagedUserRow> getRows() {
            return rows;
        }
    }

    private AdminManagedUserRow fromState(State state) {
        AdminManagedUserRow row = base(state.getMuser(), state.getName(), "-", state.getLgdCode());
        return row;
    }

    private AdminManagedUserRow fromDistrict(District district) {
        String stateName = district.getState() == null ? "-" : district.getState().getName();
        return base(district.getMuser(), stateName, district.getName(), district.getLgdCode());
    }

    private AdminManagedUserRow fromIgr(Igr igr) {
        String stateName = igr.getState() == null ? "-" : igr.getState().getName();
        Integer lgdCode = igr.getState() == null ? null : igr.getState().getLgdCode();
        return base(igr.getUser(), stateName, "-", lgdCode);
    }

    private AdminManagedUserRow base(User user, String stateName, String districtName, Integer lgdCode) {
        AdminManagedUserRow row = new AdminManagedUserRow();
        row.setUsername(user == null || user.getUserName() == null ? "-" : user.getUserName());
        row.setStateName(blank(stateName));
        row.setDistrictName(blank(districtName));
        row.setLgdCode(lgdCode == null ? "-" : String.valueOf(lgdCode));
        row.setRole(user == null || user.getRoleId() == null ? "-" : user.getRoleId());
        row.setAccountStatus(user != null && user.isActive() ? "Active" : "Inactive");
        return row;
    }

    private String blank(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
