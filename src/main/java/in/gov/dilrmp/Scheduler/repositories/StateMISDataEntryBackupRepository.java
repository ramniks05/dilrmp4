package in.gov.dilrmp.Scheduler.repositories;


import in.gov.dilrmp.Scheduler.models.StateMISDataEntryBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StateMISDataEntryBackupRepository extends JpaRepository<StateMISDataEntryBackup,Long> {

    // Column order (state_id, date, 9 CLR flags) is read positionally by StateCLRAsOnDateService.
    @Query(value = "SELECT state_id, backup_date, ror_available_online, digitally_signed_ror_available, " +
            "digitally_signed_ror_legally_valid, online_mutation_facility, auto_trigger_mutation, " +
            "bank_red_flag_mortgage_in_land_records, land_records_online_from_registration_system, " +
            "revenue_court_proceedings_paperless, case_filing_red_flagged_in_land_records_from_civil_courts " +
            "FROM statemisdata_entry_backup ORDER BY state_id, backup_date, id", nativeQuery = true)
    List<Object[]> findClrFlagHistory();
}
