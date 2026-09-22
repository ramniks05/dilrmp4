package in.gov.dilrmp.repositories.naksha;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntry;
import in.gov.dilrmp.models.naksha.StateNakshaSLCMeetingDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface SLCMeetingRepository extends JpaRepository<StateNakshaSLCMeetingDate, Long> {


    boolean existsByNakshaMISDataEntryAndMeetingDate(NakshaMISDataEntry entry, LocalDate meetingDate);

    @Modifying
    @Transactional
    @Query("DELETE FROM StateNakshaSLCMeetingDate s WHERE s.nakshaMISDataEntry = :entry")
    void deleteAllByEntry(@Param("entry") NakshaMISDataEntry entry);


    List<StateNakshaSLCMeetingDate> findByNakshaMISDataEntry(NakshaMISDataEntry entry);
}
