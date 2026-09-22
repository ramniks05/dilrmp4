package in.gov.dilrmp.repositories.administrativeBoundry;

import in.gov.dilrmp.models.administrativeBoundry.UserLoginHistory;
import in.gov.dilrmp.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Long> {
    UserLoginHistory findTopByUserOrderByLoginTimeDesc(User user);

    List<UserLoginHistory> findByOrderByLoginTimeAsc();





}
