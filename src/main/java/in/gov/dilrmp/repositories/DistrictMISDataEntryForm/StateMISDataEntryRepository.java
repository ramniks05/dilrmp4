package in.gov.dilrmp.repositories.DistrictMISDataEntryForm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.gov.dilrmp.models.dataEntryModel.StateMISDataEntry;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateMISDataEntryRepository extends JpaRepository<StateMISDataEntry,Long> {


    StateMISDataEntry findByState_Id(Long stateID);

    Optional<StateMISDataEntry> findByStateIdAndUserId(Long id, Long id1);

    //v5 CLR — Auto-Mutation Facility Yes/No by state
    @Query("SELECT e.state.id, e.autoMutationFacility FROM StateMISDataEntry e")
    List<Object[]> findAutoMutationFacilityByStateId();

    @Query("SELECT COUNT(e) FROM StateMISDataEntry e WHERE e.autoMutationFacility = true")
    Long countAutoMutationFacilityYesNational();

    // Same column order as StateMISDataEntryBackupRepository.findClrFlagHistory().
    @Query(value = "SELECT state_id, last_update_on_date, ror_available_online, digitally_signed_ror_available, " +
            "digitally_signed_ror_legally_valid, online_mutation_facility, auto_trigger_mutation, " +
            "bank_red_flag_mortgage_in_land_records, land_records_online_from_registration_system, " +
            "revenue_court_proceedings_paperless, case_filing_red_flagged_in_land_records_from_civil_courts " +
            "FROM state_mis_data_entry", nativeQuery = true)
    List<Object[]> findClrFlagsCurrent();
}
