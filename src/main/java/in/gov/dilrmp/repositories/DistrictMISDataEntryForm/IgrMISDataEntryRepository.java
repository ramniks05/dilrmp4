package in.gov.dilrmp.repositories.DistrictMISDataEntryForm;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gov.dilrmp.models.dataEntryModel.StateRegistrationSystem;

import java.util.Optional;

@Repository
public interface IgrMISDataEntryRepository extends JpaRepository<StateRegistrationSystem,Long> {

    StateRegistrationSystem findByState_Id(Long stateID);

    Optional<StateRegistrationSystem> findByStateId(Long stateId);
}
