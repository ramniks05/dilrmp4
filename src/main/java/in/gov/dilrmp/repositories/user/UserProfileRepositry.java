package in.gov.dilrmp.repositories.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import in.gov.dilrmp.models.user.UserProfile;
import org.springframework.data.repository.query.Param;

public interface UserProfileRepositry extends CrudRepository<UserProfile,Long> {

    @Query("SELECT u FROM UserProfile u WHERE u.muser.id = :userId")
    public UserProfile findIgrByUserId(@Param("userId") Long userId);
    
    
}
