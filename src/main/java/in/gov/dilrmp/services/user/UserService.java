package in.gov.dilrmp.services.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import in.gov.dilrmp.models.igr.Igr;
import in.gov.dilrmp.models.user.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import groovy.util.logging.Slf4j;
import in.gov.dilrmp.models.administrativeBoundry.AdministrativeBoundry;
import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.repositories.user.UserRepositry;

import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService implements UserDetails {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepositry userRepository;

    private User user;
    private AdministrativeBoundry boundry;
    private Igr igr;

    public UserService() {
    }

    public UserService(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRoleId());
        return Arrays.asList(authority);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getRole() {
        return user.getRoleId();
    }

    public Long getUserID() {
        return user.getId();
    }

    public AdministrativeBoundry getBoundry() {
        return boundry;
    }

    public void setBoundry(AdministrativeBoundry boundry) {
        this.boundry = boundry;
    }

    public void addUser(User user) {
        try {
            logger.info("Adding user: {}", user.getUserName());
            userRepository.save(user);
            logger.info("User {} added successfully", user.getUserName());
        } catch (Exception e) {
            logger.error("Error adding user: {}", user.getUserName(), e);
        }
    }

    public Igr getIgr() {
        return igr;
    }

    public void setIgr(Igr igr) {
        this.igr = igr;
    }

    public void saveUserforBlock(User user) {
        try {
            logger.info("Saving user for block: {}", user.getUserName());
            userRepository.save(user);
            logger.info("User {} saved successfully for block", user.getUserName());
        } catch (Exception e) {
            logger.error("Error saving user for block: {}", user.getUserName(), e);
        }
    }

    public User getUser(Long id) {
        try {
            logger.info("Retrieving user with ID: {}", id);
            User user = userRepository.getUserById(id);
            if (user != null) {
                logger.info("User with ID {} retrieved successfully", id);
            } else {
                logger.warn("User with ID {} not found", id);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error retrieving user with ID: {}", id, e);
            return null;
        }
    }

    public void deleteByUserId(User userObject) {
        try {
            logger.info("Deleting user: {}", userObject.getUserName());
            userRepository.delete(userObject);
            logger.info("User {} deleted successfully", userObject.getUserName());
        } catch (Exception e) {
            logger.error("Error deleting user: {}", userObject.getUserName(), e);
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Optional<User> findUserName(String userName) {
        try {
            logger.info("Finding user by username: {}", userName);
            Optional<User> user = userRepository.findUserID(userName);
            if (user.isPresent()) {
                logger.info("User {} found", userName);
            } else {
                logger.warn("User {} not found", userName);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error finding user by username: {}", userName, e);
            return Optional.empty();
        }
    }

    public List<Object> getUsersByMuserId(Long stateId) {
        try {
            logger.info("Retrieving users by Muser ID: {}", stateId);
            List<Object> users = userRepository.findByMuserId(stateId);
            logger.info("Retrieved {} users by Muser ID: {}", users.size(), stateId);
            return users;
        } catch (Exception e) {
            logger.error("Error retrieving users by Muser ID: {}", stateId, e);
            return List.of();
        }
    }

    public boolean doesUserNameExist(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Transactional
    public boolean resetPassword(String userName, String hashedPassword) {

        // Store the hashed password (SHA-256) in the database
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentDate = LocalDateTime.parse("2024-04-17 00:00:00", formatter);

        // Call the repository method to update the password and lastPasswordChangeDate
        int updatedRows = userRepository.resetPassword(hashedPassword, userName, currentDate);

        // Return true if at least one row was updated, indicating success
        return updatedRows > 0;
    }
}