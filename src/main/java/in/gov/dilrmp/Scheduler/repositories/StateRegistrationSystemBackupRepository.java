package in.gov.dilrmp.Scheduler.repositories;

import in.gov.dilrmp.Scheduler.models.StateRegistrationSystemBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StateRegistrationSystemBackupRepository extends JpaRepository<StateRegistrationSystemBackup,Long> {

    // Column order (state_id, date, legacy year SRO, legacy year public, SROs integrated) matches
    // IgrMISDataEntryRepository.findSroAsOnDateFieldsCurrent(); read positionally.
    @Query(value = "SELECT state_id, backup_date, legacy_records_available_from_yearsro, " +
            "legacy_records_available_from_year_public, number_ofsros_integrated_with_land_records " +
            "FROM state_registration_system_backup ORDER BY state_id, backup_date, id", nativeQuery = true)
    List<Object[]> findSroAsOnDateFieldHistory();
}
