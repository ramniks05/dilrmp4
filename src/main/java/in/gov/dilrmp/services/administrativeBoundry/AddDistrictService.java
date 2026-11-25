package in.gov.dilrmp.services.administrativeBoundry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.user.Role;
import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.repositories.administrativeBoundry.DistrictRepositry;
import in.gov.dilrmp.repositories.user.UserRepositry;
import in.gov.dilrmp.utils.SecurityUtils;
import in.gov.dilrmp.utils.UserUtility;

@Service
public class AddDistrictService {

    private static final Logger logger = LoggerFactory.getLogger(AddDistrictService.class);

    static final LocalDateTime DEFAULT_LAST_PASSWORD_CHANGE = LocalDateTime.of(2026, 4, 10, 0, 0);

    /** 3 to 100 characters. Letters and single spaces only. */
    private static final Pattern DISTRICT_NAME =
            Pattern.compile("^(?=.{3,100}$)[A-Za-z]+(?: [A-Za-z]+)*$");

    @Autowired
    private DistrictRepositry districtRepositry;

    @Autowired
    private UserRepositry userRepositry;

    @Autowired
    private StateService stateService;

    @Autowired
    private EntityManager entityManager;

    /**
     * Empty when the code is free. Otherwise a message naming the district
     * that already uses it.
     */
    public Optional<String> lgdConflict(Integer lgdCode) {
        return lgdConflict(lgdCode, null);
    }

    /**
     * Empty when the code is free. {@code excludeDistrictId} is the district
     * being edited, so its own LGD code is not treated as a duplicate.
     */
    public Optional<String> lgdConflict(Integer lgdCode, Long excludeDistrictId) {
        if (lgdCode == null || lgdCode <= 0) {
            throw new AddDistrictException("Enter a valid numeric LGD Code.");
        }
        List<District> matches = districtRepositry.findByLgdCode(lgdCode);
        if (matches == null) {
            return Optional.empty();
        }
        for (District district : matches) {
            if (district == null || Objects.equals(district.getId(), excludeDistrictId)) {
                continue;
            }
            String name = district.getName() == null ? "" : district.getName().trim();
            if (name.isEmpty()) {
                name = "another district";
            }
            return Optional.of("LGD Code " + lgdCode + " is already used by district "
                    + name + ". Enter a different LGD Code.");
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> districtsForState(Long stateId) {
        if (stateId == null) {
            throw new AddDistrictException("Please select a State.");
        }
        List<Map<String, Object>> options = new ArrayList<>();
        for (Object[] row : districtRepositry.findOptionsByStateId(stateId)) {
            Map<String, Object> option = new LinkedHashMap<>();
            option.put("id", row[0]);
            option.put("name", row[1]);
            option.put("lgdCode", row[2]);
            options.add(option);
        }
        return options;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> districtForEdit(Long districtId) {
        District district = districtRepositry.findById(districtId)
                .orElseThrow(() -> new AddDistrictException("The selected district does not exist."));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", district.getId());
        body.put("districtName", district.getName());
        body.put("lgdCode", district.getLgdCode());
        body.put("stateId", district.getState() == null ? null : district.getState().getId());
        body.put("userName", district.getMuser() == null ? null : district.getMuser().getUserName());
        return body;
    }

    /**
     * Creates the district user and the district row in one transaction.
     * District and user ids are left to the existing generators
     * ({@code district} identity and {@code muser_seq}).
     */
    @Transactional(rollbackFor = Exception.class)
    public District createDistrict(Long stateId, String rawDistrictName, Integer lgdCode) {
        String districtName = rawDistrictName == null ? "" : rawDistrictName.trim().replaceAll("\\s+", " ");
        validate(stateId, districtName, lgdCode);

        State state = stateService.findActiveStateById(stateId)
                .orElseThrow(() -> new AddDistrictException("Selected state does not exist or is not active."));

        lgdConflict(lgdCode).ifPresent(message -> {
            throw new AddDistrictException(message);
        });

        String userName = UserUtility.generateDistrictUserName(districtName, lgdCode);
        if (userName == null || userName.isBlank()) {
            throw new AddDistrictException("A login username could not be generated for this district.");
        }
        if (userRepositry.existsByUserName(userName)) {
            throw new AddDistrictException("Username " + userName + " already exists. Use a different district name or LGD Code.");
        }

        String plainPassword = UserUtility.generateDistrictPassword(userName);
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new AddDistrictException("A default password could not be generated for this district.");
        }

        User user = new User();
        user.setUserName(userName);
        user.setPassword(SecurityUtils.hashWithSHA256(plainPassword));
        user.setNewPassword(plainPassword);
        user.setRoleId(Role.USER_ROLE_DISTRICT);
        // is_active = true (account enabled). The separate active column stays
        // false; that flag tracks a live session, not whether the account exists.
        user.setActive(true);
        user.setBlock(false);
        user.setBlockTime(null);
        user.setLastPasswordChangeDate(DEFAULT_LAST_PASSWORD_CHANGE);

        User savedUser = userRepositry.save(user);
        savedUser.setNewPassword(plainPassword);
        entityManager.flush();

        District district = new District();
        district.setBoundryType(District.ADMINSTRATATIVE_BOUNDRY_TYPE_DISTRICT);
        district.setLgdCode(lgdCode);
        district.setName(districtName);
        district.setMuser(savedUser);
        district.setState(state);

        District savedDistrict = districtRepositry.save(district);
        entityManager.flush();
        logger.info("Created district {} (LGD {}) with username {}", districtName, lgdCode, userName);
        return savedDistrict;
    }

    /**
     * Updates the district name, LGD code, and state. The existing district
     * login and password are left unchanged.
     */
    @Transactional(rollbackFor = Exception.class)
    public District updateDistrict(Long districtId, Long stateId, String rawDistrictName, Integer lgdCode) {
        if (districtId == null) {
            throw new AddDistrictException("Please select a district to edit.");
        }
        String districtName = rawDistrictName == null ? "" : rawDistrictName.trim().replaceAll("\\s+", " ");
        validate(stateId, districtName, lgdCode);

        District district = districtRepositry.findById(districtId)
                .orElseThrow(() -> new AddDistrictException("The selected district does not exist."));
        State state = stateService.findActiveStateById(stateId)
                .orElseThrow(() -> new AddDistrictException("Selected state does not exist or is not active."));
        lgdConflict(lgdCode, districtId).ifPresent(message -> {
            throw new AddDistrictException(message);
        });

        district.setName(districtName);
        district.setLgdCode(lgdCode);
        district.setState(state);
        District savedDistrict = districtRepositry.save(district);
        if (savedDistrict.getState() != null) {
            savedDistrict.getState().getId();
        }
        if (savedDistrict.getMuser() != null) {
            savedDistrict.getMuser().getUserName();
        }
        entityManager.flush();
        logger.info("Updated district {} (LGD {})", districtName, lgdCode);
        return savedDistrict;
    }

    private void validate(Long stateId, String districtName, Integer lgdCode) {
        if (stateId == null) {
            throw new AddDistrictException("Please select a State.");
        }
        if (districtName.isEmpty()) {
            throw new AddDistrictException("District name is required.");
        }
        if (!DISTRICT_NAME.matcher(districtName).matches()) {
            throw new AddDistrictException(
                    "Enter a district name of 3 to 100 characters. Use letters and spaces only. Numbers and special characters are not allowed.");
        }
        if (lgdCode == null || lgdCode <= 0) {
            throw new AddDistrictException("Enter a valid numeric LGD Code.");
        }
    }
}
