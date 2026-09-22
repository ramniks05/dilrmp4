package in.gov.dilrmp.repositories.naksha;
import in.gov.dilrmp.models.naksha.IECActivities;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface IecActivityRepository extends JpaRepository<IECActivities, Long> {

    // Fetch IEC activity by ULB user ID
    @Query("SELECT i FROM IECActivities i WHERE i.muser.id = :muserId")
    IECActivities findByUlbMasterId(@Param("muserId") Long muserId);

    // Get last update date for a given user
    @Query("SELECT i.updateOnDate FROM IECActivities i WHERE i.muser.id = :muserId")
    LocalDate findUpdateOnDateByUserId(@Param("muserId") Long muserId);

    // Custom update for partial IEC data (you can add more fields as needed)
//    @Modifying
//    @Transactional
//    @Query("UPDATE IECActivities i SET " +
//            "i.totalProgramsConducted = :programs, " +
//            "i.noOfParticipants = :participants, " +
//            "i.updateOnDate = :lastUpdateOnDate " +
//            "WHERE i.muser.id = :muserId")
//    int updateIecActivityFields(@Param("muserId") Long muserId,
//                                @Param("programs") Integer totalProgramsConducted,
//                                @Param("participants") Integer noOfParticipants,
//                                @Param("lastUpdateOnDate") LocalDate lastUpdateOnDate);
}
