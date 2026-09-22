package in.gov.dilrmp.repositories.administrativeBoundry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.gov.dilrmp.models.administrativeBoundry.State;

public interface StateRepositry extends CrudRepository<State, Long> {

    Page<State> findAll(Pageable pageable);

    List<State> findAll();

    @Query("SELECT s FROM State s WHERE s.lgdCode = :lgdCode")
    public Optional<State> findStateByLgdCode(@Param("lgdCode") Integer lgdCode);

    @Query("SELECT s FROM State s WHERE s.lgdCode = :lgdCode")
    public List<State> findStateListByLgdCode(@Param("lgdCode") Integer lgdCode);
    
//    @Query("SELECT s FROM State s WHERE s.id = :stateID")
//    public List<State> findStateListByID(@Param("stateID") Long stateID);

    @Query("SELECT s FROM State s WHERE s.id = :stateID ORDER BY s.name ASC")
    List<State> findStateListByID(@Param("stateID") Long stateID);
    
    @Query("SELECT s FROM State s WHERE s.muser.id = :userId")
    public State findStateByUserID(@Param("userId") Long userId);

    @Query("SELECT s FROM State s WHERE s.name = :name")
    public State findByStateName(@Param("name") String StateName);

    @Query("SELECT s FROM State s order by s.name")
    List<State> statelist();

    @Query("SELECT s FROM State s WHERE s.muser.roleId=:ROLE_STATE")
    List<State> findAllStateUserBYRoleID(@Param("ROLE_STATE") String ROLE_STATE);

    @Query("SELECT s FROM State s order by s.name")
    List<State> findAllOrderByStateName();
    @Query("SELECT d.name FROM State d WHERE d.id = :id")
    String findStateNameById(@Param("id") Long id);

    @Query("SELECT s FROM State s WHERE s.id = :userId")
    public State findStateByAdcUserID(@Param("userId") Long userId);



}
