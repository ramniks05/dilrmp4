package in.gov.dilrmp.repositories.igr;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.gov.dilrmp.models.igr.Igr;

import java.util.List;


public interface IgrRepositry extends CrudRepository<Igr, Long> {
    @Query("SELECT i FROM Igr i WHERE i.user.id = :userId")
    public Igr findIgrByUserId(@Param("userId") Long userId);

    @Query("SELECT i FROM Igr i JOIN FETCH i.user u JOIN FETCH i.state s WHERE u.roleId = :role AND s.id = :stateId ORDER BY u.userName ASC")
    List<Igr> findIgrUsersByStateAndRole(@Param("role") String role, @Param("stateId") Long stateId);

    @Query("SELECT i FROM Igr i JOIN FETCH i.user u JOIN FETCH i.state s WHERE u.roleId = :role ORDER BY s.name ASC, u.userName ASC")
    List<Igr> findIgrUsersByRole(@Param("role") String role);
}
