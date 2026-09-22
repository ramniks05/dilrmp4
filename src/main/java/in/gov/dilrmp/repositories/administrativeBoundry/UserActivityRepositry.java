package in.gov.dilrmp.repositories.administrativeBoundry;

import in.gov.dilrmp.models.administrativeBoundry.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserActivityRepositry<userLoginHistoryID> extends JpaRepository<UserActivity, Long> {
    //@Query("SELECT u FROM UserActivity u WHERE u.userLoginHistory = :userLoginHistoryID")
    @Query("SELECT ut FROM UserActivity  ut  WHERE ut.userLoginHistory.id in(SELECT u.id from UserLoginHistory u where u.user.id =:userId)")
    List<UserActivity> findAllLoginHistoryByID(@Param("userId") Long userId);

}
