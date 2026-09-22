package in.gov.dilrmp.repositories.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.gov.dilrmp.models.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepositry extends CrudRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.userName = :username")
    public User getUserByUsername(@Param("username") String userName);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    public User getUserById(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.userName = :userName")
    public Optional<User> findUserID(@Param("userName") String userName );

    @Query("SELECT u FROM User u WHERE u.userName = :userName AND u.active = :active")
    Optional<User> findByUserNameAndLoginStatus(@Param("userName") String userName, @Param("active") Boolean active);


    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.active = :active WHERE u.userName = :userName")
    int updateUserActive(@Param("userName") String userName, @Param("active") boolean active);


    @Query(value = "SELECT * FROM userlist_v WHERE muser_id = :muserId", nativeQuery = true)
    List<Object> findByMuserId(@Param("muserId") Long muserId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.active = :active")
    int updateUsersActive( @Param("active") boolean active);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.userName = :userName")
    boolean existsByUserName(@Param("userName") String userName);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password, u.lastPasswordChangeDate = :lastPasswordChangeDate, u.block = false,u.blockTime=null WHERE u.userName = :userName")
    int resetPassword(@Param("password") String password, @Param("userName") String userName, @Param("lastPasswordChangeDate") LocalDateTime lastPasswordChangeDate);

}
