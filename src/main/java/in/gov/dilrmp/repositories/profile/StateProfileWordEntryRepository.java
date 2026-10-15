package in.gov.dilrmp.repositories.profile;

import in.gov.dilrmp.models.profile.StateProfileWordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StateProfileWordEntryRepository extends JpaRepository<StateProfileWordEntry, Long> {

    Optional<StateProfileWordEntry> findByStateIdAndAsOnDate(Long stateId, LocalDate asOnDate);

    Optional<StateProfileWordEntry> findFirstByStateIdOrderByAsOnDateDescUpdatedOnDesc(Long stateId);
}
