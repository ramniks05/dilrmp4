package in.gov.dilrmp.services.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.dilrmp.models.user.UserProfile;
import in.gov.dilrmp.repositories.user.UserProfileRepositry;

@Service
public class UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    @Autowired
    private UserProfileRepositry userProfileRepositry;

    public void addUserProfile(UserProfile userProfile) {
        try {
            logger.info("Adding user profile for user ID: {}", userProfile.getMuser().getId());
            userProfileRepositry.save(userProfile);
            logger.info("User profile for user ID {} added successfully", userProfile.getMuser().getId());
        } catch (Exception e) {
            logger.error("Error adding user profile for user ID: {}", userProfile.getMuser().getId(), e);
        }
    }

    public UserProfile getUserProfile(Long id) {
        try {
            logger.info("Retrieving user profile with ID: {}", id);
            UserProfile userProfile = userProfileRepositry.findIgrByUserId(id);
            if (userProfile != null) {
                logger.info("User profile with ID {} retrieved successfully", id);
            } else {
                logger.warn("User profile with ID {} not found", id);
            }
            return userProfile;
        } catch (Exception e) {
            logger.error("Error retrieving user profile with ID: {}", id, e);
            return null;
        }
    }
}
