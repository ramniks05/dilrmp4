package in.gov.dilrmp.repositories.DistrictMISDataEntryForm;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.gov.dilrmp.models.dataEntryModel.StateRegistrationSystem;

import java.util.List;
import java.util.Optional;

@Repository
public interface IgrMISDataEntryRepository extends JpaRepository<StateRegistrationSystem,Long> {

    StateRegistrationSystem findByState_Id(Long stateID);

    Optional<StateRegistrationSystem> findByStateId(Long stateId);

    //v5 SRO Modernization aggregates
    // row: [0]=state_id, [1]=onlineA, [2]=B stateFunds, [3]=C dilrmpSanctioned,
    // [4]=D dilrmpFunds, [5]=totalSros
    @Query(value = "SELECT e.state_id, "
            + "COALESCE(SUM(e.number_of_sros_using_online_registration), 0), "
            + "COALESCE(SUM(e.sros_modernised_state_funds), 0), "
            + "COALESCE(SUM(e.sros_dilrmp_sanctioned), 0), "
            + "COALESCE(SUM(e.sros_modernised_dilrmp_funds), 0), "
            + "COALESCE(SUM(e.number_of_sros_in_state), 0) "
            + "FROM state_registration_system e "
            + "GROUP BY e.state_id", nativeQuery = true)
    List<Object[]> sumSroModernizationByStateId();

    @Query(value = "SELECT "
            + "COALESCE(SUM(e.number_of_sros_using_online_registration), 0), "
            + "COALESCE(SUM(e.sros_modernised_state_funds), 0), "
            + "COALESCE(SUM(e.sros_dilrmp_sanctioned), 0), "
            + "COALESCE(SUM(e.sros_modernised_dilrmp_funds), 0), "
            + "COALESCE(SUM(e.number_of_sros_in_state), 0) "
            + "FROM state_registration_system e", nativeQuery = true)
    List<Object[]> sumSroModernizationNational();
}
