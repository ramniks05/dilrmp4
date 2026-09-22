package in.gov.dilrmp.repositories.naksha;

import in.gov.dilrmp.models.naksha.CapacityBuilding;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface CapacityBuildingRepository extends JpaRepository<CapacityBuilding, Long> {
    @Query("SELECT c FROM CapacityBuilding c WHERE c.muser.id = :muserId")
    public CapacityBuilding findByUlbMasterId(@Param("muserId") Long muserId);

    @Query("SELECT c.updateOnDate FROM CapacityBuilding c WHERE c.muser.id = :muserId")
    LocalDate findUpdateOnDateByUserId(@Param("muserId") Long muserId);

    @Modifying
    @Transactional
    @Query("UPDATE CapacityBuilding c SET c.membersToBeTrained = :membersToBeTrained, c.updateOnDate = :lastUpdateOnDate WHERE c.muser.id = :muserId")
    int updateCapacityBuildingFields(@Param("muserId") Long muserId,
                                     @Param("membersToBeTrained") int membersToBeTrained,
                                     @Param("lastUpdateOnDate") LocalDate lastUpdateOnDate);


}
