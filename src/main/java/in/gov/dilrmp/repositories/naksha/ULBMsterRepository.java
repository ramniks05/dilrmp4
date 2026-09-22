package in.gov.dilrmp.repositories.naksha;
import in.gov.dilrmp.models.naksha.ULBMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ULBMsterRepository extends JpaRepository<ULBMaster, Long> {

    @Query("SELECT n FROM ULBMaster n WHERE n.muser_id = :userId")
    public List<ULBMaster> findBymuser_id(@Param("userId") Long userId);

    @Query("SELECT u FROM ULBMaster u WHERE u.district_id = :districtId")
    List<ULBMaster> findULBsByDistrictId(Long districtId);

    @Query("SELECT n FROM ULBMaster n WHERE n.muser_id = :userId")
    public ULBMaster findBymid(@Param("userId") Long userId);



    // Query to get all states (state_id and state_name) for a given zoneID
    @Query("SELECT DISTINCT n.state_id, n.state_name FROM ULBMaster n WHERE n.gdID = :gdID ORDER BY n.state_name ASC")
    List<Object[]> findStatesByGdID(@Param("gdID") Integer gdID);

    // Query to get all districts (district_id and district_name) for a given zoneID
    @Query("SELECT DISTINCT n.district_id, n.district_name FROM ULBMaster n WHERE n.state_id = :stateId ORDER BY n.district_name ASC" )
    List<Object[]> getDistrictsByStateId(@Param("stateId") Long stateId);

    @Query("SELECT DISTINCT n.id, n.ulb_name FROM ULBMaster n WHERE n.district_id = :districtId ORDER BY n.ulb_name ASC")
    List<Object[]> getUlbNameByDistrictId(@Param("districtId") Long districtId);

    @Query("SELECT DISTINCT  n.gdName FROM ULBMaster n WHERE n.gdID = :gdId")
    String getGdNameByZoneId(@Param("gdId") Integer gdId);

    @Query("SELECT DISTINCT  n.zoneName FROM ULBMaster n WHERE n.gdID = :gdId")
    String getzoneNameZoneId(@Param("gdId") Integer gdId);

    @Query("SELECT n FROM ULBMaster n WHERE n.id = :ulbMasterId ")
    public Optional<ULBMaster> getUlbMasterByUlbMasterId(@Param("ulbMasterId") Long ulbMasterId);

    @Query("SELECT u FROM ULBMaster u WHERE u.state_id = :stateId")
    List<ULBMaster> findByStateId(Long stateId);

    @Query("SELECT DISTINCT u.state_id, u.state_name FROM ULBMaster u")
    List<Object[]> findStateIdAndStateName();

    @Query("SELECT COUNT(u.id), COUNT(DISTINCT u.gdID) FROM ULBMaster u")
    List<Object[]> getULBSummary();

    @Query("SELECT u.gdID, u.gdName, COUNT(u.id) FROM ULBMaster u GROUP BY u.gdID, u.gdName ORDER BY COUNT(u.id) DESC")
    List<Object[]> getULCountByGD();

    @Query(value = "SELECT gd_name, state_name, STRING_AGG(DISTINCT ulb_name, ', ' ORDER BY ulb_name) AS ulb_names " +
            "FROM ulbmaster WHERE gd_id = :gdId GROUP BY gd_name, state_name", nativeQuery = true)
    List<Object[]> getGroupedULBsByGdId(@Param("gdId") Long gdId);


    @Query("SELECT u.ulb_name FROM ULBMaster u")
    List<String[]> getUlbName();

    @Query("SELECT DISTINCT u.gdName FROM ULBMaster u ORDER BY u.gdName")
    List<String[]> getGdName();











}
