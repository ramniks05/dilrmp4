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
}
